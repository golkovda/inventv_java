USE [InventV2]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AdministrationObjekte]') AND type in (N'U'))
DROP TABLE [dbo].[AdministrationObjekte]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create table [dbo].AdministrationObjekte
  (
	ObjektID int Identity not null,
	TypID int not null,
    Inventarnummer int not null,
	Hersteller varchar(100) not null,
	Modell varchar(100) not null,
	Kaufdatum date not null,
	Einzelpreis float not null,
	geaendert_von char(20) not null,
    geaendert_am datetime not null,
    aktion varchar(20) not null
	); 
GO

SET IDENTITY_INSERT [dbo].AdministrationObjekte ON;
GO