import objects.library.dao.AuthorDAO;
import objects.library.dao.BookDAO;
import objects.library.dao.BorrowingDAO;
import objects.library.dao.UserDAO;
import objects.library.model.Author;
import objects.library.model.Book;
import objects.library.model.Borrowing;
import objects.library.model.User;
import objects.library.util.DBUtil;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("Успешное подключение к базе данных.");

            AuthorDAO authorDAO = new AuthorDAO(conn);
            BookDAO bookDAO = new BookDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            BorrowingDAO borrowingDAO = new BorrowingDAO(conn);

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n===== Меню библиотеки =====");
                System.out.println("\n===== Авторы =====");
                System.out.println("1. Показать всех авторов");
                System.out.println("2. Добавить нового автора");
                System.out.println("\n===== Книги =====");
                System.out.println("3. Показать все книги");
                System.out.println("4. Добавить новую книгу");
                System.out.println("\n===== Пользователи =====");
                System.out.println("5. Добавить нового пользователя");
                System.out.println("6. Показать всех пользователей");
                System.out.println("\n===== Заимствование =====");
                System.out.println("7. Выдать книгу");
                System.out.println("8. Показать все заимствования");
                System.out.println("9. Вернуть книгу");
                System.out.println();
                System.out.println("0. Выход");
                System.out.print("Выберите опцию: ");

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> {
                        List<Author> authors = authorDAO.findAll();
                        authors.forEach(System.out::println);
                    }
                    case 2 -> {
                        System.out.print("Введите имя автора: ");
                        String name = scanner.nextLine();
                        Author author = new Author(0, name);
                        authorDAO.save(author);
                        System.out.println("Автор добавлен: " + author);
                    }
                    case 3 -> {
                        List<Book> books = bookDAO.findAll();
                        books.forEach(System.out::println);
                    }
                    case 4 -> {
                        System.out.print("Введите название книги: ");
                        String title = scanner.nextLine();

                        System.out.print("Введите ID автора: ");
                        int authorId = Integer.parseInt(scanner.nextLine());
                        Author author = authorDAO.findById(authorId);
                        if (author == null) {
                            System.out.println("Автор не найден.");
                        } else {
                            Book book = new Book(0, title, author, true);
                            bookDAO.save(book);
                            System.out.println("Книга добавлена: " + book);
                        }
                    }
                    case 5 -> {
                        System.out.print("Введите имя пользователя: ");
                        String name = scanner.nextLine();
                        User user = new User(0, name);
                        userDAO.save(user);
                        System.out.println("Пользователь добавлен: " + user);
                    }
                    case 6 -> {
                        List<User> users = userDAO.findAll();
                        if (users.isEmpty()) {
                            System.out.println("Пользователи не найдены.");
                        } else {
                            System.out.println("Список пользователей:");
                            users.forEach(System.out::println);
                        }
                    }
                    case 7 -> {
                        System.out.print("Введите ID пользователя: ");
                        int userId = Integer.parseInt(scanner.nextLine());
                        User user = userDAO.findById(userId);
                        if (user == null) {
                            System.out.println("Пользователь не найден.");
                        } else {
                            System.out.print("Введите ID книги: ");
                            int bookId = Integer.parseInt(scanner.nextLine());
                            Book book = bookDAO.findById(bookId);
                            if (book == null || !book.isAvailable()) {
                                System.out.println("Книга не найдена или она недоступна для выдачи.");
                            } else {
                                System.out.print("Введите дату возврата (year-month-day), если нет - оставьте пустым: ");
                                String returnDateInput = scanner.nextLine();
                                LocalDate returnDate = returnDateInput.isEmpty() ? null : LocalDate.parse(returnDateInput);
                                Borrowing borrowing = new Borrowing(0, user, book, LocalDate.now(), returnDate);
                                borrowingDAO.save(borrowing);
                                book.setAvailable(false); // Изменить статус книги на недоступную
                                bookDAO.update(book); // Сохранить изменения в БД
                                System.out.println("Книга выдана: " + borrowing);
                            }
                        }
                    }
                    case 8 -> {
                        List<Borrowing> borrowings = borrowingDAO.findAll();
                        borrowings.forEach(System.out::println);
                    }

                    case 9 -> {
                        System.out.print("Введите ID заимствования для возврата книги: ");
                        int borrowingId = Integer.parseInt(scanner.nextLine());
                        Borrowing borrowing = borrowingDAO.findById(borrowingId);

                        if (borrowing == null) {
                            System.out.println("Заимствование не найдено.");
                        } else if (borrowing.getReturnDate() != null) {
                            System.out.println("Книга уже возвращена.");
                        } else {
                            LocalDate today = LocalDate.now();
                            borrowing.setReturnDate(today);
                            borrowingDAO.updateReturnDate(borrowingId, today);

                            Book bookToReturn = borrowing.getBook();
                            bookToReturn.setAvailable(true);
                            bookDAO.update(bookToReturn);

                            System.out.println("Книга успешно возвращена: " + bookToReturn.getTitle());
                        }
                    }
                    case 0 -> {
                        running = false;
                        System.out.println("Выход из программы.");
                    }
                    default -> System.out.println("Неверный ввод. Повторите.");
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
