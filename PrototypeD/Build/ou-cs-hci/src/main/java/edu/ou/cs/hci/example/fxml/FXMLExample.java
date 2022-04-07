//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Thu Feb  7 00:42:00 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190206 [weaver]:	Original file.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.example.fxml;

//import java.lang.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

//******************************************************************************

public class FXMLExample extends Application
{
	//**********************************************************************
	// Main
	//**********************************************************************

	public static void  main(String[] args)
	{
		Application.launch(FXMLExample.class, args);
	}

	//**********************************************************************
	// Override Methods (Application)
	//**********************************************************************

	public void  start(Stage stage)
		throws Exception
	{
		Parent  root =
			FXMLLoader.load(getClass().getResource("Example.fxml"));

		stage.setTitle("FXML Example");
		stage.setScene(new Scene(root, 300, 275));
		stage.show();
	}
}

//******************************************************************************
