package com.seusite.backend.repository;

import com.seusite.backend.model.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiteRepository extends JpaRepository<SiteModel, Long> {
    List<SiteModel> findByUsuarioEmail(String email);
    SiteModel findBySlug(String slug);
}


