package models;

import com.deswis.utils.Config;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nikhosagala on 04/07/2017.
 */
@Entity
@Table(name = "survey")
public class Survey extends Model {
    private static final long serialVersionUID = 1L;
    public static final Finder<Integer, Survey> find = new Finder<>(Integer.class, Survey.class);
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Integer id;
    public String name;
    public int age;
    public String familiar;
    public String pu1;
    public String pu2;
    public String pu3;
    public String pu4;
    public String pu5;
    public String pu6;
    public String pu7;
    public String eou1;
    public String eou2;
    public String eou3;
    public String eou4;
    public String eou5;
    public String tr1;
    public String tr2;
    public String tr3;
    public String tr4;
    public String pe1;
    public String pe2;
    public String pe3;
    public String pe4;
    public String bi1;
    public String bi2;
    public String bi3;
    public Integer recommendationId;
    public String comment;
    public Date createdAt;
    public Date updatedAt;

    @Transient
    public String getCreatedAt() {
        return Config.getInstance().getStringDate(createdAt);
    }

    @Transient
    public String getUpdatedAt() {
        return Config.getInstance().getStringDate(updatedAt);
    }
}
