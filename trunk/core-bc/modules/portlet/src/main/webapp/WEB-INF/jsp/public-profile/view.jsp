<%--

    Copyright 2010 Västra Götalandsregionen

      This library is free software; you can redistribute it and/or modify
      it under the terms of version 2.1 of the GNU Lesser General Public
      License as published by the Free Software Foundation.

      This library is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Lesser General Public License for more details.

      You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307  USA


--%>

<%@page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style type="text/css">

    .public-profile span.portlet-msg-success {
        display: inline;
    }

    .profile-edit-trigger {
        background: transparent url(/regionportalen-theme/images/common/edit.png) right center no-repeat;
        cursor: pointer;
        display: inline-block;
        height: 16px;
        text-indent: -9999em;
        width: 16px;
    }

    .public-profile-friends-list li {
        list-style: none;
    }

    .public-profile-friends-list li span, .public-profile-friends-list li img {
        display: block;
        float: left;
        line-height: 30px;
        /*padding: 5px 10px 0px 0px;*/
    }

    .accept-reject .accept, .accept-reject .reject {
        background-repeat: no-repeat;
        height: 30px;
        width: 30px;
    }

    .accept-reject .accept {
        background-image: url('/rp-new-theme/images/common/checked.png');
    }

    .accept-reject .reject {
        background-image: url('/rp-new-theme/images/common/close.png');
    }
</style>

<portlet:actionURL var="requestFriend">
    <portlet:param name="action" value="requestFriend"/>
    <portlet:param name="userId" value="${user.userId}"/>
</portlet:actionURL>
<portlet:renderURL var="editProfileImage">
    <portlet:param name="action" value="showEditProfileImage"/>
</portlet:renderURL>

<div class="portlet-body public-profile">
    <div class="summary-container">
        <h2>${user.fullName}</h2>
        <c:choose>
            <c:when test="${ownProfile}">
                <a id="<portlet:namespace/>editProfileImage" href="${editProfileImage}" title="Ändra bild">
                    <img alt="<liferay-ui:message key="user-portrait" />" class="user-profile-image"
                         src="${profileImage}"/>
                </a>
            </c:when>
            <c:otherwise>
                <img alt="<liferay-ui:message key="user-portrait" />" class="user-profile-image" src="${profileImage}"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${isFriend}">
                <p class="remove-friend">
                    <liferay-ui:icon image="join" label="true" message="remove-friend" url=""/>
                </p>
            </c:when>
            <c:when test="${isFriendRequestPending}">
                <div class="portlet-msg-info add-as-friend pending">
                    <liferay-ui:message key="friend-requested"/>
                </div>
            </c:when>
            <c:when test="${not isFriend and not ownProfile}">

                <p class="add-as-friend">
                    <a href="${requestFriend}">
                        <liferay-ui:icon image="join" label="true" message="add-as-friend" url=""/>
                    </a>
                </p>
            </c:when>
        </c:choose>

        <p>
            <span class="user-job-title"><liferay-ui:message key="job-title"/></span>
            : <span id="<portlet:namespace/>jobTitleText" class="job-title-text">${user.jobTitle}</span>
            <c:if test="${ownProfile}">
                <span class="profile-edit-trigger profile-edit-trigger-job-title">Redigera text</span>
                <span id="<portlet:namespace/>jobTitleCheck" style="opacity: 0;"
                      class="portlet-msg-success">Sparat!</span>

                <%--<form id="<portlet:namespace/>jobTitleForm" action="${updateJobTitle}">
                    <input id="<portlet:namespace/>jobTitleTextInput" type="hidden" name="jobTitle"/>
                </form>--%>
            </c:if>
        </p>

        <p>
      <span class="user-about"><liferay-ui:message key="about-me"/>
      </span>
            : <span id="<portlet:namespace/>userAboutText" class="user-about-text">${userAbout}</span>
            <c:if test="${ownProfile}">
                <span class="profile-edit-trigger profile-edit-trigger-user-about">Redigera text</span>
                <span id="<portlet:namespace/>userAboutCheck" style="opacity: 0;"
                      class="portlet-msg-success">Sparat!</span>
            </c:if>
        </p>

        <p>
      <span class="user-language"><liferay-ui:message key="language"/>
      </span>
            : ${language}
        </p>

    </div>

    <%--<c:if test="true">
        <br/>
        <portlet:renderURL var="editURL"/>

        <liferay-ui:icon image="edit" label="true" url="${editURL}"/>
    </c:if>--%>

    <c:if test="${ownProfile}">
        <h3>Vänförfrågningar</h3>
        <ul class="public-profile-friends-list clearfix">

            <c:forEach items="${friendRequests}" var="friendRequest">
                <c:set var="user" value="${friendRequest.value}"/>
                <portlet:actionURL var="acceptFriend">
                    <portlet:param name="action" value="acceptFriend"/>
                    <portlet:param name="requestId" value="${friendRequest.key.requestId}"/>
                </portlet:actionURL>
                <portlet:actionURL var="rejectFriend">
                    <portlet:param name="action" value="rejectFriend"/>
                    <portlet:param name="requestId" value="${friendRequest.key.requestId}"/>
                </portlet:actionURL>

                <li>
                    <div style="height: 30px">
                        <img alt="${user.fullName}"
                             src="/image/user_${user.male ? 'male' : 'female'}_portrait?img_id=${user.portraitId}"
                             height="30"/>
                        <span>${user.fullName}</span>
                        <span class="accept-reject">
                            <a href="${acceptFriend}"><span title="Godkänn" class="accept">&nbsp;</span></a>
                            <a href="${rejectFriend}"><span title="Avslå" class="reject">&nbsp;</span></a>
                        </span>
                    </div>
                </li>
            </c:forEach>

        </ul>
    </c:if>

    <h3><liferay-ui:message key="friends"/></h3>
    <ul class="public-profile-friends-list clearfix">
        <c:forEach items="${friends}" var="friend">
            <li>
                <span>
                    <a href="/group/vgregion/social/-/user/${friend.screenName}">
                        <img alt="test" src="/image/user_male_portrait?img_id=${friend.portraitId}" height="30">
                            ${friend.fullName}
                    </a>
                </span>
            </li>
        </c:forEach>
    </ul>
</div>

<c:if test="${ownProfile}">
    <%--<aui:script use="aui-base,profile">--%>
    <%--&lt;%&ndash;<%@ include file="editIFeedFormJs.jspf" %>&ndash;%&gt;--%>
    <%--</aui:script>--%>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/social-config.js"></script>
    <aui:script use="aui-base,social-config">
        <%@ include file="profile.jspf" %>
    </aui:script>
</c:if>
