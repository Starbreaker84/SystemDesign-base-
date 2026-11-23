import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class ImprovedDateTimeFormatter {

    // УЛУЧШЕНИЕ 1 (Потокобезопасность):
    // В отличие от SimpleDateFormat, DateTimeFormatter является неизменяемым.
    // Его можно безопасно вынести в static final константу и использовать из разных потоков без гонок данных.
    //
    // УЛУЧШЕНИЕ 2 (Строгая валидация):
    // ResolverStyle.STRICT запрещает «тихое» исправление несуществующих дат (например, 31 февраля не превратится в 2 марта).
    // Символ 'u' используется вместо 'y' для корректной работы строгого режима без указания эры (AD/BC).
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        String dateString = "2024-05-13 14:30:00";

        try {
            // УЛУЧШЕНИЕ 3 (Точный выбор типа и отбрасывание лишних данных):
            // Вместо устаревшего java.util.Date, который всегда хранит время в миллисекундах и зависит
            // от системного часового пояса (что может приводить к сдвигу даты на день назад/вперёд),
            // используем LocalDate. Метод parse() проверяет всю входную строку по шаблону,
            // но извлекает только календарную дату (2024-05-13), отбрасывая время.
            LocalDate parsedDate = LocalDate.parse(dateString, INPUT_FORMATTER);

            // УЛУЧШЕНИЕ 4 (Неизменяемость):
            // Объект LocalDate нельзя случайно изменить через сеттеры (как Date.setTime()).
            // Любые операции (например, plusDays) возвращают новый экземпляр.
            System.out.println("Date: " + parsedDate);

        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
    }
}
