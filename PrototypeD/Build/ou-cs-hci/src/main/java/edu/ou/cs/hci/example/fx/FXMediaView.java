//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Feb 24 15:08:17 2019 by Chris Weaver
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
//import java.net.URL;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;

//******************************************************************************

/**
 * The <CODE>FXMediaView</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class FXMediaView extends Application
{
	//**********************************************************************
	// Main
	//**********************************************************************

	public static void	main(String[] args)
	{
		launch(args);
	}

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Handlers
	private ActionHandler		actionHandler;

	// Layout
	private MediaView			mediaView;
	private Button				playButton;
	private Button				stopButton;

	// Support
	private MediaPlayer		mediaPlayer;

	//**********************************************************************
	// Override Methods (Application)
	//**********************************************************************

	public void	start(Stage stage)
	{
		actionHandler = new ActionHandler();

		mediaView = new MediaView();

		mediaView.setOnError(new MediaErrorHandler());

		playButton = new Button("Play");
		stopButton = new Button("Stop");

		playButton.setOnAction(actionHandler);
		stopButton.setOnAction(actionHandler);

		FlowPane	flowPane = new FlowPane(8.0, 8.0);

		flowPane.setAlignment(Pos.CENTER);

		flowPane.getChildren().add(playButton);
		flowPane.getChildren().add(stopButton);

		BorderPane	pane = new BorderPane();

		pane.setCenter(new StackPane(mediaView));
		pane.setBottom(flowPane);

		Scene	scene = new Scene(pane, 400, 240);

		stage.setTitle("Media View");
		stage.setScene(scene);
		stage.show();

		// Keep the media view the same width as the entire scene
		mediaView.fitWidthProperty().bind(scene.widthProperty());

		loadMedia();
	}

	//**********************************************************************
	// Private Methods
	//**********************************************************************

	public void	loadMedia()
	{
		// Loading from Jar file doesn't work (bug in javafx.media)
		//URL		url = Resources.getResource("example/fx/video/HPSS.mp4");
		//URL		url = FXMediaView.class.getResource("HPSS.mp4");
		//String		surl = url.toExternalForm();

		// Loading from file or using http works fine?
		// Can't currently use http on MacOS 10.15 due to security issues! %-{
		// For now, put media file in home directory as a workaround...
		//String		suri = "https://ampliation.org/hci/HPSS.mp4";
		String		home = System.getProperty("user.home");
		String		suri = "file://" + home + "/HPSS.mp4";

		Media		media = new Media(suri);

		media.setOnError(new Runnable() {
				public void run() {
					System.out.println(media.getError());
				}
			});

		mediaPlayer = new MediaPlayer(media);

		mediaPlayer.setOnError(new Runnable() {
				public void run() {
					System.out.println(mediaPlayer.getError());
				}
			});

		mediaView.setMediaPlayer(mediaPlayer);
	}

	//**********************************************************************
	// Override Methods (EventHandler<ActionEvent>)
	//**********************************************************************

	public final class ActionHandler
		implements EventHandler<ActionEvent>
	{
		public void	handle(ActionEvent e)
		{
			Object	source = e.getSource();

			if (source == playButton)
				mediaPlayer.play();
			else if (e.getSource() == stopButton)
				mediaPlayer.stop();
		}
	}

	//**********************************************************************
	// Override Methods (EventHandler<MediaErrorEvent>)
	//**********************************************************************

	public final class MediaErrorHandler
		implements EventHandler<MediaErrorEvent>
	{
		public void	handle(MediaErrorEvent e)
		{
			System.out.println(e);
		}
	}
}

//******************************************************************************
