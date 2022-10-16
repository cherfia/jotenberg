package dev.gotenberg.common.exceptions;

public class MarginMalformedException extends ArithmeticException {
    public MarginMalformedException() {
        super("negative margins are not allowed");
    }
}
