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
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb;

//import java.lang.*;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.*;

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
	private final Controller				controller;

	// Easy, extensible way to store multiple simple, independent parameters
	private final HashMap<String, Object>	properties;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(Controller controller)
	{
		this.controller = controller;

		// Create the parameters accessed and/or modified by controls
		properties = new HashMap<String, Object>();

		// Add parameters accessed and/or modified by CollectionPane controls
		properties.put("selectedMovieIndex",			-1);

		// Add parameters accessed and/or modified by EditorPane controls
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
}

//******************************************************************************
