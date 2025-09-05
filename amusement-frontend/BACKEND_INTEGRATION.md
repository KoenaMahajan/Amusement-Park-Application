# Backend Integration Guide

This guide explains how to test the connection between your Angular frontend and Spring Boot backend.

## Prerequisites

1. **Spring Boot Backend**: Make sure your Spring Boot application is running on `http://localhost:8080`
2. **Angular Frontend**: Make sure your Angular application is running

## Configuration Changes Made

### 1. Updated Registration Form
- Removed `name` and `phoneNumber` fields to match your backend API
- Backend expects: `{ email, password, role }`
- Frontend now sends exactly what your backend expects

### 2. Added Proxy Configuration
- Created `proxy.conf.json` to handle CORS issues
- Updated `angular.json` to use the proxy configuration
- Updated `UserService` to use relative URLs

### 3. Switched to Real Backend
- Changed `USE_MOCK_SERVICE` to `false` in `user.service.provider.ts`
- Now using your actual Spring Boot backend instead of mock data

### 4. Added Test Connection Component
- Created `/test-connection` route for testing backend endpoints
- Tests registration, OTP verification, and resend OTP endpoints

## Testing the Integration

### Step 1: Start Your Spring Boot Backend
```bash
# In your Spring Boot project directory
./mvnw spring-boot:run
# or
java -jar target/your-app.jar
```

### Step 2: Start Your Angular Frontend
```bash
# In your Angular project directory
ng serve
```

### Step 3: Test the Connection
1. Navigate to `http://localhost:4200`
2. Click on "🔌 Test Backend Connection" in the demo section
3. Or go directly to `http://localhost:4200/test-connection`

### Step 4: Test Each Endpoint
1. **Test Registration**: Click "Test Registration" button
   - Expected: Success message with "Registered successfully. OTP sent to console (valid for 5 min)."
   - Check your Spring Boot console for the generated OTP

2. **Test OTP Verification**: Click "Test OTP Verification" button
   - Expected: Error message (since we're using a dummy OTP)
   - This is normal - it tests the endpoint connectivity

3. **Test Resend OTP**: Click "Test Resend OTP" button
   - Expected: Success message with "OTP resent to your email (console for now)."
   - Check your Spring Boot console for the new OTP

## Expected Behavior

### Successful Registration
- User fills out registration form (email, password, role)
- Form submits to `/api/users/register`
- Backend creates user and sends OTP
- Frontend redirects to OTP verification page
- User enters OTP from console/email
- Account gets verified and user is redirected to login

### OTP Verification
- User enters 6-digit OTP
- Frontend calls `/api/users/verify-otp`
- Backend verifies OTP and marks user as verified
- User gets success message and redirects to login

### Resend OTP
- User clicks "Resend OTP" button
- Frontend calls `/api/users/resend-otp`
- Backend generates new OTP and sends it
- Countdown timer resets

## Troubleshooting

### CORS Issues
If you encounter CORS errors:
1. Make sure the proxy configuration is working
2. Check that `USE_MOCK_SERVICE` is set to `false`
3. Verify your Spring Boot backend is running on port 8080

### Connection Issues
If you can't connect to the backend:
1. Check if Spring Boot is running
2. Verify the port number (8080)
3. Check the browser console for error messages
4. Use the test connection page to debug

### Form Validation Issues
If registration fails:
1. Check that password meets requirements (8+ chars, uppercase, lowercase, number, special char)
2. Verify email format is valid
3. Check backend console for validation error messages

## API Endpoints

Your frontend now correctly calls these backend endpoints:

- `POST /api/users/register` - User registration
- `POST /api/users/verify-otp` - OTP verification
- `POST /api/users/resend-otp` - Resend OTP

## Next Steps

1. **Test the full registration flow** from registration → OTP verification → login
2. **Implement email service** in your backend to send real OTPs
3. **Add user profile management** features
4. **Implement authentication and authorization** with JWT tokens

## Notes

- Currently, OTPs are logged to the Spring Boot console for development
- The frontend automatically redirects users through the verification flow
- All error handling is in place for both frontend and backend errors
- The proxy configuration handles CORS automatically during development
