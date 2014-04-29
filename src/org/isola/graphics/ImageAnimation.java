package org.isola.graphics;

import org.isola.client.Piece;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageAnimation extends Animation {
	private Image wg;
    private Image source;
    PopupPanel pp = new PopupPanel();
    private int startX;
    private int startY;
    private int finalX;
    private int finalY;
    Piece piece;
    Audio soundOfDrop;
    int rowI;
    int colI;

    public ImageAnimation(Image wg, int sRow, int sCol, int fRow, int fCol, Image ogn, Audio pieceDrop) {
            VerticalPanel vp = new VerticalPanel();
            this.wg = wg;
            vp.add(wg);
            this.pp.setWidget(vp);
            this.startX = (sCol) * wg.getWidth();
            this.startY = (sRow) * wg.getHeight();
            pp.setPopupPosition(startX, startY);
            pp.show();
            this.finalX = (fCol) * ogn.getWidth();
            this.finalY = (fRow) * ogn.getHeight();
            soundOfDrop = pieceDrop;
            source = ogn;
            source.setVisible(false);
    }

    @Override
    protected void onUpdate(double progress) {

            double positionX = startX + (progress * (this.finalX - this.startX));
            double positionY = startY + (progress * (this.finalY - this.startY));
            pp.setPopupPosition((int) positionX, (int) positionY);

    }

    @Override
    protected void onComplete() {
            super.onComplete();
            if(soundOfDrop != null)
            	//soundOfDrop.play();
            pp.hide();
            source.setVisible(true);

    }


}