-- phpMyAdmin SQL Dump
-- version 3.3.0
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 08, 2010 at 04:56 PM
-- Server version: 5.1.37
-- PHP Version: 5.3.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `wowzadb`
--

-- --------------------------------------------------------

--
-- Table structure for table `wza_bytes`
--

CREATE TABLE IF NOT EXISTS `wza_bytes` (
  `byt_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `byt_type` varchar(255) NOT NULL DEFAULT 'unknown',
  `byt_bytes` bigint(20) NOT NULL,
  `byt_name` varchar(255) NOT NULL DEFAULT 'unknown',
  `byt_seconds` int(11) NOT NULL,
  `byt_app` varchar(50) NOT NULL DEFAULT 'unknown',
  `byt_when` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`byt_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `wza_viewed`
--

CREATE TABLE IF NOT EXISTS `wza_viewed` (
  `view_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `view_user` int(11) NOT NULL,
  `view_session` varchar(255) NOT NULL DEFAULT 'unknown',
  `view_timein` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `view_timeout` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `view_type` varchar(255) NOT NULL DEFAULT 'unknown',
  `view_app` varchar(50) NOT NULL DEFAULT 'unknown',
  `view_video` varchar(255) NOT NULL DEFAULT 'unknown',
  PRIMARY KEY (`view_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;
