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