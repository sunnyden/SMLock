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
    public partial class Record : Form
    {
        public Record()
        {
            InitializeComponent();
        }
       
        private int sel_lkid = -1;
        private string sel_lkname = "";
        private DataTable loglst = new DataTable();

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
            byte[] data = Encoding.UTF8.GetBytes("action=14&uid=" + MDIParent1.uid.ToString() + "&token=" + MDIParent1.token);
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
            var serializer = new DataContractJsonSerializer(typeof(LogModel));
            LogModel result = (LogModel)serializer.ReadObject(mStream);
            if (result.error == 403)
            {
                File.Delete(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData) + "\\BTSML\\info.ini");
                MessageBox.Show("令牌安全校验失败！为了系统安全，程序已经抹去令牌信息，请重新登录！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
            }
            loglst = new DataTable();
            loglst.Columns.Add(new DataColumn("ID", typeof(int)));
            loglst.Columns.Add(new DataColumn("UNAME", typeof(string)));
            loglst.Columns.Add(new DataColumn("LKNAME", typeof(string)));
            loglst.Columns.Add(new DataColumn("STAT", typeof(string)));
            loglst.Columns.Add(new DataColumn("TIME", typeof(string)));
            DataRow dr;
            for (int i = 0; i < result.count; i++)
            {
                dr = loglst.NewRow();
                dr["ID"] = result.idset[i];
                dr["UNAME"] = result.usernameset[i];
                dr["LKNAME"] = result.lknameset[i];
                dr["STAT"] = result.statset[i]==1?"开锁":"关锁";
                dr["TIME"] = result.timeset[i];
                loglst.Rows.Add(dr);
            }
            lkListView.DataSource = loglst;
            loglst.AcceptChanges();

            lkListView.Columns[0].HeaderCell.Value = "记录ID";
            lkListView.Columns[1].HeaderCell.Value = "用户名";
            lkListView.Columns[2].HeaderCell.Value = "锁名称";
            lkListView.Columns[3].HeaderCell.Value = "操作类型";
            lkListView.Columns[4].HeaderCell.Value = "时间";
            lkListView.Columns[4].Width = 200;

            //register the listener to mointor the change of the table
            this.lkListView.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.lkListView_CellContentChange);

        }



        private void lkListView_CellContentChange(object sender, DataGridViewCellEventArgs e)
        {
            loglst.RejectChanges();
            updateData();
        }


        private void onCellClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                //show the menu
                contextMenuStrip1.Show(MousePosition.X, MousePosition.Y);
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



        private void 刷新ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            updateData();
        }
    }
    
}


