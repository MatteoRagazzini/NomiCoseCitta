package model;

public enum RoundType {
    STOP, TIMER;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}