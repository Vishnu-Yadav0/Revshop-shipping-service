![Banner](https://raw.githubusercontent.com/Vishnu-Yadav0/Revshop-shipping-service/main/banner.png)

# 🚚 RevShop — Shipping Service

Dedicated service for managing the logistics flow: shipper profile management, route planning, and real-time tracking of shipments across the RevShop platform.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=flat-square&logo=docker)](https://www.docker.com/)

---

## What it does

- **Shipper Portal:** Authenticates and manages courier/delivery partner profiles.
- **Shipment Tracking:** Real-time status updates from `DISPATCHED` to `DELIVERED`.
- **Order Assignment:** Intelligent logic to associate shipments with specific orders.
- **Integration:** Feedback loop with the **Order Service** to update status in the buyer's history.

## Architecture

- Registers with **Eureka** for discovery.
- Consumes **User Service** for authentication.
- Provides data to **Order Service** via Feign.

| Tech | Implementation |
|---|---|
| Runtime | Spring Boot 3 |
| Data | MySQL (Hibernate/JPA) |
| Registry | Eureka |
| Container | Docker |

---

## Explore Repositories

- 🌐 [Frontend UI](https://github.com/Vishnu-Yadav0/Revshop-frontend)
- 📋 [Operations Center](https://github.com/Vishnu-Yadav0/Revshop-order-sales-service)
- ⚙️ [Infrastructure](https://github.com/Vishnu-Yadav0/Revshop-api-gateway)

