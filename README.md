# LeverX – Homework 3

A Spring Boot application for managing warehouse inventory with order processing, product reservations, and comprehensive AOP-based analytics.

## Features Overview

### ✅ Product Inventory Management
- Preloaded warehouse items
- Thread-safe storage using `ConcurrentHashMap`
- Product lookup by name
- Real-time quantity tracking and updates
- Reserved vs available quantity tracking

### ✅ Order Processing
- Create customer orders with item lists
- Validate product availability (excluding reserved items)
- Calculate total order cost
- Automatic stock deduction on successful processing
- Structured success/failure responses

### ✅ Reservation System
- Reserve products before ordering (locks inventory)
- Cancel reservations (releases inventory back)
- Convert reservations directly to orders
- Prevent others from purchasing reserved items
- Time-stamped reservation tracking

### ✅ AOP-Based Analytics
Implemented using Spring AOP for non-intrusive monitoring:

**Order Analytics:**
- Total orders processed
- Success vs failed orders count
- Success rate percentage
- Per-product order frequency
- Revenue accumulation
- Execution duration tracking

**Reservation Analytics:**
- **Reserved percentage per product** (key metric)
- Total/active/cancelled reservations
- Success rate tracking
- Product-wise reservation counts
- Real-time metrics logging

---

## 📁 Project Structure

```
src/main/java/com/leverx/warehouse/
├── controller/
│   ├── OrderController.java              # Order REST endpoints
│   └── ReservationController.java        # Reservation REST endpoints
├── service/
│   ├── WarehouseService.java             # Order business logic
│   ├── ReservationService.java           # Reservation business logic
│   ├── OrderAnalyticsAspect.java         # AOP for order analytics
│   └── ReservationAnalyticsAspect.java   # AOP for reservation analytics
├── model/
│   ├── Product.java                      # Product entity
│   ├── Order.java                        # Order entity
│   └── Reservation.java                  # Reservation entity
├── dto/
│   ├── request/
│   │   ├── OrderRequest.java             # Order creation request
│   │   └── ReservationRequest.java       # Reservation creation request
│   └── response/
│       ├── OrderResponse.java            # Order response
│       ├── ReservationResponse.java      # Reservation response
│       ├── InventoryResponse.java        # Inventory summary response
│       └── ReservationAnalyticsResponse.java # Analytics response
└── WarehouseApplication.java             # Main application
```

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run

```bash
  mvn clean install
  mvn spring-boot:run
```

The application will start on `http://localhost:8080`

---

## API Endpoints

### Order Management

#### ➤ Create Order
```http
POST /api/orders
Content-Type: application/json

{
  "customerId": "customer-123",
  "items": {
    "Laptop": 2,
    "Mouse": 5
  }
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Order processed successfully",
  "orderId": "ORD-1234567890",
  "totalCost": 2149.93,
  "items": {
    "Laptop": 2,
    "Mouse": 5
  }
}
```

**Failure Response:**
```json
{
  "success": false,
  "message": "Insufficient stock for product: Laptop. Available: 1, Requested: 2",
  "orderId": null,
  "totalCost": 0.0
}
```

---

### Reservation Management

#### ➤ Create Reservation
```http
POST /api/reservations
Content-Type: application/json

{
  "customerId": "customer-123",
  "items": {
    "Laptop": 2,
    "Mouse": 5
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation created successfully",
  "reservationId": "RSV-abc123def",
  "customerId": "customer-123",
  "items": {
    "Laptop": 2,
    "Mouse": 5
  },
  "timestamp": "2025-11-13T10:30:45"
}
```

#### ➤ Cancel Reservation
```http
DELETE /api/reservations/{reservationId}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation cancelled successfully"
}
```

#### ➤ Convert Reservation to Order
```http
POST /api/reservations/{reservationId}/convert
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation converted to order successfully",
  "orderId": "ORD-1234567890",
  "totalCost": 2149.93
}
```

---

### Inventory & Analytics

#### ➤ Get Inventory
```http
GET /api/orders
```

**Response:**
```json
{
  "totalProducts": 5,
  "totalQuantity": 325,
  "inventory": {
    "Laptop": {
      "name": "Laptop",
      "price": 999.99,
      "availableQuantity": 48
    },
    "Mouse": {
      "name": "Mouse",
      "price": 29.99,
      "availableQuantity": 95
    },
    "Keyboard": {
      "name": "Keyboard",
      "price": 79.99,
      "availableQuantity": 62
    },
    "Monitor": {
      "name": "Monitor",
      "price": 299.99,
      "availableQuantity": 35
    },
    "Headset": {
      "name": "Headset",
      "price": 89.99,
      "availableQuantity": 85
    }
  }
}
```

#### ➤ Get Reservation Analytics
```http
GET /api/reservations/analytics
```

**Response:**
```json
{
  "totalReservations": 45,
  "activeReservations": 12,
  "cancelledReservations": 3,
  "successRate": 93.33,
  "productReservations": {
    "Laptop": {
      "totalQuantity": 50,
      "reservedQuantity": 10,
      "availableQuantity": 40,
      "reservedPercentage": 20.00
    },
    "Mouse": {
      "totalQuantity": 100,
      "reservedQuantity": 15,
      "availableQuantity": 85,
      "reservedPercentage": 15.00
    }
  }
}
```

## Testing
You can test all endpoints using **cURL** (PowerShell-compatible commands).

---

## Create an Order

```powershell
curl -Method POST "http://localhost:8080/api/orders" `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body '{
            "customerId": "customer-123",
            "items": {
                "Laptop": 2,
                "Mouse": 5
            }
         }'
```

---

## Get All Orders

```powershell
curl "http://localhost:8080/api/orders"
```

---

## Create a Reservation

```powershell
curl -Method POST "http://localhost:8080/api/reservations" `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body '{
            "customerId": "test-1",
            "items": {
                "Laptop": 2
            }
        }'
```

---

## Cancel a Reservation

```powershell
curl -Method DELETE "http://localhost:8080/api/reservations/RSV-abc123def"
```

---

## Get Reservation Analytics

```powershell
curl "http://localhost:8080/api/reservations/analytics"
```

---

## Convert Reservation to Order

```powershell
curl -Method POST "http://localhost:8080/api/reservations/RSV-abc123def/convert"
```

## Key Implementation Details

### Thread Safety
- Uses `ConcurrentHashMap` for thread-safe inventory management
- Atomic operations for quantity updates
- Synchronized blocks for complex operations

### Reservation Impact
- Reservations lock inventory (reduce available quantity)
- Cancelled reservations restore inventory
- Reserved items cannot be ordered by others
- Analytics track reserved percentage for planning

### AOP Benefits
- Non-intrusive monitoring
- Automatic metric collection
- Zero performance impact on business logic
- Comprehensive logging without cluttering code

---

## Development Evolution

**PR #1:** Initial implementation with order processing and basic analytics  
**PR #2:** Added reservation functionality with advanced analytics including reserved percentage tracking

---
