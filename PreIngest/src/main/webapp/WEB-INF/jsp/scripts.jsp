<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script src="<spring:url value ='/resources/jquery/jquery.js' />" type='text/javascript'></script>
<script src="<spring:url value ='/resources/jquery/jquery-ui.custom.js' />" type='text/javascript'></script>
<script src="<spring:url value ='/resources/dynatree/jquery.dynatree.js' />" type='text/javascript'></script>

<!-- Add code to initialize the tree when the document is loaded: -->
<script type='text/javascript'>
$(function(){
    $("#tree").dynatree({
    	checkbox: true,
    	selectMode: 2,
        onActivate: function(node) {
            //alert("You activated: node # " + node.data.key);
        },
        onSelect: function(flag, node) {
        	// what node.data contains: http://wwwendt.de/tech/dynatree/doc/dynatree-doc.html#h4.2.1
        		
        	if(flag) { if ($("#fbDetectOnOff:checkbox:checked").val()) {
	            jQuery.ajax({
		            url: "${pageContext.request.contextPath}/filebrowser/detectLanguage",
		            data: {"node": node.data.key
		                   },
		            async: false,
		            success: function(data) {
		            	if(data == "") {
			            	$("#fbSelectLang").val(data);
			            	$("#fbDetectLangHint").text("(" + node.data.title + ": no language code found)");
		            	} else {
		            		$("#fbSelectLang").val(data);
			            	$("#fbDetectLangHint").text("(from " + node.data.title + ")");
		            	}
		            }
		            
	            
	        	});
        	}}
        },
        onDeactivate: function(node) {
            //alert("You deactivated " + node);
        },
	    initAjax: {url: "${pageContext.request.contextPath}/filebrowser/ajaxTree",
               data: {key: "root", // Optional arguments to append to the url
                      mode: "all"
                      }
               },
        onLazyRead: function(node){
            node.appendAjax({url: "${pageContext.request.contextPath}/filebrowser/sendData",
                               data: {"key": node.data.key, // Optional url arguments
                                      "mode": "all"
                                      }
                              });
        }
    });
});

</script>

<!-- Include the required JavaScript libraries: -->
<script src="<spring:url value ='/resources/yui/build/yui/yui-min.js' />"></script>


<script type='text/javascript'>
//Call the "use" method, passing in "node-menunav".  This will load the
//script and CSS for the MenuNav Node Plugin and all of the required
//dependencies.

YUI({ filter: 'raw' }).use("node-menunav", function(Y) {

	//  Retrieve the Node instance representing the root menu
	//  (<div id="productsandservices">) and call the "plug" method
	//  passing in a reference to the MenuNav Node Plugin.
	
	var menu = Y.one("#menu");
	menu.plug(Y.Plugin.NodeMenuNav);
	menu.get("ownerDocument").get("documentElement").removeClass("yui3-loading");

});
</script>