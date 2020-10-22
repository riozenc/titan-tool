/**
 *    Auth:riozenc
 *    Date:2018年6月19日 下午6:27:36
 *    Title:com.riozenc.quicktool.springmvc.strategy.messageConverter.MessageConverter.java
 **/
package com.riozenc.titanTool.spring.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import com.riozenc.titanTool.common.json.utils.GsonUtils;

public class MessageConverter extends AbstractHttpMessageConverter<Object> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	public MessageConverter() {
		this(DEFAULT_CHARSET);
	}

	/**
	 * A constructor accepting a default charset to use if the requested content
	 * type does not specify one.
	 */
	public MessageConverter(Charset defaultCharset) {
		super(defaultCharset, MediaType.TEXT_PLAIN, MediaType.ALL);
	}

	@Override
	public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
		// TODO Auto-generated method stub
		List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.APPLICATION_JSON);
		list.add(MediaType.APPLICATION_XML);
		super.setSupportedMediaTypes(list);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
//		return String.class == clazz;
		return true;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub

		Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());

//		System.out.println(StreamUtils.copyToString(inputMessage.getBody(), charset));

		return StreamUtils.copyToString(inputMessage.getBody(), charset);
	}

	@Override
	protected void writeInternal(Object t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		// TODO Auto-generated method stub
		Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
		String message = null;
		if (outputMessage.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)) {
			// json
//			message = JSONUtil.toJsonString(t);
			message = GsonUtils.toJsonIgnoreNull(t);
		} else if (outputMessage.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_XML)) {
			// xml
//			message = XmlUtils.object2xml(t);
			message = "<暂不支持xml转化/>";
		} else if (t.getClass() == String.class) {
			message = (String) t;
		} else {
			message = "未知格式数据..";
		}

		if (logger.isDebugEnabled()) {
			logger.info(t.getClass() + " converter ===>>>" + message);
		}
		StreamUtils.copy(message, charset, outputMessage.getBody());

	}

	private Charset getContentTypeCharset(MediaType contentType) {
		if (contentType != null && contentType.getCharset() != null) {
			return contentType.getCharset();
		} else {
			return getDefaultCharset();
		}
	}

}
