package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.converter.CategoryConverter;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{

    private final ObjectMapper objectMapper;

    @Value("${gpt.api.url}")
    private String url;

    @Value("${gpt.api.key}")
    private String apiKey;

    public String createPrompt(String clothingName) {
        return String.format(
                "옷 이름을 드릴 테니, 적절한 카테고리를 추천해주세요. "
                        + "카테고리는 다음과 같으며, 숫자는 카테고리 아이디입니다:\n\n"
                        + "{\n"
                        + "  \"상의\": {\n"
                        + "    \"5\": \"티셔츠\",\n"
                        + "    \"6\": \"니트/스웨터\",\n"
                        + "    \"7\": \"맨투맨\",\n"
                        + "    \"8\": \"후드티\",\n"
                        + "    \"9\": \"셔츠/블라우스\",\n"
                        + "    \"10\": \"반팔티\",\n"
                        + "    \"11\": \"나시\",\n"
                        + "    \"12\": \"기타\"\n"
                        + "  },\n"
                        + "  \"하의\": {\n"
                        + "    \"13\": \"청바지\",\n"
                        + "    \"14\": \"반바지\",\n"
                        + "    \"15\": \"트레이닝/조거팬츠\",\n"
                        + "    \"16\": \"면바지\",\n"
                        + "    \"17\": \"슈트팬츠/슬렉스\",\n"
                        + "    \"18\": \"레깅스\",\n"
                        + "    \"19\": \"미니스커트\",\n"
                        + "    \"20\": \"미디스커트\",\n"
                        + "    \"21\": \"롱스커트\",\n"
                        + "    \"22\": \"원피스\",\n"
                        + "    \"23\": \"투피스\",\n"
                        + "    \"24\": \"기타\"\n"
                        + "  },\n"
                        + "  \"아우터\": {\n"
                        + "    \"25\": \"숏패딩/헤비 아우터\",\n"
                        + "    \"26\": \"무스탕/퍼\",\n"
                        + "    \"27\": \"후드집업\",\n"
                        + "    \"28\": \"점퍼/바람막이\",\n"
                        + "    \"29\": \"가죽자켓\",\n"
                        + "    \"30\": \"청자켓\",\n"
                        + "    \"31\": \"슈트/블레이저\",\n"
                        + "    \"32\": \"가디건\",\n"
                        + "    \"33\": \"아노락\",\n"
                        + "    \"34\": \"후리스/양털\",\n"
                        + "    \"35\": \"코트\",\n"
                        + "    \"36\": \"롱패딩\",\n"
                        + "    \"37\": \"패딩조끼\",\n"
                        + "    \"38\": \"기타\"\n"
                        + "  },\n"
                        + "  \"기타\": {\n"
                        + "    \"39\": \"신발\",\n"
                        + "    \"40\": \"가방\",\n"
                        + "    \"41\": \"모자\",\n"
                        + "    \"42\": \"머플러\",\n"
                        + "    \"43\": \"시계\",\n"
                        + "    \"44\": \"양말/레그웨어\",\n"
                        + "    \"45\": \"주얼리\",\n"
                        + "    \"46\": \"벨트\",\n"
                        + "    \"47\": \"선글라스/안경\",\n"
                        + "    \"48\": \"기타\"\n"
                        + "  }\n"
                        + "}\n\n"
                        + "'%s'에 해당하는 카테고리를 추천해주세요.\n"
                        + "출력 형식은 다음과 같습니다:\n"
                        + "[{\"큰 카테고리\": \"상의\", \"작은 카테고리\": \"티셔츠\", \"카테고리 아이디\": 5}]",
                clothingName
        );
    }


    @Override
    public CategoryResponseDTO.CategoryRecommendResult getChatGPTResponse(String clothingName) {
        String prompt = createPrompt(clothingName);

        // ChatGPT API 호출
        String response = chatGPT(prompt);

        // 결과 파싱
        return parseResponse(response);
    }

    @Override
    public String chatGPT(String prompt) {
        System.out.println(url);
        String model = "gpt-3.5-turbo";

        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String jsonBody = String.format(
                "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
                model, prompt.replaceAll("\"", "\\\\\"")
        );

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return extractMessageFromJSONResponse(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to OpenAI: " + e.getMessage();
        }

    }

    public String extractMessageFromJSONResponse(String response) {
        try {
            Map<String, Object> map = objectMapper.readValue(response, new TypeReference<>() {});
            List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices in API response: " + response);
            }

            Map<String, Object> messageMap = (Map<String, Object>) choices.get(0).get("message");
            if (messageMap == null || !messageMap.containsKey("content")) {
                throw new RuntimeException("Invalid message format in API response: " + response);
            }

            return (String) messageMap.get("content");

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }




    private CategoryResponseDTO.CategoryRecommendResult parseResponse(String response) {
        try {
            // JSON 파싱을 위한 ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 응답을 리스트로 변환
            List<Map<String, Object>> parsedList = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> item : parsedList) {
                // "큰 카테고리", "작은 카테고리", "카테고리 아이디" 추출
                String largeCategory = (String) item.get("큰 카테고리");
                String smallCategory = (String) item.get("작은 카테고리");
                Long categoryId = (Long) item.get("카테고리 아이디");

                // DTO 생성 및 리스트에 추가
                return CategoryConverter.toRecommendResultDTO(categoryId, largeCategory, smallCategory);
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        CategoryServiceImpl chatGPTService = new CategoryServiceImpl(null); // `null`은 필요 시 `MusicService` 의존성 주입에 맞게 수정

        // 프롬프트 생성
        String prompt = chatGPTService.createPrompt("흰색 티셔츠");

        // ChatGPT API 호출
        String response = chatGPTService.chatGPT(prompt);

        // 결과 출력
        System.out.println("===== ChatGPT Response =====");
        System.out.println(response);

        CategoryResponseDTO.CategoryRecommendResult result = chatGPTService.parseResponse(response);

        System.out.println(result);

    }
}
