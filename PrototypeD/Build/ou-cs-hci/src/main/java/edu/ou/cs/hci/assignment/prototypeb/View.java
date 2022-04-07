//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb 25 13:36:56 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Updated for new PrototypeB in Spring 2020.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb;

//import java.lang.*;
import java.util.ArrayList;
import java.net.URL;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.*;
import edu.ou.cs.hci.assignment.prototypeb.pane.*;

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
	private static final double	SCENE_H = 512;		// Scene height

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Master of the program, manager of the data, mediator of all updates
	private final Controller				controller;

	// Handlers
	private final WindowHandler			windowHandler;

	// Layout
	private final ArrayList<AbstractPane>	panes;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	// Construct a scene and display it in a window (stage).
	public View(Controller controller, String name, double x, double y)
	{
		this.controller = controller;

		// Handle WINDOW_CLOSE_REQUESTs
		windowHandler = new WindowHandler();

		// Create a set of panes to include
		panes = new ArrayList<AbstractPane>();

		panes.add(new CollectionPane(controller));
		panes.add(new EditorPane(controller));

		// Create a tab pane with tabs for the set of included panes
		TabPane	tabPane = new TabPane();

		for (AbstractPane pane : panes)
			tabPane.getTabs().add(pane.createTab());

		// Create a scene with an initial size, and attach a style sheet to it
		Scene		scene = new Scene(tabPane, SCENE_W, SCENE_H);
		URL		url = View.class.getResource("View.css");
		String		surl = url.toExternalForm();

		scene.getStylesheets().add(surl);

		// Create a window/stage with a name and an initial position on screen
		Stage		stage = new Stage();

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
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		for (AbstractPane pane : panes)
			pane.terminate();
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		for (AbstractPane pane : panes)
			pane.update(key, value);
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************

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
