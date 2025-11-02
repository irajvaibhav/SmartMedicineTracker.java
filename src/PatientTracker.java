import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PatientTracker {
    private Map<String, Patient> patients = new HashMap<>();
    private static final String DATA_FILE = "patients.dat";
    private static final String HTML_FILE = "medicines.html";
    private static int medIdCounter = 1;
    private final Map<String, String> abbrMap = new HashMap<>();

    public PatientTracker() {
        abbrMap.put("pcm", "Paracetamol");
        abbrMap.put("para", "Paracetamol");
        abbrMap.put("asp", "Aspirin");
        abbrMap.put("amox", "Amoxicillin");
        loadFromFile();
    }

    public void generateAllWebPages() {
        generateSinglePageDashboard();
        generatePatientDetailPages();
    }

    private String expandShortForm(String name) {
        return abbrMap.getOrDefault(name.trim().toLowerCase(), name);
    }

    private Map.Entry<String, String> detectDiseaseAndRisk(String medName, int age) {
        String n = medName.toLowerCase();
        String disease = "Unknown", risk = "Low";
        if (n.contains("warf") || n.contains("hepar")) { disease = "Heart/Anticoagulation"; risk = "High"; }
        else if (n.contains("aspirin")) { disease = "Heart/Antiplatelet"; risk = "High"; }
        else if (n.contains("metformin")) { disease = "Diabetes"; risk = "Moderate"; }
        else if (n.contains("amoxicillin")) { disease = "Infection/Antibiotic"; risk = "Moderate"; }
        else if (n.contains("paracetamol")) { disease = "Fever/Analgesic"; risk = "Moderate"; }
        if (age >= 60 && (risk.equalsIgnoreCase("Moderate") || risk.equalsIgnoreCase("High"))) { risk = "High"; }
        return new AbstractMap.SimpleEntry<>(disease, risk);
    }

    public void addMedicineToPatient(Scanner sc, String adminEmail) {
        System.out.print("Enter Patient Name: "); String patientName = sc.nextLine().trim();
        String patientKey = patientName.toLowerCase();
        Patient patient;
        if (patients.containsKey(patientKey)) {
            patient = patients.get(patientKey);
            System.out.println("Updating existing patient: " + patient.getName());
        } else {
            System.out.print("Enter Patient Age: ");
            int age;
            try {
                age = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid age. Please enter a number. Aborting.");
                return;
            }
            patient = new Patient(patientName, age, adminEmail);
            patients.put(patientKey, patient);
        }
        System.out.print("Medicine Name: "); String name = sc.nextLine(); name = expandShortForm(name);
        System.out.print("Batch No: "); String batch = sc.nextLine();
        for (Medicine existingMed : patient.getMedicines()) {
            if (existingMed.getName().equalsIgnoreCase(name) && existingMed.getBatchNo().equalsIgnoreCase(batch)) {
                System.out.println("❌ ERROR: This medicine is already registered."); return;
            }
        }
        System.out.print("Expiry (yyyy-MM-dd): ");
        LocalDate exp;
        try {
            exp = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            System.out.println("❌ Invalid date format. Please use yyyy-MM-dd. Aborting.");
            return;
        }
        System.out.print("Quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid quantity. Please enter a number. Aborting.");
            return;
        }
        System.out.print("Price per Unit: ");
        double price;
        try {
            price = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid price. Please enter a number. Aborting.");
            return;
        }
        System.out.print("Manufacturer: "); String mfg = sc.nextLine();
        Map.Entry<String, String> dr = detectDiseaseAndRisk(name, patient.getAge());
        Medicine med = new Medicine(medIdCounter++, name, batch, exp, qty, price, mfg, dr.getKey(), dr.getValue());
        patient.addMedicine(med);
        saveToFile();
        generateAllWebPages();
        System.out.println("✅ Medicine added to " + patient.getName() + "'s profile.");
    }

    public void deletePatient(Scanner sc) {
        System.out.print("Enter Patient Name to delete: "); String patientName = sc.nextLine().trim();
        if (patients.remove(patientName.toLowerCase()) != null) {
            saveToFile();
            generateAllWebPages();
            System.out.println("✅ Patient '" + patientName + "' deleted.");
        } else {
            System.out.println("❌ Patient not found.");
        }
    }

    public void generateSinglePageDashboard() {
        try (PrintWriter w = new PrintWriter(new FileWriter(HTML_FILE))) {
            w.println("<!doctype html><html lang='en'><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
            w.println("<title>Smart Medicine Dashboard</title>");
            w.println("<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap' rel='stylesheet'>");
            w.println("<style>:root{--bg:#f4f7fb;--card:#fff;--text:#0f172a;--muted:#64748b}body.dark{--bg:#0b1220;--card:#0f1724;--text:#e6eef6;--muted:#94a3b8}body{margin:0;padding:20px;background:var(--bg);color:var(--text);font-family:Poppins,system-ui,Arial}.wrap{max-width:1200px;margin:0 auto}.header{display:flex;justify-content:space-between;align-items:center;gap:12px;padding:16px 0;}.title{font-size:1.5rem;font-weight:700;background:linear-gradient(90deg,#667eea,#764ba2);-webkit-background-clip:text;color:transparent}.controls{display:flex;gap:8px;align-items:center;flex-wrap:wrap}.search{padding:8px;border-radius:8px;border:1px solid rgba(0,0,0,0.08);min-width:220px}.btn{padding:8px 12px;border-radius:999px;border:0;cursor:pointer;font-weight:600}.btn.primary{background:linear-gradient(90deg,#667eea,#764ba2);color:#fff}.grid{display:grid;gap:16px;margin-top:18px;grid-template-columns:repeat(auto-fit,minmax(300px,1fr))}.card{background:var(--card);padding:14px;border-radius:12px;border-left:6px solid #10b981;box-shadow:0 10px 30px rgba(2,6,23,0.06);cursor:pointer}.card.expired{border-left-color:#ef4444}.card.expiring{border-left-color:#f59e0b}.card.low{border-left-color:#3b82f6}.card h3{margin:0 0 8px 0}.meta{display:flex;gap:10px;flex-wrap:wrap}.meta .item{font-weight:700;color:var(--muted)}.badge{display:inline-block;padding:6px 10px;border-radius:999px;font-weight:800;color:white;margin-left:6px}.badge.high{background:#ef4444}.badge.mod{background:#f59e0b}.badge.low{background:#10b981}.warn{margin-top:8px;padding:8px;border-radius:8px;background:linear-gradient(135deg,#fff7ed,#ffedd5);border:1px solid #f59e0b;font-weight:700}.logout-btn{font-weight:600;background:transparent;color:var(--muted);border:1px solid var(--muted);margin-left:10px;}</style></head><body>");
            w.println("<div class='wrap'><div class='header'><div class='title'>💊 Smart Medicine Tracker</div><div class='controls'><input id='search' class='search' placeholder='Search by patient name' oninput='liveSearch()'/><button class='btn primary' onclick=\"filter('all')\">All</button><button class='btn' onclick=\"filter('expiring')\">Expiring</button><button class='btn' onclick=\"filter('expired')\">Expired</button><button class='btn' onclick=\"filter('low')\">Low Stock</button><button id='toggleDark' class='btn' onclick='toggleDark()'>Dark Mode</button><button class='btn logout-btn' onclick='logout()'>Logout</button></div></div>");
            w.println("<div class='grid' id='patientGrid'>");
            for (Patient p : patients.values()) {
                long expiredCount = p.getMedicines().stream().filter(Medicine::isExpired).count();
                long expiringCount = p.getMedicines().stream().filter(m -> !m.isExpired() && m.isExpiringSoon()).count();
                long lowStockCount = p.getMedicines().stream().filter(Medicine::isLowStock).count();
                String highestRisk = "Low";
                if (p.getMedicines().stream().anyMatch(m -> "High".equalsIgnoreCase(m.getRisk()))) highestRisk = "High";
                else if (p.getMedicines().stream().anyMatch(m -> "Moderate".equalsIgnoreCase(m.getRisk()))) highestRisk = "Moderate";
                String badgeCls = highestRisk.equalsIgnoreCase("High") ? "high" : (highestRisk.equalsIgnoreCase("Moderate") ? "mod" : "low");
                Set<String> cardClasses = new HashSet<>(Arrays.asList("card"));
                if (expiredCount > 0) cardClasses.add("expired");
                else if (expiringCount > 0) cardClasses.add("expiring");
                else if (lowStockCount > 0) cardClasses.add("low");
                String detailPageUrl = "patient_details_" + p.getName().trim().replaceAll("\\s+", "_") + ".html";
                w.println("<div class='" + String.join(" ", cardClasses) + "' data-owner='" + p.getOwnerEmail().toLowerCase() + "' onclick=\"window.location.href='" + detailPageUrl + "'\">");
                w.println("<h3>" + p.getName() + " <span class='badge " + badgeCls + "'>" + highestRisk + "</span></h3>");
                w.println("<div class='meta'><div class='item'>Age: " + p.getAge() + "</div><div class='item'>Total Meds: " + p.getMedicines().size() + "</div></div>");
                if (expiredCount > 0) w.println("<div class='warn'>❌ " + expiredCount + " med(s) EXPIRED</div>");
                else if (expiringCount > 0) w.println("<div class='warn'>⚠️ " + expiringCount + " med(s) expiring soon</div>");
                else if (lowStockCount > 0) w.println("<div class='warn'>📦 " + lowStockCount + " med(s) on low stock</div>");
                w.println("</div>");
            }
            w.println("</div></div>");
            w.println("<script>");
            w.println("let currentFilterType = 'all';");
            w.println("function applyFiltersAndSearch() {");
            w.println("  const loggedInUser = sessionStorage.getItem('loggedInUser');");
            w.println("  const searchTerm = document.getElementById('search').value.toLowerCase();");
            w.println("  document.querySelectorAll('#patientGrid .card').forEach(card => {");
            w.println("    const ownerMatch = loggedInUser === 'guest' || card.dataset.owner === loggedInUser;");
            w.println("    const filterMatch = currentFilterType === 'all' || card.classList.contains(currentFilterType);");
            w.println("    const searchMatch = card.innerText.toLowerCase().includes(searchTerm);");
            w.println("    if (ownerMatch && filterMatch && searchMatch) {");
            w.println("      card.style.display = 'block';");
            w.println("    } else {");
            w.println("      card.style.display = 'none';");
            w.println("    }");
            w.println("  });");
            w.println("}");
            w.println("window.onload = function() {");
            w.println("  const loggedInUser = sessionStorage.getItem('loggedInUser');");
            w.println("  if (!loggedInUser) { window.location.href = 'login.html'; return; }");
            w.println("  applyFiltersAndSearch();");
            w.println("};");
            w.println("function filter(type) {");
            w.println("  currentFilterType = type;");
            w.println("  applyFiltersAndSearch();");
            w.println("}");
            w.println("function liveSearch() {");
            w.println("  applyFiltersAndSearch();");
            w.println("}");
            w.println("function logout() { sessionStorage.removeItem('loggedInUser'); window.location.href = 'login.html'; }");
            w.println("function toggleDark(){ document.body.classList.toggle('dark'); }");
            w.println("</script></body></html>");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void generatePatientDetailPages() {
        for (Patient p : patients.values()) {
            String detailPageUrl = "patient_details_" + p.getName().trim().replaceAll("\\s+", "_") + ".html";
            try (PrintWriter w = new PrintWriter(new FileWriter(detailPageUrl))) {
                w.println("<!doctype html><html><head><title>Details for " + p.getName() + "</title><link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap' rel='stylesheet'><style>:root{--bg:#f4f7fb;--card:#fff;--text:#0f172a;--muted:#64748b}body.dark{--bg:#0b1220;--card:#0f1724;--text:#e6eef6;--muted:#94a3b8}body{margin:0;padding:20px;background:var(--bg);color:var(--text);font-family:Poppins,system-ui,Arial}.wrap{max-width:1200px;margin:0 auto}.header{display:flex;justify-content:space-between;align-items:center;padding:16px;}.title{font-size:1.5rem;font-weight:700}.back-link{font-weight:600;text-decoration:none;color:var(--muted)}.grid{display:grid;gap:16px;margin-top:18px;grid-template-columns:repeat(auto-fit,minmax(300px,1fr))}.card{background:var(--card);padding:14px;border-radius:12px;border-left:6px solid #10b981;box-shadow:0 10px 30px rgba(2,6,23,0.06);}.card.expired{border-left-color:#ef4444}.card.expiring{border-left-color:#f59e0b}.card.low{border-left-color:#3b82f6}.card h3{margin:0 0 8px 0}.meta{display:flex;gap:10px;flex-wrap:wrap}.meta .item{font-weight:700;color:var(--muted)}.warn{margin-top:8px;padding:8px;border-radius:8px;background:linear-gradient(135deg,#fff7ed,#ffedd5);border:1px solid #f59e0b;font-weight:700}</style></head><body>");
                w.println("<div class='wrap'><div class='header'><div class='title'>Medicines for " + p.getName() + "</div><a href='medicines.html' class='back-link'>&larr; Back to Dashboard</a></div><div class='grid' id='medGrid'>");
                if (p.getMedicines().isEmpty()) { w.println("<p>No medicines recorded for this patient.</p>"); }
                else {
                    for (Medicine m : p.getMedicines()) {
                        String cls = "";
                        if (m.isExpired()) cls = "expired";
                        else if (m.isExpiringSoon()) cls = "expiring";
                        else if (m.isLowStock()) cls = "low";
                        w.println("<div class='card "+cls+"'><h3>"+m.getName()+"</h3><div class='meta'><div class='item'>Risk: "+m.getRisk()+"</div><div class='item'>Batch: "+m.getBatchNo()+"</div></div>");
                        if (m.isExpired()) w.println("<div class='warn'>❌ EXPIRED</div>");
                        else if (m.isExpiringSoon()) w.println("<div class='warn'>⚠️ Expires in " + m.getDaysUntilExpiry() + " day(s)</div>");
                        else if (m.isLowStock()) w.println("<div class='warn'>📦 Low stock (<=5)</div>");
                        w.println("</div>");
                    }
                }
                w.println("</div></div></body></html>");
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) { oos.writeObject(patients); oos.writeInt(medIdCounter); }
        catch (IOException e) { System.out.println("❌ Save Error: " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            patients = (Map<String, Patient>) ois.readObject();
            if (!patients.isEmpty()) {
                int maxId = 0;
                for (Patient p : patients.values()) {
                    for (Medicine m : p.getMedicines()) {
                        if (m.getId() > maxId) maxId = m.getId();
                    }
                }
                medIdCounter = maxId + 1;
            }
        }
        catch (FileNotFoundException e) {
        }
        catch (Exception e) { System.out.println("❌ Load Error: " + e.getMessage()); }
    }
}