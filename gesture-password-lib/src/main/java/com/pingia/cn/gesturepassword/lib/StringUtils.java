
package com.pingia.cn.gesturepassword.lib;

import java.util.List;


public class StringUtils {

	/**
	 * 将list中的元素 的字符串表现形式 拼接起来
	 * @param list
	 * @return
	 */
		public static String joinListElementsToString(List list){
			StringBuffer sb = new StringBuffer();
			for (Object o : list){
				sb.append(o.toString());
			}

			return sb.toString();
		}
}
