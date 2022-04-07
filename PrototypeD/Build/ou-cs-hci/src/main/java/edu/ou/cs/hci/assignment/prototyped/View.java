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

package edu.ou.cs.hci.assignment.prototyped;

//import java.lang.*;
import java.io.File;
import java.util.*;
import java.net.URL;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.web.WebView;
import javafx.stage.*;
import edu.ou.cs.hci.assignment.prototyped.pane.*;

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

	// TODO #3: Adjust w and h to match your About Pane from Design D
	private static final double	ABOUT_W = 400;		// About Pane width
	private static final double	ABOUT_H = 300;		// About Pane height

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Master of the program, manager of the data, mediator of all updates
	private final Controller				controller;

	// Add members for your menus/items here...
	private MenuItem						appAboutMenuItem;
	private MenuItem						fileOpenMenuItem;
	private MenuItem						fileSaveMenuItem;

	// Handlers
	private final ActionHandler			actionHandler;
	private final WindowHandler			windowHandler;

	// Layout
	private final Stage					stage;
	private final ArrayList<AbstractPane>	panes;

	// About Stage
	private Stage							aboutStage;
	private Button							aboutStageCloseButton;

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

		// Create the stage to show when the About menu item is selected
		buildAboutStage();

		// Construct the pane for the scene.
		Pane	view = buildView();

		// Create a scene with an initial size, and attach a style sheet to it
		Scene		scene = new Scene(view, SCENE_W, SCENE_H + 32);
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
		aboutStageCloseButton.setOnAction(actionHandler);

		appAboutMenuItem.setOnAction(actionHandler);
		fileOpenMenuItem.setOnAction(actionHandler);
		fileSaveMenuItem.setOnAction(actionHandler);
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		for (AbstractPane pane : panes)
			pane.terminate();

		// Terminate your menus/items here...
		aboutStageCloseButton.setOnAction(null);

		appAboutMenuItem.setOnAction(null);
		fileOpenMenuItem.setOnAction(null);
		fileSaveMenuItem.setOnAction(null);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		for (AbstractPane pane : panes)
			pane.update(key, value);

		// Update your menus/items as needed here... (mine need no updating)
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	updateProperty(String key, Object newValue, Object oldValue)
	{
		for (AbstractPane pane : panes)
			pane.updateProperty(key, newValue, oldValue);

		// Update your menus/items as needed here...(mine need no updating)
	}

	//**********************************************************************
	// Private Methods (About Stage)
	//**********************************************************************

	private void	buildAboutStage()
	{
		WebView	webView = new WebView();
		URL		url = View.class.getResource("about/index.html");
		String		surl = url.toExternalForm();

		webView.getEngine().load(surl);

		aboutStageCloseButton = new Button("Close");
		BorderPane.setAlignment(aboutStageCloseButton, Pos.CENTER);

		BorderPane	pane = new BorderPane(webView, null, null,
										  aboutStageCloseButton, null);

		// TODO #4: Change the pane background color to match your about page.
		pane.setStyle("-fx-background-color: #ffffff;");

		Scene		scene = new Scene(pane, ABOUT_W, ABOUT_H);

		aboutStage = new Stage(StageStyle.UNDECORATED);
		aboutStage.setScene(scene);
		aboutStage.centerOnScreen();
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildView()
	{
		panes.add(new CollectionPane(controller));
		panes.add(new EditorPane(controller));

		// Add a second EditorPane that uses absolute positioning for layout.
		panes.add(new EditorPane2(controller));

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
		// Create the app (Movies) menu and populate it with menu items
		Menu			amenu = new Menu("Movies");

		appAboutMenuItem =
			createMenuItem("About...",  null, "ShortCut+I",    false);

		amenu.getItems().addAll(
			appAboutMenuItem,
			new SeparatorMenuItem(),
			createMenuItem("Quit",   null, "Shortcut+Q",       false));

		// Create the File menu and populate it with menu items
		Menu			fmenu = new Menu("File");

		fileOpenMenuItem =
			createMenuItem("Open",   null, "ShortCut+O",       false);
		fileSaveMenuItem =
			createMenuItem("Save",   null, "ShortCut+S",       false);

		fmenu.getItems().addAll(
			createMenuItem("New",    null, "Shortcut+N",       false),
			fileOpenMenuItem,
			new SeparatorMenuItem(),
			createMenuItem("Close",  null, "Shortcut+W",       false),
			fileSaveMenuItem,
			new SeparatorMenuItem(),
			createMenuItem("Print",  null, "Shortcut+P",       true));

		// Create the Edit menu and populate it with menu items
		Menu			emenu = new Menu("Edit");

		emenu.getItems().addAll(
			createMenuItem("Undo",   null, "Shortcut+Z",       true),
			createMenuItem("Redo",   null, "Shortcut+Shift+Z", true),
			new SeparatorMenuItem(),
			createMenuItem("Cut",    null, "Shortcut+X",       false),
			createMenuItem("Copy",   null, "Shortcut+C",       false),
			createMenuItem("Paste",  null, "Shortcut+V",       false));

		// Create the Window menu and populate it with menu items
		Menu			wmenu = new Menu("Window");
		ToggleGroup	tg = new ToggleGroup();

		wmenu.getItems().addAll(
			createRadioMenuItem("this.csv", null, tg, true),
			createRadioMenuItem("alt1.csv", null, tg, false),
			createRadioMenuItem("alt2.csv", null, tg, false),
			createRadioMenuItem("alt3.csv", null, tg, false),
			createRadioMenuItem("alt4.csv", null, tg, false),
			createRadioMenuItem("alt5.csv", null, tg, false),
			createRadioMenuItem("alt6.csv", null, tg, false));

		// Create the menu bar and populate it with menus
		MenuBar	menuBar = new MenuBar();

		menuBar.getMenus().addAll(amenu, fmenu, emenu, wmenu);

		return menuBar;
	}

	// Convenience method to allow terse creation of many plain menu items.
	private MenuItem	createMenuItem(String text, Node graphic,
									   String accelerator, boolean disable)
	{
		MenuItem	item = new MenuItem(text, graphic);

		item.setAccelerator(KeyCombination.valueOf(accelerator));
		item.setDisable(disable);

		return item;
	}

	// Convenience method to allow terse creation of many radio menu items.
	private RadioMenuItem	createRadioMenuItem(String text, Node graphic,
												ToggleGroup tg, boolean select)
	{
		RadioMenuItem	item = new RadioMenuItem(text, graphic);

		item.setToggleGroup(tg);
		item.setSelected(select);

		return item;
	}

	private void	handleAppAboutMenuItem()
	{
		aboutStage.show();
	}

	private void	handleFileOpenMenuItem()
	{
		FileChooser	chooser = new FileChooser();

		chooser.setTitle("Open Movie Collection File");
		chooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Collection Files", "*.csv"));

		File	file = chooser.showOpenDialog(stage);

		if (file != null)
			controller.setProperty("file", file);
	}

	private void	handleFileSaveMenuItem()
	{
		FileChooser	chooser = new FileChooser();

		chooser.setTitle("Save Movie Collection File");
		chooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Collection Files", "*.csv"));

		File	file = chooser.showSaveDialog(stage);

		if (file != null)
			controller.save(file);
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

			System.out.println("User selected a menu item: " + source);

			if (source == aboutStageCloseButton)
				aboutStage.hide();
			else if (source == appAboutMenuItem)
				handleAppAboutMenuItem();
			else if (source == fileOpenMenuItem)
				handleFileOpenMenuItem();
			else if (source == fileSaveMenuItem)
				handleFileSaveMenuItem();
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
