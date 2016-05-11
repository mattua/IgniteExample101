package model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Created by mattua on 09/05/2016.
 */
public class Company {


    public int getId() {
        return id;
    }

    // This annotation  - can be indexed or not
    @QuerySqlField(index=true)
    private int id;


    @QuerySqlField(index=true)
    private String name;

    public Company(int id, String name){

        this.id=id;
        this.name=name;


    }

    @Override
    public String toString(){

        return "util.ExamplesUtils.Company [id="+id+", name="+name+" ]";
    }



}
