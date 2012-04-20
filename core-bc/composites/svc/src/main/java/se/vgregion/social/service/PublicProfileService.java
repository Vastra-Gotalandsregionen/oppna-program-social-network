package se.vgregion.social.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.comparator.UserLoginDateComparator;
import com.liferay.portlet.imagegallery.service.IGImageLocalService;
import com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil;
import com.liferay.portlet.social.model.SocialRelationConstants;
import com.liferay.portlet.social.model.SocialRequest;
import com.liferay.portlet.social.model.SocialRequestConstants;
import com.liferay.portlet.social.service.SocialRelationLocalService;
import com.liferay.portlet.social.service.SocialRelationLocalServiceUtil;
import com.liferay.portlet.social.service.SocialRequestLocalService;
import com.liferay.portlet.social.service.SocialRequestLocalServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.vgregion.liferay.expando.UserExpandoHelper;

import java.util.List;

/**
 * @author Patrik Bergström
 */
@Service
public class PublicProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProfileService.class);
    private static final String USER_ABOUT = "userAbout";

    private SocialRequestLocalService socialRequestLocalService;
    private SocialRelationLocalService socialRelationLocalService;
    private UserLocalService userLocalService;
    private IGImageLocalService imageLocalService;
    private UserExpandoHelper userExpandoHelper;

    @Autowired
    public PublicProfileService(UserExpandoHelper userExpandoHelper) {
        this.imageLocalService = IGImageLocalServiceUtil.getService();
        this.socialRequestLocalService = SocialRequestLocalServiceUtil.getService();
        this.socialRelationLocalService = SocialRelationLocalServiceUtil.getService();
        this.userLocalService = UserLocalServiceUtil.getService();
        this.userExpandoHelper = userExpandoHelper;
    }

    public User getUserByScreenName(long companyId, String screenName) {
        try {
            return userLocalService.getUserByScreenName(companyId, screenName);
        } catch (PortalException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (SystemException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean hasFriendRelation(long userId, long otherUserId) throws PublicProfileServiceException {
        try {
            return socialRelationLocalService.hasRelation(userId, otherUserId, SocialRelationConstants.TYPE_BI_FRIEND);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public String getLanguage(User userToShow) {
        return userExpandoHelper.get("language", userToShow);
    }

    public List<User> getFriends(User userToShow) throws PublicProfileServiceException {
        try {
            return userLocalService.getSocialUsers(userToShow.getUserId(), 0,
                    SocialRelationConstants.TYPE_BI_FRIEND, new UserLoginDateComparator());
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public int getUserRequestsCount(User loggedInUser) throws PublicProfileServiceException {
        try {
            return socialRequestLocalService.getUserRequestsCount(loggedInUser.getUserId());
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public List<SocialRequest> getUserRequests(User loggedInUser) throws PublicProfileServiceException {
        try {
            return socialRequestLocalService.getUserRequests(loggedInUser.getUserId(), 0,
                    getUserRequestsCount(loggedInUser));
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public User getUserById(long userId) throws PublicProfileServiceException {
        try {
            return userLocalService.getUserById(userId);
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public void deletePortrait(long userId) throws PublicProfileServiceException {
        try {
            userLocalService.deletePortrait(userId);
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public void updatePortrait(long userId, byte[] bytes) throws PublicProfileServiceException {
        try {
            userLocalService.deletePortrait(userId);
            userLocalService.updatePortrait(userId, bytes);
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public void addFriendRequest(Long userId, Long otherUserId) throws PublicProfileServiceException {
        try {
            socialRequestLocalService.addRequest(userId, 0L, User.class.getName(), otherUserId.hashCode()
                    + userId.hashCode(), SocialRequestConstants.STATUS_PENDING, "", otherUserId);
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public SocialRequest getSocialRequest(Long requestId) throws PublicProfileServiceException {
        try {
            return socialRequestLocalService.getSocialRequest(requestId);
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public void addFriendRelation(long userId, long receiverUserId) throws PublicProfileServiceException {
        try {
            socialRelationLocalService.addRelation(userId, receiverUserId, SocialRelationConstants.TYPE_BI_FRIEND);
        } catch (PortalException e) {
            throw new PublicProfileServiceException(e);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public void confirmRequest(SocialRequest socialRequest) throws PublicProfileServiceException {
        socialRequest.setStatus(SocialRequestConstants.STATUS_CONFIRM);
        try {
            socialRequestLocalService.updateSocialRequest(socialRequest);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }

    public void setUserAbout(User user, String value) {
        userExpandoHelper.set(USER_ABOUT, value, user);
    }

    public String getUserAbout(User user) {
        return userExpandoHelper.get(USER_ABOUT, user);
    }

    public void updateUser(User user) throws PublicProfileServiceException {
        try {
            userLocalService.updateUser(user);
        } catch (SystemException e) {
            throw new PublicProfileServiceException(e);
        }
    }
}
