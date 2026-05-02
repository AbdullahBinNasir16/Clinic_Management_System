package cms;

public class Appointment {

    public enum Status { SCHEDULED, COMPLETED, CANCELLED, NO_SHOW }

    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorName;
    private String date;        // YYYY-MM-DD
    private String timeSlot;    // e.g. "09:00 AM"
    private String reason;
    private Status status;
    private String notes;
    private String createdAt;

    public Appointment(String appointmentId, String patientId, String patientName,
                       String doctorName, String date, String timeSlot,
                       String reason, String createdAt) {
        this.appointmentId = appointmentId;
        this.patientId     = patientId;
        this.patientName   = patientName;
        this.doctorName    = doctorName;
        this.date          = date;
        this.timeSlot      = timeSlot;
        this.reason        = reason;
        this.status        = Status.SCHEDULED;
        this.notes         = "";
        this.createdAt     = createdAt;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String    getAppointmentId() { return appointmentId; }
    public String    getPatientId()     { return patientId; }
    public String    getPatientName()   { return patientName; }
    public String    getDoctorName()    { return doctorName; }
    public String    getDate()          { return date; }
    public String    getTimeSlot()      { return timeSlot; }
    public String    getReason()        { return reason; }
    public Status    getStatus()        { return status; }
    public String    getNotes()         { return notes; }
    public String    getCreatedAt()     { return createdAt; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setStatus(Status s)  { this.status = s; }
    public void setNotes(String n)   { this.notes  = n; }
    public void setDate(String d)    { this.date   = d; }
    public void setTimeSlot(String t){ this.timeSlot = t; }
    public void setDoctorName(String d) { this.doctorName = d; }

    public String getStatusLabel() {
        switch (status) {
            case SCHEDULED:  return "Scheduled";
            case COMPLETED:  return "Completed";
            case CANCELLED:  return "Cancelled";
            case NO_SHOW:    return "No-Show";
            default:         return "Unknown";
        }
    }

    /** Returns the formatted reference used across the billing module. */
    public String getReference() { return appointmentId; }
}
