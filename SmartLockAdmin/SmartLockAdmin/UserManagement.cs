/*
 * SmartLock Administration System
 * Module:Main window
 * Author: Haoqing Deng (admin@denghaoqing.com)
 * All rights reserved.
 * Date:2016.11.9
 * 
 */
using System;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text.RegularExpressions;

namespace SmartLockAdmin
{
    public partial class UserManagement : Form
    {
        public UserManagement()
        {
            InitializeComponent();
        }
       
        private int sel_uid = -1;
        private string sel_uname = "";
        private DataTable lklst = new DataTable();

        private void ProgMain_Load(object sender, EventArgs e)
        {
            updateData();
        }

        public void updateData()
        {
            //unregister the listener to avoid submit wrong information to the server
            this.lkListView.CellValueChanged -= new System.Windows.Forms.DataGridViewCellEventHandler(this.lkListView_CellContentChange);

            string uri = "http://58.63.232.138:62078/lock/api.php";
            string responce;

            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(uri);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.ProtocolVersion = new Version(1, 1);
            byte[] data = Encoding.UTF8.GetBytes("action=10&uid=" + MDIParent1.uid.ToString() + "&token=" + MDIParent1.token);
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
            var serializer = new DataContractJsonSerializer(typeof(UserListModel));
            UserListModel result = (UserListModel)serializer.ReadObject(mStream);
            if (result.error == 403)
            {
                File.Delete(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData) + "\\BTSML\\info.ini");
                MessageBox.Show("令牌安全校验失败！为了系统安全，程序已经抹去令牌信息，请重新登录！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
            }
            lklst = new DataTable();
            lklst.Columns.Add(new DataColumn("UID", typeof(int)));
            lklst.Columns.Add(new DataColumn("UNAME", typeof(string)));
            lklst.Columns.Add(new DataColumn("PWD", typeof(string)));
            lklst.Columns.Add(new DataColumn("GID", typeof(int)));
            DataRow dr;
            for (int i = 0; i < result.count; i++)
            {
                dr = lklst.NewRow();
                dr["UID"] = result.uidset[i];
                dr["UNAME"] = result.usernameset[i];
                dr["PWD"] = result.pwdset[i];
                dr["GID"] = result.gidset[i];
                lklst.Rows.Add(dr);
            }
            lkListView.DataSource = lklst;
            lklst.AcceptChanges();

            lkListView.Columns[0].HeaderCell.Value = "用户ID";
            lkListView.Columns[1].HeaderCell.Value = "用户名";
            lkListView.Columns[2].HeaderCell.Value = "密钥";
            lkListView.Columns[3].HeaderCell.Value = "用户组ID";
            lkListView.Columns[2].Width = 200;

            //register the listener to mointor the change of the table
            this.lkListView.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.lkListView_CellContentChange);

        }



        private void lkListView_CellContentChange(object sender, DataGridViewCellEventArgs e)
        {
            if (e.ColumnIndex != 0)
            {
                try
                {
                    DialogResult isChange = MessageBox.Show("是否确认修改？", "确认", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

                    if (isChange == DialogResult.Yes)
                    {

    
                        lklst.AcceptChanges();
                        
                        POSTText("action=12&nuid=" + lkListView.Rows[e.RowIndex].Cells[0].Value.ToString() +
                            "&nuname=" + lkListView.Rows[e.RowIndex].Cells[1].Value.ToString() +
                            "&npwd=" + lkListView.Rows[e.RowIndex].Cells[2].Value.ToString() +
                            "&ngid=" + lkListView.Rows[e.RowIndex].Cells[3].Value.ToString() +
                            "&uid=" + MDIParent1.uid + "&token=" + MDIParent1.token+"&ispwdchange="+
                            (e.ColumnIndex==2?"1":"0"));
                    }
                    else
                    {
                        lklst.RejectChanges();
                    }

                }
                catch (Exception ex)
                {

                }
            } else
            {
                MessageBox.Show("用户ID为索引，不允许修改！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                lklst.RejectChanges();
            }



        }


        private void onCellClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                if (e.RowIndex >= 0)
                {
                    //select the whole row
                    if (lkListView.Rows[e.RowIndex].Selected == false)
                    {
                        lkListView.ClearSelection();
                        lkListView.Rows[e.RowIndex].Selected = true;
                    }
                    sel_uid = int.Parse(lkListView.Rows[e.RowIndex].Cells[0].Value.ToString());
                    sel_uname = lkListView.Rows[e.RowIndex].Cells[1].Value.ToString();
                    //show the menu
                    lkMenu.Show(MousePosition.X, MousePosition.Y);
                }
            }
        }
    


        private void RefreshToolStripMenuItem_Click(object sender, EventArgs e)
        {
            updateData();
            
        }

        private void POSTText(string contents)
        {
            string uri = "http://58.63.232.138:62078/lock/api.php";
            string responce = "";
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(uri);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.ProtocolVersion = new Version(1, 1);
            byte[] data = Encoding.UTF8.GetBytes(contents);
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
            updateData();

        }

        private void lklstView_DataError(object sender, DataGridViewDataErrorEventArgs e)
        {
            MessageBox.Show("请检查输入类型！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            
        }

        private void 删除记录ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("是否确删除用户："+sel_uname+" (ID:"+sel_uid.ToString()+")? 注意，操作不可取消!", "确认", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) == DialogResult.Yes && sel_uid != -1)
            {
                bool endTry = false;
                bool hasSucceed = false;
                while (!endTry && !hasSucceed)
                {
                    InternetUtilities mInternetUTilities = new InternetUtilities();
                    string responce = mInternetUTilities.POSTText("action=11&duid=" + sel_uid.ToString() + "&uid=" + MDIParent1.uid.ToString() + "&token=" + MDIParent1.token);
                    if (mInternetUTilities.isSucceed(responce))
                    {
                        MessageBox.Show("操作成功！", "提示", MessageBoxButtons.OK, MessageBoxIcon.Information);
                        hasSucceed = true;
                    }
                    else
                    {
                        if (MessageBox.Show("操作失败！服务器返回信息：" + mInternetUTilities.msg, "提示", MessageBoxButtons.RetryCancel, MessageBoxIcon.Error) != DialogResult.Retry)
                        {
                            endTry = true;
                        }
                    }
                }
            }
            updateData();
            sel_uid = -1;
            sel_uname = "";
        }

        private void 刷新ToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            updateData();
        }

        private void 新建ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            AddUser childForm = new AddUser();
            childForm.MdiParent = this.MdiParent;
            //childForm.Text = "窗口 " + childFormNumber++;
            childForm.Show();
        }
    }
    
}


