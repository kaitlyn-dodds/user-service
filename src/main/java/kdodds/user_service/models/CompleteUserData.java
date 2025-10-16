package kdodds.user_service.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CompleteUserData {

    private User user;
    private UserProfile userProfile;
    private List<UserAddress> userAddresses;

}
