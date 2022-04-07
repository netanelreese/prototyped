//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Fri Feb 14 12:15:51 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Overhauled for new PrototypeB in Spring 2020.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb.pane;

//import java.lang.*;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Callback;
import javafx.util.converter.*;
import edu.ou.cs.hci.assignment.prototypeb.*;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>CollectionPane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
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

	private static final Insets	PADDING =
		new Insets(40.0, 20.0, 40.0, 20.0);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Data
	private final List<String>			gdata;		// Genre strings
	private final List<String>			rdata;		// Rating strings
	private final List<List<String>>	mdata;		// Movie attributes

	// Collection
	private final List<Movie>			movies;	// Movie objects

	// Layout
	private TableView<Movie>			table;
	private SelectionModel<Movie>		smodel;

	// Part of TODO #9: Add members for the widgets in your summary design here.
	//private Label						summaryTitle;
	//private ImageView					summaryImage;
	//...

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public CollectionPane(Controller controller)
	{
		super(controller, NAME, HINT);

		// Load data sets from hardcoded file locations
		gdata = Resources.getLines("data/genres.txt");
		rdata = Resources.getLines("data/ratings.txt");
		mdata = Resources.getCSVData("data/movies.csv");

		// Convert the raw movie data into movie objects
		movies = new ArrayList<Movie>();

		for (List<String> item : mdata)
			movies.add(new Movie(item));

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
		smodel.selectedIndexProperty().addListener(this::changeIndex);

		int	index = (Integer)controller.get("selectedMovieIndex");

		smodel.select(index);

		// Part of TODO #9: Initialize your summary widgets here, to show the
		// movie's attributes (if one is selected) or default values (if not).

		// Hint: You can use the provided Movie.getImageAsImage() method with
		// FX_ICON as the path to load and size an Image object from one of the
		// files included in edu/ou/cs/hci/resources/example/fx/icon.
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		smodel.selectedIndexProperty().removeListener(this::changeIndex);

		// Part of TODO #9: Terminate your summary widgets here, as needed.
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		if ("selectedMovieIndex".equals(key))
		{
			int	index = (Integer)value;
			Movie	movie = movies.get(index);

			smodel.select(index);

			// Part of TODO #9: Update your summary widgets here, to show the
			// movie attributes (if one is selected) or default values (if not).
		}
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		Node	bregion = buildTableView();
		Node	tregion = buildCoverFlow();
		Node	lregion = buildLaterView();
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

		// TODO #8: Uncomment these to add columns for your three attributes.
		//table.getColumns().add(buildAttr1Column());
		//table.getColumns().add(buildAttr2Column());
		//table.getColumns().add(buildAttr3Column());

		// Put the movies into an ObservableList to use as the table model
		table.setItems(FXCollections.observableArrayList(movies));

		return table;
	}

	private Node	buildCoverFlow()
	{
		Label	label = new Label("this space reserved for cover flow (later)");

		label.setPadding(PADDING);

		return label;
	}

	private Node	buildLaterView()
	{
		Label	label = new Label("saving for later");

		label.setPadding(PADDING);

		return label;
	}

	// TODO #9: Build the layout for your movie summary here, showing the title,
	// image, and your three attributes. For any widgets you use, add members
	// and/or code to initialize(), terminate(), and update() above, as needed.
	// Keep in mind that the movie summary is meant for display, not editing.
	private Node	buildMovieView()
	{
		// The label is just a placeholder. Replace it with your own widgets!
		Label	label = new Label("replace me now!");

		label.setPadding(PADDING);

		return label;
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

	// TODO #7: Complete the TableColumn methods for your three attributes.
	// You must adapt the code to the column's attribute type in each case.

	//private TableColumn<Movie, String>	buildAttr1Column()
	//{
	//}

	//private TableColumn<Movie, String>	buildAttr2Column()
	//{
	//}

	//private TableColumn<Movie, String>	buildAttr3Column()
	//{
	//}

	//**********************************************************************
	// Private Methods (Change Handlers)
	//**********************************************************************

	private void	changeIndex(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		int	index = (Integer)newValue;

		controller.set("selectedMovieIndex", index);
	}

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

	// TODO #6: Complete the CellFactory classes for your three attributes.
	// You must adapt the code to the column's attribute type in each case.

	// private final class Attr1CellFactory
	// {
	// }

	// private final class Attr2CellFactory
	// {
	// }

	// private final class Attr3CellFactory
	// {
	// }

	//**********************************************************************
	// Inner Classes (Cells)
	//**********************************************************************

	// Each Cell determines the contents of one row/column intersection in the
	// table. The code for each one maps its attribute object into text and/or
	// graphic in different ways.

	// To modify the styling of cells, use methods in the ancestor classes of
	// javafx.scene.control.TableCell, especially javafx.scene.control.Labeled
	// and javafx.scene.layout.Region. (You can also edit View.css. It currently
	// sets background-color and text-fill properties for entire rows of cells.)

	// To make a cell editable...although only shallowly at this point:
	// Extend a javafx.scene.control.cell.*TableCell class to allow editing.
	// Match a javafx.util.converter.*StringConverter to each attribute type.

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

	// TODO #5: Complete the Cell classes for your three attributes.
	// You must adapt the code to the column's attribute type in each case.
	// Allow editing (shallowly) in at least one of the three columns.

	// private final class Attr1Cell
	// {
	// }

	// private final class Attr2Cell
	// {
	// }

	// private final class Attr3Cell
	// {
	// }

	//**********************************************************************
	// Inner Classes (Table Column Edit Handlers)
	//**********************************************************************

	// This EventHander processes edits in the title column.
	private final class TitleEditHandler
		implements EventHandler<TableColumn.CellEditEvent<Movie, String>>
	{
		public void	handle(TableColumn.CellEditEvent<Movie, String> t)
		{
			// Get the movie for the row that was edited
			int	index = t.getTablePosition().getRow();
			Movie	movie = movies.get(index);

			// Set its title to the new value that was entered
			movie.setTitle(t.getNewValue());
		}
	}

	// No EventHander implemented, since the image column isn't editable.
	//private final class ImageEditHandler
	//{
	//}

	// TODO #4: Add an EventHandler class for each of your editable columns.
	// You must adapt the code to the column's attribute type in each case.
	// Allow editing (shallowly) in at least one of the three columns.

	// private final class Attr1EditHandler
	// {
	// }

	// private final class Attr2EditHandler
	// {
	// }

	// private final class Attr3EditHandler
	// {
	// }
}

//******************************************************************************
