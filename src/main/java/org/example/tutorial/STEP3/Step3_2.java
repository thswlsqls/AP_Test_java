package org.example.tutorial.STEP3;

// import client packages
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
// Amazon Personalize exception package
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
// schema packages
import software.amazon.awssdk.services.personalize.model.CreateSchemaRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Step3_2 {

    public static void main(String[] args) {

        Region region = Region.AP_NORTHEAST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
    }

    // 다음 createSchema 메서드를 사용하여 Amazon Personalize에서 스키마를 생성합니다.
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

}
