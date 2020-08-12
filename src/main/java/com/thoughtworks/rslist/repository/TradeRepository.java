package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.TradeDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TradeRepository extends PagingAndSortingRepository<TradeDto, Integer> {

    @Override
    List<TradeDto> findAll();

    Optional<TradeDto> findByRank(Integer rank);
}
