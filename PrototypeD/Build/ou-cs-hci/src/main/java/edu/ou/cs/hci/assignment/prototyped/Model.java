//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Fri Feb 14 12:13:47 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
// 20200212 [weaver]:	Overhauled for new PrototypeB in Spring 2020.
// 20200228 [weaver]:	Added observables for new Prototype C in Spring 2020.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototyped;

//import java.lang.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>Model</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Model
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Master of the program, manager of the data, mediator of all updates
	private final Controller					controller;

	// Easy, extensible way to store multiple simple, independent parameters
	private final HashMap<String, Object>		properties;

	// Add an ObservableMap to store a set of observable objects.
	private final HashMap<String, Observable>	observables;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(Controller controller)
	{
		this.controller = controller;

		// Create the parameters accessed and/or modified by controls. These are
		// old properties carried over from the implementation of Prototype B.
		properties = new HashMap<String, Object>();
		addProperties();

		// Create the observables accessed and/or modified by controls. These
		// are new properties to be used in the implementation of Prototype C.
		observables = new HashMap<String, Observable>();
		addObservables();
	}

	private void	addProperties()
	{
		// Add parameters accessed and/or modified by CollectionPaneB controls
		properties.put("selectedMovieIndex",			-1);

		// Add parameters accessed and/or modified by EditorPaneB controls
		properties.put("movie.title",					"");
		properties.put("movie.imageFile",				"");
		properties.put("movie.year",					1900);
		properties.put("movie.rating",					1);
		properties.put("movie.runtime",				1);

		properties.put("movie.award.picture",			Boolean.FALSE);
		properties.put("movie.award.directing",		Boolean.FALSE);
		properties.put("movie.award.cinematography",	Boolean.FALSE);
		properties.put("movie.award.acting",			Boolean.FALSE);

		properties.put("movie.averageReviewScore",		0.0);
		properties.put("movie.numberOfReviews",		0);
		properties.put("movie.genre",					0);

		properties.put("movie.director",				"");
		properties.put("movie.isAnimated",				Boolean.FALSE);
		properties.put("movie.isColor",				Boolean.TRUE);

		properties.put("movie.summary",				"");
		properties.put("movie.comments",				"");

		// Add special parameters accessed and/or modified by TextAreas
		properties.put("movie.summary.anchor",			0);
		properties.put("movie.comments.anchor",		0);
		properties.put("movie.summary.caret",			0);
		properties.put("movie.comments.caret",			0);
	}

	private void	addObservables()
	{
		// For each observable, (1) create a parameter or data structure object;
		// (2) create a property for it; and (3) add the property to the map.
		// For some observables, also (4) add a listener to handle changes.

		// ********** Collection File **********
		// The collection CSV file displayed in the UI. Defaults to null. The
		// model loads the collection data from that file. If the file is null,
		// example data is loaded from a fixed resource path (see below).
		SimpleObjectProperty<File>	pfile = new SimpleObjectProperty<File>();

		observables.put("file", pfile);

		pfile.addListener(this::load);		// Listener to process changes


		// ********** Genres List **********
		// List of genre strings. Loaded from a file at a fixed resource path.
		List<String>				gd = Resources.getLines("data/genres.txt");
		ObservableList<String>		gl = FXCollections.observableArrayList(gd);
		SimpleListProperty<String>	gp = new SimpleListProperty<String>(gl);

		observables.put("genres", gp);


		// ********** Ratings List **********
		// List of rating strings. Loaded from a file at a fixed resource path.
		List<String>				rd = Resources.getLines("data/ratings.txt");
		ObservableList<String>		rl = FXCollections.observableArrayList(rd);
		SimpleListProperty<String>	rp = new SimpleListProperty<String>(rl);

		observables.put("ratings", rp);


		// ********** Movies List **********
		// List of movie objects. Loaded from a CSV file at a fixed resource
		// path, until the collectionFile property is set to a user-chosen file.
		List<List<String>>		md = Resources.getCSVData("data/movies.csv");
		List<Movie>			ma = new ArrayList<Movie>();

		for (List<String> item : md)
			ma.add(new Movie(item));

		ObservableList<Movie>		ml = FXCollections.observableArrayList(ma);
		SimpleListProperty<Movie>	mp = new SimpleListProperty<Movie>(ml);

		observables.put("movies", mp);


		// ********** Selected Movie **********
		// The currently selected movie object in the collection. Defaults to
		// null when the collection is empty, including at the start before data
		// is loaded.
		Movie							m = null;//ml.get(0);
		SimpleObjectProperty<Movie>	sp = new SimpleObjectProperty<Movie>(m);

		observables.put("movie", sp);
	}

	//**********************************************************************
	// Public Methods (Property Change Handlers)
	//**********************************************************************

	// This method is called whenever the value of the 'file' changes. From the
	// named file, it loads movie CSV data, creates Movie objects, puts them in
	// a list, and updates 'movies' and 'movie' so the first Movie is selected.
	private void	load(ObservableValue<? extends File> observable,
						 File oldValue, File newValue)
	{
		try
		{
			List<List<String>>		md;

			if (newValue == null)
				md = Resources.getCSVData("data/movies.csv");
			else
				md = Resources.getCSVData(newValue.toURI().toURL());

			List<Movie>			ma = new ArrayList<Movie>();

			for (List<String> item : md)
				ma.add(new Movie(item));

			ObservableList<Movie>	ml = FXCollections.observableArrayList(ma);

			setPropertyValue("movie", null);
			setPropertyValue("movies", ml);
			setPropertyValue("movie", ((ml.size() > 0) ? ml.get(0) : null));
		}
		catch (SecurityException ex)
		{
			System.err.println("***Error accessing file.***");
			return;
		}
		catch (MalformedURLException ex)
		{
			System.err.println("***Error converting file path to URL.***");
			return;
		}
		catch (Exception ex)
		{
			System.err.println("***Error loading data from file.***");
			return;
		}
	}

	// Saves the collection to a file, which becomes the new value of 'file'.
	// This is an easy but not very safe way to support opening/saving of files.
	@SuppressWarnings("unchecked")
	public void	save(File file)
	{
		List<Movie>		movies = (List<Movie>)getPropertyValue("movies");
		List<List<String>>	data = new ArrayList<List<String>>();

		for (Movie movie : movies)
			data.add(movie.getAllAttributesAsStrings());

		Resources.putCSVData(file, data);

		setValue("file", file);
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	public Object	getValue(String key)
	{
		return properties.get(key);
	}

	public void	setValue(String key, Object value)
	{
		if (properties.containsKey(key) &&
			properties.get(key).equals(value))
		{
			System.out.println("  model: value not changed");
			return;
		}

		Platform.runLater(new Updater(key, value));
	}

	public void	trigger(String name)
	{
		System.out.println("  model: (not!) calculating function: " + name);
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	public Object	getPropertyValue(String key)
	{
		return ((Property)observables.get(key)).getValue();
	}

	public void	setPropertyValue(String key, Object newValue)
	{
		if (!observables.containsKey(key))
			return;

		Object	oldValue = ((Property)observables.get(key)).getValue();

		// Ignore when newValue == oldValue (including when both are null).
		if ((oldValue == null) ? (newValue == null) : oldValue.equals(newValue))
		{
			System.out.println("  model: property value not changed");
			return;
		}

		Platform.runLater(new PropertyUpdater(key, newValue, oldValue));
	}

	//**********************************************************************
	// Inner Classes
	//**********************************************************************

	private class Updater
		implements Runnable
	{
		private final String	key;
		private final Object	value;

		public Updater(String key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		public void	run()
		{
			properties.put(key, value);
			controller.update(key, value);
		}
	}

	private class PropertyUpdater
		implements Runnable
	{
		private final String	key;
		private final Object	newValue;
		private final Object	oldValue;

		public PropertyUpdater(String key, Object newValue, Object oldValue)
		{
			this.key = key;
			this.newValue = newValue;
			this.oldValue = oldValue;
		}

		// Suppress unchecked warnings. This is risky because View code can try
		// to assign a value of one type to a property of a different type.
		@SuppressWarnings("unchecked")
		public void	run()
		{
			((Property)(observables.get(key))).setValue(newValue);
			controller.updateProperty(key, newValue, oldValue);
		}
	}
}

//******************************************************************************
