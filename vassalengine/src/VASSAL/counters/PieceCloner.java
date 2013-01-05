/*
 * $Id$
 *
 * Copyright (c) 2004-2008 by Rodney Kinney, Joel Uckelman
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

import VASSAL.build.GameModule;
import VASSAL.build.GameModuleRetriever;
import VASSAL.build.GameModuleRetrieverSingleton;
import VASSAL.build.module.IMap;
import VASSAL.command.AddPiece;
import VASSAL.tools.ReflectionUtils;

/**
 * Utility class for cloning {@link GamePiece}s
 */
public class PieceCloner implements IPieceCloner {
  
  private final GameModuleRetriever gameModuleRetriever;
  
  private static IPieceCloner instance = new PieceCloner();

  /**
   * Introduces the use of {@link GameModuleRetrieverSingleton}
   * as the {@link GameModuleRetriever}.
   *  For use by subclasses
   */
  protected PieceCloner() 
  {
	  gameModuleRetriever = new GameModuleRetrieverSingleton();
  }
  
  /**
   * @param gameModuleRetriever Tool to use for retrieving the game module.
   */
  PieceCloner(GameModuleRetriever gameModuleRetriever)
  {
	this.gameModuleRetriever = gameModuleRetriever;  
  }
  
  public static IPieceCloner getInstance() {
    return instance;
  }

  /* (non-Javadoc)
 * @see VASSAL.counters.IPieceCloner#clonePiece(VASSAL.counters.GamePiece)
 */
  public GamePiece clonePiece(GamePiece piece) {
    GamePiece clone = null;
	if (piece instanceof BasicPiece) {
	  GameModule gameModule = gameModuleRetriever.getGameModule();
      clone = gameModule.createPiece(piece.getType());
      final IMap m = piece.getMap();

      // Temporarily set map to null so that clone won't be added to map
      piece.setMap(null);

      clone.setState(piece.getState());
      piece.setMap(m);
    }
    else if (piece instanceof UsePrototype) {
      clone = clonePiece(((UsePrototype)piece).getExpandedInner());
    }
    else if (piece instanceof EditablePiece && piece instanceof Decorator) {
      try {
        clone = piece.getClass().getConstructor().newInstance();
        ((Decorator)clone).setInner(clonePiece(((Decorator)piece).getInner()));
        ((EditablePiece)clone).mySetType(((Decorator)piece).myGetType());
        ((Decorator)clone).mySetState(((Decorator)piece).myGetState());
      }
      catch (Throwable t) {
        ReflectionUtils.handleNewInstanceFailure(t, piece.getClass());
      }
    }
    else {
    	AddPiece newPiece = new AddPiece(piece);
    	GameModule gameModule = gameModuleRetriever.getGameModule();
    	String encodedNewPiece = gameModule.encode(newPiece);
    	clone = ((AddPiece) gameModule.decode(
    			encodedNewPiece)).getTarget();
    	final IMap m = piece.getMap();

      // Temporarily set map to null so that clone won't be added to map
      piece.setMap(null);

      clone.setState(piece.getState());
      piece.setMap(m);
    }
    return clone;
  }
}
