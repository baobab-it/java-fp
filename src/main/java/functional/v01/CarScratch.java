package functional.v01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CarScratch {

    public static void showAll(List<Car> lc) {
        for (Car c : lc) {
            System.out.println(c);
        }
        System.out.println("---------------------------");
    }
    
    /**
     * Функцію потрібно копіювати, наприклад, щоб отримати синій (Blue) колір
     * автомобіля, що є недоцільним.
     *
     * Також можна передатавати паметр в метод, що є більш доцільним дивись
     * метод getColoredCars()
     */
    public static List<Car> getRedCars(List<Car> in) {
        List<Car> output = new ArrayList<>();
        for (Car c : in) {
            if (c.getColor().equals("Red")) {
                output.add(c);
            }
        }
        return output;
    }

    /**
     * Передаємо параметр в метод, та змінюємо в параметрі List на Iterable, для
     * більш широкої підтримки типів колекцій
     */
    public static List<Car> getColoredCars(Iterable<Car> in, String color) {
        List<Car> output = new ArrayList<>();
        for (Car c : in) {
            if (c.getColor().equals(color)) {
                output.add(c);
            }
        }
        return output;
    }

    /**
     * Це погано постійно копіювати код для зміни інших праметрів пошуку,
     * наприклад, пошук машини, по кількості використання бензину
     */
    public static List<Car> getCarsByGasLevel(Iterable<Car> in, int gasLevel) {
        List<Car> output = new ArrayList<>();
        for (Car c : in) {
            if (c.getGasLevel() >= gasLevel) { // <- різниця з попереднім - один вираз
                output.add(c);
            }
        }
        return output;
    }

    public static void main(String[] args) {
        List<Car> cars = Arrays.asList(
                Car.withGasColorPassengers(6, "Red", "Fred", "Jim", "Sheila"),
                Car.withGasColorPassengers(3, "Octarine", "Rincewind", "Ridcully"),
                Car.withGasColorPassengers(9, "Black", "Weatherwax", "Magrat"),
                Car.withGasColorPassengers(7, "Green", "Valentine", "Gillian", "Anne", "Dr. Mahmoud"),
                Car.withGasColorPassengers(6, "Red", "Ender", "Hyrum", "Locke", "Bonzo"));
        showAll(cars);
        
        showAll(getRedCars(cars));
        
        showAll(getColoredCars(cars, "Black"));
        
        cars.sort(new PassengerCountOrder());
        showAll(cars); //<- загальний список елементів залишається незмінним
    }

    /**
     */
    static class PassengerCountOrder implements Comparator<Car> {

        @Override
        public int compare(Car o1, Car o2) {
            return o1.getPassengers().size() - o2.getPassengers().size();
        }
    }
}
