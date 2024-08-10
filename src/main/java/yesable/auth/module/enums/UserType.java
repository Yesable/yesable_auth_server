package yesable.auth.module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {
    MEMBER(0, "MEMBER"),
    COMPANY(1, "COMPANY"),
    ;

    private final Integer value;
    private final String name;
}
