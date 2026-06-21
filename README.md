# Tool Rental System

Practical assignment — Verification, Validation and Software Testing  
IFSP São Carlos — Prof. Dr. Lucas Oliveira

## Description

A tool rental module that applies progressive pricing: the longer the rental period, the lower the daily cost, with distinct price tiers for daily, weekly, and monthly rentals.

## Business Rules

- A previously registered customer selects an available tool.
- The customer can request a cost estimate by specifying the tool and the desired rental period.
- When registering a rental, the customer chooses a guarantee type: promissory note, credit card hold, or cash deposit.
- The rental is recorded with the current date as the start date.
- The return date is recorded only when the rental is finalized, at which point the total amount due is calculated.
- A rental can be cancelled without recording a return date.
- A tool can be sent for maintenance (unavailable during the process).
- When the tool returns from maintenance, the return is logged and the tool becomes available again.

## How to Run

### With Docker

**Prerequisites:** [Docker Desktop](https://www.docker.com/products/docker-desktop/)

1. Clone the repository:
```bash
git clone https://github.com/brenonlps/VVTS-LocacaoDeFerramentas.git
cd VVTS-LocacaoDeFerramentas
```

2. Start the containers:
```bash
docker-compose up --build
```
> The first run may take a few minutes while downloading dependencies.

3. Open in your browser:
- **Frontend:** http://localhost:3000
- **API:** http://localhost:8080

To stop: `Ctrl+C` or `docker-compose down`

To restart without rebuilding:
```bash
docker compose up
```

> The database is created automatically on the first run and persists across restarts.

---

### Without Docker

**Prerequisites:** Java 21+, Maven, Node.js 18+

**Back-end:**
1. Open the project in IntelliJ.
2. Run the main class `DemoAuthAppApplication`.
3. The API will be available at `http://localhost:8080`.

**Front-end:**
1. Navigate to the frontend folder:
```bash
cd frontend
```
2. Install dependencies:
```bash
npm install
```
3. Create the `.env` file from the example:
```bash
cp .env.example .env
```
Or create the `.env` file manually with:
```
VITE_API_URL=http://localhost:8080/api/v1
```
4. Run:
```bash
npm run dev
```
The application will be available at `http://localhost:5173`.

## Technologies

| Technology | Description |
|------------|-------------|
| Java 21 | Primary back-end language |
| Spring Boot | REST API framework |
| SQLite | Database |
| React + Vite | Front-end |
| Nginx | Front-end server in production |
| Docker | Containerization and local execution |

## Testing Practices

| Practice | Description |
|----------|-------------|
| DDD | Domain modeling with aggregates, entities, and value objects |
| BDD | Scenario specification using ubiquitous language |
| TDD | Test-driven implementation |
| Functional Testing | Validation based on functional criteria |
| Structural & Mutation Testing | Test coverage and quality measurement |
| Integration & System Testing | End-to-end validation with UI focus |

## Team

| Member | GitHub |
|--------|--------|
| Breno Nascimento Lopes | [@brenonlps](https://github.com/brenonlps) |

## Former Contributors

| Member | GitHub |
|--------|--------|
| Lucas Jundi Hikazudani | [@hikazudani](https://github.com/hikazudani) |
| Maria Clara Passareli Alves | [@passareliscoding](https://github.com/passareliscoding) |
