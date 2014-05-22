-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2
-- http://www.phpmyadmin.net
--
-- Hoszt: localhost
-- Létrehozás ideje: 2014. máj. 23. 01:43
-- Szerver verzió: 5.5.37
-- PHP verzió: 5.4.4-14+deb7u9

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
('atlantis.local', 3),
('localhost', 4);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `languages`
--

CREATE TABLE IF NOT EXISTS `languages` (
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `languages`
--

INSERT INTO `languages` (`code`, `name`) VALUES
('en', 'English'),
('hu', 'Magyar');

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
  `parent-id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_nodes_parent-id` (`parent-id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- A tábla adatainak kiíratása `nodes`
--

INSERT INTO `nodes` (`id`, `type`, `disabled`, `parent-id`) VALUES
(1, 'page', 0, NULL),
(2, 'page', 0, 1),
(3, 'site', 0, NULL),
(4, 'site', 0, NULL);

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
('körte', NULL, 2, 'hu');

-- --------------------------------------------------------

--
-- Tábla szerkezet: `page-params`
--

CREATE TABLE IF NOT EXISTS `page-params` (
  `page-id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `index` int(11) NOT NULL,
  KEY `page-id` (`page-id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `page-params`
--

INSERT INTO `page-params` (`page-id`, `name`, `index`) VALUES
(1, 'value1', 0),
(1, 'value2', 1);

-- --------------------------------------------------------

--
-- Tábla szerkezet: `pages`
--

CREATE TABLE IF NOT EXISTS `pages` (
  `id` bigint(20) NOT NULL,
  `view-path` varchar(255) NOT NULL,
  `site-dependent` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `pages`
--

INSERT INTO `pages` (`id`, `view-path`, `site-dependent`) VALUES
(1, 'home.xhtml', 0),
(2, 'index.html', 0);

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `sites`
--

INSERT INTO `sites` (`id`) VALUES
(3),
(4);

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
  ADD CONSTRAINT `FK_page-filters_site-id` FOREIGN KEY (`site-id`) REFERENCES `sites` (`id`),
  ADD CONSTRAINT `FK_page-filters_page-id` FOREIGN KEY (`page-id`) REFERENCES `pages` (`id`);

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
  ADD CONSTRAINT `FK_sites_id` FOREIGN KEY (`id`) REFERENCES `nodes` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
