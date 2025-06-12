package com.chargingstation.demo.CLI;

import com.chargingstation.demo.services.ChargingStationService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ChargingStationCLI {
    private final ChargingStationService service;

    public ChargingStationCLI(ChargingStationService service) {
        this.service = service;
    }

    public void run()   {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("\n------ Laddstationsmeny ------");
            System.out.println("1. Visa hushållets energiförbrukning");
            System.out.println("2. Visa elpriser");
            System.out.println("3. Starta laddning");
            System.out.println("4. Stoppa laddning");
            System.out.println("5. Visa batteristatus");
            System.out.println("6. Visa optimeringsschema");
            System.out.println("7. Utför automatisk laddoptimering");
            System.out.println("8. Ladda ur batteriet till 20%");
            System.out.println("0. Avsluta");
            System.out.print("Välj ett alternativ: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice) {
                case 1 -> System.out.println(service.getHouseholdEnergyConsumption().toPrettyString());
                case 2 -> System.out.println(service.getElectricityPrices().toPrettyString());
                case 3 -> System.out.println("Svar: " + service.startCharging());
                case 4 -> System.out.println("Svar: " + service.stopCharging());
                case 5 -> System.out.println("Batteri: " + service.getBatteryPercentage().asInt() + "%");
                case 6 -> System.out.println(service.generateChargingSchedule());
                case 7 -> System.out.println(service.sendChargingCommand(service.optimizeCharging()));
                case 8 -> System.out.println(service.dischargeBattery());
                case 0 -> {
                    System.out.println("Avslutar klienten.");
                    return;
                }
                default -> System.out.println("Ogiltigt val.");
            }

        }
    }
}
