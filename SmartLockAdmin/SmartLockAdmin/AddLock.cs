using InTheHand.Windows.Forms;
using System;
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
                    txtMAC.Text += dialog.SelectedDevice.DeviceAddress.ToByteArray()[5-i].ToString("x2");//To get the iverse order of the addr
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

            }
        }
    }
}
