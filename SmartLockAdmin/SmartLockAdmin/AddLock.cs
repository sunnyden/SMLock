using InTheHand.Windows.Forms;
using System;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Forms;

namespace SmartLockAdmin
{
    public partial class AddLock : Form
    {
        public AddLock()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            SelectBluetoothDeviceDialog dialog = new SelectBluetoothDeviceDialog();
            dialog.ShowRemembered = true;//show memorized devices
            dialog.ShowAuthenticated = true;//show authorized devices 
            dialog.ShowUnknown = true;//show unknown devices 
            if (dialog.ShowDialog() == DialogResult.OK)
            {
                txtMAC.Text = "";
                for(int i = 0; i < 6; i++)
                {
                    txtMAC.Text += dialog.SelectedDevice.DeviceAddress.ToByteArray()[5-i].ToString("x2").ToUpper();//To get the iverse order of the addr
                    if (i < 5) txtMAC.Text += ":";
                }
                
                
            }

        }

        private void button2_Click(object sender, EventArgs e)
        {
            Regex macMatch = new Regex(@"^([0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F])$");
            Regex permissionMatch = new Regex(@"^(((\d,?)+))$");
            if (permissionMatch.Match(txtPermission.Text).Success && macMatch.Match(txtMAC.Text).Success)
            {
              
                bool endTry = false;
                bool hasSucceed = false;
                while (!endTry && !hasSucceed)
                {
                    InternetUtilities mInternetUTilities = new InternetUtilities();
                    string responce = mInternetUTilities.POSTText("action=8&uid=" + MDIParent1.uid + "&token=" + MDIParent1.token + "&lkname=" +
                        txtName.Text + "&mac=" + txtMAC.Text + "&access=" + txtPermission.Text);
                    if (mInternetUTilities.isSucceed(responce))
                    {

                        MessageBox.Show("操作成功！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Information);
                        hasSucceed = true;
                        this.Close();
                    }
                    else
                    {
                        if (MessageBox.Show("操作失败！服务器返回信息：" + mInternetUTilities.msg, "提示", MessageBoxButtons.RetryCancel, MessageBoxIcon.Error) != DialogResult.Retry)
                        {
                            endTry = true;
                        }
                        this.Close();
                    }
                }
                    
                
                
            }else
            {
                MessageBox.Show("输入有误！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }
    }
}
