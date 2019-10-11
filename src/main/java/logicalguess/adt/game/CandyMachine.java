package logicalguess.adt.game;

import io.vavr.Tuple2;
import logicalguess.adt.domain.Event;
import logicalguess.adt.domain.Input;
import logicalguess.adt.domain.State;

import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vavr.Tuple;
import lombok.extern.slf4j.Slf4j;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Tuple2;

@Slf4j
public class CandyMachine {

    static BiFunction<Input, State, Tuple2<State, Event>> update = (input, state) ->
            Match(Tuple.of(input, state)).of(
                    Case($Tuple2($(Input.Exit), $()), (in1, s1) -> new Tuple2(s1, Event.Exited)),
                    Case($Tuple2($(), $(s -> s.candies == 0)), (in1, s1) -> new Tuple2(s1, Event.InputIgnored)),
                    Case($Tuple2($(Input.Coin), $(s -> s.locked == false)), (in1, s1) -> new Tuple2(s1, Event.InputIgnored)),
                    Case($Tuple2($(Input.Turn), $(s -> s.locked == true)), (in1, s1) -> new Tuple2(s1, Event.InputIgnored)),
                    Case($Tuple2($(Input.Coin), $(s -> s.locked == true)), (in1, s1) -> new Tuple2(new State(false, s1.candies, s1.coins + 1), Event.CoinReceived)),
                    Case($Tuple2($(Input.Turn), $(s -> s.locked == false)), (in1, s1) -> new Tuple2(new State(true, s1.candies - 1, s1.coins), Event.CandyReleased)),
                    Case($Tuple2($(), $()), (in1, s1) -> new Tuple2(s1, Event.InputIgnored))

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
        log.debug(s.toString());
        char c = getInput.get();
        Input input = interpretInput.apply(Character.valueOf(c));
        boolean loop = evaluateInput.apply(input);
        Tuple2<State, Event> output = update.apply(input, s);
        log.info(output._2.toString());
        if (loop)
            CandyMachine.processLoop.accept(output._1);
    };

    public static void main(String[] args) {
        processLoop.accept(new State(true, 5, 10));
    }
}
