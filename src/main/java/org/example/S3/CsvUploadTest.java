package org.example.S3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 *  csv 파일을 s3 클라우드로 업로드하기 위해서는
 *  1. 전용 유저를 생성하고
 *  2. AmazonS3FullAccess (arn:aws:iam::aws:policy/AmazonS3FullAccess) 정책 권한을 부여한 후
 *  3. 전용 유저의 액세스키와 시크릿키를 생성해서 S3Client 의 크레덴셜으로 사용해야 한다.
 * */

public class CsvUploadTest {

    public static void main(String[] args) {

        try{
            // ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
            Region region = Region.AP_NORTHEAST_2;
            S3Client s3 = S3Client.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    ""
                                    , "")))
                    .build();
            // createBucket(s3, "dev-user-v2-20240128");
            // deleteBucket(s3, "dev-user-v2-20240128");
            // putObject(s3, "dev-user-v2-20240128");
            // deleteObject(s3, "dev-user-v2-20240128");
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
        }

    }

    // Create a bucket by using a S3Waiter object
    public static void createBucket( S3Client s3Client, String bucketName) {

        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Wait until the bucket is created and print out the response.
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(bucketName +" is ready");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }

    public static void deleteBucket(S3Client s3Client, String bucketName) {

        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.deleteBucket(deleteBucketRequest);
        s3Client.close();

    }

    public static void putObject(S3Client s3Client, String bucketName) {

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("test-object-20240128")
                    .build();

            //s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(getRandomByteBuffer(10_000)));
            s3Client.putObject(objectRequest
                    , RequestBody.fromFile(new File("/Users/m1/Downloads/ml-latest-small/movies_test.csv")));
        } catch(Exception e){
            e.getStackTrace();
        }
    }

    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }

    public static void deleteObject(S3Client s3Client, String bucketName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key("test-object-20240128")
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

}
