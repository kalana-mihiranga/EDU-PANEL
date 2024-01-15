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
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpUpgradeHandler;
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
    public void updateLecturerDetailsViaMultipart(@PathVariable("lecturer-id") Integer lecturerId,
                                                  @ModelAttribute @Validated(LecturerReqTo.Update.class) LecturerReqTo lecturerReqTO){
        Lecturer currentLecturer = em.find(Lecturer.class, lecturerId);
        if (currentLecturer == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        em.getTransaction().begin();
        try {
            Lecturer newLecturer = mapper.map(lecturerReqTO, Lecturer.class);
            newLecturer.setId(lecturerId);
            newLecturer.setPicture(null);
            newLecturer.setLinkedin(null);

            if (lecturerReqTO.getPicture() != null) {
                newLecturer.setPicture(new Picture(newLecturer, "lecturers/" + lecturerId));
            }
            if (lecturerReqTO.getLinkedin() != null) {
                newLecturer.setLinkedin(new Linkdin(newLecturer, lecturerReqTO.getLinkedin()));
            }


            if (newLecturer.getPicture() != null && currentLecturer.getPicture() == null) {
                em.persist(newLecturer.getPicture());
                bucket.create(newLecturer.getPicture().getPicturePath(), lecturerReqTO.getPicture().getInputStream(), lecturerReqTO.getPicture().getContentType());
            } else if (newLecturer.getPicture() == null && currentLecturer.getPicture() != null) {
                em.remove(currentLecturer.getPicture());
                bucket.get(currentLecturer.getPicture().getPicturePath()).delete();
            } else if (newLecturer.getPicture() != null) {
                em.merge(newLecturer.getPicture());
                bucket.create(newLecturer.getPicture().getPicturePath(), lecturerReqTO.getPicture().getInputStream(), lecturerReqTO.getPicture().getContentType());
            }

            em.merge(newLecturer);
            em.getTransaction().commit();
        } catch (Throwable t) {
            em.getTransaction().rollback();
            throw new RuntimeException(t);
        }

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{lecturer-id",consumes = "application/json")
    public void updateLecturerDetailsViaJson(@PathVariable("lecturer-id") Integer lecturerId){

    }



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lecturer-id")
    public void deleteLecturerDetails(@PathVariable("lecturer-id") Integer lecturerId){
        Lecturer lecturer = em.find(Lecturer.class, lecturerId);
        if (lecturer == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        em.getTransaction().begin();
        try {
            em.remove(lecturer);

            if (lecturer.getPicture() != null) {
                bucket.get(lecturer.getPicture().getPicturePath()).delete();
            }

            em.getTransaction().commit();
        } catch (Throwable t) {
            em.getTransaction().rollback();
            throw new RuntimeException(t);
        }
    }

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
    public LecturerTo getLecturerDetails(@PathVariable("lecturer-id") Integer lecturerId){
        Lecturer lecturer = em.find(Lecturer.class, lecturerId);
        if(lecturer==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND );
        LecturerTo lecturerTo=mapper.map(lecturer,LecturerTo.class);
        if(lecturer.getPicture()!=null){
            lecturer.setPicture(bucket.get(lecturer.getPicture().getPicturePath()).signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString());
        }
        return lecturerTo;



    }

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
