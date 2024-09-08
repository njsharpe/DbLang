package test.njsharpe.dblang.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.njsharpe.dblang.annotation.Alias;
import net.njsharpe.dblang.annotation.AutoIncrement;
import net.njsharpe.dblang.annotation.Ignore;
import net.njsharpe.dblang.annotation.PrimaryKey;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;

@Getter
@Setter
@ToString
@Alias("person")
public class Person {

    @PrimaryKey
    @AutoIncrement
    private int id;

    @Alias("first_name")
    private String firstName;

    @Alias("last_name")
    private String lastName;

    @Ignore
    private char middleInitial;

    @Alias("date_of_birth")
    private Date dateOfBirth;

    @Nullable
    @Alias("household_id")
    private Integer householdId;

    @Nullable
    @Alias("creditcard_id")
    private Integer creditCardId;

}
