package com.lab5.resteventhub.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDTO {

    @ApiModelProperty(example = "https://opendata.utah.gov/resource/s9t3-bccv.json")
    private String url;

    @ApiModelProperty(example = "eventHub")
    private String strategy;

}