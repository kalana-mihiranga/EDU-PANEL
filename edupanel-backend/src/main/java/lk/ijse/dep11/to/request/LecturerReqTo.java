package lk.ijse.dep11.to.request;

import jdk.jfr.DataAmount;
import lk.ijse.dep11.edupanel.util.LecturerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerReqTo implements Serializable {
   private String name;
   private String designation;
   private String qualification;
  private  LecturerType type;
  private  Integer displayOrder;
  private  MultipartFile picture;
  private  String linkedin;

}
