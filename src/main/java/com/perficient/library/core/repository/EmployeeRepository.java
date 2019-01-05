package com.perficient.library.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.enums.Role;
import com.perficient.library.core.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByScreenNameContaining(String screenName);

    Employee findByEmid(String emid);

    Page<Employee> findByRole(Role role, Pageable pageable);

    Employee findByScreenName(String screenName);

    @Query(value = "select emp.* from employee emp,(select br.employee_id employee_id, count(*) borrowedNum from borrow_record br where date_format(?1,'%Y-%m-%d') <= date_format(br.checkout_date,'%Y-%m-%d') and date_format(br.checkout_date,'%Y-%m-%d') <= date_format(?2,'%Y-%m-%d') group by br.employee_id order by borrowedNum desc) b where emp.id = b.employee_id limit 0,?3", nativeQuery = true)
    List<Employee> findEmployeeBorrowedRecordRank(Date startDate, Date endDate, Integer size);

    @Query(value = "select count(*) from borrow_record br where br.employee_id = ?1", nativeQuery = true)
    Integer findEmployeeBorrowedRecordsByEmployeeId(Integer employeeId);

    @Query(value = "select emp.* from employee emp,(select br.employee_id employee_id, count(*) overdueNum from borrow_record br where date_format(?1,'%Y-%m-%d') <= date_format(br.checkout_date,'%Y-%m-%d') and date_format(br.checkout_date,'%Y-%m-%d') <= date_format(?2,'%Y-%m-%d') and br.id in (select record.borrow_record_id from overdue_record record) group by br.employee_id order by overdueNum desc) b where emp.id = b.employee_id limit 0,?3", nativeQuery = true)
    List<Employee> findEmployeeOverdueRecordRank(Date startDate, Date endDate, Integer size);

    @Query(value = "select count(*) from borrow_record br where br.id in (select record.borrow_record_id from overdue_record record) and br.employee_id = ?1", nativeQuery = true)
    Integer findEmployeeOverdueRecordsByEmployeeId(Integer employeeId);
    
}
