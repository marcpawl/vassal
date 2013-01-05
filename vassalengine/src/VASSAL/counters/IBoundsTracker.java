/*
 * $Id$
 *
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

/**
 * Records the bounding boxes of GamePieces.  Use addPiece() to
 * record the bounding box of a GamePiece at a certain time.  Use
 * repaint() to repaint the appropriate areas of the maps to which the
 * added pieces belonged.
 */
public interface IBoundsTracker {

	/** Remove all the game pieces that have been previously added. */
	public abstract void clear();

	/** 
	 * Indicate that a game piece has been modified.
	 * 
	 * @param p Game piece that has been modified.
	 */
	public abstract void addPiece(GamePiece p);

	/** Repaint the areas that have been modified. */
	public abstract void repaint();

}