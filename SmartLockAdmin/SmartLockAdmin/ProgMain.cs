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
    public partial class ProgMain : Form
    {
        public ProgMain()
        {
            InitializeComponent();
        }
        public static string username = "";
        public static string token = "";
        public static int uid = -1;
        private int sel_lkid = -1;
        private string sel_lkname = "";
        private DataTable lklst = new DataTable();

        private void ProgMain_Load(object sender, EventArgs e)
        {

            INIProfile mINIProfile = new INIProfile();

            bool isException = false;
            while (true)
            {
                int i = 0;

                signin msignin = new signin();
                if (i == 1)
                {
                    this.Close();
                }
                try
                {
                    uid = mINIProfile.GetIntValue("uid", -1);
                    username = mINIProfile.GetStringValue("username", "N/A");
                    token = mINIProfile.GetStringValue("token", "N/A");
                }
                catch (Exception ex)
                {
                    msignin.ShowDialog();
                    i++;
                    isException = true;
                }
                if ((username == "N/A" || token == "N/A" || uid == -1) && !isException)
                {
                    msignin.ShowDialog();
                    i++;
                }
                else
                {
                    if (!isException)
                    {
                        break;
                    }

                }
            }

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
            byte[] data = Encoding.UTF8.GetBytes("action=6&uid=" + uid.ToString() + "&token=" + token);
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
            var serializer = new DataContractJsonSerializer(typeof(LkListModel));
            LkListModel result = (LkListModel)serializer.ReadObject(mStream);
            if (result.error == 403)
            {
                File.Delete(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData) + "\\BTSML\\info.ini");
                MessageBox.Show("令牌安全校验失败！为了系统安全，程序已经抹去令牌信息，请重新登录！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
            }
            lklst = new DataTable();
            lklst.Columns.Add(new DataColumn("ID", typeof(int)));
            lklst.Columns.Add(new DataColumn("NUM", typeof(int)));
            lklst.Columns.Add(new DataColumn("NAME", typeof(string)));
            lklst.Columns.Add(new DataColumn("MAC", typeof(string)));
            lklst.Columns.Add(new DataColumn("ACCESS", typeof(string)));
            lklst.Columns.Add(new DataColumn("STAT", typeof(int)));
            DataRow dr;
            for (int i = 0; i < result.count; i++)
            {
                dr = lklst.NewRow();
                dr["ID"] = result.lkidset[i];
                dr["NUM"] = result.lknumset[i];
                dr["NAME"] = result.lknameset[i];
                dr["MAC"] = result.lkmacset[i];
                dr["ACCESS"] = result.lkaccessset[i];
                dr["STAT"] = result.lkstatset[i];
                lklst.Rows.Add(dr);
            }
            lkListView.DataSource = lklst;
            lklst.AcceptChanges();

            lkListView.Columns[0].HeaderCell.Value = "锁ID";
            lkListView.Columns[1].HeaderCell.Value = "锁编号";
            lkListView.Columns[2].HeaderCell.Value = "锁名称";
            lkListView.Columns[3].HeaderCell.Value = "MAC地址";
            lkListView.Columns[3].Width = 150;
            lkListView.Columns[4].HeaderCell.Value = "权限管理";
            lkListView.Columns[5].HeaderCell.Value = "锁状态";

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

                        Regex macMatch = new Regex(@"^([0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F])$");
                        Regex permissionMatch = new Regex(@"^(((\d,?)+))$");
                        if (permissionMatch.Match(lkListView.Rows[e.RowIndex].Cells[4].Value.ToString()).Success &&
                            macMatch.Match(lkListView.Rows[e.RowIndex].Cells[3].Value.ToString()).Success)
                        {
                            lklst.AcceptChanges();
                            POSTText("action=7&lkid=" + lkListView.Rows[e.RowIndex].Cells[0].Value.ToString() +
                                "&lknum=" + lkListView.Rows[e.RowIndex].Cells[1].Value.ToString() +
                                "&lkname=" + lkListView.Rows[e.RowIndex].Cells[2].Value.ToString() +
                                "&lkmac=" + lkListView.Rows[e.RowIndex].Cells[3].Value.ToString() +
                                "&lkaccess=" + lkListView.Rows[e.RowIndex].Cells[4].Value.ToString() +
                                "&lkstat=" + lkListView.Rows[e.RowIndex].Cells[5].Value.ToString() +
                                "&uid=" + uid + "&token=" + token);


                        }
                        else
                        {
                            MessageBox.Show("保存失败！请输入合法的MAC地址或权限序列！格式如下：\n Mac地址：12:34:56:AB:CD:EF（注意大小写）\n 权限序列：1,2,3,4 或 0（无权限设置）", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            lklst.RejectChanges();
                        }
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
                MessageBox.Show("锁ID为索引，不允许修改！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
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
                    sel_lkid = int.Parse(lkListView.Rows[e.RowIndex].Cells[0].Value.ToString());
                    sel_lkname = lkListView.Rows[e.RowIndex].Cells[2].Value.ToString();
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
            if (MessageBox.Show("是否确删除锁："+sel_lkname+" (ID:"+sel_lkid.ToString()+")? 注意，操作不可取消!", "确认", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) == DialogResult.Yes && sel_lkid != -1)
            {

            }
            sel_lkid = -1;
            sel_lkname = "";
        }

        private void 刷新ToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            updateData();
        }
    }
    
}


