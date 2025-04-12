---
order: 1
title: ВЫЗОВ ХРАНИМЫХ ПРОЦЕДУР ИЗ КЛИЕНТСКИХ ПРИЛОЖЕНИЙ
---

**Цель работы: Получить навыки вызова хранимых процедур из клиентских приложений БД в языке программирования java.**

## ***Что такое хранимая процедура?***

Хранимой процедурой (ХП) называют подпрограмму, написанную на расширенной версии языка SQL или ином языке, которая хранится в БД на сервере и выполняется в адресном пространстве сервера в случае вызова ее со стороны клиентского приложения. Использование ХП имеет следующие основные преимущества: 

-   возможность организовать обработку данных непосредственно на сервере БД, что сокращает трафик, передаваемый по сети; 

-   улучшение централизации информационной системы, за счет чего проще вносить изменения в алгоритмы обработки данных; 

-  повышение безопасности данных, так как появляется возможность запретить пользователю непосредственное выполнение операторов INSERT, UPDATE и DELETE. 

Исходные данные передаются из клиентского приложения в ХП в виде входных параметров. Результат выполнения ХП можно передать в клиентское приложение следующими основными способами: 

-  путем возврата из ХП в клиентское приложение выходных параметров; 

-  путем возврата из ХП в клиентское приложение кода завершения ХП с помощью оператора RETURN; 

-  путем возврата из ХП в клиентское приложение результирующего набора данных, полученного при выполнении оператора SELECT в теле ХП. 

Возможна также комбинация перечисленных выше способов. Кроме того, можно запускать процедуры, не принимающие и не возвращающие никаких данных

## Вызов ХП в java приложении

Предположим, что мы хотим создать хранимую процедуру, которая будет возвращать информацию о студенте и его экзаменах, а также средний балл студента по всем его экзаменам. Процедура будет принимать `student_number` как входной параметр и возвращать:

-  `student_name`: полное имя студента.

-  `average_mark`: средний балл по всем экзаменам.

-  `exam_info`: список всех экзаменов студента с указанием даты и оценки.

Вот пример создания такой хранимой процедуры:

```sql
CREATE OR REPLACE PROCEDURE get_student_exam_info(
    student_number INT,
    OUT student_name VARCHAR,
    OUT average_mark DECIMAL,
    OUT exam_info TEXT
)
LANGUAGE plpgsql AS $$
BEGIN
    -- Получаем полное имя студента
    SELECT full_name INTO student_name
    FROM student
    WHERE number = student_number;

    -- Получаем средний балл по всем экзаменам студента
    SELECT AVG(mark) INTO average_mark
    FROM exam
    WHERE student = student_number;

    -- Собираем информацию о всех экзаменах студента
    SELECT string_agg(
        'Предмет: ' || s.full_name || ', Дата: ' || e.date_of_exam || ', Оценка: ' || e.mark, 
        '; '
    ) INTO exam_info
    FROM exam e
    JOIN subject s ON e.subject = s.number
    WHERE e.student = student_number;

    -- Если у студента нет экзаменов, возвращаем пустую строку
    IF exam_info IS NULL THEN
        exam_info := 'Нет экзаменов';
    END IF;
END;
$$;
```

### Пример вызова хранимой процедуры через JDBC

Для вызова этой процедуры в Java через JDBC вы должны использовать `CallableStatement`, как показано в вашем первоначальном коде, с учётом того, что она теперь является процедурой:

```java
import java.math.BigDecimal;
import java.sql.*;

public class StoredProc {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/short_db_session";

    static final String USER = "your_user_name";

    static final String PASSWORD = "your_password";

    public static void main(String[] args) {

        int studentNumber = 1; // Пример студента с номером 1

        String query = "{ call get_student_exam_info(?, ?, ?, ?) }";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(query)) {

            // Устанавливаем входной параметр
            stmt.setInt(1, studentNumber);

            // Регистрируем выходные параметры
            stmt.registerOutParameter(2, Types.VARCHAR);  // student_name
            stmt.registerOutParameter(3, Types.DECIMAL);  // average_mark
            stmt.registerOutParameter(4, Types.VARCHAR);  // exam_info

            // Выполняем запрос
            stmt.execute();

            // Извлекаем результаты
            String studentName = stmt.getString(2);
            BigDecimal averageMark = stmt.getBigDecimal(3);
            String examInfo = stmt.getString(4);

            // Выводим результаты
            System.out.println("Студент: " + studentName);
            System.out.println("Средний балл: " + averageMark);
            System.out.println("Информация об экзаменах: " + examInfo);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

#### 1\. **Подключение к базе данных**

```java
String url = "jdbc:postgresql://localhost:5432/short_db_session"; 
String user = "your_user"; 
String password = "your_password";
```

-  **Что происходит**: Здесь мы задаем параметры подключения к базе данных PostgreSQL. В строке подключения `url` указаны:

   -  `jdbc:postgresql` -- протокол для подключения к PostgreSQL через JDBC.

   -  [`localhost`](http://localhost) -- сервер базы данных, который находится на локальной машине.

   -  `5432` -- стандартный порт для PostgreSQL.

   -  `short_db_session` -- имя базы данных, к которой мы подключаемся.

-  Переменные `user` и `password` содержат учетные данные для подключения к базе данных.

#### 2\. **Определение запроса для вызова хранимой процедуры**

```java
String query = "{ call get_student_exam_info(?, ?, ?, ?) }";
```

-  **Что происходит**: Мы определяем строку запроса для вызова хранимой процедуры `get_student_exam_info`.

-  **Формат вызова хранимой процедуры**: В PostgreSQL для вызова хранимой процедуры через JDBC используется следующий синтаксис:

   -  `{ call procedure_name(?, ?, ?, ?) }` -- это форма вызова, где `?` -- это placeholders (место для параметров), которые будут заполнены позже в процессе выполнения кода.

-  Здесь:

   -  `?` -- это параметры, которые мы передаем в хранимую процедуру:

      -  Первый параметр -- это номер студента (входной параметр).

      -  Второй, третий и четвертый параметры -- это выходные параметры, которые вернет процедура.

#### 3\. **Создание подключения и подготовка CallableStatement**

```java
try (Connection conn = DriverManager.getConnection(url, user, password); 
    CallableStatement stmt = conn.prepareCall(query)) {
```

-  **Что происходит**:

   -  Сначала создается соединение с базой данных через `DriverManager.getConnection(url, user, password)`. Это подключение будет использоваться для отправки запросов в базу данных.

   -  В строке `conn.prepareCall(query)` создается объект `CallableStatement`, который позволяет выполнять хранимые процедуры. Метод `prepareCall()` подготавливает SQL-запрос (в данном случае вызов процедуры), который затем будет выполнен.

#### 4\. **Установка входных параметров**

```java
stmt.setInt(1, studentNumber);
```

-  **Что происходит**: Мы передаем входной параметр в хранимую процедуру.

   -  `stmt.setInt(1, studentNumber)` -- это метод для установки целочисленного значения в первый параметр хранимой процедуры (номер студента).

   -  `1` -- это индекс параметра (нумерация параметров начинается с 1). Мы передаем значение `studentNumber` (например, 1), которое представляет номер студента.

#### 5\. **Регистрация выходных параметров**

```java
stmt.registerOutParameter(2, Types.VARCHAR); // student_name 
stmt.registerOutParameter(3, Types.DECIMAL); // average_mark 
stmt.registerOutParameter(4, Types.VARCHAR); // exam_info
```

-  **Что происходит**: Мы регистрируем выходные параметры, которые процедура будет возвращать после выполнения.

   -  `stmt.registerOutParameter(2, Types.VARCHAR)` -- регистрируем второй параметр как строку (`VARCHAR`), который будет содержать имя студента.

   -  `stmt.registerOutParameter(3, Types.DECIMAL)` -- регистрируем третий параметр как число с плавающей запятой (`DECIMAL`), которое будет содержать средний балл студента.

   -  `stmt.registerOutParameter(4, Types.VARCHAR)` -- регистрируем четвертый параметр как строку (`VARCHAR`), который будет содержать информацию о всех экзаменах студента.

#### 6\. **Выполнение запроса**

```java
stmt.execute();
```

-  **Что происходит**: Мы вызываем хранимую процедуру с помощью `execute()`.

   -  Метод `execute()` выполняет подготовленный запрос (в данном случае хранимую процедуру). В этот момент данные из базы данных обрабатываются, и результаты записываются в выходные параметры, которые мы зарегистрировали.

#### 7\. **Извлечение выходных параметров**

```java
String studentName = stmt.getString(2); 
BigDecimal averageMark = stmt.getBigDecimal(3); 
String examInfo = stmt.getString(4);
```

-  **Что происходит**:

   -  После выполнения хранимой процедуры мы извлекаем данные, которые были возвращены через выходные параметры:

      -  `stmt.getString(2)` -- извлекаем имя студента из второго выходного параметра (тип `VARCHAR`).

      -  `stmt.getBigDecimal(3)` -- извлекаем средний балл студента из третьего выходного параметра (тип `DECIMAL`).

      -  `stmt.getString(4)` -- извлекаем информацию о экзаменах студента из четвертого выходного параметра (тип `VARCHAR`).

#### 8\. **Вывод результатов**

```java
System.out.println("Студент: " + studentName); 
System.out.println("Средний балл: " + averageMark); 
System.out.println("Информация об экзаменах: " + examInfo);
```

-  **Что происходит**: Мы выводим результаты на консоль:

   -  Имя студента, средний балл и информация о экзаменах.

   -  Эти данные были извлечены из выходных параметров, возвращенных хранимой процедурой.

#### 9\. **Обработка ошибок**

```java
catch (SQLException e) { e.printStackTrace(); }
```

-  **Что происходит**: Если возникает ошибка при выполнении SQL-запроса или подключении к базе данных, она будет перехвачена блоком `catch`. В случае ошибки мы выводим стек вызовов с помощью `e.printStackTrace()`, чтобы понять, что пошло не так.