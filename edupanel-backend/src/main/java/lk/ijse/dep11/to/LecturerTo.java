package lk.ijse.dep11.to;

import lk.ijse.dep11.edupanel.util.LecturerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data@AllArgsConstructor@NoArgsConstructor

public class LecturerTo implements Serializable {
  private  Integer id;
  private  String name;
 private   String designation;
  private  String qualification;
  private  LecturerType type;
 private   Integer displayOrder;
 private   String picture;
  private  String linkdin;

}
