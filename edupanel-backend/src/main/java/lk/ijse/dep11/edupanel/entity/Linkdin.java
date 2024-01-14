package lk.ijse.dep11.edupanel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "linkdin")
public class Linkdin implements Serializable {
    @OneToOne
    @Id
    @JoinColumn(name = "lecturer_id",referencedColumnName = "id")
    private Lecturer lecturer;
    @Column(nullable = false,length = 2000)
    private String url;

}
