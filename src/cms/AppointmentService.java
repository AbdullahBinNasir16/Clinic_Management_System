package cms;

public class AppointmentService {

    /**
     * Books a new appointment after validating all inputs and checking
     * for doctor/slot conflicts.
     *
     * @throws IllegalArgumentException with a human-readable message on failure.
     */
    public static Appointment book(String patientId, String doctorName,
                                    String date, String timeSlot, String reason) {
        // ── Input validation ───────────────────────────────────────────────
        if (patientId == null || patientId.trim().isEmpty())
            throw new IllegalArgumentException("Patient ID is required.");
        if (doctorName == null || doctorName.trim().isEmpty())
            throw new IllegalArgumentException("Please select a doctor.");
        if (date == null || date.trim().isEmpty())
            throw new IllegalArgumentException("Appointment date is required.");
        if (timeSlot == null || timeSlot.trim().isEmpty())
            throw new IllegalArgumentException("Please select a time slot.");
        if (reason == null || reason.trim().isEmpty())
            throw new IllegalArgumentException("Reason for visit is required.");

        // ── Validate date format ───────────────────────────────────────────
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");

        // ── Patient must exist ─────────────────────────────────────────────
        Patient patient = PatientDB.searchById(patientId.trim().toUpperCase());
        if (patient == null)
            throw new IllegalArgumentException("No patient found with ID: " + patientId.trim().toUpperCase());

        // ── Conflict check ─────────────────────────────────────────────────
        if (AppointmentDB.isSlotTaken(doctorName, date, timeSlot, ""))
            throw new IllegalArgumentException(
                doctorName + " is already booked for " + timeSlot + " on " + date + ".\n"
                + "Please choose a different time slot.");

        // ── Create and save ────────────────────────────────────────────────
        String id = AppointmentDB.generateId();
        Appointment apt = new Appointment(id, patient.getPatientId(),
            patient.getFullName(), doctorName, date, timeSlot,
            reason.trim(), PatientDB.now());
        AppointmentDB.save(apt);
        return apt;
    }

    /**
     * Cancels an appointment. Only SCHEDULED appointments can be cancelled.
     */
    public static void cancel(String appointmentId, String notes) {
        Appointment apt = getOrThrow(appointmentId);
        if (apt.getStatus() != Appointment.Status.SCHEDULED)
            throw new IllegalArgumentException(
                "Only scheduled appointments can be cancelled.");
        apt.setStatus(Appointment.Status.CANCELLED);
        if (notes != null && !notes.trim().isEmpty())
            apt.setNotes(notes.trim());
        AppointmentDB.save(apt);
    }

    /**
     * Marks an appointment as completed and saves optional clinical notes.
     */
    public static void complete(String appointmentId, String notes) {
        Appointment apt = getOrThrow(appointmentId);
        if (apt.getStatus() == Appointment.Status.CANCELLED)
            throw new IllegalArgumentException(
                "A cancelled appointment cannot be marked complete.");
        if (apt.getStatus() == Appointment.Status.COMPLETED)
            throw new IllegalArgumentException(
                "Appointment is already marked as completed.");
        apt.setStatus(Appointment.Status.COMPLETED);
        if (notes != null && !notes.trim().isEmpty())
            apt.setNotes(notes.trim());
        AppointmentDB.save(apt);
    }

    /**
     * Marks an appointment as No-Show.
     */
    public static void markNoShow(String appointmentId) {
        Appointment apt = getOrThrow(appointmentId);
        if (apt.getStatus() != Appointment.Status.SCHEDULED)
            throw new IllegalArgumentException(
                "Only scheduled appointments can be marked as No-Show.");
        apt.setStatus(Appointment.Status.NO_SHOW);
        AppointmentDB.save(apt);
    }

    /**
     * Reschedules a SCHEDULED appointment to a new date/time.
     */
    public static void reschedule(String appointmentId, String newDate,
                                   String newTimeSlot) {
        if (newDate == null || !newDate.matches("\\d{4}-\\d{2}-\\d{2}"))
            throw new IllegalArgumentException("New date must be in YYYY-MM-DD format.");
        if (newTimeSlot == null || newTimeSlot.trim().isEmpty())
            throw new IllegalArgumentException("Please select a new time slot.");

        Appointment apt = getOrThrow(appointmentId);
        if (apt.getStatus() != Appointment.Status.SCHEDULED)
            throw new IllegalArgumentException(
                "Only scheduled appointments can be rescheduled.");

        if (AppointmentDB.isSlotTaken(apt.getDoctorName(), newDate,
                                       newTimeSlot, appointmentId))
            throw new IllegalArgumentException(
                apt.getDoctorName() + " is already booked for "
                + newTimeSlot + " on " + newDate + ".\n"
                + "Please choose a different time slot.");

        apt.setDate(newDate);
        apt.setTimeSlot(newTimeSlot);
        AppointmentDB.save(apt);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private static Appointment getOrThrow(String id) {
        Appointment apt = AppointmentDB.getById(id);
        if (apt == null)
            throw new IllegalArgumentException("Appointment not found: " + id);
        return apt;
    }
}
