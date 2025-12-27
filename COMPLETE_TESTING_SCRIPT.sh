#!/bin/bash

# Complete Microservices Testing Script
# This script tests User Service, Post Service, and Opinion Service

echo "=================================="
echo "Microservices Testing Script"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variables
USER_SERVICE="http://localhost:8081"
POST_SERVICE="http://localhost:8082"
OPINION_SERVICE="http://localhost:8083"
GATEWAY="http://localhost:8080"

TOKEN=""
USER_ID=""
POST_ID=""

# Function to print success
success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print info
info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

echo "=================================="
echo "1. TESTING USER SERVICE"
echo "=================================="
echo ""

# Test 1: Signup
info "Test 1.1: User Signup"
SIGNUP_RESPONSE=$(curl -s -X POST "$USER_SERVICE/api/v1/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "phoneNumber": "9876543210",
    "password": "Test123!",
    "confirmPassword": "Test123!",
    "role": "PUBLIC",
    "city": "Mumbai",
    "state": "Maharashtra",
    "address": "123 Test Street"
  }')

echo "$SIGNUP_RESPONSE" | jq '.'

if echo "$SIGNUP_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "User signup successful"
    TOKEN=$(echo "$SIGNUP_RESPONSE" | jq -r '.token')
    USER_ID=$(echo "$SIGNUP_RESPONSE" | jq -r '.user.id')
    echo "Token: $TOKEN"
    echo "User ID: $USER_ID"
else
    error "User signup failed"
fi

echo ""
sleep 2

# Test 2: Login
info "Test 1.2: User Login"
LOGIN_RESPONSE=$(curl -s -X POST "$USER_SERVICE/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "9876543210",
    "password": "Test123!"
  }')

echo "$LOGIN_RESPONSE" | jq '.'

if echo "$LOGIN_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "User login successful"
    TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
else
    error "User login failed"
fi

echo ""
sleep 2

echo "=================================="
echo "2. TESTING POST SERVICE"
echo "=================================="
echo ""

# Test 3: Create Post
info "Test 2.1: Create Post"
CREATE_POST_RESPONSE=$(curl -s -X POST "$POST_SERVICE/api/v1/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"title\": \"Test Road Repair\",
    \"description\": \"Testing post creation - road needs repair\",
    \"dept\": \"PUBLIC_WORKS\",
    \"location\": \"Test Location, Mumbai\",
    \"userId\": \"$USER_ID\"
  }")

echo "$CREATE_POST_RESPONSE" | jq '.'

if echo "$CREATE_POST_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Post creation successful"
    POST_ID=$(echo "$CREATE_POST_RESPONSE" | jq -r '.data.id')
    echo "Post ID: $POST_ID"
else
    error "Post creation failed"
fi

echo ""
sleep 2

# Test 4: Get All Unresolved Posts
info "Test 2.2: Get Unresolved Posts"
UNRESOLVED_RESPONSE=$(curl -s -X GET "$POST_SERVICE/api/v1/posts/unresolved" \
  -H "Authorization: Bearer $TOKEN")

echo "$UNRESOLVED_RESPONSE" | jq '.'

if echo "$UNRESOLVED_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Get unresolved posts successful"
    POST_COUNT=$(echo "$UNRESOLVED_RESPONSE" | jq '.data | length')
    echo "Number of unresolved posts: $POST_COUNT"
else
    error "Get unresolved posts failed"
fi

echo ""
sleep 2

# Test 5: Get Specific Post
info "Test 2.3: Get Post by ID"
GET_POST_RESPONSE=$(curl -s -X GET "$POST_SERVICE/api/v1/posts/$POST_ID" \
  -H "Authorization: Bearer $TOKEN")

echo "$GET_POST_RESPONSE" | jq '.'

if echo "$GET_POST_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Get post by ID successful"
else
    error "Get post by ID failed"
fi

echo ""
sleep 2

# Test 6: Upvote Post
info "Test 2.4: Upvote Post"
UPVOTE_RESPONSE=$(curl -s -X POST "$POST_SERVICE/api/v1/posts/$POST_ID/upvote" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"userId\": \"$USER_ID\"
  }")

echo "$UPVOTE_RESPONSE" | jq '.'

if echo "$UPVOTE_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Post upvote successful"
else
    error "Post upvote failed"
fi

echo ""
sleep 2

echo "=================================="
echo "3. TESTING OPINION SERVICE"
echo "=================================="
echo ""

# Test 7: Create Opinion
info "Test 3.1: Create Opinion"
CREATE_OPINION_RESPONSE=$(curl -s -X POST "$OPINION_SERVICE/api/v1/opinions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"opinion\": \"This is a test opinion. The road really needs urgent repair!\",
    \"postId\": \"$POST_ID\",
    \"userId\": \"$USER_ID\"
  }")

echo "$CREATE_OPINION_RESPONSE" | jq '.'

if echo "$CREATE_OPINION_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Opinion creation successful"
    OPINION_ID=$(echo "$CREATE_OPINION_RESPONSE" | jq -r '.data.id')
    echo "Opinion ID: $OPINION_ID"
else
    error "Opinion creation failed"
fi

echo ""
sleep 2

# Test 8: Get All Opinions
info "Test 3.2: Get All Opinions"
GET_OPINIONS_RESPONSE=$(curl -s -X GET "$OPINION_SERVICE/api/v1/opinions" \
  -H "Authorization: Bearer $TOKEN")

echo "$GET_OPINIONS_RESPONSE" | jq '.'

if echo "$GET_OPINIONS_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Get all opinions successful"
    OPINION_COUNT=$(echo "$GET_OPINIONS_RESPONSE" | jq '.data | length')
    echo "Number of opinions: $OPINION_COUNT"
else
    error "Get all opinions failed"
fi

echo ""
sleep 2

# Test 9: Get Opinions by Post ID
info "Test 3.3: Get Opinions for Post"
GET_POST_OPINIONS_RESPONSE=$(curl -s -X GET "$OPINION_SERVICE/api/v1/opinions/post/$POST_ID" \
  -H "Authorization: Bearer $TOKEN")

echo "$GET_POST_OPINIONS_RESPONSE" | jq '.'

if echo "$GET_POST_OPINIONS_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Get opinions for post successful"
else
    error "Get opinions for post failed"
fi

echo ""
sleep 2

echo "=================================="
echo "4. TESTING API GATEWAY"
echo "=================================="
echo ""

# Test 10: Create Post via Gateway
info "Test 4.1: Create Post via API Gateway"
GATEWAY_POST_RESPONSE=$(curl -s -X POST "$GATEWAY/api/v1/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"title\": \"Gateway Test Post\",
    \"description\": \"Testing via API Gateway\",
    \"dept\": \"EDUCATION\",
    \"location\": \"Gateway Test Location\",
    \"userId\": \"$USER_ID\"
  }")

echo "$GATEWAY_POST_RESPONSE" | jq '.'

if echo "$GATEWAY_POST_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Post creation via gateway successful"
else
    error "Post creation via gateway failed"
fi

echo ""
sleep 2

# Test 11: Get Unresolved via Gateway
info "Test 4.2: Get Unresolved Posts via Gateway"
GATEWAY_UNRESOLVED_RESPONSE=$(curl -s -X GET "$GATEWAY/api/v1/posts/unresolved" \
  -H "Authorization: Bearer $TOKEN")

echo "$GATEWAY_UNRESOLVED_RESPONSE" | jq '.'

if echo "$GATEWAY_UNRESOLVED_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    success "Get unresolved via gateway successful"
else
    error "Get unresolved via gateway failed"
fi

echo ""
sleep 2

echo "=================================="
echo "5. TESTING WEBSOCKET (Manual)"
echo "=================================="
echo ""

info "WebSocket testing requires a WebSocket client"
echo "To test WebSocket manually:"
echo ""
echo "1. Open a WebSocket client (like wscat or browser console)"
echo "2. Connect to: ws://localhost:8083/ws"
echo "3. Send a message:"
echo '   {"type":"upvote","postId":"'$POST_ID'","userId":"'$USER_ID'"}'
echo ""
echo "Expected: Real-time notification about upvote"

echo ""
echo "=================================="
echo "TESTING SUMMARY"
echo "=================================="
echo ""

echo "Services Tested:"
success "User Service (Signup, Login)"
success "Post Service (Create, Get, Upvote)"
success "Opinion Service (Create, Get)"
success "API Gateway (Routing)"
info "WebSocket (Manual testing required)"

echo ""
echo "Saved IDs for manual testing:"
echo "TOKEN: $TOKEN"
echo "USER_ID: $USER_ID"
echo "POST_ID: $POST_ID"

echo ""
echo "=================================="
echo "NEXT STEPS"
echo "=================================="
echo ""
echo "1. Check RabbitMQ Management UI: http://localhost:15672"
echo "2. Verify messages in queues"
echo "3. Test WebSocket with a client"
echo "4. Check MongoDB for data persistence"
echo ""
echo "Testing complete!"
