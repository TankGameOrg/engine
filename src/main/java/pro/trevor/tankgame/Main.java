package pro.trevor.tankgame;

import pro.trevor.tankgame.rule.impl.IRepl;
import pro.trevor.tankgame.rule.impl.version3.Repl;

import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {
        File setup = new File("example/initial.txt");
        File moves = new File("example/moves.json");
        try {
            IRepl repl = new Repl(new FileInputStream(setup), new FileInputStream(moves));
            while (!repl.isDone()) {
                repl.handleLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}