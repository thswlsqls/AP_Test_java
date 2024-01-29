package org.example.tutorial.STEP4;

// import client packages
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
// Amazon Personalize exception package
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

// solution packages
import software.amazon.awssdk.services.personalize.model.CreateSolutionRequest;
import software.amazon.awssdk.services.personalize.model.CreateSolutionResponse;


public class Step4_1 {

    public static void main(String[] args) {

        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
    }

    // 다음 createPersonalizeSolution 메서드를 사용하여 새 솔루션을 생성합니다.
    // Amazon Personalize 서비스 클라이언트, 데이터 세트 그룹 Amazon 리소스 이름(ARN),
    // 솔루션 이름, 사용자 개인 맞춤 레시피의 ARN(arn:aws:personalize:::recipe/aws-user-personalization)을 파라미터로 전달합니다.
    // 메서드가 새 솔루션의 ARN을 반환합니다. 나중에 사용하기 위해 이 ARN을 저장합니다.
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


}
