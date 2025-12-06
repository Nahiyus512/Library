package com.wkc.library.oss;

import com.wkc.library.entity.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author xiaoniao
 * @desc 文件上传下载（使用阿里云 OSS）
 */
@RestController
@RequestMapping("/common")
@CrossOrigin
@RequiredArgsConstructor
public class CommonController {

    private final AliOssUtil aliOssUtil;

    /**
     * 上传文件到阿里云 OSS
     * @param file 前端上传的文件
     * @return 文件在 OSS 上的访问 URL（或 objectName，看你需要）
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 使用 UUID 生成 OSS 对象名，可以加上业务前缀，比如 "book/"
            String objectName = "book/" + UUID.randomUUID().toString() + suffix;

            // 调用你封装好的 AliOssUtil 上传，返回访问 URL
            String url = aliOssUtil.upload(file.getBytes(), objectName);


            //只返回 objectName，前端/数据库存 objectName，需要访问时自己拼 URL。
            return R.success(objectName);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败");
        }
    }

    /**
     *
     * 做个重定向到 OSS
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        // name 可以是数据库中的 objectName，比如 "book/xxx.png"
        // 下面这段是用 AliOssUtil 自己的拼接规则生成 URL：
        String url = buildOssUrl(name);

        // 直接重定向到 OSS 地址
        response.sendRedirect(url);
    }

    /**
     * 用 AliOssUtil 的拼接规则构建 URL（也可以把这段封装进 AliOssUtil 里）
     */
    private String buildOssUrl(String objectName) {
        // AliOssUtil.upload 里的拼接方式是：
        // https:// + bucketName + "." + endpoint + "/" + objectName
        String bucketName = aliOssUtil.getBucketName();
        String endpoint = aliOssUtil.getEndpoint();  // 注意：这里 endpoint 不能带 https://

        if (endpoint.startsWith("http://")) {
            endpoint = endpoint.substring("http://".length());
        } else if (endpoint.startsWith("https://")) {
            endpoint = endpoint.substring("https://".length());
        }

        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }
}
