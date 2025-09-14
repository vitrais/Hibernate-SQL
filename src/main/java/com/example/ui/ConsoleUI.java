package com.example.ui;

import com.example.dao.UserDAO;
import com.example.dao.impl.UserDAOImpl;
import com.example.entity.User;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final UserDAO userDAO = new UserDAOImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        printWelcome();
        while (true) {
            printMenu();
            int choice = readInt("Выберите пункт: ");
            handleChoice(choice);
            if (choice == 0) break;
        }
        scanner.close();
    }

    // Основные методы интерфейса
    // ===============================================

    private void printWelcome() {
        System.out.println("=================================");
        System.out.println("  Управление пользователями v1.0 ");
        System.out.println("=================================\n");
    }

    private void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Добавить пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Обновить данные пользователя");
        System.out.println("4. Удалить пользователя");
        System.out.println("5. Показать всех пользователей");
        System.out.println("0. Выход");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> addUser();
            case 2 -> findUserById();
            case 3 -> updateUser();
            case 4 -> deleteUser();
            case 5 -> listAllUsers();
            case 0 -> System.out.println("Завершение работы...");
            default -> System.out.println("Неверный выбор! Попробуйте снова.");
        }
    }

    // Методы работы с данными
    // ===============================================

    public void addUser() {
        String name;
        do {
            name = getInput("Введите имя: ");
            if (name.trim().isEmpty()) {
                System.out.println("❌ Поле не может быть пустым!");
            }
        } while (name.trim().isEmpty());

        // Запрос email
        String email = getInput("Введите email: ");

        // Запрос возраста
        int age = getAgeInput();

        // Создание объекта
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userDAO.save(user);
    }


    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int getAgeInput() {
        while (true) {
            try {
                String input = getInput("Введите возраст: ");
                int age = Integer.parseInt(input);
                if (age < 0) throw new NumberFormatException();
                return age;
            } catch (NumberFormatException e) {
                System.out.println("❌ Возраст должен быть целым числом ≥ 0!");
            }
        }
    }

    private void findUserById() {
        System.out.println("\n--- Поиск пользователя ---");
        long id = readId();
        User user = userDAO.findById(id);

        if (user != null) {
            printUserHeader();
            printUser(user);
        } else {
            System.out.println("❌ Пользователь с ID " + id + " не найден");
        }
    }

    private void updateUser() {
        System.out.println("\n--- Обновление данных ---");
        long id = readId();
        User user = userDAO.findById(id);

        if (user == null) {
            System.out.println("❌ Пользователь не найден");
            return;
        }

        String newName = readNonEmptyString("Текущее имя: " + user.getName() + "\nНовое имя (оставьте пустым, чтобы не менять): ");
        String newEmail = readEmail("Текущий email: " + user.getEmail() + "\nНовый email (оставьте пустым, чтобы не менять): ");

        if (!newName.isEmpty()) user.setName(newName);
        if (!newEmail.isEmpty()) user.setEmail(newEmail);

        try {
            userDAO.update(user);
            System.out.println("✅ Данные обновлены!");
        } catch (Exception e) {
            System.err.println("⛔ Ошибка: " + e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.println("\n--- Удаление пользователя ---");
        long id = readId();
        try {
            userDAO.delete(id);
            System.out.println("✅ Пользователь удалён");
        } catch (Exception e) {
            System.err.println("⛔ Ошибка: " + e.getMessage());
        }
    }

    private void listAllUsers() {
        System.out.println("\n--- Список пользователей ---");
        List<User> users = userDAO.findAll();

        if (users.isEmpty()) {
            System.out.println("😞 Нет данных для отображения");
            return;
        }

        printUserHeader();
        users.forEach(this::printUser);
        System.out.println("Всего записей: " + users.size());
    }

    // Вспомогательные методы
    // ===============================================

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("⚠️ Введите целое число: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private long readId() {
        while (true) {
            int id = readInt("Введите ID пользователя: ");
            if (id > 0) return id;
            System.out.println("❌ ID должен быть положительным числом");
        }
    }

    private String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("❌ Поле не может быть пустым!");
            }
        } while (input.isEmpty());
        return input;
    }

    private String readEmail(String prompt) {
        String email;
        do {
            System.out.print(prompt);
            email = scanner.nextLine().trim();
            if (!email.matches("^[\\w.-]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$")) {
                System.out.println("❌ Неверный формат email!");
            }
        } while (!email.matches("^[\\w.-]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$"));
        return email;
    }

    private void printUserHeader() {
        System.out.printf("\n%-5s | %-20s | %-30s | %-7s | %-15s%n", "ID", "Имя", "Email", "Возраст", "Создан");
        System.out.println("-------------------------------------------------------------------------------------");
    }

    private void printUser(User user) {
        System.out.printf("%-5d | %-20s | %-30s | %-7d | %s%n",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt());
    }
}

