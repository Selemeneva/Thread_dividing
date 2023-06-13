import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static java.lang.System.out;


public class Main {
    private static final int FIRST_NUMBER_FIRST = 1;
    private static final int FIRST_NUMBER_LAST = 500;
    private static final int SECOND_NUMBER_FIRST = 501;
    private static final int SECOND_NUMBER_LAST = 1000;
    private static final String TEMPLATE_MESSAGE_THREAD_NAME_AND_NUMBER = "%s : %d\n";
    public static void main(String[] args) throws InterruptedException {

        //т.к. нам нужны ссылки на экземпляры потоков, firstTask и secondTask создадим через конструкторы
        final TaskSummingNumbers firstTask = new TaskSummingNumbers(FIRST_NUMBER_FIRST,FIRST_NUMBER_LAST);
        final Thread firstThread = new Thread(firstTask);
        firstThread.start();
        final TaskSummingNumbers secondTask = new TaskSummingNumbers(SECOND_NUMBER_FIRST, SECOND_NUMBER_LAST);
        final Thread secondThread = new Thread(secondTask);
        secondThread.start();

        // от потока main "отпочковываем" два потока, которые делят задачу на две
        //так создавали задачи для метода sleep()

        /*final TaskSummingNumbers firstTask = startSubTask(FIRST_NUMBER_FIRST,FIRST_NUMBER_LAST);
        final TaskSummingNumbers secondTask = startSubTask(SECOND_NUMBER_FIRST, SECOND_NUMBER_LAST);*/
        //в потоке main вывод будет 0, т.к. никакие вычисления не проводились
        waitForTasksFinished(firstThread,secondThread); //здесь даем потоку main подождать, когда выполнятся вычисления в потоках 0 и 1
        final int resultNumber = firstTask.resultNumber + secondTask.resultNumber;
        printThreadNameAndNumber(resultNumber);
    }
    //для метода join уже не нужен этот метод
/*    private static TaskSummingNumbers startSubTask(final int fromNumber, final int toNumber) {
        final TaskSummingNumbers subTask = new TaskSummingNumbers(fromNumber, toNumber);
        final Thread thread = new Thread(subTask);
        thread.start();
        return subTask;
    }*/
    private static void printThreadNameAndNumber(final int number) { //печать по шаблону
        out.printf(TEMPLATE_MESSAGE_THREAD_NAME_AND_NUMBER, Thread.currentThread().getName(), number);
    }

    //меняем логику засыпания: текущий поток main будет остановлен, пока thread.join не закончит работу
    //то есть, main ждет, пока все потоки выполнятся, потом возобновляет работу и суммирует рез-ты
    private static void waitForTasksFinished(final Thread...threads) throws InterruptedException {
        for (final Thread thread: threads) {
            thread.join();
        }
    }

   /* private static void waitForTasksFinished() throws InterruptedException { //логика "засыпания" потока
        Thread.sleep(1000);
    }*/

    private static void startThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    private static final class TaskSummingNumbers implements Runnable {
        private static final int INITIAL_RESULT_NUMBER = 0;
        private final int fromNumber;
        private final int toNumber;
        private int resultNumber;

        public TaskSummingNumbers(int fromNumber, int toNumber) {
            this.fromNumber = fromNumber;
            this.toNumber = toNumber;
            this.resultNumber = INITIAL_RESULT_NUMBER;
        }
        public int getResultNumber() {
            return this.resultNumber;
        }
        @Override
        public void run() {
            IntStream.rangeClosed(this.fromNumber, this.toNumber).forEach(i -> this.resultNumber += i);
            printThreadNameAndNumber(this.resultNumber);

        }
    }
}