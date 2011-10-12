<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="yui3-u-1">
	<div id="monitor"></div>
</div>

<script type="text/javascript">

YUI().use("datasource-io", "datasource-jsonschema", "datatable-datasource", "datasource-polling", function(Y) {
	
	var myDataSource = new Y.DataSource.IO({source:"<spring:url value='monitor/json' />",
											ioConfig:{data:"show=1", 
													  headers: {'Accept': 'application/json'}
													  }
	});
 
    myDataSource.plug(Y.Plugin.DataSourceJSONSchema, {
        schema: {
        	resultListLocator: "Result",
    		resultFields: ["displaydate","parentfolder","filename","username","observation"]
        }
    });
    
    var cols = ["displaydate","parentfolder","filename","username","observation"];

    
    var table = new Y.DataTable.Base({
        columnset: cols,
        summary: "Monitoring current system activity",
        caption: "Monitoring last 100 pre-ingest activities (reloads every 5 seconds)"
    });
    
    table.plug(Y.Plugin.DataTableDataSource, {
        datasource: myDataSource
    }).render("#monitor");
    
    table.datasource.load();
    
    myDataSource.setInterval(5000, {       
        callback: {
            success: Y.bind(table.datasource.onDataReturnInitializeTable, table.datasource),
            failure: Y.bind(table.datasource.onDataReturnInitializeTable, table.datasource)
        }
    });
});


</script>