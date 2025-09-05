package com.amusement.amusement_park.service.feedback;

import com.amusement.amusement_park.Enums.LostFoundStatus;
import com.amusement.amusement_park.dto.feedback.LostAndFoundResponse;
import com.amusement.amusement_park.entity.feedback.LostAndFound;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.exception.ResourceNotFoundException;
import com.amusement.amusement_park.repository.feedback.LostAndFoundRepository;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostAndFoundService {

    private final LostAndFoundRepository lostAndFoundRepository;
    private final UserRepository userRepository;

    public LostAndFoundService(LostAndFoundRepository lostAndFoundRepository, UserRepository userRepository) {
        this.lostAndFoundRepository = lostAndFoundRepository;
        this.userRepository = userRepository;
    }

    public LostAndFoundResponse reportItem(String userEmail, LostAndFound request) {
        if (!StringUtils.hasText(userEmail)) {
            throw new IllegalArgumentException("User email cannot be null or empty.");
        }

        if (request == null) {
            throw new IllegalArgumentException("Lost and found request cannot be null.");
        }
        validateLostAndFoundRequest(request);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        LostAndFound entry = LostAndFound.builder()
                .itemName(request.getItemName())
                .description(request.getDescription())
                .status(request.getStatus() == null ? LostFoundStatus.LOST : request.getStatus())
                .location(request.getLocation())
                .reportedAt(LocalDateTime.now())
                .reportedBy(user)
                .build();

        return toDto(lostAndFoundRepository.save(entry));
    }

    private void validateLostAndFoundRequest(LostAndFound request) {
        if (!StringUtils.hasText(request.getItemName())) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }
        if (!StringUtils.hasText(request.getLocation())) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
    }

    public List<LostAndFoundResponse> getUserReports(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        List<LostAndFound> reports = lostAndFoundRepository.findByReportedByEmail(email);
        if (reports.isEmpty()) {
            throw new ResourceNotFoundException("No reports found for user: " + email);
        }
        return reports.stream().map(this::toDto).collect(Collectors.toList());
    }

    // Get all FOUND items
    public List<LostAndFoundResponse> getAllFoundItems() {
        List<LostAndFound> foundItems = lostAndFoundRepository.findByStatus(LostFoundStatus.FOUND);
        if (foundItems.isEmpty()) {
            throw new ResourceNotFoundException("No found items available.");
        }
        return foundItems.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteByUser(Long id) {
        LostAndFound entry = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));

        lostAndFoundRepository.delete(entry);
    }

    public List<LostAndFoundResponse> getAllEntries() {
        return lostAndFoundRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteEntry(Long id) {
        if (!lostAndFoundRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entry not found: " + id);
        }
        lostAndFoundRepository.deleteById(id);
    }

    public LostAndFoundResponse updateStatus(Long id, LostFoundStatus status) {
        LostAndFound entry = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));
        entry.setStatus(status);
        return toDto(lostAndFoundRepository.save(entry));
    }

    public List<LostAndFoundResponse> getAllEntriesByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null.");
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<LostAndFound> entries = lostAndFoundRepository.findAllByReportedAtBetween(start, end);
        if (entries.isEmpty()) {
            throw new ResourceNotFoundException("No lost and found entries found for date: " + date);
        }

        return entries.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LostAndFoundResponse toDto(LostAndFound entry) {
        return LostAndFoundResponse.builder()
                .id(entry.getId())
                .itemName(entry.getItemName())
                .description(entry.getDescription())
                .location(entry.getLocation())
                .status(entry.getStatus())
                .reportedBy(entry.getReportedBy().getName())
                .reportedAt(entry.getReportedAt())
                .build();
    }

}
