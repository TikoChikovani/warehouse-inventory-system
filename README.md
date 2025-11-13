# LeverX – Homework 3

## PR #1 — Warehouse Management System (without reservation functionality)

This pull request introduces the **initial implementation** of the Warehouse Management System.  
It includes product inventory handling, order creation, and analytic tracking using AOP.  
Reservation logic is intentionally **excluded** in this PR, as required.

---

## Features Included in This PR

### ✅ 1. Product Inventory Management
- Preloaded warehouse items
- Thread-safe storage (`ConcurrentHashMap`)
- Product lookup by name
- Quantity tracking and updates

---

### ✅ 2. Order Processing
- Creates customer orders with item lists
- Validates product availability
- Calculates total order cost
- Deducts stock on successful processing
- Returns structured success/failure response

---

### ✅ 3. AOP-Based Analytics (Custom Aspect)
Implemented using Spring AOP:
- Total orders processed
- Successful vs failed orders
- Per-product order counts
- Revenue accumulation
- Timestamp logging
- Execution duration for each order

---

## REST API Endpoints

### ➤ **Create Order**
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

**Example Response (Success):**
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

**Example Response (Failure):**
```json
{
  "success": false,
  "message": "Insufficient stock for product: Laptop. Available: 1, Requested: 2",
  "orderId": null,
  "totalCost": 0.0
}
```

---

### ➤ **Get Inventory**
```http
GET /api/orders
```

**Example Response:**
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

---

## 📊 Analytics Automatically Logged by AOP

The custom aspect (`OrderAnalyticsAspect`) logs the following metrics after each order:

- **Total number of orders** processed
- **Success/failure count**
- **Success rate** (%)
- **Aggregated revenue**
- **Product order frequency** (per-product statistics)
- **Processing time** per request (in milliseconds)

All analytics are printed to the application logs automatically via AOP.

---

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run
```bash
  mvn clean install
  mvn spring-boot:run
```

## 📁 Project Structure

```
src/main/java/com/leverx/warehouse/
├── controller/
│   └── OrderController.java          # REST endpoints
├── service/
│   ├── WarehouseService.java         # Business logic
│   └── OrderAnalyticsAspect.java     # AOP analytics
├── model/
│   ├── Product.java                  # Product entity
│   ├── Order.java                    # Order entity
│   └── OrderResponse.java            # Response DTO
└── WarehouseApplication.java         # Main application
```





🧪 Testing the Application
Using cURL
Create an order:
bashcurl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{
"customerId": "customer-123",
"items": {
"Laptop": 2,
"Mouse": 5
}
}'
Get inventory:
bashcurl http://localhost:8080/api/orders
