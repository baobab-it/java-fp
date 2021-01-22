# Functional Programming For Java (livelessons - Simon Roberts)

## 1. З ООП дизайну шаблонів до функціонального програмування

### 1.1 Проблема

Припустимо, що нам потрібно вивести список машин:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v01/v01/CarScratch.java#10-15
public static void showAll(List<Car> lc) {
    for (Car c : lc) {
        System.out.println(c);
    }
    System.out.println("---------------------------");
}
```

Припустимо, що нам потрібні тільки червоні машини:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v01/CarScratch.java#17-32
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
```

Проте, замість копіювання методів можна передавати параметр кольору автомобіля:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v01/CarScratch.java#34-46
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
```

В складних додатках потріно знаходити машини не тільки по кольору, а й по багатьом критеріям пошуку, для цього потрібно використовувати функціональне програмування, яке полегшує компактно виразити це в коді.

### 1.2 Зануремось глибше в проблему з кодуванням

Ось додамо пошук машини по рівню використання пального, що відрізняється від попереднього методу одним виразом:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v01/CarScratch.java#48-60
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
```

Схема роботи в ООП, в якому немає визначення `ПОВЕДІНКИ` аргументів, що дозволяє вибрати колір чи бензин для перевірки.

```java
getBy____(Iterable<Car> in, xxxxx) {
    List<Car> output = ___________;
    for (Car c : in) {
        if (<ТАК, НАМ ЦЕ ПІДХОДИТЬ>) { // <- РІЗНИЦЯ
            output.add(c);
        }
    }
    return output;
}
```

Тобто нам потрібно передати об’єкт як аргумент, який ми передаємо поведінку, що містить об’єкт, а не стан як в чисто ООП.

```java
getBy____(Iterable<Car> in, <якВибираємо>) {
    List<Car> output = ___________;
    for (Car c : in) {
        if (<якВибираємо>.test(c)) {
            output.add(c);
        }
    }
    return output;
}
```

### 1.3 Розглянемо сортування

Ось приклад сортування по класу `PassengerOrder`:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v01/CarScratch.java#81-87
class PassengerCountOrder implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.getPassengers().size() - o2.getPassengers().size();
    }
}
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v01/CarScratch.java#75-76
cars.sort(new PassengerCountOrder());
showAll(cars); //<- загальний список елементів залишається незмінним
```

Основне, що сортування впливає на створенний об’кт, розширенням програмного інтерфейсу `Comparator`, який використовує метод `compare()`, що впливає на поведінку об’єкту, при вирішенні порівняння об’єктів. Тобто вирішення сортування вирішується тільки під час читання методу сортування колекції.

### 1.4 Загальний механізм вибору

Для цього нам потрібно створити інтерфейс і реалізувати його:

```java
interface CarCriterion {
    boolean test(Car c);
}

class RedCarCriterion implements CarCriterion{
    public boolean test(Car c) {
        return c.getColor().equals("Red");
    }
}
```

### 1.5 Покращення вибірки

Створимо інтерфейс та реалізуємо його:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v02/CarScratch.java#57-85
interface CarCriterion {

    boolean test(Car c);
}

/**
 * Реалізація інтерфейсу вибірки
 */
class RedCarCriterion implements CarCriterion {

    @Override
    public boolean test(Car c) {
        return c.getColor().equals("Red");
    }
}

class GasLevelCarCriterion implements CarCriterion {

    private int threshold;

    public GasLevelCarCriterion(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean test(Car c) {
        return c.getGasLevel() >= threshold;
    }
}
```

Замість кількох методів (`getColoredCars()`, `getCarsByGasLevel()`) своримо один метод, що буде обробляти різні варіанти вибірки:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v02/CarScratch.java#17-29
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
```

Застосовуємо реалізації `RedCarCriterion` і `GasLevelCriterion`:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v02/CarScratch.java#40-41
showAll(getCarsByCriterion(cars, new RedCarCriterion()));
showAll(getCarsByCriterion(cars, new GasLevelCarCriterion(6)));
```

## 2. Будуємо більш функціональний концепт

### 2.1 Питання про власність

Можна організувати внутрішні класи з критеріями з класом моделі, що дозволяє мати безпосередній (превелигійований) доступ до полів класу та тримати все в одному місці. Також треба зазначити, що всі внутрішні класи були зроблені статичними, щоб бути пов'язаними чи згрупованими з класом `Car` (концепція внутрішніх класів, як одного цілого з `Car`).

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v03/Car.java#7-64,73-86
public class Car {

    private final int gasLevel;
    private final String color;
    private final List<String> passengers;
    private final List<String> trunkContents;

    private Car(int gasLevel, String color, List<String> passengers, List<String> trunkContents) {
        this.gasLevel = gasLevel;
        this.color = color;
        this.passengers = passengers;
        this.trunkContents = trunkContents;
    }

    public static Car withGasColorPassengers(int gas, String color, String... passengers) {
        List<String> p = Collections.unmodifiableList(Arrays.asList(passengers));

        Car self = new Car(gas, color, p, null);
        return self;
    }

    public static Car withGasColorPassengersAndTrunk(int gas, String color, String... passengers) {
        List<String> p = Collections.unmodifiableList(Arrays.asList(passengers));

        Car self = new Car(gas, color, p, Arrays.asList("jack", "wrench", "spare wheel"));
        return self;
    }

    public int getGasLevel() {
        return gasLevel;
    }

    public String getColor() {
        return color;
    }

    public List<String> getPassenger() {
        return passengers;
    }

    public List<String> getTrunkContents() {
        return trunkContents;
    }

    @Override
    public String toString() {
        return "Car{" + "gasLevel=" + gasLevel + ", color=" + color + ", passengers=" + passengers
                + (trunkContents != null ?
                ", trunkContents=" + trunkContents: " no trunk") + '}';
    }

    static class RedCarCriterion implements CarCriterion {

        @Override
        public boolean test(Car c) {
            return c.color.equals("Red"); // <- використовуємо безпосередньо поле, без getter
        }
    }

    static class GasLevelCarCriterion implements CarCriterion {

        private int threshold;

        public GasLevelCarCriterion(int threshold) {
            this.threshold = threshold;
        }

        @Override
        public boolean test(Car c) {
            return c.gasLevel >= threshold; // <- використовуємо безпосередньог поле, без getter
        }
    }
}
```

Використання класів у коді:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v03/CarScratch.java#40-41
showAll(getCarsByCriterion(cars, new Car.RedCarCriterion()));
showAll(getCarsByCriterion(cars, new Car.GasLevelCarCriterion(6)));
```

### 2.2 Питання кількості

Що робити, щоб одну критерію використовувати декілька разів раз за разом? Можна використовувати різні рішення одне з яких статичні поля.

#### Статичні поля

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v03/CarScratch.java#66
public static final RedCarCriterion RED_CAR_CRITERION = new RedCarCriterion();
```

Також можна використовувати шаблон фабрики.

#### Шаблон Одинак

В наступному прикладі використовується шаблон Одинак, який отримує один і той самий екземпляр класу, який записаний в статичне поле, що економить використання ресурсів системи. Цей варіант має більшу перевагу ніж попередній (статичне поле):

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v03/CarScratch.java#68-70
public static RedCarCriterion getRedCarCriterion() {
    return RED_CAR_CRITERION;
}
```

Особливість цього підходу, полягає в тому, що ми можемо змінювати реалізацію в методі `getRedCarCriterion()` і не змінювати код при використанні цього методу:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v03/CarScratch.java#43-45
// Використання статичного поля і шаблону фабрики
showAll(getCarsByCriterion(cars, Car.RED_CAR_CRITERION));
showAll(getCarsByCriterion(cars, Car.getRedCarCriterion()));
```

Зробимо невеличкий рефакторинг:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v04/Car.java#58-70
private static class RedCarCriterion implements CarCriterion {

    @Override
    public boolean test(Car c) {
        return c.color.equals("Red"); // <- використовуємо безпосередньо поле, без getter
    }
}

private static final RedCarCriterion RED_CAR_CRITERION = new RedCarCriterion(); // <- забороняємо доступ із зовні

public static CarCriterion getRedCarCriterion() { // <- результат дані прирівнені до інтерфейсу
    return RED_CAR_CRITERION;
}
```

### 2.3 Питання про видимість

Створимо фабрику для визначення рівня бензину:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v04/Car.java#72-76
public static CarCriterion getGasLevelCarCriterion(int threshold) {
    return new GasLevelCarCriterion(threshold);
}

private static class GasLevelCarCriterion implements CarCriterion { // <-забороняємо доступ із зовні
```

Використання статичного методу:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v04/CarScratch.java#42
showAll(getCarsByCriterion(cars, Car.getGasLevelCarCriterion(6)));
```

Якщо порівнювати дві реалізації

```java
Car.getGasLevelCarCriterion(6);
Car.getRedCarCriterion();
```

Другий є шаблоном Singleton, а перший таким не являється.

В Java для створення об'єктів можна використовувати два підходи: використовувати готовні класи, які використовують інтерфейси чи створюжвати анонімний клас, з нашою поточною реалізацією інтерфейсу.

Ось як можна візуально перетворити клас з загальною реалізацією в анонімний клас:

```java
Food brownie = new    ChocolateBrownie();
                      xxxxxxxxxxxxxxxx  x      <- видаляємо
class ChocolateBrownie implements Food {}
xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx              <-  видаляємо

Отримуємо:

Food brownie = new Food() {};
```

Також можна спростити реалізацію використовуючи анонімний клас. Ось код, який містить показ повної та спрощеної версії:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v04/Car.java#58-66
private static class RedCarCriterion implements CarCriterion {

    @Override
    public boolean test(Car c) {
        return c.color.equals("Red"); // <- використовуємо безпосередньо поле, без getter
    }
}

private static final RedCarCriterion RED_CAR_CRITERION = new RedCarCriterion();

//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v05/Car.java#58-64
// СПРОЩЕНА ВЕРСІЯ (використання анонімного класу, який реалізує інтерфейс CarCriterion)
public static final CarCriterion RED_CAR_CRITERION = new CarCriterion() {

    @Override
    public boolean test(Car c) {
        return c.color.equals("Red"); // <- використовуємо безпосередньо поле, без getter
    }
};
```

### 2.4 Спрощення синтаксису, використовуючи лямбда

Як використовувати скорочений синтаксис:

```java
public static final CarCriterion RED_CAR_CRITERION = new CarCriterion() {
                                                     xxxxxxxxxxxxxxxxxxxx <- видаляємо, компілятор дізнається тип по типу змінної
    @Override
    public boolean test(Car c) /*->*/ {
    xxxxxxxxxxxxxxxxxxx xxx    xx  xx   <- видаляємо, компілятор читає інтерфейс, у якого є один абстрактний метод, тому ці дані він бере з інтерфейсу
        return c.color.equals("Red"); // <- використовуємо безпосередньо поле, без getter
    }
};

(c) -> c.color.equals("Red"); // дослівно (цей аргумент) -> представляє цей результат
```

Лямбда синтаксис спрощує код до рівня важливих деталей, які мають значення. Такий синтаксис легко і швидко проглянути і зрозуміти без відволікання на другорядні деталі (анонімного чи іменного калсу, чи назви та типу метода, який є одним в реалізації) тільки поведінка коду.

Оскільки ми використовуємо інтерфейс з одним публічним методом, то ми можемо скористатись лямбда:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v06/Car.java#59-61
public static final CarCriterion RED_CAR_CRITERION = (Car c) -> {
    return c.color.equals("Red");
};
```

### 2.5 Лямбда синтаксис варіація

Приклад сортування машин по рівню бензину:

```java
// file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v06/Car.java#85-98
public static Comparator<Car> getGasComparator() {
    return gasComparator;
}

private static final Comparator<Car> gasComparator = new CarGasComparator();

private static class CarGasComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.gasLevel - o2.gasLevel;
    }

}
```

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v06/CarScratch.java#45-46
cars.sort(Car.getGasComparator());
showAll(cars);
```

Реалізуємо, аналогічний функціонал, використовуючи лямбда:

```java
private static final Comparator<Car> gasComparator = (o1, o2) -> {
    return o1.gasLevel - o2.gasLevel;
};
```

Також можна ще більше спростити синтаксис:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v07/Car.java#59
public static final CarCriterion RED_CAR_CRITERION = c -> c.color.equals("Red");
```

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v07/Car.java#87
private static final Comparator<Car> gasComparator = (o1, o2) -> o1.gasLevel - o2.gasLevel;
```

### 2.6 Анотація `@FunctionalInterface`

Анотація `@FunctionalInterface` допомагає звернути увагу компілятора на даний інтерфейс, що буде використовуватись лямбда, а також перевірити інтерфейс чи має він один абстрактний метод.

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v07/CarScratch.java#59-63
@FunctionalInterface
interface CarCriterion {

    boolean test(Car c);
}
```

### 2.7 Встановлюємо тип лямбда

#### Tипи лямбда

1. Використання безпосередньо лямбда, призначаємо лямбда до змінної (застосовуємо найчастіше): `CarCriterion c = c -> ...;`
2. Передаємо лямбда вираз для використання як аргуент методу: `doSometingWithSomeCars(c -> ...);`
3. Отримуємо лямбда вираз, що повертається з методу: `CarCriterion getCriterion() {
   return c -> ...;
   };
4. Прирівнюємо (`casting`) визначення лямбди до інтерфейсу (рідко використовується) для встановлення контексту: `((Interface) (c -> ...)).methodDefinedInInterface(arguments);` метод з реалізацією: `boolean b = ((CarCriterion) (c -> c.getColor().equals("Red"))).test(Car.withGasColorPassengers(0, "Red"));`

Додамо приклад виклику лямбда, що повертається з методу функції:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v08/Car.java#65-67
public static CarCriterion getFourPassengerCriterion() {
    return c -> c.getPassengers().size() == 4;
}
```

Передаємо лямбда як аргумент методу, в останньому прикладі встановлюємо контекст лямбда в круглих скобках:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v08/CarScratch.java#47-51
showAll(getCarsByCriterion(cars, c -> c.getPassengers().size() == 2));
showAll(getCarsByCriterion(cars, Car.getFourPassengerCriterion()));
// Перевіримо нову машину Car чи вона має червоний колір
boolean b = ((CarCriterion) (c -> c.getColor().equals("Red"))).test(Car.withGasColorPassengers(0, "Red"));
System.out.println(b);
```

### 2.8 Подальше узагальнення

Щоб зробити код більш узагальненим нам потрібно змінити:

```java
// Замість Car використовуємо узагальнену змінну X
// !!! Зверніть увагу, що потрібно задекларувати тип змінної X
// Для цього на початку всього виразу ставимо тип в дужках <X>
<X> List<X> getByCriterion ( // <-- для встановлення узагальнення використовуємо
                Iterable<X> in, // <-- використовуємо узагальнення до ітератору
                Criterion<X> crit) { // <-- інтерфейс також прирівнюємо до узагальнення
    List<X> output = ...;
    for(X c : in) {
        if(crit.test(c)) {
            output.add(c);
        }
    }
    return output;
}
```

Також нам потрібно змінити інтерфейс додавши і йому узагальнення:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v09/CarScratch.java#64-68
@FunctionalInterface
interface Criterion<E> {

    boolean test(E c);
}
```

Ось приклад як можна організувати узагальнення:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v09/CarScratch.java#21-29
public static <E> List<E> getByCriterion(Iterable<E> in, Criterion<E> crit) {
    List<E> output = new ArrayList<>();
    for (E c : in) {
        if (crit.test(c)) {
            output.add(c);
        }
    }
    return output;
}
```

Для викоритання інтерфейсу нам потрібно передавати тип даних:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v09/Car.java#65-67
public static Criterion<Car> // встановлюємо `<Car>`
                                getFourPassengerCriterion() {
    return c -> c.getPassengers().size() == 4;
}
```

Також можна змінити метод `showAll()`:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v09/CarScratch.java#10-15
public static <E> void showAll(List<E> lc) {
    for (E c : lc) {
        System.out.println(c);
    }
    System.out.println("---------------------------");
}
```

Тепер ми маємо велику гнучкість у використанні інтерфейсу `Criterion` та методу `showAll()`, що дозволяє використовувати різні класи об’єктів, а не тільки клас `Car`.

### 2.9 Демонстрування узагальнень

Ось приклад як можна використовувати узагальнення:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v10/CarScratch.java#45-52
// Використання узагальнень
List<String> colors = Arrays.asList("LightCoral", "pink", "Orange", "Gold", "plum", "Blue", "limeGreen");
showAll(getByCriterion(colors, str -> str.length() > 4)); // LightCoral, Orange, limeGreen
showAll(getByCriterion(colors, str -> Character.isUpperCase(str.charAt(0)))); // LightCoral, Orange, Gold, Blue

LocalDate today = LocalDate.now();
List<LocalDate> dates = Arrays.asList(today, today.plusDays(1), today.plusDays(7), today.minusDays(1));

showAll(getByCriterion(dates, ld -> ld.isAfter(today)));
```

## 3. Сила комбінацій та модифікацій

### 3.1 Покращення факторів поведінки

Перепишемо всю фабрику`GasLevelCarCriterion`:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v09/Car.java#69-85
public static Criterion<Car> getGasLevelCarCriterion(int threshold) {
    return new GasLevelCarCriterion(threshold);
}

static class GasLevelCarCriterion implements Criterion<Car> {

    private int threshold;

    public GasLevelCarCriterion(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean test(Car c) {
        return c.gasLevel >= threshold; // <- використовуємо безпосередньог поле, без getter
    }
}
// Варіант 1
public static Criterion<Car> getGasLevelCarCriterion(int threshold) {
    return new Criterion<Car>() {

        @Override
        public boolean test(Car c) {
            return c.gasLevel >= threshold;
        }
    };
}
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v11/Car.java#69-71
// Варіант 2 - максимальне скорочення лямбда
public static Criterion<Car> getGasLevelCarCriterion(int threshold) {
    return c -> c.gasLevel >= threshold;
}
```

Перевіряємо код:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v10/CarScratch.java#43
showAll(getByCriterion(cars, Car.getGasLevelCarCriterion(6)));
```

### 3.2 Вимоги до замикання

Змінна, що використовується в замиканні повинна бути `final` чи ефектино `final`, що в функціональному програмуванні дозволяє посилатись на аргумент, але не змінювати його:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/functional/v11/Car.java#69
public static Criterion<Car> getGasLevelCarCriterion(final int threshold) {
```

В цьому прикладі це не дуже очевидно, тому що використовується примітивне значення, проте при використанні об'єкту це стає більш вагомим для функціонального стилю програмування, тому що `final` запобігає змінювати об'єкт, який передається в метод.

### 3.3 Інші приклади

Ось варіант як можна порівнювати машини по декільком кольорам:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v12/Car.java#71-74
public static Criterion<Car> getColorCriterion(String...colors) {
    Set<String> colorSet = new HashSet<>(Arrays.asList(colors));
    return c -> colorSet.contains(c.color);
}
```

Використання:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v12/CarScratch.java#53
showAll(getByCriterion(cars, Car.getColorCriterion("Red", "Black")));
```

Функція, яка бере іншу функцію як аргумент, називається Hight-Order Function (Функції вищого порядку). Функція, що повертає поведінку від аргументів фабрики називається замиканням. Значення, які використовуються всередині поведінки сценарія будуть константами.

### 3.4-3.5 Комбінування поведінок

Як комбінувати кілька різних поведінок (кольори, використання рівнів пального) в одному фабричному методі.

Чи, наприклад, як отримати негативний результат з нашого запиту. Якщо ми передаємо критерії в фабричний метод, яка показує поведінку використовуючи поведінку аргументів як частини цієї поведінки:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v12/CarScratch.java#11-13
public static <E> Criterion<E> negate(Criterion<E> crit) {
    return x -> !crit.test(x);
}
```

Використання:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v12/CarScratch.java#69-72
Criterion<Car> level7 = Car.getGasLevelCarCriterion(7);
showAll(getByCriterion(cars, level7));
Criterion<Car> notLevel7 = CarScratch.negate(level7);
showAll(getByCriterion(cars, notLevel7));
```

#### Створимо методи `and()` та `or()` для критеріїв пошуку автомобілів

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v12/CarScratch.java#15-21
public static <E> Criterion<E> and(Criterion<E> first, Criterion<E> second) {
    return x -> first.test(x) && second.test(x);
}

public static <E> Criterion<E> or(Criterion<E> first, Criterion<E> second) {
    return x -> first.test(x) || second.test(x);
}
```

Використання:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v13/CarScratch.java#74-83
// Criterion AND
Criterion<Car> isRed = Car.getColorCriterion("Red");
Criterion<Car> fourPassengers = Car.getFourPassengerCriterion();

Criterion<Car> redFourPassengers = and(isRed, fourPassengers);
showAll(getByCriterion(cars, redFourPassengers));

// Criterion OR
Criterion<Car> isBlack = Car.getColorCriterion("Black");
Criterion<Car> blackOrFourPassengers = or(isBlack, fourPassengers);
showAll(getByCriterion(cars, blackOrFourPassengers));
```

Ключ функціонального програмування - це взяти поведінку, яка вам майже підходить та перетворити на поведінку, яка вам повністю підходить.

### 3.6 Чистка дизайну

З Java 8, крім абстратних методів та констант, була додана підтримка статичних методів в інтерфейсі. Тому ми створимо новий клас `Criterion` та перенесемо в нього з файлу `CarScratch.java` інтерфейс `Criterion` та методи: `negate()`, `and()` та `or()`, зверніть увагу, що ці методи зазначаються в інтерфейсі без ключового слова `public`:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v14/Criterion.java

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
```

Такий варіант звільняє від розкидання методів критерій по файлам java, та тримає всі потрібні методи в одному місці (наче належать до цього інтерфейсу), що полегшує до них доступ з інших класів java.

Оскільки в класі `CarScratch` вже немає вищевказаних методів, тому потрібно вказати де ці методи знаходяться:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v14/CarScratch.java#57-72
Criterion<Car> level7 = Car.getGasLevelCarCriterion(7);
showAll(getByCriterion(cars, level7));
Criterion<Car> notLevel7 = Criterion.negate(level7);                            // <-- вказуємо інтерфейс Criterion
showAll(getByCriterion(cars, notLevel7));

// Criterion AND
Criterion<Car> isRed = Car.getColorCriterion("Red");
Criterion<Car> fourPassengers = Car.getFourPassengerCriterion();

Criterion<Car> redFourPassengers = Criterion.and(isRed, fourPassengers);      // <-- вказуємо інтерфейс Criterion
showAll(getByCriterion(cars, redFourPassengers));

// Criterion OR
Criterion<Car> isBlack = Car.getColorCriterion("Black");
Criterion<Car> blackOrFourPassengers = Criterion.or(isBlack, fourPassengers); // <-- вказуємо інтерфейс Criterion
showAll(getByCriterion(cars, blackOrFourPassengers));
```

#### Використання `default` методів інтерфейсу

Java 8 підтримує не тільки статичні методи, а також методи екземпляру (що мають обмеження, використовувати тільки методи getter/setter), які називаються методами по-замовчуванню і позначаються ключовим словом `default`.

Ось варіант рефакторингу, який замість статичних методів використовує методи екземпляру по-замовчуванню (для цього потрібно замінити `static <E>` на ключове слово `default` та переписати внутрішню реалізацію як на екземпляр).

Основна проблема використання статичних методів в інтерфейсі заключається в тому, що вони відносяться до всього інтерфейсу і можуть виникнути проблеми з типом даних, який підставляється, ви не можете нормально використовувати прикріплений тип до статичних полів і їх поведінки.

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v15/Criterion.java
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

```

Тепер статичні методи використовуються як методи екземпляру, що тепер має більш читаєму структуру їх застосування:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v15/CarScratch.java#57-72
Criterion<Car> level7 = Car.getGasLevelCarCriterion(7);
showAll(getByCriterion(cars, level7));
Criterion<Car> notLevel7 = level7.negate();                        // <-- метод екзмпляру
showAll(getByCriterion(cars, notLevel7));

// Criterion AND
Criterion<Car> isRed = Car.getColorCriterion("Red");
Criterion<Car> fourPassengers = Car.getFourPassengerCriterion();

Criterion<Car> redFourPassengers = isRed.and(fourPassengers);      // <-- метод екзмпляру
showAll(getByCriterion(cars, redFourPassengers));

// Criterion OR
Criterion<Car> isBlack = Car.getColorCriterion("Black");
Criterion<Car> blackOrFourPassengers = isBlack.or(fourPassengers); // <-- метод екзмпляру
showAll(getByCriterion(cars, blackOrFourPassengers));
```

### 3.7 Інтерфейси для лямбда

Оскільки для побудови лямбда ми повинні мати інтерфейс з одним методом, контекст, щоб було зрозуміло в якому виконується лямбда. Проте постійно створювати інтерфейси це незручно та складно, тому можне це все узагальнити, наприклад, якщо метод отримує один екземпляр і після своєї роботи отримує інший екземпляр:

```java
interface Function<E, F> {
    F apply(E e);
}
```

Крім того, ще потрібно враховувати, що типи можуть бути примітивними і буде відобуватись автоупаковка та авторозпаковка, давайте подивимось декілька варіантів:

```java
F f(E e) -> Function<E, F>
boolean f(E e) -> Predicate<E>
void f(E e) -> Consumer<E>
E f() -> Supplier<E>
E f(E e) -> UnaryOperator<E>
E f(E e, E f) -> BinaryOperator<E>
```

Це є базові інтерфейси ядра колекції Java.

В стандартній колекції функціональних інтерфейсів налічується 43 інтерфейси (`java.util.function`), проте більша частина це варіації вищенаведених. Є варіації роботи з поверненням типів: `boolean`, `int` (нижне цього типу примітиви не підтримуються), `long`, `double` (нижне цього типу примітиви не підтримуються, тобто `float` і нижче - взаємодія відбувається з 64 бітнимим числами)

```java
// інтерфейси, які отримують два аргументи
Bi... BiFunction<E, F, G>    G function(E e, F f)
// інтерфейси, що повертають вказаний тип
ToInt... -> return int
ToLong... -> return long
ToDouble... -> return double
// інтерфейси, що мають один аргумент вказаного типу
Int... -> argument type int
Long... -> argument type long
Double... -> argument type double
// інтерфейси, що повертають вказаний тип, методом без аргументів
IntSupplier
LongSupplier
DoubleSupplier
// інтерфейси, що приймають перший аргумент об’єкт, а в другому вказаний тип
ObjIntConsumer
ObjLongConsumer
ObjDoubleConsumer
```

Якщо ми поглянемо на `java.util.function.Predicate` в категорії `Instance Methods` ми побачимо вже визначені за нас методи: `negate()`, `or()` та `and()`.

### 3.8 Використання `Predicate` у прикладах

Оскільки стандартний інтерфейс є вже визначений як `Predicate` з всіма методами, що ми визначили в інтерфейсі `Criterion`, ми просто замінємо `Criterion` інтерфейс стандартним `java.util.function.Predicate`.

### 3.9-3.10 Використання шаблону Адаптер

```plant-uml#keuehlhlahebhe
@startuml

skinparam backgroundColor transparent
skinparam defaultFontName Roboto Medium
' використвоуємо прямі і тільки прямілінії
skinparam linetype ortho
' відключаємо тіні
skinparam shadowing false
skinparam sequenceParticipantBackgroundColor #lightgray-white

skinparam sequence {
    ' задаємо колір ліній
    ArrowColor DimGray
    ' задаємо колір внутрішньої кайми часу виконання функції
    LifeLineBorderColor DimGray
    ' задаємо градієнт фону внутрішніх прямокутників
    LifeLineBackgroundColor #LightGray-White

    ' задаємо колір ліній
    ParticipantBorderColor DimGray
    ' задаємо колір каймі прямокутників
    ParticipantBackgroundColor #LightGray-White
    ' задаємо товщину ліній для прямокутників
    ParticipantBorderThickness .55
    'ParticipantFontName Roboto Medium
    ParticipantFontSize 15
    'ParticipantFontColor Black
}

title Створення шаблону Адаптера

' Ім’я екземпляру : Ім’я класу
participant "compareGreater : Predicate<E>"
participant "compareWithThis : ToIntFunction<>"
participant "Comparator"

activate "compareWithThis : ToIntFunction<>"
"compareWithThis : ToIntFunction<>" -> "Comparator": target
activate "Comparator"

"compareGreater : Predicate<E>" -> "compareWithThis : ToIntFunction<>": x
"compareWithThis : ToIntFunction<>" -> "Comparator": x
"Comparator" -> "compareGreater : Predicate<E>": comp.compare(target, x): int
deactivate "compareWithThis : ToIntFunction<>"
deactivate "Comparator"
[<- "compareGreater : Predicate<E>": [int > 0] ? true : false

@enduml
```

Шабон Адаптеру дозволяє поєднати об’єкт інтерфейсу з клієнтом, який його використовує для вибірки елементів.

Адаптер `Predicate` передає один параметр, тоді як інтерфейс функції передає два параметри, один з яких вже заздалегіть встановлений.

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v18/CarScratch.java#13-19
public static <E> ToIntFunction<E> compareWithThis(E target, Comparator<E> comp) {
    return x -> comp.compare(target, x);
}

public static <E> Predicate<E> compareGreater(ToIntFunction<E> comp) {
    return x -> comp.applyAsInt(x) < 0;
}
```

Використання:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v18/CarScratch.java#84-90
Car bert = Car.withGasColorPassengers(5, "Blue");

ToIntFunction<Car> compareWithBert = compareWithThis(bert, Car.getGasComparator());
cars.forEach(car -> System.out.println("comparing " + car + " with bert gives "
        + compareWithBert.applyAsInt(car)));

showAll(getByCriterion(cars, compareGreater(compareWithBert)));
```

## 4. Робота з чистими (pure) функціями

### 4.1 Концепт чистих (pure) функцій

Числа (`pure`) функція - це функція, що повертає результат тільки по аргументу і результат завжди однаковий при однакових аргументах. Наприклад, регулярні функції (`impure`) можуть повертати результат з баз даних чи i/o, що може відрізнятись результатом роботи.

Чиста функція має реферанту прозорість (`referential transparentcy`), тобто, якщо:

```
f(x) -> x + 2
```

то

```
f(x) рівне x + 2
```

Це дійсно тільки для чистих функцій, тому що звичайна функція може представляти різний результат при тих самих аргументах чи мати побічних ефект (`side effect`), коли функція впливає на змінні, які не належать цій функції. Це дозволяє писати більш надійний код, який містить менше помилок, оскільки функція веде себе прогнозовано, а також її легше тестувати та відлагоджувати.

Також функції мають кращу архітектуру і продуктивність. Оскільки функції при однакових аргументах виводять однаковий результат, то результат роботи функції можна кешувати, що при повторному виклику з аналогічними аргументами виводить вже попередньо готовий результат.

Також чисті функції можуть полегшити використання багатопоточності. Оскільки функції не залежать одна від одної, їх можна запускати паралельно.

### 4.2 Чиста функція на практиці

З чистими функціями важко працювати. Як з їх допомогою можна зробити щось робоче і зручне? Можна написати тільки програми командного рядка, які будуть використовувати 100% чистих функцій. Така програма не буде мати ніякого I/O чи доступу до бази даних, що дуже не практично.

Основне рішення - це передача змінної на верхню (`top`) фунцію (одна функція може викликати іншу функцію і т.д.), яка буде передавати дані до нижніх функцій, тобто дані отримані з баз даних чи I/O  будуть передаватись зі звичайних функцій до чистих функцій.

Приклад стандартної схеми виклика функцій:

```
impurefn()
   |  ^
   /  |
  impurefn2()
     |  ^
     /  |
    База даних
```

Функціональна схема роботи:

```
База даних <- impurefn()
                |
                /
                purefn()
                  |
                  /
                  purefn2()
                    |
                    /
                  ...
```

Ми робимо pipeline ("трубопровід") процесу, де елемент даних крок за кроком проходить всі чисті функції.

```java
дані ◇◇◇◇ -> pure function -> дані △△△△ -> pure function -> дані XXX
дані                            нові дані                        нові дані  
```

### 4.3 Планування pipeline (трубопроводу) фреймворку

З одного боку ми маємо дані, а з іншого - операції, які можна використовувати над даними.

Для їх поєднання нам потрібна спеціальна обгортка, яка дозволить поєднати дані з операціями над даними. Для обгортки ми будемо використовувати клас `SuperIterable`, оскільки він дозволяє створити ефект трубопроводу з операціями над елементами. Можна використовувати `List`, проте він менш ефективний, оскільки не дозволяє зв'язувати операції в pipeline. Це є стандартний шлях, який адресований саме цій стандартній проблемі.

```java
<SuperIterable> ◇◇◇◇ -> pure function -> <SuperIterable> △△△△
```

Ми при кожному виклику чистої функції отримуємо об'єкт `SuperIterable` з новими даними.

### 4.4 Реалізація pipeline фреймворку

```
SuperIterable <|---- Iterable<E>
+ iterator()
+ filter()
```

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v19/SuperIterable.java#10-21,40-47
public class SuperIterable<E> implements Iterable<E> {

    private Iterable<E> self;

    public SuperIterable(Iterable<E> self) {
        this.self = self;
    }

    @Override
    public Iterator<E> iterator() {
        return self.iterator();
    }

    public static void main(String[] args) {
        SuperIterable<String> strings = new SuperIterable<>(
                Arrays.asList("LightCoral", "pink", "Orange", "Gold", "plum", "Blue", "limegreen")
        );

        for (String s : strings) {
            System.out.println("> " + s);
        }
    }
}
```

Наступний блок реалізує `Predicate<E>`, що дозовляє нам фільтрувати результат:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v19/SuperIterable.java#30-38
public SuperIterable<E> filter(Predicate<E> pred) {
    List<E> results = new ArrayList<>();
    for (E e : self) {
        if (pred.test(e)) {
            results.add(e);
        }
    }
    return new SuperIterable<>(results);
}

//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v19/SuperIterable.java#49-55
// модифікація значень відбуваєтья на копії обгортки масиву,
// масив strings залишається незмінним
SuperIterable<String> upperCase = strings.filter(s -> Character.isUpperCase(s.charAt(0)));
System.out.println("---------------------------");
for (String s : upperCase) {
    System.out.println(">" + s);
}
```

### 4.5 Внутрішня ітерація

Ви звернули увагу, що щоразу як нам потрібні дані ми запускаємо цикл `for` для колекції. Це виглядає не дуже добре, тому краще використовувати елегантний спосіб, щоб це спростити. Потрібно використовувати `Consumer` для обробки кожного елементу:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v19/SuperIterable.java#23-28
public void forEvery(Consumer<E> cons) {

    for (E e : self) {
        cons.accept(e);
    }
}

//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v19/SuperIterable.java#56-58
// Consumer
System.out.println("---------------------------");
upperCase.forEvery(s -> System.out.println(">" + s));
```

Всі ці рішення вже є вбудованими в Java в інтерфейс `Iterable`. Тому потрібно видалити метод `forEvery()` і замінити на `forEach()`, тому зробимо рефакторинг всього коду, використовуючи реалізацію з коробки:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v20/SuperIterable.java#36,41
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
```

### 4.6-4.7 Робимо зміни

На даний час `SuperIterable` дозволяє фільтрувати дані і повертати новий `SuperIterable`. Проте крім фільтрації дані повинні змінюватись, тобто з одних даних потрібно створити нові дані того самого типу чи іншого.

```java
◇◇◇◇◇ (10 <E> елементів) -> map(Function<E,F>) -> △△△△△ (на виході 10 <F> елментів)
SuperIterable<E> -> Function<E, F> -> SuperIterable<F>
```

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v21/SuperIterable.java#23-27
public <F> SuperIterable<F> map(Function<E, F> op) {
    List<F> results = new ArrayList<>();
    self.forEach(e -> results.add(op.apply(e)));
    return new SuperIterable<>(results);
}
```

Використовуємо метод `map()` в ланцюжку викликів, також можна побачити, що при наступній ітерації елементи колекції залишились незмінними:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v21/SuperIterable.java#51-72
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
```

`SuperIterable` дозволяє застосовувати операції, визначаючи "чисту" функцію, до набору даних. `SuperIterable` створює нову копію даних з самої себе, роблячи потрібні структурні модифікації.

### 4.8 Кодування незмінних типів даних

Проте при використання `SuperIterable` можна ненароком змінити існуючі дані, при створенні нових даних.

```
Car{fuel=3} -> map(c-> c.addFuel(3)) -> Car{fuel=6}
Car{fuel=6} <----------------| фактично змінили дані в початковому об'єкті
Car{fuel=6} ----------> == <------------Car{fuel=6}
```

Для нормальної роботи нам потрібно дістати дані зі старого об'єкту і на основі них створити новий об'єкт.

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v21/SuperIterable.java#75-80
carIter
    .map(c -> Car.withGasColorPassengers(
        c.getGasLevel() + 4,
        c.getColor(), 
        c.getPassengers().toArray(new String[]{})))
    .forEach(c -> System.out.println("> " + c));
```

Такий підхід виглядає дуже заплутано, натомість всю цю логіку можна приховати в класі об'єкта, наприклад, в методі `addFuel(int)`, який замість модифікації самого об'єкта буде повертати новий об'єкт з змінненим полем `fuel`.

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v21/Car.java#43-49
public Car addGass(int g) {
    return Car.withGasColorPassengers(
        gasLevel + 4,
        color, 
        passengers.toArray(new String[]{})
    );
}
```

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v21/SuperIterable.java#84-86
carIter
        .map(c -> c.addGass(4))
        .forEach(c -> System.out.println("> " + c));
```

### 4.9 One-to-many зміни Зміни один до багатьох

Робота з даними вимагає в певних ситуаціях отримати дані з існуючого об'єкту (наприклад, масив чи список всіх пасажирів) чи зробити агрегацію даних, тощо.

```
<E>Cars -----------------------> <F> список пасажирів
Function<E, SuperIterable<F>>
c -> (SuperIterable<String>) new SuperIterable((List<String>) c.getPassengers())
```

Такий вид операцій називається `flatMap` (плоске перетворення):

```
flatMap(Function<E, SuperIterable<F>> f)

self <- для кожного ...
    (SuperIterable<F>) f.apply(e) <- forEach додає елементи до фінального результату
```

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v22/SuperIterable.java#23-29
public <F> SuperIterable<F> flatMap(Function<E, SuperIterable<F>> op) {
    List<F> results = new ArrayList<>();

    self.forEach(e -> op.apply(e).forEach(f -> results.add(f)));

    return new SuperIterable<>(results);
}
```
Наступний приклад виведе список всіх пасажирів в представлених автомобілях:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v22/SuperIterable.java#98-102
carIter
        .filter(c -> c.getPassengers().size() > 3)
        .flatMap(c -> new SuperIterable<>(c.getPassengers()))
        .map(s -> s.toUpperCase())
        .forEach(c -> System.out.println("> " + c));
```

Ми беремо `SuperIterable` отримуємо список елементів (`carIter`), ми створюємо новий `SuperIterable` залишаючи частину елементів відфільтрованих предикатом (операція `filter`), ми створюємо новий `SuperIterable` де ми змінюємо індивідуальний вміст кожного елементу, використовуючи перетворення (операція `map`), ми створюємо новий `SuperIterable`, який з одного елементу робить багато (0 і більше) елементів (операція `flatMap`).

При операції `flatMap` встачаються зв'язки між елементами (наприлад, машиною і пасажирами, що в ній знаходяться).

### 4.10 Тримаємо зв'язок між пов'язаними елементами

Нагадаємо, що при нормальній роботі операції `flatMap` втрачаються зв'язки між елементами, проте в деяких випадках нам потрібно знати, наприклад, до якої машини належить пасажир.

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v22/SuperIterable.java#106-109
carIter
        .flatMap(c -> new SuperIterable<>(c.getPassengers())
                            .map(p -> p + " is riding in a " + c.getColor() + " car"))
        .forEach(c -> System.out.println("> " + c));
```

### 4.11 Формальні оригінальні огортки

`SuperIterable` використовуються для обгорток в функціональному стилі, проте не в матиматичному. В матиматичному використовуються монади. Монади мають три шматки: обгортання даних в монаду, діставання даних з монади і операція `flatMap` для діставання різних даних. Тобто операція `flatMap` бере монаду, виконує операції над даними, що містить монада і назад повертає монаду з зміненими даними. Існують вбудовані монади в Java 8: це Stream API, який має багато вбудованих методів і реалізований на основі `SuperIterable`. (Stream API буде розглянутий пізніше)

### 4.12 Інші обгортки

Розглянемо приклад перевірки на `null`:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v22/NullChecks.java#6-22
public class NullChecks {
    
    public static void main(String[] args) {
        HashMap<String, Car> owners = new HashMap<>();
        owners.put("Sheila", Car.withGasColorPassengers(
            6, "Red", "Fred", "Jim", "Sheila"));
        owners.put("Librarian", Car.withGasColorPassengers(
            3, "Octarin", "Rincewind", "Ridcully"));
        owners.put("Ogg", Car.withGasColorPassengersAndTrunk(
            9, "Black", "Weatherwax", "Magrat"));

        String owner = "Ogg";
        Car c = owners.get(owner);
        List<String> trunkItems = c.getTrunkContents();
        System.out.println(owner + " has " + trunkItems + " in the car");
    }
}
```

Якщо для прикладу ми вкажемо власника `"Weatherwax"` ми отримаємо `NullPointException`, для цього нам потрібно весь код після `owner` обгорнути в блок `if (c != null) {`, і після `trunkItems` також обгорнути в `if (trunkItems != null) {`, для перевірки всіх виключень `NullPointException`.

Звичайно змість `null` ми можемо поверути порожній `List<String>`, проте можна використовувати функціональні техніки обійти це виключення.

Для цього можна використовувати техніку присутності чи відсутності елементу, який в разі присутності елементу виконує над ним потрібні операції, в іншому випадку при порожньому результаті операції пропускаються.

Для цього можна використовувати клас `java.util.Optional<T>`, цей клас має вже реалізовані методи:

* `Optional<T> filter(Predicate<? super T> predicate)`;
* `<U> Optional<U> map(Function<? super T, ? extends U> mapper)`;
* `<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper)`;
* `void ifPresent(Consumer<? super T> consumer)` можна використовувати замість `forEach`;

Ось приклад, як можна організувати код без використання блоків `if (obj != null) {`, які можна забути включити в код:

```java
//file:///~/Projects/Java/Core/FP/FunctionalProgrammingForJava/src/main/java/functional/v22/NullChecks.java#28-33
Optional<HashMap<String, Car>> ownerOpt = Optional.of(owners);
ownerOpt
        .map(m -> m.get(owner))
        .map(x -> x.getTrunkContents())
        .map(x -> owner + " has " + x + "in the car")
        .ifPresent(m -> System.out.println(m));
```

### 4.13 Використання `Optional` в car API

ВІДЕО 44 01:27 copy new version of  code
=========================================================================


