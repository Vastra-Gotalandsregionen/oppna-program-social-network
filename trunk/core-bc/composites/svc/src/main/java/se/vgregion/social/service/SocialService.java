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
public class SocialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialService.class);
    private static final String LANGUAGE = "language";
    private static final String USER_ABOUT = "userAbout";

    private SocialRequestLocalService socialRequestLocalService;
    private SocialRelationLocalService socialRelationLocalService;
    private UserLocalService userLocalService;
    private IGImageLocalService imageLocalService;
    private UserExpandoHelper userExpandoHelper;

    @Autowired
    public SocialService(UserExpandoHelper userExpandoHelper) {
        this.imageLocalService = IGImageLocalServiceUtil.getService();
        this.socialRequestLocalService = SocialRequestLocalServiceUtil.getService();
        this.socialRelationLocalService = SocialRelationLocalServiceUtil.getService();
        this.userLocalService = UserLocalServiceUtil.getService();
        this.userExpandoHelper = userExpandoHelper;
    }

    public SocialService(SocialRequestLocalService socialRequestLocalService,
                         SocialRelationLocalService socialRelationLocalService,
                         UserLocalService userLocalService, IGImageLocalService imageLocalService,
                         UserExpandoHelper userExpandoHelper) {
        this.socialRequestLocalService = socialRequestLocalService;
        this.socialRelationLocalService = socialRelationLocalService;
        this.userLocalService = userLocalService;
        this.imageLocalService = imageLocalService;
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

    public boolean hasFriendRelation(long userId, long otherUserId) throws SocialServiceException {
        try {
            return socialRelationLocalService.hasRelation(userId, otherUserId, SocialRelationConstants.TYPE_BI_FRIEND);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public String getLanguage(User userToShow) {
        return userExpandoHelper.get(LANGUAGE, userToShow);
    }

    public List<User> getFriends(User userToShow) throws SocialServiceException {
        try {
            return userLocalService.getSocialUsers(userToShow.getUserId(), 0,
                    SocialRelationConstants.TYPE_BI_FRIEND, new UserLoginDateComparator());
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public int getUserRequestsCount(User loggedInUser, int status) throws SocialServiceException {
        try {
            return socialRequestLocalService.getReceiverUserRequestsCount(loggedInUser.getUserId(), status);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public List<SocialRequest> getUserRequests(User loggedInUser) throws SocialServiceException {
        try {
            return socialRequestLocalService.getReceiverUserRequests(loggedInUser.getUserId(),
                    SocialRequestConstants.STATUS_PENDING, 0, getUserRequestsCount(loggedInUser, SocialRequestConstants.STATUS_PENDING));
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public User getUserById(long userId) throws SocialServiceException {
        try {
            return userLocalService.getUserById(userId);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void deletePortrait(long userId) throws SocialServiceException {
        try {
            userLocalService.deletePortrait(userId);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void updatePortrait(long userId, byte[] bytes) throws SocialServiceException {
        try {
            userLocalService.deletePortrait(userId);
            userLocalService.updatePortrait(userId, bytes);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void addFriendRequest(Long requester, Long otherUserId) throws SocialServiceException {
        try {
            socialRequestLocalService.addRequest(requester, 0L, User.class.getName(), requester,
                    SocialRelationConstants.TYPE_BI_FRIEND, "", otherUserId);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public SocialRequest getSocialRequest(Long requestId) throws SocialServiceException {
        try {
            return socialRequestLocalService.getSocialRequest(requestId);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    protected void addFriendRelation(long userId, long receiverUserId) throws SocialServiceException {
        try {
            socialRelationLocalService.addRelation(userId, receiverUserId, SocialRelationConstants.TYPE_BI_FRIEND);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void confirmRequest(SocialRequest socialRequest) throws SocialServiceException {
        addFriendRelation(socialRequest.getUserId(), socialRequest.getReceiverUserId());

        socialRequest.setStatus(SocialRequestConstants.STATUS_CONFIRM);
        try {
            socialRequestLocalService.updateSocialRequest(socialRequest);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void rejectRequest(SocialRequest socialRequest) throws SocialServiceException {
        socialRequest.setStatus(SocialRequestConstants.STATUS_IGNORE);
        try {
            socialRequestLocalService.updateSocialRequest(socialRequest);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void setUserAbout(User user, String value) {
        userExpandoHelper.set(USER_ABOUT, value, user);
    }

    public String getUserAbout(User user) {
        return userExpandoHelper.get(USER_ABOUT, user);
    }

    public void updateUser(User user) throws SocialServiceException {
        try {
            userLocalService.updateUser(user);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void setLanguage(User loggedInUser, String value) {
        userExpandoHelper.set(LANGUAGE, value, loggedInUser);
    }

    public boolean hasCurrentFriendRequest(long requesterId, long receiverUserId) throws SocialServiceException {
        try {
            boolean hasRequest = socialRequestLocalService.hasRequest(requesterId, User.class.getName(),
                    requesterId, SocialRelationConstants.TYPE_BI_FRIEND, receiverUserId,
                    SocialRequestConstants.STATUS_PENDING);
            return hasRequest;
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }
    }

    public void removeFriend(long loggedInUserId, Long userToDeleteId) throws SocialServiceException {

        try {
            socialRelationLocalService.deleteRelation(loggedInUserId, userToDeleteId, SocialRelationConstants.TYPE_BI_FRIEND);
        } catch (PortalException e) {
            throw new SocialServiceException(e);
        } catch (SystemException e) {
            throw new SocialServiceException(e);
        }

    }
}
