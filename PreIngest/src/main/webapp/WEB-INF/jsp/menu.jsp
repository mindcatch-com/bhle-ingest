<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<ul>
	<li class="yui3-menuitem"><a class="yui3-menuitem-content" href="<spring:url value ='/filebrowser/index' />">Submissions</a></li>
	<li class="yui3-menuitem"><a class="yui3-menuitem-content" href="<spring:url value ='/packages/queue' />">Queue</a></li>
	<li class="yui3-menuitem"><a class="yui3-menuitem-content" href="<spring:url value ='/packages/monitor' />">Monitor</a></li>
	<li class="yui3-menuitem"><a class="yui3-menuitem-content" href="<spring:url value ='/guidelines' />">Guidelines</a></li>
</ul>