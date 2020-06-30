package com.example.selflearning.demo.springboot.uploadfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "files")
public class FileModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "web_path")
    private String webPath;
    @Column(name = "directory_parent")
    private String directoryParent;
    @Column(name = "upload_type")
    private String uploadType;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "username")
    private String username;
    @Column(name = "modified")
    private Date modified;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "size")
    private String Size;
    @Column(name = "file_byte", length = 5000)
    private byte[] fileByte;


}
