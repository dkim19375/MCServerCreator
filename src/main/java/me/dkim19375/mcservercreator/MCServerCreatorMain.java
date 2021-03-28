package me.dkim19375.mcservercreator;

import me.dkim19375.mcservercreator.controller.OptionsController;

import java.util.Scanner;
import java.util.concurrent.Executors;

public class MCServerCreatorMain {
    public static void main(String[] args) {
        System.out.println("testtt");
        Executors.newSingleThreadExecutor().submit(() -> {
            System.out.println("test");
        });
        // MCServerCreator.main(args);
    }
}