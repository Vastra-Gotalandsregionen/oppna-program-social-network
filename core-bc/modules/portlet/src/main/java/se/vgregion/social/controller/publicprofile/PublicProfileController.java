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

package se.vgregion.social.controller.publicprofile;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
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
import se.vgregion.social.service.SocialService;
import se.vgregion.social.service.SocialServiceException;
import se.vgregion.social.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.portlet.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This controller class performs a search for actors.
 *
 * @author simgo3
 * @author Patrik Bergström
 */

@Controller
@RequestMapping(value = "VIEW")
public class PublicProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProfileController.class);
    private static final String VGR_PROFILE_ID = "vgrProfileId";

    private SocialService service;

    @Autowired
    public PublicProfileController(SocialService service) {
        this.service = service;
    }

    @ActionMapping
    public void setPublicRenderParameters(ActionRequest request, ActionResponse response) {
        String vgrProfileId = request.getParameter(VGR_PROFILE_ID);
        response.setRenderParameter(VGR_PROFILE_ID, vgrProfileId); // Public render parameter
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
    public String showPublicProfileView(RenderRequest request, RenderResponse response, Model model) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        String vgrProfileId = request.getParameter(VGR_PROFILE_ID);

        try {

            String loggedInUserScreenName = themeDisplay.getUser().getScreenName();
            User loggedInUser = service.getUserByScreenName(themeDisplay.getCompanyId(),
                    loggedInUserScreenName);

            boolean ownProfile;
            User userToShow;
            if (vgrProfileId == null || loggedInUserScreenName.equals(vgrProfileId)) {
                ownProfile = true;
                userToShow = loggedInUser;
            } else {
                ownProfile = false;
                userToShow = service.getUserByScreenName(themeDisplay.getCompanyId(), vgrProfileId);
            }

            if (!ownProfile) {
                boolean isFriend = service.hasFriendRelation(userToShow.getUserId(), loggedInUser.getUserId());
                model.addAttribute("isFriend", isFriend);

                boolean hasFriendRequest = service.hasCurrentFriendRequest(loggedInUser.getUserId(),
                        userToShow.getUserId());
                model.addAttribute("hasFriendRequest", hasFriendRequest);

                boolean otherUserHasFriendRequest = service.hasCurrentFriendRequest(userToShow.getUserId(),
                        loggedInUser.getUserId());
                model.addAttribute("otherUserHasFriendRequest", otherUserHasFriendRequest);
            }

            userToShow.getEmailAddress();

            model.addAttribute("ownProfile", ownProfile);
            model.addAttribute("profileImage", "/image/user_male_portrait?img_id=" + userToShow.getPortraitId());
            model.addAttribute("user", userToShow);

            model.addAttribute("userAbout", service.getUserAbout(userToShow));
            String language = service.getLanguage(userToShow);
            model.addAttribute("language", language);

        } catch (SocialServiceException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return "view";
    }

    @RenderMapping(params = "action=showEditProfileImage")
    public String showEditProfileImage() {
        return "edit-profile-image";
    }

    @ActionMapping(params = "action=uploadProfileImage")
    public void uploadProfileImage(MultipartActionRequest request) throws IOException, SocialServiceException {
        MultipartFile profileImageInput = request.getFile("profileImage");

        if (profileImageInput == null || profileImageInput.getSize() <= 0) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final int croppedWidth = 160;
        final int croppedHeight = 160;

        BufferedImage bufferedImage = ImageIO.read(profileImageInput.getInputStream());

        ImageUtil.writeImageToStream(baos, croppedWidth, croppedHeight, bufferedImage);

        long loggedInUserId = getLoggedInUserId(request);
        service.updatePortrait(loggedInUserId, baos.toByteArray());
    }

    @ResourceMapping(value = "submitProperty")
    public void submitProperty(ResourceRequest request, ResourceResponse response)
            throws SocialServiceException {
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        User loggedInUser = getLoggedInUser(request);

        if ("jobTitle".equals(key)) {
            loggedInUser.setJobTitle(value);
            service.updateUser(loggedInUser);
        } else if ("userAbout".equals(key)) {
            service.setUserAbout(loggedInUser, value);
        } else if ("language".equals(key)) {
            service.setLanguage(loggedInUser, value);
        }

    }

    @ActionMapping(params = "action=requestFriend")
    public void requestFriend(ActionRequest request, ActionResponse response) throws SocialServiceException {
        Long userId = Long.valueOf(request.getParameter("userId"));
        long loggedInUserId = getLoggedInUserId(request);

        // Whether they already have a relation
        if (!service.hasFriendRelation(loggedInUserId, userId)) {
            if (service.hasCurrentFriendRequest(loggedInUserId, userId)) {
                // Say nothing to the user since he/she probably just has reposted the last request
                return;
            }
            // The other way around
            if (service.hasCurrentFriendRequest(userId, loggedInUserId)) {
                request.setAttribute("message", "Denna person har redan en förfrågan till dig");
                return;
            }
            service.addFriendRequest(loggedInUserId, userId);
        }
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
