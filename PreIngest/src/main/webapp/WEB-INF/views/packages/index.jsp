<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="contenttabs">
	<ul>
		<li><a class="loadingtab" href="#loading">Loading</a></li>
		<li><a href="#wrapup">WrapUp</a></li>
	</ul>
	<div>
		<div id="loading">
			<div id="loadingcontent">
				List of all queued folders<br />
				<ul>
				</ul>
			</div>
		</div>
		<div id="wrapup">
			<div id="wrapupcontent">
				List of all queued archival informationpackages:<br />
				<ul>
				</ul>
			</div>
		</div>
	</div>
</div>