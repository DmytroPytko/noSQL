package com.lab5.resteventhub.controller;

import com.lab5.resteventhub.LogService;
import com.lab5.resteventhub.dto.RequestDTO;
import com.lab5.resteventhub.service.SendDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class URLController {

    @Autowired
    private LogService logService;

    @PostMapping("/url")
    public void addNewUrl(@RequestBody RequestDTO dto) {
        SendDataService logService = this.logService.getService(dto.getStrategy());
        try {
            logService.sendAndLog(dto.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}