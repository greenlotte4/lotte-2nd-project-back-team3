package BackAnt.entity.chatting;

import BackAnt.entity.User;
import BackAnt.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Dm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "dm_members",
            joinColumns = @JoinColumn(name = "dm_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;
}
