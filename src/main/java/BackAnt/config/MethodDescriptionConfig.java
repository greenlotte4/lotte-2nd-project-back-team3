package BackAnt.config;

import java.util.HashMap;
import java.util.Map;

public class MethodDescriptionConfig {
    public static final Map<String, String> DESCRIPTIONS;

    static {
        DESCRIPTIONS = new HashMap<>();
        DESCRIPTIONS.put("login", "사용자 로그인 처리");
        DESCRIPTIONS.put("logoutUser", "사용자 로그아웃 처리");
        DESCRIPTIONS.put("getUserInfo", "사용자 정보 조회");
        DESCRIPTIONS.put("calendar", "캘린더 생성");
        DESCRIPTIONS.put("deleteCalendar", "캘린더 삭제");
        DESCRIPTIONS.put("updateShare", "공유 캘린더 인원 초대");
        DESCRIPTIONS.put("deleteShare", "공유 캘린더 인원 삭제");
        DESCRIPTIONS.put("insertSchedule", "일정 추가");
        DESCRIPTIONS.put("deleteSchedule", "일정 삭제");
        DESCRIPTIONS.put("createProject", "프로젝트 추가");
        DESCRIPTIONS.put("deleteProject", "프로젝트 삭제");
        DESCRIPTIONS.put("addCollaboratorToProject", "프로젝트 협업자 추가");
        DESCRIPTIONS.put("removeProjectCollaborator", "프로젝트 협업자 삭제");
        DESCRIPTIONS.put("addState", "작업상태 생성");
        DESCRIPTIONS.put("deleteState", "작업상태 삭제");
        DESCRIPTIONS.put("createTask", "작업 생성");
        DESCRIPTIONS.put("deleteTaskById", "작업 삭제");
        DESCRIPTIONS.put("createPage", "페이지 생성");
        DESCRIPTIONS.put("DeleteById", "페이지 삭제");
        DESCRIPTIONS.put("addCollaborators", "페이지 협업자 추가");
        DESCRIPTIONS.put("removeCollaborator", "페이지 협업자 삭제");
        DESCRIPTIONS.put("folderInsert", "폴더 등록");
        DESCRIPTIONS.put("filesInsert", "파일 등록");
        DESCRIPTIONS.put("MyDriveFileDownload", "파일 다운로드");
        DESCRIPTIONS.put("insertDriveCollaborator", "폴더 협업자 추가");
        DESCRIPTIONS.put("deleteDriveCollaborator", "폴더 협업자 삭제");
    }

    public static String getDescription(String methodName) {
        return DESCRIPTIONS.getOrDefault(methodName, "설명이 등록되지 않은 메서드");
    }
}
