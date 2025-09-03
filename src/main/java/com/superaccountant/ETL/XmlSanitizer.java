package com.superaccountant.ETL;

import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public class XmlSanitizer {

    private static final Logger log = LoggerFactory.getLogger(XmlSanitizer.class);

    public String sanitize(byte[] bytes, String originalFilename) throws JAXBException {
        String content = readWithCorrectEncoding(bytes, originalFilename);

        int firstTagIndex = content.indexOf('<');
        if (firstTagIndex == -1) {
            log.warn("File does not contain any XML tags. File: {}", originalFilename);
            throw new JAXBException("File does not appear to contain XML content.");
        }
        content = content.substring(firstTagIndex);

        String invalidRefRegex = "&(amp;)?#(0?[0-8]|1[12]|1[4-9]|2\\d|3[01]);?";
        content = content.replaceAll(invalidRefRegex, "");

        String xmlInvalidCharRegex = "[^"
                + "\u0009\r\n" // tab, line feed, carriage return
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "]";
        content = content.replaceAll(xmlInvalidCharRegex, "");

        if (content.isEmpty()) {
            log.warn("XML content is empty after cleaning. File: {}", originalFilename);
            throw new JAXBException("XML content is empty after cleaning process.");
        }

        return content;
    }

    private String readWithCorrectEncoding(byte[] bytes, String originalFilename) {
        Charset charset = StandardCharsets.UTF_8;
        int bomLength = 0;
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
            charset = StandardCharsets.UTF_16BE;
            bomLength = 2;
        } else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
            charset = StandardCharsets.UTF_16LE;
            bomLength = 2;
        } else if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF) {
            bomLength = 3;
        }
        log.info("Reading file '{}' with detected/assumed charset: {}", originalFilename, charset.displayName());
        return new String(bytes, bomLength, bytes.length - bomLength, charset);
    }
}