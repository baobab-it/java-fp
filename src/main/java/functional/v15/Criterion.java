package functional.v15;

@FunctionalInterface
public interface Criterion<E> {

    boolean test(E c);

    default Criterion<E> negate() {
        return x -> !this.test(x);
    }

    default Criterion<E> and(Criterion<E> second) {
        return x -> this.test(x) && second.test(x);
    }

    default Criterion<E> or(Criterion<E> second) {
        return x -> this.test(x) || second.test(x);
    }
}
