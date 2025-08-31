package com.superaccountant.ETL;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class XmlParsingService {

    public TallyData parse(MultipartFile file) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TallyData.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (TallyData) jaxbUnmarshaller.unmarshal(file.getInputStream());
    }
}
