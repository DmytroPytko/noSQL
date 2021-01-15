package com.lab5.resteventhub.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
@Slf4j
public class SendDataConsoleImpl implements SendDataService {

    @Value("${use.ssl}")
    private boolean USE_SSL;

    @Value("${max.number}")
    private int MAX_NUMBER;

    @Value("${cache.hostname}")
    private String CACHE_HOSTNAME;

    @Value("${cache.key}")
    private String CACHE_KEY;

    @Value("${map.name}")
    private String MAP_NAME;

    @Value("${file.name}")
    private String FILE_NAME;

    @Value("${dataservice.port}")
    private int PORT;

    public void sendAndLog(String url) {
        Jedis jedis = getJedis();
        try {
            URL data = new URL(url);
            HttpURLConnection con = (HttpURLConnection) data.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            JSONArray jsonArray;
            jedis.hset(MAP_NAME, "File", "None");
            Map<String, String> redisData = jedis.hgetAll(MAP_NAME);

            int count = 1;
            int startRaw = 1;
            int limit = 100;
            int endRaw = 0;
            if (checkIfFileExist(jedis, redisData)) {
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                    if (count == limit) {
                        jsonArray = new JSONArray(response.toString() + ']');
                        log.info(response.toString());
                        log.info("LENGTH: " + jsonArray.length());
                        endRaw = endRaw + count;
                        jedis.set("Raws", startRaw + ":" + endRaw);
                        startRaw = startRaw + count;
                        showData(jsonArray.length(), jsonArray, jedis, redisData);
                        count = 0;
                    }
                    count += 1;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Jedis getJedis() {
        JedisShardInfo info = new JedisShardInfo(CACHE_HOSTNAME, PORT, USE_SSL);
        info.setPassword(CACHE_KEY);
        return new Jedis(info);
    }

    public void showData(int count, JSONArray jsonArray, Jedis jedis, Map<String, String> map) {
        jedis.hset(MAP_NAME, "Raws", "" + count);
        if (jsonArray.length() != MAX_NUMBER) {
            log.info("Raws from file " + "'" + FILE_NAME + "': " + jedis.get("Raws"));
            jedis.hset(MAP_NAME, "File", FILE_NAME);

            jedis.hset(MAP_NAME, "Status", "NotFinished");
        } else {
            log.info("Raws from file " + "'" + FILE_NAME + "': " + jedis.get("Raws"));
            jedis.hset(MAP_NAME, "Raws", "" + count);
            jedis.hset(MAP_NAME, "Status", "Completed");
            jedis.hset(MAP_NAME, "Info", "First attempt to input this file");
            log.info(map.get("Status"));
            jedis.close();
        }
    }

    public boolean checkIfFileExist(Jedis jedis, Map<String, String> map) {
        map = jedis.hgetAll(MAP_NAME);
        String name = map.get("File");
        String status = map.get("Status");

        if (!name.equals(FILE_NAME)) {
            jedis.hset(MAP_NAME, "File", FILE_NAME);
        } else {
            if (status.equals("Completed")) {
                jedis.hset(MAP_NAME, "Info", "Retry to input this file");
                log.info("Such file: " + "'" + FILE_NAME + "'" + " already exists. Application stop");
                jedis.close();
                return false;
            }
        }
        return true;
    }
}