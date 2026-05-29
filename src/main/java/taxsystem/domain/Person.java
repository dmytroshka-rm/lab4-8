package taxsystem.domain;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String personId;
    private String firstName;
    private String lastName;
    private String taxId;
    private List<IncomeSource> incomeSources;
    private List<TaxBenefit> taxBenefits;

    public Person(String personId, String firstName, String lastName, String taxId) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.taxId = taxId;
        this.incomeSources = new ArrayList<>();
        this.taxBenefits = new ArrayList<>();
    }

    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public List<IncomeSource> getIncomeSources() { return incomeSources; }
    public void setIncomeSources(List<IncomeSource> incomeSources) { this.incomeSources = incomeSources; }

    public List<TaxBenefit> getTaxBenefits() { return taxBenefits; }
    public void setTaxBenefits(List<TaxBenefit> taxBenefits) { this.taxBenefits = taxBenefits; }
}
