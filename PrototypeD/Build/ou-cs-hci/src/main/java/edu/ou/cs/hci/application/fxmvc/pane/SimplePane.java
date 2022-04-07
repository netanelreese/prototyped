//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Jan 27 17:39:03 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.application.fxmvc.pane;

//import java.lang.*;
import java.util.ArrayList;
import javafx.animation.*;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Callback;
import edu.ou.cs.hci.application.fxmvc.Controller;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>SimplePane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class SimplePane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Simple";
	private static final String	HINT = "Gallery of A Few JavaFX Widgets";

	//**********************************************************************
	// Private Class Members (Effects)
	//**********************************************************************

	private static final Font		FONT_LARGE =
		Font.font("Serif", FontPosture.ITALIC, 24.0);

	private static final Font		FONT_SMALL =
		Font.font("Serif", FontPosture.ITALIC, 18.0);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Layout (a few widgets)
	private Slider					slider;

	private Spinner<Integer>		spinner;

	private TextField				textField;

	// Handlers
	private final ActionHandler	actionHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public SimplePane(Controller controller)
	{
		super(controller, NAME, HINT);

		actionHandler = new ActionHandler();

		setBase(buildPane());
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// The controller calls this method when it adds a view.
	// Set up the nodes in the view with data accessed through the controller.
	public void	initialize()
	{
		// Widget Gallery, Slider
		slider.setValue((Double)controller.get("decimal"));

		// Widget Gallery, Spinner
		spinner.getValueFactory().setValue((Integer)controller.get("integer"));

		// Widget Gallery, Text Field
		textField.setText((String)controller.get("string"));
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		// Widget Gallery, Slider
		slider.valueProperty().removeListener(this::changeDecimal);

		// Widget Gallery, Spinner
		spinner.valueProperty().removeListener(this::changeInteger);

		// Widget Gallery, Text Field
		textField.setOnAction(null);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		//System.out.println("update " + key + " to " + value);

		if ("decimal".equals(key))
		{
			slider.setValue((Double)value);
		}
		else if ("integer".equals(key))
		{
			spinner.getValueFactory().setValue((Integer)value);
		}
		else if ("string".equals(key))
		{
			textField.setText((String)value);
		}
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		// Layout the widgets in a vertical flow with small gaps between them.
		FlowPane	pane = new FlowPane(Orientation.VERTICAL, 8.0, 8.0);

		pane.setAlignment(Pos.TOP_LEFT);

		pane.getChildren().add(createSlider());
		pane.getChildren().add(createSpinner());
		pane.getChildren().add(createTextField());

		return pane;
	}

	//**********************************************************************
	// Private Methods (Widget Pane Creators)
	//**********************************************************************

	// Create a pane with a slider for the gallery. The progress bar and
	// slider show the same value from the model, so are synchronized.
	private Pane	createSlider()
	{
		slider = new Slider(0.0, 100.0, 0.0);

		slider.setOrientation(Orientation.HORIZONTAL);
		slider.setMajorTickUnit(20.0);
		slider.setMinorTickCount(4);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);

		slider.valueProperty().addListener(this::changeDecimal);

		return createTitledPane(slider, "Slider");
	}

	// Create a pane with a spinner for the gallery. The progress bar,
	// slider, and spinner show the same value from the model, so stay synced.
	private Pane	createSpinner()
	{
		spinner = new Spinner<Integer>(0, 100, 0, 1);

		spinner.setEditable(true);
		spinner.getEditor().setPrefColumnCount(4);

		spinner.valueProperty().addListener(this::changeInteger);

		return createTitledPane(spinner, "Spinner");
	}

	// Create a pane with a text field for the gallery.
	private Pane	createTextField()
	{
		textField = new TextField();

		textField.setPrefColumnCount(6);

		textField.setOnAction(actionHandler);

		return createTitledPane(textField, "Text Field");
	}

	//**********************************************************************
	// Private Methods (Property Change Handlers)
	//**********************************************************************

	private void	changeDecimal(ObservableValue<? extends Number> observable,
								  Number oldValue, Number newValue)
	{
		if (observable == slider.valueProperty())
			controller.set("decimal", newValue);
	}

	private void	changeInteger(ObservableValue<? extends Number> observable,
								  Number oldValue, Number newValue)
	{
		if (observable == spinner.valueProperty())
			controller.set("integer", newValue);
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************

	private final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object	source = e.getSource();

			if (source == textField)
				controller.set("string", textField.getText());
		}
	}
}

//******************************************************************************
