package com.riozenc.titanTool.mybatis.pagination.interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;

import com.riozenc.titanTool.common.reflect.ReflectUtil;
import com.riozenc.titanTool.mybatis.pagination.Page;
import com.riozenc.titanTool.mybatis.pagination.dialect.Dialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.DB2Dialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.DerbyDialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.H2Dialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.HSQLDialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.MySQLDialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.OracleDialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.PostgreSQLDialect;
import com.riozenc.titanTool.mybatis.pagination.dialect.db.SybaseDialect;
import com.riozenc.titanTool.properties.Global;

/**
 * Mybatis分页拦截器基类
 * 
 */
public abstract class BaseInterceptor implements Interceptor, Serializable {
	private static final Log logger = LogFactory.getLog(BaseInterceptor.class);
	private static final long serialVersionUID = 1L;

	protected static final String PAGE = "page";

	protected static final String DELEGATE = "delegate";

	protected static final String MAPPED_STATEMENT = "mappedStatement";

	private Map<String, Dialect> DIALECT_MAP = new HashMap<>();

	/**
	 * 对参数进行转换和检查
	 * 
	 * @param parameterObject 参数对象
	 * @param page            分页对象
	 * @return 分页对象
	 * @throws NoSuchFieldException 无法找到参数
	 */

	protected static Page convertParameter(Object parameterObject, Page page) {
		try {
			if (parameterObject instanceof Page) {
				return (Page) parameterObject;
			} else {
				return (Page) ReflectUtil.getFieldValue(parameterObject, PAGE);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	protected Dialect getDialect(String jdbcType) {
		return DIALECT_MAP.get(jdbcType);
	}

	/**
	 * 需要改 ！！！ 设置属性，支持自定义方言类和制定数据库的方式 <code>dialectClass</code>,自定义方言类。可以不配置这项
	 * <ode>dbms</ode> 数据库类型，插件支持的数据库 <code>sqlPattern</code> 需要拦截的SQL ID
	 * 
	 * @param p 属性
	 */
	protected void initProperties(Properties p) {

		String dbValue = Global.getConfig("db");
		String[] dbs = dbValue.split(",");
		Dialect dialect = null;
		String jdbcType = null;
		for (String db : dbs) {
			jdbcType = Global.getConfig(db + ".jdbc.type");
			if ("db2".equals(jdbcType)) {
				dialect = new DB2Dialect();
			} else if ("derby".equals(jdbcType)) {
				dialect = new DerbyDialect();
			} else if ("h2".equals(jdbcType)) {
				dialect = new H2Dialect();
			} else if ("hsql".equals(jdbcType)) {
				dialect = new HSQLDialect();
			} else if ("mysql".equals(jdbcType)) {
				dialect = new MySQLDialect();
			} else if ("oracle".equals(jdbcType)) {
				dialect = new OracleDialect();
			} else if ("postgre".equals(jdbcType)) {
				dialect = new PostgreSQLDialect();
			} else if ("sybase".equals(jdbcType)) {
				dialect = new SybaseDialect();
			}
			if (dialect == null) {
				throw new RuntimeException("mybatis dialect error.");
			}
			DIALECT_MAP.put(db, dialect);
		}

	}
}
