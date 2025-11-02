import java.io.Serializable;
import java.util.ArrayList;

/**
 * Data model for a Patient.
 * Includes an 'ownerEmail' to link them to a specific user account.
 * Implements Serializable to allow the object to be saved to a file.
 */
public class Patient implements Serializable {
    private static final long serialVersionUID = 3L;
    private String name;
    private int age;
    private String ownerEmail;
    private ArrayList<Medicine> medicines;

    public Patient(String name, int age, String ownerEmail) {
        this.name = name;
        this.age = age;
        this.ownerEmail = ownerEmail;
        this.medicines = new ArrayList<>();
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getOwnerEmail() { return ownerEmail; }
    public ArrayList<Medicine> getMedicines() { return medicines; }
    public void addMedicine(Medicine med) { this.medicines.add(med); }
}