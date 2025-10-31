package kdodds.userservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Jacksonized
@Data
@Builder
public class PagedUsersResponseDto extends RepresentationModel<PagedUsersResponseDto> {

    @JsonProperty("users")
    private List<UserResponseDto> users;

    @JsonProperty("page")
    private PageDto page;

}
