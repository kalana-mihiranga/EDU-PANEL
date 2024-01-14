package lk.ijse.dep11.to.request;

import jdk.jfr.DataAmount;
import lk.ijse.dep11.edupanel.util.LecturerType;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.groups.Default;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerReqTo implements Serializable {
    @NotBlank(message = "Name Cannot be emphty")
    @Pattern(regexp = "^[A-Za-z ]{2,}$",message = "Invalid name")
   private String name;

    @NotBlank(message = "Designation Cannot be emphty")
    @Length(min = 3,message = "Invalid designation")
   private String designation;

    @NotBlank(message = "Qualification Cannot be emphty")
    @Length(min = 3,message = "Invalid qualification")
    private String qualification;

    @NotNull(message = "Type should either fulltime or visiting")
  private  LecturerType type;

    @Null(groups = Create.class,message = "Display Order Should be Empty")
    @NotNull(groups = Update.class,message = "Display Order Cant be emphty")
    @PositiveOrZero(groups = Update.class,message = "invalid display order")

  private  Integer displayOrder;
  private  MultipartFile picture;

  @Pattern(regexp = "^http(s)://.+$",message = "Invalid linkdin Url")
  private  String linkedin;

  public  interface Create extends Default{}

    public interface Update extends Default{}

}
