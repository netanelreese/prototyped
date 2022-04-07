//******************************************************************************
// Copyright (C) 2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Wed Feb 12 23:13:57 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20200212 [weaver]:	Original file.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb;

//import java.lang.*;
import java.util.List;
import javafx.beans.property.*;
import javafx.scene.image.Image;

//******************************************************************************

/**
 * The <CODE>Movie</CODE> class manages the attributes of a movie as a set of
 * properties. The properties are created in the constructor. This differs from
 * the lazy creation of properties described in the TableView API (in the Person
 * class example), because we also use the properties to store the results of
 * parsing the inputs when the application starts.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Movie
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Each attribute has a matching property of the corresponding type.

	private final SimpleStringProperty		title;
	private final SimpleStringProperty		image;

	// TODO #0: Add members for the other 15 attributes.

	//private final SimpleFootypeProperty	foo;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Movie(List<String> item)
	{
		// Each attribute value must be calculated from its string.

		title = new SimpleStringProperty(item.get(0));
		image = new SimpleStringProperty(item.get(1));

		// TODO #1: Create properties for the other 15 attributes. For
		// non-string types, look for methods in the Boolean, Integer, and
		// Double classes to do any needed conversions or parsing of strings.

		//foo = new SimpleFootypeProperty(item.get(2));

		// Hint for genre: Treat the individual bits of an integer as booleans
		// that represent which genres a movie has or not. Look in EditorPane
		// for examples of how to map bits to and from subsets of genres.
	}

	//**********************************************************************
	// Public Methods (Getters and Setters)
	//**********************************************************************

	// Each attribute has methods to access and modify its value.

	public String	getTitle()
	{
		return title.get();
	}

	public void	setTitle(String v)
	{
		title.set(v);
	}

	public String	getImage()
	{
		return image.get();
	}

	public void	setImage(String v)
	{
		image.set(v);
	}

	// TODO #2: Create access and modify methods for the other 15 attributes.
	// For non-string attributes, use primitive types (boolean, int, double)
	// for the argument and return types.

	//public footype	getFoo()
	//{
	//	return foo.get();
	//}

	//public void	setFoo(footype v)
	//{
	//	foo.set(v);
	//}

	//**********************************************************************
	// Public Methods (Alternative Access Methods)
	//**********************************************************************

	// Convenience method for loading and resizing movie poster images. Loads
	// the image in the file named by the image property value, relative to the
	// given path, and returns a version resized to the given width and height.
	public Image	getImageAsImage(String path, double width, double height)
	{
		try
		{
			return new Image(path + getImage(), width, height, false, true);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
}

//******************************************************************************
