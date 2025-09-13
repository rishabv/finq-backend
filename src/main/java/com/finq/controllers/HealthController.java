package com.finq.controllers;

import com.finq.enums.Status;
import com.finq.repositories.UserRepository;
import com.finq.repositories.AdminUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "System health and monitoring endpoints")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class HealthController implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @GetMapping("/status")
    @Operation(summary = "Basic health check", description = "Returns basic application health status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy"),
            @ApiResponse(responseCode = "503", description = "Service is unhealthy")
    })
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now());
            health.put("service", "FinQ Authentication Service");
            health.put("version", "1.0.0");

            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.error("Health check failed", e);
            Map<String, Object> health = new HashMap<>();
            health.put("status", "DOWN");
            health.put("timestamp", LocalDateTime.now());
            health.put("error", e.getMessage());

            return ResponseEntity.status(503).body(health);
        }
    }

    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check", description = "Returns detailed system health including database connectivity")
    public ResponseEntity<?> detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        Map<String, Object> components = new HashMap<>();

        try {
            // Database health check
            Map<String, Object> dbHealth = checkDatabaseHealth();
            components.put("database", dbHealth);

            // Application health
            Map<String, Object> appHealth = new HashMap<>();
            appHealth.put("status", "UP");
            appHealth.put("timestamp", LocalDateTime.now());
            components.put("application", appHealth);

            // Repository health
            Map<String, Object> repoHealth = checkRepositoryHealth();
            components.put("repositories", repoHealth);

            // Overall status
            boolean allHealthy = components.values().stream()
                    .allMatch(component -> "UP".equals(((Map<?, ?>) component).get("status")));

            health.put("status", allHealthy ? "UP" : "DOWN");
            health.put("components", components);
            health.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(health);

        } catch (Exception e) {
            logger.error("Detailed health check failed", e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(503).body(health);
        }
    }

    @GetMapping("/readiness")
    @Operation(summary = "Readiness probe", description = "Kubernetes readiness probe endpoint")
    public ResponseEntity<?> readinessProbe() {
        try {
            // Check if essential services are ready
            checkDatabaseConnection();

            Map<String, Object> readiness = new HashMap<>();
            readiness.put("status", "READY");
            readiness.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(readiness);

        } catch (Exception e) {
            logger.error("Readiness probe failed", e);
            Map<String, Object> readiness = new HashMap<>();
            readiness.put("status", "NOT_READY");
            readiness.put("error", e.getMessage());
            readiness.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(503).body(readiness);
        }
    }

    @GetMapping("/liveness")
    @Operation(summary = "Liveness probe", description = "Kubernetes liveness probe endpoint")
    public ResponseEntity<?> livenessProbe() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(liveness);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Basic metrics", description = "Returns basic application metrics")
    public ResponseEntity<?> getMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();

            // User metrics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countByStatus(Status.ACTIVE);
            long totalAdmins = adminUserRepository.count();

            Map<String, Object> userMetrics = new HashMap<>();
            userMetrics.put("totalUsers", totalUsers);
            userMetrics.put("activeUsers", activeUsers);
            userMetrics.put("totalAdmins", totalAdmins);

            metrics.put("users", userMetrics);

            // System metrics
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> systemMetrics = new HashMap<>();
            systemMetrics.put("totalMemory", runtime.totalMemory());
            systemMetrics.put("freeMemory", runtime.freeMemory());
            systemMetrics.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
            systemMetrics.put("maxMemory", runtime.maxMemory());
            systemMetrics.put("availableProcessors", runtime.availableProcessors());

            metrics.put("system", systemMetrics);
            metrics.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(metrics);

        } catch (Exception e) {
            logger.error("Failed to fetch metrics", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch metrics", "timestamp", LocalDateTime.now()));
        }
    }

    // Implementation of HealthIndicator interface
    @Override
    public Health health() {
        try {
            checkDatabaseConnection();
            return Health.up()
                    .withDetail("database", "UP")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "DOWN")
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
    }

    // Helper methods
    private void checkDatabaseConnection() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(5)) {
                throw new Exception("Database connection is not valid");
            }
        }
    }

    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        try {
            checkDatabaseConnection();
            dbHealth.put("status", "UP");
            dbHealth.put("database", "PostgreSQL");
        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        return dbHealth;
    }

    private Map<String, Object> checkRepositoryHealth() {
        Map<String, Object> repoHealth = new HashMap<>();
        try {
            // Test basic repository operations
            userRepository.count();
            adminUserRepository.count();

            repoHealth.put("status", "UP");
            repoHealth.put("userRepository", "UP");
            repoHealth.put("adminUserRepository", "UP");
        } catch (Exception e) {
            repoHealth.put("status", "DOWN");
            repoHealth.put("error", e.getMessage());
        }
        return repoHealth;
    }
}