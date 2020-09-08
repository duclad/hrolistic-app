package ro.optimizit.hrolistic.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.optimizit.hrolistic.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
