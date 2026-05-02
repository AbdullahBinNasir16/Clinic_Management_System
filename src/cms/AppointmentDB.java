package cms;

import java.util.*;
import java.util.stream.Collectors;

public class AppointmentDB {

    private static final Map<String, Appointment> records = new LinkedHashMap<>();
    private static int counter = 0;

    public static final String[] TIME_SLOTS = {
        "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM",
        "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
        "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM",
        "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM",
        "04:00 PM", "04:30 PM", "05:00 PM"
    };

    public static final String[] DOCTORS = {
        "Dr. Imran Malik",
        "Dr. Sana Rehman",
        "Dr. Tariq Hussain",
        "Dr. Aisha Noor",
        "Dr. Bilal Chaudhry"
    };

    static { load(); }

    // ── Persistence ──────────────────────────────────────────────────────────

    private static void load() {
        String json = JsonStore.read(JsonStore.APPOINTMENTS);
        if (json.isEmpty()) {
            seedSampleData();
            persist();
            return;
        }
        for (Map<String, String> m : JsonStore.parseArray(json)) {
            String id = m.get("appointmentId");
            if (id == null) continue;
            Appointment a = new Appointment(id,
                m.get("patientId"), m.get("patientName"),
                m.get("doctorName"), m.get("date"), m.get("timeSlot"),
                m.get("reason"), m.get("createdAt"));
            try { a.setStatus(Appointment.Status.valueOf(m.get("status"))); }
            catch (Exception ignored) {}
            String notes = m.get("notes");
            if (notes != null && !notes.isEmpty()) a.setNotes(notes);
            records.put(id, a);
            try {
                int n = Integer.parseInt(id.substring(4));
                if (n >= counter) counter = n;
            } catch (Exception ignored) {}
        }
    }

    public static void persist() {
        List<String> objs = new ArrayList<>();
        for (Appointment a : records.values()) {
            objs.add(JsonStore.obj(
                "appointmentId", a.getAppointmentId(),
                "patientId",     a.getPatientId(),
                "patientName",   a.getPatientName(),
                "doctorName",    a.getDoctorName(),
                "date",          a.getDate(),
                "timeSlot",      a.getTimeSlot(),
                "reason",        a.getReason(),
                "status",        a.getStatus().name(),
                "notes",         a.getNotes(),
                "createdAt",     a.getCreatedAt()
            ));
        }
        JsonStore.write(JsonStore.APPOINTMENTS, JsonStore.array(objs));
    }

    private static void seedSampleData() {
        Appointment a1 = new Appointment("APT-001", "P1001", "Ali Hassan",
            "Dr. Imran Malik", "2026-04-28", "09:00 AM",
            "Routine check-up and blood sugar monitoring", PatientDB.now());
        a1.setStatus(Appointment.Status.COMPLETED);
        a1.setNotes("Patient advised to continue current medication.");
        records.put(a1.getAppointmentId(), a1);

        Appointment a2 = new Appointment("APT-002", "P1001", "Ali Hassan",
            "Dr. Sana Rehman", "2026-05-05", "10:30 AM",
            "Follow-up for X-Ray results", PatientDB.now());
        records.put(a2.getAppointmentId(), a2);

        Appointment a3 = new Appointment("APT-003", "P1002", "Fatima Khan",
            "Dr. Tariq Hussain", "2026-04-30", "02:00 PM",
            "Blood pressure consultation", PatientDB.now());
        a3.setStatus(Appointment.Status.COMPLETED);
        a3.setNotes("BP stabilised. Medication adjusted.");
        records.put(a3.getAppointmentId(), a3);

        Appointment a4 = new Appointment("APT-004", "P1003", "Usman Tariq",
            "Dr. Aisha Noor", "2026-05-07", "11:00 AM",
            "General wellness check", PatientDB.now());
        records.put(a4.getAppointmentId(), a4);

        Appointment a5 = new Appointment("APT-005", "P1002", "Fatima Khan",
            "Dr. Imran Malik", "2026-05-12", "03:00 PM",
            "Hypertension management review", PatientDB.now());
        records.put(a5.getAppointmentId(), a5);

        counter = 5;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public static String generateId() { return String.format("APT-%03d", ++counter); }

    public static void save(Appointment a) {
        records.put(a.getAppointmentId(), a);
        persist();
    }

    public static Appointment getById(String id) { return records.get(id); }

    public static List<Appointment> getAll() { return new ArrayList<>(records.values()); }

    public static List<Appointment> getByPatientId(String patientId) {
        return records.values().stream()
            .filter(a -> a.getPatientId().equalsIgnoreCase(patientId))
            .sorted(Comparator.comparing(Appointment::getDate).reversed())
            .collect(Collectors.toList());
    }

    public static List<Appointment> getByPatientName(String name) {
        String q = name.toLowerCase();
        return records.values().stream()
            .filter(a -> a.getPatientName().toLowerCase().contains(q))
            .sorted(Comparator.comparing(Appointment::getDate).reversed())
            .collect(Collectors.toList());
    }

    public static List<Appointment> getByDate(String date) {
        return records.values().stream()
            .filter(a -> a.getDate().equals(date))
            .sorted(Comparator.comparing(Appointment::getTimeSlot))
            .collect(Collectors.toList());
    }

    public static List<Appointment> getByDoctor(String doctorName) {
        return records.values().stream()
            .filter(a -> a.getDoctorName().equalsIgnoreCase(doctorName))
            .sorted(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTimeSlot))
            .collect(Collectors.toList());
    }

    public static boolean isSlotTaken(String doctorName, String date,
                                       String timeSlot, String excludeId) {
        return records.values().stream()
            .filter(a -> !a.getAppointmentId().equals(excludeId))
            .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
            .anyMatch(a -> a.getDoctorName().equalsIgnoreCase(doctorName)
                        && a.getDate().equals(date)
                        && a.getTimeSlot().equals(timeSlot));
    }

    public static Set<String> takenSlots(String doctorName, String date, String excludeId) {
        return records.values().stream()
            .filter(a -> !a.getAppointmentId().equals(excludeId))
            .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
            .filter(a -> a.getDoctorName().equalsIgnoreCase(doctorName)
                      && a.getDate().equals(date))
            .map(Appointment::getTimeSlot)
            .collect(Collectors.toSet());
    }
}
