//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Feb 24 15:08:15 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190206 [weaver]:	Original file.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.example.fx;

//import java.lang.*;
import java.util.List;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

//******************************************************************************

public final class FXIceSkating extends Application
{
	//**********************************************************************
	// Main
	//**********************************************************************

	public static void	main(String[] args)
	{
		launch(args);
	}

	//**********************************************************************
	// Private Class Members (Animation)
	//**********************************************************************

	private static final double	FRAMERATE = 60.0;
	private static final Duration	DURATION = Duration.seconds(12.0);

	//**********************************************************************
	// Private Class Members (Layout)
	//**********************************************************************

	private static final double	W = 800.0;		// Scene width
	private static final double	H = 600.0;		// Scene height
	private static final double	R = 100.0;		// Circle radius;

	//**********************************************************************
	// Private Class Members (Effects)
	//**********************************************************************

	private static final Color		WHITE_50 = Color.web("white", 0.50);
	private static final Color		WHITE_25 = Color.web("white", 0.25);

	private static final RadialGradient	RADIAL_GRADIENT =
		new RadialGradient(90.0, 0.5, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE,
						   new Stop(1.00, Color.web("#8080ff", 1.00)),
						   new Stop(0.00, Color.web("#ffffff", 1.00)));
	private static final LinearGradient	LINEAR_GRADIENT =
		new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
						   new Stop(0.00, Color.web("#ffffff", 1.00)),
						   new Stop(1.00, Color.web("#8080ff", 1.00)));

	private static final Reflection		REFLECTION =
		new Reflection(10.0, 0.33, 0.50, 0.0);

	//**********************************************************************
	// Override Methods (Application)
	//**********************************************************************

	public void	start(Stage stage)
	{
		Group		shapes = new Group();

		for (int i=0; i<5; i++)
		{
			Circle	circle = new Circle(R);

			circle.setFill(WHITE_25);
			circle.setStroke(WHITE_50);
			circle.setStrokeType(StrokeType.OUTSIDE);
			circle.setStrokeWidth(4);

			// For experimenting with effects...
			circle.setEffect(REFLECTION);
			//circle.setEffect(new Bloom(0.3));
			//circle.setEffect(new BoxBlur(10, 10, 1));
			//circle.setEffect(new Glow(0.3));
			//circle.setEffect(new MotionBlur(45.0, 10.0));
			//circle.setEffect(new SepiaTone(0.7));
			//circle.setEffect(new Shadow(10.0, Color.RED));

			shapes.getChildren().add(circle);
		}

		Rectangle	fill = new Rectangle(0.0, 0.0, RADIAL_GRADIENT);

		Group		group = new Group(fill, shapes);
		Scene		scene = new Scene(group, W, H, Color.BLACK);

		stage.setScene(scene);
		stage.setTitle("Ice Skating");
		stage.setResizable(false);
		stage.show();

		fill.widthProperty().bind(scene.widthProperty());
		fill.heightProperty().bind(scene.heightProperty());

		Animation	animation = createAnimation(shapes);

		animation.play();
	}

	//**********************************************************************
	// Private Methods (Animation)
	//**********************************************************************

	private Animation	createAnimation(Group shapes)
	{
		MotionBlur		blur = new MotionBlur(45.0, 0.0);
		DropShadow		shadow = new DropShadow(10.0, 5.0, 5.0, Color.BLUE);

		Timeline		timeline = new Timeline(FRAMERATE);
		List<KeyFrame>	kfs = timeline.getKeyFrames();

		for (Node circle : shapes.getChildren())
		{
			double	x1 = W * Math.random();
			double	y1 = H * Math.random();
			double	x2 = W * Math.random();
			double	y2 = H * Math.random();

			// Begin at a random location with full width and height, zero blur
			kfs.add(new KeyFrame(Duration.ZERO,
						new KeyValue(circle.translateXProperty(), x1),
						new KeyValue(circle.translateYProperty(), y1),
						new KeyValue(circle.scaleXProperty(), 1.0),
						new KeyValue(circle.scaleYProperty(), 1.0),
						new KeyValue(shapes.effectProperty(), blur),
						new KeyValue(blur.radiusProperty(), 0.0)));

			// Start decreasing blur, spin and make shorter
			kfs.add(new KeyFrame(Duration.seconds(3.0),
						new KeyValue(circle.scaleXProperty(), -1.0),
						new KeyValue(circle.scaleYProperty(), 0.9),
						new KeyValue(shapes.effectProperty(), blur),
						new KeyValue(blur.radiusProperty(), 20.0)));

			// Turn off blur, start increasing shadow, spin and make taller
			kfs.add(new KeyFrame(Duration.seconds(6.0),
						new KeyValue(circle.scaleXProperty(), 1.0),
						new KeyValue(circle.scaleYProperty(), 1.2),
						new KeyValue(shapes.effectProperty(), shadow),
						new KeyValue(blur.radiusProperty(), 0.0),
						new KeyValue(shadow.radiusProperty(), 0.0)));

			// Start decreasing shadow, spin and make shorter
			kfs.add(new KeyFrame(Duration.seconds(9.0),	// Plain
						new KeyValue(circle.scaleXProperty(), -1.0),
						new KeyValue(circle.scaleYProperty(), 0.7),
						new KeyValue(shapes.effectProperty(), shadow),
						new KeyValue(shadow.radiusProperty(), 10.0)));

			// End at a random location with fill width and height
			kfs.add(new KeyFrame(DURATION,
						new KeyValue(circle.translateXProperty(), x2),
						new KeyValue(circle.translateYProperty(), y2),
						new KeyValue(circle.scaleXProperty(), 1.0),
						new KeyValue(circle.scaleYProperty(), 1.0),
						new KeyValue(shapes.effectProperty(), null),
						new KeyValue(shadow.radiusProperty(), 0.0)));
		}

		timeline.setAutoReverse(true);					// Move back and forth
		timeline.setCycleCount(Animation.INDEFINITE);	// Repeat indefinitely

		return timeline;
	}
}

//******************************************************************************
