package functional.v09;

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
        //cars.sort(new PassengerCountOrder());
        showAll(cars); //<- загальний список елементів залишається незмінним
        cars.sort(Car.getGasComparator());
        showAll(cars);
        showAll(getByCriterion(cars, c -> c.getPassengers().size() == 2));
        showAll(getByCriterion(cars, Car.getFourPassengerCriterion()));
        // Перевіримо нову машину Car чи вона має червоний колір
        boolean b = ((Criterion<Car>) (c -> c.getColor().equals("Red"))).test(Car.withGasColorPassengers(0, "Red"));
        System.out.println(b);
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