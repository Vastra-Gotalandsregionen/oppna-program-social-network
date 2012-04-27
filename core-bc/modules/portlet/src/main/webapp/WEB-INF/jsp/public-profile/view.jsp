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

    .public-profile .user-property-heading {
        font-weight: bold;
    }

    .public-profile .user-about-text {
        min-height: 50px;
        min-width: 350px;
    }

    .profile-edit-trigger {
        background: transparent url(/regionportalen-theme/images/common/edit.png) right center no-repeat;
        cursor: pointer;
        display: inline-block;
        height: 16px;
        text-indent: -9999em;
        width: 16px;
    }

</style>

<portlet:actionURL var="requestFriend">
    <portlet:param name="action" value="requestFriend"/>
    <portlet:param name="userId" value="${user.userId}"/>
</portlet:actionURL>
<portlet:renderURL var="editProfileImage">
    <portlet:param name="action" value="showEditProfileImage"/>
</portlet:renderURL>

<c:set var="userUrlPrefix" value="/group/vgregion/social/-/user/"/>

<div class="portlet-body public-profile">
    <div class="summary-container">
        <c:if test="${not empty message}">
            <div class="portlet-msg-info">${message}</div>
        </c:if>

        <h2><a href="${userUrlPrefix}${user.screenName}"><c:out value="${user.fullName}"/></a></h2>
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
            <c:when test="${hasFriendRequest}">
                <div class="portlet-msg-info">
                    <liferay-ui:message key="friend-requested"/>
                </div>
            </c:when>
            <c:when test="${otherUserHasFriendRequest}">
                <div class="portlet-msg-info">
                    <c:out value="${user.fullName} har redan en förfrågan på dig."/>
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
            <span class="user-property-heading user-job-title"><liferay-ui:message key="job-title"/></span>:
            <span id="<portlet:namespace/>jobTitleText" class="job-title-text">
                <c:out value="${user.jobTitle}"/>
            </span>
            <c:if test="${ownProfile}">
                <span class="profile-edit-trigger profile-edit-trigger-job-title">Redigera text</span>
                <span id="<portlet:namespace/>jobTitleCheck" style="display: none;"
                      class="portlet-msg-success">Sparat!</span>
            </c:if>
        </p>

        <p>
            <span class="user-property-heading user-about"><liferay-ui:message key="about-me"/></span>:
            <c:if test="${ownProfile}">
                <span class="profile-edit-trigger profile-edit-trigger-user-about">Redigera text</span>
                <span id="<portlet:namespace/>userAboutCheck" style="display: none;"
                      class="portlet-msg-success">Sparat!</span>
            </c:if>
            <div id="<portlet:namespace/>userAboutText" class="user-about-text"><c:out value="${userAbout}"/></div>
        </p>

        <p>
            <span class="user-property-heading user-language"><liferay-ui:message key="language"/></span>:
            <span id="<portlet:namespace/>languageText" class="language-text"><c:out value="${language}"/></span>
            <c:if test="${ownProfile}">
                <span class="profile-edit-trigger profile-edit-trigger-language">Redigera text</span>
                <span id="<portlet:namespace/>languageCheck" style="display: none;"
                      class="portlet-msg-success">Sparat!</span>
            </c:if>
        </p>
    </div>

<c:if test="${ownProfile}">
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/social-config.js"></script>
    <aui:script use="aui-base,social-config">
        <%@ include file="profile.jspf" %>
    </aui:script>
</c:if>
