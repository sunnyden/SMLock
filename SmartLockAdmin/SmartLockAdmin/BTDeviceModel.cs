using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using InTheHand.Net.Bluetooth;
using InTheHand.Net;

namespace SmartLockAdmin
{
    class BTDeviceModel
    {
        
            public string blueName { get; set; }                  //l蓝牙名字
            public BluetoothAddress btAddr { get; set; }        //蓝牙的唯一标识符
            public ClassOfDevice blueClassOfDevice { get; set; }      //蓝牙是何种类型
            public bool IsBlueAuth { get; set; }                  //指定设备通过验证
            public bool IsBlueRemembered { get; set; }            //记住设备
            public DateTime blueLastSeen { get; set; }
            public DateTime blueLastUsed { get; set; }
        
    }
}
