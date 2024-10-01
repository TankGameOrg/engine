package pro.trevor.tankgame.util.openhours;

public enum Day {
    SUNDAY("Sunday", 1),
    MONDAY("Monday", 2),
    TUESDAY("Tuesday", 3),
    WEDNESDAY("Wednesday", 4),
    THURSDAY("Thursday", 5),
    FRIDAY("Friday", 6),
    SATURDAY("Saturday", 7);

    private final int number;
    private final String name;

    Day(String name, int number) {
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public static Day fromNumber(int number) {
        return switch (number) {
            case 1 -> SUNDAY;
            case 2 -> MONDAY;
            case 3 -> TUESDAY;
            case 4 -> WEDNESDAY;
            case 5 -> THURSDAY;
            case 6 -> FRIDAY;
            case 7 -> SATURDAY;
            default -> throw new IllegalArgumentException("Invalid day: " + number);
        };
    }

    public static Day fromName(String name) {
        return Day.valueOf(name.toUpperCase());
    }

    @Override
    public String toString() {
        return name;
    }
}