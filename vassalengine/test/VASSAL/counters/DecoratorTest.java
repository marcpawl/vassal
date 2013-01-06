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
 */package VASSAL.counters;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.*;

import javax.swing.KeyStroke;

import VASSAL.command.Command;


public class DecoratorTest 
{
	private void propertyNameInListTest(String propertyName)
	{
		// Setup
		GamePiece piece = new BasicPiece();
		Decorator sut = new Decorator() {
			
			public Shape getShape() {
				throw new Error("Method should not be called in test case");
			}
			
			public String getName() {
				throw new Error("Method should not be called in test case");
			}
			
			public void draw(Graphics g, int x, int y, Component obs, double zoom) {
				throw new Error("Method should not be called in test case");
			}
			
			public Rectangle boundingBox() {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public void mySetState(String newState) {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public Command myKeyEvent(KeyStroke stroke) {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public String myGetType() {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public String myGetState() {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			protected KeyCommand[] myGetKeyCommands() {
				throw new Error("Method should not be called in test case");
			}
		};
		sut.setInner(piece);
		
		// Exercise
		List<String> names = sut.getPropertyNames();
		
		// Validate
		assertTrue(names.contains(propertyName));
	}
	
	@Test
	public void testKeyCommandsPropertyInNames()
	{
		this.propertyNameInListTest(Properties.KEY_COMMANDS);
	}
	
	@Test
	public void testInnerPropertyInNames()
	{
		this.propertyNameInListTest(Properties.INNER);
	}
	
	@Test
	public void testOuterPropertyInNames()
	{
		this.propertyNameInListTest(Properties.OUTER);
	}

	@Test
	public void testVisibleStatePropertyInNames()
	{
		this.propertyNameInListTest(Properties.VISIBLE_STATE);
	}


	@Test
	public void testPropertyNamesIncludeNamesFromInnerPiece()
	{
		// Setup
		GamePiece piece = new BasicPiece();
		Decorator sut = new Decorator() {
			
			public Shape getShape() {
				throw new Error("Method should not be called in test case");
			}
			
			public String getName() {
				throw new Error("Method should not be called in test case");
			}
			
			public void draw(Graphics g, int x, int y, Component obs, double zoom) {
				throw new Error("Method should not be called in test case");
			}
			
			public Rectangle boundingBox() {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public void mySetState(String newState) {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public Command myKeyEvent(KeyStroke stroke) {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public String myGetType() {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			public String myGetState() {
				throw new Error("Method should not be called in test case");
			}
			
			@Override
			protected KeyCommand[] myGetKeyCommands() {
				throw new Error("Method should not be called in test case");
			}
		};
		sut.setInner(piece);
		
		// Exercise
		List<String> names = sut.getPropertyNames();
		
		// Validate
		List<String> pieceNames = piece.getPropertyNames();
		for (String pieceName : pieceNames) {
			assertTrue(
					"Piece name property not in property names: " + pieceName,
					names.contains(pieceName));
		}
	}
}
