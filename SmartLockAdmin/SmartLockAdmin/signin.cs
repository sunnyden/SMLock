/*
 * SmartLock Administration System
 * Module:Login window
 * Author: Haoqing Deng (admin@denghaoqing.com)
 * All rights reserved.
 * Date:2016.11.9
 * 
 */
using System;
using System.IO;
using System.Management;

using System.Net;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Windows.Forms;

namespace SmartLockAdmin
{
    public partial class signin : Form
    {
        private bool isSuccess = false;
        public signin()
        {
            InitializeComponent();
        }
        
  
        private void signin_Load(object sender, EventArgs e)
        {
            
        }

        private void btnLogin_Click(object sender, EventArgs e)
        {

            string sysid = "";
            string uri = "http://58.63.232.138:62078/lock/api.php";
            string responce = "";

            try
            {
                ManagementObjectSearcher searcher = new ManagementObjectSearcher("Select * From Win32_BIOS");
                string sBIOSSerialNumber = "";
                foreach (ManagementObject mo in searcher.Get())
                {
                    sBIOSSerialNumber = mo["SerialNumber"].ToString().Trim();
                }
                sysid = sBIOSSerialNumber;
            }
            catch
            {
                sysid= "";
            }

            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(uri);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.ProtocolVersion = new Version(1, 1);
            byte[] data = Encoding.UTF8.GetBytes("action=1&username=" + txtUsername.Text + "&passwd=" + txtPasswd.Text+"&imei="+sysid);
            request.ContentLength = data.Length;
            using (Stream reqStream = request.GetRequestStream())
            {
                reqStream.Write(data, 0, data.Length);
                reqStream.Close();
            }
            HttpWebResponse resp = (HttpWebResponse)request.GetResponse();
            Stream stream = resp.GetResponseStream();
            using (StreamReader reader = new StreamReader(stream, Encoding.UTF8))
            {
                responce = reader.ReadToEnd();
            }
            var mStream = new MemoryStream(Encoding.Default.GetBytes(responce));
            var serializer = new DataContractJsonSerializer(typeof(LoginJson));
            LoginJson result = (LoginJson)serializer.ReadObject(mStream);
            if (result.error == 0)
            {
                if (result.gid==1)
                {
                    try
                    {
                        INIProfile mINIProfile = new INIProfile();
                        mINIProfile.WriteProfile("uid", result.uid);
                        mINIProfile.WriteProfile("token", result.token);
                        mINIProfile.WriteProfile("username", result.username);
                        MDIParent1.uid = result.uid;
                        MDIParent1.token = result.token;
                        MDIParent1.username = result.username;
                        this.FormClosing += new FormClosingEventHandler(this.onClose);
                        isSuccess = true;
                        this.Close();
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show("登录失败，登录信息写入异常，错误信息："+ex.Message+"！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                }
                
            }
            else
            {
                MessageBox.Show("登录失败，用户名或密码错误！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            //MessageBox.Show(result.token);
        }

        private void onClose(object Sender,FormClosingEventArgs args)
        {
            if (isSuccess)
            {
                MDIParent1.isLogin = true;
                
                
            }
        }
    }
}
