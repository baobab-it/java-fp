package functional.v03;

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
     * Передаємо інтерфейс в метод для більш гнучкої реалізації вибірки в одному 
     * методі (можна перевіряти колір, бензин та інші критерії, реалізувавши інтерфейс CarCriterion
     */
    public static List<Car> getCarsByCriterion(Iterable<Car> in, CarCriterion crit) {
        List<Car> output = new ArrayList<>();
        for (Car c : in) {
            if (crit.test(c)) {
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

        showAll(getCarsByCriterion(cars, new Car.RedCarCriterion()));
        showAll(getCarsByCriterion(cars, new Car.GasLevelCarCriterion(6)));

        // Використання статичного поля і шаблону фабрики
        showAll(getCarsByCriterion(cars, Car.RED_CAR_CRITERION));
        showAll(getCarsByCriterion(cars, Car.getRedCarCriterion()));
        cars.sort(new PassengerCountOrder());
        showAll(cars); //<- загальний список елементів залишається незмінним
    }

}

class PassengerCountOrder implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.getPassengers().size() - o2.getPassengers().size();
    }
}

interface CarCriterion {

    boolean test(Car c);
}