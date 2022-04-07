//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Feb  5 00:19:21 2019 by Chris Weaver
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//******************************************************************************

/**
 * The <CODE>Application</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Application
{
	//**********************************************************************
	// Main
	//**********************************************************************

	public static void	main(String[] argv)
		throws Exception
	{
		// You can comment this out to use the native look & feel, but beware
		// if you do: your UI will look different on Mac, Windows, and Linux.
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		// Should always run Swing code on the Swing thread
		SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					Controller	controller = new Controller();
					Model		model = new Model(controller);

					controller.setModel(model);

					View		view1 = new View(controller);
					View		view2 = new View(controller);
					//View		view3 = new View(controller);

					controller.addView(view1);
					controller.addView(view2);
					//controller.addView(view3);
				}
			});
	}
}

//******************************************************************************
