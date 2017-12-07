
package com.heyijoy.gamesdk.act;

import com.heyijoy.gamesdk.data.Bean;

/**
 * @author msh
 * @since Ver 1.1
 * @Date 2014-2-25 下午1:06:19
 *
 */
public interface HYCallBack {
	public final static String FAIL_REASION_NO_POP_UP = "FAIL_REASION_NO_POP_UP";

	public void onSuccess(Bean bean);

	public void onFailed(int code, String failReason);
}
