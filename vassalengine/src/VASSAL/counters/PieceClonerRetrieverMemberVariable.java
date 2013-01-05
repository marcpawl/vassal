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
 * Returns the instance of {@link PieceCloner} that the retriever is
 * initialized with.
 */
public class PieceClonerRetrieverMemberVariable implements PieceClonerRetriever {

	private final IPieceCloner pieceCloner;
	
	
	public PieceClonerRetrieverMemberVariable(IPieceCloner pieceCloner) {
		this.pieceCloner = pieceCloner;
	}


	public IPieceCloner getPieceCloner() {
		return this.pieceCloner;
	}

}
