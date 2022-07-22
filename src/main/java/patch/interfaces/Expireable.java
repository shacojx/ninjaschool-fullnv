package patch.interfaces;

public interface Expireable {
    boolean isExpired();

    void tick();
}
