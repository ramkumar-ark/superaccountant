package com.superaccountant.ETL;

import com.superaccountant.ETL.model.VoucherInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class EtlController {

    private static final Logger log = LoggerFactory.getLogger(EtlController.class);

    private final VoucherRepository voucherRepository;
    private final XmlGenerationService xmlGenerationService;
    private final CompanyRepository companyRepository;
    private final VoucherInputMapper voucherInputMapper;
    private final TallyXmlDataRepository tallyXmlDataRepository;

    @Autowired
    public EtlController(VoucherRepository voucherRepository, XmlGenerationService xmlGenerationService,
            CompanyRepository companyRepository, VoucherInputMapper voucherInputMapper,
            TallyXmlDataRepository tallyXmlDataRepository) {
        this.voucherRepository = voucherRepository;
        this.xmlGenerationService = xmlGenerationService;
        this.companyRepository = companyRepository;
        this.voucherInputMapper = voucherInputMapper;
        this.tallyXmlDataRepository = tallyXmlDataRepository;
    }

    @MutationMapping
    public Voucher updateVoucher(@Argument("id") Long id, @Argument("voucher") VoucherInput voucherInput) {
        return voucherRepository.findById(id)
                .map(voucher -> {
                    voucherInputMapper.updateVoucherFromInput(voucherInput, voucher);
                    return voucherRepository.save(voucher);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));
    }

    @QueryMapping
    public String getVouchersAsXml(@Argument String companyName) {
        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with name: " + companyName));
        List<Voucher> vouchers = voucherRepository.findAllByCompany(company);
        return xmlGenerationService.generateXml(vouchers);
    }

    @QueryMapping
    public TallyXmlData getStagedXml(@Argument String companyName, @Argument String fileName) {
        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with name: " + companyName));
        return tallyXmlDataRepository.findTopByCompanyAndFileNameOrderByIdDesc(company, fileName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No staged XML found for file '" + fileName + "' in company '" + companyName + "'"));
    }
}
