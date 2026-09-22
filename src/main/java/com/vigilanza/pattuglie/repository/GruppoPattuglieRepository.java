package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.GruppoPattuglie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GruppoPattuglieRepository extends JpaRepository<GruppoPattuglie, Long> {
    List<GruppoPattuglie> findAllByOrderByNomeAsc();
}
