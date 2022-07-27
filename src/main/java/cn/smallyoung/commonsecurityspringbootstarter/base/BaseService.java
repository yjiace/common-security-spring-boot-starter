package cn.smallyoung.commonsecurityspringbootstarter.base;

import cn.smallyoung.commonsecurityspringbootstarter.base.specification.SimpleSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author smallyoung
 */

public abstract class BaseService<T, ID extends Serializable> {

    @Autowired
    public BaseRepository<T, ID> baseRepository;

    public Optional<T> findById(ID id) {
        return baseRepository.findById(id);
    }

    public T findOne(ID id) {
        Optional<T> optional = baseRepository.findById(id);
        return optional.orElse(null);
    }

    public List<T> findAll() {
        return baseRepository.findAll();
    }

    public List<T> findAll(Map<String, Object> map) {
        return baseRepository.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }

    public List<T> findAllById(Iterable<ID> var1) {
        return baseRepository.findAllById(var1);
    }

    public List<T> findAll(Map<String, Object> map, Sort sort) {
        return baseRepository.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification(), sort);
    }

    public List<T> findAll(Sort sort) {
        return baseRepository.findAll(sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    public Page<T> findAll(Map<String, Object> map, Pageable pageable) {
        return baseRepository.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification(), pageable);
    }

    public List<T> findAll(Iterable<ID> iterable) {
        return this.baseRepository.findAllById(iterable);
    }

    public T save(T t) {
        return baseRepository.save(t);
    }

    public <S extends T> List<S> save(Iterable<S> s) {
        return baseRepository.saveAll(s);
    }

    public boolean existsById(ID id) {
        return baseRepository.existsById(id);
    }

    public boolean exists(Map<String, Object> map){
        return baseRepository.exists(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }

    public long count(Map<String, Object> map) {
        return baseRepository.count(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }
}
