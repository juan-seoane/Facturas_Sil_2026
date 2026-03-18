package infraestructure.helpers;

public enum _Auth {
    AUTH_OK(1),
    AUTH_FAIL(0),
    AUTH_RETRY(2);

    private final int Code;

    _Auth(int Code) {
        this.Code = Code;
    }

    public int getCode() {
        return this.Code;
    }
}
