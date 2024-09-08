package test.njsharpe.dblang.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.njsharpe.dblang.annotation.Alias;
import net.njsharpe.dblang.annotation.BelongsTo;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;

@Getter
@Setter
@ToString
public class HouseholdPerson {

    private int id;

    @Alias("first_name")
    private String firstName;

    @Alias("last_name")
    private String lastName;

    @Alias("date_of_birth")
    private Date dateOfBirth;

    @Nullable
    @Alias("id")
    @BelongsTo(Household.class)
    private Integer householdId;

    @Nullable
    @BelongsTo(Household.class)
    private String address1;

    @Nullable
    @BelongsTo(Household.class)
    private String address2;

    @Nullable
    @BelongsTo(Household.class)
    private String city;

    @Nullable
    @BelongsTo(Household.class)
    private String state;

    @Nullable
    @Alias("zip_code")
    @BelongsTo(Household.class)
    private String zipCode;

    public HouseholdPerson() {}

    public HouseholdPerson(Person person) {
        this(person,null);
    }

    public HouseholdPerson(Person person, @Nullable Household household) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.dateOfBirth = person.getDateOfBirth();

        if(household != null) {
            this.householdId = household.getId();
            this.address1 = household.getAddress1();
            this.address2 = household.getAddress2();
            this.city = household.getCity();
            this.state = household.getState();
            this.zipCode = household.getState();
        }
    }

}
