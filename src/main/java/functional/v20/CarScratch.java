package functional.v20;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class CarScratch {

    public static <E> ToIntFunction<E> compareWithThis(E target, Comparator<E> comp) {
        return x -> comp.compare(target, x);
    }

    public static <E> Predicate<E> compareGreater(ToIntFunction<E> comp) {
        return x -> comp.applyAsInt(x) < 0;
    }

    public static <E> void showAll(List<E> lc) {
        for (E c : lc) {
            System.out.println(c);
        }
        System.out.println("---------------------------");
    }

    /**
     * Передаємо інтерфейс в метод для більш гнучкої реалізації вибірки в одному
     * методі (можна перевіряти колір, бензин та інші критерії, реалізувавши
     * інтерфейс Criterion
     */
    public static <E> List<E> getByCriterion(Iterable<E> in, Predicate<E> crit) {
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

        // Predicate Negate
        Predicate<Car> level7 = Car.getGasLevelCarCriterion(7);
        showAll(getByCriterion(cars, level7));
        Predicate<Car> notLevel7 = level7.negate();
        showAll(getByCriterion(cars, notLevel7));

        // Predicate AND
        Predicate<Car> isRed = Car.getColorCriterion("Red");
        Predicate<Car> fourPassengers = Car.getFourPassengerCriterion();

        Predicate<Car> redFourPassengers = isRed.and(fourPassengers);
        showAll(getByCriterion(cars, redFourPassengers));

        // Predicate OR
        Predicate<Car> isBlack = Car.getColorCriterion("Black");
        Predicate<Car> blackOrFourPassengers = isBlack.or(fourPassengers);
        showAll(getByCriterion(cars, blackOrFourPassengers));

        Car bert = Car.withGasColorPassengers(5, "Blue");

        ToIntFunction<Car> compareWithBert = compareWithThis(bert, Car.getGasComparator());
        cars.forEach(car -> System.out.println("comparing " + car + " with bert gives "
                + compareWithBert.applyAsInt(car)));

        showAll(getByCriterion(cars, compareGreater(compareWithBert)));
    }

}

class PassengerCountOrder implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.getPassengers().size() - o2.getPassengers().size();
    }
}
