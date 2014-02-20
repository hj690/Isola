package org.isola.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Map;


import org.isola.client.IsolaPresenter;
import org.isola.client.IsolaPresenter.View;
import org.isola.client.GameApi.Container;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
	  private static final String C = "C"; // Card i
	  private static final String W = "W"; // White hand
	  private static final String B = "B"; // Black hand
	  private static final String M = "M"; // Middle pile
	  private static final String CLAIM = "claim"; // a claim has the form: [3cards, rankK]
	  private static final String IS_CHEATER = "isCheater"; // we claim we have a cheater
	  private static final String YES = "yes"; // we claim we have a cheater
	  private final int viewerId = GameApi.VIEWER_ID;
	  private final int wId = 41;
	  private final int bId = 42;
	  private final ImmutableList<Integer> playerIds = ImmutableList.of(wId, bId);
	  private final ImmutableMap<String, Object> wInfo =
	      ImmutableMap.<String, Object>of(PLAYER_ID, wId);
	  private final ImmutableMap<String, Object> bInfo =
	      ImmutableMap.<String, Object>of(PLAYER_ID, bId);
	  private final ImmutableList<Map<String, Object>> playersInfo =
	      ImmutableList.<Map<String, Object>>of(wInfo, bInfo);

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
	

}
