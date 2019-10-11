package logicalguess.adt;

import io.vavr.Tuple;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Tuple2;

public class CandyMachine {

    enum Input {
        Coin,
        Turn,
        Exit
    }

    static class State {
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

    static BiFunction<Input, State, State> update = (input, state) ->
            Match(Tuple.of(input, state)).of(
                    Case($Tuple2($(Input.Exit), $()), (in1, s1) -> s1),
                    Case($Tuple2($(), $(s -> s.candies == 0)), (in1, s1) -> s1),
                    Case($Tuple2($(Input.Coin), $(s -> s.locked == false)), (in1, s1) -> s1),
                    Case($Tuple2($(Input.Turn), $(s -> s.locked == true)), (in1, s1) -> s1),
                    Case($Tuple2($(Input.Coin), $(s -> s.locked == true)), (in1, s1) -> new State(false, s1.candies, s1.coins + 1)),
                    Case($Tuple2($(Input.Turn), $(s -> s.locked == false)), (in1, s1) -> new State(true, s1.candies - 1, s1.coins)),
                    Case($Tuple2($(), $()), (in1, s1) -> s1)

            );

    static Function<Character, Input> interpretInput = c ->
            Match(c.charValue()).of(
                    Case($('c'), Input.Coin),
                    Case($('t'), Input.Turn),
                    Case($('x'), Input.Exit),
                    Case($(), (Input) null)
            );

    static Function<Input, Boolean> evaluateInput = input ->
            Match(input).of(
                    Case($(Input.Exit), false),
                    Case($(), true)
            );


    static Supplier<Character> getInput = () -> {
        System.out.println("Please enter an input from: 'c', 't', or 'x'");
        Scanner scanner = new Scanner(System.in);
        return scanner.next().charAt(0);
    };

    static Consumer<State> processLoop = s -> {
        System.out.println(s);
        char c = getInput.get();
        Input input = interpretInput.apply(Character.valueOf(c));
        boolean loop = evaluateInput.apply(input);
        if (loop) CandyMachine.processLoop.accept(update.apply(input, s));
    };

    public static void main(String[] args) throws IOException {
        State state = new State(true, 5, 10);
        processLoop.accept(state);
    }
}
