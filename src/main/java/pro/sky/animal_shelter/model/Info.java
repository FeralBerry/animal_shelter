package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "info")
public class Info {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rules")
    private String rules;
    @Column(name = "documents")
    private String documents;
    @Column(name = "transportation")
    private String transportation;
    @Column(name = "house_for_a_puppy")
    private String houseForAPuppy;
    @Column(name = "home_for_an_adult_animal")
    private String homeForAnAdultAnimal;
    @Column(name = "home_for_an_animal_with_disabilities")
    private String homeForAnAnimalWithDisabilities;
    @Column(name = "tips_from_a_dog_handler")
    private String tipsFromADogHandler;
    @Column(name = "recommendations_of_a_dog_handler")
    private String recommendationsOfADogHandler;
    @Column(name = "reasons_for_refusal")
    private String reasonsForRefusal;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRules() {
        return rules;
    }
    public void setRules(String rules) {
        this.rules = rules;
    }
    public String getDocuments() {
        return documents;
    }
    public void setDocuments(String documents) {
        this.documents = documents;
    }
    public String getTransportation() {
        return transportation;
    }
    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }
    public String getHouseForAPuppy() {
        return houseForAPuppy;
    }
    public void setHouseForAPuppy(String houseForAPuppy) {
        this.houseForAPuppy = houseForAPuppy;
    }
    public String getHomeForAnAdultAnimal() {
        return homeForAnAdultAnimal;
    }
    public void setHomeForAnAdultAnimal(String homeForAnAdultAnimal) {
        this.homeForAnAdultAnimal = homeForAnAdultAnimal;
    }
    public String getHomeForAnAnimalWithDisabilities() {
        return homeForAnAnimalWithDisabilities;
    }
    public void setHomeForAnAnimalWithDisabilities(String homeForAnAnimalWithDisabilities) {
        this.homeForAnAnimalWithDisabilities = homeForAnAnimalWithDisabilities;
    }
    public String getTipsFromADogHandler() {
        return tipsFromADogHandler;
    }
    public void setTipsFromADogHandler(String tipsFromADogHandler) {
        this.tipsFromADogHandler = tipsFromADogHandler;
    }
    public String getRecommendationsOfADogHandler() {
        return recommendationsOfADogHandler;
    }
    public void setRecommendationsOfADogHandler(String recommendationsOfADogHandler) {
        this.recommendationsOfADogHandler = recommendationsOfADogHandler;
    }
    public String getReasonsForRefusal() {
        return reasonsForRefusal;
    }
    public void setReasonsForRefusal(String reasonsForRefusal) {
        this.reasonsForRefusal = reasonsForRefusal;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Info info = (Info) o;
        return Objects.equals(id, info.id) && Objects.equals(rules, info.rules) && Objects.equals(documents, info.documents) && Objects.equals(transportation, info.transportation) && Objects.equals(houseForAPuppy, info.houseForAPuppy) && Objects.equals(homeForAnAdultAnimal, info.homeForAnAdultAnimal) && Objects.equals(homeForAnAnimalWithDisabilities, info.homeForAnAnimalWithDisabilities) && Objects.equals(tipsFromADogHandler, info.tipsFromADogHandler) && Objects.equals(recommendationsOfADogHandler, info.recommendationsOfADogHandler) && Objects.equals(reasonsForRefusal, info.reasonsForRefusal);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, rules, documents, transportation, houseForAPuppy, homeForAnAdultAnimal, homeForAnAnimalWithDisabilities, tipsFromADogHandler, recommendationsOfADogHandler, reasonsForRefusal);
    }
    @Override
    public String toString() {
        return "Info{" +
                "id=" + id +
                ", rules='" + rules + '\'' +
                ", documents='" + documents + '\'' +
                ", transportation='" + transportation + '\'' +
                ", houseForAPuppy='" + houseForAPuppy + '\'' +
                ", homeForAnAdultAnimal='" + homeForAnAdultAnimal + '\'' +
                ", homeForAnAnimalWithDisabilities='" + homeForAnAnimalWithDisabilities + '\'' +
                ", tipsFromADogHandler='" + tipsFromADogHandler + '\'' +
                ", recommendationsOfADogHandler='" + recommendationsOfADogHandler + '\'' +
                ", reasonsForRefusal='" + reasonsForRefusal + '\'' +
                '}';
    }
}
