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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.KeyStroke;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import VASSAL.build.module.IMap;
import VASSAL.build.module.map.StackMetrics;
import VASSAL.command.Command;
import VASSAL.command.NullCommand;

public class KeyBufferTest 
{
	/** 
	 * Return the command, and all its sub-commands (recursively).
	 * @param command Command to retrieve sub-commands from.
	 * @param result Location to add commands to.
	 */
	private void flatten(Command command, List<Command> result)
	{
		result.add(command);
		Command[] subCommands = command.getSubCommands();
		for (Command sub : subCommands) {
			flatten(sub, result);
		}
	}
	
	/** 
	 * Return the command, and all its sub-commands (recursively).
	 * @param command Command to retrieve sub-commands from.
	 * @return All the mentioned commands.
	 */
	private Command[] flatten(Command command)
	{
		ArrayList<Command> results = new ArrayList<Command>(4);
		flatten(command, results);
		return results.toArray(new Command[0]);
	}
	
	
	/** 
	 * Verify that a command and its sub-commands (recursively) equal
	 * the expected commands.
	 * @param expected Commands that are to be found.
	 * @param actual Command that is to be compared. 
	 */
	private void asserEquals(
			Command expected[],
			Command actual) 
	{
		Command[] actualCommands = flatten(actual);
		assertEquals(expected.length, actualCommands.length );
		for (int i=0; i<expected.length; ++i) {
			assertEquals(expected[i], actualCommands[i]);
		}
	}
	
	class MockPieceSorterRetriever implements PieceSorterRetriever
	{
		public Comparator<GamePiece> getPieceSorter() {
			return new Comparator<GamePiece>() {

				public int compare(GamePiece o1, GamePiece o2) {
					MockGamePiece m1 = (MockGamePiece) o1;
					MockGamePiece m2 = (MockGamePiece) o2;
					String i1 = m1.getState();
					String i2 = m2.getState();
					return i1.compareTo(i2);
				}};
		}		
	}
	
	class MockGamePiece implements GamePiece
	{
		final String id;
		final IMap map;
		String state = "before";
		
		
		public MockGamePiece(String id, IMap map) {
			super();
			this.id = id;
			this.map = map;
		}

		public Object getLocalizedProperty(Object key) {
			throw new Error("Method should not be called in test");
		}

		public void setMap(IMap map) {
			throw new Error("Method should not be called in test");
		}

		public IMap getMap() {
			return map;
		}

		public void draw(Graphics g, int x, int y, Component obs, double zoom) {
			throw new Error("Method should not be called in test");
		}

		public Point getPosition() {
			throw new Error("Method should not be called in test");
		}

		public void setPosition(Point p) {
			throw new Error("Method should not be called in test");
		}

		public Rectangle boundingBox() {
			throw new Error("Method should not be called in test");
		}

		public Shape getShape() {
			throw new Error("Method should not be called in test");
		}

		public Stack getParent() {
			throw new Error("Method should not be called in test");
		}

		public void setParent(Stack s) {
			throw new Error("Method should not be called in test");
		}

		public Command myExecute(KeyCommand keyCommand) {
			throw new Error("Method should not be called in test");
		}

		public Command groupKeyEvent(KeyStroke stroke, List<GamePiece> targets) {
			return null;
		}

		public Command keyEvent(KeyStroke stroke) {
			state = "after";
			return new NullCommand();
		}

		public String getName() {
			throw new Error("Method should not be called in test");
		}

		public String getLocalizedName() {
			throw new Error("Method should not be called in test");
		}

		public String getId() {
			throw new Error("Method should not be called in test");
		}

		public void setId(String id) {
			throw new Error("Method should not be called in test");
		}

		public String getType() {
			throw new Error("Method should not be called in test");
		}

		public String getState() {
			return this.id + state;
		}

		public void setState(String newState) {
			throw new Error("Method should not be called in test");
		}

		public void setProperty(Object key, Object val) {
		}

		public Object getProperty(Object key) {
			throw new Error("Method should not be called in test");
		}

		public List<String> getPropertyNames() {
			throw new Error("Method should not be called in test");
		}
		
	}
	
	class MockPieceCloner implements IPieceCloner
	{

		public GamePiece clonePiece(GamePiece piece) {
			return piece;
		}
		
	}
	
	class MockPieceClonerRetriever implements PieceClonerRetriever
	{
		MockPieceCloner cloner = new MockPieceCloner();
		
		public IPieceCloner getPieceCloner() {
			return cloner;
		}
		
	}
	
	class MockBoundsTracker implements IBoundsTracker
	{
		Vector<String> pieces = new Vector<String>();
		
		public void repaint() {
		}
		
		public void clear() {
		}
		
		public void addPiece(GamePiece p) {
			MockGamePiece gamePiece = (MockGamePiece) p;
			pieces.add(gamePiece.getState());
		}			
	}

	
	@Test
	public void testSelectedGamePiecesAreAddedToBoundsBeforeTheGamePieceChanged()
	{		
		// Setup 
		final Mockery context = new Mockery();
		final IMap map = context.mock(IMap.class);
		context.checking(new Expectations() {{
			StackMetrics stackMetrics = new StackMetrics();
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		}});
		
		MockBoundsTracker boundsChecker = new MockBoundsTracker();
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		PieceSorterRetriever pieceSorterRetriever = new MockPieceSorterRetriever();
		KeyBuffer sut = new KeyBuffer(boundsChecker, pieceClonerRetriever, pieceSorterRetriever);
		GamePiece gamePiece1 = new MockGamePiece("1", map);
		GamePiece gamePiece2 = new MockGamePiece("2", map);
		sut.add(gamePiece1);
		sut.add(gamePiece2);
	    javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
		
		// Exercise
		sut.keyCommand(stroke);
		
		// Verify
		assertTrue(boundsChecker.pieces.contains("1before"));
		assertTrue(boundsChecker.pieces.contains("2before"));
	}
	
	@Test
	public void testSelectedGamePiecesAreAddedToBoundsAfterTheGamePieceChanged()
	{		
		// Setup 
		final Mockery context = new Mockery();
		final IMap map = context.mock(IMap.class);
		context.checking(new Expectations() {{
			StackMetrics stackMetrics = new StackMetrics();
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		}});
		
		MockBoundsTracker boundsChecker = new MockBoundsTracker();
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		PieceSorterRetriever pieceSorterRetriever = new MockPieceSorterRetriever();
		KeyBuffer sut = new KeyBuffer(boundsChecker, pieceClonerRetriever, pieceSorterRetriever);
		GamePiece gamePiece1 = new MockGamePiece("1", map);
		GamePiece gamePiece2 = new MockGamePiece("2", map);
		sut.add(gamePiece1);
		sut.add(gamePiece2);
	    javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
		
		// Exercise
		sut.keyCommand(stroke);
		
		// Verify
		assertTrue(boundsChecker.pieces.contains("1after"));
		assertTrue(boundsChecker.pieces.contains("2after"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandResultFromEachPieceIsReturnedByKeyCommand()
	{		
		// Setup
		final Mockery context = new Mockery();
		final GamePiece piece1 = context.mock(GamePiece.class, "piece1");
		final GamePiece piece2 = context.mock(GamePiece.class, "piece2");
		final IMap map = context.mock(IMap.class, "map");
		final BoundsTracker boundsTracker = new BoundsTracker();
		final Comparator<GamePiece> pieceSorter = new Comparator<GamePiece>() {

			public int compare(GamePiece o1, GamePiece o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1 == piece1) {
					return -1;
				}
				return 1;
			}
		};
		
		final PieceSorterRetriever pieceSorterRetriever = new PieceSorterRetriever()
		{

			public Comparator<GamePiece> getPieceSorter() {
				return pieceSorter;
			}
			
		};
		
		final StackMetrics stackMetrics = new StackMetrics();
	    final javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
	    final Command groupResult = null;
	    final Command piece1Result = new Command() {
			
			@Override
			protected Command myUndoCommand() {
				throw new Error("Method is not expected in test case");
			}
			
			@Override
			protected void executeCommand() {
				throw new Error("Method is not expected in test case");
			}
		};
	    final Command piece2Result = new Command() {
			
			@Override
			protected Command myUndoCommand() {
				throw new Error("Method is not expected in test case");
			}
			
			@Override
			protected void executeCommand() {
				throw new Error("Method is not expected in test case");
			}
		};
		context.checking(new Expectations() {{
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		    allowing(map).repaint();
		    allowing(piece1).setProperty("Selected", true);
		    allowing(piece2).setProperty("Selected", true);
		    allowing(piece1).getMap();
		    will(returnValue(map));
		    allowing(piece2).getMap();
		    will(returnValue(map));
		    allowing(piece1).setProperty("snapshot", piece1);
		    allowing(piece2).setProperty("snapshot", piece2);
		    allowing(piece2).groupKeyEvent(
		    		with(aNonNull(javax.swing.KeyStroke.class)), 
		    		with(aNonNull(List.class)));
		    will(returnValue(groupResult));
		    one(piece1).keyEvent(stroke);
		    will(returnValue(piece1Result));
		    one(piece2).keyEvent(stroke);
		    will(returnValue(piece2Result));
		}});
		
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		KeyBuffer sut = new KeyBuffer(boundsTracker, pieceClonerRetriever, pieceSorterRetriever);
		sut.add(piece1);
		sut.add(piece2);
		
		// Exercise
		Command actual = sut.keyCommand(stroke);
		
		// Verify
		Command[] expected = new Command[] { piece1Result, piece2Result};
		asserEquals(expected, actual);
	}	


	@Test
	public void testBoundsTrackerRepaintedAfterSelectedPiecesAdded()
	{		
		// Setup
		final Mockery context = new Mockery();
		final IMap map = context.mock(IMap.class);
		context.checking(new Expectations() {{
			StackMetrics stackMetrics = new StackMetrics();
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		}});
		
		class MockPaintingBoundsTracker extends MockBoundsTracker
		{
			int piecesRepainted = 0;
			
			@Override
			public void repaint() 
			{
				piecesRepainted = this.pieces.size();
			}
		}
		MockPaintingBoundsTracker boundsChecker = new MockPaintingBoundsTracker();
		
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		PieceSorterRetriever pieceSorterRetriever = new MockPieceSorterRetriever();
		KeyBuffer sut = new KeyBuffer(boundsChecker, pieceClonerRetriever, pieceSorterRetriever);
		GamePiece gamePiece1 = new MockGamePiece("1", map);
		GamePiece gamePiece2 = new MockGamePiece("2", map);
		sut.add(gamePiece1);
		sut.add(gamePiece2);
	    javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
		
		// Exercise
		sut.keyCommand(stroke);
		
		// Verify
		assertEquals(4, boundsChecker.piecesRepainted);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testKeyEventIsNotSentToOtherPiecesIfActionIsForGroup()
	{		
		// Setup
		final Mockery context = new Mockery();
		final GamePiece piece1 = context.mock(GamePiece.class, "piece1");
		final GamePiece piece2 = context.mock(GamePiece.class, "piece2");
		final IMap map = context.mock(IMap.class, "map");
		final BoundsTracker boundsTracker = new BoundsTracker();
		final Comparator<GamePiece> pieceSorter = new Comparator<GamePiece>() {

			public int compare(GamePiece o1, GamePiece o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1 == piece1) {
					return -1;
				}
				return 1;
			}
		};
		
		final PieceSorterRetriever pieceSorterRetriever = new PieceSorterRetriever()
		{

			public Comparator<GamePiece> getPieceSorter() {
				return pieceSorter;
			}
			
		};
		
		final StackMetrics stackMetrics = new StackMetrics();
	    final javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
	    final Command groupResult = new NullCommand();
		context.checking(new Expectations() {{
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		    allowing(map).repaint();
		    allowing(piece1).setProperty("Selected", true);
		    allowing(piece2).setProperty("Selected", true);
		    allowing(piece1).getMap();
		    will(returnValue(map));
		    allowing(piece2).getMap();
		    will(returnValue(map));
		    allowing(piece1).setProperty("snapshot", piece1);
		    allowing(piece2).setProperty(aNonNull(String.class), aNonNull(Object.class));
		    allowing(piece2).groupKeyEvent(
		    		with(aNonNull(javax.swing.KeyStroke.class)), 
		    		with(aNonNull(List.class)));
		    will(returnValue(groupResult));
		    never(piece1).keyEvent(with(aNonNull(javax.swing.KeyStroke.class)));
		    never(piece1).keyEvent(with(aNonNull(javax.swing.KeyStroke.class)));
		}});
		
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		KeyBuffer sut = new KeyBuffer(boundsTracker, pieceClonerRetriever, pieceSorterRetriever);
		sut.add(piece1);
		sut.add(piece2);
		
		// Exercise
		sut.keyCommand(stroke);
		
		// Verify
		// If we got here then we never called keyEvent on the game pieces.
		assertTrue(true);
	}

	@Test
	public void tesKeyCommandDoesNotThrowExceptionIfNoPiecesAreSelected()
	{		
		// Setup
		final Mockery context = new Mockery();
		final IMap map = context.mock(IMap.class, "map");
		final BoundsTracker boundsTracker = new BoundsTracker();
		final Comparator<GamePiece> pieceSorter = new Comparator<GamePiece>() {

			public int compare(GamePiece o1, GamePiece o2) {
				throw new Error("Method should not be called in this test case");
			}
		};
		
		final PieceSorterRetriever pieceSorterRetriever = new PieceSorterRetriever()
		{

			public Comparator<GamePiece> getPieceSorter() {
				return pieceSorter;
			}
			
		};
		
		final StackMetrics stackMetrics = new StackMetrics();
	    final javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
		context.checking(new Expectations() {{
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		    allowing(map).repaint();
		}});
		
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		KeyBuffer sut = new KeyBuffer(boundsTracker, pieceClonerRetriever, pieceSorterRetriever);
		
		// Exercise
		sut.keyCommand(stroke);
		
		// Verify
		// If we got here then we never called keyEvent on the game pieces.
		assertTrue(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCommandResultFromGroupIsReturnedByKeyCommand()
	{		
		// Setup
		final Mockery context = new Mockery();
		final GamePiece piece1 = context.mock(GamePiece.class, "piece1");
		final GamePiece piece2 = context.mock(GamePiece.class, "piece2");
		final IMap map = context.mock(IMap.class, "map");
		final BoundsTracker boundsTracker = new BoundsTracker();
		final Comparator<GamePiece> pieceSorter = new Comparator<GamePiece>() {

			public int compare(GamePiece o1, GamePiece o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1 == piece1) {
					return -1;
				}
				return 1;
			}
		};
		
		final PieceSorterRetriever pieceSorterRetriever = new PieceSorterRetriever()
		{

			public Comparator<GamePiece> getPieceSorter() {
				return pieceSorter;
			}
			
		};
		
		final StackMetrics stackMetrics = new StackMetrics();
	    final javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke('l');
	    final Command groupResult = new NullCommand();
		context.checking(new Expectations() {{
		    allowing (map).getStackMetrics();
		    will(returnValue(stackMetrics));
		    allowing(map).repaint();
		    allowing(piece1).setProperty("Selected", true);
		    allowing(piece2).setProperty("Selected", true);
		    allowing(piece1).getMap();
		    will(returnValue(map));
		    allowing(piece2).getMap();
		    will(returnValue(map));
		    allowing(piece1).setProperty("snapshot", piece1);
		    allowing(piece2).setProperty(aNonNull(String.class), aNonNull(Object.class));
		    allowing(piece2).groupKeyEvent(
		    		with(aNonNull(javax.swing.KeyStroke.class)), 
		    		with(aNonNull(List.class)));
		    will(returnValue(groupResult));
		    never(piece1).keyEvent(with(aNonNull(javax.swing.KeyStroke.class)));
		    never(piece1).keyEvent(with(aNonNull(javax.swing.KeyStroke.class)));
		}});
		
		PieceClonerRetriever pieceClonerRetriever = new MockPieceClonerRetriever();
		KeyBuffer sut = new KeyBuffer(boundsTracker, pieceClonerRetriever, pieceSorterRetriever);
		sut.add(piece1);
		sut.add(piece2);
		
		// Exercise
		Command actual = sut.keyCommand(stroke);
		
		// Verify
		assertSame(groupResult, actual);
	}

}
