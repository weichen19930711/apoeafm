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
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.LostRecord;
import com.perficient.library.core.repository.LostRecordRepository;
import com.perficient.library.core.service.LostRecordService;
import com.perficient.library.export.ExportXLS;

@Service
public class LostRecordServiceImpl extends ExportXLS implements LostRecordService {

    public static final String LOSTRECORDLIST_TEMPLATE_PATH = "/xls-templates/lostRecordListTemplate.xls";

    public static final String LOSTRECORDLIST_REPORT_BASE_NAME = "LostRecordReport";

    public static final String LOSTRECORDLIST_CONTEXT_KEY = "lostRecords";

    @Autowired
    private LostRecordRepository lostRecordRepository;

    @Override
    public LostRecord save(LostRecord entity) {
        return lostRecordRepository.save(entity);
    }

    @Override
    public List<LostRecord> findAll() {
        return lostRecordRepository.findAll();
    }

    @Override
    public LostRecord findOne(Integer id) {
        return lostRecordRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        lostRecordRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return lostRecordRepository.exists(id);
    }

    @Override
    public Page<LostRecord> getLostRecords(Specification<LostRecord> spec, Pageable pageable) {
        return lostRecordRepository.findAll(spec, pageable);
    }

    @Override
    public List<LostRecord> findNotPaidLostRecordsByEmployee(Employee employee) {
        return lostRecordRepository.findByEmployeeAndIsPaid(employee, false);
    }

    @Override
    public void exportLostRecords(Object records) {
        Context context = new Context();
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        context.putVar(LOSTRECORDLIST_CONTEXT_KEY, records);
        try {
            export(response, LOSTRECORDLIST_REPORT_BASE_NAME, LOSTRECORDLIST_TEMPLATE_PATH, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
