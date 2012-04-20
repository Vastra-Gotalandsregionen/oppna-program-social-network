<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var="uploadProfileImage">
    <portlet:param name="action" value="uploadProfileImage"/>
</portlet:actionURL>

<form action="${uploadProfileImage}" enctype="multipart/form-data" method="post">
    <span>Sökväg: </span><input type="file" name="profileImage" />
    <div><input type="submit" value="Ladda upp"/></div>
</form>