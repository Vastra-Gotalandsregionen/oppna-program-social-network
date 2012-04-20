package se.vgregion.social.image;

/**
 * @author Patrik Bergström
 */
public class ProfileImageUtil {

    public static long getProfileImageId(String loggedInUser, ProfileImageType type) {
        return (loggedInUser + type.name()).hashCode();
    }

    public static enum ProfileImageType {
        FULL, MEDIUM, SMALL
    }
}
