USE [InventV2]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Benutzer]') AND type in (N'U'))
DROP TABLE [dbo].[Benutzer]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create table [dbo].Benutzer 
(
    BenutzerID int Identity not null PRIMARY KEY,
	Kennung varchar(20) not null,
	Nachname varchar(50) not null,
    Vorname varchar(50) not null,
	Telefon varchar(20),
	EMail varchar(50),
	Administrator bit not null
)
GO
