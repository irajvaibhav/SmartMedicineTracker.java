import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Data model for a Medicine.
 * Contains all details and business logic related to a single medication.
 * Implements Serializable to allow the object to be saved to a file.
 */
public class Medicine implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name, batchNo, manufacturer, disease, risk;
    private LocalDate expiryDate;
    private int quantity;
    private double pricePerUnit;

    public Medicine(int id, String name, String batchNo, LocalDate expiryDate, int quantity,
                    double pricePerUnit, String manufacturer, String disease, String risk) {
        this.id = id;
        this.name = name;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.manufacturer = manufacturer;
        this.disease = disease;
        this.risk = risk;
    }

    // --- Getter Methods ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBatchNo() { return batchNo; }
    public String getRisk() { return risk; }

    // --- Business Logic Methods ---
    public long getDaysUntilExpiry() { return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate); }
    public boolean isExpired() { return getDaysUntilExpiry() < 0; }
    public boolean isExpiringSoon() { long d = getDaysUntilExpiry(); return d >= 0 && d <= 15; }
    public boolean isLowStock() { return quantity > 0 && quantity <= 5; }
}