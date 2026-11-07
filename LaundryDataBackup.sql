/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 8.0.13 : Database - laundryservice
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`laundryservice` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `laundryservice`;

/*Table structure for table `files` */

DROP TABLE IF EXISTS `files`;

CREATE TABLE `files` (
  `id` varchar(255) NOT NULL,
  `data` longblob,
  `file_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `files` */

/*Table structure for table `hibernate_sequence` */

DROP TABLE IF EXISTS `hibernate_sequence`;

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `hibernate_sequence` */

insert  into `hibernate_sequence`(`next_val`) values (1),(1);

/*Table structure for table `ta_la_account_type` */

DROP TABLE IF EXISTS `ta_la_account_type`;

CREATE TABLE `ta_la_account_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACCOUNT_TYPE` varchar(255) DEFAULT NULL,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `ta_la_account_type` */

/*Table structure for table `ta_la_company_profile` */

DROP TABLE IF EXISTS `ta_la_company_profile`;

CREATE TABLE `ta_la_company_profile` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `LOCATION` varchar(255) DEFAULT NULL,
  `MOBILE_No` int(11) DEFAULT NULL,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `ADDRESS1` varchar(255) DEFAULT NULL,
  `ADDRESS2` varchar(255) DEFAULT NULL,
  `CITY` varchar(255) DEFAULT NULL,
  `COMPANY_NAME` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `EMAIL_ID` varchar(255) DEFAULT NULL,
  `FAX_No` int(11) DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `PERSON_NAME` varchar(255) DEFAULT NULL,
  `PHONE_No` int(11) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `ta_la_company_profile` */

/*Table structure for table `ta_la_extra_charges` */

DROP TABLE IF EXISTS `ta_la_extra_charges`;

CREATE TABLE `ta_la_extra_charges` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_status` int(11) NOT NULL,
  `discount_name` varchar(255) DEFAULT NULL,
  `discount_type` varchar(255) DEFAULT NULL,
  `value` int(11) NOT NULL,
  `charge_name` varchar(255) DEFAULT NULL,
  `charge_type` varchar(255) DEFAULT NULL,
  `charge_value` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `ta_la_extra_charges` */

/*Table structure for table `ta_la_washing_type` */

DROP TABLE IF EXISTS `ta_la_washing_type`;

CREATE TABLE `ta_la_washing_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `PRICE` int(11) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `WASHING_TYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `ta_la_washing_type` */

insert  into `ta_la_washing_type`(`ID`,`ACTIVE_STATUS`,`CREATED_BY`,`CREATED_DATE`,`LOCATION_ID`,`PRICE`,`UPDATED_BY`,`UPDATED_DATE`,`WASHING_TYPE`) values (1,1,'shiva','2019-03-23 15:05:48',NULL,333,NULL,NULL,'Ironing'),(2,1,'shiva','2019-03-23 15:30:19',NULL,33333,'shiva','2022-01-08 09:55:15','wash'),(3,1,'shiva','2019-03-23 16:49:26',NULL,0,NULL,'2022-01-08 09:55:15','IRON'),(4,1,'shiva','2019-03-23 16:49:44',NULL,333,NULL,NULL,'clean'),(5,1,NULL,'2022-01-08 09:52:16',NULL,0,NULL,NULL,'DRY WASH'),(6,1,NULL,'2022-01-08 09:52:51',NULL,0,NULL,NULL,'DRY WASH ABD CLEAN'),(7,1,NULL,'2022-01-08 12:32:24',NULL,0,NULL,NULL,'CLEAN 1');

/*Table structure for table `tb_billing_component` */

DROP TABLE IF EXISTS `tb_billing_component`;

CREATE TABLE `tb_billing_component` (
  `SID` int(11) NOT NULL AUTO_INCREMENT,
  `DISCOUNT_AMT` double DEFAULT NULL,
  `IRON_AMT` double DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `DISCPUNT_PERCENT` double DEFAULT NULL,
  `EST_ID` int(11) DEFAULT NULL,
  `INVOICE_NO` varchar(255) DEFAULT NULL,
  `ITEM_ID` int(11) DEFAULT NULL,
  `ITEM_NAME` varchar(255) DEFAULT NULL,
  `LAUNDRY_AMT` double DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `QTY` varchar(255) DEFAULT NULL,
  `TOTAL_AMT` double DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

/*Data for the table `tb_billing_component` */

insert  into `tb_billing_component`(`SID`,`DISCOUNT_AMT`,`IRON_AMT`,`CREATED_BY`,`CREATED_DATE`,`DISCPUNT_PERCENT`,`EST_ID`,`INVOICE_NO`,`ITEM_ID`,`ITEM_NAME`,`LAUNDRY_AMT`,`LOCATION_ID`,`QTY`,`TOTAL_AMT`,`UPDATED_BY`,`UPDATED_DATE`,`USER_ID`) values (1,0,0,'shiva','2019-03-15 00:55:06',0,4,NULL,2,'Shirt',0,NULL,'5',25,NULL,NULL,NULL),(2,0,0,NULL,'2019-03-15 07:24:56',0,5,NULL,2,'Shirt',0,NULL,'1',10,NULL,NULL,NULL),(3,0,0,NULL,'2019-03-15 07:24:58',0,6,NULL,2,'Shirt',0,NULL,'1',10,NULL,NULL,NULL),(4,0,0,NULL,'2019-03-15 08:38:36',0,7,NULL,2,'Shirt',0,NULL,'1',10,NULL,NULL,NULL),(5,0,0,NULL,'2019-03-15 08:38:38',0,8,NULL,2,'Shirt',0,NULL,'1',10,NULL,NULL,NULL),(6,0,0,NULL,'2019-03-15 08:57:43',0,9,NULL,2,'Shirt',0,NULL,'8',80,NULL,NULL,NULL),(7,0,0,NULL,'2019-03-15 08:57:45',0,10,NULL,2,'Shirt',0,NULL,'8',80,NULL,NULL,NULL),(8,0,0,NULL,'2019-03-15 13:47:01',0,11,NULL,2,'Shirt',0,NULL,'1',5,NULL,NULL,NULL),(9,0,0,NULL,'2019-03-15 13:47:57',0,12,NULL,2,'Shirt',0,NULL,'1',5,NULL,NULL,NULL),(10,0,0,NULL,'2019-03-15 14:36:45',0,13,NULL,10,'Bisht',0,NULL,'1',0,NULL,NULL,NULL),(11,0,0,NULL,'2019-03-15 15:30:37',0,14,NULL,10,'Bisht',0,NULL,'1',0,NULL,NULL,NULL),(12,0,0,NULL,'2019-03-15 15:30:39',0,14,NULL,2,'Shirt',0,NULL,'1',10,NULL,NULL,NULL),(13,0,0,NULL,'2019-03-15 15:30:45',0,14,NULL,3,'T-Shirt',0,NULL,'1',10,NULL,NULL,NULL),(14,0,0,NULL,'2019-03-15 15:30:47',0,14,NULL,13,'Serwal',0,NULL,'1',0,NULL,NULL,NULL),(15,0,0,NULL,'2019-03-15 15:34:56',0,15,NULL,10,'Bisht',0,NULL,'1',0,NULL,NULL,NULL),(16,0,0,NULL,'2019-03-15 15:34:59',0,15,NULL,2,'Shirt',0,NULL,'1',10,NULL,NULL,NULL),(17,0,0,NULL,'2019-03-15 15:35:01',0,15,NULL,3,'T-Shirt',0,NULL,'1',10,NULL,NULL,NULL),(18,0,0,NULL,'2019-03-15 15:35:03',0,15,NULL,13,'Serwal',0,NULL,'1',0,NULL,NULL,NULL);

/*Table structure for table `tb_estimation_details` */

DROP TABLE IF EXISTS `tb_estimation_details`;

CREATE TABLE `tb_estimation_details` (
  `EST_ID` int(11) NOT NULL AUTO_INCREMENT,
  `BILLING_STATUS` int(11) DEFAULT NULL,
  `SPCL_COMMENTS` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `CUS_ID` int(11) DEFAULT NULL,
  `DELIVERY_DATE` datetime DEFAULT NULL,
  `IRON_STS` varchar(255) DEFAULT NULL,
  `LAUNDRY_STS` varchar(255) DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`EST_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

/*Data for the table `tb_estimation_details` */

insert  into `tb_estimation_details`(`EST_ID`,`BILLING_STATUS`,`SPCL_COMMENTS`,`CREATED_BY`,`CREATED_DATE`,`CUS_ID`,`DELIVERY_DATE`,`IRON_STS`,`LAUNDRY_STS`,`LOCATION_ID`,`UPDATED_BY`,`UPDATED_DATE`,`USER_ID`) values (1,0,NULL,'shiva','2019-03-15 00:39:28',789456,'2019-03-14 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(2,0,NULL,'shiva','2019-03-15 00:39:50',789456,'2019-03-14 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(3,0,NULL,'shiva','2019-03-15 00:40:22',789456,'2019-03-14 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(4,0,NULL,'shiva','2019-03-15 00:55:06',789456,'2019-03-14 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(5,0,NULL,NULL,'2019-03-15 07:24:56',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(6,0,NULL,NULL,'2019-03-15 07:24:57',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(7,0,NULL,NULL,'2019-03-15 08:38:36',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(8,0,NULL,NULL,'2019-03-15 08:38:38',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(9,0,NULL,NULL,'2019-03-15 08:57:43',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(10,0,NULL,NULL,'2019-03-15 08:57:45',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(11,0,NULL,NULL,'2019-03-15 13:47:00',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(12,0,NULL,NULL,'2019-03-15 13:47:57',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(13,0,NULL,NULL,'2019-03-15 14:36:44',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(14,0,NULL,NULL,'2019-03-15 15:30:28',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL),(15,0,NULL,NULL,'2019-03-15 15:34:48',0,'2019-03-15 00:00:00',NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `tb_invoice_details` */

DROP TABLE IF EXISTS `tb_invoice_details`;

CREATE TABLE `tb_invoice_details` (
  `INV_ID` int(11) NOT NULL AUTO_INCREMENT,
  `ADVANCE_AMT` double DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `CUS_ID` int(11) DEFAULT NULL,
  `DISCOUNT_AMT` double DEFAULT NULL,
  `DISCOUNT_PERCENT` double DEFAULT NULL,
  `INVOICE_NO` varchar(255) DEFAULT NULL,
  `INVOICED_AMT` double DEFAULT NULL,
  `INVOICE_DATE` datetime DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `OUTSTANDING_AMT` double DEFAULT NULL,
  `PAID_AMT` double DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`INV_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `tb_invoice_details` */

/*Table structure for table `tb_la_customer_info` */

DROP TABLE IF EXISTS `tb_la_customer_info`;

CREATE TABLE `tb_la_customer_info` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `MOBILE_NO` varchar(255) DEFAULT NULL,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `CUSTOMER_NAME` varchar(255) DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `QATAR_ID` bigint(20) DEFAULT NULL,
  `REMARKS` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_customer_info` */

insert  into `tb_la_customer_info`(`ID`,`MOBILE_NO`,`ACTIVE_STATUS`,`ADDRESS`,`CREATED_BY`,`CREATED_DATE`,`CUSTOMER_NAME`,`LOCATION_ID`,`QATAR_ID`,`REMARKS`,`UPDATED_BY`,`UPDATED_DATE`) values (1,NULL,1,'north street','shiva','2021-10-30 19:01:21','prakashraj',NULL,400002,'test',NULL,'2021-10-30 19:24:59'),(2,NULL,1,'north street',NULL,'2021-10-30 19:03:57','prakashraj',NULL,4012,'test',NULL,'2021-10-30 19:52:16'),(3,'33001094',1,'north street1',NULL,'2021-10-30 19:09:10','sam1',NULL,291356234,'test1',NULL,NULL),(4,'3300109',1,'north street1',NULL,'2021-10-30 19:18:48','sam1',NULL,4000005,'test1',NULL,NULL),(5,'33001099',1,'north street1',NULL,'2021-10-30 19:20:01','sam1',NULL,4000005,'test1',NULL,NULL),(6,'',1,'north street1',NULL,'2021-10-30 19:21:08','sam1',NULL,421,'test1',NULL,NULL),(7,'123',1,'north street1',NULL,'2021-10-30 19:21:21','sam1',NULL,NULL,'test1',NULL,NULL);

/*Table structure for table `tb_la_item_category` */

DROP TABLE IF EXISTS `tb_la_item_category`;

CREATE TABLE `tb_la_item_category` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `CATEGORY_CODE` varchar(255) DEFAULT NULL,
  `CATEGORY_NAME` varchar(255) NOT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_hicrkbvnyify0sc6ro5xnc9v8` (`CATEGORY_NAME`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_item_category` */

insert  into `tb_la_item_category`(`ID`,`ACTIVE_STATUS`,`CATEGORY_CODE`,`CATEGORY_NAME`,`CREATED_BY`,`CREATED_DATE`,`LOCATION_ID`,`UPDATED_BY`,`UPDATED_DATE`) values (1,1,'001','MENS',NULL,'2019-03-14 21:13:47',NULL,NULL,'2022-03-26 07:57:22'),(2,1,'002','women',NULL,'2019-03-15 10:42:22',NULL,NULL,'2022-01-02 18:52:58'),(3,1,'003','Others',NULL,'2019-03-15 10:42:44',NULL,NULL,'2022-01-02 18:52:58'),(4,1,'004','Kids',NULL,'2019-03-15 10:43:06',NULL,NULL,'2022-01-02 18:52:58'),(5,1,'005','Local items',NULL,'2019-03-15 11:54:31',NULL,NULL,'2022-01-02 18:52:58'),(6,1,'006','Comon',NULL,'2020-02-18 18:45:22',NULL,NULL,'2022-01-02 18:52:58'),(7,1,'007','test items99',NULL,'2020-09-19 13:43:58',NULL,NULL,'2022-01-02 18:52:58'),(8,1,'008','Test Item2',NULL,'2021-10-29 16:53:46',NULL,NULL,'2022-01-02 18:52:58'),(9,1,'0011','MEN12',NULL,'2021-10-29 16:56:03',NULL,NULL,'2022-01-08 12:40:21'),(10,1,'113','mena',NULL,'2021-10-30 20:02:04',NULL,NULL,'2022-01-02 18:52:58'),(11,1,'114','SAREEES',NULL,'2021-12-11 16:10:35',NULL,NULL,'2022-01-02 18:52:58'),(12,1,'','A22',NULL,'2022-01-01 18:57:56',NULL,NULL,'2022-01-02 18:52:58'),(13,1,'','A23',NULL,'2022-01-02 18:00:51',NULL,NULL,'2022-01-02 18:52:58'),(14,1,'','A24',NULL,'2022-01-02 18:23:06',NULL,NULL,'2022-01-02 18:52:58'),(15,1,'100','A25A',NULL,'2022-01-02 18:33:41',NULL,NULL,'2022-01-02 18:52:58'),(16,1,'','MEN14',NULL,'2022-01-08 12:40:52',NULL,NULL,'2022-01-08 12:41:13');

/*Table structure for table `tb_la_item_master` */

DROP TABLE IF EXISTS `tb_la_item_master`;

CREATE TABLE `tb_la_item_master` (
  `ITEM_ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `CATEGORY_ID` int(11) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `IRONING_RATE` double NOT NULL DEFAULT '0',
  `ITEM_ALT_NAME` varchar(255) DEFAULT NULL,
  `ITEM_CATEGORY` varchar(255) DEFAULT NULL,
  `Item_CODE` varchar(255) DEFAULT NULL,
  `ITEM_NAME` varchar(255) NOT NULL,
  `LAUNDRY_RATE` double NOT NULL DEFAULT '0',
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `UNIT` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL,
  `arabic_name` varchar(255) DEFAULT NULL,
  `frequently_used` bit(1) DEFAULT NULL,
  `item_category_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ITEM_ID`),
  UNIQUE KEY `UK_ctskjc3lxrmdrvymiwjdnjbc6` (`ITEM_NAME`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_item_master` */

insert  into `tb_la_item_master`(`ITEM_ID`,`ACTIVE_STATUS`,`CATEGORY_ID`,`CREATED_BY`,`CREATED_DATE`,`IRONING_RATE`,`ITEM_ALT_NAME`,`ITEM_CATEGORY`,`Item_CODE`,`ITEM_NAME`,`LAUNDRY_RATE`,`LOCATION_ID`,`UNIT`,`UPDATED_BY`,`UPDATED_DATE`,`USER_ID`,`id`,`arabic_name`,`frequently_used`,`item_category_id`) values (1,0,0,'shiva','2019-03-14 22:25:52',50,'neck tshirt men','1','123','mat',20,NULL,NULL,NULL,'2019-03-15 00:48:59',NULL,0,NULL,NULL,NULL),(2,1,0,NULL,'2019-03-15 00:51:58',5,'Shirt','1','001','Shirt',10,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(3,1,0,NULL,'2019-03-15 10:46:40',10,'تي شيرت','1','002','T-Shirt',20,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(4,1,0,NULL,'2019-03-15 10:49:31',25,'اللىاللى','2','001','Chudithar',100,NULL,'1',NULL,'2019-03-15 11:01:03',NULL,0,NULL,NULL,NULL),(5,1,0,NULL,'2019-03-15 11:12:45',12,'العباءة','2','004','Abaya ',12,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(6,1,0,NULL,'2019-03-15 11:13:54',10,'الاميرة-','2','005','al-Amira',14,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(7,1,0,NULL,'2019-03-15 11:22:10',5,'بخنق','2','006','Bukhnuq',8,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(8,1,0,NULL,'2019-03-15 11:23:00',3,'برقع شرق الجزيرة العربية','2','007','Eastern Arabia Batula',9,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(9,1,0,NULL,'2019-03-15 11:24:04',4,'الشادور','2','008','Chador',6,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(10,1,0,NULL,'2019-03-15 11:28:50',8,'بشت','1','009','Bisht',3,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(11,1,0,NULL,'2019-03-15 11:30:29',10,'الثوب','1','010','Thobe',14,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(12,1,0,NULL,'2019-03-15 11:31:22',8,'غوترا وإغال','1','011','Ghutra and Egal',13,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(13,1,0,NULL,'2019-03-15 11:33:01',8,'Serwal','1','012','Serwal',4,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(14,1,0,NULL,'2019-03-15 11:35:48',11,'شالوار كاميز','1','013','Shalwar Kameez',16,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(15,1,0,NULL,'2019-03-15 11:40:18',10,'شالوار كاميز','4','014','Kids jatty',10,NULL,'1',NULL,NULL,NULL,0,NULL,NULL,NULL),(16,1,0,NULL,'2019-03-15 11:41:04',20,'بطانية','3','015','blanket',15,NULL,'2',NULL,NULL,NULL,0,NULL,NULL,NULL),(17,1,0,NULL,'2019-03-15 11:41:51',16,'منشفة','3','016','Towel',16,NULL,'2',NULL,NULL,NULL,0,NULL,NULL,NULL),(18,1,0,NULL,'2020-02-18 19:09:35',0,'m','1','36','cvvs',0,NULL,'3',NULL,'2020-02-18 19:22:16',NULL,0,NULL,NULL,NULL),(19,1,0,NULL,'2020-09-19 13:47:01',0,'qq','4','111','sattai',0,NULL,'6',NULL,NULL,NULL,0,NULL,NULL,NULL),(20,0,0,NULL,'2021-10-30 20:04:55',0,'sf','3','2002','bed',0,NULL,'3',NULL,'2021-10-30 20:05:30',NULL,0,NULL,NULL,NULL);

/*Table structure for table `tb_la_item_master_price_details` */

DROP TABLE IF EXISTS `tb_la_item_master_price_details`;

CREATE TABLE `tb_la_item_master_price_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_master_id` int(11) NOT NULL,
  `ordinary_price` int(11) NOT NULL,
  `serve_unit_id` int(11) DEFAULT NULL,
  `urgent_price` int(11) DEFAULT NULL,
  `washing_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_item_master_price_details` */

/*Table structure for table `tb_la_outgoing_payment` */

DROP TABLE IF EXISTS `tb_la_outgoing_payment`;

CREATE TABLE `tb_la_outgoing_payment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACCOUNT_TYPE` varchar(255) DEFAULT NULL,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `AMOUNT` int(11) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `ENTRY_DATE` datetime DEFAULT NULL,
  `INVOICE_NO` int(11) DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `PAYMENT_TYPE` varchar(255) DEFAULT NULL,
  `REMARKS` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `VOID_REASON` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_outgoing_payment` */

insert  into `tb_la_outgoing_payment`(`ID`,`ACCOUNT_TYPE`,`ACTIVE_STATUS`,`AMOUNT`,`CREATED_BY`,`CREATED_DATE`,`ENTRY_DATE`,`INVOICE_NO`,`LOCATION_ID`,`PAYMENT_TYPE`,`REMARKS`,`UPDATED_BY`,`UPDATED_DATE`,`VOID_REASON`) values (1,'Inte',1,200,'shiva','2019-03-23 08:36:37','2018-02-01 03:00:00',123,NULL,'cash','100mll Liquid','shiva','2019-03-23 08:52:43',''),(2,'Detergent powder',1,200,'shiva','2019-03-23 08:37:01','2019-02-01 03:00:00',123452,NULL,'card','100mll Liquid',NULL,NULL,'test'),(3,'Detergent powder',1,200,'shiva','2019-03-23 08:37:21','2019-02-01 03:00:00',123452,NULL,'card','100mll Liquid',NULL,NULL,'test');

/*Table structure for table `tb_la_payment_mode` */

DROP TABLE IF EXISTS `tb_la_payment_mode`;

CREATE TABLE `tb_la_payment_mode` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_status` int(11) DEFAULT NULL,
  `payment_mode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_payment_mode` */

/*Table structure for table `tb_la_payment_type` */

DROP TABLE IF EXISTS `tb_la_payment_type`;

CREATE TABLE `tb_la_payment_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_status` int(11) NOT NULL,
  `payment_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_payment_type` */

/*Table structure for table `tb_la_pwd_change` */

DROP TABLE IF EXISTS `tb_la_pwd_change`;

CREATE TABLE `tb_la_pwd_change` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NEWPWD` varchar(255) DEFAULT NULL,
  `OLDPWD` varchar(255) DEFAULT NULL,
  `USERID` bigint(20) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_pwd_change` */

/*Table structure for table `tb_la_unit` */

DROP TABLE IF EXISTS `tb_la_unit`;

CREATE TABLE `tb_la_unit` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `UNIT` varchar(255) NOT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_nmg7yqs6rrpndgowa2w80rfed` (`UNIT`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_unit` */

insert  into `tb_la_unit`(`ID`,`ACTIVE_STATUS`,`CREATED_BY`,`CREATED_DATE`,`LOCATION_ID`,`UNIT`,`UPDATED_BY`,`UPDATED_DATE`) values (1,1,NULL,'2019-03-15 00:48:38',NULL,'Nos',NULL,NULL),(2,1,NULL,'2019-03-15 10:43:23',NULL,'Pc',NULL,NULL),(3,1,NULL,'2019-03-15 10:43:32',NULL,'Pack',NULL,'2022-01-02 18:42:55'),(4,1,NULL,'2020-02-18 18:18:59',NULL,'boxs',NULL,'2022-01-02 18:42:55'),(5,1,NULL,'2020-02-18 18:30:56',NULL,'Carton',NULL,'2022-01-02 18:42:55'),(6,1,NULL,'2020-09-19 13:44:57',NULL,'Kgs',NULL,NULL),(7,1,NULL,'2020-09-19 13:47:19',NULL,'ML',NULL,NULL),(8,1,NULL,'2021-10-30 20:02:47',NULL,'small',NULL,'2022-01-02 18:42:32'),(9,1,NULL,'2022-01-02 18:38:46',NULL,'BOX',NULL,NULL),(10,1,NULL,'2022-01-02 18:39:16',NULL,'BOX1',NULL,'2022-01-02 18:42:55'),(11,1,NULL,'2022-01-02 18:41:44',NULL,'BOX2 SAMPLE',NULL,'2022-01-02 18:43:16');

/*Table structure for table `tb_la_user_info` */

DROP TABLE IF EXISTS `tb_la_user_info`;

CREATE TABLE `tb_la_user_info` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `BLOOD_GROUP` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `GENDER` varchar(255) DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `MOBILE_NO` bigint(20) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `REMARKS` varchar(255) DEFAULT NULL,
  `ROLE` varchar(255) NOT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ID` bigint(20) NOT NULL,
  `USER_NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_nyvudrymgml2k5jq3tmthot2v` (`USER_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_user_info` */

insert  into `tb_la_user_info`(`ID`,`ACTIVE_STATUS`,`ADDRESS`,`BLOOD_GROUP`,`CREATED_BY`,`CREATED_DATE`,`GENDER`,`LOCATION_ID`,`MOBILE_NO`,`PASSWORD`,`REMARKS`,`ROLE`,`UPDATED_BY`,`UPDATED_DATE`,`USER_ID`,`USER_NAME`) values (1,1,'chennai','O+ve',NULL,'2019-03-14 21:45:29','Male',NULL,33001091,'123','test','admin',NULL,NULL,333,'shiva'),(2,1,'','',NULL,'2019-03-15 09:24:39','',NULL,NULL,'123','','Admin',NULL,NULL,500,'shiva'),(3,1,'','',NULL,'2019-03-15 09:26:33','',NULL,NULL,'12','','Admin',NULL,NULL,501,'pandy'),(4,1,'chennai','O+',NULL,'2019-03-15 09:27:58','Male',NULL,NULL,'123','test','Admin',NULL,NULL,502,'santi'),(5,1,'chennai','O+ve',NULL,'2020-02-16 18:51:59','Male',NULL,33001091,'123','test','admin',NULL,NULL,3033,'shiva'),(6,1,'chennai','O+ve','shiva','2020-02-16 19:07:09','Male',NULL,33001091,'123','test','admin',NULL,NULL,3035,'shivaLe'),(7,1,'chennai','O+ve','shiva','2020-09-19 08:27:55','Male',NULL,33001091,'123','test','admin',NULL,NULL,333122,'akil'),(8,1,'','',NULL,'2020-09-19 08:29:31','',NULL,NULL,'123456','','Admin',NULL,NULL,1,'n'),(9,1,'chennai','O+ve','shiva','2020-09-19 08:41:04','Male',NULL,97433001091,'123','test','admin',NULL,NULL,333123,'akil'),(10,1,'ww','O-',NULL,'2020-09-19 08:53:46','Male',NULL,NULL,'123','ww','Admin',NULL,NULL,2,'akilla'),(11,1,'','',NULL,'2020-09-19 09:00:00','',NULL,97433001091,'123','','Vendor',NULL,NULL,3,'aki'),(12,1,'','',NULL,'2020-09-25 08:18:01','',NULL,NULL,'123','','Admin',NULL,NULL,4,'Ajmal'),(13,1,'','',NULL,'2020-09-25 08:18:47','',NULL,NULL,'123','','Vendor',NULL,NULL,5,'Biju'),(14,1,'z','O-',NULL,'2021-10-29 17:59:30','Male',NULL,NULL,'123','','Admin',NULL,NULL,6,'Ajmal'),(15,1,'','',NULL,'2021-10-29 18:01:36','',NULL,NULL,'123','','Admin',NULL,NULL,7,'sam'),(16,1,'','',NULL,'2021-10-29 18:02:11','',NULL,10010010,'123','','Vendor',NULL,NULL,8,'asd'),(17,1,'','',NULL,'2021-10-29 18:11:01','',NULL,33001091,'123','','Vendor',NULL,NULL,99,'q123');

/*Table structure for table `tb_la_user_role` */

DROP TABLE IF EXISTS `tb_la_user_role`;

CREATE TABLE `tb_la_user_role` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTIVE_STATUS` int(11) NOT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ROLE` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_ccgc94eagspqygu4v98inf21f` (`USER_ROLE`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `tb_la_user_role` */

insert  into `tb_la_user_role`(`ID`,`ACTIVE_STATUS`,`CREATED_BY`,`CREATED_DATE`,`LOCATION_ID`,`UPDATED_BY`,`UPDATED_DATE`,`USER_ROLE`) values (1,1,NULL,'2019-03-15 09:19:33',NULL,NULL,NULL,'Admin'),(2,0,NULL,'2019-03-15 09:34:05',NULL,NULL,'2021-10-30 15:53:18','salesmans'),(3,1,NULL,'2019-03-15 10:41:35',NULL,NULL,'2021-10-29 16:43:40','Cashier1'),(4,1,NULL,'2020-09-19 13:26:28',NULL,NULL,'2020-09-19 13:40:52','supervisor'),(5,1,NULL,'2020-09-25 08:10:53',NULL,NULL,NULL,'Accounts'),(6,0,NULL,'2020-09-25 08:11:02',NULL,NULL,'2021-10-30 15:54:15','driver'),(7,0,NULL,'2020-09-25 08:11:12',NULL,NULL,'2021-10-30 15:54:16','waiter'),(8,1,NULL,'2020-09-25 08:11:20',NULL,NULL,NULL,'cleaner'),(9,1,NULL,'2020-09-25 08:11:32',NULL,NULL,NULL,'manager'),(10,1,NULL,'2020-09-25 08:11:43',NULL,NULL,'2021-10-30 15:53:06','trainee2'),(11,0,NULL,'2021-10-29 16:44:04',NULL,NULL,'2021-10-29 16:48:48','Cashier2');

/*Table structure for table `tb_payment_details` */

DROP TABLE IF EXISTS `tb_payment_details`;

CREATE TABLE `tb_payment_details` (
  `PAY_ID` int(11) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `DISCOUNT_AMT` double DEFAULT NULL,
  `DISCOUNT_PERCENT` double DEFAULT NULL,
  `INVOICE_NUM` varchar(255) DEFAULT NULL,
  `LOCATION_ID` varchar(255) DEFAULT NULL,
  `PAID_AMT` double DEFAULT NULL,
  `PAID_MODE` varchar(255) DEFAULT NULL,
  `PAY_DATE` datetime DEFAULT NULL,
  `RECEIPT_NUM` varchar(255) DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`PAY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `tb_payment_details` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
