package models;

import com.deswis.utils.Config;
import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "recommendation_detail")
public class RecommendationDetail extends Model {
    private static final long serialVersionUID = 1L;
    public static Finder<Integer, RecommendationDetail> find = new Finder<>(Integer.class, RecommendationDetail.class);
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Integer id;
    public double interestValue;
    @ManyToOne
    public Category category;
    @ManyToOne
    @JsonBackReference
    public Recommendation recommendation;
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

