package com.chargingstation.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chargingstation.demo.services.ChargingStationService;

@RestController
@RequestMapping("/charging-station")
public class ChargingStationController {
    private final ChargingStationService chargingStationService;

    public ChargingStationController(ChargingStationService chargingStationService) {
        this.chargingStationService = chargingStationService;
    }

    @GetMapping("/prices")
    public String getElectricityPrices() {
        return chargingStationService.getElectricityPrices().toPrettyString();
    }

    @GetMapping("/household-consumption")
    public String getHouseholdEnergyConsumption() {
        return chargingStationService.getHouseholdEnergyConsumption().toPrettyString();
    }

    @PostMapping("/start-charging")
    public String startCharging() {
        return chargingStationService.startCharging();
    }

    @PostMapping("/stop-charging")
    public String stopCharging() {
        return chargingStationService.stopCharging();
    }

    @GetMapping("/battery-percentage")
    public String getBatteryPercentage() {
        return chargingStationService.getBatteryPercentage().toPrettyString();
    }

    @GetMapping("/auto-charge")
    public String manageCharging() {
        return chargingStationService.manageCharging();
    }

    @GetMapping("/optimize-charge")
    public String optimizeCharging() {
        return chargingStationService.optimizeCharging();
    }

    @GetMapping("/schedule")
    public String getChargingSchedule() {
        return chargingStationService.generateChargingSchedule();
    }


}
