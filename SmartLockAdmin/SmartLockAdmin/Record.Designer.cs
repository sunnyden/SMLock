using System.Windows.Forms;

namespace SmartLockAdmin
{
    partial class Record
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.lkListView = new System.Windows.Forms.DataGridView();
            this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.刷新ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            ((System.ComponentModel.ISupportInitialize)(this.lkListView)).BeginInit();
            this.contextMenuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // lkListView
            // 
            this.lkListView.AllowUserToAddRows = false;
            this.lkListView.AllowUserToDeleteRows = false;
            this.lkListView.AllowUserToResizeColumns = false;
            this.lkListView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.lkListView.Location = new System.Drawing.Point(12, 12);
            this.lkListView.Name = "lkListView";
            this.lkListView.RowTemplate.Height = 23;
            this.lkListView.Size = new System.Drawing.Size(902, 528);
            this.lkListView.TabIndex = 1;
            this.lkListView.CellMouseDown += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.onCellClick);
            this.lkListView.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.lklstView_DataError);
            // 
            // contextMenuStrip1
            // 
            this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.刷新ToolStripMenuItem});
            this.contextMenuStrip1.Name = "contextMenuStrip1";
            this.contextMenuStrip1.Size = new System.Drawing.Size(101, 26);
            // 
            // 刷新ToolStripMenuItem
            // 
            this.刷新ToolStripMenuItem.Name = "刷新ToolStripMenuItem";
            this.刷新ToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.刷新ToolStripMenuItem.Text = "刷新";
            this.刷新ToolStripMenuItem.Click += new System.EventHandler(this.刷新ToolStripMenuItem_Click);
            // 
            // Record
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(926, 552);
            this.Controls.Add(this.lkListView);
            this.Name = "Record";
            this.Text = "锁管理";
            this.Load += new System.EventHandler(this.ProgMain_Load);
            ((System.ComponentModel.ISupportInitialize)(this.lkListView)).EndInit();
            this.contextMenuStrip1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion
        private System.Windows.Forms.DataGridView lkListView;
        private ContextMenuStrip contextMenuStrip1;
        private ToolStripMenuItem 刷新ToolStripMenuItem;
    }
}