<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Transform Files</title>
</head>
<body>

<h1>
	Transformed Files List
</h1>

<c:forEach items="${fileList}" var="file_item">
	<c:out value="${file_item}"/><br/>
</c:forEach>
</body>
</html>
