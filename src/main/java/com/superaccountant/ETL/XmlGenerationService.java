package com.superaccountant.ETL;

import com.superaccountant.ETL.model.Envelope;
import com.superaccountant.ETL.model.Body;
import com.superaccountant.ETL.model.ImportData;
import com.superaccountant.ETL.model.RequestData;
import com.superaccountant.ETL.model.TallyMessage;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XmlGenerationService {

    private final XmlModelMapper xmlModelMapper;
    private final JAXBContext jaxbContext;

    @Autowired
    public XmlGenerationService(XmlModelMapper xmlModelMapper, JAXBContext jaxbContext) {
        this.xmlModelMapper = xmlModelMapper;
        this.jaxbContext = jaxbContext;
    }

    public String generateXml(List<Voucher> vouchers) {
        List<TallyMessage> tallyMessages = vouchers.stream()
                .map(xmlModelMapper::toVoucherXml)
                .map(voucherXml -> {
                    TallyMessage tallyMessage = new TallyMessage();
                    tallyMessage.setVoucher(voucherXml);
                    return tallyMessage;
                })
                .collect(Collectors.toList());

        RequestData requestData = new RequestData();
        requestData.setTallyMessages(tallyMessages);

        ImportData importData = new ImportData();
        importData.setRequestData(requestData);

        Body body = new Body();
        body.setImportData(importData);

        Envelope envelope = new Envelope();
        envelope.setBody(body);

        try {
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter stringWriter = new StringWriter();
            jaxbMarshaller.marshal(envelope, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            // Wrap the checked exception in our custom runtime exception.
            throw new XmlGenerationException("Failed to marshal vouchers to XML", e);
        }
    }
}
