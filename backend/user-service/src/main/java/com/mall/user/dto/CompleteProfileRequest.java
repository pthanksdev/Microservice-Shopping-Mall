package com.mall.user.dto;

import com.mall.user.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteProfileRequest {
    @NotNull(message = "accountType is required: CUSTOMER or VENDOR")
    private Role accountType;
}
