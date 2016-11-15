using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SmartLockAdmin
{
    public partial class MDIParent1 : Form
    {
        public static string username = "";
        public static string token = "";
        public static int uid = -1;
        public static bool isLogin = false;
        private int childFormNumber = 0;

        public MDIParent1()
        {
            InitializeComponent();
        }

        private void showLkManageMent(object sender, EventArgs e)
        {
            ProgMain childForm = new ProgMain();
            childForm.MdiParent = this;
            //childForm.Text = "窗口 " + childFormNumber++;
            childForm.Show();
        }
        //showLogin
        private void showLogin(object sender, EventArgs e)
        {
            signin login = new signin();
            login.ShowDialog();
            updateLoginInfo();
        }


        private void ExitToolsStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }


        private void StatusBarToolStripMenuItem_Click(object sender, EventArgs e)
        {
            statusStrip.Visible = statusBarToolStripMenuItem.Checked;
        }

        private void CascadeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.Cascade);
        }

        private void TileVerticalToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.TileVertical);
        }

        private void TileHorizontalToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.TileHorizontal);
        }

        private void ArrangeIconsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LayoutMdi(MdiLayout.ArrangeIcons);
        }

        private void CloseAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (Form childForm in MdiChildren)
            {
                childForm.Close();
            }
        }

        private void MDIParent1_Load(object sender, EventArgs e)
        {
            updateLoginInfo();
            
        }
        
        public void updateLoginInfo()
        {
            INIProfile mINIProfile = new INIProfile();

            bool isException = false;

            int i = 0;

            signin msignin = new signin();

            try
            {
                uid = mINIProfile.GetIntValue("uid", -1);
                username = mINIProfile.GetStringValue("username", "N/A");
                token = mINIProfile.GetStringValue("token", "N/A");
            }
            catch (Exception ex)
            {
                isLogin = false;
                isException = true;
            }
            if ((username == "N/A" || token == "N/A" || uid == -1) && !isException)
            {
                isLogin = false;
            }
            else
            {
                if (!isException)
                {
                    isLogin = true;
                }

            }
            if (!isLogin)
            {
                toolStripStatusLabel.Text = "未登录！";
                toolStripStatusLabel.ForeColor = Color.Red;
                loginToolStripMenuItem.Visible = true;
            }
            else
            {
                toolStripStatusLabel.Text = "已登录！";
                toolStripStatusLabel.ForeColor = Color.Green;
                loginToolStripMenuItem.Visible = false;
            }
        }
    }
}
