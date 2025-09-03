package com.superaccountant.ETL;

import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileUploadRestController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadRestController.class);

    private final XmlParsingService xmlParsingService;

    @Autowired
    public FileUploadRestController(XmlParsingService xmlParsingService) {
        this.xmlParsingService = xmlParsingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadTallyXmlResponse> uploadTallyXml(@RequestParam("file") MultipartFile file)
            throws JAXBException, IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadTallyXmlResponse("File is empty"));
        }

        xmlParsingService.parseAndSave(file);
        String successMessage = "File uploaded and parsed successfully: " + file.getOriginalFilename();
        return ResponseEntity.ok(new UploadTallyXmlResponse(successMessage));
    }

    @PostMapping("/reprocess/{id}")
    public ResponseEntity<UploadTallyXmlResponse> reprocessTallyXml(@PathVariable Long id) throws JAXBException {
        xmlParsingService.reprocessXml(id);
        String successMessage = "Successfully reprocessed staged XML data with id: " + id;
        return ResponseEntity.ok(new UploadTallyXmlResponse(successMessage));
    }
}
