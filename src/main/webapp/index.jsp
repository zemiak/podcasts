<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<jsp:useBean id="podcasts" scope="request" class="com.zemiak.podcasts.service.jsp.PodcastJSPService" />

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Zemiak Podcasts</title>
    </head>
    <body>
        <h1>List of available podcasts</h1>
        <ul>
        <c:forEach var="item" items="${podcasts.podcasts}">
            <li>
                <a href="/podcasts/feed.jsp?name=${item.name}/feed">
                    ${item.title}
                </a>
            </li>
        </c:forEach>
        </ul>
    </body>
</html>
