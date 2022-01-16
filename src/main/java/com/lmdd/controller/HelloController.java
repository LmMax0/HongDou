package com.lmdd.controller;


import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin(origins = "http://101.35.55.105:8080/hongdou/**")
public class HelloController {

    @GetMapping("/test/t1")
    public ModelAndView test1(){
        return new ModelAndView("/test");
    }
    @GetMapping("/test/t2")
    @ResponseBody
    public Map test2(){
        Map result = new HashMap();
        result.put("test", "测试文本");
        return result;
    }

    @GetMapping("/")
    public String index() {
        return "index.jsp";
    }

    @RequestMapping("/media")
    @ResponseBody
    public void getVideo(@RequestParam("video") String videoName, HttpServletResponse res, HttpServletRequest req) throws IOException {
        String fileRoot = "/usr/local/Eapplication/hongdou-media/";

//        文件真实名字+后缀
        String fileName = videoName;
        fileName = URLEncoder.encode(fileName,"UTF-8");
//        设置头文件
        res.setHeader("Content-Disposition", "attachment;filename="+fileName);

        //创建输出流 二进制
        ServletOutputStream os = res.getOutputStream();

//        获取文件真实路径
        String realFileName = fileRoot +  fileName;
        System.out.println(realFileName);

        File downFile = new File(realFileName);
        InputStream fis = new BufferedInputStream(new FileInputStream(downFile));
        OutputStream toClient = new BufferedOutputStream(res.getOutputStream());

        // byte[] fileBuffer = new byte[1024];//每次读取1024字节大小的数据
        try{
            byte[] buffer = new byte[1024 * 1024 * 4];
            int i = -1;
            while ((i = fis.read(buffer)) != -1) {
                toClient.write(buffer, 0, i);
            }
            fis.close();
            toClient.flush();
            toClient.close();
        }
        catch (Exception  e) {
            e.printStackTrace();
        }
////      第三方下载
//        byte[] bytes = FileUtils.readFileToByteArray(downFile);
//        os.write(bytes);
//        os.flush();
//        os.close();

    }

    @RequestMapping("/multimedia")
    @ResponseBody
    public void getMultiVideo(@RequestParam("video") String videoName,
                              HttpServletResponse res,
                              HttpServletRequest req) throws IOException {
        if (req.getHeader("Range") != null){
            System.out.println("浏览器支持range");
            String fileRoot = "/usr/local/Eapplication/hongdou-media/";
            //        文件真实名字+后缀
            String fileName = videoName;
            fileName = URLEncoder.encode(fileName,"UTF-8");
//        设置头文件
            res.setHeader("Content-Disposition", "attachment;filename="+fileName);


//        获取文件真实路径
            String realFileName = fileRoot +  fileName;
            System.out.println(realFileName);

            File downFile = new File(realFileName);
            Long fileSize = downFile.length();
            InputStream in = new BufferedInputStream(new FileInputStream(downFile));
            OutputStream toClient = new BufferedOutputStream(res.getOutputStream());
            //解析Range
            Map<String, Integer> range = this.analyzeRange(req.getHeader("Range"), fileSize.intValue());

            String contentType = new MimetypesFileTypeMap().getContentType(downFile);
            //设置响应头
            res.setContentType(contentType);
            res.setHeader("Content-Length", String.valueOf(fileSize.intValue()));
            res.setHeader("Content-Range", "bytes " + range.get("startByte") + "-" + range.get("endByte") + "/" + fileSize.intValue());
            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Accept-Ranges", "bytes");
            res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

            //开始输出
            OutputStream os = res.getOutputStream();
            int length = range.get("endByte") - range.get("startByte");
            System.out.println("length：" + length);
            byte[] buffer = new byte[length < 1024 ? length : 1024];
            in.skip(range.get("startByte"));
            int i = in.read(buffer);
            length = length - buffer.length;
            while (i != -1) {
                os.write(buffer, 0, i);
                if (length <= 0) {
                    break;
                }
                i = in.read(buffer);
                length = length - buffer.length;
            }
            os.flush();
            //关闭
            os.close();
            in.close();
            return;



        }else{
            System.out.println("不支持range");
        }

    }

    /**
     * 解析range，解析出起始byte（startByte）和结束byte（endBytes）
     *
     * @param range    请求发来的range
     * @param fileSize 目标文件的大小
     * @return
     */
    private Map<String, Integer> analyzeRange(String range, Integer fileSize) {
//        解析中
        System.out.println("rang:"+range+"filesize:"+fileSize);

        String[] split = range.split("-");
        Map<String, Integer> result = new HashMap<String, Integer>();
        if (split.length == 1) {
            //从xxx长度读取到结尾
            Integer startBytes = new Integer(range.replaceAll("bytes=", "").replaceAll("-", ""));
            result.put("startByte", startBytes);
            result.put("endByte", fileSize - 1);
        } else if (split.length == 2) {
            //从xxx长度读取到yyy长度
            Integer startBytes = new Integer(split[0].replaceAll("bytes=", "").replaceAll("-", ""));
            Integer endBytes = new Integer(split[1].replaceAll("bytes=", "").replaceAll("-", ""));
            result.put("startByte", startBytes);
            result.put("endByte", endBytes > fileSize ? fileSize : endBytes);
        } else {
            System.out.println("未识别的range："+ range);
        }
        return result;
    }

}
