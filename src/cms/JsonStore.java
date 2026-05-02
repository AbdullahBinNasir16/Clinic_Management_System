package cms;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Minimal hand-rolled JSON persistence utility.
 * No external libraries required – uses only java.io and java.util.
 *
 * Stores each record type in its own .json file inside a "data/" folder
 * next to the running JAR (or the project root when run from the IDE).
 */
public class JsonStore {

    private static final String DATA_DIR = "data";

    static {
        new File(DATA_DIR).mkdirs();
    }

    // ── Public file paths ────────────────────────────────────────────────────

    public static final String PATIENTS     = DATA_DIR + "/patients.json";
    public static final String INVOICES     = DATA_DIR + "/invoices.json";
    public static final String APPOINTMENTS = DATA_DIR + "/appointments.json";

    // ══════════════════════════════════════════════════════════════════════════
    //  LOW-LEVEL HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /** Write the whole file atomically via a temp file. */
    public static void write(String path, String json) {
        try {
            File tmp = new File(path + ".tmp");
            try (PrintWriter pw = new PrintWriter(new FileWriter(tmp))) {
                pw.print(json);
            }
            Files.move(tmp.toPath(), new File(path).toPath(),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("[JsonStore] write failed: " + e.getMessage());
        }
    }

    /** Read file content; return "" if missing. */
    public static String read(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return "";
            return new String(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            System.err.println("[JsonStore] read failed: " + e.getMessage());
            return "";
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NAÏVE JSON SERIALISER / PARSER
    //  Rules: values are strings or numbers (never nested objects/arrays).
    //  Each record is one JSON object on one line; the file is a JSON array.
    // ══════════════════════════════════════════════════════════════════════════

    /** Escape a string value for JSON. */
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /** Build a JSON object from ordered key-value pairs. */
    public static String obj(String... kvPairs) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(kvPairs[i]).append("\":");
            String v = kvPairs[i + 1];
            // Numeric? – no quotes
            if (v != null && v.matches("-?\\d+(\\.\\d+)?")) {
                sb.append(v);
            } else {
                sb.append("\"").append(esc(v)).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /** Wrap a list of JSON objects into a JSON array string. */
    public static String array(List<String> objs) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < objs.size(); i++) {
            sb.append("  ").append(objs.get(i));
            if (i < objs.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Parse a JSON array of flat objects.
     * Returns a list of maps (field → value), all values as Strings.
     */
    public static List<Map<String, String>> parseArray(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return result;
        // Strip outer [ ]
        int start = json.indexOf('[');
        int end   = json.lastIndexOf(']');
        if (start < 0 || end < 0) return result;
        String body = json.substring(start + 1, end);

        // Split on object boundaries { … }
        int depth = 0;
        StringBuilder cur = new StringBuilder();
        for (char c : body.toCharArray()) {
            if (c == '{') { depth++; cur.append(c); }
            else if (c == '}') { depth--; cur.append(c);
                if (depth == 0) {
                    Map<String,String> m = parseObject(cur.toString().trim());
                    if (!m.isEmpty()) result.add(m);
                    cur = new StringBuilder();
                }
            } else { if (depth > 0) cur.append(c); }
        }
        return result;
    }

    /** Parse a single flat JSON object string into a String→String map. */
    public static Map<String, String> parseObject(String obj) {
        Map<String, String> m = new LinkedHashMap<>();
        if (obj == null || obj.trim().isEmpty()) return m;
        // Remove outer braces
        String s = obj.trim();
        if (s.startsWith("{")) s = s.substring(1);
        if (s.endsWith("}"))   s = s.substring(0, s.length() - 1);
        s = s.trim();

        // Tokenise key:"value" or key:number pairs
        int i = 0;
        while (i < s.length()) {
            // skip whitespace / commas
            while (i < s.length() && (s.charAt(i) == ',' || Character.isWhitespace(s.charAt(i)))) i++;
            if (i >= s.length()) break;

            // read key (quoted)
            if (s.charAt(i) != '"') { i++; continue; }
            int keyEnd = s.indexOf('"', i + 1);
            if (keyEnd < 0) break;
            String key = s.substring(i + 1, keyEnd);
            i = keyEnd + 1;

            // skip ':'
            while (i < s.length() && s.charAt(i) != ':') i++;
            i++;
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;

            // read value
            String value;
            if (i < s.length() && s.charAt(i) == '"') {
                // quoted string – handle escapes
                StringBuilder val = new StringBuilder();
                i++; // skip opening quote
                while (i < s.length()) {
                    char c = s.charAt(i);
                    if (c == '\\' && i + 1 < s.length()) {
                        char next = s.charAt(i + 1);
                        if      (next == '"')  { val.append('"');  i += 2; }
                        else if (next == '\\') { val.append('\\'); i += 2; }
                        else if (next == 'n')  { val.append('\n'); i += 2; }
                        else if (next == 'r')  { val.append('\r'); i += 2; }
                        else                   { val.append(c);    i++; }
                    } else if (c == '"') { i++; break; }
                    else { val.append(c); i++; }
                }
                value = val.toString();
            } else {
                // unquoted (number / boolean / null)
                int valEnd = i;
                while (valEnd < s.length() && s.charAt(valEnd) != ',' && s.charAt(valEnd) != '}') valEnd++;
                value = s.substring(i, valEnd).trim();
                i = valEnd;
            }
            m.put(key, value);
        }
        return m;
    }
}
