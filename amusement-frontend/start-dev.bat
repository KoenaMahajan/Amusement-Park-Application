@echo off
echo Starting Angular Development Server with Proxy Configuration...
echo.
echo Make sure your backend is running on http://localhost:8080
echo.
ng serve --proxy-config proxy.conf.json
pause
