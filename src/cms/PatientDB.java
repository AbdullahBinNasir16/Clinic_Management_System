package cms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.bson.Document;

public class PatientDB {

    private static final Map<String, Patient> records = new LinkedHashMap<>();
    private static int counter = 1000;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static { load(); }

    // ── Persistence ──────────────────────────────────────────────────────────

    private static void load() {
        if (MongoStore.isAvailable()) {
            List<Document> docs = MongoStore.readAll("patients");
            if (docs.isEmpty()) {
                addSample("Ali Hassan",   "1990-03-15", "Male",   "0300-1234567",
                          "House 5, Lahore",       "Sara Hassan – 0300-9876543",   "Diabetes");
                addSample("Fatima Khan",  "1985-07-22", "Female", "0321-2345678",
                          "Flat 3, Karachi",        "Ahmed Khan – 0321-8765432",    "Hypertension");
                addSample("Usman Tariq",  "2000-11-01", "Male",   "0333-3456789",
                          "Street 9, Islamabad",    "Ayesha Tariq – 0333-7654321",  "None");
                persist();
                return;
            }
            for (Document d : docs) {
                String id = d.getString("patientId");
                if (id == null) continue;
                Patient p = new Patient(id,
                    d.getString("fullName"), d.getString("dob"), d.getString("gender"),
                    d.getString("contactNumber"), d.getString("address"),
                    d.getString("emergencyContact"), d.getString("medicalConditions"),
                    d.getString("lastUpdated"));
                records.put(id, p);
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n >= counter) counter = n;
                } catch (Exception ignored) {}
            }
            return;
        }

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
        if (MongoStore.isAvailable()) {
            List<Document> docs = new ArrayList<>();
            for (Patient p : records.values()) {
                docs.add(new Document("patientId", p.getPatientId())
                    .append("fullName", p.getFullName())
                    .append("dob", p.getDob())
                    .append("gender", p.getGender())
                    .append("contactNumber", p.getContactNumber())
                    .append("address", p.getAddress())
                    .append("emergencyContact", p.getEmergencyContact())
                    .append("medicalConditions", p.getMedicalConditions())
                    .append("lastUpdated", p.getLastUpdated()));
            }
            MongoStore.replaceCollection("patients", docs);
            return;
        }

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
        if (MongoStore.isAvailable()) {
            MongoStore.upsert("patients", new Document("patientId", p.getPatientId())
                .append("fullName", p.getFullName())
                .append("dob", p.getDob())
                .append("gender", p.getGender())
                .append("contactNumber", p.getContactNumber())
                .append("address", p.getAddress())
                .append("emergencyContact", p.getEmergencyContact())
                .append("medicalConditions", p.getMedicalConditions())
                .append("lastUpdated", p.getLastUpdated()),
                "patientId");
            return;
        }
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
