/*
 * SmartLock Administration System
 * Module:Configuration File Operation Class
 * Author: Haoqing Deng (admin@denghaoqing.com)
 * All rights reserved.
 * Date:2016.11.9
 * 
 */
using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

namespace SmartLockAdmin
{
    public class INIProfile
    {
        public static string dirName = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData)+"\\BTSML";
        public static string fileName = dirName + "\\info.ini";
        public INIProfile()
        {
            Directory.CreateDirectory(dirName);
            
        }
        [DllImport("kernel32", CharSet = CharSet.Unicode, SetLastError = true)]
        private static extern bool WritePrivateProfileString(string lpAppName, string lpKeyName, string lpString, string lpFileName);
        [DllImport("kernel32", CharSet = CharSet.Unicode, SetLastError = true)]
        private static extern int GetPrivateProfileString(string lpAppName, string lpKeyName, string lpDefault, StringBuilder lpReturnedString,int nSize, string lpFileName);
        public void WriteProfile(string key,string value)
        {
            WritePrivateProfileString("BTSMLOCK", key, value, fileName);
        }
        public void WriteProfile(string key, int value)
        {
            WritePrivateProfileString("BTSMLOCK", key,value.ToString(), fileName);
        }
        public string GetStringValue(string key,string defaultStr)
        {
            StringBuilder result = new StringBuilder(1024);
            GetPrivateProfileString("BTSMLOCK",key,defaultStr,result,1024,fileName);
            return result.ToString();
        }
        public int GetIntValue(string key,int defalutValue)
        {
            StringBuilder result = new StringBuilder(1024);
            GetPrivateProfileString("BTSMLOCK", key, defalutValue.ToString(), result, 1024, fileName);
            return int.Parse(result.ToString());
        }
    }

}
