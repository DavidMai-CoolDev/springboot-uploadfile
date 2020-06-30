package com.example.selflearning.demo.springboot.uploadfile;

import com.example.selflearning.demo.springboot.uploadfile.criteria.FileSpecification;
import com.example.selflearning.demo.springboot.uploadfile.criteria.SearchCriteria;
import com.example.selflearning.demo.springboot.uploadfile.criteria.SearchOperation;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.hibernate.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*", maxAge = 3600000,allowCredentials = "true")
public class Application {
	@Autowired
	private FileUploadService fileUploadService;
	private static final String OUTPUT_ZIP_FILE = "Folder.zip";
	private static final String SOURCE_FOLDER = "uploadfolder-test";
	@Autowired
	private ServletContext servletContext;
	@GetMapping("/")
	public String ok(){
		return "OK";
	}
	@PostMapping("/uploadfile")
	public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file, @RequestPart("userinfor") String code, @RequestPart("username") String username) throws IOException {
		return fileUploadService.uploadFile(file, username);
	}

	@PostMapping("uploadfolder")
	public ResponseEntity<?> uploadFolder(@RequestPart("file") MultipartFile file,
										  @RequestPart("webPath") String webPath) throws IOException {
		fileUploadService.uploadFolder(file, webPath);
		return fileUploadService.uploadfileInFolder(file,webPath);
	}

	@PostMapping("/uploadfolderreference")
	public ResponseEntity<?> addNewItem(@RequestPart("file") MultipartFile file,
							 @RequestPart("webPath") String webPath
	) throws IOException, IOException {
//		String string = webPath.toString();
		String[] parts = webPath.split("/");
		String directory  = "";
		for (int i =0;i<parts.length-1;i++){
			directory+= parts[i]+"/";
		}
		File directfile = new File(directory);
		if (!directfile.exists()) {
			if (directfile.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		File convFile = new File( directfile.getAbsolutePath()+"/"+ file.getOriginalFilename() );
		FileOutputStream fos = new FileOutputStream( convFile );
		fos.write( file.getBytes() );
		fos.close();
		return ResponseEntity.ok(new MessageResponse("upload sucessfully"));
	}
	@GetMapping("/zipfolder")
	public void zipFolder(HttpServletResponse response) throws IOException {
//		zip file
		ZipUtils appZip = new ZipUtils();
		appZip.generateFileList(new File(SOURCE_FOLDER));
		appZip.zipIt(OUTPUT_ZIP_FILE,SOURCE_FOLDER);


//		download then delete file

		File file = new File(OUTPUT_ZIP_FILE);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
		response.setContentLength((int) file.length());

		OutputStream out = response.getOutputStream();
		FileInputStream in = new FileInputStream(file);
		IOUtils.copy(in,out);

		out.close();
		in.close();
		file.delete();

//		Delete the directory
		boolean result = FileSystemUtils.deleteRecursively(Paths.get("uploadfolder-test"));
	}
	@GetMapping("/GetFiles")
	public List<FileModel> GetAllFiles(){
		return fileUploadService.GetAllFile();
	}


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
