package org.example.tutorial.STEP6;

// import client packages
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;

// get recommendations packages
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsRequest;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsResponse;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;

import java.util.List;

public class Step6_1 {

    public static void main(String[] args) {

        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();

        getRecs(personalizeRuntimeClient, "arn:aws:personalize:ap-northeast-2:962369067237:campaign/campaignName4_2104", "123");
    }

    // 캠페인을 생성한 후 이 캠페인을 사용하여 추천을 받습니다.
    // 사용자에 대한 추천을 받으려면 다음 getRecs 메서드를 사용합니다.
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
