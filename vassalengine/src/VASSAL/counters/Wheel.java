/*
 * $Id$
 *
 * Copyright (c) 2004 by Rodney Kinney
 * Copyright (c) 2012 by Marc Pawlowsky
 * Copyright (c) 2013 by Marc Pawlowsky
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package VASSAL.counters;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.command.ChangeTracker;
import VASSAL.command.Command;
import VASSAL.command.NullCommand;
import VASSAL.configure.NamedHotKeyConfigurer;
import VASSAL.configure.StringConfigurer;
import VASSAL.i18n.PieceI18nData;
import VASSAL.i18n.TranslatablePiece;
import VASSAL.tools.NamedKeyStroke;
import VASSAL.tools.SequenceEncoder;

/**
 * Provides commands to wheel a Game Piece around a given point
 */
public class Wheel extends Decorator implements TranslatablePiece {
	
  protected class WheelKeyCommand extends KeyCommand
  {
	  /**
	 * 
	 */
	private static final long serialVersionUID = -4029473204914188946L;

	/** 
	 * Index of the co-ordinates in the game pieces shape of the vertix
	 * that will be pivoted on.
	 */
	private final int pivotIndex;
	
	/**
	 * Name of the command that rotates the game piece.
	 */
	private final String pivotCommandName;
	  
	  public WheelKeyCommand(
			  String command, 
			  NamedKeyStroke key,
			  GamePiece outermost, 
			  Wheel wheel, 
			  int pivotIndex,
			  String commandName) 
	  {
		  super(command, key, outermost, wheel);
		  this.pivotIndex = pivotIndex;
		  this.pivotCommandName = commandName;
	  }

	/** 
	   * Index of the co-ordinates in the shape of the game piece
	   * where the will will take place on.
	   * 2=left front corner
	   * 1=right front corner
	   * @return index to use.
	   */
	  int getPivotPointIndex()
	  {
		  return pivotIndex;
	  }
	  
	  /**
	   * Return the name of the command that is implemented by another
	   * decorator, or the game piece, that performs the rotation
	   * of the game piece.
	   * @return Name of the command.
	   */
	  String getPivotCommandName()
	  {
		  return this.pivotCommandName;
	  }
  }
  
  public static final String ID = "wheel;";
  
  /** Description of the left wheel command in the menus. */
  protected String leftCommand;
  /** Description of the right wheel command in the menus. */
  protected String rightCommand;
  /** Key stroke for the left wheel command. */
  protected NamedKeyStroke leftKey;
  /** Key stroke for the right wheel command. */
  protected NamedKeyStroke rightKey;
  /** Name of the command that rotates the game pieces left. */
  protected String leftRotateCommand;
  /** Name of the command that rotates the game pieces right. */
  protected String rightRotateCommand;

  /** Command for wheeling left. */
  WheelKeyCommand leftWheelKeyCommand;
  /** Command for wheeling right. */
  WheelKeyCommand rightWheelKeyCommand;
  /** All the commands implemented by this decorator. */
  protected KeyCommand[] commands;

  public Wheel() {
    this(ID, null);
  }

  public Wheel(String type, GamePiece inner) {
    mySetType(type);
    setInner(inner);
  }

  public String getDescription() {
    return "Can Wheel";
  }

  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("Wheel.htm");
  }

  public void mySetType(String type) {
    type = type.substring(ID.length());
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(type, ';');
    leftCommand = st.nextToken("Left Wheel");
    rightCommand = st.nextToken("Right Wheel");
    leftKey = st.nextNamedKeyStroke(null);
    rightKey = st.nextNamedKeyStroke(null);
    leftRotateCommand = st.nextToken("");
    rightRotateCommand = st.nextToken("");
    commands = null;
  }

  protected KeyCommand[] myGetKeyCommands() {
    if (commands == null) {
      leftWheelKeyCommand = new WheelKeyCommand(
    		  leftCommand, leftKey, Decorator.getOutermost(this), this, 
    		  2, this.leftRotateCommand);
      rightWheelKeyCommand = new WheelKeyCommand(
    		  rightCommand, rightKey, Decorator.getOutermost(this), this, 
    		  1, this.rightRotateCommand);
      leftWheelKeyCommand.setEnabled(true);
      rightWheelKeyCommand.setEnabled(true);
	  commands = new KeyCommand[]{leftWheelKeyCommand, rightWheelKeyCommand};
    }
    return commands;
  }

  public String myGetState() {
    return "";
  }

  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(leftCommand)
      .append(rightCommand)
      .append(leftKey)
      .append(rightKey)
      .append(leftRotateCommand)
      .append(rightRotateCommand);
    return ID + se.getValue();
  }

  private KeyCommand getPivotKeyCommand(WheelKeyCommand wheelKeyCommand)
  {
	  String name = wheelKeyCommand.getPivotCommandName();
	  
	  KeyCommand[] commands = this.getKeyCommands();
	  for (KeyCommand command : commands) {
		  if (command.getName().equals(name)) {
			  return command;
		  }
	  }
	  return null;
  }
  
  /**
   * Perform the wheel for the game piece.
   * @param wheelKeyCommand Command that was selected to perform the wheel.
   * @return Command that is the result of the wheel.
   * 
   * A wheel is a pivot around a corner.  The implementation is done
   * in two steps, a pivot, and then a translation so the corner that
   * was being pivoted is moved as little as possible.
   */
  private Command wheel(WheelKeyCommand wheelKeyCommand)
  {
	  Point2D originalWheelPoint = this.getPivotPoint(wheelKeyCommand);
	  KeyCommand pivotKeyCommand = getPivotKeyCommand(wheelKeyCommand);
	  Command rotatePart=this.execute(pivotKeyCommand);
	  Point2D newWheelPoint = this.getPivotPoint(wheelKeyCommand);
	  double xDiff = newWheelPoint.getX() - originalWheelPoint.getX();
	  double yDiff = newWheelPoint.getY() - originalWheelPoint.getY();
      ChangeTracker tracker = new ChangeTracker(this);
      Point position = this.getPosition();
      position.x += (int) Math.round(xDiff);
      position.y += (int) Math.round(yDiff);
      this.setPosition(position);
      Command movePart = tracker.getChangeCommand();
	  Command result = new NullCommand();
	  result.append(rotatePart);
	  result.append(movePart);
	  return result;
  }
  

  @Override
  public Command myExecute(KeyCommand keyCommand) 
  {
	  for (KeyCommand command : this.myGetKeyCommands()) {
		  if (command == keyCommand) {
			  WheelKeyCommand rotateKeyCommand = (WheelKeyCommand) keyCommand;
			  return this.wheel(rotateKeyCommand);
		  }
	  }
	  return null;
  }

  public Command myKeyEvent(KeyStroke stroke) {
    Command c = null;
    for (KeyCommand keyCommand : myGetKeyCommands()) {
    	if (keyCommand.matches(stroke)) {
    		c = myExecute(keyCommand);
    		break;
    	}
    }
    
    // Apply map auto-move key
    if (c != null && getMap() != null && getMap().getMoveKey()!= null) {
      c.append(Decorator.getOutermost(this).keyEvent(getMap().getMoveKey()));
    }
    Command result = new NullCommand();
    result.append(c);
    return result;
  }

  /**
   * Retrieve the offset from the game piece center point, of
   * the corner that is being rotated.
   *
   * @param rotateCommand Command indicating which wheel to do.
   * @return Location of the corner of the game piece.
   */
  private Point2D getPivotPoint(WheelKeyCommand rotateCommand)
  {
	  int index = rotateCommand.getPivotPointIndex();
	  
	  Shape shape = this.getShape();
	  PathIterator pathIterator = shape.getPathIterator(null);
	  for (int i=0; i<index;++i) {
		  pathIterator.next();
	  }
	  double[] coords = new double[2];
	  pathIterator.currentSegment(coords);
	  Point2D point = new Point2D.Double(coords[0], coords[1]);
	  return point;
  }
 

  public void mySetState(String newState) {
  }

  public Rectangle boundingBox() {
    return getInner().boundingBox();
  }

  public void draw(Graphics g, int x, int y, Component obs, double zoom) {
    getInner().draw(g, x, y, obs, zoom);
  }

  public String getName() {
    return getInner().getName();
  }

  public Shape getShape() {
    return getInner().getShape();
  }

  public PieceEditor getEditor() {
    return new Ed(this);
  }

  public PieceI18nData getI18nData() {
    return getI18nData("", "Wheel command");
  }


  public static class Ed implements PieceEditor {
    private StringConfigurer leftCommand;
    private StringConfigurer rightCommand;
    private NamedHotKeyConfigurer leftKey;
    private NamedHotKeyConfigurer rightKey;
    private String leftRotateCommand;
	private String rightRotateCommand;
    private JPanel controls;
    public Ed(Wheel p) {
    	controls = new JPanel();
    	controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));



    	KeyCommand[] commands = p.getKeyCommands();
    	String[] commandStrings = new String[commands.length];
    	for (int i=0; i< commands.length; ++i) {
    		commandStrings[i] = commands[i].getName();
    	}
    	JLabel left = new JLabel("Left Wheel", JLabel.CENTER);
    	controls.add(left);
    	leftCommand = new StringConfigurer(null, "Command:  ", p.leftCommand);
    	controls.add(leftCommand.getControls());    	
    	Box wheelLeftCommandBox = Box.createHorizontalBox();
    	JLabel wheelLeftLabel = new JLabel("Rotation Command:", JLabel.LEFT);
    	wheelLeftCommandBox.add(wheelLeftLabel);
    	final JComboBox wheelLeftComboBox = new JComboBox(commandStrings);
    	wheelLeftComboBox.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    			leftRotateCommand = (String) wheelLeftComboBox.getSelectedItem();
    		}
    	});
    	wheelLeftComboBox.setSelectedItem(p.leftRotateCommand);
    	wheelLeftCommandBox.add(wheelLeftComboBox);
    	controls.add(wheelLeftCommandBox);
       	leftKey = new NamedHotKeyConfigurer(null, "Keyboard command:  ", p.leftKey);
       	controls.add(leftKey.getControls());

    	JLabel right = new JLabel("Right Wheel", JLabel.CENTER);
    	controls.add(right);
    	rightCommand = new StringConfigurer(null, "Command:  ", p.rightCommand);
    	controls.add(rightCommand.getControls());    	
    	Box wheelRightCommandBox = Box.createHorizontalBox();
    	JLabel wheelRightLabel = new JLabel("Rotation Command:", JLabel.LEFT);
    	final JComboBox wheelRightComboBox = new JComboBox(commandStrings);
    	wheelRightComboBox.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    			rightRotateCommand = (String) wheelRightComboBox.getSelectedItem();
    		}
    	});
    	wheelRightComboBox.setSelectedItem(p.rightRotateCommand);
    	wheelRightCommandBox.add(wheelRightLabel);
    	wheelRightCommandBox.add(wheelRightComboBox);
    	controls.add(wheelRightCommandBox);
    	rightKey = new NamedHotKeyConfigurer(null, "Keyboard command:  ", p.rightKey);
    	controls.add(rightKey.getControls());
    }

    public Component getControls() {
      return controls;
    }

    public String getState() {
      return "";
    }

    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se
          .append(leftCommand.getValueString())
          .append(rightCommand.getValueString())
          .append(leftKey.getValueString())
          .append(rightKey.getValueString())
          .append(leftRotateCommand)
          .append(rightRotateCommand);
      return ID + se.getValue();
    }
  }
}
