//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sat Feb 23 19:38:23 2019 by Chris Weaver
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
import javafx.animation.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import edu.ou.cs.hci.application.fxmvc.Controller;

//******************************************************************************

/**
 * The <CODE>FloatPane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class FloatPane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Float";
	private static final String	HINT = "Float Layout for Images";

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 60;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height
	private static final double	INSET = (W + 12.0) * 2.75;
	private static final double	OFFSET = 32.0;	// Compensate for tab

	//**********************************************************************
	// Private Class Members (Animation)
	//**********************************************************************

	private static final double	FRAMERATE = 40.0;
	private static final Duration	DURATION = Duration.seconds(5.0);

	//**********************************************************************
	// Private Class Members (Effects)
	//**********************************************************************

	private static final Color		WHITE_75 = Color.web("white", 0.75);
	private static final Color		WHITE_25 = Color.web("white", 0.25);

	private static final DropShadow		SHADOW =
		new DropShadow(10.0, 5.0, 5.0, Color.web("#c0c000"));

	private static final LinearGradient	GRADIENT =
		new LinearGradient(0.0, 0.0, 0.0, 1.0, true,
						   CycleMethod.NO_CYCLE,
						   new Stop(0.00, Color.web("#606040", 1.00)),
						   new Stop(1.00, Color.web("#000000", 1.00)));

	private static final Reflection		REFLECTION =
		new Reflection(10.0, 0.25, 0.50, 0.0);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Data
	private List<List<String>>		data;

	// Layout
	private StackPane				base;
	private Rectangle				fill;
	private Group					all;

	// Animation
	private Animation				animation;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public FloatPane(Controller controller)
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
		Scene	scene = fill.getScene();

		// Keep the background the same width as the entire scene
		fill.setWidth(scene.getWidth());
		fill.setHeight(scene.getHeight() - OFFSET);

		scene.widthProperty().addListener(this::changeWidth);
		scene.heightProperty().addListener(this::changeHeight);

		updateItemEffects();

		animation = createAnimation();
		animation.play();
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		animation.stop();

		Scene	scene = fill.getScene();

		scene.widthProperty().removeListener(this::changeWidth);
		scene.heightProperty().removeListener(this::changeHeight);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		if ("itemIndex".equals(key))
			updateItemEffects();
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		data = loadFXData("list-movies.txt");

		all = new Group();		// Small labels, moving back and forth randomly

		for (List<String> item : data)
		{
			// Create a small, randomly-positioned label for each item
			ImageView		icon = createFXIcon(item.get(1), W, H);
			Label			label = new Label(item.get(0), icon);

			label.setTextFill(Color.WHITE);
			label.setContentDisplay(ContentDisplay.TOP);
			label.setPrefWidth(W);

			label.setCursor(Cursor.HAND);

			// Register a method to handle mouse clicks on each label
			label.addEventHandler(MouseEvent.MOUSE_CLICKED,
								  this::handleMouseClicked);

			// Add a circular halo around each item
			Circle	shape = new Circle(W + 12.0);

			shape.setFill(WHITE_25);

			shape.setStroke(WHITE_75);
			shape.setStrokeType(StrokeType.OUTSIDE);
			shape.setStrokeWidth(2);
			shape.setEffect(REFLECTION);

			// Put the border and label together, and add it to the set of items
			StackPane	one = new StackPane(shape, label);

			all.getChildren().add(one);
		}

		// Add a top-to-bottom background gradient
		fill = new Rectangle();
		fill.setFill(GRADIENT);

		// Stack the background, spotlight, titles, and items
		base = new StackPane(fill, all);

		return base;
	}

	private void	updateItemEffects()
	{
		int	index = (Integer)controller.get("itemIndex");
		int	i = 0;

		// For each item, calculate effects for its node
		for (Node one : all.getChildren())
		{
			Label	label = (Label)getDescendant(one, 1);

			if (i == index)
				one.setEffect(SHADOW);
			else
				one.setEffect(null);

			i++;
		}
	}

	//**********************************************************************
	// Private Methods (Animation)
	//**********************************************************************

	private Animation	createAnimation()
	{
		Timeline		timeline = new Timeline(FRAMERATE);
		List<KeyFrame>	kfs = timeline.getKeyFrames();

		for (Node one : all.getChildren())
		{
			// Calculate random start and end points inside the pane
			double	x1 = W * 0.5 + Math.random() * (fill.getWidth() - INSET);
			double	y1 = H * 0.5 + Math.random() * (fill.getHeight() - INSET);
			double	x2 = W * 0.5 + Math.random() * (fill.getWidth() - INSET);
			double	y2 = H * 0.5 + Math.random() * (fill.getHeight() - INSET);

			kfs.add(new KeyFrame(Duration.ZERO,		// Random start point
						new KeyValue(one.translateXProperty(), x1),
						new KeyValue(one.translateYProperty(), y1)));
			kfs.add(new KeyFrame(DURATION,				// Random end point
						new KeyValue(one.translateXProperty(), x2,
									 Interpolator.EASE_BOTH),
						new KeyValue(one.translateYProperty(), y2,
									 Interpolator.EASE_BOTH)));
		}

		timeline.setAutoReverse(true);					// Move back and forth
		timeline.setCycleCount(Animation.INDEFINITE);	// Repeat indefinitely

		return timeline;
	}

	//**********************************************************************
	// Private Methods (Change Handlers)
	//**********************************************************************

	// Whenever the scene resizes, update background and restart animation.
	private void	changeWidth(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		fill.setWidth((Double)newValue);

		animation.stop();					// Restart the animation, since
		animation = createAnimation();		// item movements depend on size
		animation.play();
	}

	// Whenever the scene resizes, update background and restart animation.
	private void	changeHeight(ObservableValue<? extends Number> observable,
								 Number oldValue, Number newValue)
	{
		fill.setHeight((Double)newValue - OFFSET);

		animation.stop();					// Restart the animation, since
		animation = createAnimation();		// item movements depend on size
		animation.play();
	}

	//**********************************************************************
	// Private Methods (Input Handlers)
	//**********************************************************************

	// Highlight the clicked item
	private void	handleMouseClicked(MouseEvent e)
	{
		Object		source = e.getSource();
		int		index = 0;

		// Find the index of the item that has the event source as its label
		for (Node one : all.getChildren())
		{
			Label	label = (Label)getDescendant(one, 1);

			if (label == source)					// Found it!
				break;

			index++;								// Keep going...
		}

		controller.set("itemIndex", index);

		e.consume();
	}
}

//******************************************************************************
