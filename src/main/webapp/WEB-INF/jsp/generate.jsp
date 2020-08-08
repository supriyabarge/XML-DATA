<!DOCTYPE html>
<html>
<head>
<title>Webpage using div</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="/resources/css/select2.min.css">
<link rel="stylesheet" type="text/css" href="/resources/css/common.css">
<style type="text/css">
.form-group.required .control-label:after {
	color: red;
	content: "*";
	position: absolute;
	margin-left: 3px;
}
</style>
<script type="text/javascript">


$(document).ready(function() {
	
	$('#select_table').select2({
		placeholder: "Please select Table"
	});
	gettableDetails();
});

function gettableDetails(){
	$.ajax({
		url : "/xml-data/getTables",
		type : "GET",
		contentType : "application/json",
		dataType : "json",
		success : function(response, textStatus, jqXHR) {
			if (response != null && !jQuery.isEmptyObject(response)) {
				var data = response;
				var option = '';
				$.each(data, function(k, v){
				    option += '<option value="'+v+'">'+v+'</option>';
				});
				$('#select_table').append(option);
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			waitingDialog.hide();
			generateNoty('topCenter', 3000, 'error', "Project data not found...")
		}
	});
}

function generateXML(){
  var tableName = $('#select_table option:selected').val();
  
  $.ajax({
		url : "/xml-data/getTable/"+tableName,
		type : "GET",
		contentType : "application/json",
		dataType : "json",
		success : function(response, textStatus, jqXHR) {
			if (response != null && !jQuery.isEmptyObject(response)) {
				var data = response;
				console.log(data);
				$('#txtXML').val(data);
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			waitingDialog.hide();
			generateNoty('topCenter', 3000, 'error', "Project data not found...")
		}
	});
}
</script>
</head>
<body>
	<div>
		<%@include file="/resources/nav/navbar.jsp"%>

		<!-- main -->
		<div>

			<!-- side -->
			<div class="rside">

				<div class="form">
					<div class="row">
						<div class="col-sm-6">
							<div class="form-group required">
								<label for="select_Table" class="control-label">Select Table</label><br> 
									<select id="select_table"
									class="form-control" name="selectTable">
								</select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-3">
							<div class="form-group">
								<br> <input class="btn-lg btn-success" value="Generate XML"
									type="Submit" id="sendReportToVen" onclick="generateXML();">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-3">
							<div class="form-group">
								<!-- <input value="" type="text" id="txtXML"> -->
								<textarea id="txtXML" name="" rows="30" cols="120"></textarea>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>
</body>
<script src="/resources/js/select2.full.js"></script>
<script src="/resources/js/select2.full.min.js"></script>
</html>
