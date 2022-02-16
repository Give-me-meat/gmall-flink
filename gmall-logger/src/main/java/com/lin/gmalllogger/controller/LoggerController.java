package com.lin.gmalllogger.controller;

//import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoggerController {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @RequestMapping("test")
    public String test1(){
        System.out.println("success1");
        return "success.html";
    }

    @RequestMapping("nba2022")
    public String c2022(@RequestParam("name") String name,@RequestParam(value = "reward",defaultValue = "mvp") String reward){
        System.out.println("2022"+reward+"is"+name);
        return "2022";
    }

    @RequestMapping("applog")
    public String getLog(@RequestParam("param") String jsonStr){
        //System.out.println(jsonStr);

        log.info(jsonStr);

        kafkaTemplate.send("ods_base_log",jsonStr);

        return "success";
    }
}
