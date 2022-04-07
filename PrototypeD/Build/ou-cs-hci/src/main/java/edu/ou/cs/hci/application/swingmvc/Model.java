//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb  4 22:33:57 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.application.swingmvc;

//import java.lang.*;
import java.util.HashMap;
import javax.swing.SwingUtilities;

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

		properties = new HashMap<String, Object>();

		// All of the parameters shown and/or modified in Views
		properties.put("lions",		Boolean.FALSE);
		properties.put("tigers",		Boolean.TRUE);
		properties.put("bears",		Boolean.FALSE);
		properties.put("surprised",	Boolean.FALSE);
		properties.put("flavor",		"chocolate");
		properties.put("number",		5);
		properties.put("comment",		"comment");
		properties.put("value",		"123");
		properties.put("index",		2);
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
			System.out.println("  value not changed");
			return;
		}

		SwingUtilities.invokeLater(new Updater(key, value));
	}

	public void	trigger(String name)
	{
		System.out.println("  model (not!) calculating function: " + name);
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
