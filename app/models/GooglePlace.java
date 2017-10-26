package models;

import com.deswis.utils.Config;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "google_place")
public class GooglePlace extends Model {
    private static final long serialVersionUID = 1L;
    public static final Finder<Integer, GooglePlace> find = new Finder<>(Integer.class, GooglePlace.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public Double lat;
    public Double lng;
    public String placeId;
    public String name;
    public String contentType;
    public String types;
    public byte[] imageData;
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
