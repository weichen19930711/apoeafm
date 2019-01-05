package com.perficient.library.core.service.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.perficient.library.common.utils.ServletUtils;
import com.perficient.library.core.model.DamagedRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.repository.DamagedRecordRepository;
import com.perficient.library.core.service.DamagedRecordService;
import com.perficient.library.export.ExportXLS;

@Service
public class DamagedRecordServiceImpl extends ExportXLS implements DamagedRecordService {

    public static final String DAMAGEDRECORDLIST_TEMPLATE_PATH = "/xls-templates/damagedRecordListTemplate.xls";

    public static final String DAMAGEDRECORDLIST_REPORT_BASE_NAME = "DamagedRecordReport";

    public static final String DAMAGEDRECORDLIST_CONTEXT_KEY = "damagedRecords";

    @Autowired
    private DamagedRecordRepository damagedRecordRepository;

    @Override
    public DamagedRecord save(DamagedRecord entity) {
        return damagedRecordRepository.save(entity);
    }

    @Override
    public List<DamagedRecord> findAll() {
        return damagedRecordRepository.findAll();
    }

    @Override
    public DamagedRecord findOne(Integer id) {
        return damagedRecordRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        damagedRecordRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return damagedRecordRepository.exists(id);
    }

    @Override
    public void exportDamagedRecords(Object records) {
        Context context = new Context();
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        context.putVar(DAMAGEDRECORDLIST_CONTEXT_KEY, records);
        try {
            export(response, DAMAGEDRECORDLIST_REPORT_BASE_NAME, DAMAGEDRECORDLIST_TEMPLATE_PATH, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Page<DamagedRecord> getDamagedRecords(Specification<DamagedRecord> spec, Pageable pageable) {
        return damagedRecordRepository.findAll(spec, pageable);
    }

    @Override
    public List<DamagedRecord> findNotPaidDamagedRecordsByEmployee(Employee employee) {
        return damagedRecordRepository.findByEmployeeAndIsPaid(employee, false);
    }

}
