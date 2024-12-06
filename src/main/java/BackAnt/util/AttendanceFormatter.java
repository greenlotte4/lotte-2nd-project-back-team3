package BackAnt.util;
/*
*   날짜 : 2024/12/06
*   이름 : 최준혁
*   내용 : LocalDate 보기좋은 문자열 가공 Util클래스
*/

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AttendanceFormatter {

    public static String formatCheckInTime(String checkInTime) {
        // 문자열을 LocalDateTime 객체로 변환
        LocalDateTime dateTime = LocalDateTime.parse(checkInTime);

        // 시분초만 추출하여 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static void main(String[] args) {
        String checkInTime = "2024-12-06T11:41:51.897274600";
        String formattedTime = formatCheckInTime(checkInTime);
        System.out.println("Formatted Check-In Time: " + formattedTime); // 출력: 11:41:51
    }
}
