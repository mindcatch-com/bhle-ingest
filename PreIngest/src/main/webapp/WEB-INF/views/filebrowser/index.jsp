<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="yui3-u-1">	
	<script type='text/javascript'>
		$(function() {
			$("form").submit(
					function() {
						// Serialize standard form fields:
						var formData = $(this).serializeArray();

						// then append Dynatree selected 'checkboxes':
						var tree = $("#tree").dynatree("getTree");
						formData = formData.concat(tree.serializeArray());

						// and/or add the active node as 'radio button':
						if (tree.getActiveNode()) {
							formData.push({
								name : "activeNode",
								value : tree.getActiveNode().data.key
							});
						}

						// alert("POSTing this:\n" + jQuery.param(formData));

						$.post("sendNodes", formData, function(response,
								textStatus, xhr) {
							// alert("POST returned " + response + ", " + textStatus);
						});
						return false;
					});
		});
		//assuming we have an element on the page with an ID
		//attribute "foo":
		YUI().use('node-base', function(Y) {
			var foo = Y.one("#sipsub");
			var foo2 = Y.one("#sipsub2");
			var disableBtn = function() {
				foo.set('disabled','disabled');
				foo2.set('disabled','disabled');
			};
			var enableBtn = function(e) {	
				e.removeAttribute('disabled');
			};	
		    var handleClick = function(e, arg1) {
		        disableBtn();
				Y.later(2500,this,enableBtn,[foo],false);
				Y.later(2500,this,enableBtn,[foo2],false);
		    };			
			foo.on("click", handleClick);
			foo2.on("click", handleClick);
		});
		$(document).ready(function() {
			$('#fbSelectLang').change(function() {
				$("#fbDetectLangHint").text("");
				$("#fbDetectOnOff").val([]);
			});
		})	
	</script>
	<br />
	<!-- Add a <div> element where the tree should appear: -->
	<form method="POST" action="submitNodes">
		OCR-Language: <select id="fbSelectLang" size="1" name="lang">
<option value=""></option>
<option value="bul">bul</option>
<option value="cat">cat</option>
<option value="ces">ces</option>
<option value="dan">dan</option>
<option value="dan-frak">dan-frak</option>
<option value="data">data</option>
<option value="deu">deu</option>
<option value="deu-f">deu-f</option>
<option value="ell">ell</option>
<option value="eng">eng</option>
<option value="fin">fin</option>
<option value="fra">fra</option>
<option value="hun">hun</option>
<option value="ind">ind</option>
<option value="ita">ita</option>
<option value="lav">lav</option>
<option value="lit">lit</option>
<option value="nld">nld</option>
<option value="nor">nor</option>
<option value="pol">pol</option>
<option value="por">por</option>
<option value="ron">ron</option>
<option value="rus">rus</option>
<option value="slk">slk</option>
<option value="slv">slv</option>
<option value="spa">spa</option>
<option value="srp">srp</option>
<option value="swe">swe</option>
<option value="tgl">tgl</option>
<option value="tur">tur</option>
<option value="ukr">ukr</option>
<option value="vie">vie</option>
</select>
<input type="checkbox" id="fbDetectOnOff" checked="checked" value="true"/><label for="fbDetectOnOff">detect</label>
<span id="fbDetectLangHint"></span>
		<div><input type="submit" value="Submit selection for processing" id="sipsub2" /></div>
		<div>Select folders to be processed: </div>
		<!-- The name attribute is used by tree.serializeArray()  -->
		<div id="tree" id="selNodes" name="selNodes"></div>
		<div><input type="submit" value="Submit selection for processing" id="sipsub" /></div>
	</form>
</div>
