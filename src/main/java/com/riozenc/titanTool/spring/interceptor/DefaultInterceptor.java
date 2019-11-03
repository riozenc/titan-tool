package com.riozenc.titanTool.spring.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.riozenc.titanTool.common.date.DateUtil;
import com.riozenc.titanTool.common.json.utils.JSONUtil;
import com.riozenc.titanTool.spring.web.http.HttpResult;

public abstract class DefaultInterceptor extends HandlerInterceptorAdapter {
	private static final Log logger = LogFactory.getLog(DefaultInterceptor.class);
	// 参数中的Object handler是下一个拦截器。

	// 最后执行，可用于释放资源
	// 在afterCompletion中，可以根据e是否为null判断是否发生了异常，进行日志记录

	private void executeException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object, Exception exception) throws Exception {
		Throwable throwable = exception;

		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}

		// 设置头信息,字符集UTF-8
		httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
		httpServletResponse.getWriter().println(JSONUtil
				.toJsonString(new HttpResult(HttpResult.ERROR, "执行异常:	" + throwable + " " + throwable.getMessage())));
		httpServletResponse.getWriter().close();

		logger.error("[" + DateUtil.getDateTime() + "]{" + httpServletRequest.getRemoteAddr() + "} 执行"
				+ getClassMethod(object) + "[" + httpServletRequest.getMethod() + "]:" + exception);
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object, Exception exception) throws Exception {
		// TODO Auto-generated method stub

		if (null != exception) {

			executeException(httpServletRequest, httpServletResponse, object, exception);

		}

	}

	// Action之后,生成视图之前执行
	// 在postHandle中，有机会修改ModelAndView
	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		if (null != modelAndView) {

		}
		System.out.println("postHandler");
	}

	// Action之前执行
	// 在preHandle中，可以进行编码、安全控制等处理

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object) throws Exception {
		// TODO Auto-generated method stub
		logger.info("[" + DateUtil.getDateTime() + "]{" + httpServletRequest.getRemoteAddr() + "} 执行"
				+ getClassMethod(object) + "[" + httpServletRequest.getMethod() + "]" + ":{"
				+ JSONUtil.toJsonString(httpServletRequest.getParameterMap()) + "}");
		return true;
	}

	protected String getClassMethod(Object object) {
		if (object instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) object;
			Class<?> clazz = handlerMethod.getBeanType();
			Method method = handlerMethod.getMethod();

			return clazz.getName() + "." + method.getName();
		} else {
			return "执行未知操作:" + object.getClass();
		}
	}
}
