package org.example.tutorial;

// STEP1 Amazon Personalize 패키지를 사용하도록 프로젝트 설정
// 1-2 프로젝트에 다음 가져오기 명령문을 추가합니다.
// import client packages
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
// Amazon Personalize exception package
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
// schema packages
import software.amazon.awssdk.services.personalize.model.CreateSchemaRequest;
// dataset group packages
import software.amazon.awssdk.services.personalize.model.CreateDatasetGroupRequest;
// dataset packages
import software.amazon.awssdk.services.personalize.model.CreateDatasetRequest;
// dataset import job packages
import software.amazon.awssdk.services.personalize.model.CreateDatasetImportJobRequest;
import software.amazon.awssdk.services.personalize.model.DataSource;
import software.amazon.awssdk.services.personalize.model.DatasetImportJob;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetImportJobRequest;
// solution packages
import software.amazon.awssdk.services.personalize.model.CreateSolutionRequest;
import software.amazon.awssdk.services.personalize.model.CreateSolutionResponse;
// solution version packages
import software.amazon.awssdk.services.personalize.model.DescribeSolutionRequest;
import software.amazon.awssdk.services.personalize.model.CreateSolutionVersionRequest;
import software.amazon.awssdk.services.personalize.model.CreateSolutionVersionResponse;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionVersionRequest;
// campaign packages
import software.amazon.awssdk.services.personalize.model.CreateCampaignRequest;
import software.amazon.awssdk.services.personalize.model.CreateCampaignResponse;
// get recommendations packages
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsRequest;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsResponse;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;
// Java time utility package
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

// STEP2 Amazon Personalize 클라이언트 생성
// 2-1 Amazon Personalize 종속 항목을 pom.xml 파일에 추가하고 필요한 패키지를 가져온 후 다음과 같은 Amazon Personalize 클라이언트를 생성합니다.
import software.amazon.awssdk.regions.Region;

public class TutorialTest {

    public static void main(String[] args) {
        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();

        getRecs(personalizeRuntimeClient, "arn:aws:personalize:ap-northeast-2:962369067237:campaign/my-campaign-v1-20240129", "123");
    }

    // STEP3 데이터 가져오기
    // 3-2 다음 createSchema 메서드를 사용하여 Amazon Personalize에서 스키마를 생성합니다.
    // Amazon Personalize 서비스 클라이언트, 스키마 이름, 이전 단계에서 생성한 스키마 JSON 파일의 파일 경로를 파라미터로 전달합니다.
    // 메서드가 새 스키마의 Amazon 리소스 이름(ARN)을 반환합니다. 나중에 사용하기 위해 이 ARN을 저장합니다.
    public static String createSchema(PersonalizeClient personalizeClient, String schemaName, String filePath) {

        String schema = null;
        try {
            schema = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            CreateSchemaRequest createSchemaRequest = CreateSchemaRequest.builder()
                    .name(schemaName)
                    .schema(schema)
                    .build();

            String schemaArn = personalizeClient.createSchema(createSchemaRequest).schemaArn();

            System.out.println("Schema arn: " + schemaArn);

            return schemaArn;

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    // 3-3 데이터 세트 그룹을 생성합니다. 다음 createDatasetGroup 메서드를 사용하여 데이터 세트 그룹을 생성합니다.
    // Amazon Personalize 서비스 클라이언트 및 데이터 세트 그룹 이름을 파라미터로 전달합니다.
    // 메서드가 새 데이터 세트 그룹의 ARN을 반환합니다. 나중에 사용하기 위해 이 ARN을 저장합니다.
    public static String createDatasetGroup(PersonalizeClient personalizeClient, String datasetGroupName) {

        try {
            CreateDatasetGroupRequest createDatasetGroupRequest = CreateDatasetGroupRequest.builder()
                    .name(datasetGroupName)
                    .build();
            return personalizeClient.createDatasetGroup(createDatasetGroupRequest).datasetGroupArn();
        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    // 3-4 상호작용 데이터 세트를 생성합니다. 다음 createDataset 메서드를 사용하여 상호작용 데이터 세트를 생성합니다.
    // Amazon Personalize 서비스 클라이언트, 데이터 세트 이름, 스키마의 ARN, 데이터 세트 그룹의 ARN, 데이터 세트 유형에 대한 Interactions을 파라미터로 전달합니다.
    // 메서드가 새 데이터 세트의 ARN을 반환합니다. 나중에 사용하기 위해 이 ARN을 저장합니다.
    public static String createDataset(PersonalizeClient personalizeClient,
                                       String datasetName,
                                       String datasetGroupArn,
                                       String datasetType,
                                       String schemaArn) {
        try {
            CreateDatasetRequest request = CreateDatasetRequest.builder()
                    .name(datasetName)
                    .datasetGroupArn(datasetGroupArn)
                    .datasetType(datasetType)
                    .schemaArn(schemaArn)
                    .build();

            String datasetArn = personalizeClient.createDataset(request)
                    .datasetArn();
            System.out.println("Dataset " + datasetName + " created.");
            return datasetArn;

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    // 3-5 데이터 세트 가져오기 작업을 사용하여 데이터를 가져옵니다. 다음 createPersonalizeDatasetImportJob 메서드를 사용하여 데이터 세트 가져오기 작업을 생성합니다.
    //Amazon Personalize 서비스 클라이언트, 작업 이름, 상호작용 데이터 세트의 ARN,
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

    // STEP4 솔루션 생성
    // 데이터를 가져온 후 다음과 같이 솔루션과 솔루션 버전을 생성합니다. 솔루션에는 모델을 학습하기 위한 구성이 포함되어 있으며 솔루션 버전은 학습된 모델입니다.
    // 4-1 다음 createPersonalizeSolution 메서드를 사용하여 새 솔루션을 생성합니다.
    // Amazon Personalize 서비스 클라이언트, 데이터 세트 그룹 Amazon 리소스 이름(ARN), 솔루션 이름,
    // 사용자 개인 맞춤 레시피의 ARN(arn:aws:personalize:::recipe/aws-user-personalization)을 파라미터로 전달합니다.
    // 메서드가 새 솔루션의 ARN을 반환합니다.
    // 나중에 사용하기 위해 이 ARN을 저장합니다.
    public static String createPersonalizeSolution(PersonalizeClient personalizeClient,
                                                   String datasetGroupArn,
                                                   String solutionName,
                                                   String recipeArn) {

        try {
            CreateSolutionRequest solutionRequest = CreateSolutionRequest.builder()
                    .name(solutionName)
                    .datasetGroupArn(datasetGroupArn)
                    .recipeArn(recipeArn)
                    .build();

            CreateSolutionResponse solutionResponse = personalizeClient.createSolution(solutionRequest);
            return solutionResponse.solutionArn();

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    // 4-2 다음 createPersonalizeSolutionVersion 메서드를 사용하여 솔루션 버전을 생성합니다. 이전 단계의 솔루션 ARN을 파라미터로 전달합니다.
    // 다음 코드는 먼저 솔루션이 준비되었는지 확인한 다음 솔루션 버전을 생성합니다.
    // 학습 중에 코드는 DescribeSolutionVersion 작업을 사용하여 솔루션 버전의 상태를 검색합니다.
    // 학습이 완료되면 메서드가 새 솔루션 버전의 ARN을 반환합니다. 나중에 사용하기 위해 이 ARN을 저장합니다.
    public static String createPersonalizeSolutionVersion(PersonalizeClient personalizeClient, String solutionArn) {
        long maxTime = 0;
        long waitInMilliseconds = 30 * 1000; // 30 seconds
        String solutionStatus = "";
        String solutionVersionStatus = "";
        String solutionVersionArn = "";

        try {
            DescribeSolutionRequest describeSolutionRequest = DescribeSolutionRequest.builder()
                    .solutionArn(solutionArn)
                    .build();

            maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

            // Wait until solution is active.
            while (Instant.now().getEpochSecond() < maxTime) {

                solutionStatus = personalizeClient.describeSolution(describeSolutionRequest).solution().status();
                System.out.println("Solution status: " + solutionStatus);

                if (solutionStatus.equals("ACTIVE") || solutionStatus.equals("CREATE FAILED")) {
                    break;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (solutionStatus.equals("ACTIVE")) {

                CreateSolutionVersionRequest createSolutionVersionRequest = CreateSolutionVersionRequest.builder()
                        .solutionArn(solutionArn)
                        .build();

                CreateSolutionVersionResponse createSolutionVersionResponse = personalizeClient.createSolutionVersion(createSolutionVersionRequest);
                solutionVersionArn = createSolutionVersionResponse.solutionVersionArn();

                System.out.println("Solution version ARN: " + solutionVersionArn);

                DescribeSolutionVersionRequest describeSolutionVersionRequest = DescribeSolutionVersionRequest.builder()
                        .solutionVersionArn(solutionVersionArn)
                        .build();

                while (Instant.now().getEpochSecond() < maxTime) {

                    solutionVersionStatus = personalizeClient.describeSolutionVersion(describeSolutionVersionRequest).solutionVersion().status();
                    System.out.println("Solution version status: " + solutionVersionStatus);

                    if (solutionVersionStatus.equals("ACTIVE") || solutionVersionStatus.equals("CREATE FAILED")) {
                        break;
                    }
                    try {
                        Thread.sleep(waitInMilliseconds);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                return solutionVersionArn;
            }
        } catch(PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    // STEP5 캠페인 생성
    // 솔루션 버전을 학습시키고 평가한 후 Amazon Personalize 캠페인을 사용하여 배포할 수 있습니다.
    // 솔루션 버전을 배포하려면 다음 createPersonalCampaign 메서드를 사용합니다.
    // Amazon Personalize 서비스 클라이언트, 이전 단계에서 생성한 솔루션 버전의 Amazon 리소스 이름(ARN), 캠페인 이름을 파라미터로 전달합니다.
    // 메서드가 새 캠페인의 ARN을 반환합니다. 나중에 사용하기 위해 이 ARN을 저장합니다.
    public static String createPersonalCompaign(PersonalizeClient personalizeClient, String solutionVersionArn, String name) {

        try {
            CreateCampaignRequest createCampaignRequest = CreateCampaignRequest.builder()
                    .minProvisionedTPS(1)
                    .solutionVersionArn(solutionVersionArn)
                    .name(name)
                    .build();

            CreateCampaignResponse campaignResponse = personalizeClient.createCampaign(createCampaignRequest);
            System.out.println("The campaign ARN is "+campaignResponse.campaignArn());
            return campaignResponse.campaignArn();

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return null;
        }
    }


    // STEP6 추천 받기
    // 캠페인을 생성한 후 이 캠페인을 사용하여 추천을 받습니다. 사용자에 대한 추천을 받으려면 다음 getRecs 메서드를 사용합니다.
    // Amazon Personalize 런타임 클라이언트, 이전 단계에서 생성한 캠페인의 Amazon 리소스 이름(ARN), 가져온 과거 데이터의 사용자 ID(예:123)를 파라미터로 전달합니다.
    // 메서드가 추천 항목 목록을 화면에 인쇄합니다.
    public static void getRecs(PersonalizeRuntimeClient personalizeRuntimeClient, String campaignArn, String userId){

        try {
            GetRecommendationsRequest recommendationsRequest = GetRecommendationsRequest.builder()
                    .campaignArn(campaignArn)
                    .numResults(20)
                    .userId(userId)
                    .build();

            GetRecommendationsResponse recommendationsResponse = personalizeRuntimeClient.getRecommendations(recommendationsRequest);

            List<PredictedItem> items = recommendationsResponse.itemList();
            for (PredictedItem item: items) {
                System.out.println("Item Id is : "+item.itemId());
                System.out.println("Item score is : "+item.score());
            }

        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

}

