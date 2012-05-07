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

package se.vgregion.social.controller.friends;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.social.model.SocialRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.social.service.SocialService;
import se.vgregion.social.service.SocialServiceException;

import javax.portlet.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrik Bergström
 */

@Controller
@RequestMapping(value = "VIEW")
public class FriendsController {

    private final Logger LOGGER = LoggerFactory.getLogger(FriendsController.class);

    private SocialService service;

    @Autowired
    public FriendsController(SocialService service) {
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
    public String showFriendsView(RenderRequest request, RenderResponse response, Model model) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        String vgrProfileId = request.getParameter("vgrProfileId");

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

            model.addAttribute("ownProfile", ownProfile);

            List<User> friends = service.getFriends(userToShow);

            model.addAttribute("friends", friends);

            // Friend requests
            List<SocialRequest> friendRequests = service.getUserRequests(loggedInUser);

            // Need to make a map with the whole User objects in order to get e.g. the full name.
            Map<SocialRequest, User> friendRequestUserMap = new HashMap<SocialRequest, User>();
            for (SocialRequest friendRequest : friendRequests) {
                User receiverUser = service.getUserById(friendRequest.getUserId());
                friendRequestUserMap.put(friendRequest, receiverUser);
            }

            model.addAttribute("friendRequests", friendRequestUserMap);

        } catch (SocialServiceException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return "friends_view";
    }

    @ActionMapping(params = "action=acceptFriend")
    public void acceptFriend(ActionRequest request, ActionResponse response) throws SocialServiceException {
        Long requestId = Long.valueOf(request.getParameter("requestId"));
        SocialRequest socialRequest = service.getSocialRequest(requestId);

        User requester = service.getUserById(socialRequest.getUserId());

        request.setAttribute("acceptedFriend", requester);

        service.confirmRequest(socialRequest);
    }

    @ActionMapping(params = "action=rejectFriend")
    public void rejectFriend(ActionRequest request, ActionResponse response, Model model) throws SocialServiceException {
        Long requestId = Long.valueOf(request.getParameter("requestId"));
        SocialRequest socialRequest = service.getSocialRequest(requestId);

        request.setAttribute("message", "Du har ignorerat denna förfrågan.");

        service.rejectRequest(socialRequest);
    }

    @ActionMapping(params = "action=deleteFriend")
    public void deleteFriend(ActionRequest request, ActionResponse response) throws SocialServiceException {
        Long userToDelete = Long.valueOf(request.getParameter("userId"));

        long loggedInUserId = getLoggedInUserId(request);
        if (service.hasFriendRelation(loggedInUserId, userToDelete)) {
            service.removeFriend(loggedInUserId, userToDelete);
            request.setAttribute("message", "Du har raderat denna vän.");
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
