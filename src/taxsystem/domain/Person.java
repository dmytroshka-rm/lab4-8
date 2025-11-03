package taxsystem.domain;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTaxId() { return taxId; }
    public List<IncomeSource> getIncomeSources() { return incomeSources; }
    public List<TaxBenefit> getTaxBenefits() { return taxBenefits; }
}
