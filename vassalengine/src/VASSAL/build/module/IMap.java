/*
 * $Id$
 *
 * Copyright (c) 2000-2012 by Rodney Kinney, Joel Uckelman, Brent Easton
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


package VASSAL.build.module;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import VASSAL.build.Buildable;
import VASSAL.build.Configurable;
import VASSAL.build.module.map.Drawable;
import VASSAL.build.module.map.PieceCollection;
import VASSAL.build.module.map.StackMetrics;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.Region;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.build.module.properties.MutablePropertiesContainer;
import VASSAL.build.module.properties.PropertySource;
import VASSAL.command.Command;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Highlighter;
import VASSAL.counters.PieceFinder;
import VASSAL.counters.PieceVisitorDispatcher;
import VASSAL.tools.ToolBarComponent;
import VASSAL.tools.UniqueIdManager;

/**
 * The Map is the main component for displaying and containing {@link GamePiece}s during play. Pieces are displayed on
 * a Map and moved by clicking and dragging. Keyboard events are forwarded to selected pieces. Multiple map windows are
 * supported in a single game, with dragging between windows allowed.
 *
 * A Map may contain many different {@link Buildable} subcomponents. Components which are added directly to a Map are
 * contained in the <code>VASSAL.build.module.map</code> package
 */
public interface IMap 
extends GameComponent, MouseListener, MouseMotionListener, DropTargetListener, Configurable,
UniqueIdManager.Identifyable, ToolBarComponent, MutablePropertiesContainer, 
PropertySource, PlayerRoster.SideChangeListener
{

	/**
	 * Removes a piece from the map
	 */
	public abstract void removePiece(GamePiece p);

	/**
	 * Make a best guess for a unique identifier for the target. Use
	 * {@link VASSAL.tools.UniqueIdManager.Identifyable#getConfigureName} if non-null, otherwise use
	 * {@link VASSAL.tools.UniqueIdManager.Identifyable#getId}
	 *
	 * @return
	 */
	public abstract String getIdentifier();

	/**
	 * @return the {@link Board} on this map containing the argument point
	 */
	public abstract Board findBoard(Point p);

	/**
	 * Every map must include a {@link StackMetrics} as one of its build
	 * components, which governs the stacking behavior of GamePieces on the map
	 *
	 * @return the StackMetrics for this map
	 */
	public abstract StackMetrics getStackMetrics();

	public abstract String getMapName();

	/**
	 * Return the auto-move key. It may be named, so just return
	 * the allocated KeyStroke.
	 * @return auto move keystroke
	 */
	public abstract KeyStroke getMoveKey();

	public abstract PieceCollection getPieceCollection();

	/**
	 * @return an array of all GamePieces on the map. This is a read-only copy.
	 * Altering the array does not alter the pieces on the map.
	 */
	public abstract GamePiece[] getPieces();

	/**
	 * Returns the index of a piece. When painting the map, pieces are drawn in order of index Return -1 if the piece is
	 * not on this map
	 */
	public abstract int indexOf(GamePiece s);

	/**
	 * Center the map at given map coordinates within its JScrollPane container
	 */
	public abstract void centerAt(Point p);

	public abstract void repaint();

	/**
	 * Place a piece at the destination point. If necessary, remove the piece from its parent Stack or Map
	 *
	 * @return a {@link Command} that reproduces this action
	 */
	public abstract Command placeAt(GamePiece piece, Point pt);

	/**
	 * Apply the provided {@link PieceVisitorDispatcher} to all pieces on this map. Returns the first non-null
	 * {@link Command} returned by <code>commandFactory</code>
	 *
	 * @param commandFactory
	 *
	 */
	public abstract Command apply(PieceVisitorDispatcher commandFactory);

	/**
	 * Returns the selection bounding box of a GamePiece accounting for the offset of a piece within a stack
	 *
	 * @see GamePiece#getShape
	 */
	public abstract Rectangle selectionBoundsOf(GamePiece p);

	/** Ensure that the given region (in map coordinates) is visible */
	public abstract void ensureVisible(Rectangle r);

	/**
	 * Returns the boundingBox of a GamePiece accounting for the offset of a piece within its parent stack. Return null if
	 * this piece is not on the map
	 *
	 * @see GamePiece#boundingBox
	 */
	public abstract Rectangle boundingBoxOf(GamePiece p);

	/**
	 *
	 * @return the {@link Zone} on this map containing the argument point
	 */
	public abstract Zone findZone(Point p);
	
	  /**
	   * Search on all boards for a Zone with the given name
	   * @param Zone name
	   * @return Located zone
	   */
	  public abstract Zone findZone(String name);

	public abstract Highlighter getHighlighter();

	public String getLocalizedConfigureName();

	/** @return the Swing component representing the map */
	public abstract JComponent getView();

	public abstract String localizedLocationName(Point p);

	/**
	 * @return a String name for the given location on the map
	 *
	 * @see Board#locationName
	 */
	public abstract String locationName(Point p);

	/**
	 * Move a piece to the destination point. If a piece is at the point (i.e. has a location exactly equal to it), merge
	 * with the piece by forwarding to {@link StackMetrics#merge}. Otherwise, place by forwarding to placeAt()
	 *
	 * @see StackMetrics#merge
	 */
	public abstract Command placeOrMerge(final GamePiece p, final Point pt);

	/**
	 * Repaint the given area, specified in map coordinates
	 */
	public abstract void repaint(Rectangle r);

	  /**
	   * Translate a point from map coordinates to component coordinates
	   *
	   * @see #mapCoordinates
	   */
	public abstract Point componentCoordinates(Point p);

	public abstract String getChangeFormat();

	  /**
	   * @return the current zoom factor for the map
	   */
	  public abstract double getZoom();
	  
	  /**
	   * Translate a point from component coordinates (i.e., x,y position on
	   * the JPanel) to map coordinates (i.e., accounting for zoom factor).
	   *
	   * @see #componentCoordinates
	   */
	  public abstract Point mapCoordinates(Point p);
	  

	  /**
	   * @return the size of the map in pixels at 100% zoom,
	   * including the edge buffer
	   */
	  public abstract Dimension mapSize();
	  
	  /**
	   * Use the provided {@link PieceFinder} instance to locate a visible piece at the given location
	   */
	  public abstract GamePiece findPiece(Point pt, PieceFinder finder) ;
	  
	  /**
	   * Returns the position of a GamePiece accounting for the offset within a parent stack, if any
	   */
	  public abstract Point positionOf(GamePiece p);
	  
	  /**
	   * @return the nearest allowable point according to the {@link VASSAL.build.module.map.boardPicker.board.MapGrid} on
	   *         the {@link Board} at this point
	   *
	   * @see Board#snapTo
	   * @see VASSAL.build.module.map.boardPicker.board.MapGrid#snapTo
	   */
	  public abstract Point snapTo(Point p);
	  
	  /**
	   * Add a {@link Drawable} component to this map
	   *
	   * @see #paint
	   */
	  public abstract void addDrawComponent(Drawable theComponent);
	  
	  /**
	   * Because MouseEvents are received in component coordinates, it is inconvenient for MouseListeners on the map to have
	   * to translate to map coordinates. MouseListeners added with this method will receive mouse events with points
	   * already translated into map coordinates.
	   * addLocalMouseListenerFirst inserts the new listener at the start of the chain.
	   */
	  public abstract void addLocalMouseListener(MouseListener l);
	  
	  /**
	   * Save all current Key Listeners and remove them from the
	   * map. Used by Traits that need to prevent Key Commands
	   * at certain times.
	   */
	  public abstract void enableKeyListeners();
	  
	  /**
	   * Restore the previously disabled KeyListeners
	   */
	  public abstract void disableKeyListeners();
	  
	  /**
	   * Search on all boards for a Region with the given name
	   * @param Region name
	   * @return Located region
	   */
	  public abstract Region findRegion(String name);
	  
	  /**
	   * Return the board with the given name
	   *
	   * @param name
	   * @return null if no such board found
	   */
	  public abstract Board getBoardByName(String name);

	  /**
	   * MouseListeners on a map may be pushed and popped onto a stack. Only the top listener on the stack receives mouse
	   * events
	   */
	  public abstract void popMouseListener();
	  
	  /**
	   * MouseListeners on a map may be pushed and popped onto a stack.
	   * Only the top listener on the stack receives mouse events.
	   */
	  public abstract void pushMouseListener(MouseListener l);
	  
	  /**
	   * Remove a {@link Drawable} component from this map
	   *
	   * @see #paint
	   */
	  public abstract void removeDrawComponent(Drawable theComponent);
}