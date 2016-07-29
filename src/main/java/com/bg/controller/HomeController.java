package com.bg.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2016/7/29.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String index() {
        return "home";
    }

    @RequestMapping(path={"/transferFile/"},method = {RequestMethod.POST})
    @ResponseBody
    public String transferFile(@RequestParam("file") MultipartFile file) {
        try {
            return "home";
        } catch (Exception e){
            logger.error("发送文件失败" + e.getMessage());
            return "发送文件失败";
        }
    }

}
