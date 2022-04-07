//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb 25 13:36:48 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb;

//import java.lang.*;
import java.util.ArrayList;

//******************************************************************************

/**
 * The <CODE>Controller</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Controller
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Where the data lives; only one place.
	private Model					model;

	// Where the data is shown; can be in multiple places.
	private final ArrayList<View>	views;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Controller()
	{
		this.views = new ArrayList<View>();
	}

	//**********************************************************************
	// Public Methods (Model)
	//**********************************************************************

	public void	setModel(Model model)
	{
		this.model = model;
	}

	// Pass updates from the model on to all the views.
	public void	update(String key, Object value)
	{
		//System.out.println("controller: update " + key + " to " + value);

		for (View v : views)
			v.update(key, value);
	}

	//**********************************************************************
	// Public Methods (Views)
	//**********************************************************************

	public void	addView(View view)
	{
		view.initialize();		// Couple view appearance to model state
		views.add(view);		// Make sure view receives all future updates
	}

	public void	removeView(View view)
	{
		views.remove(view);	// Make sure view receives no more updates
		view.terminate();		// Decouple view appearance from model state

		if (views.isEmpty())	// Nothing left to see here...
			System.exit(0);
	}

	// For views to access data values whenever they want, such as for
	// drawing or calculating changes in response to interactions.
	public Object	get(String key)
	{
		//System.out.println("controller: get " + key);

		return model.getValue(key);
	}

	// For views to modify data values whenever they want, usually after
	// changes have been calculated in response to an interaction.
	public void	set(String key, Object value)
	{
		System.out.println("controller: set " + key + " to " + value);

		model.setValue(key, value);
	}

	// This a placeholder, here to suggest reponsiveness to interactions that
	// trigger state transitions, such as button presses. In practice, the
	// trigger would indicate to the model to perform (or initiate a query to
	// perform) some calculation based on its current data. The set() method
	// is essentially a trigger in which the calculation is value assignment.
	public void	trigger(String name)
	{
		System.out.println("controller: trigger " + name);
		model.trigger(name);
	}
}

//******************************************************************************
