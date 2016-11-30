using InTheHand.Windows.Forms;
using System;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Forms;

namespace SmartLockAdmin
{
    public partial class AddUser : Form
    {
        public AddUser()
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
                txtUname.Text = "";
                for(int i = 0; i < 6; i++)
                {
                    txtUname.Text += dialog.SelectedDevice.DeviceAddress.ToByteArray()[5-i].ToString("x2").ToUpper();//To get the iverse order of the addr
                    if (i < 5) txtUname.Text += ":";
                }
                
                
            }

        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (!String.IsNullOrEmpty(txtGid.Text) && !String.IsNullOrEmpty(txtUname.Text) && !String.IsNullOrEmpty(txtPwd.Text))
            {
              
                bool endTry = false;
                bool hasSucceed = false;
                while (!endTry && !hasSucceed)
                {
                    InternetUtilities mInternetUTilities = new InternetUtilities();
                    string responce = mInternetUTilities.POSTText("action=13&uid=" + MDIParent1.uid + "&token=" + MDIParent1.token + "&npwd=" +
                        txtPwd.Text + "&nuname=" + txtUname.Text + "&ngid=" + txtGid.Text);
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
