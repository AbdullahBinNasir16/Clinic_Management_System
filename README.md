# Clinic Management System

## Project Overview
A comprehensive Java-based clinic management system built with Swing UI for managing patients, appointments, billing, and more. Features a modern sidebar navigation, dashboard with live statistics and calendar, and flexible persistence options.

## Technologies Used
- **Language**: Java 21
- **UI Framework**: Swing (with custom theming)
- **Persistence**: MongoDB (optional, with JSON fallback)
- **Build Tool**: Manual compilation with javac (scripts provided)

## Requirements
- **Java 21 JDK** (required for compilation and runtime)
- **MongoDB** (optional; if not installed, the app falls back to JSON file storage)
- **Operating System**: Windows, Linux, or macOS (scripts provided for each)

## How to Compile and Run
1. **Install Java 21**: Download and install JDK 21 from [Adoptium](https://adoptium.net/) or Oracle.
2. **(Optional) Install MongoDB**: Download from [mongodb.com](https://www.mongodb.com/) and start the service if you want persistent storage.
3. **Compile and Run**:
   - **Windows**: Double-click `compile_and_run.bat` or run it in Command Prompt.
   - **Linux/macOS**: Run `./compile_and_run.sh` in Terminal.
   - The script will compile all Java files and launch the application.

## Features
- Patient registration and profile management
- Appointment scheduling with conflict checking
- Billing and invoice generation
- Payment processing
- Dashboard with live statistics and calendar view
- Search and reporting capabilities
- Modern, professional UI with sidebar navigation

---

## Sprint 4 – Appointment Scheduling Module

### New Files Added
| File | Purpose |
|---|---|
| `Appointment.java` | Model: holds appointment data and status enum |
| `AppointmentDB.java` | In-memory store; sample data; conflict/slot queries |
| `AppointmentService.java` | Business logic: book, cancel, complete, reschedule, no-show |
| `AppointmentPanel.java` | Full UI: Book Appointment tab + Appointment Schedule tab |

### Modified Files
| File | Change |
|---|---|
| `MainFrame.java` | Added "Appointments" tab (tab index 3); refresh listener wired |
| `GenerateInvoicePanel.java` | Appointment Ref field is now a live dropdown populated with the patient's **completed** appointments after Lookup |

### Features Implemented
- **Book Appointment** – select doctor, date, time slot (taken slots are hidden), enter reason; validates patient exists
- **Appointment Schedule** – tabular view of all appointments, filterable by Patient ID / Name / Date / Doctor
- **Mark Complete** – with optional clinical notes
- **Cancel** – with confirmation dialog and optional reason
- **No-Show** – one-click status update
- **Reschedule** – inline dialog to pick new date/time; slot conflict check applied
- **Invoice Integration** – Generate Invoice tab now shows only completed appointments for the selected patient in the Appointment Ref dropdown
