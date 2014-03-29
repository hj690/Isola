package org.isola.graphics;

import org.isola.client.IsolaLogic;
import org.game_api.GameApi;
import org.game_api.GameApi.Container;
import org.game_api.GameApi.Game;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;
import org.isola.client.IsolaPresenter;
import org.game_api.GameApi.ContainerConnector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class IsolaEntryPoint implements EntryPoint {
	Container container;
	IsolaPresenter isolaPresenter;

	  @Override
	  public void onModuleLoad() {
	    Game game = new Game() {
	      @Override
	      public void sendVerifyMove(VerifyMove verifyMove) {
	        container.sendVerifyMoveDone(new IsolaLogic().verify(verifyMove));
	      }

	      @Override
	      public void sendUpdateUI(UpdateUI updateUI) {
	    	  isolaPresenter.updateUI(updateUI);
	      }
	    };
	    container = new GameApi.ContainerConnector(game);
	    IsolaGraphics isolaGraphics = new IsolaGraphics();
	    isolaPresenter = new IsolaPresenter(isolaGraphics, container);

	    RootPanel.get("mainDiv").add(isolaGraphics);
	    container.sendGameReady();
	  }
}
