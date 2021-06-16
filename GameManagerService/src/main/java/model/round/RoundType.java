package model.round;

public enum RoundType {
    STOP, TIMER;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}