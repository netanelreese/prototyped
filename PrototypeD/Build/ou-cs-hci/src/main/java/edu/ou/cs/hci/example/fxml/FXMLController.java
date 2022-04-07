//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Thu Feb  7 00:42:04 2019 by Chris Weaver
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

//******************************************************************************

public class FXMLController
{
	@FXML private Text		target;

	@FXML protected void	handleAction(ActionEvent event)
	{
		target.setFill(Color.YELLOW);
		target.setText("Button pressed");
	}
}

//******************************************************************************
