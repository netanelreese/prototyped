//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Fri Feb 14 12:13:49 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Overhauled for Sp2020 PrototypeB.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb.pane;

//import java.lang.*;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import edu.ou.cs.hci.assignment.prototypeb.*;
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
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 280;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height

	private static final Insets	PADDING =
		new Insets(10.0, 10.0, 10.0, 10.0);

	private static final Insets	PADDING_SMALL =
		new Insets(2.0, 2.0, 2.0, 2.0);

	//**********************************************************************
	// Private Class Members (Styling)
	//**********************************************************************

	private static final Font		FONT_LABEL =
		Font.font("SansSerif", FontWeight.BOLD, 12.0);

	private static final Border	BORDER = new Border(
		new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID,
						 CornerRadii.EMPTY, BorderStroke.THIN));

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Data
	private final List<List<String>>	mdata;		// Movie attributes
	private final List<String>			gdata;		// Genre strings
	private final List<String>			rdata;		// Rating strings

	// Collection
	private final List<Movie>			movies;	// Movie objects

	// Layout (widgets in same order as model)
	private TextField				cTitle;
	private TextField				cImageFile;
	private Spinner<Integer>		cYear;
	private ComboBox<String>		cRating;
	private Slider					cRuntime;

	private CheckBox				cAwardPicture;
	private CheckBox				cAwardDirecting;
	private CheckBox				cAwardCinematography;
	private CheckBox				cAwardActing;

	private Spinner<Double>		cAverageReviewScore;
	private Spinner<Integer>		cNumberOfReviews;
	private ArrayList<CheckBox>	cGenres;

	private TextField				cDirector;
	private CheckBox				cIsAnimated;
	private CheckBox				cIsColor;
	private TextArea				cSummary;
	private TextArea				cComments;

	// Layout (extras widgets for movie poster image)
	private ImageView				cImageView;
	private Button					cImageButton;

	// Support
	private boolean				ignoreRangeEvents;

	// Handlers
	private final ActionHandler	actionHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public EditorPane(Controller controller)
	{
		super(controller, NAME, HINT);

		// Load data sets from hardcoded file locations
		mdata = Resources.getCSVData("data/movies.csv");
		gdata = Resources.getLines("data/genres.txt");
		rdata = Resources.getLines("data/ratings.txt");

		// Convert the raw movie data into movie objects
		movies = new ArrayList<Movie>();

		for (List<String> item : mdata)
			movies.add(new Movie(item));

		// Create a listener for various widgets that emit ActionEvents
		actionHandler = new ActionHandler();

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
		cTitle.setText((String)controller.get("movie.title"));
		cImageFile.setText((String)controller.get("movie.imageFile"));
		cYear.getValueFactory().setValue((Integer)controller.get("movie.year"));
		cRating.getSelectionModel().select((Integer)controller.get("movie.rating"));
		cRuntime.setValue((Integer)controller.get("movie.runtime"));

		cAwardPicture.setSelected((Boolean)controller.get("movie.award.picture"));
		cAwardDirecting.setSelected((Boolean)controller.get("movie.award.directing"));
		cAwardCinematography.setSelected((Boolean)controller.get("movie.award.cinematography"));
		cAwardActing.setSelected((Boolean)controller.get("movie.award.acting"));

		cAverageReviewScore.getValueFactory().setValue((Double)controller.get("movie.averageReviewScore"));
		cNumberOfReviews.getValueFactory().setValue((Integer)controller.get("movie.numberOfReviews"));

		// Map bit positions to genre hits/misses
		int	genre = (Integer)controller.get("movie.genre");
		int	n = 0;

		for (CheckBox c : cGenres)
		{
			c.setSelected((genre & (1 << n)) != 0);
			n++;
		}

		cDirector.setText((String)controller.get("movie.director"));
		cIsAnimated.setSelected((Boolean)controller.get("movie.isAnimated"));
		cIsColor.setSelected((Boolean)controller.get("movie.isColor"));

		cSummary.setText((String)controller.get("movie.summary"));
		cComments.setText((String)controller.get("movie.comments"));

		try
		{
			cImageView.setImage(new Image(
				FX_ICON + (String)controller.get("movie.imageFile"),
				W, H, false, true));
		}
		catch (Exception ex)
		{
			cImageView.setImage(null);
		}
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		cTitle.setOnAction(null);
		cImageFile.setOnAction(null);
		cYear.valueProperty().removeListener(this::changeInteger);
		cRating.getSelectionModel().selectedItemProperty().removeListener(
														this::changeItem);
		cRuntime.valueProperty().removeListener(this::changeDecimal);

		cAwardPicture.setOnAction(null);
		cAwardDirecting.setOnAction(null);
		cAwardCinematography.setOnAction(null);
		cAwardActing.setOnAction(null);

		cAverageReviewScore.valueProperty().removeListener(this::changeDecimal);
		cNumberOfReviews.valueProperty().removeListener(this::changeInteger);

		for (CheckBox c : cGenres)
			c.setOnAction(null);

		cDirector.setOnAction(null);
		cIsAnimated.setOnAction(null);
		cIsColor.setOnAction(null);

		cSummary.textProperty().removeListener(this::changeComment);
		cSummary.anchorProperty().removeListener(this::changeRange);
		cSummary.caretPositionProperty().removeListener(this::changeRange);

		cComments.textProperty().removeListener(this::changeComment);
		cComments.anchorProperty().removeListener(this::changeRange);
		cComments.caretPositionProperty().removeListener(this::changeRange);

		cImageButton.setOnAction(null);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		//System.out.println("update " + key + " to " + value);

		// ----- Updates for changes to the CollectionPane properties. -----

		if ("selectedMovieIndex".equals(key))
			updateMoviePropertiesInModel();

		// ----- Updates for changes to the EditorPane properties. -----

		// Apply updates that require only simple adjustment of a widget
		if ("movie.title".equals(key))
			cTitle.setText((String)value);
		else if ("movie.year".equals(key))
			cYear.getValueFactory().setValue((Integer)value);
		else if ("movie.rating".equals(key))
			cRating.getSelectionModel().select((Integer)value);
		else if ("movie.runtime".equals(key))
			cRuntime.setValue((Integer)value);
		else if ("movie.award.picture".equals(key))
			cAwardPicture.setSelected((Boolean)value);
		else if ("movie.award.directing".equals(key))
			cAwardDirecting.setSelected((Boolean)value);
		else if ("movie.award.cinematography".equals(key))
			cAwardCinematography.setSelected((Boolean)value);
		else if ("movie.award.acting".equals(key))
			cAwardActing.setSelected((Boolean)value);
		else if ("movie.averageReviewScore".equals(key))
			cAverageReviewScore.getValueFactory().setValue((Double)value);
		else if ("movie.numberOfReviews".equals(key))
			cNumberOfReviews.getValueFactory().setValue((Integer)value);
		else if ("movie.director".equals(key))
			cDirector.setText((String)value);
		else if ("movie.isAnimated".equals(key))
			cIsAnimated.setSelected((Boolean)value);
		else if ("movie.isColor".equals(key))
			cIsColor.setSelected((Boolean)value);

		// Apply updates that require more elaborate adjusting
		if ("movie.imageFile".equals(key))
		{
			cImageFile.setText((String)value);

			try
			{
				cImageView.setImage(new Image(FX_ICON + (String)value,
											  W, H, false, true));
			}
			catch (Exception ex)
			{
				cImageView.setImage(null);
			}
		}
		else if ("movie.genre".equals(key))
		{
			// Map bit positions to genre hits/misses
			int	genre = (Integer)value;
			int	n = 0;

			for (CheckBox c : cGenres)
			{
				c.setSelected((genre & (1 << n)) != 0);
				n++;
			}
		}

		// Apply updates that involve text+anchor+caret handling in TextAreas
		if ("movie.summary".equals(key) ||
			"movie.summary.anchor".equals(key) ||
			"movie.summary.caret".equals(key))
		{
			String	text = (String)controller.get("movie.summary");
			int	anchor = (Integer)controller.get("movie.summary.anchor");
			int	caret = (Integer)controller.get("movie.summary.caret");

			ignoreRangeEvents = true;
			cSummary.setText(text);
			cSummary.selectRange(anchor, caret);
			ignoreRangeEvents = false;
		}

		if ("movie.comments".equals(key) ||
			"movie.comments.anchor".equals(key) ||
			"movie.comments.caret".equals(key))
		{
			String	text = (String)controller.get("movie.comments");
			int	anchor = (Integer)controller.get("movie.comments.anchor");
			int	caret = (Integer)controller.get("movie.comments.caret");

			ignoreRangeEvents = true;
			cComments.setText(text);
			cComments.selectRange(anchor, caret);
			ignoreRangeEvents = false;
		}
	}

	// Update all movie properties when selectedMovieIndex changes in the model.
	private void	updateMoviePropertiesInModel()
	{
		// Get the currently selected movie
		int	index = (Integer)controller.get("selectedMovieIndex");
		Movie	movie = movies.get(index);

		// Update the title and image properties in the model
		controller.set("movie.title", movie.getTitle());
		controller.set("movie.imageFile", movie.getImage());

		// TODO #3: Update the other 13 properties, summary, and comments below
		//controller.set("movie.foo", movie.getFoo());	// <-- 13 like this...

		// Update the text+anchor+caret properties used by TextAreas
		controller.set("movie.summary", "");			// <-- then for summary
		controller.set("movie.summary.anchor", 0);
		controller.set("movie.summary.caret", 0);

		controller.set("movie.comments", "");			// <-- and for comments
		controller.set("movie.comments.anchor", 0);
		controller.set("movie.comments.caret", 0);
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		buildWidgets();

		return buildLayout();
	}

	// Create and set up widgets, in same order as attributes in model
	private void	buildWidgets()
	{
		cTitle = new TextField();
		cTitle.setPrefColumnCount(40);
		cTitle.setOnAction(actionHandler);

		cImageFile = new TextField();
		cImageFile.setPrefColumnCount(18);
		cImageFile.setPromptText("Location of image file");
		cImageFile.setOnAction(actionHandler);

		cYear = new Spinner<Integer>(1900, 2040, 0, 1);
		cYear.setEditable(true);
		cYear.getEditor().setPrefColumnCount(5);
		cYear.valueProperty().addListener(this::changeInteger);

		cRating = new ComboBox<String>();
		cRating.getItems().addAll(rdata);
		cRating.setEditable(false);
		cRating.setVisibleRowCount(5);
		cRating.getSelectionModel().selectedItemProperty().addListener(
														this::changeItem);

		cRuntime = new Slider(0.0, 360.0, 120.0);
		cRuntime.setOrientation(Orientation.HORIZONTAL);
		cRuntime.setMajorTickUnit(120.0);
		cRuntime.setMinorTickCount(120);
		cRuntime.setShowTickLabels(true);
		cRuntime.setShowTickMarks(false);
		cRuntime.setSnapToTicks(true);
		cRuntime.valueProperty().addListener(this::changeDecimal);

		cAwardPicture = buildCheckBox("Picture");
		cAwardDirecting = buildCheckBox("Directing");
		cAwardCinematography = buildCheckBox("Cinematography");
		cAwardActing = buildCheckBox("Acting");

		cAverageReviewScore = new Spinner<Double>(0.0, 10.0, 0.0, 0.1);
		cAverageReviewScore.setEditable(true);
		cAverageReviewScore.getEditor().setPrefColumnCount(4);
		cAverageReviewScore.valueProperty().addListener(this::changeDecimal);

		cNumberOfReviews = new Spinner<Integer>(0, 9999999, 0, 1);
		cNumberOfReviews.setEditable(true);
		cNumberOfReviews.getEditor().setPrefColumnCount(7);
		cNumberOfReviews.valueProperty().addListener(this::changeInteger);

		cGenres = new ArrayList<CheckBox>();

		for (String genre : gdata)
			cGenres.add(buildCheckBox(genre));

		cDirector = new TextField();
		cDirector.setPrefColumnCount(40);
		cDirector.setOnAction(actionHandler);

		cIsAnimated = buildCheckBox("Animated");
		cIsColor = buildCheckBox("Color");

		// Note: Buggy! Currently moves the caret to the start every time.
		cSummary = new TextArea();
		cSummary.setMinSize(200, 0);
		cSummary.setPrefRowCount(10);
		cSummary.setWrapText(true);
		cSummary.textProperty().addListener(this::changeComment);
		cSummary.anchorProperty().addListener(this::changeRange);
		cSummary.caretPositionProperty().addListener(this::changeRange);

		// Note: Buggy! Currently moves the caret to the start every time.
		cComments = new TextArea();
		cComments.setPrefColumnCount(72);
		cComments.setWrapText(true);
		cComments.textProperty().addListener(this::changeComment);
		cComments.anchorProperty().addListener(this::changeRange);
		cComments.caretPositionProperty().addListener(this::changeRange);

		cImageView = new ImageView();

		cImageButton = new Button("Choose...");
		cImageButton.setOnAction(actionHandler);
	}

	// Layout widgets, more or less from leaf to root in layout hierarchy
	// Create and layout labels above many of the widgets, and add padding
	// at several levels in the hierarchy to fine tune positioning. There
	// are still a few minor glitches when resizing the window.
	private Pane	buildLayout()
	{
		VBox	hTitle = buildLabeledVBox("Title", cTitle);
		VBox	hYear = buildLabeledVBox("Year", cYear);
		VBox	hRating = buildLabeledVBox("Rating", cRating);
		VBox	hRuntime = buildLabeledVBox("Runtime", cRuntime);
		VBox	hDirector = buildLabeledVBox("Director", cDirector);
		VBox	hAvg = buildLabeledVBox("Average Score", cAverageReviewScore);
		VBox	hNum = buildLabeledVBox("Number of Reviews", cNumberOfReviews);

		// Layout of summary+isAnimated+isColor is somewhat complicated
		Label		label = new Label("Summary");
		FlowPane	flow = new FlowPane(Orientation.HORIZONTAL, 0.0, 0.0,
										cIsAnimated, cIsColor);
		VBox		hSummary = new VBox(new HBox(label, flow), cSummary);

		flow.setAlignment(Pos.CENTER_RIGHT);
		label.setFont(FONT_LABEL);
		label.setMinSize(60, 0);
		hSummary.setPadding(PADDING);

		VBox	hComments = buildLabeledVBox("Comments", cComments);
		VBox	pfield = new VBox(hTitle, hDirector, hSummary);
		VBox	paward = buildLabeledVBox("Awards", new VBox(cAwardPicture,
					cAwardDirecting, cAwardCinematography, cAwardActing));
		VBox	paar = new VBox(paward, hAvg, hNum);
		VBox	pg = buildLabeledVBox("Genres",
					new VBox(cGenres.toArray(new CheckBox[cGenres.size()])));

		HBox		paarg = new HBox(paar, pg);
		HBox		pyrr = new HBox(hYear, hRating, hRuntime);
		BorderPane	ppicks = new BorderPane(paarg, pyrr, null, null, null);
		BorderPane	pbunch = new BorderPane(pfield, null, ppicks, null, null);
		VBox		pentry = new VBox(pbunch, hComments);
		BorderPane	hImageFile = new BorderPane(cImageFile);
		BorderPane	hImageButton = new BorderPane(cImageButton);
		HBox		pipick = new HBox(hImageFile, hImageButton);
		BorderPane	pimage = new BorderPane(cImageView);
		BorderPane	poster = new BorderPane(pimage, null, null, pipick, null);
		BorderPane	ptotal = new BorderPane(pentry, null, poster, null, null);

		hImageFile.setPadding(new Insets(4.0, 2.0, 0.0, 0.0));
		hImageButton.setPadding(new Insets(4.0, 0.0, 0.0, 2.0));
		pimage.setBorder(BORDER);
		poster.setPadding(PADDING);

		return ptotal;
	}

	// Builds a padded Checkbox with the text.
	private CheckBox	buildCheckBox(String text)
	{
		CheckBox	c = new CheckBox(text);

		c.setPadding(PADDING_SMALL);
		c.setOnAction(actionHandler);

		return c;
	}

	// Builds a padded VBox with the text (as a label) above the child.
	private VBox	buildLabeledVBox(String text, Node child)
	{
		Label	label = new Label(text);
		VBox	vbox = new VBox(label, child);

		label.setFont(FONT_LABEL);
		vbox.setPadding(PADDING);

		return vbox;
	}

	//**********************************************************************
	// Private Methods (Property Change Handlers)
	//**********************************************************************

	// For ComboBox
	private void	changeItem(ObservableValue<? extends String> observable,
							   String oldValue, String newValue)
	{
		if (observable == cRating.getSelectionModel().selectedItemProperty())
			controller.set("movie.rating", rdata.indexOf(newValue));
	}

	// For Slider, Spinner<Double>
	private void	changeDecimal(ObservableValue<? extends Number> observable,
								  Number oldValue, Number newValue)
	{
		if (observable == cRuntime.valueProperty())
			controller.set("movie.runtime", (int)Math.floor((Double)newValue));
		else if (observable == cAverageReviewScore.valueProperty())
			controller.set("movie.averageReviewScore", newValue);
	}

	// For Spinner<Integer>
	private void	changeInteger(ObservableValue<? extends Number> observable,
								  Number oldValue, Number newValue)
	{
		if (observable == cYear.valueProperty())
			controller.set("movie.year", newValue);
		else if (observable == cNumberOfReviews.valueProperty())
			controller.set("movie.numberOfReviews", newValue);
	}

	// For TextArea (changes to the text itself)
	private void	changeComment(ObservableValue<? extends String> observable,
								  String oldValue, String newValue)
	{
		if (observable == cSummary.textProperty())
			controller.set("movie.summary", newValue);
		else if (observable == cComments.textProperty())
			controller.set("movie.comments", newValue);
	}

	// For TextArea (changes to range = caret position + selection anchor)
	private void	changeRange(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		// Ignore changes to TextArea range that come indirectly from the model.
		if (ignoreRangeEvents)
			return;

		// Handle changes to TextArea range that come directly from the user.
		if (observable == cSummary.caretPositionProperty())
			controller.set("movie.summary.caret", newValue);
		else if (observable == cComments.caretPositionProperty())
			controller.set("movie.comments.caret", newValue);

		if (observable == cSummary.anchorProperty())
			controller.set("movie.summary.anchor", newValue);
		else if (observable == cComments.anchorProperty())
			controller.set("movie.comments.anchor", newValue);
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************

	// For Button, CheckBox, TextField
	private final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object	source = e.getSource();

			if (source == cTitle)
				controller.set("movie.title", cTitle.getText());
			else if (source == cImageFile)
				controller.set("movie.imageFile", cImageFile.getText());
			else if (source == cAwardPicture)
				controller.set("movie.award.picture",
							   cAwardPicture.isSelected());
			else if (source == cAwardDirecting)
				controller.set("movie.award.directing",
							   cAwardDirecting.isSelected());
			else if (source == cAwardCinematography)
				controller.set("movie.award.cinematography",
							   cAwardCinematography.isSelected());
			else if (source == cAwardActing)
				controller.set("movie.award.acting",
							   cAwardActing.isSelected());
			else if (source == cDirector)
				controller.set("movie.director", cDirector.getText());
			else if (source == cIsAnimated)
				controller.set("movie.isAnimated", cIsAnimated.isSelected());
			else if (source == cIsColor)
				controller.set("movie.isColor", cIsColor.isSelected());
			else if (source == cImageButton)
				controller.trigger("choosing new image file for movie");
			else
				handleGenres(source);
		}

		private void	handleGenres(Object source)
		{
			int		genre = (Integer)controller.get("movie.genre");
			int		n = 0;
			boolean	changed = false;

			for (CheckBox c : cGenres)
			{
				if (source == c)
				{
					genre = (c.isSelected() ? (genre+(1<<n)) : (genre-(1<<n)));
					changed = true;
				}

				n++;
			}

			if (changed)
				controller.set("movie.genre", genre);
		}
	}
}

//******************************************************************************
