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
    public class LogModel
    {
        [DataMember(Name = "error")]
        public int error { get; set; }

        [DataMember(Name = "count")]
        public int count { get; set; }

        [DataMember(Name = "id")]
        public int[] idset { get; set; }

        [DataMember(Name = "username")]
        public string[] usernameset { get; set; }

        [DataMember(Name = "stat")]
        public int[] statset { get; set; }

        [DataMember(Name = "lkname")]
        public string[] lknameset { get; set; }

        [DataMember(Name = "time")]
        public string[] timeset { get; set; }



    }
}
