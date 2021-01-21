package functional.v20;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
    }
}