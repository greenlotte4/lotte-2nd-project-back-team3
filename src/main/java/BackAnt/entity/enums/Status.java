package BackAnt.entity.enums;

public enum Status {
    ACTIVE("활성화된 계정"),
    INVITE("초대상태"),
    INVITE_COMPLETE("초대완료상태"),
    INACTIVE("비활성화된 계정"),
    SUSPENDED("일시 정지된 계정"),
    LOCKED("잠긴 계정"),
    EXPIRED("만료된 계정"),
    DELETED("삭제된 계정");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
