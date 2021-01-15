package com.lab5.resteventhub;

import com.lab5.resteventhub.service.SendDataConsoleImpl;
import com.lab5.resteventhub.service.SendDataEventHubImpl;
import com.lab5.resteventhub.service.SendDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class LogService {

    @Autowired
    private SendDataEventHubImpl sendDataEventHub;

    @Autowired
    private SendDataConsoleImpl sendDataConsole;

    private Map<String, SendDataService> logServiceMap;

    @PostConstruct
    public void initMap() {
        logServiceMap = createStrategies();
    }

    public SendDataService getService(String repositoryName) {
        return logServiceMap.get(repositoryName);
    }

    private Map<String, SendDataService> createStrategies() {
        Map<String, SendDataService> strategies = new HashMap<>();
        strategies.put("eventHub", sendDataEventHub);
        strategies.put("redis", sendDataConsole);
        return strategies;
    }
}