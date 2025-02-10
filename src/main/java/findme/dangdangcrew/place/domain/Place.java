package findme.dangdangcrew.place.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "places")
public class Place {
    @Id
    private String id;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String categoryGroupCode;
    private String categoryGroupName;
    private String phone;
    private String placeUrl;
    private double x;
    private double y;

    @Builder
    public Place(String id, String placeName, String addressName, String roadAddressName,
                 String categoryGroupCode, String categoryGroupName, String phone,
                 String placeUrl, double x, double y) {
        this.id = id;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryGroupName = categoryGroupName;
        this.phone = phone;
        this.placeUrl = placeUrl;
        this.x = x;
        this.y = y;
    }
}
