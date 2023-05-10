USE [InventV2]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AdministrationBenutzer]') AND type in (N'U'))
DROP TABLE [dbo].[AdministrationBenutzer]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create table [dbo].AdministrationBenutzer
  (
	BenutzerID int Identity not null,
	Nachname varchar(50) not null,
    Vorname varchar(50) not null,
	Telefon varchar(20),
	EMail varchar(50),
	geaendert_von char(20) not null,
    geaendert_am datetime not null,
    aktion varchar(20) not null
	); 
GO

SET IDENTITY_INSERT [dbo].AdministrationBenutzer ON;
GO