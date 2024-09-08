package test.njsharpe.dblang.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.njsharpe.dblang.annotation.Alias;
import net.njsharpe.dblang.annotation.AutoIncrement;
import net.njsharpe.dblang.annotation.PrimaryKey;

@Getter
@Setter
@ToString
@Alias("creditcard")
public class CreditCard {

    @PrimaryKey
    @AutoIncrement
    private int id;

    private String name;

    private String number;

    private int month;

    private int year;

    private String cvc;

    @Alias("zip_code")
    private String zipCode;

}
