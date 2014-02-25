package org.isola.client;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.isola.client.GameApi;
import org.isola.client.IsolaPresenter;
import org.isola.client.IsolaLogic;
import org.isola.client.IsolaPresenter;
import org.isola.client.IsolaPresenter.View;
import org.isola.client.GameApi.Container;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.isola.client.IsolaPresenter.View;
import org.isola.client.GameApi.Container;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.SetTurn;
import org.isola.client.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class IsolaPresenterTest {

	/** The class under test. */
	  private IsolaPresenter isolaPresenter;
	  private final IsolaLogic isolaLogic = new IsolaLogic();
	  private View mockView;
	  private Container mockContainer;

	  private static final String PLAYER_ID = "playerId";
	  /* The entries used in the cheat game are:
	   *   isCheater:yes, W, B, M, claim, C0...C51
	   */
	  private static final String TURN = "turn";
	  private static final String R = "R";
	  private static final String W = "W"; 
	  private static final String B = "B"; 
	  private static final String G = "G"; 
	  private final int viewerId = GameApi.VIEWER_ID;
	  private final int rId = 11;
	  private final int gId = 12;
	  private final ImmutableList<Integer> playerIds = ImmutableList.of(rId, gId);
	  private final ImmutableMap<String, Object> rInfo =
	      ImmutableMap.<String, Object>of(PLAYER_ID, rId);
	  private final ImmutableMap<String, Object> gInfo =
	      ImmutableMap.<String, Object>of(PLAYER_ID, gId);
	  private final ImmutableList<Map<String, Object>> playersInfo =
	      ImmutableList.<Map<String, Object>>of(rInfo, gInfo);
	
	  /**
	   * The interesting states that I'll test. 
	   * all gameAPI state, not IsolaState
	   */
	   
	 
	  
	  private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();
	  Map<String, Object> initialState = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "---R---")
				.put("line1", "-------")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "---G---")
				.build();
	  Map<String, Object> normalState_redTurn = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "--XXX--")
				.put("line1", "--XR---")
				.put("line2", "--XX---")
				.put("line3", "--X----")
				.put("line4", "----GX-")
				.put("line5", "---XX--")
				.put("line6", "-------")
				.build();
	  Map<String, Object> normalState_greenTurn = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----R--")
				.put("line2", "---XX--")
				.put("line3", "--X-X--")
				.put("line4", "----GX-")
				.put("line5", "---XX--")
				.put("line6", "-------")
				.build();
	  Map<String, Object> endState_redWin = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----R--")
				.put("line2", "---XX--")
				.put("line3", "--XXXX-")
				.put("line4", "---XGX-")
				.put("line5", "---XXX-")
				.put("line6", "-------")
				.build();
	
	
	  @Before
	  public void runBefore() {
	    mockView = Mockito.mock(View.class);
	    mockContainer = Mockito.mock(Container.class);
	    isolaPresenter = new IsolaPresenter(mockView, mockContainer);
	    verify(mockView).setPresenter(isolaPresenter);
	  }

	  @After
	  public void runAfter() {
	    // This will ensure I didn't forget to declare any extra interaction the mocks have.
	    verifyNoMoreInteractions(mockContainer);
	    verifyNoMoreInteractions(mockView);
	  }

	  @Test
	  public void testEmptyStateForR() {
	    isolaPresenter.updateUI(createUpdateUI(rId, 0, emptyState));
	    verify(mockContainer).sendMakeMove(isolaLogic.getMoveInitial(playerIds));
	  }
	  
	  @Test
	  public void testEmptyStateForG() {
	    isolaPresenter.updateUI(createUpdateUI(gId, 0, emptyState));
	  }
	  
	  @Test
	  public void testEmptyStateForViewer() {
	    isolaPresenter.updateUI(createUpdateUI(viewerId, 0, emptyState));
	  }
	  
	  @Test
	  public void testNormalStateForR() {
	    isolaPresenter.updateUI(createUpdateUI(rId, rId, normalState_redTurn));
	   
	    verify(mockView).setPlayerState(normalState_redTurn);
	    verify(mockView).selectPiece(Color.R);
	    
	    isolaPresenter.pieceSelected(new Position(1,3));
	    
	    List<Position> positions = Lists.newArrayList();
	    positions.add(new Position(1,4));
	    positions.add(new Position(2,4));
	    
	    verify(mockView).selectMovePosition(positions);

	  }
	  
	  @Test
	  public void testNormalStateForG() {
	    isolaPresenter.updateUI(createUpdateUI(gId, gId, normalState_greenTurn));
	    verify(mockView).setPlayerState(normalState_greenTurn);
	    verify(mockView).selectPiece(Color.G);
	  }
	  
	  
	  
	  
	  
	  private UpdateUI createUpdateUI(int yourPlayerId, int turnOfPlayerId, Map<String, Object> state) {
		    // Our UI only looks at the current state
		    // (we ignore: lastState, lastMovePlayerId, playerIdToNumberOfTokensInPot)
		    return new UpdateUI(yourPlayerId, playersInfo, state,
		        emptyState, // we ignore lastState
		        ImmutableList.<Operation>of(new SetTurn(turnOfPlayerId)),
		        0,
		        ImmutableMap.<Integer, Integer>of());
		  }

}
