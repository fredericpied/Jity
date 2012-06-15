SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `jity` ;
CREATE SCHEMA IF NOT EXISTS `jity` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `jity` ;

-- -----------------------------------------------------
-- Table `jity`.`job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `jity`.`job` ;

CREATE  TABLE IF NOT EXISTS `jity`.`job` (
  `id` DOUBLE NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(25) NOT NULL ,
  `description` VARCHAR(45) NULL ,
  `hostname` VARCHAR(45) NOT NULL ,
  `user` VARCHAR(45) NOT NULL ,
  `commandPath` VARCHAR(255) NOT NULL ,
  `isActived` TINYINT(1)  NOT NULL DEFAULT TRUE ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jity`.`execStatus`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `jity`.`execStatus` ;

CREATE  TABLE IF NOT EXISTS `jity`.`execStatus` (
  `id` DOUBLE NOT NULL AUTO_INCREMENT ,
  `dateBegin` DATETIME NOT NULL COMMENT 'Execution beginning Date/Time' ,
  `dateEnd` DATETIME NULL ,
  `status` INT NOT NULL COMMENT 'Execution status for the date time in (\"FAILED\",\"SUCESS\",\"EXECUTING\")' ,
  `job_id` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_execStatus_job1` (`job_id` ASC) ,
  CONSTRAINT `fk_execStatus_job1`
    FOREIGN KEY (`job_id` )
    REFERENCES `jity`.`job` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jity`.`calendar`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `jity`.`calendar` ;

CREATE  TABLE IF NOT EXISTS `jity`.`calendar` (
  `id` DOUBLE NOT NULL ,
  `name` VARCHAR(25) NOT NULL ,
  `description` VARCHAR(45) NULL ,
  `year` YEAR NOT NULL ,
  `openDaysTab` VARCHAR(366) NOT NULL COMMENT 'String indiquate if the day of year  is open (0 for closed, 1 for opened)' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jity`.`dateConstraint`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `jity`.`dateConstraint` ;

CREATE  TABLE IF NOT EXISTS `jity`.`dateConstraint` (
  `id` DOUBLE NOT NULL ,
  `planifRule` VARCHAR(255) NOT NULL ,
  `calendar_id` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_dateConstraint_calendar1` (`calendar_id` ASC) ,
  CONSTRAINT `fk_dateConstraint_calendar1`
    FOREIGN KEY (`calendar_id` )
    REFERENCES `jity`.`calendar` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jity`.`ExecConstraint`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `jity`.`ExecConstraint` ;

CREATE  TABLE IF NOT EXISTS `jity`.`ExecConstraint` (
  `job_id` DOUBLE NOT NULL ,
  `dateConstraint_id` DOUBLE NOT NULL ,
  INDEX `fk_JobConstraints_job1` (`job_id` ASC) ,
  INDEX `fk_JobConstraints_dateConstraint1` (`dateConstraint_id` ASC) ,
  CONSTRAINT `fk_JobConstraints_job1`
    FOREIGN KEY (`job_id` )
    REFERENCES `jity`.`job` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_JobConstraints_dateConstraint1`
    FOREIGN KEY (`dateConstraint_id` )
    REFERENCES `jity`.`dateConstraint` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
