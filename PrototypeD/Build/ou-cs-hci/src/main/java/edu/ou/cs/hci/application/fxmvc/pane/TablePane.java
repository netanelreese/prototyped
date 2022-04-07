//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Feb 24 19:56:48 2019 by Chris Weaver
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
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import edu.ou.cs.hci.application.fxmvc.Controller;

//******************************************************************************

/**
 * The <CODE>TablePane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class TablePane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Table";
	private static final String	HINT = "Table Layout for Images";

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 32;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height

	private static final Insets	PADDING =
		new Insets(40.0, 20.0, 40.0, 20.0);

	//**********************************************************************
	// Private Class Members (Effects)
	//**********************************************************************

	private static final Glow			GLOW =
			new Glow(0.3);

	private static final ColorAdjust	COLOR_ADJUST =
		new ColorAdjust(0.0, 0.0, -0.25, 0.0);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Data
	private List<List<String>>			data;

	// Layout
	private BorderPane					base;
	private TableView<Record>			table;
	private SelectionModel<Record>		smodel;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public TablePane(Controller controller)
	{
		super(controller, NAME, HINT);

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

		int	index = (Integer)controller.get("itemIndex");

		smodel.select(index);
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		smodel.selectedIndexProperty().removeListener(this::changeIndex);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		if ("itemIndex".equals(key))
		{
			int	index = (Integer)value;

			smodel.select(index);
		}
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		data = loadFXData("list-movies.txt");

		// Transfer the data into an ObservableList to use as the table model
		ObservableList<Record>	records = FXCollections.observableArrayList();

		for (List<String> item : data)
			records.add(new Record(item.get(0), item.get(1)));

		// Create a column for movie titles
		TableColumn<Record, String>	nameColumn =
			new TableColumn<Record, String>("Title");

		nameColumn.setEditable(false);
		nameColumn.setPrefWidth(250);
		nameColumn.setCellValueFactory(
			new PropertyValueFactory<Record, String>("name"));
		nameColumn.setCellFactory(new NameCellFactory());

		// Create a column for movie posters
		TableColumn<Record, String>	iconColumn =
			new TableColumn<Record, String>("Poster");

		iconColumn.setEditable(false);
		iconColumn.setPrefWidth(W + 16.0);
		iconColumn.setCellValueFactory(
			new PropertyValueFactory<Record, String>("icon"));
		iconColumn.setCellFactory(new IconCellFactory());

		// Create the table from the columns
		table = new TableView<Record>();
		smodel = table.getSelectionModel();

		table.setEditable(false);
		table.setPlaceholder(new Text("No Data!"));
		//table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		table.getColumns().add(nameColumn);
		table.getColumns().add(iconColumn);

		table.setItems(records);

		// Create a split pane to share space between the cover pane and table
		SplitPane	splitPane = new SplitPane();

		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.1);	// Put divider at 50% T-to-B

		Label	tlabel = new Label("this space intentionally left blank");
		Label	llabel = new Label("put accordion here");
		Label	rlabel = new Label("put movieinfo here");

		tlabel.setPadding(PADDING);
		llabel.setPadding(PADDING);
		rlabel.setPadding(PADDING);

		splitPane.getItems().add(tlabel);
		splitPane.getItems().add(table);

		StackPane	lpane = new StackPane(llabel);
		StackPane	rpane = new StackPane(rlabel);

		//base = new BorderPane(table, null, rpane, null, lpane);
		base = new BorderPane(splitPane, null, rpane, null, lpane);

		return base;
	}

	//**********************************************************************
	// Private Methods (Change Handlers)
	//**********************************************************************

	private void	changeIndex(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		int	index = (Integer)newValue;

		controller.set("itemIndex", index);
	}

	//**********************************************************************
	// Inner Classes (Record Object)
	//**********************************************************************

	public static final class Record
	{
		private final SimpleStringProperty	name;
		private final SimpleStringProperty	icon;

		public Record(String name, String icon)
		{
			this.name = new SimpleStringProperty(name);
			this.icon = new SimpleStringProperty(icon);
		}

		public String	getName()
		{
			return name.get();
		}

		public void	setName(String v)
		{
			this.name.set(v);
		}

		public String	getIcon()
		{
			return icon.get();
		}

		public void	setIcon(String v)
		{
			this.icon.set(v);
		}
	}

	//**********************************************************************
	// Inner Classes (Cell Factories)
	//**********************************************************************

	private static final class NameCellFactory
		implements Callback<TableColumn<Record, String>,
							TableCell<Record, String>>
	{
		public TableCell<Record, String>	call(TableColumn<Record, String> v)
		{
			return new NameCell();
		}
	}

	private static final class IconCellFactory
		implements Callback<TableColumn<Record, String>,
							TableCell<Record, String>>
	{
		public TableCell<Record, String>	call(TableColumn<Record, String> v)
		{
			return new IconCell();
		}
	}

	private static final class NameCell extends TableCell<Record, String>
	{
		protected void	updateItem(String item, boolean empty)
		{
			super.updateItem(item, empty);			// Prepare for setup

			if (empty || (item == null))			// Handle special cases
			{
				setText(null);
				setGraphic(null);

				return;
			}

			setText(item);
			setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
		}
	}

	private static final class IconCell extends TableCell<Record, String>
	{
		protected void	updateItem(String item, boolean empty)
		{
			super.updateItem(item, empty);			// Prepare for setup

			if (empty || (item == null))			// Handle special cases
			{
				setText(null);
				setGraphic(null);

				return;
			}

			ImageView	icon = createFXIcon(item, W, H);

			setText(null);
			setGraphic(icon);
		}
	}
}

//******************************************************************************
