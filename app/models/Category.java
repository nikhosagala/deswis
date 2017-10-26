package models;

import com.deswis.utils.Config;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "category")
public class Category extends Model {
    private static final long serialVersionUID = 1L;
    public static final Finder<Integer, Category> find = new Finder<>(Integer.class, Category.class);
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Integer id;
    public String name;
    public String children;
    public String hint;
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
