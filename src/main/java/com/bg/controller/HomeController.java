package com.bg.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bg.model.client.RequestFile;
import com.bg.model.client.ResponseFile;
import com.bg.service.client.FileTransferClient;
import com.bg.util.JedisAdapter;
import com.bg.util.MD5FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/29.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    JedisAdapter jedisAdapter;

    private final Executor exec = Executors.newCachedThreadPool();

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index() {
        return "home";
    }

    @RequestMapping(path = {"/transferFile/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String transferFile(@RequestParam("qqfile") List<MultipartFile> fileTmp) {
        try {
            int port = 10012;
        /*if (args != null && args.length > 0) {
            try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}*/
            List<String> md5 = new ArrayList<>();
            for (MultipartFile fTmp : fileTmp) {
                RequestFile echo = new RequestFile();
                //System.out.println(fileTmp.getName());
                System.out.println(fTmp.getOriginalFilename());
                File file = new File("D:/deaProjects/netty-web-filestransfer/tmp/" + fTmp.getOriginalFilename());  //  "D://files/xxoo"+args[0]+".amr"
                if(!file.exists())
                    fTmp.transferTo(file);



                String fileName = file.getName();// 文件名
                echo.setFile(file);
                String m = MD5FileUtil.getFileMD5String(file);
                echo.setFile_md5(m);
                md5.add(m);

                echo.setFile_name(fileName);

                echo.setFile_type(FileTransferClient.getSuffix(fileName));

                echo.setStarPos(0);// 文件开始位置

               //System.out.println(echo);
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new FileTransferClient().connect(port, "127.0.0.1", echo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                exec.execute(task);
            }

            JSONObject json = new JSONObject();
            json.put("success", "ok");
            json.put("md5",md5);
            //System.out.println(json.toJSONString());
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

    @RequestMapping(path = {"/getProgress"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String getProgress() {
        JSONObject json = new JSONObject();
        List<String> response = jedisAdapter.brpop(2,"response");
        if (response == null || response.isEmpty()) {
            System.out.println("done");
            json.put("done", "1");
            json.put("progress", "100");
            json.put("id","0");
            return json.toJSONString();
        }
        //System.out.println(response);
        for(String res : response) {
            if (res.equals("response")) {
                continue;
            }
            ResponseFile resfile = JSON.parseObject(res, ResponseFile.class);
            json.put("done", "0");
            json.put("progress", String.valueOf((resfile.getProgress())));
            json.put("id",resfile.getFile_md5());
        }


        //System.out.println("in");



        return json.toJSONString();
}

    @RequestMapping(path = {"/test"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String test() {
        //System.out.println("1n");
        JSONObject json = new JSONObject();
        Random random = new Random();
        String str = String.valueOf(random.nextInt(1000) % 10);
        json.put("test", str);
        return json.toJSONString();
    }
}
