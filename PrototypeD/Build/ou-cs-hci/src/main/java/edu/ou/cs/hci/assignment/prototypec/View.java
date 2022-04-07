//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Mar  1 12:41:45 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Updated for new PrototypeB in Spring 2020.
// 20200228 [weaver]:	Added menu code for new PrototypeC in Spring 2020.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypec;

//import java.lang.*;
import java.io.File;
import java.util.*;
import java.net.URL;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import edu.ou.cs.hci.assignment.prototypec.pane.*;

//******************************************************************************

/**
 * The <CODE>View</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class View
{
	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	SCENE_W = 960;		// Scene width
	private static final double	SCENE_H = 540;		// Scene height

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Master of the program, manager of the data, mediator of all updates
	private final Controller				controller;

	// TODO #2a: Add members for your menus/items here...
	private MenuItem						menuItemExample;

	// Handlers
	private final ActionHandler			actionHandler;
	private final WindowHandler			windowHandler;

	// Layout
	private final Stage					stage;
	private final ArrayList<AbstractPane>	panes;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	// Construct a scene and display it in a window (stage).
	public View(Controller controller, String name, double x, double y)
	{
		this.controller = controller;

		// Create a listener for various widgets that emit ActionEvents
		actionHandler = new ActionHandler();

		// Create a listener to handle WINDOW_CLOSE_REQUESTs
		windowHandler = new WindowHandler();

		// Create a set of panes to include
		panes = new ArrayList<AbstractPane>();

		// Construct the pane for the scene.
		Pane	view = buildView();

		// Create a scene with an initial size, and attach a style sheet to it
		Scene		scene = new Scene(view, SCENE_W, SCENE_H);
		URL		url = View.class.getResource("View.css");
		String		surl = url.toExternalForm();

		scene.getStylesheets().add(surl);

		// Create a window/stage with a name and an initial position on screen
		stage = new Stage();

		stage.setOnHiding(windowHandler);
		stage.setScene(scene);
		stage.setTitle(name);
		stage.setX(x);
		stage.setY(y);
		stage.show();
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// The controller calls this method when it adds a view.
	// Set up the nodes in the view with data accessed through the controller.
	public void	initialize()
	{
		for (AbstractPane pane : panes)
			pane.initialize();

		// Initialize your menus/items here...
		menuItemExample.setOnAction(actionHandler);
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		for (AbstractPane pane : panes)
			pane.terminate();

		// Terminate your menus/items here...
		menuItemExample.setOnAction(null);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		for (AbstractPane pane : panes)
			pane.update(key, value);

		// Update your menus as needed when any old model properties change...
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	updateProperty(String key, Object newValue, Object oldValue)
	{
		for (AbstractPane pane : panes)
			pane.updateProperty(key, newValue, oldValue);

		// Update your menus as needed when any new model observables change...
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildView()
	{
		// OPTIONAL: Swap the comments for the pairs of panes below if you'd
		// like to see a reference solution to Prototype B. Swap them back to
		// work on the TODOs for Prototype C.

		//panes.add(new CollectionPaneB(controller));
		//panes.add(new EditorPaneB(controller));

		panes.add(new CollectionPane(controller));
		panes.add(new EditorPane(controller));

		// Create a tab pane with tabs for the set of included panes
		TabPane	tabPane = new TabPane();

		for (AbstractPane pane : panes)
			tabPane.getTabs().add(pane.createTab());

		MenuBar	menuBar = buildMenuBar();

		return new BorderPane(tabPane, menuBar, null, null, null);
	}

	//**********************************************************************
	// Inner Classes (Menus)
	//**********************************************************************

	private MenuBar	buildMenuBar()
	{
		MenuBar	menuBar = new MenuBar();

		// TODO #2b: Build your Menus and MenuBar below. For any MenuItems you
		// use, add members and code to initialize(), terminate(), update(), and
		// updateProperty() above, as needed. The following example code creates
		// a menu with one item in it, with examples of graphics for decoration.

		// Create some simple Nodes used as examples of graphics in menus/items.
		Rectangle	decoration1 = new Rectangle(8.0, 8.0, Color.RED);
		Circle		decoration2 = new Circle(4.0, Color.BLUE);

		// Create MenuItems...
		menuItemExample = new MenuItem("Item", decoration1);

		// ...create Menus to hold them...
		Menu	menuExample = new Menu("Menu", decoration2);

		// ...add the MenuItems to their menus...
		menuExample.getItems().addAll(menuItemExample);

		// ...then add the Menus to the MenuBar.
		menuBar.getMenus().addAll(menuExample);

		return menuBar;
	}

	// TODO #3a: Implement the File/Open menu item handler, allowing the user
	// to select a CSV file to open. See the javafx.stage.FileChooser class.
	// Pass the chosen file to the Model via Controller.setProperty() to open.
	private void	handleFileOpenMenuItem()
	{
	}

	// TODO #3b: Implement the File/Save menu item handler, allowing the user
	// to select a CSV file to save. See the javafx.stage.FileChooser class.
	// Pass the chosen file to the Model via Controller.save() to save.
	private void	handleFileSaveMenuItem()
	{
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************

	// TODO #2c: Add code to process user selection of each of your MenuItems.
	// Call your handleFileOpenMenuItem() and handleFileSaveMenuItem() methods
	// above for those two menu items. For all other menu items, print a brief
	// but informative message to the console.
	private final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object	source = e.getSource();

			if (source == menuItemExample)
				System.out.println("User selected the example menu item.");
		}
	}

	private final class WindowHandler
		implements EventHandler<WindowEvent>
	{
		public void	handle(WindowEvent e)
		{
			if (e.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST)
				controller.removeView(View.this);
		}
	}
}

//******************************************************************************
