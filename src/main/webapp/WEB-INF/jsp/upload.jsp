<!DOCTYPE html>
<html>
<head>
<title>Webpage using div</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
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
$(document).on("change",'#uploadFile',function(file){
	var maxSize=100 *1024 * 1024;
	if(this.files[0].size > maxSize){
			alert( "File size is not nore than 100 mb!");
	 }
	
});

function uploadFile(){
	console.log($('#uploadFile')[0].files[0])
	if($('#uploadFile')[0].files[0] != null && $('#uploadFile')[0].files[0] != "undefined"){
	var formData = new FormData();
	formData.append('file', $('#uploadFile')[0].files[0]);
		$.ajax({
			type : "POST",
			enctype : 'multipart/form-data/',
			url : "/xml-data/doUpload",
			data : formData,
			processData : false,
			contentType : false,
			cache : false,
			success : function(data) {
				if(data == "Successful Ingestion"){
					alert(data);
				}else{
					alert(data);
				}
			},
			error : function(e) {
			}
		});
	}else{
		alert( "Please select file!");
	}
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
								<label for="select_file" class="control-label">Upload
									File</label> <input type="file" name="uploadFile" id="uploadFile"
									class="form-control" accept=".xml" >
							</div>
						</div>
					</div>
				</div>
				<div class="row">
						<div class="col-sm-3">
							<div class="form-group">
								<br> <input class="btn-lg btn-success" value="Submit"
									type="Submit" id="sendReportToVen" onclick="uploadFile()">
							</div>
						</div>
					</div>
			</div>
		</div>
	</div>
</body>
</html>
