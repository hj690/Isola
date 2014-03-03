package org.isola.graphics;

import org.isola.client.IsolaLogic;
import org.isola.client.GameApi;
import org.isola.client.GameApi.Game;
import org.isola.client.GameApi.UpdateUI;
import org.isola.client.GameApi.VerifyMove;

import org.isola.client.IsolaPresenter;
import org.isola.client.GameApi.IteratingPlayerContainer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class IsolaEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
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
		container = new IteratingPlayerContainer(game, 2);
		IsolaGraphics isolaGraphics = new IsolaGraphics();
		isolaPresenter = new IsolaPresenter(isolaGraphics, container);
		final ListBox playerSelect = new ListBox();
		playerSelect.addItem("Thief");
		playerSelect.addItem("Warrier");
		playerSelect.addItem("Viewer");
		playerSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int selectedIndex = playerSelect.getSelectedIndex();
				int playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
						: container.getPlayerIds().get(selectedIndex);
				container.updateUi(playerId);
			}
		});
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(isolaGraphics);
		flowPanel.add(playerSelect);
		RootPanel.get("mainDiv").add(flowPanel);
		container.sendGameReady();
		container.updateUi(container.getPlayerIds().get(0));
	}
}
