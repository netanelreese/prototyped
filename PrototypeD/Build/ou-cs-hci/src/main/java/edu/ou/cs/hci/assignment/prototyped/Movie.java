//******************************************************************************
// Copyright (C) 2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Mar  1 18:20:44 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20200212 [weaver]:	Original file.
// 20200228 [weaver]:	Added alternative access methods.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototyped;

//import java.lang.*;
import java.util.*;
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

	private final SimpleIntegerProperty	year;
	private final SimpleStringProperty		rating;
	private final SimpleIntegerProperty	runtime;

	private final SimpleBooleanProperty	awardPicture;
	private final SimpleBooleanProperty	awardDirecting;
	private final SimpleBooleanProperty	awardCinematography;
	private final SimpleBooleanProperty	awardActing;

	private final SimpleDoubleProperty		averageReviewScore;
	private final SimpleIntegerProperty	numberOfReviews;
	private final SimpleIntegerProperty	genre;

	private final SimpleStringProperty		director;
	private final SimpleBooleanProperty	isAnimated;
	private final SimpleBooleanProperty	isColor;

	private final SimpleStringProperty		summary;
	private final SimpleStringProperty		comments;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Movie(List<String> item)
	{
		// Each attribute value must be calculated from its string.

		title = new SimpleStringProperty(item.get(0));
		image = new SimpleStringProperty(item.get(1));

		// DONE #1: Create properties for the other attributes. For non-string
		// types, look for methods in the Boolean, Integer, and Double classes.

		year = new SimpleIntegerProperty(Integer.parseInt(item.get(2)));
		rating = new SimpleStringProperty(item.get(3));
		runtime = new SimpleIntegerProperty(Integer.parseInt(item.get(4)));

		awardPicture = new SimpleBooleanProperty(
										Boolean.parseBoolean(item.get(5)));
		awardDirecting = new SimpleBooleanProperty(
										Boolean.parseBoolean(item.get(6)));
		awardCinematography = new SimpleBooleanProperty(
										Boolean.parseBoolean(item.get(7)));
		awardActing = new SimpleBooleanProperty(
										Boolean.parseBoolean(item.get(8)));

		averageReviewScore = new SimpleDoubleProperty(
										Double.parseDouble(item.get(9)));
		numberOfReviews = new SimpleIntegerProperty(
										Integer.parseInt(item.get(10)));
		genre = new SimpleIntegerProperty(Integer.parseInt(item.get(11)));

		director = new SimpleStringProperty(item.get(12));
		isAnimated = new SimpleBooleanProperty(
										Boolean.parseBoolean(item.get(13)));
		isColor = new SimpleBooleanProperty(
										Boolean.parseBoolean(item.get(14)));

		summary = new SimpleStringProperty(item.get(15));
		comments = new SimpleStringProperty(item.get(16));

		// Hint for genres: An integer can be treated as a collection of
		// independently set bits. See genre code in EditorPane for examples.
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

	public int	getYear()
	{
		return year.get();
	}

	public void	setYear(int v)
	{
		year.set(v);
	}

	public String	getRating()
	{
		return rating.get();
	}

	public void	setRating(String v)
	{
		rating.set(v);
	}

	public int	getRuntime()
	{
		return runtime.get();
	}

	public void	setRuntime(int v)
	{
		runtime.set(v);
	}

	public boolean	getAwardPicture()
	{
		return awardPicture.get();
	}

	public void	setAwardPicture(boolean v)
	{
		awardPicture.set(v);
	}

	public boolean	getAwardDirecting()
	{
		return awardDirecting.get();
	}

	public void	setAwardDirecting(boolean v)
	{
		awardDirecting.set(v);
	}

	public boolean	getAwardCinematography()
	{
		return awardCinematography.get();
	}

	public void	setAwardCinematography(boolean v)
	{
		awardCinematography.set(v);
	}

	public boolean	getAwardActing()
	{
		return awardActing.get();
	}

	public void	setAwardActing(boolean v)
	{
		awardActing.set(v);
	}

	public double	getAverageReviewScore()
	{
		return averageReviewScore.get();
	}

	public void	setAverageReviewScore(double v)
	{
		averageReviewScore.set(v);
	}

	public int	getNumberOfReviews()
	{
		return numberOfReviews.get();
	}

	public void	setNumberOfReviews(int v)
	{
		numberOfReviews.set(v);
	}

	public int	getGenre()
	{
		return genre.get();
	}

	public void	setGenre(int v)
	{
		genre.set(v);
	}

	public String	getDirector()
	{
		return director.get();
	}

	public void	setDirector(String v)
	{
		director.set(v);
	}

	public boolean	getIsAnimated()
	{
		return isAnimated.get();
	}

	public void	setIsAnimated(boolean v)
	{
		isAnimated.set(v);
	}

	public boolean	getIsColor()
	{
		return isColor.get();
	}

	public void	setIsColor(boolean v)
	{
		isColor.set(v);
	}

	public String	getSummary()
	{
		return summary.get();
	}

	public void	setSummary(String v)
	{
		summary.set(v);
	}

	public String	getComments()
	{
		return comments.get();
	}

	public void	setComments(String v)
	{
		comments.set(v);
	}

	//**********************************************************************
	// Public Methods (Property Methods)
	//**********************************************************************

	public StringProperty	titleProperty()
	{
		return title;
	}

	public StringProperty	imageProperty()
	{
		return image;
	}

	public IntegerProperty	yearProperty()
	{
		return year;
	}

	public StringProperty	ratingProperty()
	{
		return rating;
	}

	public IntegerProperty	runtimeProperty()
	{
		return runtime;
	}

	public BooleanProperty	awardPictureProperty()
	{
		return awardPicture;
	}

	public BooleanProperty	awardDirectingProperty()
	{
		return awardDirecting;
	}

	public BooleanProperty	awardCinematographyProperty()
	{
		return awardCinematography;
	}

	public BooleanProperty	awardActingProperty()
	{
		return awardActing;
	}

	public DoubleProperty	averageReviewScoreProperty()
	{
		return averageReviewScore;
	}

	public IntegerProperty	numberOfReviewsProperty()
	{
		return numberOfReviews;
	}

	public IntegerProperty	genreProperty()
	{
		return genre;
	}

	public StringProperty	directorProperty()
	{
		return director;
	}

	public BooleanProperty	isAnimatedProperty()
	{
		return isAnimated;
	}

	public BooleanProperty	isColorProperty()
	{
		return isColor;
	}

	public StringProperty	summaryProperty()
	{
		return summary;
	}

	public StringProperty	commentsProperty()
	{
		return comments;
	}

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

	public String	getGenreAsString(List<String> gdata)
	{
		int			genre = getGenre();
		StringJoiner	sj = new StringJoiner(",");
		int			n = 0;

		for (String gname : gdata)
			if ((genre & (1 << n++)) != 0)
				sj.add(gname);

		return sj.toString();
	}

	public String	getRuntimeAsString()
	{
		int	runtime = getRuntime();

		return ((runtime / 60) + "h " + (runtime % 60) + "min");
	}

	public List<String>	getAllAttributesAsStrings()
	{
		ArrayList<String>	list = new ArrayList<String>();

		list.add(getTitle());
		list.add(getImage());

		list.add(Integer.toString(getYear()));
		list.add(getRating());
		list.add(Integer.toString(getRuntime()));

		list.add(Boolean.toString(getAwardPicture()));;
		list.add(Boolean.toString(getAwardDirecting()));;
		list.add(Boolean.toString(getAwardCinematography()));;
		list.add(Boolean.toString(getAwardActing()));;

		list.add(Double.toString(getAverageReviewScore()));
		list.add(Integer.toString(getNumberOfReviews()));
		list.add(Integer.toString(getGenre()));

		list.add(getDirector());
		list.add(Boolean.toString(getIsAnimated()));;
		list.add(Boolean.toString(getIsColor()));;

		list.add(getSummary());
		list.add(getComments());

		return list;
	}
}

//******************************************************************************
