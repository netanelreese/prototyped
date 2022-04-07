//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Jan 27 17:38:52 2020 by Chris Weaver
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
 * The <CODE>GalleryPane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class GalleryPane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Gallery";
	private static final String	HINT = "Gallery of Common JavaFX Widgets";

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

	// Layout
	private BorderPane				base;

	// Layout (gallery of widgets on the left)
	private CheckBox				checkBox1;
	private CheckBox				checkBox2;
	private CheckBox				checkBox3;
	private CheckBox				checkBox4;

	private ComboBox<String>		comboBox;

	private Label					label;

	private ProgressBar			progressBar;

	private RadioButton			radioButton1;
	private RadioButton			radioButton2;
	private RadioButton			radioButton3;

	private Slider					slider;

	private Spinner<Integer>		spinner;

	private TextArea				textArea;

	private TextField				textField;

	private ToolBar				toolBar;

	private Accordion				accordion;

	// Layout (accordion contents)
	private ListView<String>		accA;
	private ListView<String>		accB;

	// Layout (list on the right)
	private ListView<String>		list;

	// Layout (buttons on the bottom)
	private Button					strollButton;
	private Button					sleepButton;
	private Button					awakenButton;

	// Support
	private boolean				ignoreCaretEvents;

	// Handlers
	private final ActionHandler	actionHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public GalleryPane(Controller controller)
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
		// Widget Gallery, Checkboxes
		checkBox1.setSelected((Boolean)controller.get("lions"));
		checkBox2.setSelected((Boolean)controller.get("tigers"));
		checkBox3.setSelected((Boolean)controller.get("bears"));
		checkBox4.setSelected((Boolean)controller.get("surprised"));

		checkBox4.setDisable(!((Boolean)controller.get("lions") &&
							   (Boolean)controller.get("tigers") &&
							   (Boolean)controller.get("bears")));

		// Widget Gallery, ComboBox
		comboBox.getSelectionModel().select((String)controller.get("item"));

		// Widget Gallery, ListView
		list.getSelectionModel().select((String)controller.get("item"));

		// Widget Gallery, ProgressBar
		progressBar.setProgress(0.01 * (Double)controller.get("decimal"));

		// Widget Gallery, RadioButtons
		String	flavor = (String)controller.get("flavor");

		radioButton1.setSelected("vanilla".equals(flavor));
		radioButton2.setSelected("chocolate".equals(flavor));
		radioButton3.setSelected("strawberry".equals(flavor));

		// Widget Gallery, Slider
		slider.setValue((Double)controller.get("decimal"));

		// Widget Gallery, Spinner
		spinner.getValueFactory().setValue((Integer)controller.get("integer"));

		// Widget Gallery, Text Area
		textArea.setText((String)controller.get("comment"));

		// Widget Gallery, Text Field
		textField.setText((String)controller.get("string"));

		// Widget Gallery, Accordion
		accA.getSelectionModel().select((String)controller.get("item"));
		accB.getSelectionModel().select((String)controller.get("tool"));
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		// Widget Gallery, Buttons
		strollButton.setOnAction(null);
		sleepButton.setOnAction(null);
		awakenButton.setOnAction(null);

		// Widget Gallery, Checkboxes
		checkBox1.setOnAction(null);
		checkBox2.setOnAction(null);
		checkBox3.setOnAction(null);
		checkBox4.setOnAction(null);

		// Widget Gallery, ComboBox
		comboBox.getSelectionModel().selectedItemProperty().removeListener(
														this::changeItem);

		// Widget Gallery, ListView
		list.getSelectionModel().selectedItemProperty().removeListener(
														this::changeItem);

		// Widget Gallery, ProgressBar
		// Nothing to do here. JavaFX progress bars are not interactive!

		// Widget Gallery, RadioButtons
		radioButton1.setOnAction(null);
		radioButton2.setOnAction(null);
		radioButton3.setOnAction(null);

		// Widget Gallery, Slider
		slider.valueProperty().removeListener(this::changeDecimal);

		// Widget Gallery, Spinner
		spinner.valueProperty().removeListener(this::changeInteger);

		// Widget Gallery, Text Area
		textArea.textProperty().removeListener(this::changeComment);

		// Widget Gallery, Text Field
		textField.setOnAction(null);

		// Widget Gallery, Accordon
		accA.getSelectionModel().selectedItemProperty().removeListener(
														this::changeItem);
		accB.getSelectionModel().selectedItemProperty().removeListener(
														this::changeItem);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		//System.out.println("update " + key + " to " + value);

		if ("lions".equals(key) || "tigers".equals(key) || "bears".equals(key))
		{
			if ("lions".equals(key))
				checkBox1.setSelected((Boolean)value);
			else if ("tigers".equals(key))
				checkBox2.setSelected((Boolean)value);
			else if ("bears".equals(key))
				checkBox3.setSelected((Boolean)value);

			// Check to see if the fourth checkbox should be enabled/disabled.
			checkBox4.setDisable(!((Boolean)controller.get("lions") &&
								   (Boolean)controller.get("tigers") &&
								   (Boolean)controller.get("bears")));
		}
		else if ("surprised".equals(key))
		{
			checkBox4.setSelected((Boolean)value);
		}
		else if ("item".equals(key))
		{
			comboBox.getSelectionModel().select((String)value);
			list.getSelectionModel().select((String)value);
			accA.getSelectionModel().select((String)value);
		}
		else if ("flavor".equals(key))
		{
			if ("vanilla".equals(value))
				radioButton1.setSelected(true);
			if ("chocolate".equals(value))
				radioButton2.setSelected(true);
			if ("strawberry".equals(value))
				radioButton3.setSelected(true);
		}
		else if ("decimal".equals(key))
		{
			progressBar.setProgress(0.01 * (Double)value);
			slider.setValue((Double)value);
		}
		else if ("integer".equals(key))
		{
			spinner.getValueFactory().setValue((Integer)value);
		}
		else if ("comment".equals(key))
		{
			// Setting the text in a TextArea moves the caret to the start,
			// leading to an infinite update loop when there are multiple views.
			// Shut off caret event handling temporarily to avoid the problem.
			ignoreCaretEvents = true;
			textArea.setText((String)value);
			ignoreCaretEvents = false;
		}
		else if ("string".equals(key))
		{
			textField.setText((String)value);
		}
		else if ("tool".equals(key))
		{
			accB.getSelectionModel().select((String)value);
		}
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		// Create a vertical box of panes containing various widgets
		VBox		box = new VBox();

		box.setId("gallery-vbox");			// See #gallery-vbox in View.css

		box.getChildren().add(createCheckBoxes());
		box.getChildren().add(createComboBox());
		box.getChildren().add(createLabel());
		box.getChildren().add(createProgressBar());
		box.getChildren().add(createRadioButtons());
		box.getChildren().add(createSlider());
		box.getChildren().add(createSpinner());
		box.getChildren().add(createTextArea());
		box.getChildren().add(createTextField());
		box.getChildren().add(createToolBar());
		box.getChildren().add(createAccordion());

		for (Node node : box.getChildren())
			VBox.setMargin(node, new Insets(10));

		// Put the box full width in a scroll pane set for vertical scrolling
		ScrollPane	scrollPane = new ScrollPane(box);

		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToWidth(true);

		// Create a pane containing a list
		Pane		listPane = createList();

		// Create a split pane to share space between the scroll pane and list
		SplitPane	splitPane = new SplitPane();

		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.setDividerPosition(0, 0.7);	// Put divider at 70% L-to-R

		splitPane.getItems().add(scrollPane);
		splitPane.getItems().add(listPane);

		// Create a pane containing a few buttons
		Pane		buttonPane = createButtons();

		// Create a pane to layout the split pane and button pane
		base = new BorderPane();

		base.setCenter(splitPane);
		base.setBottom(buttonPane);

		BorderPane.setMargin(splitPane, new Insets(10, 10, 0, 10));
		BorderPane.setMargin(buttonPane, new Insets(10));

		return base;
	}

	//**********************************************************************
	// Private Methods (Widget Pane Creators)
	//**********************************************************************

	// Create a pane with three buttons for the gallery.
	private Pane	createButtons()
	{
		FlowPane	pane = new FlowPane(8.0, 8.0);

		pane.setAlignment(Pos.CENTER_RIGHT);

		ImageView	strollIcon = createSwingIcon("baby-carriage.png", 32);
		ImageView	sleepIcon = createSwingIcon("bed.png", 32);

		strollButton = new Button("Stroll", strollIcon);
		sleepButton = new Button("Sleep", sleepIcon);
		awakenButton = new Button("Awaken");

		strollButton.setOnAction(actionHandler);
		sleepButton.setOnAction(actionHandler);
		awakenButton.setOnAction(actionHandler);

		pane.getChildren().add(strollButton);
		pane.getChildren().add(sleepButton);
		pane.getChildren().add(awakenButton);

		return pane;//createTitledPane(pane, "Buttons");
	}

	// Create a pane with four checkboxes for the gallery. In this case, the
	// fourth checkbox depends on the settings of the other three. Use
	// setDisable(true) on any control to make it inactive.
	private Pane	createCheckBoxes()
	{
		checkBox1 = new CheckBox("Lions");
		checkBox2 = new CheckBox("Tigers");
		checkBox3 = new CheckBox("Bears");
		checkBox4 = new CheckBox("Oh My!");

		checkBox1.setOnAction(actionHandler);
		checkBox2.setOnAction(actionHandler);
		checkBox3.setOnAction(actionHandler);
		checkBox4.setOnAction(actionHandler);

		checkBox4.setDisable(true);	// See the end of the update() method.

		VBox	box = new VBox();

		box.getChildren().add(checkBox1);
		box.getChildren().add(checkBox2);
		box.getChildren().add(checkBox3);
		box.getChildren().add(checkBox4);

		VBox.setMargin(checkBox4, new Insets(10.0, 0.0, 0.0, 0.0));

		return createTitledPane(box, "CheckBoxes");
	}

	// Create a pane with a combobox for the gallery. The combobox uses a
	// custom ListCell class to draw its items. The combobox and the list
	// share the same selection.
	private Pane	createComboBox()
	{
		// See the Resources class for a convenient way to handle text access.
		ArrayList<String>		data =
			Resources.getLines("example/swing/text/list-data.txt");

		comboBox = new ComboBox<String>();
		comboBox.getItems().addAll(data);

		comboBox.setEditable(false);
		comboBox.setButtonCell(new MyListCell(false));
		comboBox.setCellFactory(new MyCellFactory(false));
		comboBox.setVisibleRowCount(4);

		comboBox.getSelectionModel().selectedItemProperty().addListener(
														this::changeItem);

		return createTitledPane(comboBox, "ComboBox");
	}

	// Create a pane with a label for the gallery.
	private Node	createLabel()
	{
		ImageView	icon = createSwingIcon("bicycle.png", 64);

		label = new Label("Bicycle", icon);
		label.setId("gallery-label");			// Style the label using CSS

		// Could have done it in code, too...
		//setBackgroundColor(label, Color.YELLOW);
		//label.setTextFill(Color.BLUE);
		//label.setFont(Font.font("Serif", FontWeight.BOLD,
		//						FontPosture.REGULAR, 24.0));

		return createTitledPane(label, "Label");
	}

	// Create a pane with a list for the gallery. The list uses a custom
	// CellFactory to draw its items. The combobox and the list share
	// the same selection.
	private Pane	createList()
	{
		ArrayList<String>	data =
			Resources.getLines("example/swing/text/list-data.txt");

		list = new ListView<String>();

		list.getItems().addAll(data);

		list.setEditable(false);
		list.setPlaceholder(new MyListCell(true));
		list.setCellFactory(new MyCellFactory(true));

		list.getSelectionModel().selectedItemProperty().addListener(
															this::changeItem);

		return createTitledPane(list, "List");
	}

	// Create a pane with a progress bar for the gallery. The progress bar and
	// slider show the same value from the model, so are synchronized.
	private Node	createProgressBar()
	{
		progressBar = new ProgressBar();

		//progressBar.setPrefSize(new Dimension(200, 32));

		return createTitledPane(progressBar, "ProgressBar");
	}

	// Create a pane with three radio buttons for the gallery. Putting them in
	// a ToggleGroup makes them mutually exclusive.
	private Pane	createRadioButtons()
	{
		ToggleGroup	tg = new ToggleGroup();

		radioButton1 = new RadioButton("Vanilla");
		radioButton2 = new RadioButton("Chocolate");
		radioButton3 = new RadioButton("Strawberry");

		radioButton1.setOnAction(actionHandler);
		radioButton2.setOnAction(actionHandler);
		radioButton3.setOnAction(actionHandler);

		radioButton1.setToggleGroup(tg);
		radioButton2.setToggleGroup(tg);
		radioButton3.setToggleGroup(tg);

		radioButton1.setPadding(new Insets(0.0, 4.0, 0.0, 4.0));
		radioButton2.setPadding(new Insets(0.0, 4.0, 0.0, 4.0));
		radioButton3.setPadding(new Insets(0.0, 4.0, 0.0, 4.0));

		HBox	box = new HBox();

		box.getChildren().add(radioButton1);
		box.getChildren().add(radioButton2);
		box.getChildren().add(radioButton3);

		return createTitledPane(box, "Radio Buttons");
	}

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

	// Create a pane with a text area for the gallery.
	private Pane	createTextArea()
	{
		textArea = new TextArea();

		textArea.setPrefRowCount(4);
		textArea.setPrefColumnCount(40);

		// Note: Buggy! Currently moves the caret to the start every time.
		//textArea.textProperty().addListener(this::changeComment);
		textArea.caretPositionProperty().addListener(this::changeCaret);

		return createTitledPane(textArea, "Text Area");
	}

	// Create a pane with a text field for the gallery.
	private Pane	createTextField()
	{
		textField = new TextField();

		textField.setPrefColumnCount(6);

		textField.setOnAction(actionHandler);

		return createTitledPane(textField, "Text Field");
	}

	private Pane	createToolBar()
	{
		ArrayList<String>	data =
			Resources.getLines("example/swing/text/tool-data.txt");

		toolBar = new ToolBar();

		for (String item : data)
		{
			String[]		text = item.split(",");
			ImageView		icon = createSwingIcon(text[1], 32);
			Button			button = new Button(text[0], icon);

			button.setContentDisplay(ContentDisplay.TOP);
			button.setPrefWidth(80);

			button.setOnAction(actionHandler);

			// Toolbars can take any kind of component. Usually buttons.
			toolBar.getItems().add(button);
		}

		return createTitledPane(toolBar, "ToolBar");
	}

	private Pane	createAccordion()
	{
		// Create ListView A to put in the accordion
		ArrayList<String>	adata =
			Resources.getLines("example/swing/text/list-data.txt");

		accA = new ListView<String>();

		accA.getItems().addAll(adata);

		accA.setEditable(false);
		accA.setPlaceholder(new MyListCell(true));
		accA.setCellFactory(new MyCellFactory(true));

		accA.getSelectionModel().selectedItemProperty().addListener(
														this::changeItem);

		// Create ListView B to put in the accordion
		ArrayList<String>	bdata =
			Resources.getLines("example/swing/text/tool-data.txt");

		accB = new ListView<String>();

		accB.getItems().addAll(bdata);

		accB.setEditable(false);
		accB.setPlaceholder(new MyListCell(true));
		accB.setCellFactory(new MyCellFactory(true));

		accB.getSelectionModel().selectedItemProperty().addListener(
														this::changeItem);

		// Create a TitledPane for each list and put them in the accordion
		TitledPane	titledPaneA = new TitledPane("Items", accA);
		TitledPane	titledPaneB = new TitledPane("Tools", accB);

		accordion = new Accordion(titledPaneA, titledPaneB);

		//accordion.setExpandedPane(titledPaneB);

		return createTitledPane(accordion, "Accordion");
	}

	//**********************************************************************
	// Private Methods (Property Change Handlers)
	//**********************************************************************

	private void	changeItem(ObservableValue<? extends String> observable,
							   String oldValue, String newValue)
	{
		if (observable == comboBox.getSelectionModel().selectedItemProperty())
			controller.set("item", newValue);
		else if (observable == list.getSelectionModel().selectedItemProperty())
			controller.set("item", newValue);
		else if (observable == accA.getSelectionModel().selectedItemProperty())
			controller.set("item", newValue);
		else if (observable == accB.getSelectionModel().selectedItemProperty())
			controller.set("tool", newValue);
	}

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

	private void	changeComment(ObservableValue<? extends String> observable,
								  String oldValue, String newValue)
	{
		if (observable == textArea.textProperty())
			controller.set("comment", newValue);
	}

	private void	changeCaret(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		if (ignoreCaretEvents)
			return;

		if (observable == textArea.caretPositionProperty())
			controller.set("comment", textArea.getText());
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

			if (source == strollButton)
				controller.trigger("strolling");
			else if (e.getSource() == sleepButton)
				controller.set("sleeping", 1);
			else if (e.getSource() == awakenButton)
				controller.set("sleeping", 0);
			else if (e.getSource() == checkBox1)
				controller.set("lions", checkBox1.isSelected());
			else if (e.getSource() == checkBox2)
				controller.set("tigers", checkBox2.isSelected());
			else if (e.getSource() == checkBox3)
				controller.set("bears", checkBox3.isSelected());
			else if (e.getSource() == checkBox4)
				controller.set("surprised", checkBox4.isSelected());
			else if (e.getSource() == radioButton1)
				controller.set("flavor", "vanilla");
			else if (e.getSource() == radioButton2)
				controller.set("flavor", "chocolate");
			else if (e.getSource() == radioButton3)
				controller.set("flavor", "strawberry");
			else if (source == textField)
				controller.set("string", textField.getText());
			else if (source instanceof Button)				// ToolBar button
				controller.trigger(((Button)source).getText());
		}
	}

	//**********************************************************************
	// Inner Classes (Cell Factory)
	//**********************************************************************

	private static final class MyCellFactory
		implements Callback<ListView<String>, ListCell<String>>
	{
		private final boolean	showIcon;	// For combobox, or list?

		public MyCellFactory(boolean showIcon)
		{
			this.showIcon = showIcon;
		}

		public ListCell<String>	call(ListView<String> v)
		{
			return new MyListCell(showIcon);
		}
	}

	private static final class MyListCell extends ListCell<String>
	{
		private final boolean	showIcon;	// For combobox, or list?

		public MyListCell(boolean showIcon)
		{
			this.showIcon = showIcon;
		}

		protected void	updateItem(String item, boolean empty)
		{
			super.updateItem(item, empty);			// Prepare for setup

			if (empty || (item == null))			// Handle special cases
			{
				setText(null);
				setGraphic(null);

				return;
			}

			// Parse lines from text file. Format: label,filename
			String[]	text = item.split(",");

			setText(text[0]);
			setFont(showIcon ? FONT_LARGE : FONT_SMALL);

			if (showIcon)
			{
				ImageView	icon = AbstractPane.createSwingIcon(text[1], 32);

				setGraphic(icon);
				setGraphicTextGap(2.0);
			}
		}
	}
}

//******************************************************************************
