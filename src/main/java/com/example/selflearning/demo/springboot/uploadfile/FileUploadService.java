package com.example.selflearning.demo.springboot.uploadfile;

import com.example.selflearning.demo.springboot.uploadfile.criteria.FileSpecification;
import com.example.selflearning.demo.springboot.uploadfile.criteria.SearchCriteria;
import com.example.selflearning.demo.springboot.uploadfile.criteria.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
@Service
@Transactional
public class FileUploadService {
    @Autowired
    private FileRepository fileRepository;
    @PersistenceContext
    private EntityManager em;
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;

    public ResponseEntity<?> uploadFile (MultipartFile file, String username) throws IOException {
        Double aDouble = (double)file.getSize()/1024/1024;
        Double aDouble1 = BigDecimal.valueOf(aDouble)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        FileModel fileModel = new FileModel();
//        fileModel.setName(username);
        fileModel.setFileName(file.getOriginalFilename());
        fileModel.setFileType(file.getContentType());
        fileModel.setModified(new Date());
        fileModel.setModifiedBy(username);
        fileModel.setFileByte(compressBytes(file.getBytes()));
        fileModel.setSize(aDouble1.toString()+" MB");
        fileModel.setUsername(username);
        fileModel.setUploadType("FILE");
        fileRepository.save(fileModel);
//        this.simpMessagingTemplate.convertAndSend("/topic/greetings", new FileUploadNotificationResponse("New File(s) being uploaded !!" ,new Date()));
        return ResponseEntity.ok(new MessageResponse("OK uploaded!!"));
    }
    //     compress the image bytes before storing it in the database
    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }

    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
        } catch (DataFormatException e) {
        }
        return outputStream.toByteArray();
    }
    //    @Scheduled(fixedRate = 5000)
    public List<FileModel> GetAllFile() {
//        FileSpecification fileSpecification = new FileSpecification();
//        fileSpecification.add(new SearchCriteria("directoryParent", SearchOperation.IS_NULL));
//        List<FileModel> products = fileRepository.findAll(fileSpecification);
//        --------------------------
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<FileModel> cq = cb.createQuery(FileModel.class);
//
//        Root<FileModel> fileModelRoot = cq.from(FileModel.class);
//        Predicate authorNamePredicate = cb.isNull(fileModelRoot.get("directoryParent"));
//        Predicate titlePredicate = cb.isNull(fileModelRoot.get("webPath"));
//        cq.where(authorNamePredicate, titlePredicate);
//        TypedQuery<FileModel> query = em.createQuery(cq);
//        -----------------------------

        String query = "select * from files where directory_parent isnull";
        Query result = em.createNativeQuery(query);

        return result.getResultList();
    }

    public ResponseEntity<?> uploadFolder(MultipartFile file, String webPath) throws IOException {
        String[] parts = webPath.split("/");
        if (parts.length >=3){
            Optional<FileModel> fileModel2 = fileRepository.findByfileName(parts[parts.length-2]);

            if (fileModel2.isPresent()) {
//                throw new RuntimeException("Duplicated Directory Name");
                return ResponseEntity.ok(new MessageResponse("Duplicated Directory Name ^_^"));
            }

            else{
                FileModel newFileModel = new FileModel();
                newFileModel.setDirectoryParent(parts[parts.length-3]);
                newFileModel.setFileName(parts[parts.length-2]);
                newFileModel.setUploadType("FOLDER");
                newFileModel.setModified(new Date());
                fileRepository.save(newFileModel);

            }
        }
        else if(parts.length ==2) {
            Optional<FileModel> fileModel2 = fileRepository.findByfileName(parts[parts.length - 2]);

            if (fileModel2.isPresent()) {
//                throw new RuntimeException("Duplicated Directory Name");
                return ResponseEntity.ok(new MessageResponse("Duplicated Directory Name ^_^"));
            } else {
                FileModel newFileModel = new FileModel();
                newFileModel.setFileName(parts[parts.length - 2]);
                newFileModel.setUploadType("FOLDER");
                newFileModel.setModified(new Date());
                fileRepository.save(newFileModel);
            }
        }


        return ResponseEntity.ok(new MessageResponse("upload sucessfully"));
    }

    public ResponseEntity<?> uploadfileInFolder(MultipartFile file, String webPath) throws IOException {
        String[] parts = webPath.split("/");
        Double aDouble = (double)file.getSize()/1024/1024;
        Double aDouble1 = BigDecimal.valueOf(aDouble)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        FileModel fileModel = new FileModel();
//        fileModel.setName(username);
        fileModel.setFileName(file.getOriginalFilename());
        fileModel.setFileType(file.getContentType());
        fileModel.setModified(new Date());
        fileModel.setWebPath(webPath);
        fileModel.setDirectoryParent(parts[parts.length-2]);
//        fileModel.setModifiedBy(username);
        fileModel.setFileByte(compressBytes(file.getBytes()));
        fileModel.setSize(aDouble1.toString()+" MB");
//        fileModel.setUsername(username);
        fileModel.setUploadType("FILE");
        fileRepository.save(fileModel);
        return ResponseEntity.ok(new MessageResponse("upload sucessfully"));

    }







//    public FileModel DownloadFiles(DownloadFileRequest downloadFileRequest) {
//
//        FileModel fileModel = new FileModel();
//        fileModel =fileRepository.findById(downloadFileRequest.getId()).get();
//        fileModel.setFileByte(decompressBytes(fileModel.getFileByte()));
//        return fileModel;
//    }
}
