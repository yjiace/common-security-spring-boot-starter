package cn.smallyoung.commonsecurityspringbootstarter.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author smallyoung
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    List<T> findAll(Map<String, Object> map);

    List<T> findAll(Map<String, Object> map, Sort sort);

    Page<T> findAll(Map<String, Object> map, Pageable pageable);

    long count(Map<String, Object> map);
}
