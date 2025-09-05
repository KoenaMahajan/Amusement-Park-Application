package com.amusement.amusement_park.service.user;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpStore {
    private record OtpEntry(String otp, LocalDateTime expiry) {
    }

    private final ConcurrentHashMap<String, OtpEntry> otpMap = new ConcurrentHashMap<>();
    private static final int EXPIRY_MINUTES = 5;

    public void storeOtp(String email, String otp) {
        otpMap.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(EXPIRY_MINUTES)));
    }

    public String getOtp(String email) {
        OtpEntry entry = otpMap.get(email);
        if (entry != null && LocalDateTime.now().isBefore(entry.expiry)) {
            return entry.otp;
        }
        otpMap.remove(email);
        return null;
    }

    public void clearOtp(String email) {
        otpMap.remove(email);
    }
}
