package org.isola.graphics;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.isola.graphics.PieceImages;
import org.isola.client.Color;
import org.isola.client.IsolaPresenter;
import org.isola.client.Piece;
import org.isola.client.Position;
import org.isola.graphics.PieceImages;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class IsolaGraphics extends Composite implements IsolaPresenter.View {
	  public interface CheatGraphicsUiBinder extends UiBinder<Widget, IsolaGraphics> {
	  }
	  
	  private static PieceImages pieceImages = GWT.create(PieceImages.class);
	  
	  @UiField
	  Grid gameGrid = new Grid();
	 
	  private boolean enableClicks = false;
	  private final PieceImageSupplier pieceImageSupplier;
	  private IsolaPresenter presenter;
	  private FlowPanel[][] myPanel = new FlowPanel[7][7];
	  private PieceImage[][] myPieces = new PieceImage[7][7];
	  private Color turn;
	  private Position myfrom, myto, mydestroy;
	  private Position lastclickPosition = new Position();
	  //handler for every cell
	  private HandlerRegistration[][] handlers = new HandlerRegistration[7][7];
	  
	  
	  public IsolaGraphics(){
		  PieceImages pieceImages = GWT.create(PieceImages.class);
		  this.pieceImageSupplier = new PieceImageSupplier(pieceImages);
		  CheatGraphicsUiBinder uiBinder = GWT.create(CheatGraphicsUiBinder.class);
		  initWidget(uiBinder.createAndBindUi(this));
		  initializeGrid();
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
				  FlowPanel imageContainer = new FlowPanel();
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

	private Image getTurnImageResource() {
		Image image;
		if(turn == Color.R)
			image = (Image)pieceImages.red();
		else
			image = (Image)pieceImages.green();
		return image;
	}

	@Override
	public void selectMovePosition(Color turnOfColor, Position from, List<Position> available_Move_Positions) {
		System.out.println("selectMovePosition " + from.toString());
		
		myfrom = from;
		final Color turn = turnOfColor;
		final List<Position> positions = available_Move_Positions;
		for(Position p : available_Move_Positions){
			final Position destination = p;
			Image image = new Image(pieceImages.white());
			handlers[p.getRow()][p.getColumn()] = image.addClickHandler(new ClickHandler() {
		          @Override
		          public void onClick(ClickEvent event) {
		        	  myto = destination;
		        	  makeMoveUpdate(myfrom, myto, turn);
		        	  removeAllHandlers(positions);
		        	  presenter.movePositionSelected(destination);
		          }
		        });
			myPanel[p.getRow()][p.getColumn()].clear();
			myPanel[p.getRow()][p.getColumn()].add(image);
			gameGrid.clearCell(p.getRow(),p.getColumn());
			gameGrid.setWidget(p.getRow(),p.getColumn(), myPanel[p.getRow()][p.getColumn()]);
		}
		
	}

	/**
	 * update UI images for moving action
	 * @param from
	 * @param to
	 * @param turn
	 */
	protected void makeMoveUpdate(Position from, Position to, Color turn) {
		System.out.println("makeMoveUpdate: ");
		System.out.println("from " + from.getRow() + from.getColumn() + "to " + to.getRow() + to.getColumn() + "turn " + turn.toString());
		System.out.println();
		
		System.out.println("set from Pic");
  	  	myPieces[from.getRow()][from.getColumn()] = new PieceImage(Color.W, from);
	  	Image fromImage = new Image(pieceImageSupplier.getResource(myPieces[to.getRow()][to.getColumn()]));
	  	myPanel[from.getRow()][from.getColumn()].clear();
	  	myPanel[from.getRow()][from.getColumn()].add(fromImage);
	  	gameGrid.clearCell(from.getRow(),from.getColumn());
	  	gameGrid.setWidget(from.getRow(),from.getColumn(),myPanel[from.getRow()][from.getColumn()]);
	  	
	  	System.out.println("set to Pic");
		myPieces[to.getRow()][to.getColumn()] = new PieceImage(turn, to);
  	  	Image toImage = new Image(pieceImageSupplier.getResource(myPieces[to.getRow()][to.getColumn()]));
  	  	myPanel[to.getRow()][to.getColumn()].clear();
  	  	myPanel[to.getRow()][to.getColumn()].add(toImage);
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
			gameGrid.clearCell(p.getRow(),p.getColumn());
			gameGrid.setWidget(p.getRow(),p.getColumn(),myPanel[p.getRow()][p.getColumn()]);
		}
		
	}

	/**
	 * update Ui Piece images for destroying action
	 * @param destroy
	 */
	protected void destroyUpdate(Position destroy) {
		System.out.println("destroy " + destroy.getRow() + destroy.getColumn());
		int row = destroy.getRow();
		int col = destroy.getColumn();
		myPieces[row][col] = new PieceImage(Color.B, destroy);
	  	Image destroyImage = new Image(pieceImageSupplier.getResource(myPieces[row][col]));
	  	myPanel[row][col].clear();
	  	myPanel[row][col].add(destroyImage);
	  	gameGrid.clearCell(row, col);
	  	gameGrid.setWidget(row, col ,myPanel[row][col]);
		
	}

	  
	  
}
