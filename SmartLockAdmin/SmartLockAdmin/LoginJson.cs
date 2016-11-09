using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization.Json;
using System.Threading.Tasks;
using System.Runtime.Serialization;
using System.IO;

namespace SmartLockAdmin
{
    [DataContract]
    public class LoginJson
    {
        [DataMember(Name ="error")]
        public int error { get; set; }

        [DataMember(Name = "token")]
        public string token { get; set; }

        [DataMember(Name = "uid")]
        public int uid { get; set; }

        [DataMember(Name = "gid")]
        public int gid { get; set; }

        [DataMember(Name = "username")]
        public string username { get; set; }

        [DataMember(Name = "gpname")]
        public string gpname { get; set; }




    }
}
