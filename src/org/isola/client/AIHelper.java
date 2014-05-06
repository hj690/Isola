package org.isola.client;

import java.util.Map;

public class AIHelper {
	public static boolean checkOver(Map<String,Object> state){
		IsolaState st = new IsolaState(state);
		if (st.can_move(Color.G) && st.can_move(Color.R))
			return true;
		else 
			return false;
	}
}
