<?php
	/****************************************************
	* Copyright(C), Haoqing Deng, denghaoqing.com
	* Author: Haoqing Deng(dhq.sunny@gmail.com)
	* Date: 2016-10-21
	* Description:The API of the SmartLock Cloud System
	*****************************************************/
	
	//import my library.
	require("./inc/basic.php");
	require("./inc/database.php");

	
	
	$action=$_POST['action'];
	switch($action)
	{
		case 1:
			/*
			*Case 1:Login action
			*Require:action,username,password,imei
			*Return:Success:{"error":0,"uid":"xxx","username":"xxx","token":"xxxxx"}
			*				Failure:{"error":403,"info":"Username or password error"}
			*/
			
			$username = $_POST['username'];
			$passwd = $_POST['passwd'];
			$passwd_md5 = md5($passwd);
			$is_succeed=0;
			$token = NULL;
			$uid = NULL;
			$sql = mysql_query("select * from tb_users where username=${username}");
			
			while($row = mysql_fetch_array($sql))
			{
				if($row['username'] = $username && $row['passwd'] = $passwd_md5){
					$uid = $row['uid'];
					for($i = 0; $i < 3; $i++)
					{
						$rnd[$i] = rand(0,99999);
					}
					$token = md5($username.$uid.$rnd[0].$rnd[1].$rnd[2]);
					
					$is_succeed=1;
				}
			}
			if($is_succeed == 1)
			{
				echo(getJson(array("error" => 0,"uid" => $uid,"username" => $username,"token" => $token)));
			}
			else
			{
				echo(getJson(array("error" => 403,"info","Password or username error")));
			}
			break;
		
		case 2:
			/*
			*Case 1:Get Mac action
			*Require:action,token
			*Return:Success:{"error":0,"mac":"xx:xx:xx:xx:xx:xx"}
			*			  Failure:{"error":404,"info":"Unknown Lock ID"}
			*/
			if(validate($_POST['uid'],$_POST['token']))
			{
				$locknum=$_POST['lknum'];
				$sql=mysql_query("select * from tb_lock where locknum=${locknum}");
				$row = mysql_fetch_array($sql);
				$mac=$row['lkmac'];
				echo(getJson(array("error" => 0,"mac" => $mac)));
			}
			else
			{
				echo(getJson(array("error" => 503,"info","Authorization error")));
			}
			break;
	}
	
	mysql_close($sql_conn);
?>