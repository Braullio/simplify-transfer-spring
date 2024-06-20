package br.com.braullio.simplify_transfer_spring.user;

public enum UserType {
    COMUM("COMUM"),
    LOJISTA("LOJISTA");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserType fromString(String value) {
        for (UserType userType : UserType.values()) {
            if (userType.name().equalsIgnoreCase(value)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + value);
    }

    @Override
    public String toString() {
        return description;
    }
}