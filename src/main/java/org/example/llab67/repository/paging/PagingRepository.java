package org.example.llab67.repository.paging;

import org.example.llab67.domain.Entity;
import org.example.llab67.repository.Repository;
import org.example.llab67.utils.paging.Page;
import org.example.llab67.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}