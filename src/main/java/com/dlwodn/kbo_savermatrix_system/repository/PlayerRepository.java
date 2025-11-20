package com.dlwodn.kbo_savermatrix_system.repository;

import com.dlwodn.kbo_savermatrix_system.domain.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    // "포지션이 투수인 애들만 페이지네이션해서 찾아줘"
    Page<Player> findByPosition(String position, Pageable pageable);

    // "이름에 '이형빈'이 들어가는 애들 찾아줘"
    Page<Player> findByNameContaining(String name, Pageable pageable);
}