USE [InventV2]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Objekt]') AND type in (N'U'))
DROP TABLE [dbo].[Objekt]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create table [dbo].Objekt 
(
    ObjektID int Identity not null PRIMARY KEY,
	TypID int not null,
    AblageortID int not null,
	Inventarnummer int not null,
	Hersteller varchar(100) not null,
	Modell varchar(100) not null,
	Kaufdatum date not null,
	Einzelpreis float not null
)
GO

SET IDENTITY_INSERT [dbo].Objekt ON;

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Typ]') AND type in (N'U'))
DROP TABLE [dbo].[Typ]
GO

create table [dbo].Typ
(
	TypID int Identity not null PRIMARY KEY,
	Bezeichnung varchar(50) not null
)
GO

create table [dbo].Ablageort
(
	AblageortID int Identity not null PRIMARY KEY,
	Bezeichnung varchar(50) not null
)
GO

ALTER TABLE Objekt
	ADD FOREIGN KEY (TypID) REFERENCES Typ(TypID)
GO

ALTER TABLE Objekt
	ADD FOREIGN KEY (AblageortID) REFERENCES Ablageort(AblageortID)
GO