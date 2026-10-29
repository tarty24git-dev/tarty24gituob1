package com.cth.datamatch.dashboard.controller;

import com.cth.datamatch.dashboard.service.DashboardMetricsService;
import com.cth.datamatch.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardMetricsService dashboardMetricsService;
    private final UserService userService;

    @Value("${app.name:DataMatch Studio}")
    private String appName;

    public DashboardController(DashboardMetricsService dashboardMetricsService, UserService userService) {
        this.dashboardMetricsService = dashboardMetricsService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Authentication authentication) {
        model.addAttribute("appName", appName);
        model.addAttribute("metrics", dashboardMetricsService.getDashboardMetrics());
        model.addAttribute("recentLogs", userService.getRecentAuditLogs());
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "");
        return "dashboard";
    }

    @GetMapping("/api/dashboard/metrics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(dashboardMetricsService.getDashboardMetrics());
    }
}
