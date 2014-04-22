package org.isola.graphics;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.UpdateUI;
import org.isola.graphics.PieceImages;
import org.isola.client.Color;
import org.isola.client.IsolaPresenter;
import org.isola.client.Position;
import org.isola.graphics.ImageAnimation;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class IsolaGraphics extends Composite implements IsolaPresenter.View {
	  public interface CheatGraphicsUiBinder extends UiBinder<Widget, IsolaGraphics> {
	  }
	  private static GameSounds gameSounds = GWT.create(GameSounds.class);
	  private static PieceImages pieceImages = GWT.create(PieceImages.class);
	  
	  @UiField
	  Grid gameGrid = new Grid();
	  
	  @UiField
	  AbsolutePanel boardArea = new AbsolutePanel(); 
	  
	  private final PieceImageSupplier pieceImageSupplier;
	  
	  private IsolaPresenter presenter;
	  private AbsolutePanel[][] myPanel = new AbsolutePanel[7][7];
	  private PieceImage[][] myPieces = new PieceImage[7][7];
	  private Image[][] myImages = new Image[7][7];
	  
	  private Position myfrom, myto, mydestroy;
	  private ImageAnimation animation;
	  //handler for every cell
	  private HandlerRegistration[][] handlers = new HandlerRegistration[7][7];
	  private Audio pieceDestory;
	  private Audio pieceDrop;
	  
	  private IsolaDropController target;
		
	  
	  public IsolaGraphics(){
		  PieceImages pieceImages = GWT.create(PieceImages.class);
		  this.pieceImageSupplier = new PieceImageSupplier(pieceImages);
		  CheatGraphicsUiBinder uiBinder = GWT.create(CheatGraphicsUiBinder.class);
		  initWidget(uiBinder.createAndBindUi(this));
		  initializeGrid();
		  
		  if (Audio.isSupported()) {
			  pieceDestory = Audio.createIfSupported();
			  pieceDestory.addSource(gameSounds.pieceDestoryMp3().getSafeUri()
                              .asString(), AudioElement.TYPE_MP3);
			  pieceDestory.addSource(gameSounds.pieceDestoryWav().getSafeUri()
                              .asString(), AudioElement.TYPE_WAV);
			  pieceDrop = Audio.createIfSupported();
			  pieceDrop.addSource(gameSounds.pieceDropMp3().getSafeUri()
                              .asString(), AudioElement.TYPE_MP3);
			  pieceDrop.addSource(gameSounds.pieceDropWav().getSafeUri()
                              .asString(), AudioElement.TYPE_WAV);
            
		  }	  
	  }
	  
	  /**
	   * initialize the Grid
	   */
	  private void initializeGrid() {	  
		  gameGrid.setPixelSize(420, 420);
		  gameGrid.resize(7, 7);
		  gameGrid.setCellPadding(0);
		  gameGrid.setCellSpacing(0);
		  gameGrid.setBorderWidth(1);
		  
		  boardArea.setPixelSize(435, 435);
		  boardArea.add(gameGrid);

	}
	  
	  

	  private void removeAllHandlers(List<Position> available_Move_Positions){
		  for(Position p : available_Move_Positions)
			  handlers[p.getRow()][p.getColumn()].removeHandler();
	  }
	  
	private void placeImages(List<Image> images) {
		  //	gameGrid.clear();
		    for (Image image : images) {
		    	FlowPanel imageContainer = new FlowPanel();
		    	imageContainer.setStyleName("imgContainer");
		    	imageContainer.add(image);
		    	gameGrid.add(imageContainer);
		    } 
	  }
	  
	  @Override
	  public void setViewerState(Map<String, Object> gameApiState){
		 
		  myPieces = getImages(gameApiState);
		  for(int i = 0; i < 7; i++)
			  for(int j = 0; i < 7; j++){
				  myPanel[i][j].setPixelSize(60, 60);
				  myPanel[i][j].add(new Image(pieceImageSupplier.getResource(myPieces[i][j])));
				  gameGrid.setWidget(i, j, myPanel[i][j]);
				  
			  }
	  }
	  
	  @Override
	  public void setPlayerState(Map<String, Object> gameApiState){
		  gameGrid.resize(7,7);
		  myPieces = getImages(gameApiState);
		  for(int i = 0; i < 7; i++)
			  for(int j = 0; j < 7; j++){
				  
				  Image image = new Image(pieceImageSupplier.getResource(myPieces[i][j]));
				  myImages[i][j] = image;
				  AbsolutePanel imageContainer = new AbsolutePanel();
			      imageContainer.setStyleName("imgContainer");
			      imageContainer.add(image);
				  myPanel[i][j] = imageContainer;
				  myPanel[i][j].setPixelSize(60, 60);
				  gameGrid.setWidget(i, j, myPanel[i][j]);
			  }
	  }
	  
	  private PieceImage[][] getImages(Map<String, Object> gameApiState){
		  PieceImage[][] myPieces = new PieceImage[7][7];
		  for(int i = 0; i < 7; i++)
			  for(int j = 0; j < 7; j++){
				  myPieces[i][j] = new PieceImage(Color.W, new Position(i, j));
			  }
		  Iterator it = gameApiState.entrySet().iterator();
		  while(it.hasNext()){
				Map.Entry entry = (Map.Entry) it.next(); 
				String pos = (String)entry.getKey();
				String color = (String)entry.getValue();
				int posInt = Integer.parseInt(pos);
				Position p = new Position((posInt - posInt%10)/10, posInt%10);
				Color c = Color.W;
				switch (color){
				case "R":
					c = Color.R;
					break;
				case "G":
					c = Color.G;
					break;
				case "B":
					c = Color.B;
					break;
				case "W":
					c = Color.W;
					break;
				default:
					break;
				}
				
				myPieces[p.getRow()][p.getColumn()] = new PieceImage(c, new Position(p.getRow(), p.getColumn()));
				
			}
		  return myPieces;
	  }

	@Override
	public void setPresenter(IsolaPresenter isolaPresenter) {
		this.presenter = isolaPresenter;
	}

	private Image getTurnImageResource(Color turn) {
		Image image;
		if(turn == Color.R)
			image = (Image)pieceImages.red();
		else
			image = (Image)pieceImages.green();
		return image;
	}

	@Override
	public void selectMovePosition(Color turnOfColor, Position from, List<Position> available_Move_Positions, UpdateUI updateUI) {
		myfrom = from;
		//make my piece draggable
	//	IsolaDragController dragCtrl = new IsolaDragController(RootPanel.get(), false, presenter);
		IsolaDragController dragCtrl = new IsolaDragController(boardArea, false, presenter);
		dragCtrl.setBehaviorConstrainedToBoundaryPanel(true);
		dragCtrl.setBehaviorMultipleSelection(false);
		dragCtrl.setBehaviorDragStartSensitivity(1);
		
		dragCtrl.makeDraggable(myImages[from.getRow()][from.getColumn()]);
		
		final Color turn = turnOfColor;
		final List<Position> positions = available_Move_Positions;
		for(Position p : available_Move_Positions){
			final Position destination = p;
			Image image = new Image(pieceImages.white());
			handlers[p.getRow()][p.getColumn()] = image.addClickHandler(new ClickHandler() {
		          @Override
		          public void onClick(ClickEvent event) {
		        	  myto = destination;
		        	  makeMoveUpdate(myfrom, myto, turn, true);
		        	  removeAllHandlers(positions);
		        	  presenter.movePositionSelected(destination);
		          }
		        });
			
//			target = new IsolaDropController(image, this,  presenter, from, turnOfColor, available_Move_Positions);
//			dragCtrl.registerDropController(target);
			myImages[p.getRow()][p.getColumn()] = image;
			
			myPanel[p.getRow()][p.getColumn()].clear();
			myPanel[p.getRow()][p.getColumn()].add(image);
			
			gameGrid.clearCell(p.getRow(),p.getColumn());
			gameGrid.setWidget(p.getRow(),p.getColumn(), myPanel[p.getRow()][p.getColumn()]);
		}
		
		//register for every square
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++){
				target = new IsolaDropController(myImages[i][j], this,  presenter, myfrom, turnOfColor, available_Move_Positions, updateUI, pieceDrop);
				dragCtrl.registerDropController(target);
			}
		
	}

	/**
	 * update UI images for moving action
	 * @param from
	 * @param to
	 * @param turn
	 */
	public void makeMoveUpdate(Position from, Position to, Color turn, boolean hasAnimation) {
		
  	  	myPieces[from.getRow()][from.getColumn()] = new PieceImage(Color.W, from);
	  	Image fromImage = new Image(pieceImageSupplier.getResource(myPieces[to.getRow()][to.getColumn()]));
	  	
	  	myPieces[to.getRow()][to.getColumn()] = new PieceImage(turn, to);
  	  	Image toImage = new Image(pieceImageSupplier.getResource(myPieces[to.getRow()][to.getColumn()]));
  	  	if(hasAnimation)
  	  		presenter.DoAnimation(fromImage, myPanel[from.getRow()][from.getColumn()].getAbsoluteLeft() , myPanel[from.getRow()][from.getColumn()].getAbsoluteTop(),
  	  	
  	  	myPanel[to.getRow()][to.getColumn()].getAbsoluteLeft(), myPanel[to.getRow()][to.getColumn()].getAbsoluteTop(), toImage, pieceDrop);
	  	
	  	myPanel[from.getRow()][from.getColumn()].clear();
	  	myPanel[from.getRow()][from.getColumn()].add(fromImage);
	  	myImages[from.getRow()][from.getColumn()] = fromImage;
	  	gameGrid.clearCell(from.getRow(),from.getColumn());
	  	gameGrid.setWidget(from.getRow(),from.getColumn(),myPanel[from.getRow()][from.getColumn()]);
	  	
		
  	  	myPanel[to.getRow()][to.getColumn()].clear();
  	  	myPanel[to.getRow()][to.getColumn()].add(toImage);
  	    myImages[to.getRow()][to.getColumn()] = toImage;
  	  	gameGrid.clearCell(to.getRow(),to.getColumn());
  	  	gameGrid.setWidget(to.getRow(),to.getColumn(),myPanel[to.getRow()][to.getColumn()]);

	}

	@Override
	public void chooseDestroy(List<Position> available_Destroy_Positions) {
		for(Position p : available_Destroy_Positions){
			final Position remove = p;
			final List<Position> positions = available_Destroy_Positions;
			Image image = new Image(pieceImages.white());
			handlers[p.getRow()][p.getColumn()] = image.addClickHandler(new ClickHandler() {
		          @Override
		          public void onClick(ClickEvent event) {
		        	  mydestroy = remove;
		        	  destroyUpdate(mydestroy);
		        	  removeAllHandlers(positions);
		        	  presenter.destroyPositionSelected(mydestroy);
		          }
		        });
			myPanel[p.getRow()][p.getColumn()].clear();
			myPanel[p.getRow()][p.getColumn()].add(image);
			myImages[p.getRow()][p.getColumn()] = image;
			gameGrid.clearCell(p.getRow(),p.getColumn());
			gameGrid.setWidget(p.getRow(),p.getColumn(),myPanel[p.getRow()][p.getColumn()]);
			
		}
		
	}

	/**
	 * update Ui Piece images for destroying action
	 * @param destroy
	 */
	protected void destroyUpdate(Position destroy) {
	
		int row = destroy.getRow();
		int col = destroy.getColumn();
		myPieces[row][col] = new PieceImage(Color.B, destroy);
	  	Image destroyImage = new Image(pieceImageSupplier.getResource(myPieces[row][col]));
	  	myPanel[row][col].clear();
	  	myPanel[row][col].add(destroyImage);
	  	myImages[row][col] = destroyImage;
	  	gameGrid.clearCell(row, col);
	  	gameGrid.setWidget(row, col ,myPanel[row][col]);
	  	//pieceDestory.play();
	}

	/*
	public void moveDropperBack(Position dropper, Color turn, List<Position> available_Move_Positions) {
		IsolaDragController dragCtrl = new IsolaDragController(RootPanel.get(), false, presenter);
		dragCtrl.setBehaviorConstrainedToBoundaryPanel(true);
		dragCtrl.setBehaviorMultipleSelection(false);
		dragCtrl.setBehaviorDragStartSensitivity(1);
		
		int row = dropper.getRow();
		int col = dropper.getColumn();
		myPieces[row][col] = new PieceImage(turn, dropper);
		Image image = new Image(pieceImageSupplier.getResource(myPieces[row][col]));
		dragCtrl.makeDraggable(image);
		myImages[row][col] = image;
		myPanel[row][col].clear();
		myPanel[row][col].add(image);
		gameGrid.clearCell(row,col);
		gameGrid.setWidget(row,col,myPanel[row][col]);
		
		//register for every square
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++){
				target = new IsolaDropController(myImages[i][j], this,  presenter, myfrom, turn, available_Move_Positions);
				dragCtrl.registerDropController(target);
			}		
		
	}

	public void setIllegalTarget(Position illegalTarget) {
		int row = illegalTarget.getRow();
		int col = illegalTarget.getColumn();
		Image image = new Image(pieceImageSupplier.getResource(myPieces[row][col]));
		myPanel[row][col].clear();
		myPanel[row][col].add(image);
		gameGrid.clearCell(row,col);
		gameGrid.setWidget(row,col,myPanel[row][col]);
		
		
	}
*/
}
