using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;

namespace SmartLockAdmin
{
    class InternetUtilities
    {
        public string msg="";
        public string POSTText(string contents)
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

            return responce;

        }

        public bool isSucceed(string responce)
        {
            try
            {
                var mStream = new MemoryStream(Encoding.Default.GetBytes(responce));
                var serializer = new DataContractJsonSerializer(typeof(GeneralResultModel));
                GeneralResultModel result = (GeneralResultModel)serializer.ReadObject(mStream);
                msg = result.info;
                return result.error==0?true:false;
            }catch (Exception ex)
            {
                msg = ex.Message;
            }
            return false;
        }
    }
}
