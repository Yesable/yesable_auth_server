package yesable.auth.module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidateType {

    SUCCESS(0, "SUCCESS"),
    FAILED(1, "FAILED"),
    EXPIRED_TIME(2, "EXPIRED_TIME"),
    ;

    private final Integer value;
    private final String name;

}

