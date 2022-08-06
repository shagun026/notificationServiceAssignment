package com.example.notificationservice.repositories;

import com.example.notificationservice.entities.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<Blacklist, String> {

}