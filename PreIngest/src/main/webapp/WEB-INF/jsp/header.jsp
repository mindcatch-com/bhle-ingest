<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div style="float: right">
	<sec:authentication property="principal.username" />
	<a href="<spring:url value="/j_spring_security_logout" htmlEscape="true" />">Logout</a>
</div>
<br>
<div>
<h1 class="heading">PreIngest Tool</h1>
</div>

 <!--
<span style="float: right">
    <a href="?lang=en">en</a>
    |
    <a href="?lang=de">de</a>
</span>
 //-->
 <!-- 
<span style="float: left">
    <a href="?theme=default">def</a>
    |
    <a href="?theme=black">blk</a>
    |
    <a href="?theme=blue">blu</a>
    |
    <a href="?theme=green">gre</a>
</span>
//-->