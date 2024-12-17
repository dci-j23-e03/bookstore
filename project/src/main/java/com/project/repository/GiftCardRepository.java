package com.project.repository;

import com.project.entity.GiftCardEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftCardRepository extends JpaRepository<GiftCardEntity, Long> {
  Optional<GiftCardEntity> findByCardCode(String cardCode);

}
