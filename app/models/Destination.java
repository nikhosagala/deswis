package models;

import com.deswis.utils.Config;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "destination")
public class Destination extends Model {
    private static final long serialVersionUID = 1L;
    public static final Finder<Integer, Destination> find = new Finder<>(Integer.class, Destination.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;
    public String address;
    public String description;
    public String phone;
    public String categories;
    public Double distance;
    public Double duration;
    public Double lengthOfVisit;
    public Double rating;
    public Double tariff;
    public String urlSource;
    public String monday;
    public String tuesday;
    public String wednesday;
    public String thursday;
    public String friday;
    public String saturday;
    public String sunday;

    @ManyToOne
    public GooglePlace googlePlace;

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
