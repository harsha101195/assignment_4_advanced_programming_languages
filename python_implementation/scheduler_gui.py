import tkinter as tk
from tkinter import ttk, messagebox
from scheduler import Scheduler, DAYS, SHIFTS

scheduler = Scheduler()

class SchedulerGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Employee Shift Preference Input")
        self.employees = []

        tk.Label(root, text="Add Employee:").grid(row=0, column=0, padx=5, pady=5)
        self.new_emp_var = tk.StringVar()
        self.new_emp_entry = tk.Entry(root, textvariable=self.new_emp_var)
        self.new_emp_entry.grid(row=0, column=1, padx=5, pady=5)
        self.add_emp_btn = tk.Button(root, text="Add", command=self.add_employee)
        self.add_emp_btn.grid(row=0, column=2, padx=5, pady=5)

        self.num_emp_label = tk.Label(root, text="Number of employees: 0")
        self.num_emp_label.grid(row=1, column=0, columnspan=3, padx=5, pady=5)

        tk.Label(root, text="Employee:").grid(row=2, column=0, padx=5, pady=5)
        self.employee_var = tk.StringVar()
        self.employee_combo = ttk.Combobox(root, textvariable=self.employee_var, values=self.employees, state="readonly")
        self.employee_combo.grid(row=2, column=1, padx=5, pady=5)

        tk.Label(root, text="Day:").grid(row=3, column=0, padx=5, pady=5)
        self.day_var = tk.StringVar()
        self.day_combo = ttk.Combobox(root, textvariable=self.day_var, values=DAYS, state="readonly")
        self.day_combo.grid(row=3, column=1, padx=5, pady=5)

        tk.Label(root, text="Shift:").grid(row=4, column=0, padx=5, pady=5)
        self.shift_var = tk.StringVar()
        self.shift_combo = ttk.Combobox(root, textvariable=self.shift_var, values=SHIFTS, state="readonly")
        self.shift_combo.grid(row=4, column=1, padx=5, pady=5)

        self.add_btn = tk.Button(root, text="Add Preference", command=self.add_preference)
        self.add_btn.grid(row=5, column=0, columnspan=2, pady=10)

        self.pref_listbox = tk.Listbox(root, width=50)
        self.pref_listbox.grid(row=6, column=0, columnspan=3, padx=5, pady=5)

        self.run_btn = tk.Button(root, text="Run Scheduler", command=self.run_scheduler)
        self.run_btn.grid(row=7, column=0, columnspan=3, pady=10)

    def add_employee(self):
        emp = self.new_emp_var.get().strip()
        if not emp:
            messagebox.showwarning("Input Error", "Please enter an employee name.")
            return
        if emp in self.employees:
            messagebox.showwarning("Input Error", "Employee already exists.")
            return
        self.employees.append(emp)
        self.employee_combo['values'] = self.employees
        self.new_emp_var.set("")
        self.num_emp_label.config(text=f"Number of employees: {len(self.employees)}")

    def add_preference(self):
        emp = self.employee_var.get()
        day = self.day_var.get()
        shift = self.shift_var.get()
        if not emp or not day or not shift:
            messagebox.showwarning("Input Error", "Please select employee, day, and shift.")
            return
        scheduler.add_employee_preference(emp, day, shift)
        self.pref_listbox.insert(tk.END, f"{emp} prefers {shift} on {day}")
        self.employee_combo.set("")
        self.day_combo.set("")
        self.shift_combo.set("")

    def run_scheduler(self):
        for emp in self.employees:
            if emp not in scheduler.preferences:
                scheduler.preferences[emp]
        scheduler.assign_shifts()
        schedule_text = ""
        for day in DAYS:
            schedule_text += f"\n{day}:\n"
            for shift in SHIFTS:
                emps = scheduler.schedule[day][shift]
                if len(emps) < 2:
                    schedule_text += f"  {shift.capitalize()}: Employees unavailable\n"
                else:
                    schedule_text += f"  {shift.capitalize()}: {', '.join(emps)}\n"
        self.show_schedule(schedule_text)

    def show_schedule(self, schedule_text):
        win = tk.Toplevel(self.root)
        win.title("Generated Schedule")
        text = tk.Text(win, width=60, height=25)
        text.pack(padx=10, pady=10)
        text.insert(tk.END, schedule_text)
        text.config(state=tk.DISABLED)

if __name__ == "__main__":
    root = tk.Tk()
    app = SchedulerGUI(root)
    root.mainloop()
