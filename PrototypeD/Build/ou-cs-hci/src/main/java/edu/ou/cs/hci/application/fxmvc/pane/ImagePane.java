//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Feb 24 18:22:40 2019 by Chris Weaver
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
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.util.Duration;
import edu.ou.cs.hci.application.fxmvc.Controller;

//******************************************************************************

/**
 * The <CODE>ImagePane</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class ImagePane extends AbstractPane
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final String	NAME = "Image";
	private static final String	HINT = "Basic Layout for Images";

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 80;		// Item icon width
	private static final double	H = W * 1.5;	// Item icon height

	//**********************************************************************
	// Private Class Members (Animation)
	//**********************************************************************

	private static final double	FRAMERATE = 40.0;
	private static final Duration	DURATION = Duration.seconds(3.0);

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
	private List<List<String>>		data;

	// Layout
	private StackPane				base;
	private TilePane				all;

	// Animation
	private Animation				animation;
	private SimpleDoubleProperty	scale;

	// Handlers
	private final ActionHandler	actionHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public ImagePane(Controller controller)
	{
		super(controller, NAME, HINT);

		scale = new SimpleDoubleProperty(1.0);

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
		scale.addListener(this::changeScale);

		updateItemEffects();

		animation = createAnimation();
		animation.play();
	}

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.
	public void	terminate()
	{
		animation.stop();
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

		all = new TilePane(8.0, 8.0);

		all.setAlignment(Pos.TOP_LEFT);

		for (List<String> item : data)
		{
			ImageView	icon = createFXIcon(item.get(1), W, H);
			Button		button = new Button(item.get(0), icon);

			button.setContentDisplay(ContentDisplay.TOP);
			button.setPrefWidth(H);	// *Want* height here (not a mistake!)

			button.setOnAction(actionHandler);

			all.getChildren().add(button);
		}

		// Put the box full width in a scroll pane set for vertical scrolling
		ScrollPane		scrollPane = new ScrollPane(all);

		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToWidth(true);

		base = new StackPane(scrollPane);

		return base;
	}

	private void	updateItemEffects()
	{
		int	index = (Integer)controller.get("itemIndex");
		int	i = 0;

		// For each item, calculate effects for its node
		for (Node one : all.getChildren())
		{
			Button		button = (Button)one;
			ImageView	icon = (ImageView)button.getGraphic();

			if (i == index)
			{
				button.setTextFill(Color.BLACK);

				icon.setOpacity(Math.abs(scale.get()));
				icon.setScaleX(scale.get());
				icon.setEffect(GLOW);
			}
			else
			{
				button.setTextFill(Color.GRAY);

				icon.setOpacity(1.0);
				icon.setScaleX(1.0);
				icon.setEffect(COLOR_ADJUST);
			}

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

		kfs.add(new KeyFrame(DURATION,
					new KeyValue(scale, -1.0, Interpolator.EASE_BOTH)));

		timeline.setAutoReverse(true);					// Scale back and forth
		timeline.setCycleCount(Animation.INDEFINITE);	// ...indefinitely!

		return timeline;
	}

	//**********************************************************************
	// Private Methods (Change Handlers)
	//**********************************************************************

	private void	changeScale(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue)
	{
		updateItemEffects();
	}

	//**********************************************************************
	// Inner Classes (Event Handlers)
	//**********************************************************************

	private final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object		source = e.getSource();
			int		index = all.getChildren().indexOf(source);

			if (index == -1)
				return;

			controller.trigger(((Button)source).getText());
			controller.set("itemIndex", index);
		}
	}
}

//******************************************************************************
