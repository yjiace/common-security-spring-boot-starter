package cn.smallyoung.commonsecurityspringbootstarter.base;

import cn.smallyoung.commonsecurityspringbootstarter.base.specification.SimpleSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author smallyoung
 * @date 2022/4/14
 */
@Transactional(readOnly = true)
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }


    @Override
    public List<T> findAll(Map<String, Object> map) {
        return super.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }

    @Override
    public List<T> findAll(Map<String, Object> map, Sort sort) {
        return super.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification(), sort);
    }

    @Override
    public Page<T> findAll(Map<String, Object> map, Pageable pageable) {
        return super.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification(), pageable);
    }

    @Override
    public long count(Map<String, Object> map) {
        return super.count(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }
}
