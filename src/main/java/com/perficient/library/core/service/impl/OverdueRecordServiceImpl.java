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
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.model.vo.OverdueRecordVo;
import com.perficient.library.core.repository.OverdueRecordRepository;
import com.perficient.library.core.service.OverdueRecordService;
import com.perficient.library.export.ExportXLS;

@Service
public class OverdueRecordServiceImpl extends ExportXLS implements OverdueRecordService {

    public static final String OVERDUERECORDLIST_TEMPLATE_PATH = "/xls-templates/overdueRecordListTemplate.xls";

    public static final String OVERDUERECORDLIST_REPORT_BASE_NAME = "OverdueRecordReport";

    public static final String OVERDUERECORDLIST_CONTEXT_KEY = "overdueRecords";

    @Autowired
    private OverdueRecordRepository overdueRecordRepository;

    @Override
    public OverdueRecord save(OverdueRecord entity) {
        return overdueRecordRepository.save(entity);
    }

    @Override
    public List<OverdueRecord> findAll() {
        return overdueRecordRepository.findAll();
    }

    @Override
    public OverdueRecord findOne(Integer id) {
        return overdueRecordRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        overdueRecordRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return overdueRecordRepository.exists(id);
    }

    @Override
    public OverdueRecord findByBorrowRecord(BorrowRecord borrowRecord) {
        return overdueRecordRepository.findByBorrowRecord(borrowRecord);
    }

    @Override
    public Page<OverdueRecord> findByEmployee(Employee employee, Pageable pageable) {
        return overdueRecordRepository.findByBorrowRecordEmployee(employee, pageable);
    }

    @Override
    public Page<OverdueRecord> findAllOverdueRecords(Pageable pageable) {
        return overdueRecordRepository.findAll(pageable);
    }

    @Override
    public Page<OverdueRecord> findNotCheckedInOverdueRecords(Pageable pageable) {
        return overdueRecordRepository.findByReturned(false, pageable);
    }

    @Override
    public List<OverdueRecord> findNotCheckedInOverdueRecordsByEmployee(Employee employee) {
        return overdueRecordRepository.findByBorrowRecordEmployeeAndReturned(employee, false);
    }

    @Override
    public Page<OverdueRecord> findNotCheckedInOverdueRecordsByEmployee(Employee employee, Pageable pageable) {
        return overdueRecordRepository.findByBorrowRecordEmployeeAndReturned(employee, false, pageable);
    }

    @Override
    public Page<OverdueRecord> findOverdueRecordsBySearchValue(Specification<OverdueRecord> spec, Pageable pageable) {
        return overdueRecordRepository.findAll(spec, pageable);
    }

    @Override
    public void exportOverdueRecord(List<OverdueRecordVo> records) {
        Context context = new Context();
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        context.putVar(OVERDUERECORDLIST_CONTEXT_KEY, records);
        try {
            export(response, OVERDUERECORDLIST_REPORT_BASE_NAME, OVERDUERECORDLIST_TEMPLATE_PATH, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
