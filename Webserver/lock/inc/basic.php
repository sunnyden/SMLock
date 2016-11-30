<?php
	/****************************************************
	* Copyright(C), Haoqing Deng, denghaoqing.com
	* Author: Haoqing Deng(dhq.sunny@gmail.com)
	* Date: 2016-10-21
	* Description:A PHP library for basic data processing
	*****************************************************/
	
	function getJson($orig_array)
	{
		/*
		*Function:getJson(array)
		*Parameters:Array (key,value)
		*Description:convert arrays into json
		*/
		$str="{";
		foreach($orig_array as $key => $value)
		{
			if(is_numeric($value)){
				$str.="\"${key}\":".$value.",";
			}
			else
			{
				$str.="\"${key}\":\"${value}\",";
			}
		}
		
		//After looping a extra comma will be added at the end of the string,delete it and return.
		return substr($str,0,strlen($str) - 1)."}";	
	}
	
	function getJsonPlus($orig_array)
	{
		/*
		*Function:getJsonArray(array)
		*Parameters:Array (key,value)
		*Description:convert arrays into json,for Jsons which contains arrays use only(temporarily)
		*/
		$str="{";
		foreach($orig_array as $key => $value)
		{
			if(is_numeric($value)){
				$str.="\"${key}\":".$value.",";
			}
			else
			{
				$str.="\"${key}\":${value},";
			}
		}
		
		//After looping a extra comma will be added at the end of the string,delete it and return.
		return substr($str,0,strlen($str) - 1)."}";	
	}
	
	function validate($uid,$token)
	{
		$sql=mysql_query("select * from tb_session where uid=${uid}");
		$is_legal=0;
		while($row = mysql_fetch_array($sql))
		{
			if($row['session_token']==$token && $row['valid']==1)
			{
				$is_legal=1;
			}
		}
		return $is_legal;
	}
	
	function isPrivileged($uid)
	{
		$sql=mysql_query("select * from tb_users where uid=${uid}");
		$is_legal=0;
		while($row = mysql_fetch_array($sql))
		{
			if($row['gid']==1)
			{
				$is_legal=1;
			}
		}
		return $is_legal;
	}
	
	function getGpId($uid)
	{
		$sql=mysql_query("select * from tb_users where uid=${uid}");
		$row = mysql_fetch_array($sql);
		return $row['gid'];
	}
	
	function getGroupNameByID($gid){
		$sql=mysql_query("select * from tb_usergroup where gid=${gid}");
		$result = mysql_fetch_array($sql);
		return $result['groupname'];
	}
	
	function getUnameByID($uid){
		$sql=mysql_query("select * from tb_users where uid=${uid}");
		$result = mysql_fetch_array($sql);
		if(empty($result['username']))$result['username']="(null)";
		return $result['username'];
	}
	function getLknameByID($lkid){
		$sql=mysql_query("select * from tb_lock where lknum=${lkid}");
		$result = mysql_fetch_array($sql);
		if(empty($result['lkname']))$result['lkname']="(null)";
		return $result['lkname'];
	}
?>