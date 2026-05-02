package cms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PatientDB {

    private static final Map<String, Patient> records = new LinkedHashMap<>();
    private static int counter = 1000;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static { load(); }

    // ── Persistence ──────────────────────────────────────────────────────────

    private static void load() {
        String json = JsonStore.read(JsonStore.PATIENTS);
        if (json.isEmpty()) {
            // First run – seed sample data
            addSample("Ali Hassan",   "1990-03-15", "Male",   "0300-1234567",
                      "House 5, Lahore",       "Sara Hassan – 0300-9876543",   "Diabetes");
            addSample("Fatima Khan",  "1985-07-22", "Female", "0321-2345678",
                      "Flat 3, Karachi",        "Ahmed Khan – 0321-8765432",    "Hypertension");
            addSample("Usman Tariq",  "2000-11-01", "Male",   "0333-3456789",
                      "Street 9, Islamabad",    "Ayesha Tariq – 0333-7654321",  "None");
            persist();
            return;
        }
        for (Map<String, String> m : JsonStore.parseArray(json)) {
            String id = m.get("patientId");
            if (id == null) continue;
            Patient p = new Patient(id,
                m.get("fullName"), m.get("dob"), m.get("gender"),
                m.get("contactNumber"), m.get("address"),
                m.get("emergencyContact"), m.get("medicalConditions"),
                m.get("lastUpdated"));
            records.put(id, p);
            // track counter
            try {
                int n = Integer.parseInt(id.substring(1));
                if (n >= counter) counter = n;
            } catch (Exception ignored) {}
        }
    }

    public static void persist() {
        List<String> objs = new ArrayList<>();
        for (Patient p : records.values()) {
            objs.add(JsonStore.obj(
                "patientId",        p.getPatientId(),
                "fullName",         p.getFullName(),
                "dob",              p.getDob(),
                "gender",           p.getGender(),
                "contactNumber",    p.getContactNumber(),
                "address",          p.getAddress(),
                "emergencyContact", p.getEmergencyContact(),
                "medicalConditions",p.getMedicalConditions(),
                "lastUpdated",      p.getLastUpdated()
            ));
        }
        JsonStore.write(JsonStore.PATIENTS, JsonStore.array(objs));
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    private static void addSample(String name, String dob, String gender,
                                   String phone, String addr,
                                   String emergency, String conditions) {
        String id = "P" + (++counter);
        records.put(id, new Patient(id, name, dob, gender, phone, addr,
                                    emergency, conditions, now()));
    }

    public static String generateId() { return "P" + (++counter); }

    public static String now() { return LocalDateTime.now().format(FMT); }

    public static void save(Patient p) {
        records.put(p.getPatientId(), p);
        persist();
    }

    public static List<Patient> searchByName(String query) {
        String q = query.toLowerCase();
        return records.values().stream()
            .filter(p -> p.getFullName().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    public static Patient searchById(String id) {
        return records.get(id.toUpperCase());
    }

    public static boolean exists(String name, String dob) {
        return records.values().stream()
            .anyMatch(p -> p.getFullName().equalsIgnoreCase(name)
                        && p.getDob().equals(dob));
    }

    public static List<Patient> getAll() {
        return new ArrayList<>(records.values());
    }
}
