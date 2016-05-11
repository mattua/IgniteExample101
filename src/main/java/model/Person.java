package model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Created by mattua on 09/05/2016.
 */
public class Person {

    @QuerySqlField(index=true)
    private int id;

    // dont index name because we are not going to query by it
    @QuerySqlField
    private String name;

    @QuerySqlField(index=true)
    private int companyId;

    @QuerySqlField(index=true)
    private double salary;

    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }


    public Person(int id, String name, int companyId, double salary) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "util.ExamplesUtils.Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyId=" + companyId +
                ", salary=" + salary +
                '}';
    }
}
