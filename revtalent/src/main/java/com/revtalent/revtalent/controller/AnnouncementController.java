package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.Announcement;
import com.revtalent.revtalent.repository.AnnouncementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository repository;

    // GET ALL
    @GetMapping
    public List<Announcement> getAll() {
        return repository.findAll();
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Announcement announcement) {
        try {
            System.out.println("Received: " + announcement.getTitle());
            Announcement saved = repository.save(announcement);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}