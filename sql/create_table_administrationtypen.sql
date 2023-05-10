USE [InventV2]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AdministrationTypen]') AND type in (N'U'))
DROP TABLE [dbo].[AdministrationTypen]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create table [dbo].AdministrationTypen
  (
	TypID int Identity not null,
	Bezeichnung varchar(50) not null,
	geaendert_von char(20) not null,
    geaendert_am datetime not null,
    aktion varchar(20) not null
	); 
GO
