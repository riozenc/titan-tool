/**
 *    Auth:riozenc
 *    Date:2019年3月15日 上午10:19:53
 *    Title:com.riozenc.titanTool.spring.webapp.action.BaseAction.java
 **/
package com.riozenc.titanTool.spring.webapp.action;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import com.riozenc.titanTool.common.date.DateUtil;

public abstract class BaseAction {

	@RequestMapping(params = "method=index")
	public String index() {
		return getIndex();
	}

	public abstract String getIndex();

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue((text == null || "".equals(text.trim())) ? null : HtmlUtils.htmlEscape(text.trim()));
			}

			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtil.getDate(text));
			}

		});
	}

	/**
	 * 客户端返回字符串
	 * 
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string, String type) {
		try {
			response.reset();
			response.setContentType(type);
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

}
