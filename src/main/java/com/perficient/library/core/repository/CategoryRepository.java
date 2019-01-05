package com.perficient.library.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByName(String categoryName);

    @Query(value = "SELECT COUNT(*) FROM book WHERE (property_id in(SELECT bp.id FROM book_property bp WHERE find_in_set(?1,bp.category))) AND status NOT IN('lost','damaged')", nativeQuery = true)
    Integer findBookAmountByCategoryId(Integer categoryId);
    
    @Query(value = "SELECT COUNT(*) FROM book WHERE (property_id in(SELECT bp.id FROM book_property bp WHERE isnull(bp.category))) AND status NOT IN('lost','damaged')", nativeQuery = true)
    Integer findNoCategoryBookAmount();

    @Query(value = "SELECT SUM(b.borrowNum) FROM (SELECT COUNT(bp.id) borrowNum, bp.id id FROM book_property bp, book b, (select * from borrow_record br where date_format(?1,'%Y-%m-%d') <= date_format(br.checkout_date,'%Y-%m-%d') and date_format(br.checkout_date,'%Y-%m-%d') <= date_format(?2,'%Y-%m-%d')) br WHERE bp.id = b.property_id AND b.id = br.book_id GROUP BY bp.id) b WHERE b.id in(SELECT bp.id from book_property bp WHERE find_in_set(?3,bp.category))", nativeQuery = true)
    Integer findBorrowedReocrdAmountByCategoryId(Date beforeDate, Date afterDate, Integer categoryId);

}
