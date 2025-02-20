package com.clokey.server.domain.model.entity.enums;

public enum ThicknessLevel {
    LEVEL_0(0, "나시, 반팔, 반바지 등"),
    LEVEL_1(1, "긴팔, 셔츠, 슬랙스 등 얇은 소재"),
    LEVEL_2(2, "기모 無 맨투맨, 후드티 등"),
    LEVEL_3(3, "기모 有 맨투맨, 후드티, 가디건, 니트 등"),
    LEVEL_4(4, "코트, 무스탕 등"),
    LEVEL_5(5, "패딩 등 두꺼운 아우터");

    private final int value;          // 값 (숫자)
    private final String description; // 한국어 설명

    // 생성자
    ThicknessLevel(int value, String description) {
        this.value = value;
        this.description = description;
    }

    // Getter 메서드
    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    // 값으로 Enum을 찾는 정적 메서드
    public static ThicknessLevel fromValue(int value) {
        for (ThicknessLevel level : ThicknessLevel.values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 두께 레벨 값: " + value);
    }
}
