
<?php
	require("./inc/database.php");
	$sql_count = mysql_query("select count(*) as count from tb_users");
	$user_count = mysql_fetch_array($sql_count);
	if($user_count['count'] == 0)
	{
		if(!empty($_POST['username']) && !empty($_POST['password']))
		{
			$username = $_POST['username'];
			$passwd_md5 = md5($_POST['password']);
			mysql_query("INSERT INTO tb_users VALUES (NULL, '${username}', '${passwd_md5}', 1)");
			header("Location: ./");
		}
	}
	else
	{
		header("Location: ./");
	}
?>
<html>

<head>
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
	   <script src="js/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/scripts.js"></script>
</head>


<body>
<div class="container-fluid">

	<div class="row">
		<div class="col-md-12">
			<div class="page-header">
				<h1>
					智能锁管理系统 <small>初始化</small>
				</h1>
			</div>
	<div class="row clearfix">
			<div class="col-md-12 column">
				<div class="alert alert-dismissable alert-warning">
					 <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
					<h4>
						注意!
					</h4> <strong>注意!</strong> 此处输入的信息将作为管理员信息录入，录入后本页面将失效! 
				</div>
			</div>
	</div>
			<form class="form-horizontal" action="./init.php" method="POST" role="form">
			
				<div class="form-group">
					 
					<label for="username" class="col-sm-2 control-label">
						用户名
					</label>
					<div class="col-xs-2">
						<input type="text" class="form-control" name="username" id="username" />
					</div>
				</div>
				<div class="form-group">
					 
					<label for="password" class="col-sm-2 control-label">
						密码
					</label>
					<div class="col-xs-2">
						<input type="password" class="form-control" name="password" id="password" />
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<div class="checkbox">
							 
							<label>
								<input type="checkbox" /> Remember me
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						 
						<button type="submit" class="btn btn-default">
							Sign in
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
</body>
</html>
