package test.njsharpe.dblang;

import net.njsharpe.dblang.DbConnection;
import net.njsharpe.dblang.DbFactory;
import net.njsharpe.dblang.Maker;
import net.njsharpe.dblang.SqlRuntimeException;
import net.njsharpe.dblang.query.Query;
import net.njsharpe.dblang.sqlite.SqliteDbFactory;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.njsharpe.dblang.dto.*;

import java.nio.file.Path;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteDbConnectionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteDbConnectionTest.class);

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private Path path;

    @BeforeEach
    public void init() {
        LOGGER.info("init()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            db.execute("create table person (id integer primary key autoincrement, last_name text, first_name text, date_of_birth integer, household_id integer, creditcard_id integer)");
            db.execute("create table household (id integer primary key autoincrement, address1 text, address2 text, city text, state text, zip_code text)");
            int insertedHouseholdCount = db.execute("insert into household (address1, address2, city, state, zip_code) values (?, ?, ?, ?, ?)",
                    Maker.make(() -> {
                        Map<Integer, Object> values = new HashMap<>();
                        values.put(1, "111 Fake Address Ln.");
                        values.put(2, null);
                        values.put(3, "Springfield");
                        values.put(4, "IL");
                        values.put(5, "01089");
                        return values;
                    }));
            Validate.isTrue(insertedHouseholdCount >= 1, "Households not inserted");
            int insertedPeopleCount = db.execute("insert into person (last_name, first_name, date_of_birth, household_id) values (?, ?, ?, ?), (?, ?, ?, ?)",
                    Maker.make(() -> {
                        Map<Integer, Object> values = new HashMap<>();
                        values.put(1, "Sharpe");
                        values.put(2, "Nelson");
                        values.put(3, Date.valueOf("2001-03-31"));
                        values.put(4, 1);
                        values.put(5, "Moore");
                        values.put(6, "Vincent");
                        values.put(7, Date.valueOf("2024-02-17"));
                        values.put(8, 1);
                        return values;
                    }));
            Validate.isTrue(insertedPeopleCount >= 1, "People not inserted");
        }
    }

    @Test
    public void selectTest() {
        LOGGER.info("selectTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<Person> query = db.from(Person.class);
            List<Person> people = db.select(query);
            Assertions.assertFalse(people.isEmpty());
            Assertions.assertEquals(2, people.size());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void selectAsyncTest() {
        LOGGER.info("selectAsyncTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.openAsync(database.toString()).get()) {
            Query<Person> query = db.from(Person.class);
            List<Person> people = db.selectAsync(query).get();
            Assertions.assertFalse(people.isEmpty());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void singleTest() {
        LOGGER.info("singleTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<Person> query = db.from(Person.class);
            Person person = db.single(query);
            Assertions.assertNotNull(person);
            Assertions.assertEquals(1, person.getId());
            Assertions.assertEquals("Nelson", person.getFirstName());
            Assertions.assertEquals("Sharpe", person.getLastName());
            Assertions.assertEquals(Date.valueOf("2001-03-31"), person.getDateOfBirth());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void singleAsyncTest() {
        LOGGER.info("singleAsyncTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.openAsync(database.toString()).get()) {
            Query<Person> query = db.from(Person.class);
            Person person = db.singleAsync(query).get();
            Assertions.assertNotNull(person);
            Assertions.assertEquals(1, person.getId());
            Assertions.assertEquals("Nelson", person.getFirstName());
            Assertions.assertEquals("Sharpe", person.getLastName());
            Assertions.assertEquals(Date.valueOf("2001-03-31"), person.getDateOfBirth());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void inlineWhereTest() {
        LOGGER.info("inlineWhereTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            List<Person> people = db.select(Person.class, c ->
                    c.equal("last_name", "Moore"));
            Person person = Assert.assertSingle(people);
            Assertions.assertEquals(2, person.getId());
            Assertions.assertEquals("Vincent", person.getFirstName());
            Assertions.assertEquals("Moore", person.getLastName());
            Assertions.assertEquals(Date.valueOf("2024-02-17"), person.getDateOfBirth());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void fullWhereTest() {
        LOGGER.info("fullWhereTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<Person> query = db.from(Person.class)
                    .where(c -> c.equal("id", 1));
            List<Person> people = db.select(query);
            Person person = Assert.assertSingle(people);
            Assertions.assertEquals(1, person.getId());
            Assertions.assertEquals("Nelson", person.getFirstName());
            Assertions.assertEquals("Sharpe", person.getLastName());
            Assertions.assertEquals(Date.valueOf("2001-03-31"), person.getDateOfBirth());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void joinTest() {
        LOGGER.info("joinTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<HouseholdPerson> query = db.from(Person.class)
                    .join(Household.class, c -> c.from("household_id").to("id"))
                    .select(HouseholdPerson.class);
            List<HouseholdPerson> people = db.select(query);
            Assert.assertCollection(people, p -> {
                Assertions.assertEquals(1, p.getId());
                Assertions.assertEquals("Nelson", p.getFirstName());
                Assertions.assertEquals("Sharpe", p.getLastName());
                Assertions.assertEquals(Date.valueOf("2001-03-31"), p.getDateOfBirth());
                Assertions.assertEquals(1, p.getHouseholdId());
                Assertions.assertEquals("111 Fake Address Ln.", p.getAddress1());
                Assertions.assertEquals("Springfield", p.getCity());
                Assertions.assertEquals("IL", p.getState());
                Assertions.assertEquals("01089", p.getZipCode());
            }, p -> {
                Assertions.assertEquals(2, p.getId());
                Assertions.assertEquals("Vincent", p.getFirstName());
                Assertions.assertEquals("Moore", p.getLastName());
                Assertions.assertEquals(Date.valueOf("2024-02-17"), p.getDateOfBirth());
                Assertions.assertEquals(1, p.getHouseholdId());
                Assertions.assertEquals("111 Fake Address Ln.", p.getAddress1());
                Assertions.assertEquals("Springfield", p.getCity());
                Assertions.assertEquals("IL", p.getState());
                Assertions.assertEquals("01089", p.getZipCode());
            });
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void orderByAscTest() {
        LOGGER.info("orderByAscTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<Person> query = db.from(Person.class)
                    .orderBy("date_of_birth");
            List<Person> people = db.select(query);
            Assert.assertCollection(people, p -> {
                Assertions.assertEquals(1, p.getId());
            }, p -> {
                Assertions.assertEquals(2, p.getId());
            });
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void orderByAscOnJoinTest() {
        LOGGER.info("orderByOnJoinTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<HouseholdPerson> query = db.from(Person.class)
                    .join(Household.class, c -> c.from("household_id").to("id"))
                    .orderBy("date_of_birth")
                    .select(HouseholdPerson.class);
            List<HouseholdPerson> people = db.select(query);
            Assert.assertCollection(people, p -> {
                Assertions.assertEquals(1, p.getId());
            }, p -> {
                Assertions.assertEquals(2, p.getId());
            });
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void orderByDescTest() {
        LOGGER.info("orderByDescTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<Person> query = db.from(Person.class)
                    .orderByDescending("id");
            List<Person> people = db.select(query);
            Assert.assertCollection(people, p -> {
                Assertions.assertEquals(2, p.getId());
            }, p -> {
                Assertions.assertEquals(1, p.getId());
            });
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void insertTest() {
        LOGGER.info("insertTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            long id = db.insert(Person.class, () -> {
                Person person = new Person();
                person.setLastName("Stevens");
                person.setFirstName("Zachary");
                person.setDateOfBirth(Date.valueOf("2013-09-25"));
                return person;
            });
            Assertions.assertEquals(3, id);
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void createTableTest() {
        LOGGER.info("createTableTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Assertions.assertThrowsExactly(SqlRuntimeException.class, () -> db.createTable(Person.class));
        }
    }

    @Test
    public void createTableIfNotExistsTest() {
        LOGGER.info("createTableIfNotExistsTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Assertions.assertDoesNotThrow(() -> db.createTableIfNotExists(CreditCard.class));
        }
    }

    @Test
    public void dropTableTest() {
        LOGGER.info("dropTableTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Assertions.assertDoesNotThrow(() -> db.dropTable(Household.class));
        }
    }

    @Test
    public void updateWithConditionTest() {
        LOGGER.info("updateWithConditionTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Person person = new Person();
            person.setFirstName("John");
            person.setLastName("Cena");
            person.setDateOfBirth(Date.valueOf("1976-05-02"));
            person.setHouseholdId(2);

            Assertions.assertDoesNotThrow(() ->
                    db.update(Person.class, person, c -> c.equal("id", 1)));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void updateOnlyTest() {
        LOGGER.info("updateOnlyTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Person person = new Person();
            person.setFirstName("John");
            person.setLastName("Cena");
            person.setDateOfBirth(Date.valueOf("1976-05-02"));
            person.setHouseholdId(2);

            Assertions.assertDoesNotThrow(() ->
                    db.updateOnly(Person.class, person, new String[] { "date_of_birth" }));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void updateOnlyWithConditionTest() {
        LOGGER.info("updateOnlyWithConditionTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Person person = new Person();
            person.setFirstName("John");
            person.setLastName("Cena");
            person.setDateOfBirth(Date.valueOf("1976-05-02"));
            person.setHouseholdId(2);

            Assertions.assertDoesNotThrow(() ->
                    db.updateOnly(Person.class, person, new String[] { "date_of_birth" }, c -> c.equal("id", 1)));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void deleteTest() {
        LOGGER.info("deleteTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Assertions.assertDoesNotThrow(() -> db.delete(Person.class, c -> c.equal("id", 1)));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void deleteAllTest() {
        LOGGER.info("deleteAllTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Assertions.assertDoesNotThrow(() -> db.delete(Person.class));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void betweenTest() {
        LOGGER.info("betweenTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            Query<Person> query = db.from(Person.class)
                    .where(c -> c.between("date_of_birth", Date.valueOf("2024-01-01"), Date.valueOf("2024-12-31")))
                    .orderByDescending("date_of_birth");
            List<Person> people = db.select(query);
            Assert.assertCollection(people, p -> {
                Assertions.assertEquals(2, p.getId());
                Assertions.assertEquals("Vincent", p.getFirstName());
                Assertions.assertEquals("Moore", p.getLastName());
                Assertions.assertEquals(Date.valueOf("2024-02-17"), p.getDateOfBirth());
            });
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void fullTest() {
        LOGGER.info("fullTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            db.createTableIfNotExists(CreditCard.class);

            CreditCard card = new CreditCard();
            card.setNumber("0000000000001111");
            card.setName("Steve Jobs");
            card.setMonth(2);
            card.setYear(37);
            card.setCvc("652");
            card.setZipCode("16093");

            long id = db.insert(CreditCard.class, () -> card);

            Person person = db.single(Person.class, c -> c.equal("id", 1));
            person.setCreditCardId((int) id);

            db.updateOnly(Person.class, person, new String[] { "creditcard_id" });

            Query<FullPerson> query = db.from(Person.class)
                    .join(Household.class, c -> c.from("household_id").to("id"))
                    .join(CreditCard.class, c -> c.from("creditcard_id").to("id"))
                    .where(c -> c.equal("id", 1))
                    .orderByDescending("date_of_birth")
                    .select(FullPerson.class);

            FullPerson fullPerson = db.single(query);
            Assertions.assertNotNull(fullPerson);
        }
    }

    @Test
    public void dropAndCreateInitTest() {
        LOGGER.info("dropAndCreateInitTest()");
        Path database = this.path.resolve("junit.db");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(database.toString())) {
            db.dropTable(Household.class);
            db.dropTable(Person.class);

            db.createTable(Household.class);
            db.createTable(Person.class);

            long householdId = db.insert(Household.class, () -> {
               Household household = new Household();
               household.setAddress1("112 Fake Address Ln.");
               household.setCity("Springfield");
               household.setState("IL");
               household.setZipCode("01089");
               return household;
            });

            Assertions.assertEquals(1, householdId);

            long personId = db.insert(Person.class, () -> {
                Person person = new Person();
                person.setFirstName("Nelson");
                person.setLastName("Sharpe");
                person.setDateOfBirth(Date.valueOf("2001-03-31"));
                person.setHouseholdId((int) householdId);
                return person;
            });

            Assertions.assertEquals(1, personId);
        }
    }

}
