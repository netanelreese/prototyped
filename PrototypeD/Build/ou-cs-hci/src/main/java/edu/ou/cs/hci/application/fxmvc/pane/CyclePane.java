//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sat Feb 23 19:38:22 2019 by Chris Weaver
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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;
import edu.ou.cs.hci.application.fxmvc.Controller;

//******************************************************************************

/**
 * The <CODE>CyclePane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class CyclePane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Cycle";
	private static final String	HINT = "Cycle Layout for Images";

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 60;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height
	private static final double	S = 2.0;		// Core scaling factor
	private static final double	OFFSET = 32.0;	// Compensate for tab

	//**********************************************************************
	// Private Class Members (Animation)
	//**********************************************************************

	private static final double	FRAMERATE = 40.0;
	private static final Duration	DURATION = Duration.seconds(0.6);

	//**********************************************************************
	// Private Class Members (Effects)
	//**********************************************************************

	private static final Color		WHITE_75 = Color.web("white", 0.75);
	private static final Color		WHITE_25 = Color.web("white", 0.25);

	private static final Font		FONT =
		Font.font("Serif", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 18.0);

	private static final ColorAdjust		COLOR_ADJUST =
		new ColorAdjust(0.0, 0.0, -0.25, 0.0);

	private static final Glow				GLOW =
		new Glow(0.3);

	private static final LinearGradient	GRADIENT =
		new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
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
	private Group					core;
	private Group					all;

	// Effects
	private Light.Spot				spot;

	// Animation
	private Animation				animation;
	private SimpleDoubleProperty	angle;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public CyclePane(Controller controller)
	{
		super(controller, NAME, HINT);

		angle = new SimpleDoubleProperty();

		setBase(buildPane());
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// The controller calls this method when it adds a view.
	// Set up the nodes in the view with data accessed through the controller.
	public void	initialize()
	{
		Scene		scene = fill.getScene();

		// Keep the background the same width as the entire scene
		scene.widthProperty().addListener(this::changeWidth);
		scene.heightProperty().addListener(this::changeHeight);

		// Register a method to handle changes to the angle by the animation
		angle.addListener(this::changeAngle);

		// Set up initial background, spotlight, and items based on scene size
		double		w = scene.getWidth();
		double		h = scene.getHeight() - OFFSET;

		fill.setWidth(w);
		spot.setX(w * 0.5);
		spot.setPointsAtX(w * 0.5);

		fill.setHeight(h);
		spot.setY(h * 0.5);
		spot.setPointsAtY(OFFSET);

		updateItemEffects();
		updateLayout();
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		Scene	scene = fill.getScene();

		scene.widthProperty().removeListener(this::changeWidth);
		scene.heightProperty().removeListener(this::changeHeight);

		angle.removeListener(this::changeAngle);
	}

	// The controller calls this method whenever something changes in the model.
	// Update the nodes in the view to reflect the change.
	public void	update(String key, Object value)
	{
		if ("itemIndex".equals(key))
		{
			if (animation != null)					// Restart animation, since
				animation.stop();					// toAngle has changed.

			int	index = (Integer)value;
			int	n = all.getChildren().size();
			double	ninv = 1.0 / n;				// One-nth of the way around
			double	toAngle = index * ninv;		// New angle to go to

			animation = createAnimation(toAngle);	// Prepare to go there...
			animation.play();						// ...then go!
		}
	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	private Pane	buildPane()
	{
		data = loadFXData("list-movies.txt");

		all = new Group();		// Small labels, positioned around the ring
		core = new Group();	// Large labels, only one visible at a time

		for (List<String> item : data)
		{
			// Create a small, radially-positioned label for each item
			ImageView	icon = createFXIcon(item.get(1), W, H);
			Label		label = new Label(item.get(0), icon);

			label.setTextFill(Color.WHITE);
			label.setContentDisplay(ContentDisplay.TOP);
			label.setPrefWidth(W);

			label.setCursor(Cursor.HAND);

			// Register a method to handle mouse clicks on each label
			label.addEventHandler(MouseEvent.MOUSE_CLICKED,
								  this::handleMouseClicked);

			// Add a rounded rectangular border around each item
			Rectangle	shape = new Rectangle(W + 4.0, H + 24.0);

			shape.setArcWidth(4.0);
			shape.setArcHeight(4.0);

			shape.setFill(WHITE_25);

			shape.setStroke(WHITE_75);
			shape.setStrokeType(StrokeType.OUTSIDE);
			shape.setStrokeWidth(2.0);

			// Put the border and label together, and add it to the set of items
			StackPane	one = new StackPane(shape, label);

			all.getChildren().add(one);

			// Create a large, centrally-positioned label for each item
			ImageView	image = createFXIcon(item.get(1), W * S, H * S);

			image.setOpacity(0.75);
			image.setEffect(REFLECTION);

			// Create a large, centrally-positioned label for each item
			Label		title = new Label(item.get(0), image);

			title.setContentDisplay(ContentDisplay.TOP);
			title.setFont(FONT);
			title.setPrefWidth(W * S);
			title.setTextFill(Color.WHITE);
			title.setTextAlignment(TextAlignment.CENTER);
			title.setWrapText(true);

			// Add padding at the top to offset space used for the reflection
			title.setPadding(new Insets(H * 0.25, 0.0, 0.0, 0.0));

			core.getChildren().add(title);
		}

		// Add a spotlight that looks like the top position is lit by the title
		spot = new Light.Spot(0.0, 0.0, 200.0, 1.5, Color.WHITE);

		spot.setPointsAtZ(-200.0);

		// Add a top-to-bottom background gradient lit by the spotlight
		fill = new Rectangle();

		fill.setFill(GRADIENT);
		fill.setEffect(new Lighting(spot));

		// Stack the background, spotlight, titles, and items
		base = new StackPane(fill, core, all);

		// Try to keep the keyboard focus to allow keypress navigation
		base.setFocusTraversable(true);
		base.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
		base.addEventFilter(MouseEvent.MOUSE_ENTERED, this::handleMouseEntered);
		base.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);

		return base;
	}

	private void	updateItemEffects()
	{
		int	index = (Integer)controller.get("itemIndex");
		int	i = 0;

		// For each item, calculate effects for its border and label
		for (Node one : all.getChildren())
		{
			Shape	shape = (Shape)getDescendant(one, 0);
			Label	label = (Label)getDescendant(one, 1);

			if (i == index)	// Make the selected item brighter
			{
				shape.setFill(WHITE_75);

				label.setTextFill(Color.BLACK);
				label.setEffect(GLOW);

			}
			else				// Make all other items darker
			{
				shape.setFill(WHITE_25);

				label.setTextFill(Color.WHITE);
				label.setEffect(COLOR_ADJUST);
			}

			i++;
		}
	}

	private void	updateLayout()
	{
		int	n = all.getChildren().size();		// Number of items to layout
		double	ninv = 1.0 / n;					// One-nth of the way around
		double	a = 0.45 * (fill.getWidth() - W);	// A bit less than pane w/2
		double	b = 0.45 * (fill.getHeight() - H);	// A bit less than pane h/2
		int	i = 0;								// Item number

		// For each item, calculate its angle, position, size, and blurring
		for (Node one : all.getChildren())
		{
			// Items are arranged in a cycle, clockwise from the top
			double	phase = 0.25 + angle.get() - i * ninv;	// Normalized angle
			double	theta = 2.0 * Math.PI * phase;			// Angle in radians

			// Position items relative to the center of the pane
			double	x = a * (1 + Math.cos(theta));			// [0.0, 2.0]
			double	y = b * (1 - Math.sin(theta));			// [0.0, 2.0]

			one.setTranslateX(x);
			one.setTranslateY(y);

			// Size items smaller toward the bottom of the cycle
			double	s = 0.25 * (3.0 + Math.sin(theta));	// [0.5, 1.0]

			one.setScaleX(s);
			one.setScaleY(s);

			// Gradually blur items toward bottom of the cycle
			one.setEffect(new GaussianBlur(8.0 * Math.sqrt(1.0 - s)));

			i++;
		}

		int	j = 0;								// Title number

		// There are ninv segments around the cycle, one for each item.
		// Hide all titles except the one for the item closest to the top.
		for (Node one : core.getChildren())
		{
			// Angular position of the item around the cycle
			double	phase = angle.get() - j * ninv;

			// Half of the top item's segment is at the *end* of the cycle
			if (phase > 1.0 - ninv)
				phase -= 1.0;

			// Make the title of the top item visible, all others invisible
			one.setVisible(Math.abs(phase) < 0.5 * ninv);

			j++;
		}
	}

	//**********************************************************************
	// Private Methods (Animation)
	//**********************************************************************

	// Basic version. Doesn't minimize rotation by wrapping around the cycle.
	// Rotation amount & speed are proportional to raw arc difference.
	private Animation	createAnimation2(double toAngle)
	{
		Timeline		timeline = new Timeline(FRAMERATE);
		List<KeyFrame>	kfs = timeline.getKeyFrames();
		double			fromAngle = angle.get();

		kfs.add(new KeyFrame(DURATION,			// Rotate to destination
					new KeyValue(angle, toAngle, Interpolator.EASE_BOTH)));

		return timeline;
	}

	// Improved version. Minimizes rotation by wrapping around the cycle.
	// Rotation amount & speed are proportional to shortest arc difference.
	private Animation	createAnimation(double toAngle)
	{
		Timeline		timeline = new Timeline(FRAMERATE);
		List<KeyFrame>	kfs = timeline.getKeyFrames();
		double			fromAngle = angle.get();
		double			delta = Math.abs(toAngle - fromAngle);

		if (delta < 0.5)				// Shortest arc is direct
		{
			kfs.add(new KeyFrame(DURATION,
						new KeyValue(angle, toAngle, Interpolator.EASE_BOTH)));
		}
		else if (toAngle < fromAngle)	// Shortest arc wraps clockwise
		{
			// Calculate proportional time needed to rotate to wrap point
			double		ratio = (1.0 - fromAngle) / (1.0 - delta);
			Duration	d = DURATION.multiply(ratio);

			kfs.add(new KeyFrame(d,			// Rotate to wrap point
						new KeyValue(angle, 1.0, Interpolator.EASE_IN)));
			kfs.add(new KeyFrame(d,			// Wrap is instantaneous
						new KeyValue(angle, 0.0)));
			kfs.add(new KeyFrame(DURATION,		// Rotate to destination
						new KeyValue(angle, toAngle, Interpolator.EASE_OUT)));
		}
		else if (toAngle > fromAngle)	// Shortest arc wraps counterclockwise
		{
			// Calculate proportional time needed to rotate to wrap point
			double		ratio = fromAngle / (1.0 - delta);
			Duration	d = DURATION.multiply(ratio);

			kfs.add(new KeyFrame(d,			// Rotate to wrap point
						new KeyValue(angle, 0.0, Interpolator.EASE_IN)));
			kfs.add(new KeyFrame(d,			// Wrap is instantaneous
						new KeyValue(angle, 1.0)));
			kfs.add(new KeyFrame(DURATION,		// Rotate to destination
						new KeyValue(angle, toAngle, Interpolator.EASE_OUT)));
		}

		return timeline;
	}

	//**********************************************************************
	// Private Methods (Change Handlers)
	//**********************************************************************

	// Whenever the scene resizes, update the background, spotlight, and layout.
	private void	changeWidth(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		double	w = (Double)newValue;

		fill.setWidth(w);
		spot.setX(w * 0.5);
		spot.setPointsAtX(w * 0.5);

		updateLayout();
	}

	// Whenever the scene resizes, update the background, spotlight, and layout.
	private void	changeHeight(ObservableValue<? extends Number> observable,
								 Number oldValue, Number newValue)
	{
		double	h = (Double)newValue - OFFSET;

		fill.setHeight(h);
		spot.setY(h * 0.5);
		spot.setPointsAtY(OFFSET);

		updateLayout();
	}

	// Whenever the angle changes, update positions and features of all items.
	// The animation repeatedly calls this method with interpolated angles.
	private void	changeAngle(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		updateItemEffects();
		updateLayout();
	}

	//**********************************************************************
	// Private Methods (Input Handlers)
	//**********************************************************************

	// Rotate the cycle forward or backward one, or to the home (first) item
	private void	handleKeyPressed(KeyEvent e)
	{
		KeyCode	code = e.getCode();
		int		n = all.getChildren().size();
		int		m = (e.isShiftDown() ? 5 : 1);	// Number of items to rotate
		int		index = (Integer)controller.get("itemIndex");

		switch (code)
		{
			case HOME:		index =  0;	break;	// Put first item at the top
			case LEFT:		index -= m;	break;	// Rotate CW m items
			case RIGHT:	index += m;	break;	// Rotate CCW m items
			default:						return;
		}

		while (index < 0)
			index += n;

		while (index >= n)
			index -= n;

		controller.set("itemIndex", index);

		e.consume();
	}

	// Try to hold the keyboard focus (the parent TabPane likes to claim it)
	private void	handleMouseEntered(MouseEvent e)
	{
		base.requestFocus();
	}

	// Try to hold the keyboard focus (the parent TabPane likes to claim it)
	private void	handleMouseMoved(MouseEvent e)
	{
		base.requestFocus();
	}

	// Rotate the cycle to put the clicked item at the top
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
