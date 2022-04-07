//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Jan 28 09:28:34 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypea.pane;

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
import edu.ou.cs.hci.assignment.prototypea.Controller;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>EditorPane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class EditorPane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Editor";
	private static final String	HINT = "Movie Metadata Editor";

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

	public EditorPane(Controller controller)
	{
		super(controller, NAME, HINT);

		actionHandler = new ActionHandler();

		setBase(buildPane());
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// TODO #4: Write code to initialize the widgets in your layout with the
	// data attribute values from the model when the UI first appears.

	// The controller calls this method when it adds a view.
	// Set up the nodes in the view with data accessed through the controller.
	public void	initialize()
	{
		// Widget Gallery, Slider
		slider.setValue((Double)controller.get("myDouble"));

		// Widget Gallery, Spinner
		spinner.getValueFactory().setValue((Integer)controller.get("myInt"));

		// Widget Gallery, Text Field
		textField.setText((String)controller.get("myString"));
	}

	// TODO #5: Write code to detach widgets from any model properties (or other
	// resources) it has been using, in preparation for removing and destroying
	// the widget object. For Prototype A there's nothing to do for this step
	// since we only detach a widget when its window closes or the program ends.

	// TODO #6: Write code to remove widgets from the layout hierarchy. For
	// Prototype A there's nothing to do for this step since we only remove a
	// widget when its window closes or the program ends, and in those cases
	// JavaFX does the necessary cleanup automatically.

	// TODO #7: Write code to unregister each widget from any event listeners
	// and/or property change handlers it was registered with in TODO #2.
	
	// TODO #8: Write code to actually destroy the widget objects. There is
	// nothing to do here (in Prototype A or otherwise) since Java uses garbage
	// collection to destroy objects and reclaim any memory allocated for them.

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

	// TODO #10: Write code to take any changes to data attribute values in the
	// model and update the corresponding widgets. The Model and Controller
	// classes help by calling the update() method below whenever there is a
	// data attribute value. Use the same key (name) from TODO #0 to figure out
	// which data attribute has changed, and update the corresponding widget to
	// show the new value.

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		//System.out.println("update " + key + " to " + value);

		if ("myDouble".equals(key))
		{
			slider.setValue((Double)value);
		}
		else if ("myInt".equals(key))
		{
			spinner.getValueFactory().setValue((Integer)value);
		}
		else if ("myString".equals(key))
		{
			textField.setText((String)value);
		}
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	// TODO #3: Write code to organize the widgets, labels, etc. in your design
	// into a hierarchical layout of panes. Refer to the javafx.scene.layout
	// package in the JavaFX APIs to learn about available pane classes. You are
	// likely to find BorderPane, GridPane, HBox, StackPane, VBox most useful. 

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

	// TODO #1: Write methods to create the widgets used for editing,
	// showing, labeling, etc. the various data attributes in the model.
	// (Note that the example methods below put their widget inside a titled
	// pane and return that pane instead of the widget itself. For your design,
	// you probably won't want to box and label your widgets that way.)
	
	// TODO #2a: In your methods, include code to register each widget with the
	// appropriate event listeners and/or property change handlers. Refer to the
	// javafx.scene.control package in the JavaFX APIs to learn about the events
	// and properties utilized by each widget type.

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
	
	// TODO #2b: Add any additional methods needed to register change listening
	// for the properties of the widgets you created for your layout.
	
	// TODO #9a: In the methods you added, implement code to pass the modified
	// value of the observed property to the corresponding data attribute value
	// in the model.

	private void	changeDecimal(ObservableValue<? extends Number> observable,
								  Number oldValue, Number newValue)
	{
		if (observable == slider.valueProperty())
			controller.set("myDouble", newValue);
	}

	private void	changeInteger(ObservableValue<? extends Number> observable,
								  Number oldValue, Number newValue)
	{
		if (observable == spinner.valueProperty())
			controller.set("myInt", newValue);
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************
	
	// TODO #2c: Add any additional inner classes needed to register event
	// handling for the widgets you created for your layout.
	
	// TODO #9b: In the classes you added, implement the event handling method
	// to get the modified information in the relevant widget and use it to
	// update the corresponding data attribute value in the model.

	private final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object	source = e.getSource();

			if (source == textField)
				controller.set("myString", textField.getText());
		}
	}
}

//******************************************************************************
