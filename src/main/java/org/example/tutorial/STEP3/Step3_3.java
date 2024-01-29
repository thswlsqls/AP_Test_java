package org.example.tutorial.STEP3;

// import client packages
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
// Amazon Personalize exception package
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

// dataset group packages
import software.amazon.awssdk.services.personalize.model.CreateDatasetGroupRequest;

public class Step3_3 {

    public static void main(String[] args) {

        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
    }

    // 데이터 세트 그룹을 생성합니다. 다음 createDatasetGroup 메서드를 사용하여 데이터 세트 그룹을 생성합니다.
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


}
