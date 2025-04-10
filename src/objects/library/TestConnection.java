package objects.library;

import objects.library.util.DBUtil;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {

        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("Успешное подключение к БД.");
        } catch (Exception e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }
}