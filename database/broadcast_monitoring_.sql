-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 12, 2013 at 11:51 AM
-- Server version: 5.5.29
-- PHP Version: 5.4.6-1ubuntu1.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `broadcast_monitoring`
--

-- --------------------------------------------------------

--
-- Table structure for table `channel`
--

CREATE TABLE IF NOT EXISTS `channel` (
  `number` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf16_bin NOT NULL,
  `last_time_value` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL COMMENT '0 for TV and 1 for Radio',
  `interface` varchar(100) COLLATE utf16_bin NOT NULL,
  PRIMARY KEY (`number`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf16 COLLATE=utf16_bin AUTO_INCREMENT=7 ;

--
-- Dumping data for table `channel`
--

INSERT INTO `channel` (`number`, `name`, `last_time_value`, `type`, `interface`) VALUES
(1, 'NTV', 0, 0, 'Default'),
(2, 'KTN', 0, 0, 'Default'),
(3, 'Kiss 100', 0, 0, 'Default'),
(4, 'Capital FM', 0, 1, 'Default'),
(5, 'Classic', 0, 1, 'Default'),
(6, 'XFM', 0, 1, 'Default');

-- --------------------------------------------------------

--
-- Table structure for table `hash_set`
--

CREATE TABLE IF NOT EXISTS `hash_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `start_timestamp` datetime NOT NULL,
  `stop_timestamp` datetime NOT NULL,
  `url` varchar(255) COLLATE utf8_bin NOT NULL,
  `parent` int(11) NOT NULL,
  `start_real_time` int(11) NOT NULL,
  `parent_type` int(11) NOT NULL COMMENT '0 for channel and 1 for searchable content',
  PRIMARY KEY (`id`),
  KEY `parent` (`parent`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=152 ;

--
-- Dumping data for table `hash_set`
--

INSERT INTO `hash_set` (`id`, `start_timestamp`, `stop_timestamp`, `url`, `parent`, `start_real_time`, `parent_type`) VALUES
(1, '2013-04-08 05:44:36', '2013-04-08 05:44:36', '182_2013-04-08 05:44:36_2013-04-08 05:44:36.ser', 4, 182, 0),
(2, '2013-04-08 05:44:37', '2013-04-08 05:44:37', '212_2013-04-08 05:44:37_2013-04-08 05:44:37.ser', 4, 212, 0),
(3, '2013-04-08 13:17:53', '2013-04-08 13:17:53', '501_2013-04-08 13:17:53_2013-04-08 13:17:53.ser', 4, 501, 0),
(4, '2013-04-08 13:17:58', '2013-04-08 13:17:58', '605_2013-04-08 13:17:58_2013-04-08 13:17:58.ser', 4, 605, 0),
(5, '2013-04-08 13:17:58', '2013-04-08 13:17:59', '612_2013-04-08 13:17:58_2013-04-08 13:17:59.ser', 4, 612, 0),
(6, '2013-04-08 13:17:59', '2013-04-08 13:17:59', '622_2013-04-08 13:17:59_2013-04-08 13:17:59.ser', 4, 622, 0),
(7, '2013-04-08 13:17:59', '2013-04-08 13:18:00', '631_2013-04-08 13:17:59_2013-04-08 13:18:00.ser', 4, 631, 0),
(8, '2013-04-08 13:18:00', '2013-04-08 13:18:00', '641_2013-04-08 13:18:00_2013-04-08 13:18:00.ser', 4, 641, 0),
(9, '2013-04-08 13:18:00', '2013-04-08 13:18:01', '651_2013-04-08 13:18:00_2013-04-08 13:18:01.ser', 4, 651, 0),
(10, '2013-04-08 13:18:01', '2013-04-08 13:18:01', '661_2013-04-08 13:18:01_2013-04-08 13:18:01.ser', 4, 661, 0),
(11, '2013-04-08 13:18:01', '2013-04-08 13:18:02', '671_2013-04-08 13:18:01_2013-04-08 13:18:02.ser', 4, 671, 0),
(12, '2013-04-08 13:18:02', '2013-04-08 13:18:02', '681_2013-04-08 13:18:02_2013-04-08 13:18:02.ser', 4, 681, 0),
(13, '2013-04-08 13:18:02', '2013-04-08 13:18:03', '691_2013-04-08 13:18:02_2013-04-08 13:18:03.ser', 4, 691, 0),
(14, '2013-04-08 13:18:03', '2013-04-08 13:18:03', '701_2013-04-08 13:18:03_2013-04-08 13:18:03.ser', 4, 701, 0),
(15, '2013-04-08 13:18:03', '2013-04-08 13:18:03', '711_2013-04-08 13:18:03_2013-04-08 13:18:03.ser', 4, 711, 0),
(16, '2013-04-08 13:18:04', '2013-04-08 13:18:04', '721_2013-04-08 13:18:04_2013-04-08 13:18:04.ser', 4, 721, 0),
(17, '2013-04-08 13:18:04', '2013-04-08 13:18:04', '731_2013-04-08 13:18:04_2013-04-08 13:18:04.ser', 4, 731, 0),
(18, '2013-04-08 13:18:04', '2013-04-08 13:18:05', '741_2013-04-08 13:18:04_2013-04-08 13:18:05.ser', 4, 741, 0),
(19, '2013-04-08 13:18:05', '2013-04-08 13:18:05', '751_2013-04-08 13:18:05_2013-04-08 13:18:05.ser', 4, 751, 0),
(20, '2013-04-08 13:18:05', '2013-04-08 13:18:06', '761_2013-04-08 13:18:05_2013-04-08 13:18:06.ser', 4, 761, 0),
(21, '2013-04-08 13:18:06', '2013-04-08 13:18:06', '771_2013-04-08 13:18:06_2013-04-08 13:18:06.ser', 4, 771, 0),
(22, '2013-04-08 13:18:06', '2013-04-08 13:18:07', '781_2013-04-08 13:18:06_2013-04-08 13:18:07.ser', 4, 781, 0),
(23, '2013-04-08 13:18:07', '2013-04-08 13:18:07', '791_2013-04-08 13:18:07_2013-04-08 13:18:07.ser', 4, 791, 0),
(24, '2013-04-08 13:18:07', '2013-04-08 13:18:08', '801_2013-04-08 13:18:07_2013-04-08 13:18:08.ser', 4, 801, 0),
(25, '2013-04-08 13:18:08', '2013-04-08 13:18:08', '811_2013-04-08 13:18:08_2013-04-08 13:18:08.ser', 4, 811, 0),
(26, '2013-04-08 13:18:08', '2013-04-08 13:18:09', '821_2013-04-08 13:18:08_2013-04-08 13:18:09.ser', 4, 821, 0),
(27, '2013-04-08 13:18:09', '2013-04-08 13:18:09', '831_2013-04-08 13:18:09_2013-04-08 13:18:09.ser', 4, 831, 0),
(28, '2013-04-08 13:18:09', '2013-04-08 13:18:10', '841_2013-04-08 13:18:09_2013-04-08 13:18:10.ser', 4, 841, 0),
(29, '2013-04-08 13:18:10', '2013-04-08 13:18:10', '851_2013-04-08 13:18:10_2013-04-08 13:18:10.ser', 4, 851, 0),
(30, '2013-04-08 13:18:10', '2013-04-08 13:18:10', '861_2013-04-08 13:18:10_2013-04-08 13:18:10.ser', 4, 861, 0),
(31, '2013-04-08 13:18:11', '2013-04-08 13:18:11', '871_2013-04-08 13:18:11_2013-04-08 13:18:11.ser', 4, 871, 0),
(32, '2013-04-08 13:18:11', '2013-04-08 13:18:11', '881_2013-04-08 13:18:11_2013-04-08 13:18:11.ser', 4, 881, 0),
(33, '2013-04-08 13:18:12', '2013-04-08 13:18:12', '891_2013-04-08 13:18:12_2013-04-08 13:18:12.ser', 4, 891, 0),
(34, '2013-04-08 13:18:12', '2013-04-08 13:18:12', '901_2013-04-08 13:18:12_2013-04-08 13:18:12.ser', 4, 901, 0),
(35, '2013-04-08 13:18:12', '2013-04-08 13:18:13', '911_2013-04-08 13:18:12_2013-04-08 13:18:13.ser', 4, 911, 0),
(36, '2013-04-08 13:18:13', '2013-04-08 13:18:13', '921_2013-04-08 13:18:13_2013-04-08 13:18:13.ser', 4, 921, 0),
(37, '2013-04-08 13:18:13', '2013-04-08 13:18:14', '931_2013-04-08 13:18:13_2013-04-08 13:18:14.ser', 4, 931, 0),
(38, '2013-04-08 13:18:14', '2013-04-08 13:18:14', '941_2013-04-08 13:18:14_2013-04-08 13:18:14.ser', 4, 941, 0),
(39, '2013-04-08 13:18:14', '2013-04-08 13:18:15', '951_2013-04-08 13:18:14_2013-04-08 13:18:15.ser', 4, 951, 0),
(40, '2013-04-08 13:18:15', '2013-04-08 13:18:15', '961_2013-04-08 13:18:15_2013-04-08 13:18:15.ser', 4, 961, 0),
(41, '2013-04-08 13:18:15', '2013-04-08 13:18:16', '971_2013-04-08 13:18:15_2013-04-08 13:18:16.ser', 4, 971, 0),
(42, '2013-04-08 13:18:16', '2013-04-08 13:18:16', '981_2013-04-08 13:18:16_2013-04-08 13:18:16.ser', 4, 981, 0),
(43, '2013-04-08 13:18:16', '2013-04-08 13:18:16', '991_2013-04-08 13:18:16_2013-04-08 13:18:16.ser', 4, 991, 0),
(44, '2013-04-08 13:18:17', '2013-04-08 13:18:17', '1001_2013-04-08 13:18:17_2013-04-08 13:18:17.ser', 4, 1001, 0),
(45, '2013-04-08 13:18:17', '2013-04-08 13:18:17', '1011_2013-04-08 13:18:17_2013-04-08 13:18:17.ser', 4, 1011, 0),
(46, '2013-04-08 13:18:18', '2013-04-08 13:18:18', '1021_2013-04-08 13:18:18_2013-04-08 13:18:18.ser', 4, 1021, 0),
(47, '2013-04-08 13:18:18', '2013-04-08 13:18:18', '1031_2013-04-08 13:18:18_2013-04-08 13:18:18.ser', 4, 1031, 0),
(48, '2013-04-08 13:18:18', '2013-04-08 13:18:19', '1041_2013-04-08 13:18:18_2013-04-08 13:18:19.ser', 4, 1041, 0),
(49, '2013-04-08 13:18:19', '2013-04-08 13:18:19', '1051_2013-04-08 13:18:19_2013-04-08 13:18:19.ser', 4, 1051, 0),
(50, '2013-04-08 13:18:19', '2013-04-08 13:18:20', '1061_2013-04-08 13:18:19_2013-04-08 13:18:20.ser', 4, 1061, 0),
(51, '2013-04-08 13:18:20', '2013-04-08 13:18:20', '1071_2013-04-08 13:18:20_2013-04-08 13:18:20.ser', 4, 1071, 0),
(52, '2013-04-08 13:18:20', '2013-04-08 13:18:21', '1081_2013-04-08 13:18:20_2013-04-08 13:18:21.ser', 4, 1081, 0),
(53, '2013-04-08 13:18:21', '2013-04-08 13:18:21', '1091_2013-04-08 13:18:21_2013-04-08 13:18:21.ser', 4, 1091, 0),
(54, '2013-04-08 13:18:21', '2013-04-08 13:18:22', '1101_2013-04-08 13:18:21_2013-04-08 13:18:22.ser', 4, 1101, 0),
(55, '2013-04-08 13:18:22', '2013-04-08 13:18:22', '1111_2013-04-08 13:18:22_2013-04-08 13:18:22.ser', 4, 1111, 0),
(56, '2013-04-08 13:18:22', '2013-04-08 13:18:23', '1121_2013-04-08 13:18:22_2013-04-08 13:18:23.ser', 4, 1121, 0),
(57, '2013-04-08 13:18:23', '2013-04-08 13:18:23', '1131_2013-04-08 13:18:23_2013-04-08 13:18:23.ser', 4, 1131, 0),
(58, '2013-04-08 13:18:23', '2013-04-08 13:18:23', '1141_2013-04-08 13:18:23_2013-04-08 13:18:23.ser', 4, 1141, 0),
(59, '2013-04-08 13:18:24', '2013-04-08 13:18:24', '1151_2013-04-08 13:18:24_2013-04-08 13:18:24.ser', 4, 1151, 0),
(60, '2013-04-08 13:18:24', '2013-04-08 13:18:24', '1161_2013-04-08 13:18:24_2013-04-08 13:18:24.ser', 4, 1161, 0),
(61, '2013-04-08 13:18:25', '2013-04-08 13:18:25', '1171_2013-04-08 13:18:25_2013-04-08 13:18:25.ser', 4, 1171, 0),
(62, '2013-04-08 13:18:25', '2013-04-08 13:18:25', '1181_2013-04-08 13:18:25_2013-04-08 13:18:25.ser', 4, 1181, 0),
(63, '2013-04-08 13:18:25', '2013-04-08 13:18:26', '1191_2013-04-08 13:18:25_2013-04-08 13:18:26.ser', 4, 1191, 0),
(64, '2013-04-08 13:18:26', '2013-04-08 13:18:26', '1201_2013-04-08 13:18:26_2013-04-08 13:18:26.ser', 4, 1201, 0),
(65, '2013-04-08 13:18:26', '2013-04-08 13:18:27', '1211_2013-04-08 13:18:26_2013-04-08 13:18:27.ser', 4, 1211, 0),
(66, '2013-04-08 13:18:27', '2013-04-08 13:18:27', '1221_2013-04-08 13:18:27_2013-04-08 13:18:27.ser', 4, 1221, 0),
(67, '2013-04-08 13:18:27', '2013-04-08 13:18:28', '1231_2013-04-08 13:18:27_2013-04-08 13:18:28.ser', 4, 1231, 0),
(68, '2013-04-08 13:18:28', '2013-04-08 13:18:28', '1241_2013-04-08 13:18:28_2013-04-08 13:18:28.ser', 4, 1241, 0),
(69, '2013-04-08 13:18:28', '2013-04-08 13:18:29', '1251_2013-04-08 13:18:28_2013-04-08 13:18:29.ser', 4, 1251, 0),
(70, '2013-04-08 13:18:29', '2013-04-08 13:18:29', '1261_2013-04-08 13:18:29_2013-04-08 13:18:29.ser', 4, 1261, 0),
(71, '2013-04-08 13:18:29', '2013-04-08 13:18:29', '1271_2013-04-08 13:18:29_2013-04-08 13:18:29.ser', 4, 1271, 0),
(72, '2013-04-08 13:18:30', '2013-04-08 13:18:30', '1281_2013-04-08 13:18:30_2013-04-08 13:18:30.ser', 4, 1281, 0),
(73, '2013-04-08 13:18:30', '2013-04-08 13:18:30', '1291_2013-04-08 13:18:30_2013-04-08 13:18:30.ser', 4, 1291, 0),
(74, '2013-04-08 13:18:30', '2013-04-08 13:18:31', '1301_2013-04-08 13:18:30_2013-04-08 13:18:31.ser', 4, 1301, 0),
(75, '2013-04-08 13:18:31', '2013-04-08 13:18:31', '1311_2013-04-08 13:18:31_2013-04-08 13:18:31.ser', 4, 1311, 0),
(76, '2013-04-08 13:18:31', '2013-04-08 13:18:32', '1321_2013-04-08 13:18:31_2013-04-08 13:18:32.ser', 4, 1321, 0),
(77, '2013-04-08 13:18:32', '2013-04-08 13:18:32', '1331_2013-04-08 13:18:32_2013-04-08 13:18:32.ser', 4, 1331, 0),
(78, '2013-04-08 13:18:32', '2013-04-08 13:18:33', '1341_2013-04-08 13:18:32_2013-04-08 13:18:33.ser', 4, 1341, 0),
(79, '2013-04-08 13:18:33', '2013-04-08 13:18:33', '1351_2013-04-08 13:18:33_2013-04-08 13:18:33.ser', 4, 1351, 0),
(80, '2013-04-08 13:18:33', '2013-04-08 13:18:34', '1361_2013-04-08 13:18:33_2013-04-08 13:18:34.ser', 4, 1361, 0),
(81, '2013-04-08 13:18:34', '2013-04-08 13:18:34', '1371_2013-04-08 13:18:34_2013-04-08 13:18:34.ser', 4, 1371, 0),
(82, '2013-04-08 13:18:34', '2013-04-08 13:18:35', '1381_2013-04-08 13:18:34_2013-04-08 13:18:35.ser', 4, 1381, 0),
(83, '2013-04-08 13:18:35', '2013-04-08 13:18:35', '1391_2013-04-08 13:18:35_2013-04-08 13:18:35.ser', 4, 1391, 0),
(84, '2013-04-08 13:18:35', '2013-04-08 13:18:36', '1401_2013-04-08 13:18:35_2013-04-08 13:18:36.ser', 4, 1401, 0),
(85, '2013-04-08 13:18:36', '2013-04-08 13:18:36', '1411_2013-04-08 13:18:36_2013-04-08 13:18:36.ser', 4, 1411, 0),
(86, '2013-04-08 13:18:37', '2013-04-08 13:18:37', '1445_2013-04-08 13:18:37_2013-04-08 13:18:37.ser', 4, 1445, 0),
(87, '2013-04-08 13:18:38', '2013-04-08 13:18:38', '1468_2013-04-08 13:18:38_2013-04-08 13:18:38.ser', 4, 1468, 0),
(88, '2013-04-08 13:18:40', '2013-04-08 13:18:40', '1505_2013-04-08 13:18:40_2013-04-08 13:18:40.ser', 4, 1505, 0),
(89, '2013-04-08 13:18:43', '2013-04-08 13:18:43', '1565_2013-04-08 13:18:43_2013-04-08 13:18:43.ser', 4, 1565, 0),
(90, '2013-04-08 13:18:45', '2013-04-08 13:18:45', '1613_2013-04-08 13:18:45_2013-04-08 13:18:45.ser', 4, 1613, 0),
(91, '2013-04-08 13:18:51', '2013-04-08 13:18:51', '1736_2013-04-08 13:18:51_2013-04-08 13:18:51.ser', 4, 1736, 0),
(92, '2013-04-08 13:18:53', '2013-04-08 13:18:53', '1783_2013-04-08 13:18:53_2013-04-08 13:18:53.ser', 4, 1783, 0),
(93, '2013-04-08 13:18:55', '2013-04-08 13:18:55', '1837_2013-04-08 13:18:55_2013-04-08 13:18:55.ser', 4, 1837, 0),
(94, '2013-04-08 13:18:58', '2013-04-08 13:18:58', '1904_2013-04-08 13:18:58_2013-04-08 13:18:58.ser', 4, 1904, 0),
(95, '2013-04-08 13:18:59', '2013-04-08 13:18:59', '1911_2013-04-08 13:18:59_2013-04-08 13:18:59.ser', 4, 1911, 0),
(96, '2013-04-08 13:19:00', '2013-04-08 13:19:00', '1925_2013-04-08 13:19:00_2013-04-08 13:19:00.ser', 4, 1925, 0),
(97, '2013-04-08 13:19:01', '2013-04-08 13:19:01', '1952_2013-04-08 13:19:01_2013-04-08 13:19:01.ser', 4, 1952, 0),
(98, '2013-04-08 13:19:03', '2013-04-08 13:19:03', '1993_2013-04-08 13:19:03_2013-04-08 13:19:03.ser', 4, 1993, 0),
(99, '2013-04-08 13:19:03', '2013-04-08 13:19:03', '2001_2013-04-08 13:19:03_2013-04-08 13:19:03.ser', 4, 2001, 0),
(100, '2013-04-08 13:19:05', '2013-04-08 13:19:05', '2041_2013-04-08 13:19:05_2013-04-08 13:19:05.ser', 4, 2041, 0),
(101, '2013-04-08 13:19:06', '2013-04-08 13:19:06', '2071_2013-04-08 13:19:06_2013-04-08 13:19:06.ser', 4, 2071, 0),
(102, '2013-04-08 13:19:08', '2013-04-08 13:19:08', '2111_2013-04-08 13:19:08_2013-04-08 13:19:08.ser', 4, 2111, 0),
(103, '2013-04-08 13:19:11', '2013-04-08 13:19:11', '2177_2013-04-08 13:19:11_2013-04-08 13:19:11.ser', 4, 2177, 0),
(104, '2013-04-08 13:19:15', '2013-04-08 13:19:15', '2251_2013-04-08 13:19:15_2013-04-08 13:19:15.ser', 4, 2251, 0),
(105, '2013-04-08 13:19:18', '2013-04-08 13:19:18', '2331_2013-04-08 13:19:18_2013-04-08 13:19:18.ser', 4, 2331, 0),
(106, '2013-04-08 13:19:23', '2013-04-08 13:19:23', '2424_2013-04-08 13:19:23_2013-04-08 13:19:23.ser', 4, 2424, 0),
(107, '2013-04-08 13:19:28', '2013-04-08 13:19:28', '2535_2013-04-08 13:19:28_2013-04-08 13:19:28.ser', 4, 2535, 0),
(108, '2013-04-08 13:19:31', '2013-04-08 13:19:31', '2594_2013-04-08 13:19:31_2013-04-08 13:19:31.ser', 4, 2594, 0),
(109, '2013-04-08 13:19:34', '2013-04-08 13:19:34', '2671_2013-04-08 13:19:34_2013-04-08 13:19:34.ser', 4, 2671, 0),
(110, '2013-04-08 13:19:39', '2013-04-08 13:19:39', '2772_2013-04-08 13:19:39_2013-04-08 13:19:39.ser', 4, 2772, 0),
(111, '2013-04-08 13:19:41', '2013-04-08 13:19:41', '2826_2013-04-08 13:19:41_2013-04-08 13:19:41.ser', 4, 2826, 0),
(112, '2013-04-08 13:19:42', '2013-04-08 13:19:43', '2852_2013-04-08 13:19:42_2013-04-08 13:19:43.ser', 4, 2852, 0),
(113, '2013-04-08 13:19:44', '2013-04-08 13:19:44', '2881_2013-04-08 13:19:44_2013-04-08 13:19:44.ser', 4, 2881, 0),
(114, '2013-04-08 13:19:47', '2013-04-08 13:19:47', '2941_2013-04-08 13:19:47_2013-04-08 13:19:47.ser', 4, 2941, 0),
(115, '2013-04-08 13:19:50', '2013-04-08 13:19:51', '3022_2013-04-08 13:19:50_2013-04-08 13:19:51.ser', 4, 3022, 0),
(116, '2013-04-08 13:19:51', '2013-04-08 13:19:51', '3032_2013-04-08 13:19:51_2013-04-08 13:19:51.ser', 4, 3032, 0),
(117, '2013-04-08 13:19:51', '2013-04-08 13:19:52', '3041_2013-04-08 13:19:51_2013-04-08 13:19:52.ser', 4, 3041, 0),
(118, '2013-04-08 14:51:17', '2013-04-08 14:51:17', '967_2013-04-08 14:51:17_2013-04-08 14:51:17.ser', 4, 967, 0),
(119, '2013-04-08 14:51:17', '2013-04-08 14:51:17', '971_2013-04-08 14:51:17_2013-04-08 14:51:17.ser', 4, 971, 0),
(120, '2013-04-08 14:51:42', '2013-04-08 14:51:42', '1505_2013-04-08 14:51:42_2013-04-08 14:51:42.ser', 4, 1505, 0),
(121, '2013-04-08 14:51:42', '2013-04-08 14:51:42', '1511_2013-04-08 14:51:42_2013-04-08 14:51:42.ser', 4, 1511, 0),
(122, '2013-04-08 14:51:42', '2013-04-08 14:51:43', '1521_2013-04-08 14:51:42_2013-04-08 14:51:43.ser', 4, 1521, 0),
(123, '2013-04-08 14:51:43', '2013-04-08 14:51:43', '1531_2013-04-08 14:51:43_2013-04-08 14:51:43.ser', 4, 1531, 0),
(124, '2013-04-08 14:51:43', '2013-04-08 14:51:44', '1541_2013-04-08 14:51:43_2013-04-08 14:51:44.ser', 4, 1541, 0),
(125, '2013-04-08 14:51:44', '2013-04-08 14:51:44', '1551_2013-04-08 14:51:44_2013-04-08 14:51:44.ser', 4, 1551, 0),
(126, '2013-04-08 14:51:44', '2013-04-08 14:51:45', '1561_2013-04-08 14:51:44_2013-04-08 14:51:45.ser', 4, 1561, 0),
(127, '2013-04-08 14:51:45', '2013-04-08 14:51:45', '1571_2013-04-08 14:51:45_2013-04-08 14:51:45.ser', 4, 1571, 0),
(128, '2013-04-08 14:51:45', '2013-04-08 14:51:46', '1581_2013-04-08 14:51:45_2013-04-08 14:51:46.ser', 4, 1581, 0),
(129, '2013-04-08 14:51:46', '2013-04-08 14:51:46', '1591_2013-04-08 14:51:46_2013-04-08 14:51:46.ser', 4, 1591, 0),
(130, '2013-04-08 14:51:46', '2013-04-08 14:51:47', '1601_2013-04-08 14:51:46_2013-04-08 14:51:47.ser', 4, 1601, 0),
(131, '2013-04-08 14:51:47', '2013-04-08 14:51:47', '1611_2013-04-08 14:51:47_2013-04-08 14:51:47.ser', 4, 1611, 0),
(132, '2013-04-08 14:51:47', '2013-04-08 14:51:47', '1621_2013-04-08 14:51:47_2013-04-08 14:51:47.ser', 4, 1621, 0),
(133, '2013-04-08 14:51:48', '2013-04-08 14:51:48', '1631_2013-04-08 14:51:48_2013-04-08 14:51:48.ser', 4, 1631, 0),
(134, '2013-04-08 14:51:48', '2013-04-08 14:51:48', '1641_2013-04-08 14:51:48_2013-04-08 14:51:48.ser', 4, 1641, 0),
(135, '2013-04-08 14:51:48', '2013-04-08 14:51:49', '1651_2013-04-08 14:51:48_2013-04-08 14:51:49.ser', 4, 1651, 0),
(136, '2013-04-08 14:51:49', '2013-04-08 14:51:49', '1661_2013-04-08 14:51:49_2013-04-08 14:51:49.ser', 4, 1661, 0),
(137, '2013-04-08 14:51:49', '2013-04-08 14:51:50', '1671_2013-04-08 14:51:49_2013-04-08 14:51:50.ser', 4, 1671, 0),
(138, '2013-04-08 14:51:50', '2013-04-08 14:51:50', '1681_2013-04-08 14:51:50_2013-04-08 14:51:50.ser', 4, 1681, 0),
(139, '2013-04-08 14:51:50', '2013-04-08 14:51:51', '1691_2013-04-08 14:51:50_2013-04-08 14:51:51.ser', 4, 1691, 0),
(140, '2013-04-08 14:51:51', '2013-04-08 14:51:51', '1701_2013-04-08 14:51:51_2013-04-08 14:51:51.ser', 4, 1701, 0),
(141, '2013-04-08 14:51:51', '2013-04-08 14:51:52', '1711_2013-04-08 14:51:51_2013-04-08 14:51:52.ser', 4, 1711, 0),
(142, '2013-04-08 14:51:52', '2013-04-08 14:51:52', '1721_2013-04-08 14:51:52_2013-04-08 14:51:52.ser', 4, 1721, 0),
(143, '2013-04-08 14:51:52', '2013-04-08 14:51:53', '1731_2013-04-08 14:51:52_2013-04-08 14:51:53.ser', 4, 1731, 0),
(144, '2013-04-08 14:51:53', '2013-04-08 14:51:53', '1741_2013-04-08 14:51:53_2013-04-08 14:51:53.ser', 4, 1741, 0),
(145, '2013-04-08 14:51:53', '2013-04-08 14:51:53', '1751_2013-04-08 14:51:53_2013-04-08 14:51:53.ser', 4, 1751, 0),
(146, '2013-04-08 14:51:54', '2013-04-08 14:51:54', '1761_2013-04-08 14:51:54_2013-04-08 14:51:54.ser', 4, 1761, 0),
(147, '2013-04-08 14:51:54', '2013-04-08 14:51:54', '1771_2013-04-08 14:51:54_2013-04-08 14:51:54.ser', 4, 1771, 0),
(148, '2013-04-08 14:51:55', '2013-04-08 14:51:55', '1781_2013-04-08 14:51:55_2013-04-08 14:51:55.ser', 4, 1781, 0),
(149, '2013-04-08 14:51:55', '2013-04-08 14:51:55', '1791_2013-04-08 14:51:55_2013-04-08 14:51:55.ser', 4, 1791, 0),
(150, '2013-04-08 14:51:55', '2013-04-08 14:51:56', '1801_2013-04-08 14:51:55_2013-04-08 14:51:56.ser', 4, 1801, 0),
(151, '2013-04-08 14:51:56', '2013-04-08 14:51:56', '1811_2013-04-08 14:51:56_2013-04-08 14:51:56.ser', 4, 1811, 0);

-- --------------------------------------------------------

--
-- Table structure for table `searchable_content`
--

CREATE TABLE IF NOT EXISTS `searchable_content` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf16_bin NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_bin;

-- --------------------------------------------------------

--
-- Table structure for table `search_pointer`
--

CREATE TABLE IF NOT EXISTS `search_pointer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent` int(11) NOT NULL,
  `channel` int(11) NOT NULL,
  `last_hash_set_id` int(11) NOT NULL,
  `last_start_real_time` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `PARENT` (`parent`),
  KEY `CHANNEL` (`channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `variables`
--

CREATE TABLE IF NOT EXISTS `variables` (
  `name` varchar(100) COLLATE utf16_bin NOT NULL,
  `value` int(11) NOT NULL,
  `type` int(11) NOT NULL COMMENT '0 for indexing, 1 for searching',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_bin;

--
-- Dumping data for table `variables`
--

INSERT INTO `variables` (`name`, `value`, `type`) VALUES
('anchor2peakMaxFreqDiff', 1000, 2),
('frameSize', 2048, 2),
('hashSetGroupSize', 7, 1),
('hashmapSize', 10, 2),
('keyPieceMultiplier', 2, 1),
('limitKeyPieceSize', 1, 1),
('redundantThreshold', 2, 2),
('sampleRate', 44100, 2),
('sampledFrequencies', 5, 2),
('startFreq', 50, 2),
('targetZoneSize', 5, 2);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `search_pointer`
--
ALTER TABLE `search_pointer`
  ADD CONSTRAINT `search_pointer_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `searchable_content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `search_pointer_ibfk_3` FOREIGN KEY (`channel`) REFERENCES `channel` (`number`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
