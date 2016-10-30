#include<stdio.h>
#include<string.h>
#include <DS3231.h>
#include <Wire.h>

DS3231 Clock;
int hexAddr[6] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
int myAddr[6]={0x20,0x15,0x05,0x22,0x74,0x49};
int *getMac(char* origAddr) {
  /*
    Function:getMac
    Description:fetching the MAC address from a string
    Author:Haoqing Deng
    Date:2016-10-06
    usage: getMac("string in here")
    The function will get the mac address in this format :"+ADDR:1234:56:abcdef"
  */

  unsigned int i = 0, t = -1, sub_position = 0, tmp_value = 0xFF, position = 0, length = strlen(origAddr); //initialize all variables
  int addr_empty[6] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
#ifndef __cplusplus
  _Bool isFetched = 0;//initialize boolean
#else
  bool isFetched = 0;//initialize boolean
#endif
  for (i = 0; i < length; i++) {
    if (origAddr[i] == '+' && origAddr[i + 1] == 'A' && origAddr[i + 2] == 'D'
        && origAddr[i + 3] == 'D' && origAddr[i + 4] == 'R' && origAddr[i + 5] == ':')
    {
      i = i + 5;
      isFetched = 1;
      continue;
    }
    if (isFetched) {
      if (t == -1)
      {
        t = i;
      }
      if (i - t >= 14)
      {
        break;
      }
      int current = -1;
      switch (origAddr[i]) {
        case ':':
          continue;
        case '0':
          current = 0x00;
          break;
        case '1':
          current = 0x01;
          break;
        case '2':
          current = 0x02;
          break;
        case '3':
          current = 0x03;
          break;
        case '4':
          current = 0x04;
          break;
        case '5':
          current = 0x05;
          break;
        case '6':
          current = 0x06;
          break;
        case '7':
          current = 0x07;
          break;
        case '8':
          current = 0x08;
          break;
        case '9':
          current = 0x09;
          break;
        case 'a':
          current = 0x0A;
          break;
        case 'b':
          current = 0x0B;
          break;
        case 'c':
          current = 0x0C;
          break;
        case 'd':
          current = 0x0d;
          break;
        case 'e':
          current = 0x0E;
          break;
        case 'f':
          current = 0x0F;
          break;
        case 'A':
          current = 0x0A;
          break;
        case 'B':
          current = 0x0B;
          break;
        case 'C':
          current = 0x0C;
          break;
        case 'D':
          current = 0x0d;
          break;
        case 'E':
          current = 0x0E;
          break;
        case 'F':
          current = 0x0F;
          break;
        default:
          continue;
          break;
      }
      //printf("%c %d ",origAddr[i],current);

      if (!sub_position) {
        tmp_value = current;
        sub_position = 1;
      } else {
        sub_position = 0;
        tmp_value = tmp_value * 0x10 + current;
        hexAddr[position++] = tmp_value;
        tmp_value = 0xFF;
      }
      //putchar(origAddr[i]);
    }
  }
  return hexAddr;
}


int isLeapYear(int year) {
  /*
    Function:isLeapYear
    Description:determine whether the given year is leap year
    Author:Haoqing Deng
    Date:2016-10-07
    example: isLeapYear(2016);
    return value:1 or 0, 1 for Leap Year
  */
  int is_Leap_Year = 0;
  if (year % 4 == 0) {
    if (year / 100 == year) {
      if (year % 400 == 0) {
        is_Leap_Year = 1;
      }
    } else {
      is_Leap_Year = 1;
    }
  }
  return is_Leap_Year;
}

long getTimestamp(int year, int month, int date, int hr, int mins, int diff) {
  /*
    Function:getTimestamp
    Description:Converting normal time into unix timestamp with precision of 1min
    Author:Haoqing Deng
    Date:2016-10-07
    example: getTimestamp(2016,10,7,10,00,+8);
  		 to get the unix timestamp of 2016-10-07 10:00:00 at UTC+8 zone
    return value:long unix timestamp
  */
  long normal_date[12] = {31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
  long run_date[12] = {31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};
  long iYear, bgnYear = 1970, totaldays = 0, totalsec = 0, is_Leap_Year = 0;

  for (iYear = bgnYear; iYear < year; iYear++) {
    if (isLeapYear(iYear)) {
      totaldays += 366;
    } else {
      totaldays += 365;
    }
  }
  if (isLeapYear(year)) {
    totaldays += run_date[month - 2];
  } else {
    totaldays += normal_date[month - 2];
  }
  totaldays += date - 1;
  totalsec = totaldays * 24 * 3600 + ((long)hr - diff) * 3600 + mins * 60;

  return totalsec;
}

long getPwd(long timestamp, int *hexAddr) {
  /*
    Function used to generate code.
  */
  long pwd = 0;
  pwd = ((timestamp / 100) % 25 + 1) * hexAddr[(timestamp / 100) % 5];
  return pwd;
}

void getCommand(char* p) {
  /*
    Assume that the format is:
    passwd{568963215}
    recognize the number in it and convert it into long integer
  */

  int strlength = strlen(p);
  for (int i = 0; i < strlength; i++) {
    if (p[i] == 'c' && p[i + 1] == 'o' && p[i + 2] == 'd' && p[i + 3] == 'e' && p[i + 4] == '<') {
      i = i + 5; //<
      int code = 0;
      int pos = 1;
      while (p[i] != '>') {
        code =code*pos+  (p[i] - '0');
     
        pos = 10;
        i++;
      }
        int second,minute,hour,date,month,year,temperature; 

  minute=Clock.getMinute(),DEC;
  bool h12=false;
  bool PM=false;
  bool Century=false;
  hour=Clock.getHour(h12, PM),DEC;
  date=Clock.getDate(),DEC;
  month=Clock.getMonth(Century),DEC;
  year=Clock.getYear(),DEC;
  second=Clock.getSecond(),DEC;
      if(code==getPwd(getTimestamp(year+2000,month,date,hour,minute,+8),myAddr)){
          Serial1.println("{\"result\":0}");
      }else{
        Serial1.println("{\"result\":-1}");
      }
    }
    else if (p[i] == 't' && p[i + 1] == 'i' && p[i + 2] == 'm' && p[i + 3] == 'e' && p[i + 4] == '<') {
      i = i + 5; //<
      int num = 0;
      int power = 1;
      int pos =0;
      int intTime[6]={0,0,0,0,0,0};
      while (p[i] != '>') {
        
        if (p[i]!= ',') {

          num =num* power + (p[i] - '0');
          power = 10;
        }else{
          intTime[pos]=num;

          num=0;
          power=1;
          pos++;
          
        }
        i++;
      }
       
          if(pos==5){
            intTime[pos]=num;

          num=0;
          power=1;
          pos++;
          }


      Clock.setClockMode(false);
      Clock.setYear(intTime[0]);
      Clock.setMonth(intTime[1]);
       Clock.setDate(intTime[2]);
       Clock.setHour(intTime[3]);
       Clock.setMinute(intTime[4]);
       Clock.setSecond(intTime[5]);
       
    }else if (p[i] == 'c' && p[i + 1] == 'l' && p[i + 2] == 'o' && p[i + 3] == 's' && p[i + 4] == 'e') {
      //do sth here to lock.
      Serial1.println("{\"result\":0}");
      }
  }
  long pwd = 0;
}




