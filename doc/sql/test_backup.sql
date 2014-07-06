-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2
-- http://www.phpmyadmin.net
--
-- Hoszt: localhost
-- Létrehozás ideje: 2014. júl. 06. 13:49
-- Szerver verzió: 5.5.37
-- PHP verzió: 5.4.4-14+deb7u10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Adatbázis: `test_db`
--
CREATE DATABASE `test_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `test_db`;

-- --------------------------------------------------------

--
-- Tábla szerkezet: `domains`
--

CREATE TABLE IF NOT EXISTS `domains` (
  `domain` varchar(255) NOT NULL,
  `site-id` bigint(20) NOT NULL,
  PRIMARY KEY (`domain`),
  KEY `site-id` (`site-id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `domains`
--

INSERT INTO `domains` (`domain`, `site-id`) VALUES
('*.local', 4),
('localhost', 4);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `languages`
--

CREATE TABLE IF NOT EXISTS `languages` (
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `english-name` varchar(255) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `languages`
--

INSERT INTO `languages` (`code`, `name`, `english-name`) VALUES
('en', 'English', 'English'),
('hu', 'Magyar', 'Hungarian');

-- --------------------------------------------------------

--
-- Tábla szerkezet: `node-mappings`
--

CREATE TABLE IF NOT EXISTS `node-mappings` (
  `node-id` bigint(20) NOT NULL,
  `language-code` varchar(255) NOT NULL,
  PRIMARY KEY (`node-id`,`language-code`),
  KEY `FK_node-mappings_language-code` (`language-code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet: `nodes`
--

CREATE TABLE IF NOT EXISTS `nodes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(31) DEFAULT NULL,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  `priority` int(11) NOT NULL DEFAULT '0',
  `parent-id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_nodes_parent-id` (`parent-id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- A tábla adatainak kiíratása `nodes`
--

INSERT INTO `nodes` (`id`, `type`, `disabled`, `priority`, `parent-id`) VALUES
(1, 'page', 0, 0, NULL),
(2, 'page', 0, 0, 1),
(3, 'site', 0, 0, NULL),
(4, 'site', 0, 0, NULL),
(5, 'page', 0, 0, NULL),
(6, 'page', 0, 0, NULL),
(7, 'page', 0, 0, 6);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `page-filters`
--

CREATE TABLE IF NOT EXISTS `page-filters` (
  `single` tinyint(1) NOT NULL DEFAULT '0',
  `page-id` bigint(20) NOT NULL,
  `site-id` bigint(20) NOT NULL,
  PRIMARY KEY (`page-id`,`site-id`),
  KEY `FK_page-filters_site-id` (`site-id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `page-filters`
--

INSERT INTO `page-filters` (`single`, `page-id`, `site-id`) VALUES
(1, 1, 4),
(0, 2, 3);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `page-mappings`
--

CREATE TABLE IF NOT EXISTS `page-mappings` (
  `name` varchar(255) NOT NULL,
  `pretty-name` varchar(255) DEFAULT NULL,
  `node-id` bigint(20) NOT NULL,
  `language-code` varchar(255) NOT NULL,
  PRIMARY KEY (`node-id`,`language-code`),
  KEY `FK_page-mappings_language-code` (`language-code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `page-mappings`
--

INSERT INTO `page-mappings` (`name`, `pretty-name`, `node-id`, `language-code`) VALUES
('apple', NULL, 1, 'en'),
('alma', NULL, 1, 'hu'),
('pear', NULL, 2, 'en'),
('körte', NULL, 2, 'hu'),
('nothing', NULL, 5, 'en'),
('semmi', NULL, 5, 'hu'),
('empty', NULL, 6, 'en'),
('üres', NULL, 6, 'hu'),
('content', NULL, 7, 'en'),
('tartalom', NULL, 7, 'hu');

-- --------------------------------------------------------

--
-- Tábla szerkezet: `page-params`
--

CREATE TABLE IF NOT EXISTS `page-params` (
  `page-id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `bean-variable` varchar(255) DEFAULT NULL,
  `validator` varchar(255) DEFAULT NULL,
  `index` int(11) NOT NULL,
  KEY `page-id` (`page-id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `page-params`
--

INSERT INTO `page-params` (`page-id`, `name`, `bean-variable`, `validator`, `index`) VALUES
(1, 'value1', NULL, 'testValidator', 0),
(1, 'value2', NULL, NULL, 1);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `pages`
--

CREATE TABLE IF NOT EXISTS `pages` (
  `id` bigint(20) NOT NULL,
  `view-path` varchar(255) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `view-path-generated` tinyint(1) NOT NULL DEFAULT '0',
  `action-inherited` tinyint(1) NOT NULL DEFAULT '0',
  `parameter-incremented` tinyint(1) NOT NULL DEFAULT '0',
  `site-dependent` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `pages`
--

INSERT INTO `pages` (`id`, `view-path`, `action`, `view-path-generated`, `action-inherited`, `parameter-incremented`, `site-dependent`) VALUES
(1, '/faces/home.xhtml', 'language.test', 0, 0, 1, 0),
(2, 'test.xhtml', NULL, 0, 0, 0, 0),
(5, 'index.xhtml', NULL, 0, 0, 0, 0),
(6, '/no-faces/blabla.xhtml', NULL, 0, 0, 0, 0),
(7, 'sample.xhtml', NULL, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `shops`
--

CREATE TABLE IF NOT EXISTS `shops` (
  `address` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `site-id` bigint(20) NOT NULL,
  PRIMARY KEY (`site-id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet: `site-mappings`
--

CREATE TABLE IF NOT EXISTS `site-mappings` (
  `title` varchar(255) NOT NULL,
  `node-id` bigint(20) NOT NULL,
  `language-code` varchar(255) NOT NULL,
  PRIMARY KEY (`node-id`,`language-code`),
  KEY `FK_site-mappings_language-code` (`language-code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `site-mappings`
--

INSERT INTO `site-mappings` (`title`, `node-id`, `language-code`) VALUES
('Test title', 4, 'en'),
('Teszt cím', 4, 'hu');

-- --------------------------------------------------------

--
-- Tábla szerkezet: `sites`
--

CREATE TABLE IF NOT EXISTS `sites` (
  `id` bigint(20) NOT NULL,
  `home-page` bigint(20) DEFAULT NULL,
  `def-lang` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `home-page` (`home-page`),
  KEY `def-lang` (`def-lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `sites`
--

INSERT INTO `sites` (`id`, `home-page`, `def-lang`) VALUES
(3, NULL, NULL),
(4, 2, 'en');

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `domains`
--
ALTER TABLE `domains`
  ADD CONSTRAINT `domains_ibfk_1` FOREIGN KEY (`site-id`) REFERENCES `sites` (`id`);

--
-- Megkötések a táblához `node-mappings`
--
ALTER TABLE `node-mappings`
  ADD CONSTRAINT `FK_node-mappings_language-code` FOREIGN KEY (`language-code`) REFERENCES `languages` (`code`),
  ADD CONSTRAINT `FK_node-mappings_node-id` FOREIGN KEY (`node-id`) REFERENCES `nodes` (`id`);

--
-- Megkötések a táblához `nodes`
--
ALTER TABLE `nodes`
  ADD CONSTRAINT `FK_nodes_parent-id` FOREIGN KEY (`parent-id`) REFERENCES `nodes` (`id`);

--
-- Megkötések a táblához `page-filters`
--
ALTER TABLE `page-filters`
  ADD CONSTRAINT `FK_page-filters_page-id` FOREIGN KEY (`page-id`) REFERENCES `pages` (`id`),
  ADD CONSTRAINT `FK_page-filters_site-id` FOREIGN KEY (`site-id`) REFERENCES `sites` (`id`);

--
-- Megkötések a táblához `page-mappings`
--
ALTER TABLE `page-mappings`
  ADD CONSTRAINT `FK_page-mappings_language-code` FOREIGN KEY (`language-code`) REFERENCES `languages` (`code`),
  ADD CONSTRAINT `page-mappings_ibfk_1` FOREIGN KEY (`node-id`) REFERENCES `pages` (`id`);

--
-- Megkötések a táblához `page-params`
--
ALTER TABLE `page-params`
  ADD CONSTRAINT `FK_page-params_page-id` FOREIGN KEY (`page-id`) REFERENCES `pages` (`id`),
  ADD CONSTRAINT `page-params_ibfk_1` FOREIGN KEY (`page-id`) REFERENCES `pages` (`id`);

--
-- Megkötések a táblához `pages`
--
ALTER TABLE `pages`
  ADD CONSTRAINT `pages_ibfk_1` FOREIGN KEY (`id`) REFERENCES `nodes` (`id`);

--
-- Megkötések a táblához `shops`
--
ALTER TABLE `shops`
  ADD CONSTRAINT `shops_ibfk_1` FOREIGN KEY (`site-id`) REFERENCES `sites` (`id`);

--
-- Megkötések a táblához `site-mappings`
--
ALTER TABLE `site-mappings`
  ADD CONSTRAINT `FK_site-mappings_language-code` FOREIGN KEY (`language-code`) REFERENCES `languages` (`code`),
  ADD CONSTRAINT `site-mappings_ibfk_1` FOREIGN KEY (`node-id`) REFERENCES `sites` (`id`);

--
-- Megkötések a táblához `sites`
--
ALTER TABLE `sites`
  ADD CONSTRAINT `FK_sites_id` FOREIGN KEY (`id`) REFERENCES `nodes` (`id`),
  ADD CONSTRAINT `sites_ibfk_1` FOREIGN KEY (`home-page`) REFERENCES `pages` (`id`),
  ADD CONSTRAINT `sites_ibfk_2` FOREIGN KEY (`def-lang`) REFERENCES `languages` (`code`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
