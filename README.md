# Car Management & Fuel Tracking System

A Spring Boot application with REST API, manual Servlet implementation, and CLI client for managing cars and tracking fuel consumption.

## 📋 Project Requirements

This project implements:
- **Backend REST API** with Spring Boot and in-memory storage
- **Manual Servlet** implementation demonstrating HTTP request lifecycle
- **CLI Application** for interacting with the API via HTTP

## 🛠️ Technologies Used

- Java 17
- Spring Boot 3.2.0
- Gradle
- Jackson (JSON processing)
- Gson (CLI JSON parsing)
- Java HttpClient
- Jakarta Servlet API

## 📁 Project Structure

```
src/main/java/com/example/carmanagement/
├── CarManagementBackendApplication.java  (Main Spring Boot app)
├── model/
│   ├── Car.java                          (Car entity)
│   ├── FuelEntry.java                    (Fuel entry entity)
│   └── FuelStats.java                    (Statistics DTO)
├── service/
│   └── CarService.java                   (Business logic with in-memory storage)
├── controller/
│   └── CarController.java                (REST API endpoints)
├── servlet/
│   └── FuelStatsServlet.java             (Manual servlet implementation)
└── cli/
    └── CliApplication.java               (Standalone CLI application)
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (included via wrapper)

### Running the Backend Server

1. **Clone the repository**
   ```bash
   git clone https://github.com/mwisemarierose/Car_management.git
   cd car-management-backend
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the backend**
   ```bash
   ./gradlew bootRun
   ```

   Or run `CarManagementBackendApplication.java` in IntelliJ IDEA.

4. **Verify it's running**

   Server starts on: `http://localhost:8080`

   Test: `curl http://localhost:8080/api/cars` (should return `[]`)

### Running the CLI Application

**Important:** The backend must be running before using the CLI!

1. **Build the CLI JAR**
   ```bash
   ./gradlew cliJar
   ```

2. **Run CLI commands**

   **Create a car:**
   ```bash
   java -jar build/libs/car-management-cli-0.0.1-SNAPSHOT.jar create-car --brand Toyota --model Corolla --year 2018
   ```

   **Add fuel entry:**
   ```bash
   java -jar build/libs/car-management-cli-0.0.1-SNAPSHOT.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
   ```

   **View statistics:**
   ```bash
   java -jar build/libs/car-management-cli-0.0.1-SNAPSHOT.jar fuel-stats --carId 1
   ```

## 📡 API Endpoints

### REST API

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/cars` | Create a new car | `{"brand":"Toyota","model":"Corolla","year":2018}` |
| GET | `/api/cars` | List all cars | - |
| POST | `/api/cars/{id}/fuel` | Add fuel entry | `{"liters":40,"price":52.5,"odometer":45000}` |
| GET | `/api/cars/{id}/fuel/stats` | Get fuel statistics | - |

### Servlet

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/servlet/fuel-stats?carId={id}` | Get fuel statistics (manual servlet) |

## 🧪 Testing

### Test with cURL

```bash
# Create a car
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}'

# List all cars
curl http://localhost:8080/api/cars

# Add fuel entry
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":40,"price":52.5,"odometer":45000}'

# Add another fuel entry (for average calculation)
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":35,"price":46.0,"odometer":45500}'

# Get statistics via REST API
curl http://localhost:8080/api/cars/1/fuel/stats

# Get statistics via Servlet (should return same result)
curl "http://localhost:8080/servlet/fuel-stats?carId=1"

# Test error handling - car not found
curl "http://localhost:8080/servlet/fuel-stats?carId=999"
```

### Expected Output

**Statistics Response:**
```json
{
  "totalFuel": 75.0,
  "totalCost": 98.5,
  "averageConsumption": 15.0
}
```

**CLI Output:**
```
═══════════════════════════════
   Fuel Statistics - Car #1
═══════════════════════════════
Total fuel: 75.0 L
Total cost: 98.50
Average consumption: 15.0 L/100km
═══════════════════════════════
```

## 🎯 Key Implementation Details

### In-Memory Storage
- Uses `ConcurrentHashMap<Long, Car>` for thread-safe storage
- `AtomicLong` for generating unique IDs
- Data persists only during application runtime

### Servlet Implementation
- Manually extends `HttpServlet`
- Overrides `doGet()` method
- Manually parses query parameters with `request.getParameter()`
- Explicitly sets `Content-Type: application/json`
- Explicitly sets HTTP status codes (200, 404, 400)
- Shares the same `CarService` instance as REST API

### CLI Application
- Separate executable with its own `main()` method
- Uses `java.net.http.HttpClient` for HTTP communication
- Communicates with backend purely via HTTP (no direct access to services)
- Command-line argument parsing
- JSON serialization with Gson

### Average Consumption Calculation
- Formula: `(totalFuel / distance) × 100`
- Distance = Last odometer reading - First odometer reading
- Requires at least 2 fuel entries
- Returns 0 if insufficient data

## 🔧 CLI Commands Reference

```bash
# Create a car
create-car --brand <brand> --model <model> --year <year>

# Add fuel entry
add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>

# View fuel statistics
fuel-stats --carId <id>
```

## ⚠️ Error Handling

- **404 Not Found**: Returned when car ID doesn't exist
- **400 Bad Request**: Returned when required parameters are missing or invalid
- **201 Created**: Returned when car is successfully created
- **200 OK**: Returned for successful operations


## 👨‍💻 Development

### Running in IntelliJ IDEA

1. **Backend**: Run `CarManagementApplication.java`
2. **CLI**: Run `CliApplication.java` with program arguments:
    - Right-click → Modify Run Configuration
    - Add program arguments: `create-car --brand Toyota --model Corolla --year 2018`

## 🤝 Author
Marie Rose MWISENEZA 
