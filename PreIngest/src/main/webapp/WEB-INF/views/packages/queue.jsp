<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="yui3-u-1">
	<div id="queue">
	</div>
</div>
<script type="text/javascript">

YUI().use("datasource-io", "datasource-jsonschema", "datatable-datasource", "datasource-polling", "datatable-sort", function(Y) {
	
	var myDataSource = new Y.DataSource.IO({source:"<spring:url value='queue/json' />",
											ioConfig:{data:"show=1", 
													  headers: {'Accept': 'application/json'}
													  }
	});
 
    myDataSource.plug(Y.Plugin.DataSourceJSONSchema, {
        schema: {
        	resultListLocator: "Result",
            resultFields: ["date","path","files","size","user"]
        }
    });
    
    var cols = ["date","path","files","size","user"];
    
    var table = new Y.DataTable.Base({
        columnset: cols,
        summary: "All currently active queued folders",
        caption: "Queue (reloads every 5 sec)"
    });
    
    table.plug(Y.Plugin.DataTableDataSource, {
        datasource: myDataSource
    }).render("#queue");
    
    table.datasource.load();
    
    myDataSource.setInterval(5000, {       
        callback: {
            success: Y.bind(table.datasource.onDataReturnInitializeTable, table.datasource),
            failure: Y.bind(table.datasource.onDataReturnInitializeTable, table.datasource)
        }
    });
});


</script>