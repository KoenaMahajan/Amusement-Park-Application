# Connection Troubleshooting Guide

## Issue: Unable to connect to backend server

### What was fixed:
1. **Updated all services to use relative URLs** (`/api` instead of `http://localhost:8080`)
2. **Proxy configuration is now properly utilized**
3. **CORS issues should be resolved**

### How to start the application:

1. **Start your backend server first** (should be running on `http://localhost:8080`)

2. **Start the Angular development server** using one of these methods:
   ```bash
   # Method 1: Using the batch script (Windows)
   start-dev.bat
   
   # Method 2: Using npm script
   npm start
   
   # Method 3: Direct ng serve with proxy
   ng serve --proxy-config proxy.conf.json
   ```

3. **Access the application** at `http://localhost:4200`

### Testing the connection:

1. Navigate to the test connection component in your app
2. Or manually test by visiting: `http://localhost:4200/api/membership-plans`

### Common issues and solutions:

#### Issue 1: "Unable to connect to server"
- **Solution**: Make sure your backend is running on port 8080
- **Check**: Visit `http://localhost:8080` directly in your browser

#### Issue 2: "API endpoint not found"
- **Solution**: Check if your backend endpoints match the frontend calls
- **Check**: Verify the API paths in your backend match `/api/*`

#### Issue 3: CORS errors
- **Solution**: The proxy configuration should handle this, but ensure your backend allows requests from `http://localhost:4200`

#### Issue 4: Still getting connection errors
- **Solution**: Check the browser's developer tools Network tab to see the actual HTTP requests and responses

### Proxy Configuration:
The `proxy.conf.json` file redirects all `/api/*` requests from the frontend to `http://localhost:8080/api/*` on the backend.

### Verification Steps:
1. Backend is running on port 8080 ✅
2. Frontend is running on port 4200 ✅
3. Proxy configuration is active ✅
4. All services use relative URLs ✅
5. Test connection component shows "Connected successfully!" ✅
