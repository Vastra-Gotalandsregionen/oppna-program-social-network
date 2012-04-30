<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style type="text/css">
    .friends-portlet .friend-list-link img, .friends-portlet .friend-list-link span {
        float: left;
        line-height: 30px;
    }

    .friends-portlet a:hover {
        cursor: pointer; /* IE7 doesn't do this automatically */
    }

    .friends-portlet .friend-list-link span {
        margin-left: 10px;
    }

    .friends-portlet .friend-list-row {
        height: 40px;
    }

    .friends-portlet .friends-list {
        list-style: none;
    }

    .friends-portlet .accept-reject .accept, .friends-portlet .accept-reject .reject, .friends-portlet .delete {
        float: left;
        height: 30px;
        width: 30px;
    }

    .friends-portlet .accept-reject .accept {
        background: url('/rp-new-theme/images/common/checked.png') center no-repeat;
    }

    .friends-portlet .accept-reject .reject, .friends-portlet .delete {
        background: url('/rp-new-theme/images/common/close.png') center no-repeat;
    }</style>

<portlet:actionURL var="requestFriend">
    <portlet:param name="action" value="requestFriend"/>
    <portlet:param name="userId" value="${user.userId}"/>
</portlet:actionURL>

<c:set var="userUrlPrefix" value="/group/vgregion/social/-/user/"/>

<div class="portlet-body friends-portlet">

    <c:if test="${not empty message}">
        <div class="portlet-msg-info"><c:out value="${message}"/></div>
    </c:if>
    <c:if test="${not empty acceptedFriend}">
        <div class="portlet-msg-info">
            <c:out value="Du är nu vän med "/>
            <a href="${userUrlPrefix}${acceptedFriend.screenName}"><c:out value="${acceptedFriend.fullName}."/></a>
        </div>
    </c:if>

    <c:if test="${ownProfile and fn:length(friendRequests) gt 0}">
        <h3>Vänförfrågningar</h3>
        <ul class="friends-list clearfix">

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
                    <div class="friend-list-row">
                        <a class="friend-list-link" href="${userUrlPrefix}${user.screenName}">
                            <img alt="test" src="/image/user_male_portrait?img_id=${user.portraitId}" height="30">
                            <span><c:out value="${user.fullName}"/></span>
                        </a>
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
    <ul class="friends-list">
        <c:forEach items="${friends}" var="friend">
            <portlet:actionURL var="deleteFriend">
                <portlet:param name="action" value="deleteFriend"/>
                <portlet:param name="userId" value="${friend.userId}"/>
            </portlet:actionURL>
            <li>
                <div class="friend-list-row">
                    <a class="friend-list-link" href="${userUrlPrefix}${friend.screenName}">
                        <img alt="test" src="/image/user_male_portrait?img_id=${friend.portraitId}" height="30">
                        <span>${friend.fullName}</span>
                    </a>
                    <c:if test="${ownProfile}">
                        <span class="delete-friend">
                            <a href="${deleteFriend}"><span title="Ta bort" class="delete"></span></a>
                        </span>
                    </c:if>
                </div>
            </li>
        </c:forEach>
    </ul>
</div>