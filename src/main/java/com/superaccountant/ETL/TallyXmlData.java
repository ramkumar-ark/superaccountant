package com.superaccountant.ETL;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tally_xml_data")
@Getter
@Setter
@NoArgsConstructor
public class TallyXmlData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Lob
    @Column(name = "xml_content", columnDefinition = "TEXT")
    private String xmlContent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TallyXmlData(String fileName, String xmlContent) {
        this.fileName = fileName;
        this.xmlContent = xmlContent;
    }
}