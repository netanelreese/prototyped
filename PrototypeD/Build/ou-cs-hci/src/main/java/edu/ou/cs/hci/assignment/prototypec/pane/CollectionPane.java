//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Mar  1 21:09:39 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Overhauled for new PrototypeB in Spring 2020.
// 20200228 [weaver]:	Added example solution code for PrototypeB.
// 20200229 [weaver]:	Overhauled for new PrototypeC in Spring 2020.
// 20220228 [weaver]:	Added template code for multiple selection listening.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypec.pane;

//import java.lang.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Callback;
import javafx.util.converter.*;
import edu.ou.cs.hci.assignment.prototypec.*;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>CollectionPane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
@SuppressWarnings("unchecked")
public final class CollectionPane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Collection";
	private static final String	HINT = "Movie Collection Browser";

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 32;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height

	private static final double	W2 = 150;		// Item image width
	private static final double	H2 = W2 * 1.5;	// Item image height

	private static final Insets	PADDING =
		new Insets(40.0, 20.0, 40.0, 20.0);

	private static final Insets	PADDING_SUMMARY =
		new Insets(35.0, 15.0, 25.0, 15.0);

	//**********************************************************************
	// Private Class Members (Styling)
	//**********************************************************************

	private static final DecimalFormat		FORMAT_SCORE =
		new DecimalFormat("0.0");

	private static final Font				FONT_SCORE =
		Font.font("SansSerif", FontWeight.BOLD, 16.0);

	// Gradient for filling the background of the summary pane
	private static final LinearGradient	LINEAR_GRADIENT =
		new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
						   new Stop(0.00, Color.web("#56c1ff", 1.00)),
						   new Stop(1.00, Color.web("#f8ba00", 1.00)));

	// Background for the summary pane
	private static final Background		BACKGROUND =
		new Background(new BackgroundFill(LINEAR_GRADIENT, null, null));

		// Drop shadow for the labels and image in the summary pane
	private static final DropShadow		DROP_SHADOW =
		new DropShadow(5.0, 3.0, 3.0, Color.GRAY);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Data
	private final List<String>		gdata;		// Genre strings
	private final List<String>		rdata;		// Rating strings

	// Layout
	private TableView<Movie>		table;
	private SelectionModel<Movie>	smodel;

	// Add members for your summary widgets here...
	private Label					summaryTitle;
	private ImageView				summaryImage;
	private Label					summaryYear;
	private Label					summaryGenre;
	private Label					summaryRating;
	private Label					summaryRuntime;

	// TODO #6a: Add members for your accordion widgets here...
	//private CheckBox				someCheckBox;
	//private ComboBox<String>		someComboBox;
	//private ListView<String>		someListView;

	// Support
	private boolean				ignoreSelectionEvents;

	// Handlers
	private final ActionHandler	actionHandler;
	private final ListChangeHandler	changeHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public CollectionPane(Controller controller)
	{
		super(controller, NAME, HINT);

		// Get fixed data sets loaded by model from hardcoded file locations
		gdata = (List<String>)controller.getProperty("genres");
		rdata = (List<String>)controller.getProperty("ratings");

		// Create a listener for various widgets that emit ActionEvents
		actionHandler = new ActionHandler();

		// Create a listener for widgets with multiple selection
		changeHandler = new ListChangeHandler();

		// Construct the pane
		setBase(buildPane());
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// The controller calls this method when it adds a view.
	// Set up the nodes in the view with data accessed through the controller.
	public void	initialize()
	{
		registerWidgetHandlers();

		Movie	movie = (Movie)controller.getProperty("movie");

		if (movie == null)
			populateWidgetsWithDefaultValues();
		else
			populateWidgetsWithCurrentValues(movie);

		if (movie != null)
			registerPropertyListeners(movie);

		updateFilter();

		smodel.select(movie);
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		Movie	movie = (Movie)controller.getProperty("movie");

		if (movie != null)
			unregisterPropertyListeners(movie);

		unregisterWidgetHandlers();
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		// This was used with the old Model properties. Nothing here now!
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	updateProperty(String key, Object newValue, Object oldValue)
	{
		//System.out.println("CollectionPane.updateProperty " + key +
		//				   " to " + newValue + " from " + oldValue);

		if ("movie".equals(key))
		{
			Movie	mold = (Movie)oldValue;
			Movie	mnew = (Movie)newValue;

			if (mold != null)
				unregisterPropertyListeners(mold);

			if (mnew == null)
				populateWidgetsWithDefaultValues();
			else
				populateWidgetsWithCurrentValues(mnew);

			if (mnew != null)
				registerPropertyListeners(mnew);

			smodel.select(mnew);
		}
		else if ("movies".equals(key))
		{
			Movie	movie = (Movie)controller.getProperty("movie");

			updateFilter();
		}
	}

	//**********************************************************************
	// Private Methods (Widget and Property Management)
	//**********************************************************************

	private void	registerWidgetHandlers()
	{
		smodel.selectedItemProperty().addListener(this::changeItem);

		// TODO #6c: Register listeners for your accordion widgets.
		//someCheckBox.setOnAction(actionHandler);
		//someComboBox.getSelectionModel().selectedItemProperty()
		//	.addListener(this::changeString);
		//someListView.getSelectionModel().getSelectedIndices()
		//	.addListener(changeHandler);
	}

	private void	unregisterWidgetHandlers()
	{
		smodel.selectedItemProperty().removeListener(this::changeItem);

		// TODO #6d: Unregister listeners for your accordion widgets.
		//someCheckBox.setOnAction(null);
		//someComboBox.getSelectionModel().selectedItemProperty()
		//	.removeListener(this::changeString);
		//someListView.getSelectionModel().getSelectedIndices()
		//	.removeListener(changeHandler);
	}

	private void	registerPropertyListeners(Movie movie)
	{
		movie.titleProperty().addListener(this::handleChangeS);
		movie.imageProperty().addListener(this::handleChangeS);

		// TODO #7a: Register listeners for all other movie properties that are
		// displayed in summary widgets and/or edited in accordion widgets.
	}

	private void	unregisterPropertyListeners(Movie movie)
	{
		movie.titleProperty().removeListener(this::handleChangeS);
		movie.imageProperty().removeListener(this::handleChangeS);

		// TODO #7b: Unregister listeners for all other movie properties that
		// are displayed in summary widgets and/or edited in accordion widgets.
	}

	private void	populateWidgetsWithDefaultValues()
	{
		summaryTitle.setText("(Title)");
		summaryImage.setImage(null);
		summaryYear.setText("(Year)");
		summaryGenre.setText("(Genres)");
		summaryRating.setText("(Rating)");
		summaryRuntime.setText("(Runtime)");
	}

	private void	populateWidgetsWithCurrentValues(Movie movie)
	{
		summaryTitle.setText(movie.getTitle());
		summaryImage.setImage(movie.getImageAsImage(FX_ICON, W2, H2));
		summaryYear.setText(Integer.toString(movie.getYear()));
		summaryGenre.setText(movie.getGenreAsString(gdata));
		summaryRating.setText(movie.getRating());
		summaryRuntime.setText(movie.getRuntimeAsString());
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		Node	bregion = buildTableView();
		Node	tregion = buildCoverFlow();
		Node	lregion = buildAccordion();
		Node	rregion = buildMovieView();

		// Create a split pane to share space between the cover pane and table
		SplitPane	splitPane = new SplitPane();

		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.1);	// Put divider at 50% T-to-B

		splitPane.getItems().add(tregion);		// Cover flow at the top...
		splitPane.getItems().add(bregion);		// ...table view at the bottom

		StackPane	lpane = new StackPane(lregion);
		StackPane	rpane = new StackPane(rregion);

		return new BorderPane(splitPane, null, rregion, null, lregion);
	}

	private TableView<Movie>	buildTableView()
	{
		// Create the table and grab its selection model
		table = new TableView<Movie>();
		smodel = table.getSelectionModel();

		// Set up some helpful stuff including single selection mode
		table.setEditable(true);
		table.setPlaceholder(new Text("No Data!"));
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// Add columns for title and image
		table.getColumns().add(buildTitleColumn());
		table.getColumns().add(buildImageColumn());

		// Add columns for the other three attributes.
		table.getColumns().add(buildAttr1Column());
		table.getColumns().add(buildAttr2Column());
		table.getColumns().add(buildAttr3Column());

		return table;
	}

	private Node	buildCoverFlow()
	{
		Label	label = new Label("this space reserved for cover flow (later)");

		label.setPadding(PADDING);

		return label;
	}

	// Build the layout for the movie summary.
	private Node	buildMovieView()
	{
		summaryTitle = createLabel();

		summaryImage = new ImageView();
		summaryImage.setEffect(DROP_SHADOW);

		summaryYear = createLabel();
		summaryGenre = createLabel();
		summaryRating = createLabel();
		summaryRuntime = createLabel();

		VBox		vbox = new VBox(summaryYear, summaryGenre,
									summaryRating, summaryRuntime);
		BorderPane	pane = new BorderPane(summaryImage, summaryTitle, null,
										  vbox, null);

		pane.setPadding(PADDING_SUMMARY);
		pane.setBackground(BACKGROUND);

		return pane;
	}

	// Reusable method to create a formatted label with effects for the summary.
	private Label	createLabel()
	{
		Label	label = new Label();

		label.setPrefWidth(W2);
		label.setAlignment(Pos.BASELINE_CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setWrapText(true);
		label.setFont(FONT_SCORE);

		label.setEffect(DROP_SHADOW);

		return label;
	}

	// TODO #6b: Build the layout for your accordion here, showing titles for
	// each section of the accordion. For any widgets you use, add members and
	// code to the Widget and Property Management methods above, as needed.
	private Node	buildAccordion()
	{
		// Create your widgets, put them in an accordion to return...
		//someCheckBox = new CheckBox();
		//someComboBox = new ComboBox<String>();
		//someListView = new ListView<String>();

		// Replace this placeholder label with your actual accordion design.
		Label	label = new Label("this space reserved for accordion");

		label.setPadding(PADDING);
		label.setPrefWidth(100.0);
		label.setWrapText(true);

		return label;
	}

	//**********************************************************************
	// Private Methods (Filtering)
	//**********************************************************************

	private void	updateFilter()
	{
		ObservableList<Movie>	movies =
			(ObservableList<Movie>)controller.getProperty("movies");
		Predicate<Movie>		predicate = new FilterPredicate();

		ignoreSelectionEvents = true;
		table.setItems(new FilteredList<Movie>(movies, predicate));
		ignoreSelectionEvents = false;
	}

	private final class FilterPredicate
		implements Predicate<Movie>
	{
		public boolean	test(Movie movie)
		{
			// TODO #8: Implement tests for each of your accordion sections.
			// Compare edited widget values to movie property values.

			// Hint: Widget types that allow selection from a list have either
			// a SelectionModel (for one) or MultipleSelectionModel (for many).
			// They have methods for looking up selected index(es) or item(s).

			// Return false if the movie fails ANY of the tests. (Note that this
			// is a very aggressive form of filtering. Most or all movies should
			// appear filtered out for most combinations of accordion settings!)

			return true;
		}
	}

	//**********************************************************************
	// Private Methods (Table Columns)
	//**********************************************************************

	// This TableColumn displays titles, and allows editing.
	private TableColumn<Movie, String>	buildTitleColumn()
	{
		TableColumn<Movie, String>	column =
			new TableColumn<Movie, String>("Title");

		column.setEditable(true);
		column.setPrefWidth(250);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, String>("title"));
		column.setCellFactory(new TitleCellFactory());

		// Edits in this column update movie titles
		column.setOnEditCommit(new TitleEditHandler());

		return column;
	}

	// This TableColumn displays images, and does not allow editing.
	private TableColumn<Movie, String>	buildImageColumn()
	{
		TableColumn<Movie, String>	column =
			new TableColumn<Movie, String>("Image");

		column.setEditable(false);
		column.setPrefWidth(W + 8.0);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, String>("image"));
		column.setCellFactory(new ImageCellFactory());

		return column;
	}

	// This TableColumn displays scores, and allows editing.
	private TableColumn<Movie, Double>	buildAttr1Column()
	{
		TableColumn<Movie, Double>	column =
			new TableColumn<Movie, Double>("Score");

		column.setEditable(true);
		column.setPrefWidth(60);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, Double>("averageReviewScore"));
		column.setCellFactory(new Attr1CellFactory());

		// Edits in this column update movie scores
		column.setOnEditCommit(new Attr1EditHandler());

		return column;
	}

	// This TableColumn displays ratings, and allows editing.
	private TableColumn<Movie, String>	buildAttr2Column()
	{
		TableColumn<Movie, String>	column =
			new TableColumn<Movie, String>("Rating");

		column.setEditable(true);
		column.setPrefWidth(100);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, String>("rating"));
		column.setCellFactory(new Attr2CellFactory());

		// Edits in this column update movie ratings
		column.setOnEditCommit(new Attr2EditHandler());

		return column;
	}

	// This TableColumn displays genres, and allows editing.
	private TableColumn<Movie, Boolean>	buildAttr3Column()
	{
		TableColumn<Movie, Boolean>	column =
			new TableColumn<Movie, Boolean>("Animated");

		column.setEditable(true);
		column.setPrefWidth(80);
		column.setCellValueFactory(
			new PropertyValueFactory<Movie, Boolean>("isAnimated"));
		column.setCellFactory(new Attr3CellFactory());

		// Edits in this column update movie genres
		column.setOnEditCommit(new Attr3EditHandler());

		return column;
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************

	// For widgets that involve action events, like Button, CheckBox, TextField
	private final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object	source = e.getSource();

			// TODO #9a: Call updateFilter() if the action came from any
			// of your accordion widgets that involve action events.
		}
	}

	//**********************************************************************
	// Inner Classes (ListChangeListeners)
	//**********************************************************************

	// For widgets that involve list change events, like ListView
	private final class ListChangeHandler
		implements ListChangeListener<Integer>
	{
		public void	onChanged(ListChangeListener.Change<? extends Integer> c)
		{
			Object	source = c.getList();

			// TODO #9b: Call updateFilter() if the change came from any
			// of your accordion widgets that involve list change events.
		}
	}

	//**********************************************************************
	// Private Methods (Property Change Handlers, Table SelectionModel)
	//**********************************************************************

	private void	changeItem(ObservableValue<? extends Movie> observable,
							   Movie oldValue, Movie newValue)
	{
		// Ignore changes to Table selection that arise from filtering.
		if (ignoreSelectionEvents)
			return;

		if (observable == smodel.selectedItemProperty())
			controller.setProperty("movie", newValue);
	}

	//**********************************************************************
	// Private Methods (Property Change Handlers, Widgets)
	//**********************************************************************

	// TODO #9c: Add methods and code to call updateFilter() whenever the user
	// edits an edited value in one of your accordion widgets.

	// For example, for someComboBox...
	//private void	changeString(ObservableValue<? extends String> observable,
	//							 String oldValue, String newValue)
	//{
	//	if (observable == someComboBox.getSelectionModel().selectedItemProperty())
	//		updateFilter();
	//}

	// For widgets with a numeric value like Slider and Spinner, you can't use
	// their actual numeric type directly. Use the Number superclass instead.
	// Cast oldValue or newValue to the actual type inside the method as needed.
	//private void	changeDouble(ObservableValue<? extends Number> observable,
	//							 Number oldValue, Number newValue)
	//{
	//	//Double		value = (Double)newValue;
	//}

	//**********************************************************************
	// Private Methods (Property Change Handlers, Movie)
	//**********************************************************************

	// TODO #9d: Add methods and code to update the summary widgets whenever
	// the corresponding movie attributes change. Also call updateFilter() if a
	// change comes from a movie property being filtered in the accordion.

	private void	handleChangeS(ObservableValue<? extends String> observable,
								  String oldValue, String newValue)
	{
		Movie	movie = (Movie)controller.getProperty("movie");

		// Update summary widgets when corresponding movie properties change
		if (observable == movie.titleProperty())
			summaryTitle.setText(newValue);
		else if (observable == movie.imageProperty())
			summaryImage.setImage(movie.getImageAsImage(FX_ICON, W2, H2));
		// ...

		// Call updateFilter() if the change came from a filtering property.
		if (observable == movie.titleProperty())
			updateFilter();
		// ...
	}

	// For widgets with a numeric value like Slider and Spinner, you can't use
	// their actual numeric type directly. Use the Number superclass instead.
	// Cast oldValue or newValue to the actual type inside the method as needed.
	//private void	handleChangeI(ObservableValue<? extends Number> observable,
	//							  Number oldValue, Number newValue)
	//{
	//	//Integer		value = (Integer)newValue;
	//}

	//**********************************************************************
	// Inner Classes (Cell Factories)
	//**********************************************************************

	// This CellFactory creates Cells for the title column in the table.
	private final class TitleCellFactory
		implements Callback<TableColumn<Movie, String>,
							TableCell<Movie, String>>
	{
		public TableCell<Movie, String>	call(TableColumn<Movie, String> v)
		{
			return new TitleCell();
		}
	}

	// This CellFactory creates Cells for the image column in the table.
	private final class ImageCellFactory
		implements Callback<TableColumn<Movie, String>,
							TableCell<Movie, String>>
	{
		public TableCell<Movie, String>	call(TableColumn<Movie, String> v)
		{
			return new ImageCell();
		}
	}

	// This CellFactory creates Cells for the score column in the table.
	private final class Attr1CellFactory
		implements Callback<TableColumn<Movie, Double>,
							TableCell<Movie, Double>>
	{
		public TableCell<Movie, Double>	call(TableColumn<Movie, Double> v)
		{
			return new Attr1Cell();
		}
	}

	// This CellFactory creates Cells for the rating column in the table.
	private final class Attr2CellFactory
		implements Callback<TableColumn<Movie, String>,
							TableCell<Movie, String>>
	{
		public TableCell<Movie, String>	call(TableColumn<Movie, String> v)
		{
			return new Attr2Cell();
		}
	}

	// This CellFactory creates Cells for the isAnimated column in the table.
	private final class Attr3CellFactory
		implements Callback<TableColumn<Movie, Boolean>,
							TableCell<Movie, Boolean>>
	{
		public TableCell<Movie, Boolean>	call(TableColumn<Movie, Boolean> v)
		{
			return new Attr3Cell();
		}
	}

	//**********************************************************************
	// Inner Classes (Cells)
	//**********************************************************************

	// This TableCell displays the title, and allows editing in a TextField.
	private final class TitleCell
		extends TextFieldTableCell<Movie, String>
	{
		public TitleCell()
		{
			super(new DefaultStringConverter());	// Since values are Strings
		}

		public void	updateItem(String value, boolean isEmpty)
		{
			super.updateItem(value, isEmpty);		// Prepare for setup

			if (isEmpty || (value == null))		// Handle special cases
			{
				setText(null);
				setGraphic(null);

				return;
			}

			// This cell shows the value of the title attribute as simple text.
			// If the title is too long, an ellipsis is inserted in the middle.
			String	title = value;

			setText(title);
			setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
			setGraphic(null);
		}
	}

	// This TableCell displays the image, and doesn't allow editing.
	private final class ImageCell
		extends TableCell<Movie, String>
	{
		public void	updateItem(String value, boolean isEmpty)
		{
			super.updateItem(value, isEmpty);		// Prepare for setup

			if (isEmpty || (value == null))		// Handle special cases
			{
				setText(null);
				setGraphic(null);
				setAlignment(Pos.CENTER);

				return;
			}

			// This cell uses the value of the posterFileName attribute
			// to show an image loaded from resources/example/fx/icon.
			String		posterFileName = value;
			ImageView	image = createFXIcon(posterFileName, W, H);

			setText(null);
			setGraphic(image);
			setAlignment(Pos.CENTER);
		}
	}

	// This TableCell displays the score, and allows editing in a TextField.
	// Doing it with a Spinner would make more sense, but would require adding
	// a lot of custom code. (JavaFX doesn't provide a SpinnerTableCell class.)
	private final class Attr1Cell
		extends TextFieldTableCell<Movie, Double>
	{
		public Attr1Cell()
		{
			super(new DoubleStringConverter());	// Since values are Doubles
		}

		public void	updateItem(Double value, boolean isEmpty)
		{
			super.updateItem(value, isEmpty);		// Prepare for setup

			if (isEmpty || (value == null))		// Handle special cases
			{
				setText(null);
				setGraphic(null);

				return;
			}

			// This cell shows the value of the score attribute as simple text.
			// Positions the score on the right side of the cell.
			String	rating = FORMAT_SCORE.format(value);

			setText(rating);
			setGraphic(null);

			setAlignment(Pos.CENTER);
			setFont(FONT_SCORE);
		}
	}

	// This TableCell displays the rating, and allows editing in a ChoiceBox.
	// Only one can be chosen, since ChoiceBoxes are single selection.
	private final class Attr2Cell
		extends ChoiceBoxTableCell<Movie, String>
	{
		public Attr2Cell()
		{
			// Provide the choices available in the ChoiceBox.
			super(FXCollections.observableArrayList(rdata));
		}

		// Want to simply show the string, so no need to override updateItem().
	}

	// This TableCell displays isAnimated, and allows editing in a CheckBox.
	private final class Attr3Cell
		extends CheckBoxTableCell<Movie, Boolean>
	{
		public Attr3Cell()
		{
			super(new Attr3SelectedStateCallback());
		}
	}

	// This Callback returns a movie's property for its isAnimated
	// attribute. See the CheckBoxTableCell API for how this is used.
	private final class Attr3SelectedStateCallback
		implements Callback<Integer, ObservableValue<Boolean>>
	{
		public ObservableValue<Boolean>	call(Integer v)
		{
			int			index = v.intValue();
			Movie			movie = table.getItems().get(index);

			return movie.isAnimatedProperty();
		}
	}

	//**********************************************************************
	// Inner Classes (Table Column Edit Handlers)
	//**********************************************************************

	// This EventHander processes edits in the title column.
	private final class TitleEditHandler
		implements EventHandler<TableColumn.CellEditEvent<Movie, String>>
	{
		public void	handle(TableColumn.CellEditEvent<Movie, String> t)
		{
			// Get the movie (from the *filtered* list) for the edited row
			int		index = t.getTablePosition().getRow();
			Movie		movie = table.getItems().get(index);

			// Set its title to the new value that was entered
			movie.setTitle(t.getNewValue());
		}
	}

	// No EventHander implemented, since the image column isn't editable.
	//private final class ImageEditHandler
	//{
	//}

	// This EventHander processes edits in the averageReviewScore column.
	private final class Attr1EditHandler
		implements EventHandler<TableColumn.CellEditEvent<Movie, Double>>
	{
		public void	handle(TableColumn.CellEditEvent<Movie, Double> t)
		{
			// Get the movie (from the *filtered* list) for the edited row
			int		index = t.getTablePosition().getRow();
			Movie		movie = table.getItems().get(index);

			// Set the score to the new value that was entered
			double		value = t.getNewValue();

			// Check range and apply rounding
			// (This would be automatic if editing were in a Spinner, since
			// we would set it up with the desired range and step amount.)
			value = 0.1 * Math.floor(value * 10.0);
			value = ((value < 0.0) ? 0.0 : value);
			value = ((value > 10.0) ? 10.0 : value);

			movie.setAverageReviewScore(value);
		}
	}

	// This EventHander processes edits in the rating column.
	private final class Attr2EditHandler
		implements EventHandler<TableColumn.CellEditEvent<Movie, String>>
	{
		public void	handle(TableColumn.CellEditEvent<Movie, String> t)
		{
			// Get the movie (from the *filtered* list) for the edited row
			int		index = t.getTablePosition().getRow();
			Movie		movie = table.getItems().get(index);

			// Set its rating to the new value that was entered
			movie.setRating(t.getNewValue());
		}
	}

	// This EventHander processes edits in the isAnimated column.
	private final class Attr3EditHandler
		implements EventHandler<TableColumn.CellEditEvent<Movie, Boolean>>
	{
		public void	handle(TableColumn.CellEditEvent<Movie, Boolean> t)
		{
			// Get the movie (from the *filtered* list) for the edited row
			int		index = t.getTablePosition().getRow();
			Movie		movie = table.getItems().get(index);

			// Set its rating to the new value that was entered
			movie.setIsAnimated(t.getNewValue());
		}
	}
}

//******************************************************************************
