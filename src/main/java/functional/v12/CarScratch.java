package functional.v12;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CarScratch {

    public static <E> void showAll(List<E> lc) {
        for (E c : lc) {
            System.out.println(c);
        }
        System.out.println("---------------------------");
    }

    /**
     * Передаємо інтерфейс в метод для більш гнучкої реалізації вибірки в одному
     * методі (можна перевіряти колір, бензин та інші критерії, реалізувавши інтерфейс Criterion
     */
    public static <E> List<E> getByCriterion(Iterable<E> in, Criterion<E> crit) {
        List<E> output = new ArrayList<>();
        for (E c : in) {
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

        // Використання шаблону Одинака
        showAll(getByCriterion(cars, Car.getRedCarCriterion()));
        showAll(getByCriterion(cars, Car.getGasLevelCarCriterion(6)));
        // Використання узагальнень
        List<String> colors = Arrays.asList("LightCoral", "pink", "Orange", "Gold", "plum", "Blue", "limeGreen");
        showAll(getByCriterion(colors, str -> str.length() > 4)); // LightCoral, Orange, limeGreen
        showAll(getByCriterion(colors, str -> Character.isUpperCase(str.charAt(0)))); // LightCoral, Orange, Gold, Blue

        LocalDate today = LocalDate.now();
        List<LocalDate> dates = Arrays.asList(today, today.plusDays(1), today.plusDays(7), today.minusDays(1));

        showAll(getByCriterion(dates, ld -> ld.isAfter(today)));
        showAll(getByCriterion(cars, Car.getColorCriterion("Red", "Black")));
    }

}

class PassengerCountOrder implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.getPassengers().size() - o2.getPassengers().size();
    }
}

@FunctionalInterface
interface Criterion<E> {

    boolean test(E c);
}
