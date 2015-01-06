<%@taglib prefix="test" uri="/WEB-INF/I18N.tld"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cherry Web Sample Application</title>

    <!-- Bootstrap -->
    <link href="/bootstrap-3.3.1/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
<body>

<div class="container">
	<h1>Cherry Web Sample Application</h1>
	
<div class="panel panel-info">
	<div class="panel-heading"><test:i18n key="addentry.topic"/></div>
	<div class="panel-body">
		<table class="info">
			<tr>
				<td><test:i18n key="addentry.name"/>&nbsp;</td>
				<td><input data-bind="value: f_name"/></td>
				<td>&nbsp;<button class="btn btn-success btn-mini" data-bind="click:doAdd"><span class="glyphicon glyphicon-plus"/></button></button></td>
		</table>
	</div>
</div>

<div class="panel panel-info">
	<div class="panel-heading"><button class="btn btn-warning btn-mini"><span class="glyphicon glyphicon-refresh" data-bind="click:loadEntryList"/>
	</button>&nbsp;&nbsp;&nbsp;<test:i18n key="list.topic"/></div>
	<div class="panel-body">
		<table class="info">
			<tbody data-bind="foreach: f_entries">
				<td><button type="button" class="btn btn-mini btn-danger" data-bind="click: $parent.doRemove"><span class="glyphicon glyphicon-remove"></button>&nbsp;&nbsp;&nbsp;</span><span data-bind="text: name"></span></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
	
</div>
	
<script type='text/javascript' src='/jquery-1.11.2.min.js'></script>
<script type='text/javascript' src='/knockout-3.2.0.js'></script>
<script src="/bootstrap-3.3.1/dist/js/bootstrap.min.js"></script>
<script src="/mhus.js"></script>

<script type="text/javascript">
  var viewModel = {
		f_entries : ko.observableArray(),
		f_name: ko.observable(),
		
		loadEntryList: function() {
	        var url = '/_resource/callback?action=list';
	        ajaxRequest(url, function (results) {
    			viewModel.f_entries.removeAll();
				for (var nr in results) {
					var row = results[nr];
					viewModel.f_entries.push(row);
				}
			});
		},
		
		doRemove: function(data) {
			//if (!confirm("Really remove " + data['name'])) return;
			
	        var url = '/_resource/callback?action=remove&name=' + data['name'];
	        ajaxRequest(url, function (results) {
	    		viewModel.loadEntryList();
	    	});
		},
		
		doAdd: function() {
	        var url = '/_resource/callback?action=add&name=' + viewModel.f_name();
	        ajaxRequest(url, function (results) {
    			viewModel.loadEntryList();
    			viewModel.f_name("");
	    	});
		}
  };
  // bind only to the portlet namespace
  namespace = "portlet";
  ko.applyBindings(viewModel, document.getElementById(namespace.substring(0, namespace.length-1)));
  viewModel.loadEntryList();
</script>

</body>
</html>