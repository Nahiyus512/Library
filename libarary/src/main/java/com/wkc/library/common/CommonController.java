package com.wkc.library.common;

import com.wkc.library.entity.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author xiaoniao
 * @date 2024/6/6 11:37
 * @desc 文件上传下载
 */
@RestController
@RequestMapping("/common")
@CrossOrigin
public class CommonController {

    private static final String basePath = "D:\\Download\\image\\book\\";


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成一个文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录
        File dir = new File(basePath);
        if(!dir.exists()) {
            //如果目录不存在就直接创建
            dir.mkdirs();
        }

        try {
            //将临时文件存到执行位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }



    //文件下载
    @GetMapping("/download")
    public R<String> download(String name, HttpServletResponse response) {
        try {
            //通过输入流读取文件内容
            FileInputStream fis = new FileInputStream(new File(basePath+name));
            //输出流将文件返回给浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            //response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fis.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            outputStream.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.success("下载图片成功");
    }
}
