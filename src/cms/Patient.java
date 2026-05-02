package cms;

public class Patient {
    private String patientId;
    private String fullName;
    private String dob;
    private String gender;
    private String contactNumber;
    private String address;
    private String emergencyContact;
    private String medicalConditions;
    private String lastUpdated;

    public Patient(String patientId, String fullName, String dob, String gender,
                   String contactNumber, String address, String emergencyContact,
                   String medicalConditions, String lastUpdated) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.medicalConditions = medicalConditions;
        this.lastUpdated = lastUpdated;
    }

    public String getPatientId()        { return patientId; }
    public String getFullName()         { return fullName; }
    public String getDob()              { return dob; }
    public String getGender()           { return gender; }
    public String getContactNumber()    { return contactNumber; }
    public String getAddress()          { return address; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getMedicalConditions(){ return medicalConditions; }
    public String getLastUpdated()      { return lastUpdated; }

    public void setFullName(String v)         { this.fullName = v; }
    public void setContactNumber(String v)    { this.contactNumber = v; }
    public void setAddress(String v)          { this.address = v; }
    public void setEmergencyContact(String v) { this.emergencyContact = v; }
    public void setMedicalConditions(String v){ this.medicalConditions = v; }
    public void setLastUpdated(String v)      { this.lastUpdated = v; }

    @Override
    public String toString() {
        return patientId + " – " + fullName;
    }
}
