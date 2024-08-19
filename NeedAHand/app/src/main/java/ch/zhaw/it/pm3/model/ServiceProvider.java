package ch.zhaw.it.pm3.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a ServiceProvider.
 */
public class ServiceProvider extends User{
    private String companyName;
    private Skills skill;
    private double rating;

    public ServiceProvider(UserInfo userInfo, int id, double rating) {
        super(userInfo, id);
        this.companyName = userInfo.companyName;
        this.skill = userInfo.skill;
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (this.rating == 0){
            this.rating = rating;
        } else {
            this.rating = (rating + this.rating) / 2.0;
        }
    }

    public Skills getSkill() {
        return skill;
    }
}
