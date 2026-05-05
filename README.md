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
