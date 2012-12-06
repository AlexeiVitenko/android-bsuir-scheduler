package by.bsuir.scheduler.model;

public class Alarm {
    private int week;
    private int day;
    private int sh;
    private int sm;
    private int pairNumber;

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public int getSh() {
        return sh;
    }

    public int getSm() {
        return sm;
    }

    public int getPairNumber() {
        return pairNumber;
    }

    public Alarm(int week, int day, int sh, int sm, int pairNumber) {
        this.week = week;
        this.day = day;
        this.sh = sh;
        this.sm = sm;
        this.pairNumber = pairNumber;
    }
}
