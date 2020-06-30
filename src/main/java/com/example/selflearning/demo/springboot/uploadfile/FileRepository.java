package com.example.selflearning.demo.springboot.uploadfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileModel,Long>, JpaSpecificationExecutor<FileModel> {
    Optional<FileModel> findBydirectoryParent(String part);
    Optional<FileModel> findByfileName(String part);
}
