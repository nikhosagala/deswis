package models;

import com.deswis.utils.Config;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "document")
public class Document extends Model {
    private static final long serialVersionUID = 1L;
    public static Finder<Integer, Document> find = new Finder<>(Integer.class, Document.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;
    public String url;
    public Date uploadedAt;

    @Transient
    public String getUploadedAt() {
        return Config.getInstance().getStringDate(uploadedAt);
    }

}
