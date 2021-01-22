package functional.v22;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NullChecks {
    
    public static void main(String[] args) {
        HashMap<String, Car> owners = new HashMap<>();
        owners.put("Sheila", Car.withGasColorPassengers(
            6, "Red", "Fred", "Jim", "Sheila"));
        owners.put("Librarian", Car.withGasColorPassengers(
            3, "Octarin", "Rincewind", "Ridcully"));
        owners.put("Ogg", Car.withGasColorPassengersAndTrunk(
            9, "Black", "Weatherwax", "Magrat"));

        String owner = "Ogg"; // Weatherwax
        Car c = owners.get(owner);
        if(c != null) {
            List<String> trunkItems = c.getTrunkContents();
            if (trunkItems != null) {
                System.out.println(owner + " has " + trunkItems + " in the car");
            }
        }
        System.out.println("---------------------------------");

        Optional<HashMap<String, Car>> ownerOpt = Optional.of(owners);
        ownerOpt
                .map(m -> m.get(owner))
                .map(x -> x.getTrunkContents())
                .map(x -> owner + " has " + x + "in the car")
                .ifPresent(m -> System.out.println(m));
    }
}