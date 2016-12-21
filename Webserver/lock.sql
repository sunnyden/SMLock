-- phpMyAdmin SQL Dump
-- version 4.0.10.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 21, 2016 at 08:18 AM
-- Server version: 5.1.73
-- PHP Version: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `lock`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_lock`
--

CREATE TABLE IF NOT EXISTS `tb_lock` (
  `lkid` int(11) NOT NULL AUTO_INCREMENT,
  `lkname` text COLLATE utf8_unicode_ci NOT NULL,
  `lkmac` text COLLATE utf8_unicode_ci NOT NULL,
  `lknum` int(11) NOT NULL,
  `access` text COLLATE utf8_unicode_ci NOT NULL,
  `status` tinyint(4) NOT NULL,
  PRIMARY KEY (`lkid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=6 ;



-- --------------------------------------------------------

--
-- Table structure for table `tb_log`
--

CREATE TABLE IF NOT EXISTS `tb_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `lid` int(11) NOT NULL,
  `action` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=3 ;


-- --------------------------------------------------------

--
-- Table structure for table `tb_session`
--

CREATE TABLE IF NOT EXISTS `tb_session` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `session_token` text COLLATE utf8_unicode_ci NOT NULL,
  `uid` int(11) NOT NULL,
  `imei` text COLLATE utf8_unicode_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `valid` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=39 ;




-- --------------------------------------------------------

--
-- Table structure for table `tb_usergroup`
--

CREATE TABLE IF NOT EXISTS `tb_usergroup` (
  `gid` int(11) NOT NULL AUTO_INCREMENT,
  `groupname` text COLLATE utf8_unicode_ci NOT NULL,
  UNIQUE KEY `gid` (`gid`),
  KEY `gid_2` (`gid`),
  KEY `gid_3` (`gid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=2 ;


-- --------------------------------------------------------

--
-- Table structure for table `tb_users`
--

CREATE TABLE IF NOT EXISTS `tb_users` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` text COLLATE utf8_unicode_ci NOT NULL,
  `passwd` text COLLATE utf8_unicode_ci NOT NULL,
  `gid` int(11) NOT NULL DEFAULT '3',
  PRIMARY KEY (`uid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=7 ;



/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
