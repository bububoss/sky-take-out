package com.sky.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.sky.properties.AliyunOSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AliOssUtil {

    //方式一：通过Value注解属性注入
//    @Value("${aliyun.oss.endpoint}")
//    private String endpoint;
//    @Value("${aliyun.oss.bucket-name}")
//    private String bucketName;
//    @Value("${aliyun.oss.region}")
//    private String region;

    //方式2-自定义实体类，放入容器\
    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    /**
     * 上传文件到阿里云OSS
     *
     * @param objectName  对象在Bucket中的完整路径，完整路径中不能包含Bucket名称，例如images/001.jpg
     * @param inputStream 文件的输入流
     * @return 上传成功后的文件可访问URL
     */
    public  String upload(String objectName, InputStream inputStream) {
        // 从环境变量中获取访问凭证。运行前请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();

        //获取三个参数信息
        String region = aliyunOSSProperties.getRegion();
        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();


        // 创建OSSClient实例。不再使用时调用close方法释放资源。
        OSSClient ossClient = OSSClient.newBuilder()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        try {
            // 创建PutObjectRequest对象并上传文件。
            PutObjectRequest putObjectRequest = PutObjectRequest.newBuilder()
                    .bucket(bucketName)
                    .key(objectName)
                    .body(BinaryData.fromStream(inputStream))
                    .build();

            PutObjectResult result = ossClient.putObject(putObjectRequest);
            System.out.println("上传成功，ETag: " + result.eTag());

            // 返回文件的可访问URL
            return "https://" + bucketName + "." + endpoint.substring(endpoint.lastIndexOf("/") + 1) + "/" + objectName;
        } catch (Exception e) {
            // 上传失败（网络不可达、凭证读取失败、请求被拒绝等）。
            throw new RuntimeException("文件上传到OSS失败", e);
        } finally {
            if (ossClient != null) {
                try {
                    ossClient.close();
                } catch (Exception e) {
                    // 忽略关闭客户端时的异常
                }
            }
        }
    }
}




//package com.sky.utils;
//
//import com.aliyun.oss.ClientException;
//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
//import com.aliyun.oss.OSSException;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import java.io.ByteArrayInputStream;
//
//@Data
//@AllArgsConstructor
//@Slf4j
//public class AliOssUtil {
//
//    private String endpoint;
//    private String accessKeyId;
//    private String accessKeySecret;
//    private String bucketName;
//
//    /**
//     * 文件上传
//     *
//     * @param bytes
//     * @param objectName
//     * @return
//     */
//    public String upload(byte[] bytes, String objectName) {
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        try {
//            // 创建PutObject请求。
//            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//
//        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
//        StringBuilder stringBuilder = new StringBuilder("https://");
//        stringBuilder
//                .append(bucketName)
//                .append(".")
//                .append(endpoint)
//                .append("/")
//                .append(objectName);
//
//        log.info("文件上传到:{}", stringBuilder.toString());
//
//        return stringBuilder.toString();
//    }
//}
