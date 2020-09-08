package ro.optimizit.hrolistic.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.optimizit.hrolistic.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}
