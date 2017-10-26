package models;

import com.deswis.utils.Config;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recommendation")
public class Recommendation extends Model {
    private static final long serialVersionUID = 1L;
    public static final Finder<Integer, Recommendation> find = new Finder<>(Integer.class, Recommendation.class);
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Integer id;
    public Double rating;
    public Double distanceWeight;
    public Double durationWeight;
    public Double interestWeight;
    public Double ratingWeight;
    public String name;
    public double processTime;
    @Transient
    public double threshold;
    public Double lat;
    public Double lng;
    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<RecommendationDetail> recommendationDetails;
    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<RecommendationResult> recommendationResults;
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

