package org.isola.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {

        @Source("sounds/pieceSelect.mp3")
        DataResource pieceSelectMp3();

        @Source("sounds/pieceSelect.wav")
        DataResource pieceSelectWav();

        @Source("sounds/pieceDrop.mp3")
        DataResource pieceDropMp3();

        @Source("sounds/pieceDrop.wav")
        DataResource pieceDropWav();
        
        @Source("sounds/pieceDestory.mp3")
        DataResource pieceDestoryMp3();
        
        @Source("sounds/pieceDestory.wav")
        DataResource pieceDestoryWav();
        
}