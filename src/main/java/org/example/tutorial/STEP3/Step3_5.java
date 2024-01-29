package org.example.tutorial.STEP3;

// import client packages
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
// Amazon Personalize exception package
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

// dataset import job packages
import software.amazon.awssdk.services.personalize.model.CreateDatasetImportJobRequest;
import software.amazon.awssdk.services.personalize.model.DataSource;
import software.amazon.awssdk.services.personalize.model.DatasetImportJob;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetImportJobRequest;

import java.time.Instant;

public class Step3_5 {

    public static void main(String[] args) {

        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
    }

    // 데이터 세트 가져오기 작업을 사용하여 데이터를 가져옵니다. 다음 createPersonalizeDatasetImportJob 메서드를 사용하여 데이터 세트 가져오기 작업을 생성합니다.
    // Amazon Personalize 서비스 클라이언트, 작업 이름, 상호작용 데이터 세트의 ARN,
    // 학습 데이터를 저장한 Amazon S3 버킷 경로(s3://bucket name/folder name/ratings.csv),
    // 서비스 역할의 ARN(이 역할을 시작하기 전제 조건의 일부로 생성함)을 파라미터로 전달합니다.
    // 메서드가 데이터 세트 가져오기 작업의 ARN을 반환합니다. 나중에 사용할 수 있도록 저장할 수도 있습니다.
    public static String createPersonalizeDatasetImportJob(PersonalizeClient personalizeClient,
                                                           String jobName,
                                                           String datasetArn,
                                                           String s3BucketPath,
                                                           String roleArn) {

        long waitInMilliseconds = 60 * 1000;
        String status;
        String datasetImportJobArn;

        try {
            DataSource importDataSource = DataSource.builder()
                    .dataLocation(s3BucketPath)
                    .build();

            CreateDatasetImportJobRequest createDatasetImportJobRequest = CreateDatasetImportJobRequest.builder()
                    .datasetArn(datasetArn)
                    .dataSource(importDataSource)
                    .jobName(jobName)
                    .roleArn(roleArn)
                    .build();

            datasetImportJobArn = personalizeClient.createDatasetImportJob(createDatasetImportJobRequest)
                    .datasetImportJobArn();
            DescribeDatasetImportJobRequest describeDatasetImportJobRequest = DescribeDatasetImportJobRequest.builder()
                    .datasetImportJobArn(datasetImportJobArn)
                    .build();

            long maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

            while (Instant.now().getEpochSecond() < maxTime) {

                DatasetImportJob datasetImportJob = personalizeClient
                        .describeDatasetImportJob(describeDatasetImportJobRequest)
                        .datasetImportJob();

                status = datasetImportJob.status();
                System.out.println("Dataset import job status: " + status);

                if (status.equals("ACTIVE") || status.equals("CREATE FAILED")) {
                    break;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            return datasetImportJobArn;

        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

}
