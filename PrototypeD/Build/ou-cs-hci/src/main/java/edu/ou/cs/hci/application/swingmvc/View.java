//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb  4 23:32:30 2019 by Chris Weaver
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
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;
import edu.ou.cs.hci.resources.Resources;

//******************************************************************************

/**
 * The <CODE>View</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class View
	implements ActionListener, CaretListener, ChangeListener,
			   ListSelectionListener
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final Dimension	SPACER = new Dimension(0, 10);

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Master of the program, manager of the data, mediator of all updates
	private final Controller	controller;

	// The widgets on the left
	private JCheckBox			checkBox1;
	private JCheckBox			checkBox2;
	private JCheckBox			checkBox3;
	private JCheckBox			checkBox4;
	private JComboBox<String>	comboBox;
	private JLabel				label;
	private JProgressBar		progressBar;
	private JRadioButton		radioButton1;
	private JRadioButton		radioButton2;
	private JRadioButton		radioButton3;
	private JSlider			slider;
	private JSpinner			spinner;
	private JTextArea			textArea;
	private boolean			ignoreCaretEvents;
	private JTextField			textField;
	private JToolBar			toolBar;

	// The list on the right
	private JList<String>		list;

	// The button on the bottom
	private JButton			strollButton;
	private JButton			sleepButton;
	private JButton			awakenButton;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	// Construct the UI.
	public View(Controller controller)
	{
		this.controller = controller;

		// Create a vertical panel of various widget panes to show on the left
		Box		box = Box.createVerticalBox();

		box.add(createCheckBoxes());
		box.add(Box.createRigidArea(SPACER));
		box.add(createComboBox());
		box.add(Box.createRigidArea(SPACER));
		box.add(createLabel());
		box.add(Box.createRigidArea(SPACER));
		box.add(createProgressBar());
		box.add(Box.createRigidArea(SPACER));
		box.add(createRadioButtons());
		box.add(Box.createRigidArea(SPACER));
		box.add(createSlider());
		box.add(Box.createRigidArea(SPACER));
		box.add(createSpinner());
		box.add(Box.createRigidArea(SPACER));
		box.add(createTextArea());
		box.add(Box.createRigidArea(SPACER));
		box.add(createTextField());
		box.add(Box.createRigidArea(SPACER));
		box.add(createToolBar());


		// Create a scroll pane for all the widget panes
		JScrollPane	scrollPane = new JScrollPane(box,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		// Create a panel with a scrolling list to put on the right
		JPanel		listPanel = createList();


		// Create a split pane to allow adjustment of left versus right...
		JSplitPane	splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
												true);

		splitPanel.setResizeWeight(0.70);	// ...with default 70% for the left

		splitPanel.setLeftComponent(scrollPane);
		splitPanel.setRightComponent(listPanel);


		// Create a pane with a few buttons
		JPanel		buttonPanel = createButtons();


		// Create the topmost panel to contain the split and button panels
		JPanel		panel = new JPanel();

		panel.setPreferredSize(new Dimension(840, 600));
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Title"));

		panel.add(splitPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);


		// Create a frame to hold everything
		JFrame		frame = new JFrame("View");
		JMenuBar	menubar = createMenubar();

		// Populate and show the frame
		frame.setBounds(50 + (int)(50 * Math.random()),
						50 + (int)(50 * Math.random()),
						200, 200);
		frame.setJMenuBar(menubar);
		frame.getContentPane().add(panel);
		frame.pack();
		//frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Exit when the user clicks the frame's close button
		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					controller.removeView(View.this);
				}
			});
	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	// Populate the UI with data, accessing it through the controller.
	public void	initialize()
	{
		checkBox1.setSelected((Boolean)controller.get("lions"));
		checkBox2.setSelected((Boolean)controller.get("tigers"));
		checkBox3.setSelected((Boolean)controller.get("bears"));
		checkBox4.setSelected((Boolean)controller.get("surprised"));

		checkBox4.setEnabled((Boolean)controller.get("lions") &&
							 (Boolean)controller.get("tigers") &&
							 (Boolean)controller.get("bears"));

		comboBox.setSelectedIndex((Integer)controller.get("index"));

		list.setSelectedIndex((Integer)controller.get("index"));

		progressBar.setValue((Integer)controller.get("number"));

		String	flavor = (String)controller.get("flavor");

		if ("vanilla".equals(flavor))
			radioButton1.setSelected(true);

		if ("chocolate".equals(flavor))
			radioButton2.setSelected(true);

		if ("strawberry".equals(flavor))
			radioButton3.setSelected(true);

		slider.setValue((Integer)controller.get("number"));

		spinner.setValue(controller.get("number"));

		textArea.setText((String)controller.get("comment"));

		textField.setText((String)controller.get("value"));
	}

	public void	terminate()
	{
		strollButton.removeActionListener(this);
		sleepButton.removeActionListener(this);
		awakenButton.removeActionListener(this);

		checkBox1.removeActionListener(this);
		checkBox2.removeActionListener(this);
		checkBox3.removeActionListener(this);
		checkBox4.removeActionListener(this);

		comboBox.removeActionListener(this);

		list.removeListSelectionListener(this);

		progressBar.removeChangeListener(this);

		radioButton1.removeActionListener(this);
		radioButton2.removeActionListener(this);
		radioButton3.removeActionListener(this);

		slider.removeChangeListener(this);

		spinner.removeChangeListener(this);

		textArea.removeCaretListener(this);

		textField.removeActionListener(this);

		// Should disable AbstractActions in toolbar buttons too...
	}

	// Update the UI when the controller says something changed in the model.
	public void	update(String key, Object value)
	{
		System.out.println("update " + key + " to " + value);

		if ("lions".equals(key))
			checkBox1.setSelected((Boolean)value);
		else if ("tigers".equals(key))
			checkBox2.setSelected((Boolean)value);
		else if ("bears".equals(key))
			checkBox3.setSelected((Boolean)value);
		else if ("surprised".equals(key))
			checkBox4.setSelected((Boolean)value);

		else if ("flavor".equals(key))
		{
			if ("vanilla".equals(value))
				radioButton1.setSelected(true);
			if ("chocolate".equals(value))
				radioButton2.setSelected(true);
			if ("strawberry".equals(value))
				radioButton3.setSelected(true);
		}

		else if ("number".equals(key))
		{
			progressBar.setValue((Integer)value);
			slider.setValue((Integer)value);
			spinner.setValue(value);
		}

		else if ("comment".equals(key))
		{
			// Setting the text in a JTextArea moves the caret to the start,
			// leading to an infinite update loop when there are multiple views.
			// Shut off caret event handling temporarily to avoid the problem.
			ignoreCaretEvents = true;
			textArea.setText((String)value);
			ignoreCaretEvents = false;
		}

		else if ("value".equals(key))
			textField.setText((String)value);

		else if ("index".equals(key))
		{
			comboBox.setSelectedIndex((Integer)value);
			list.setSelectedIndex((Integer)value);
		}

		// Check to see if the fourth checkbox should be enabled/disabled.
		if ("lions".equals(key) || "tigers".equals(key) || "bears".equals(key))
			checkBox4.setEnabled((Boolean)controller.get("lions") &&
								 (Boolean)controller.get("tigers") &&
								 (Boolean)controller.get("bears"));
	}

	//**********************************************************************
	// Override Methods (ActionListener)
	//**********************************************************************

	// Send changes to the controller from widgets that trigger actions.
	public void	actionPerformed(ActionEvent e)
	{
		Object		source = e.getSource();	// Component source of event

		if (source == strollButton)
			controller.trigger("strolling");
		else if (e.getSource() == sleepButton)
			controller.set("sleeping", 1);
		else if (e.getSource() == awakenButton)
			controller.set("sleeping", 0);

		else if (e.getSource() == checkBox1)
			controller.set("lions", checkBox1.isSelected());
		else if (e.getSource() == checkBox2)
			controller.set("tigers", checkBox2.isSelected());
		else if (e.getSource() == checkBox3)
			controller.set("bears", checkBox3.isSelected());
		else if (e.getSource() == checkBox4)
			controller.set("surprised", checkBox4.isSelected());

		else if (source == comboBox)
			controller.set("index", comboBox.getSelectedIndex());

		else if (e.getSource() == radioButton1)
			controller.set("flavor", "vanilla");
		else if (e.getSource() == radioButton2)
			controller.set("flavor", "chocolate");
		else if (e.getSource() == radioButton3)
			controller.set("flavor", "strawberry");

		else if (source == textField)
			controller.set("value", textField.getText());
	}

	//**********************************************************************
	// Override Methods (CaretListener)
	//**********************************************************************

	// Send changes to the controller from widgets that move a caret.
	public void	caretUpdate(CaretEvent e)
	{
		if (ignoreCaretEvents)
			return;

		Object		source = e.getSource();	// Component source of event

		if (source == textArea)
			controller.set("comment", textArea.getText());
	}

	//**********************************************************************
	// Override Methods (ChangeListener)
	//**********************************************************************

	// Send changes to the controller from widgets that change somehow.
	public void	stateChanged(ChangeEvent e)
	{
		Object		source = e.getSource();	// Component source of event

		if (source == progressBar)
			controller.set("number", progressBar.getValue());

		if (source == slider)
			controller.set("number", slider.getValue());

		if (source == spinner)
			controller.set("number", spinner.getValue());
	}

	//**********************************************************************
	// Override Methods (ListSelectionListener)
	//**********************************************************************

	// Send changes to the controller from widgets that select items.
	public void	valueChanged(ListSelectionEvent e)
	{
		Object		source = e.getSource();	// Component source of event

		if (source == list)
			controller.set("index", list.getSelectedIndex());
	}

	//**********************************************************************
	// Override Methods (WindowListener)
	//**********************************************************************

	// Let the controller know that this window is going away.
	public void	windowClosing(WindowEvent e)
	{
		controller.removeView(this);
	}

	//**********************************************************************
	// Private Methods
	//**********************************************************************

	// Make a menubar for this view's frame.
	private JMenuBar	createMenubar()
	{
		JMenuItem	openItem = new JMenuItem("Open...");
		JMenuItem	saveItem = new JMenuItem("Save...");
		JMenuItem	closeItem = new JMenuItem("Close");

		openItem.setEnabled(false);
		saveItem.setEnabled(false);
		closeItem.setEnabled(false);
		//closeItem.addActionListener(this);

		JMenu		menu = new JMenu("Menu");

		menu.add(openItem);
		menu.add(saveItem);
		menu.addSeparator();
		menu.add(closeItem);

		// By the way, menus aren't required to be in a menubar. They can be
		// laid out in windows like any other component.

		JMenuBar	menubar = new JMenuBar();

		menubar.add(menu);

		return menubar;
	}

	// This is a utility method to wrap components in a nicely labeled frame.
	// See the BorderFactory class API for many more panel decoration options.
	private JPanel		createTitledPanel(JComponent c, String title)
	{
		JPanel	p = new JPanel();

		p.setBorder(BorderFactory.createTitledBorder(title));
		p.add(c);

		return p;
	}

	//**********************************************************************
	// Private Methods (Widget Pane Creators)
	//**********************************************************************

	// Create a pane with three buttons for the gallery.
	private JPanel	createButtons()
	{
		JPanel		panel = new JPanel();

		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 8));

		// See the Resources class for a convenient way to handle image access
		// in a portable way. (This makes turning in your code much easier!)
		strollButton = new JButton("Stroll",
			Resources.getImage("example/swing/icon/baby-carriage.png"));
		sleepButton = new JButton("Sleep",
			Resources.getImage("example/swing/icon/bed.png"));
		awakenButton = new JButton("Awaken");

		strollButton.addActionListener(this);
		sleepButton.addActionListener(this);
		awakenButton.addActionListener(this);

		panel.add(strollButton);
		panel.add(sleepButton);
		panel.add(awakenButton);

		return panel;
	}

	// Create a pane with four checkboxes for the gallery. In this case, the
	// fourth checkbox depends on the settings of the other three. Use
	// setEnabled(false) on any component to make it inactive.
	private JPanel	createCheckBoxes()
	{
		checkBox1 = new JCheckBox("Lions");
		checkBox2 = new JCheckBox("Tigers");
		checkBox3 = new JCheckBox("Bears");
		checkBox4 = new JCheckBox("Oh My!");

		checkBox1.addActionListener(this);
		checkBox2.addActionListener(this);
		checkBox3.addActionListener(this);
		checkBox4.addActionListener(this);

		checkBox4.setEnabled(false);	// See the end of the update() method.

		Box	box = Box.createVerticalBox();

		box.add(checkBox1);
		box.add(checkBox2);
		box.add(checkBox3);
		box.add(Box.createRigidArea(SPACER));
		box.add(checkBox4);

		return createTitledPanel(box, "CheckBoxes");
	}

	// Create a pane with a combobox for the gallery. The combobox uses a
	// custom CellRenderer class to draw its items. The combobox and the list
	// share the same selection.
	private JPanel	createComboBox()
	{
		// See the Resources class for a convenient way to handle text access.
		ArrayList<String>	data =
			Resources.getLines("example/swing/text/list-data.txt");

		DefaultComboBoxModel<String>
			model = new DefaultComboBoxModel<String>();

		for (String item : data)
			model.addElement(item);

		comboBox = new JComboBox<String>(model);

		comboBox.setEditable(false);
		comboBox.setRenderer(new MyCellRenderer(false));
		comboBox.setMaximumRowCount(4);
		comboBox.addActionListener(this);

		return createTitledPanel(comboBox, "ComboBox");
	}

	// Create a pane with a label for the gallery.
	private JPanel	createLabel()
	{
		label = new JLabel("Bicycle",
						   Resources.getImage("example/swing/icon/bicycle.png"),
						   SwingConstants.LEFT);

		label.setBackground(Color.YELLOW);	// See comment below!
		label.setForeground(Color.BLUE);
		label.setFont(new Font(Font.SERIF, Font.BOLD, 24));

		// Setting the background color of a component generally doesn't work.
		// You *can* set the foreground color and the font for most components.
		// For background color, setBackground on the component's parent panel.

		JPanel	panel = new JPanel();

		panel.setBackground(Color.YELLOW);
		panel.add(label);

		return createTitledPanel(panel, "Label");
	}

	// Create a pane with a list for the gallery. The list uses a custom
	// CellRenderer class to draw its items. The combobox and the list share
	// the same selection.
	private JPanel	createList()
	{
		ArrayList<String>	data =
			Resources.getLines("example/swing/text/list-data.txt");

		DefaultListModel<String>	model =
			new DefaultListModel<String>();

		for (String item : data)
			model.addElement(item);

		list = new JList<String>(model);

		list.setPrototypeCellValue("##################,empty.png");
		list.setCellRenderer(new MyCellRenderer(true));
		list.setVisibleRowCount(6);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);

		JScrollPane		sp = new JScrollPane(list,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		return createTitledPanel(sp, "List");
	}

	// Create a pane with a progress bar for the gallery. The progress bar,
	// slider, and spinner show the same value from the model, so stay synced.
	private JPanel	createProgressBar()
	{
		DefaultBoundedRangeModel	model =
			new DefaultBoundedRangeModel(0, 0, 0, 100);

		progressBar = new JProgressBar(model);

		progressBar.setString("Changing mysteriously...");
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(200, 32));

		progressBar.addChangeListener(this);

		return createTitledPanel(progressBar, "ProgressBar");
	}

	// Create a pane with three radio buttons for the gallery. Putting them in
	// a ButtonGroup makes them  mutually exclusive.
	private JPanel	createRadioButtons()
	{
		radioButton1 = new JRadioButton("Vanilla");
		radioButton2 = new JRadioButton("Chocolate");
		radioButton3 = new JRadioButton("Strawberry");

		radioButton1.addActionListener(this);
		radioButton2.addActionListener(this);
		radioButton3.addActionListener(this);

		ButtonGroup	bg = new ButtonGroup();

		bg.add(radioButton1);
		bg.add(radioButton2);
		bg.add(radioButton3);

		Box	box = Box.createHorizontalBox();

		box.add(radioButton1);
		box.add(radioButton2);
		box.add(radioButton3);

		return createTitledPanel(box, "Radio Buttons");
	}

	// Create a pane with a slider for the gallery. The progress bar,
	// slider, and spinner show the same value from the model, so stay synced.
	private JPanel	createSlider()
	{
		DefaultBoundedRangeModel	model =
			new DefaultBoundedRangeModel(0, 0, 0, 100);

		slider = new JSlider(model);

		slider.setOrientation(SwingConstants.HORIZONTAL);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);

		return createTitledPanel(slider, "Slider");
	}

	// Create a pane with a spinner for the gallery. The progress bar,
	// slider, and spinner show the same value from the model, so stay synced.
	private JPanel	createSpinner()
	{
		SpinnerNumberModel	model = new SpinnerNumberModel(0, 0, 100, 1);

		spinner = new JSpinner(model);

		spinner.addChangeListener(this);

		return createTitledPanel(spinner, "Spinner");
	}

	// Create a pane with a text area for the gallery.
	private JPanel	createTextArea()
	{
		textArea = new JTextArea(4, 40);	// Set number of rows and columns

		textArea.addCaretListener(this);

		JScrollPane		sp = new JScrollPane(textArea,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		return createTitledPanel(sp, "Text Area");
	}

	// Create a pane with a text field for the gallery.
	private JPanel	createTextField()
	{
		textField = new JTextField(6);		// Set number of characters

		textField.addActionListener(this);

		return createTitledPanel(textField, "Text Field");
	}

	private JPanel	createToolBar()
	{
		ArrayList<String>	data =
			Resources.getLines("example/swing/text/tool-data.txt");

		toolBar = new JToolBar("ToolBar", SwingConstants.HORIZONTAL);

		//toolBar.setMargin(new Insets(4, 4, 4, 4));

		for (String item : data)
		{
			String[]		text = item.split(",");
			String			label = text[0];
			String			name = text[1];
			Icon			icon =
				Resources.getImage("example/swing/icon/" + name);
			AbstractAction	action = new AbstractAction(label, icon)
				{
					private static final long	serialVersionUID = 0L;

					public void	actionPerformed(ActionEvent e)
					{
						controller.trigger((String)getValue(Action.NAME));
					}
				};

			JButton	button = new JButton(action);

			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setHorizontalTextPosition(SwingConstants.CENTER);

			button.setVerticalAlignment(SwingConstants.CENTER);
			button.setVerticalTextPosition(SwingConstants.BOTTOM);

			// Toolbars can take any kind of component. Usually buttons.
			toolBar.add(button);
		}

		return createTitledPanel(toolBar, "ToolBar");
	}

	//**********************************************************************
	// Inner Classes
	//**********************************************************************

	private static final class MyCellRenderer extends JLabel
		implements ListCellRenderer<Object>
	{
		private static final long	serialVersionUID = 0L;

		private final boolean	showIcon;	// For combobox, or list?

		public MyCellRenderer(boolean showIcon)
		{
			this.showIcon = showIcon;

			setOpaque(true);
		}

		// Lists, comboboxes, tables, and trees render their cells by setting
		// the style characteristics of the same hidden JLabel over and over.
		public Component	getListCellRendererComponent(JList<?> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			String		item = value.toString();
			String[]	text = item.split(",");
			String		label = text[0];
			String		name = text[1];

			setText(label);

			if (showIcon)
			{
				Icon	icon = Resources.getImage("example/swing/icon/" + name);

				setIcon(icon);
				setIconTextGap(2);
			}

			setHorizontalAlignment(SwingConstants.LEFT);
			setHorizontalTextPosition(SwingConstants.TRAILING);

			setVerticalAlignment(SwingConstants.CENTER);
			setVerticalTextPosition(SwingConstants.CENTER);

			if (isSelected)
				setBackground(Color.LIGHT_GRAY);	// Highlight selected cells
			else
				setBackground(Color.WHITE);

			setForeground(Color.BLACK);

			// Make the text in the list larger than in the combobox
			int	size = (showIcon ? 24 : 16);

			setFont(new Font(Font.SERIF, Font.ITALIC, size));

			return this;	// <-- the same JLabel over and over!
		}
	}
}

//******************************************************************************
