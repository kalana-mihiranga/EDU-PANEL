package lk.ijse.dep11.edupanel.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LecturerImageConstrainntValidator implements ConstraintValidator<LecturerImage, MultipartFile> {
    private long maximumFileSize;

    @Override
    public void initialize(LecturerImage constraintAnnotation) {
        maximumFileSize=constraintAnnotation.maxFileSize();

    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if(multipartFile==null || multipartFile.isEmpty()) return true;
        if(multipartFile.getContentType()==null || !multipartFile.getContentType().startsWith("image/"))return false;
        if(multipartFile.getSize()>maximumFileSize) return false;
        return true;
    }
}
