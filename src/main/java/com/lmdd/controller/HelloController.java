package com.lmdd.controller;


import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
@CrossOrigin(origins = "http://101.35.55.105:8080")
//@CrossOrigin(origins = "*")
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

    @RequestMapping("/one")
    @ResponseBody
    public void getVideo(@RequestParam("v") String videoName, HttpServletResponse res, HttpServletRequest req) throws IOException {
//            fileRoot 表示读取视屏的根目录文件，需要结尾时 有/
//        String fileRoot = "D:/Code/HANJA/lmreader/media/";
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

    @RequestMapping("/two")
    @ResponseBody
    public void getMultiVideo(@RequestParam("v") String videoName,
                              HttpServletResponse res,
                              HttpServletRequest req) throws IOException {
        if (req.getHeader("Range") != null){
            System.out.println("浏览器支持range");

//            fileRoot 表示读取视屏的根目录文件，需要结尾时 有/
//            String fileRoot = "D:/Code/HANJA/lmreader/media/";
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
//            InputStream in = new BufferedInputStream(new FileInputStream(downFile));
//            OutputStream toClient = new BufferedOutputStream(res.getOutputStream());

            RandomAccessFile randomAccessFile = new RandomAccessFile(downFile, "r");

            //解析Range
            Map<String, Long> range = this.analyzeRange(randomAccessFile,req.getHeader("Range"), fileSize);

            String contentType = new MimetypesFileTypeMap().getContentType(downFile);
            //设置响应头
            res.setContentType(contentType);
            // 首先设置响应头 告知浏览器要接受 视频的总大小
            res.setHeader("Content-Length", String.valueOf(range.get("length")));

            res.setHeader("Content-Range", "bytes " + range.get("start") + "-" + range.get("end") + "/" + fileSize.intValue());
//            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Accept-Ranges", "bytes");
            res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

            //输出脚本长度
            long trans = 0;
            int len = 0;
            OutputStream os = res.getOutputStream();
            byte[] buffer = new byte[1024 * 1024];
            randomAccessFile.seek(range.get("start"));

            while ((trans + len) <= range.get("length") && (len = randomAccessFile.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                trans += len;
            }
            //处理不足buff.length部分
            if (trans < range.get("length")) {
                len = randomAccessFile.read(buffer, 0, (int) (range.get("length") - trans));
                os.write(buffer, 0, len);
                trans += len;
            }

            os.flush();
            res.flushBuffer();
            randomAccessFile.close();
            os.close();


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
    private Map<String, Long> analyzeRange(RandomAccessFile randomAccessFile, String range, Long fileSize) throws IOException {
//        解析中
        System.out.println("rang:"+range+"filesize:"+fileSize);

        String[] split = range.split("-");
        Map<String, Long> result = new HashMap<String, Long>();

        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = fileSize - 1;

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();

            String ranges[] = range.split("-");
            try {
                //根据range解析下载分片的位置区间
                if (ranges.length == 1) {
                    //情况1，如：bytes=-1024  从开始字节到第1024个字节的数据
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //情况2，如：bytes=1024-  第1024个字节到最后字节的数据
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //情况3，如：bytes=1024-2048  第1024个字节到2048个字节的数据
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = fileSize - 1;
            }
        }

        //要下载的长度 [0,1024] -->是1025个字节
        long contentLength = endByte - startByte + 1;

        result.put("start",startByte);
        result.put("end",endByte);
        result.put("length",contentLength);
        System.out.println("返回数据区间:【"+result.get("start")+"-"+result.get("end")+"】");
        return result;
    }

}
