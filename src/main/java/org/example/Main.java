package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class Main {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8787)) {
            System.out.println("Сервер запущен!");
            String city = null;

            while (true) {
                Thread thread = new Thread(() -> {
                    try (Socket clientSocket = new Socket("localhost", 8787);
                         PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.println(reader.readLine());
                        String input = scanner.nextLine();
                        writer.println(input);
                        String answer = reader.readLine();
                        if (answer != null) {
                            System.out.println(answer);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                thread.start();

                try (Socket client = serverSocket.accept();// ждем подключения
                     PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
                ) {
                    if (city == null) {
                        out.println("Вы первый игрок! Введите город и нажминете Enter");
                        city = in.readLine().toLowerCase();
                    } else {
                        out.println("Последний введенный город " + StringUtils.capitalize(city) + " Вам на - " + city.toUpperCase().charAt(city.length() - 1));
                        String newCity = in.readLine().toLowerCase();
                        if (city.endsWith(String.valueOf(newCity.charAt(0)))) {
                            out.println("Ваш город принят!");
                            city = newCity;
                        } else {
                            out.println("Неверно! Ваш город НЕ принят!");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}