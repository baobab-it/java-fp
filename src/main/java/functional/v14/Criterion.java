package functional.v14;

@FunctionalInterface
public interface Criterion<E> {

    boolean test(E c);

    static <E> Criterion<E> negate(Criterion<E> crit) {
        return x -> !crit.test(x);
    }

    static <E> Criterion<E> and(Criterion<E> first, Criterion<E> second) {
        return x -> first.test(x) && second.test(x);
    }

    static <E> Criterion<E> or(Criterion<E> first, Criterion<E> second) {
        return x -> first.test(x) || second.test(x);
    }
}
