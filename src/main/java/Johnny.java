public class Johnny {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "     _       _                       \n"
            + "    | | ___ | |__  _ __  _ __  _   _ \n"
            + " _  | |/ _ \\| '_ \\| '_ \\| '_ \\| | | |\n"
            + "| |_| | (_) | | | | | | | | | | |_| |\n"
            + " \\___/ \\___/|_| |_|_| |_|_| |_|\\__, |\n"
            + "                                |___/ \n";

    private static void printLine() {
        System.out.println("    " + LINE);
    }

    private static void printIndented(String message) {
        System.out.println("     " + message);
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        printLine();
        System.out.print(BANNER);
        printIndented("Hello! I'm Johnny.");
        printIndented("What can I do for you?");
        printLine();

        while (true) {
            String input = scanner.nextLine();
            printLine();
            if (input.equals("bye")) {
                printIndented("Bye bye! See you again soon.");
            } else {
                printIndented(input);
            }
            printLine();
            if (input.equals("bye")) {
                break;
            }
        }

        scanner.close();
    }
}
