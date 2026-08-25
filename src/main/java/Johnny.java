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

    private static void printTaskAdded(Task task, int taskCount) {
        printIndented("Got it. I've added this task:");
        printIndented("  " + task);
        printIndented("Now you have " + taskCount + " tasks in the list.");
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        java.util.ArrayList<Task> tasks = new java.util.ArrayList<>();

        printLine();
        System.out.print(BANNER);
        printIndented("Hello! I'm Johnny.");
        printIndented("What can I do for you?");
        printLine();

        while (true) {
            String input = scanner.nextLine();
            printLine();
            try {
                if (input.equals("bye")) {
                    printIndented("Bye bye! See you again soon.");
                } else if (input.equals("list")) {
                    printIndented("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        printIndented((i + 1) + "." + tasks.get(i));
                    }
                } else if (input.startsWith("mark ")) {
                    int index = Integer.parseInt(input.substring(5)) - 1;
                    tasks.get(index).markAsDone();
                    printIndented("Nice! I've marked this task as done:");
                    printIndented("  " + tasks.get(index));
                } else if (input.startsWith("unmark ")) {
                    int index = Integer.parseInt(input.substring(7)) - 1;
                    tasks.get(index).markAsNotDone();
                    printIndented("OK, I've marked this task as not done yet:");
                    printIndented("  " + tasks.get(index));
                } else if (input.startsWith("delete ")) {
                    int index = Integer.parseInt(input.substring(7)) - 1;
                    Task removed = tasks.remove(index);
                    printIndented("Noted. I've removed this task:");
                    printIndented("  " + removed);
                    printIndented("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String desc = input.substring(4).trim();
                    if (desc.isEmpty()) {
                        throw new JohnnyException("The description of a todo cannot be empty.");
                    }
                    tasks.add(new Todo(desc));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.startsWith("deadline ")) {
                    String rest = input.substring(9);
                    int byIndex = rest.indexOf(" /by ");
                    String desc = rest.substring(0, byIndex);
                    String by = rest.substring(byIndex + 5);
                    tasks.add(new Deadline(desc, by));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.startsWith("event ")) {
                    String rest = input.substring(6);
                    int fromIndex = rest.indexOf(" /from ");
                    int toIndex = rest.indexOf(" /to ");
                    String desc = rest.substring(0, fromIndex);
                    String from = rest.substring(fromIndex + 7, toIndex);
                    String to = rest.substring(toIndex + 5);
                    tasks.add(new Event(desc, from, to));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else {
                    throw new JohnnyException("I'm sorry, but I'm not too sure what that means :(");
                }
            } catch (JohnnyException e) {
                printIndented("OOPS!!! " + e.getMessage());
            }
            printLine();
            if (input.equals("bye")) {
                break;
            }
        }

        scanner.close();
    }
}
