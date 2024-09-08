package test.njsharpe.dblang.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.njsharpe.dblang.annotation.Alias;
import net.njsharpe.dblang.annotation.AutoIncrement;
import net.njsharpe.dblang.annotation.Ignore;
import net.njsharpe.dblang.annotation.PrimaryKey;

@Getter
@Setter
@ToString
@Alias("household")
public class Household {

    @PrimaryKey
    @AutoIncrement
    private int id;

    private String address1;

    private String address2;

    private String city;

    private String state;

    @Alias("zip_code")
    private String zipCode;

}
