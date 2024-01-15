package lk.ijse.dep11.edupanel.api;

import lk.ijse.dep11.edupanel.entity.Lecturer;
import lk.ijse.dep11.edupanel.entity.Linkdin;
import lk.ijse.dep11.edupanel.entity.Picture;
import lk.ijse.dep11.to.request.LecturerReqTo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {
    @Autowired
    private EntityManager em;
    @Autowired
    private ModelMapper mapper;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data",produces ="application/json" )
    public void createNewLecturer(@ModelAttribute @Validated(LecturerReqTo.Create.class) LecturerReqTo lecturerReqTo ){
        em.getTransaction().begin();
        try{
//            Lecturer lecturer = new Lecturer(lecturerReqTo.getName(), lecturerReqTo.getDesignation(), lecturerReqTo.getQualification(), lecturerReqTo.getType(), lecturerReqTo.getDisplayOrder());

            Lecturer lecturer = mapper.map(lecturerReqTo, Lecturer.class);
            lecturer.setPicture(null);
            lecturer.setLinkedin(null);
            em.persist(lecturer);

            if(lecturerReqTo.getLinkedin()!=null){
                em.persist(new Linkdin(lecturer,lecturerReqTo.getLinkedin()));
            }
            if(lecturerReqTo.getPicture()!=null){
                Picture picture = new Picture(lecturer, "lectures/" + lecturer.getId());
                em.persist(picture);
            }

            em.getTransaction().commit();

        }catch (Throwable t){
            em.getTransaction().rollback();
            throw t;
        }


    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{lecturer-id",consumes = "multipart/form-data")
    public void updateLecturerDetailsViaMultipart(@PathVariable("lecturer-id") Integer lecturerId){}

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{lecturer-id",consumes = "application/json")
    public void updateLecturerDetailsViaJson(@PathVariable("lecturer-id") Integer lecturerId){}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lecturer-id")
    public void deleteLecturerDetails(@PathVariable("lecturer-id") Integer lecturerId){}

    @GetMapping(produces = "application/json")
    public void getAllLecturers(){}

    @GetMapping(value = "/{lecturer-id",produces = "application/json")
    public void getLecturerDetails(@PathVariable("lecturer-id") Integer lecturerId){}

    @GetMapping (params = "type=full-time",produces = "application/json")
    public void getFullTimeLecturers(){}

    @GetMapping(params = "type=visiting",produces = "application/json")
    public void getVisitingLecturers(){}

}
