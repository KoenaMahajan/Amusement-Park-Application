package com.amusement.amusement_park.controller.feedback;

import com.amusement.amusement_park.Enums.LostFoundStatus;
import com.amusement.amusement_park.dto.feedback.LostAndFoundResponse;
import com.amusement.amusement_park.entity.feedback.LostAndFound;
import com.amusement.amusement_park.service.feedback.LostAndFoundService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/lost-and-found")
public class LostAndFoundController {

    private final LostAndFoundService lostAndFoundService;

    public LostAndFoundController(LostAndFoundService lostAndFoundService) {
        this.lostAndFoundService = lostAndFoundService;
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LostAndFoundResponse> reportLostItem(
            Authentication authentication,
            @Valid @RequestBody LostAndFound request) {
        String email = authentication.getName();
        return ResponseEntity.ok(lostAndFoundService.reportItem(email, request));
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LostAndFoundResponse>> getUserReports(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(lostAndFoundService.getUserReports(email));
    }

    @GetMapping("/found")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LostAndFoundResponse>> getFoundItems() {
        return ResponseEntity.ok(lostAndFoundService.getAllFoundItems());
    }

    @DeleteMapping("/user/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteMyReport(@PathVariable Long id) {

        lostAndFoundService.deleteByUser(id);
        return ResponseEntity.ok("Your report has been deleted.");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LostAndFoundResponse>> getAllEntries() {
        return ResponseEntity.ok(lostAndFoundService.getAllEntries());
    }

    @DeleteMapping("admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEntry(@PathVariable Long id) {
        lostAndFoundService.deleteEntry(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LostAndFoundResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") LostFoundStatus status) {
        return ResponseEntity.ok(lostAndFoundService.updateStatus(id, status));
    }

    @GetMapping("/admin/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LostAndFoundResponse>> getEntriesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lostAndFoundService.getAllEntriesByDate(date));
    }

}
