package com.perficient.library.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perficient.library.core.model.BookProperty;

@Repository
public interface BookPropertyRepository
    extends JpaRepository<BookProperty, Integer>, JpaSpecificationExecutor<BookProperty> {

    BookProperty findByIsbn(String isbn);

    @Query(value = "SELECT bp_.* FROM (SELECT COUNT(bp.id) borrowNum, bp.id id FROM book_property bp, book b, borrow_record br WHERE bp.id = b.property_id AND b.id = br.book_id GROUP BY bp.id ORDER BY borrowNum DESC) AS a, book_property AS bp_ WHERE bp_.id = a.id LIMIT 0,?1", nativeQuery = true)
    List<BookProperty> findPopularBookProperty(Integer size);

    @Query(value = "SELECT bp_.* FROM (SELECT * FROM (SELECT COUNT(bp.id) borrowNum, bp.id id, MAX(br.checkout_date) checkout_date FROM book_property bp, book b, (select * from borrow_record br where date_format(?1,'%Y-%m-%d') <= date_format(br.checkout_date,'%Y-%m-%d') and date_format(br.checkout_date,'%Y-%m-%d') <= date_format(?2,'%Y-%m-%d')) br WHERE bp.id = b.property_id AND b.id = br.book_id GROUP BY bp.id ORDER BY borrowNum DESC, checkout_date DESC) t) AS a, book_property AS bp_ WHERE bp_.id = a.id Limit 0,?3", nativeQuery = true)
    List<BookProperty> findPopularBookProperty(Date beforeDate, Date afterDate, Integer size);

    @Query(value = "SELECT COUNT(bp.id) borrowNum FROM book_property bp, book b, (select * from borrow_record br where date_format(?1,'%Y-%m-%d') <= date_format(br.checkout_date,'%Y-%m-%d') and date_format(br.checkout_date,'%Y-%m-%d') <= date_format(?2,'%Y-%m-%d')) br WHERE bp.id = b.property_id AND b.id = br.book_id AND bp.id = ?3", nativeQuery = true)
    Integer findBorrowedAmountByPropertyId(Date beforeDate, Date afterDate, Integer propertyId);

    @Query(value = "select * from book_property property where find_in_set(?1,property.category) and (property.isbn like %?2% or property.title like %?2% or property.author like %?2% or property.price like %?2%) \n#pageable\n", countQuery = "select count(*) from book_property property where find_in_set(?1,property.category) and (property.isbn like %?2% or property.title like %?2% or property.author like %?2% or property.price like %?2%)", nativeQuery = true)
    Page<BookProperty> findPropertyByCategoryId(Integer categoryId, String searchValue, Pageable pageable);

    @Query(value = "select * from book_property property where find_in_set(?1,property.category)", nativeQuery = true)
    List<BookProperty> findPropertyByCategoryId(Integer categoryId);
}
