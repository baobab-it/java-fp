package functional.v22;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SuperIterable<E> implements Iterable<E> {

    private Iterable<E> self;

    public SuperIterable(Iterable<E> self) {
        this.self = self;
    }

    @Override
    public Iterator<E> iterator() {
        return self.iterator();
    }

    public <F> SuperIterable<F> flatMap(Function<E, SuperIterable<F>> op) {
        List<F> results = new ArrayList<>();

        self.forEach(e -> op.apply(e).forEach(f -> results.add(f)));

        return new SuperIterable<>(results);
    }

    public <F> SuperIterable<F> map(Function<E, F> op) {
        List<F> results = new ArrayList<>();
        self.forEach(e -> results.add(op.apply(e)));
        return new SuperIterable<>(results);
    }

    public SuperIterable<E> filter(Predicate<E> pred) {
        List<E> results = new ArrayList<>();
        self.forEach((E e) -> {
            if (pred.test(e)) {
                results.add(e);
            }
        });
        return new SuperIterable<>(results);
    }

    public static void main(String[] args) {
        SuperIterable<String> strings = new SuperIterable<>(
                Arrays.asList("LightCoral", "pink", "Orange", "Gold", "plum", "Blue", "limegreen")
        );
        strings.forEach(s -> System.out.println(">" + s));

        // модифікація значень відбуваєтья на копії обгортки масиву,
        // масив strings залишається незмінним
        SuperIterable<String> upperCase = strings.filter(s -> Character.isUpperCase(s.charAt(0)));
        upperCase.forEach(s -> System.out.println(">" + s));
        System.out.println("---------------------------------");

        strings
            .filter(x -> Character.isUpperCase(x.charAt(0)))
            .map(x-> x.toUpperCase()) // <-- виклик нашого методу `map()`
            .forEach(x -> System.out.println(x));
        System.out.println("---------------------------------");

        strings.forEach(s -> System.out.println(">" + s));
        System.out.println("---------------------------------");

        SuperIterable<Car> carIter = new SuperIterable<>(Arrays.asList(
                Car.withGasColorPassengers(6, "Red", "Fred", "Jim", "Sheila"),
                Car.withGasColorPassengers(3, "Octarine", "Rincewind", "Ridcully"),
                Car.withGasColorPassengers(9, "Black", "Weatherwax", "Magrat"),
                Car.withGasColorPassengers(7, "Green", "Valentine", "Gillian", "Anne", "Dr. Mahmoud"),
                Car.withGasColorPassengers(6, "Red", "Ender", "Hyrum", "Locke", "Bonzo")
        ));

        carIter
                .filter(c -> c.getGasLevel() > 6)
                .map(c-> c.getPassengers().get(0) + "is driving a " + c.getColor()
                    + "car with lots of fuel")
                .forEach(c -> System.out.println("> " + c));
        System.out.println("---------------------------------");

        carIter
                .map(c -> Car.withGasColorPassengers(
                    c.getGasLevel() + 4,
                    c.getColor(), 
                    c.getPassengers().toArray(new String[]{})))
                .forEach(c -> System.out.println("> " + c));

        System.out.println("---------------------------------");

        carIter
                .map(c -> c.addGass(4))
                .forEach(c -> System.out.println("> " + c));

        System.out.println("---------------------------------");

        carIter
                .filter(c -> c.getPassengers().size() > 3)
                .flatMap(c -> new SuperIterable<>(c.getPassengers()))
                .map(s -> s.toUpperCase())
                .forEach(c -> System.out.println("> " + c));

        System.out.println("---------------------------------");

        carIter
                .flatMap(c -> new SuperIterable<>(c.getPassengers())
                                    .map(p -> p + " is riding in a " + c.getColor() + " car"))
                .forEach(c -> System.out.println("> " + c));
    }
}