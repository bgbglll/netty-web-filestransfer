package com.bg.controller;

import com.alibaba.fastjson.JSONObject;
import com.bg.model.client.RequestFile;
import com.bg.service.client.FileTransferClient;
import com.bg.util.MD5FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Created by Administrator on 2016/7/29.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index() {
        return "home";
    }

    @RequestMapping(path = {"/transferFile/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String transferFile(@RequestParam("qqfile") MultipartFile fileTmp) {
        try {
            int port = 10012;
        /*if (args != null && args.length > 0) {
            try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}*/
            RequestFile echo = new RequestFile();
            //System.out.println(fileTmp.getName());
            System.out.println(fileTmp.getOriginalFilename());
            File file = new File("D:/deaProjects/netty-web-filestransfer/tmp/"+ fileTmp.getOriginalFilename());  //  "D://files/xxoo"+args[0]+".amr"
            fileTmp.transferTo(file);



            String fileName = file.getName();// 文件名
            echo.setFile(file);
            echo.setFile_md5(MD5FileUtil.getFileMD5String(file));
            echo.setFile_name(fileName);
            echo.setFile_type(FileTransferClient.getSuffix(fileName));
            echo.setStarPos(0);// 文件开始位置
            //System.out.println(echo);
            new FileTransferClient().connect(port, "127.0.0.1", echo);
            JSONObject json = new JSONObject();
            json.put("success", "ok");
            return json.toJSONString();
        } catch (Exception e) {
            logger.error("发送文件失败" + e.getMessage());
            return "发送文件失败";
        }

    }

    @RequestMapping(path = {"/upload"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String upload() {
        return "fileUpload";
    }
}
