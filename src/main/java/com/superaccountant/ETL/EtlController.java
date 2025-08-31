package com.superaccountant.ETL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class EtlController {

    private final TallyXmlDataRepository tallyXmlDataRepository;

    @Autowired
    public EtlController(TallyXmlDataRepository tallyXmlDataRepository) {
        this.tallyXmlDataRepository = tallyXmlDataRepository;
    }

    @MutationMapping
    public UploadTallyXmlResponse uploadTallyXml(@Argument("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new UploadTallyXmlResponse("File is empty");
        }

        try {
            String xmlContent = new String(file.getBytes());
            TallyXmlData tallyXmlData = new TallyXmlData(file.getOriginalFilename(), xmlContent);
            tallyXmlDataRepository.save(tallyXmlData);

            return new UploadTallyXmlResponse("File uploaded and saved successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return new UploadTallyXmlResponse("Error reading file: " + e.getMessage());
        }
    }
}
