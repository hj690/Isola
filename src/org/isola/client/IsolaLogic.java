package org.isola.client;

import org.isola.client.GameApi.VerifyMove;
import org.isola.client.GameApi.VerifyMoveDone;

public class IsolaLogic {
	public static VerifyMoveDone verify(VerifyMove verifyMove) {
		// TODO: I will implement this method in HW2
		try {
			checkMoveIsLegal(verifyMove);
			return new VerifyMoveDone();
			} catch (Exception e) {
				return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
		    }
	}

	public static void checkMoveIsLegal(VerifyMove verifyMove) {
		// TODO Auto-generated method stub
		
	}
	


}
