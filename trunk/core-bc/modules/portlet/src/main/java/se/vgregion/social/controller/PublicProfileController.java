/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.social.controller;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.social.model.SocialRelation;
import com.liferay.portlet.social.model.SocialRequest;
import com.liferay.portlet.social.model.SocialRequestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.multipart.MultipartActionRequest;
import se.vgregion.social.service.PublicProfileService;
import se.vgregion.social.service.PublicProfileServiceException;

import javax.portlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller class performs a search for actors.
 *
 * @author simgo3
 */

@Controller
@RequestMapping(value = "VIEW")
public class PublicProfileController {

    private final Logger LOGGER = LoggerFactory.getLogger(PublicProfileController.class);

    //    private ImageLocalService imageLocalService;
//    private SocialRequestLocalService socialRequestLocalService;
//    private SocialRelationLocalService socialRelationLocalService;
//    private UserLocalService userLocalService;
//    private IGImageLocalService imageLocalService;
//    private ExpandoUtil expandoUtil;
    private PublicProfileService service;

    @Autowired
    public PublicProfileController(PublicProfileService service) {
//        this.imageLocalService = ImageLocalServiceUtil.getService();
//        this.imageLocalService = IGImageLocalServiceUtil.getService();
//        this.socialRequestLocalService = SocialRequestLocalServiceUtil.getService();
//        this.socialRelationLocalService = SocialRelationLocalServiceUtil.getService();
//        this.userLocalService = UserLocalServiceUtil.getService();
//        this.expandoUtil = expandoUtil;
        this.service = service;
    }

    /**
     * This method shows actor article view.
     *
     * @param request  the request
     * @param response the response
     * @param model    the model
     * @return the profile view
     */
    @RenderMapping
    public String showActorArticleView(RenderRequest request, RenderResponse response, Model model) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        String vgrProfileId = request.getParameter("vgrProfileId");

        try {

            String loggedInUserScreenName = themeDisplay.getUser().getScreenName();
            User loggedInUser = service.getUserByScreenName(themeDisplay.getCompanyId(),
                    loggedInUserScreenName);

            boolean ownProfile;
            User userToShow;
            if (vgrProfileId == null || loggedInUserScreenName.equals(vgrProfileId)) {
                vgrProfileId = loggedInUserScreenName;
                System.out.println("vgrProfileId = " + vgrProfileId);
                ownProfile = true;
                userToShow = loggedInUser;
            } else {
                ownProfile = false;
                userToShow = service.getUserByScreenName(themeDisplay.getCompanyId(), vgrProfileId);
            }

            if (!ownProfile) {
                boolean isFriend = service.hasFriendRelation(userToShow.getUserId(),
                        loggedInUser.getUserId()); // Don't know why it should be "2" but I saw it in some ContactsUtil class

                model.addAttribute("isFriend", isFriend);
            }

            userToShow.getEmailAddress();

            System.out.println("tf = " + userToShow.getDisplayURL(themeDisplay));

            model.addAttribute("ownProfile", ownProfile);
            model.addAttribute("profileImage", "/image/user_male_portrait?img_id=" + userToShow.getPortraitId());
            model.addAttribute("user", userToShow);

//            String language = (String) userToShow.getExpandoBridge().getAttribute("language");
            String language = service.getLanguage(userToShow);
            model.addAttribute("language", language);

            List<User> friends = service.getFriends(userToShow);

            model.addAttribute("friends", friends);

            // Friend requests
            List<SocialRequest> friendRequests = service.getUserRequests(loggedInUser);

            Map<SocialRequest, User> friendRequestUserMap = new HashMap<SocialRequest, User>();
            for (SocialRequest friendRequest : friendRequests) {
                if (friendRequest.getStatus() == SocialRequestConstants.STATUS_PENDING) {
                    User receiverUser = service.getUserById(friendRequest.getReceiverUserId());
                    friendRequestUserMap.put(friendRequest, receiverUser);
                }
            }

            model.addAttribute("friendRequests", friendRequestUserMap);

        } catch (Exception e) {
            e.printStackTrace();
        } catch (PublicProfileServiceException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return "view";
    }

    @RenderMapping(params = "action=showEditProfileImage")
    public String showEditProfileImage() {
        return "edit-profile-image";
    }

    @ActionMapping(params = "action=uploadProfileImage")
    public void uploadProfileImage(MultipartActionRequest request) throws IOException, PublicProfileServiceException {
        MultipartFile profileImageInput = request.getFile("profileImage");
        long loggedInUserId = getLoggedInUserId(request);
        service.updatePortrait(loggedInUserId, profileImageInput.getBytes());
    }

    @ResourceMapping(value = "submitProperty")
    public void submitProperty(ResourceRequest request, ResourceResponse response)
            throws PublicProfileServiceException {
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        User loggedInUser = getLoggedInUser(request);

        if ("jobTitle".equals(key)) {
            System.out.println("jobTitle submitted");
            loggedInUser.setJobTitle(value);
            service.updateUser(loggedInUser);
        } else if ("userAbout".equals(key)) {
            service.setUserAbout(loggedInUser, value);
        }

    }

    @ActionMapping(params = "action=requestFriend")
    public void requestFriend(ActionRequest request, ActionResponse response) throws PublicProfileServiceException {
        Long userId = Long.valueOf(request.getParameter("userId"));
        SocialRelation s;
        long loggedInUserId = getLoggedInUserId(request);
        service.addFriendRequest(userId, loggedInUserId);
    }

    @ActionMapping(params = "action=acceptFriend")
    public void acceptFriend(ActionRequest request, ActionResponse response) throws PublicProfileServiceException {
        long loggedInUserId = getLoggedInUserId(request);

        Long requestId = Long.valueOf(request.getParameter("requestId"));

        SocialRequest socialRequest = service.getSocialRequest(requestId);
        long receiverUserId = socialRequest.getReceiverUserId();

        service.addFriendRelation(loggedInUserId, receiverUserId);

        service.confirmRequest(socialRequest);
    }

    private String getLoggedInUserScreenName(PortletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return themeDisplay.getUser().getScreenName();
    }

    private User getLoggedInUser(PortletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return themeDisplay.getUser();
    }

    private long getLoggedInUserId(PortletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return themeDisplay.getUserId();
    }

    /**
     * Handles the exceptions.
     *
     * @return the string
     */
    public String handleException() {
        return "errorPage";
    }

}
