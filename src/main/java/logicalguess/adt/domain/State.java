package logicalguess.adt.domain;

import java.util.Objects;

public class State {
    public final boolean locked;
    public final int candies;
    public final int coins;

    public State(boolean locked, int candies, int coins) {
        this.locked = locked;
        this.candies = candies;
        this.coins = coins;
    }

    @Override
    public String toString() {
        return "State{" +
                "locked=" + locked +
                ", candies=" + candies +
                ", coins=" + coins +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return locked == state.locked &&
                candies == state.candies &&
                coins == state.coins;
    }

    @Override
    public int hashCode() {
        return Objects.hash(locked, candies, coins);
    }
}
