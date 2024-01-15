package lk.ijse.dep11.edupanel.api;

import com.google.cloud.storage.Storage;
import com.google.storage.v2.Bucket;
import lk.ijse.dep11.edupanel.entity.Lecturer;
import lk.ijse.dep11.edupanel.entity.Linkdin;
import lk.ijse.dep11.edupanel.entity.Picture;
import lk.ijse.dep11.to.LecturerTo;
import lk.ijse.dep11.to.request.LecturerReqTo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Blob;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {
    @Autowired
    private EntityManager em;
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Bucket bucket;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data",produces ="application/json" )
    public LecturerTo createNewLecturer(@ModelAttribute @Validated(LecturerReqTo.Create.class) LecturerReqTo lecturerReqTo ){
        em.getTransaction().begin();
        try{
//            Lecturer lecturer = new Lecturer(lecturerReqTo.getName(), lecturerReqTo.getDesignation(), lecturerReqTo.getQualification(), lecturerReqTo.getType(), lecturerReqTo.getDisplayOrder());

            Lecturer lecturer = mapper.map(lecturerReqTo, Lecturer.class);
            lecturer.setPicture(null);
            lecturer.setLinkedin(null);
            em.persist(lecturer);
            LecturerTo lecturerTo= mapper.map(lecturer, LecturerTo.class);

            if(lecturerReqTo.getLinkedin()!=null){
                em.persist(new Linkdin(lecturer,lecturerReqTo.getLinkedin()));
                lecturerTo.setLinkdin(lecturerReqTo.getLinkedin());
            }
            if(lecturerReqTo.getPicture()!=null){
                Picture picture = new Picture(lecturer, "lectures/" + lecturer.getId());
                em.persist(picture);
                Blob blobRef = bucket.create(picture.getPicturePath(), lecturerReqTo.getPicture().getInputStream(), lecturerReqTo.getPicture().getContentType());
                lecturerTo.setPicture(blobRef.signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString());

            }




            em.getTransaction().commit();

            return lecturerTo;
        }catch (Throwable t){
            em.getTransaction().rollback();
            throw new RuntimeException();
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
    public List<LecturerTo> getAllLecturers(){
        TypedQuery<Lecturer> query = em.createQuery("SELECT l FROM Lecturer l", Lecturer.class);
        query.getResultStream().map(l->{
            LecturerTo lecturerTo=mapper.map(l,LecturerTo.class);
            lecturerTo.setLinkdin((l.getLinkedin().getUrl()));
            if(lecturerTo.getPicture()!=null){
                lecturerTo.setPicture(bucket.get(l.getPicture().getPicturePath()).signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString());
            }
            return lecturerTo;

        }).collect(Collectors.toList());
        return null;
    }

    @GetMapping(value = "/{lecturer-id",produces = "application/json")
    public void getLecturerDetails(@PathVariable("lecturer-id") Integer lecturerId){}

    @GetMapping (params = "type=full-time",produces = "application/json")
    public List<LecturerTo> getFullTimeLecturers(){
        TypedQuery<Lecturer> query = em.createQuery("SELECT l FROM Lecturer l WHERE l.type=lk.ijse.dep11.edupanel.util.LecturerType.FULL_TIME", Lecturer.class);
        query.getResultStream().map(l->{
            LecturerTo lecturerTo=mapper.map(l,LecturerTo.class);
            lecturerTo.setLinkdin((l.getLinkedin().getUrl()));
            if(lecturerTo.getPicture()!=null){
                lecturerTo.setPicture(bucket.get(l.getPicture().getPicturePath()).signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString());
            }
            return lecturerTo;

        }).collect(Collectors.toList());
        return null;
    }

    @GetMapping(params = "type=visiting",produces = "application/json")
    public List<LecturerTo> getVisitingLecturers(){
        TypedQuery<Lecturer> query = em.createQuery("SELECT l FROM Lecturer l WHERE l.type=lk.ijse.dep11.edupanel.util.LecturerType.VISITING", Lecturer.class);
        query.getResultStream().map(l->{
            LecturerTo lecturerTo=mapper.map(l,LecturerTo.class);
            lecturerTo.setLinkdin((l.getLinkedin().getUrl()));
            if(lecturerTo.getPicture()!=null){
                lecturerTo.setPicture(bucket.get(l.getPicture().getPicturePath()).signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString());
            }
            return lecturerTo;

        }).collect(Collectors.toList());
        return null;
    }
}
