import random
from collections import defaultdict

DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
SHIFTS = ["morning", "afternoon", "evening"]

class Scheduler:
    def __init__(self):
        self.preferences = defaultdict(lambda: defaultdict(list))
        self.schedule = defaultdict(lambda: defaultdict(list))
        self.days_worked = defaultdict(int)

    def add_employee_preference(self, name, day, shift):
        if day in DAYS and shift in SHIFTS:
            self.preferences[name][day].append(shift)

    def assign_shifts(self):
        for day in DAYS:
            for shift in SHIFTS:
                candidates = [name for name, prefs in self.preferences.items()
                              if day in prefs and shift in prefs[day]
                              and self.days_worked[name] < 5
                              and name not in sum(self.schedule[day].values(), [])]
                random.shuffle(candidates)
                assigned = candidates[:2]

                for emp in assigned:
                    self.schedule[day][shift].append(emp)
                    self.days_worked[emp] += 1

                while len(self.schedule[day][shift]) < 2:
                    available = [name for name in self.preferences.keys()
                                 if self.days_worked[name] < 5
                                 and name not in sum(self.schedule[day].values(), [])]
                    if not available:
                        break
                    choice = random.choice(available)
                    self.schedule[day][shift].append(choice)
                    self.days_worked[choice] += 1

    def print_schedule(self):
        for day in DAYS:
            print(f"\n{day}:")
            for shift in SHIFTS:
                emps = self.schedule[day][shift]
                if len(emps) < 2:
                    print(f"  {shift.capitalize()}: Employees unavailable")
                else:
                    print(f"  {shift.capitalize()}: {', '.join(emps)}")

# Run the below code to use Scheduler via CLI

if __name__ == "__main__":
    scheduler = Scheduler()
    employees = []
    n = int(input("Enter number of employees: "))
    for i in range(n):
        emp = input(f"Enter name of employee #{i+1}: ").strip()
        while not emp or emp in employees:
            print("Invalid or duplicate name. Please enter a unique employee name.")
            emp = input(f"Enter name of employee #{i+1}: ").strip()
        employees.append(emp)
    print("\nEnter preferences (leave blank to skip):")
    for emp in employees:
        has_pref = input(f"Does {emp} have any shift preferences? (y/n): ").strip().lower()
        if has_pref == 'y':
            for day in DAYS:
                for shift in SHIFTS:
                    pref = input(f"Does {emp} prefer {shift} on {day}? (y/n, blank to skip): ").strip().lower()
                    if pref == 'y':
                        scheduler.add_employee_preference(emp, day, shift)
    for emp in employees:
        if emp not in scheduler.preferences:
            scheduler.preferences[emp]
    scheduler.assign_shifts()
    scheduler.print_schedule()
