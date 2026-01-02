package com.eagledev.bookreaders.controllers;

import com.eagledev.bookreaders.services.file.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    private final FileStorageService fileStorageService;

    @Operation(summary = "Get file Resource")
    @GetMapping(value = "/{subDirectory}/{fileName}" , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getFile(@PathVariable String subDirectory , @PathVariable String fileName){
            log.info(fileName + " " + subDirectory);
            Resource file = fileStorageService.loadFile(subDirectory,fileName);
            return new ResponseEntity<>(file, HttpStatus.OK);
    }
}
