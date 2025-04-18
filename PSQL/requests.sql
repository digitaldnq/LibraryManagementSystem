-- Для вывода всех авторов и их книг

SELECT 
    a.id AS author_id,
    a.name AS author_name,
    b.id AS book_id,
    b.title AS book_title,
    b.is_available
FROM authors a
LEFT JOIN books b ON a.id = b.author_id
ORDER BY a.id, b.id;

-- Для вывода всех пользователей и их заимствования

SELECT 
    u.id AS user_id,
    u.name AS user_name,
    b.id AS book_id,
    b.title AS book_title,
    br.borrow_date,
    br.return_date
FROM users u
LEFT JOIN borrowings br ON u.id = br.user_id
LEFT JOIN books b ON br.book_id = b.id
ORDER BY u.id, br.borrow_date;
