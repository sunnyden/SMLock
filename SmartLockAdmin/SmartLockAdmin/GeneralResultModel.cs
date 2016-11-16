using System.Runtime.Serialization;

namespace SmartLockAdmin
{
    [DataContract]
    public class GeneralResultModel
    {
        [DataMember(Name ="error")]
        public int error { get; set; }

        [DataMember(Name = "info")]
        public string info { get; set; }


    }
}
