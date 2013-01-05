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
package VASSAL.build;

/**
 * Retrieve the value of the GameModule that is passed in during
 * construction time.
 */
public class GameModuleRetrieverMemberVariable implements GameModuleRetriever {

	private final GameModule gameModule;
	
	public GameModuleRetrieverMemberVariable(GameModule gameModule) {
		this.gameModule = gameModule;
	}

	public GameModule getGameModule() {
		return gameModule;
	}

}
