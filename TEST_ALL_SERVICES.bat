@echo off
REM Complete Microservices Testing Script for Windows
REM This script tests User Service, Post Service, and Opinion Service

echo ==================================
echo Microservices Testing Script
echo ==================================
echo.

set USER_SERVICE=http://localhost:8081
set POST_SERVICE=http://localhost:8082
set OPINION_SERVICE=http://localhost:8083
set GATEWAY=http://localhost:8080

set TOKEN=
set USER_ID=
set POST_ID=

echo ==================================
echo 1. TESTING USER SERVICE
echo ==================================
echo.

echo Test 1.1: User Signup
curl -X POST %USER_SERVICE%/api/v1/auth/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"phoneNumber\":\"9876543210\",\"password\":\"Test123!\",\"confirmPassword\":\"Test123!\",\"role\":\"PUBLIC\",\"city\":\"Mumbai\",\"state\":\"Maharashtra\",\"address\":\"123 Test Street\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 1.2: User Login
curl -X POST %USER_SERVICE%/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"phoneNumber\":\"9876543210\",\"password\":\"Test123!\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo ==================================
echo 2. TESTING POST SERVICE
echo ==================================
echo.

echo NOTE: Replace YOUR_TOKEN and YOUR_USER_ID with actual values from login response
echo.

set /p TOKEN="Enter JWT Token from login: "
set /p USER_ID="Enter User ID from login: "

echo.
echo Test 2.1: Create Post
curl -X POST %POST_SERVICE%/api/v1/posts ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"title\":\"Test Road Repair\",\"description\":\"Testing post creation\",\"dept\":\"PUBLIC_WORKS\",\"location\":\"Mumbai\",\"userId\":\"%USER_ID%\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

set /p POST_ID="Enter Post ID from response: "

echo.
echo Test 2.2: Get Unresolved Posts
curl -X GET %POST_SERVICE%/api/v1/posts/unresolved ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 2.3: Get Post by ID
curl -X GET %POST_SERVICE%/api/v1/posts/%POST_ID% ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 2.4: Upvote Post
curl -X POST %POST_SERVICE%/api/v1/posts/%POST_ID%/upvote ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"userId\":\"%USER_ID%\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo ==================================
echo 3. TESTING OPINION SERVICE
echo ==================================
echo.

echo Test 3.1: Create Opinion
curl -X POST %OPINION_SERVICE%/api/v1/opinions ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"opinion\":\"Test opinion - road needs repair!\",\"postId\":\"%POST_ID%\",\"userId\":\"%USER_ID%\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 3.2: Get All Opinions
curl -X GET %OPINION_SERVICE%/api/v1/opinions ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 3.3: Get Opinions for Post
curl -X GET %OPINION_SERVICE%/api/v1/opinions/post/%POST_ID% ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo ==================================
echo 4. TESTING API GATEWAY
echo ==================================
echo.

echo Test 4.1: Create Post via Gateway
curl -X POST %GATEWAY%/api/v1/posts ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"title\":\"Gateway Test\",\"description\":\"Via gateway\",\"dept\":\"EDUCATION\",\"location\":\"Gateway Location\",\"userId\":\"%USER_ID%\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 4.2: Get Unresolved via Gateway
curl -X GET %GATEWAY%/api/v1/posts/unresolved ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.

echo ==================================
echo TESTING COMPLETE
echo ==================================
echo.
echo Check the responses above for success/error messages
echo.
echo For WebSocket testing:
echo 1. Install wscat: npm install -g wscat
echo 2. Connect: wscat -c ws://localhost:8083/ws
echo 3. Send: {"type":"upvote","postId":"%POST_ID%","userId":"%USER_ID%"}
echo.
echo For RabbitMQ:
echo Open: http://localhost:15672
echo Default credentials: guest/guest
echo.
pause
