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
    public class UserListModel
    {
        [DataMember(Name = "error")]
        public int error { get; set; }

        [DataMember(Name = "count")]
        public int count { get; set; }

        [DataMember(Name = "uid")]
        public int[] uidset { get; set; }

        [DataMember(Name = "username")]
        public string[] usernameset { get; set; }

        [DataMember(Name = "passwd")]
        public string[] pwdset { get; set; }


        [DataMember(Name = "gid")]
        public int[] gidset { get; set; }

    }
}
