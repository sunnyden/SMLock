/*
 * SmartLock Administration System
 * Module:Json Model for handling Lock list
 * Author: Haoqing Deng (admin@denghaoqing.com)
 * All rights reserved.
 * Date:2016.11.9
 * 
 */
using System.Runtime.Serialization;

namespace SmartLockAdmin
{
    [DataContract]
    public class LkListModel
    {
        [DataMember(Name = "error")]
        public int error { get; set; }

        [DataMember(Name = "count")]
        public int count { get; set; }

        [DataMember(Name = "lkid")]
        public int[] lkidset { get; set; }

        [DataMember(Name = "lknum")]
        public int[] lknumset { get; set; }

        [DataMember(Name = "lkname")]
        public string[] lknameset { get; set; }

        [DataMember(Name = "lkmac")]
        public string[] lkmacset { get; set; }

        [DataMember(Name = "lkaccess")]
        public string[] lkaccessset { get; set; }

        [DataMember(Name = "lkstat")]
        public int[] lkstatset { get; set; }

    }
}
