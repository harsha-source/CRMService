package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerDTO {

    private long id;

    private String userId;


    private String name;


    private String phone;

    private String address;

    private String address2;


    private String city;


    private String state;


    private String zipcode;


    public CustomerDTO(long id, String userId, String name, String phone, String address, String address2, String city, String state, String zipcode) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }
    // You can add more fields as needed to match your customer model
}


