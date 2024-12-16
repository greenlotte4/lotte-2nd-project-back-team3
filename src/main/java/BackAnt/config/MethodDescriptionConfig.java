package BackAnt.config;

import java.util.Map;

public class MethodDescriptionConfig {
    public static final Map<String, String> DESCRIPTIONS = Map.of(
            "login", "사용자 로그인 처리",
            "logoutUser", "사용자 로그아웃 처리",
            "getUserInfo", "사용자 정보 조회",
            "createProject", "프로젝트 추가"
    );

    public static String getDescription(String methodName) {
        return DESCRIPTIONS.getOrDefault(methodName, "설명이 등록되지 않은 메서드");
    }
}
