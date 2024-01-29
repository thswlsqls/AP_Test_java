package org.example.tutorial.STEP5;

// import client packages
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
// Amazon Personalize exception package
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

// campaign packages
import software.amazon.awssdk.services.personalize.model.CreateCampaignRequest;
import software.amazon.awssdk.services.personalize.model.CreateCampaignResponse;

public class Step5_1 {

    public static void main(String[] args) {

        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
    }

    // 솔루션 버전을 학습시키고 평가한 후 Amazon Personalize 캠페인을 사용하여 배포할 수 있습니다.
    // 솔루션 버전을 배포하려면 다음 createPersonalCampaign 메서드를 사용합니다.
    // Amazon Personalize 서비스 클라이언트, 이전 단계에서 생성한 솔루션 버전의 Amazon 리소스 이름(ARN), 캠페인 이름을 파라미터로 전달합니다.
    // 메서드가 새 캠페인의 ARN을 반환합니다.
    // 나중에 사용하기 위해 이 ARN을 저장합니다.
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


}
