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
	require("./inc/encrypt.php");
	
	
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
			$imei=$_POST['imei'];
			$token = NULL;
			$uid = NULL;
			$gid = NULL;
			$sql = mysql_query("select * from tb_users where username='${username}'");
			
			while($row = mysql_fetch_array($sql))
			{
				if($row['username'] == $username && $row['passwd'] == $passwd_md5){
					$uid = $row['uid'];
					$gid = $row['gid'];
					for($i = 0; $i < 3; $i++)
					{
						$rnd[$i] = rand(0,99999);
					}
					$token = md5($username.$uid.$rnd[0].$rnd[1].$rnd[2]);
					if(!empty($_POST['imei'])){
						$is_succeed=1;
					}
				}
			}
			if($is_succeed == 1)
			{
				mysql_query("UPDATE tb_session SET valid=0 WHERE uid=${uid}");
				mysql_query("INSERT INTO tb_session VALUES (NULL, '${token}', ${uid}, '${imei}',NULL,1)");
				echo(getJson(array("error" => 0,"uid" => $uid,"username" => $username,"token" => $token,"gid" => $gid,"gpname" => getGroupNameByID($gid))));
			}
			else
			{
				echo(getJson(array("error" => 403,"info"=>"Password or username error")));
			}
			break;
		
		case 2:
			/*
			*Case 1:Get Mac action
			*Require:action,token,locknum,uid
			*Return:Success:{"error":0,"mac":"xx:xx:xx:xx:xx:xx"}
			*			  Failure:{"error":404,"info":"Unknown Lock ID"}
			*/
			if(validate($_POST['uid'],$_POST['token']))
			{
				$locknum=$_POST['locknum'];
				$sql=mysql_query("select * from tb_lock where lknum=${locknum}");
				if(mysql_num_rows($sql)==0)
				{
					echo(getJson(array("error" => 404,"info"=>"Unknown Lock ID")));
				}
				else
				{
					$row = mysql_fetch_array($sql);
					$mac=$row['lkmac'];
					if($row['access']==0||in_array(getGpId($_POST['uid']),explode(',',$row['access'])))
					{
						echo(getJson(array("error" => 0,"mac" => $mac)));
					}
					else
					{
						echo(getJson(array("error" => 403,"info" => "Permission Denied")));
					}
					
				}
			}
			else
			{
				echo(getJson(array("error" => 503,"info" => "Authorization error")));
			}
			break;
		case 3:
			/*
			*Case 3:Validate Session ID
			*Require:action,uid,token
			*Return sample:{"error":0,"valid":1}
			*/
			echo(getJson(array("error"=>0,"valid"=>validate($_POST['uid'],$_POST['token']))));
			break;
		
		case 4:
			/*
			*Case 4:Get Auth Code
			*Require: uid,token,mac
			*Return sample:{"error":0,"code":000}
			*/
			if(validate($_POST['uid'],$_POST['token']))
			{
				echo(getJson(array("error"=>0,"code"=>getVerfCode($_POST['mac']))));
			}
			else
			{
				echo(getJson(array("error" => 503,"info" => "Authorization error")));
			}
			break;
		case 5:
			/*
			*Case 5:Update Lock Status
			*Require: uid,token,lkcode and stat
			*Return Sample:{"error":0,"stat":1(0)}
			*/
			if(validate($_POST['uid'],$_POST['token']) && !empty($_POST['lkcode']) && !empty($_POST['stat']))
			{
				$sql=mysql_query("update tb_lock set status=$_POST[stat] where lknum=$_POST[lkcode]");
			
				if(mysql_affected_rows()==0)
				{
					echo(getJson(array("error" => 404,"info"=>"Unknown Lock ID")));
				}
				else
				{
					echo(getJson(array("error" => 0,"stat" => $_POST['stat'])));
				}
			}
			break;
		case 6:
			/*
			*Case 6:Get Lock Lists(Privileged Action)
			*Require: uid, token
			*Return:Json results
			*/
			if(isPrivileged($_POST['uid']) && validate($_POST['uid'],$_POST['token']))
			{
				$sql=mysql_query("select * from tb_lock");
				$lkidset="[";
				$lknumset="[";
				$lkmacset="[";
				$lkstatset="[";
				$lknameset="[";
				$lkaccessset="[";
				$count=mysql_num_rows($sql);
				while($row = mysql_fetch_array($sql))
				{
					$lkidset.="$row[lkid],";
					$lknumset.="$row[lknum],";
					$lkstatset.="$row[status],";
					$lknameset.="\"$row[lkname]\",";
					$lkaccessset.="\"$row[access]\",";
					$lkmacset.="\"$row[lkmac]\",";
				}
				$lkidset.="0]";
				$lknumset.="0]";
				$lkstatset.="0]";
				$lknameset.="\"\"]";
				$lkaccessset.="\"\"]";
				$lkmacset.="\"\"]";
				echo(getJsonPlus(array("error" => 0,"count" => $count,"lkid"=>$lkidset,
				"lknum"=>$lknumset,"lkname"=>$lknameset,"lkmac"=>$lkmacset,"lkstat"=>$lkstatset,"lkaccess"=>$lkaccessset)));
			}
			else
			{
				echo(getJson(array("error" => 403,"info"=>"Permission denied")));
			}
			break;
		case 7:
			/*
			*Case 7:Update Lock Property
			*Require: uid, token, lock_info
			*Return:Json results
			*/
			if(isPrivileged($_POST['uid']) && validate($_POST['uid'],$_POST['token']))
			{
				$sql=mysql_query("update tb_lock set status=$_POST[lkstat],lknum=$_POST[lknum],lkmac='$_POST[lkmac]',lkname='$_POST[lkname]',access='$_POST[lkaccess]' where lkid=$_POST[lkid]");
				echo(getJson(array("error" => 0,"info"=>"Operation succeed")));
			}else
			{
				echo(getJson(array("error" => 403,"info"=>"Permission denied")));
			}
			
			break;
		case 8:
			/*
			*Case 8:Add Lock
			*Require:uid,token,lock_info
			*/
			if($_POST['lkname']!="" && $_POST['mac']!="" && $_POST['access']!=""){
				if(isPrivileged($_POST['uid']) && validate($_POST['uid'],$_POST['token']))
				{
					$count=mysql_query("SELECT max(lknum) as maximum FROM tb_lock");
					$result=mysql_fetch_array($count);
					$lknum=$result['maximum']+1;
					$lkname=$_POST['lkname'];
					$mac=$_POST['mac'];
					$access=$_POST['access'];
					$sql=mysql_query("INSERT INTO tb_lock VALUES (NULL, '${lkname}', '${mac}', ${lknum},'${access}',0)");
					echo(getJson(array("error" => 0,"info"=>"Operation succeed")));
				}else
				{
					echo(getJson(array("error" => 403,"info"=>"permission denied")));
				}
			}else
			{
				echo(getJson(array("error" => 403,"info"=>"Wrong input")));
			}
			break;
		case 9:
			/*
			*Case 8:Delete Lock
			*Require:uid,token,lock_info
			*/
			if($_POST['lkid']!=""){
				if(isPrivileged($_POST['uid']) && validate($_POST['uid'],$_POST['token']))
				{
					$count=mysql_query("DELETE FROM tb_lock WHERE lkid=$_POST[lkid]");
					echo(getJson(array("error" => 0,"info"=>"Operation succeed")));
				}else
				{
					echo(getJson(array("error" => 403,"info"=>"permission denied")));
				}
			}else
			{
				echo(getJson(array("error" => 403,"info"=>"Wrong input")));
			}
			break;
			
	}
	mysql_close($sql_conn);
?>