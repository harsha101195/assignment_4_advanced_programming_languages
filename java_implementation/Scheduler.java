import java.util.*;

public class Scheduler {
    static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    static final String[] SHIFTS = {"morning", "afternoon", "evening"};

    Map<String, Map<String, List<String>>> preferences = new HashMap<>();
    Map<String, Map<String, List<String>>> schedule = new HashMap<>();
    Map<String, Integer> daysWorked = new HashMap<>();

    public Scheduler() {
        for (String day : DAYS) {
            schedule.put(day, new HashMap<>());
            for (String shift : SHIFTS) {
                schedule.get(day).put(shift, new ArrayList<>());
            }
        }
    }

    public void addEmployeePreference(String name, String day, String shift) {
        preferences.putIfAbsent(name, new HashMap<>());
        preferences.get(name).putIfAbsent(day, new ArrayList<>());
        preferences.get(name).get(day).add(shift);
    }

    public void assignShifts() {
        Random rand = new Random();

        for (String day : DAYS) {
            for (String shift : SHIFTS) {
                List<String> candidates = new ArrayList<>();

                for (String emp : preferences.keySet()) {
                    if (preferences.get(emp).getOrDefault(day, new ArrayList<>()).contains(shift)
                            && daysWorked.getOrDefault(emp, 0) < 5
                            && !alreadyAssigned(day, emp)) {
                        candidates.add(emp);
                    }
                }

                Collections.shuffle(candidates);
                List<String> assigned = candidates.subList(0, Math.min(2, candidates.size()));
                for (String emp : assigned) {
                    schedule.get(day).get(shift).add(emp);
                    daysWorked.put(emp, daysWorked.getOrDefault(emp, 0) + 1);
                }

                while (schedule.get(day).get(shift).size() < 2) {
                    List<String> available = new ArrayList<>();
                    for (String emp : preferences.keySet()) {
                        if (daysWorked.getOrDefault(emp, 0) < 5 && !alreadyAssigned(day, emp)) {
                            available.add(emp);
                        }
                    }
                    if (available.isEmpty()) break;
                    String pick = available.get(rand.nextInt(available.size()));
                    schedule.get(day).get(shift).add(pick);
                    daysWorked.put(pick, daysWorked.getOrDefault(pick, 0) + 1);
                }
            }
        }
    }

    private boolean alreadyAssigned(String day, String emp) {
        for (String shift : SHIFTS) {
            if (schedule.get(day).get(shift).contains(emp)) return true;
        }
        return false;
    }

    public void printSchedule() {
        for (String day : DAYS) {
            System.out.println("\n" + day + ":");
            for (String shift : SHIFTS) {
                List<String> emps = schedule.get(day).get(shift);
                System.out.println("  " + shift + ": " + (emps.isEmpty() ? "Employees Unavailable" : String.join(", ", emps)));
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Scheduler scheduler = new Scheduler();
        List<String> employees = new ArrayList<>();
        System.out.print("Enter number of employees: ");
        int n = Integer.parseInt(scanner.nextLine().trim());
        for (int i = 0; i < n; i++) {
            String emp;
            while (true) {
                System.out.print("Enter name of employee #" + (i+1) + ": ");
                emp = scanner.nextLine().trim();
                if (emp.isEmpty() || employees.contains(emp)) {
                    System.out.println("Invalid or duplicate name. Please enter a unique employee name.");
                } else {
                    break;
                }
            }
            employees.add(emp);
        }
        System.out.println("\nEnter preferences (leave blank to skip):");
        for (String emp : employees) {
            System.out.print("Does " + emp + " have any shift preferences? (y/n): ");
            String hasPref = scanner.nextLine().trim().toLowerCase();
            if (hasPref.equals("y")) {
                for (String day : DAYS) {
                    for (String shift : SHIFTS) {
                        System.out.print("Does " + emp + " prefer " + shift + " on " + day + "? (y/n, blank to skip): ");
                        String pref = scanner.nextLine().trim().toLowerCase();
                        if (pref.equals("y")) {
                            scheduler.addEmployeePreference(emp, day, shift);
                        }
                    }
                }
            }
        }
        for (String emp : employees) {
            scheduler.preferences.putIfAbsent(emp, new HashMap<>());
        }
        scheduler.assignShifts();
        scheduler.printSchedule();
    }
}
