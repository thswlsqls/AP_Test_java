package org.example.tutorial.STEP2;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;

public class Step2_1 {

    public static void main(String[] args) {

        // Amazon Personalize 종속 항목을 pom.xml 파일에 추가하고 필요한 패키지를 가져온 후
        // 다음과 같은 Amazon Personalize 클라이언트를 생성합니다.
        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
    }
}
