<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<style type="text/css">
    .upload-image-form-wrapper form {
        display: inline;
    }

    .upload-image-form-wrapper .file-input {
        width: 100%
    }

    .upload-image-form-wrapper .submit-cancel {
        padding-top: 10px;
        /*width: 100%;*/
        margin-left: auto;
        margin-right: auto;
    }

    .upload-image-form-wrapper input.submit {
        padding: 4px;
        float: right;
    }

    .upload-image-form-wrapper a {
        float: right;
        margin-left: 8px;
        padding: 4px;
    }
</style>

<portlet:renderURL var="renderUrl">
</portlet:renderURL>
<portlet:actionURL var="uploadProfileImage">
    <portlet:param name="action" value="uploadProfileImage"/>
</portlet:actionURL>
<portlet:actionURL var="cancelUrl">
    <portlet:param name="action" value="cancel"/>
</portlet:actionURL>

<div class="upload-image-form-wrapper">
    <form action="${uploadProfileImage}" enctype="multipart/form-data" method="post">
        <span>Sökväg: </span>
        <input class="file-input" type="file" name="profileImage"/>

        <div class="submit-cancel clearfix">
            <a class="buttonlink" href="<%= renderUrl %>"><span>Avbryt</span></a>
            <input class="submit" type="submit" value="Ladda upp"/>
        </div>
    </form>
</div>