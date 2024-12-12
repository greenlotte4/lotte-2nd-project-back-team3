    package BackAnt.entity.approval;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.*;
    import lombok.experimental.SuperBuilder;

    import java.time.LocalDate;
    import java.util.List;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Table(name = "BusinessTrip")
    public class BusinessTrip extends ApprovalRequest {
        private String title;
        private String organization;
        private String purpose;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double budget;

        @OneToMany(mappedBy = "businessTrip", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<BusinessTripSchedule> schedules;
    }

