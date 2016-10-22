	<?php
	//initialize the SQL connection
	$sql_conn=mysql_connect("localhost","root","****");
	if(!$sql_conn){
		die("{\"error\":500,\"info\":\"Internal Server Error\"}");
	}
	mysql_select_db("lock",$sql_conn);
	?>