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

package se.vgregion.social.controller.profilestream;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.social.model.SocialActivity;
import com.liferay.portlet.social.model.SocialActivityFeedEntry;
import com.liferay.portlet.social.model.SocialRequest;
import com.liferay.portlet.social.service.SocialActivityInterpreterLocalServiceUtil;
import com.liferay.portlet.social.service.SocialActivityLocalServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.social.service.SocialService;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.List;

/**
 *
 * @author Patrik Bergström
 */

@Controller
@RequestMapping(value = "VIEW")
public class ProfileStreamController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProfileStreamController.class);

    private SocialService service;

    @Autowired
    public ProfileStreamController(SocialService service) {
        this.service = service;
    }

    @RenderMapping
    public String showFeed(RenderRequest request, RenderResponse response) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        try {
            List<SocialActivity> activities = SocialActivityLocalServiceUtil.getActivities(SocialRequest.class.getName(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
            for (SocialActivity activity : activities) {
                SocialActivityFeedEntry entry = SocialActivityInterpreterLocalServiceUtil.interpret(activity, themeDisplay);

            }
        } catch (SystemException e) {
            //todo move all this try block to the SocialService
            e.printStackTrace();
        }

        return "stream";
    }

}
