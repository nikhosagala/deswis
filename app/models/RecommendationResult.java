package models;

import com.deswis.utils.Config;
import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nikhosagala on 12/06/2017.
 */
@Entity
@Table(name = "recommendation_result")
public class RecommendationResult extends Model {
    private static final long serialVersionUID = 1L;
    public static Finder<Integer, RecommendationResult> find = new Finder<>(Integer.class, RecommendationResult.class);
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Integer id;
    public double interestValue;
    @ManyToOne
    public Destination destination;
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
