USE [InventV2]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Ausleihe]') AND type in (N'U'))
DROP TABLE [dbo].[Ausleihe]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create table [dbo].Ausleihe 
(
	AusleihID int Identity not null,
    BenutzerID int not null,
	ObjektID int not null,
	Ausleihdatum date not null,
	Abgegeben bit not null
)
GO
    
ALTER TABLE Ausleihe
	ADD FOREIGN KEY (BenutzerID) REFERENCES Benutzer(BenutzerID) ON DELETE CASCADE
GO

ALTER TABLE Ausleihe
	ADD FOREIGN KEY (ObjektID) REFERENCES Objekt(ObjektID) ON DELETE CASCADE
GO