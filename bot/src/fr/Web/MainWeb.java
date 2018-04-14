package fr.Web;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Scanner;

public class MainWeb {
    public static void main(String[] args) throws Exception {
        Timer timer;
        boolean stop=false;
        timer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.out.println("ok");
            }
        });
        timer.start();
        while (!stop) {
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.next();
            if (cmd.equalsIgnoreCase("stop")) {
                stop=true;
            }
        }
    }
}
