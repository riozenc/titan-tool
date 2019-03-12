
package com.riozenc.titanTool.mybatis.pagination.interceptor;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;

import com.riozenc.titanTool.common.reflect.ReflectUtil;
import com.riozenc.titanTool.common.string.StringUtils;
import com.riozenc.titanTool.mybatis.pagination.Page;
import com.riozenc.titanTool.mybatis.pagination.SQLHelper;
import com.riozenc.titanTool.properties.Global;

/**
 * 数据库分页插件，只拦截查询语句.
 */

public abstract class PaginationInterceptor extends BaseInterceptor {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(PaginationInterceptor.class);

	abstract protected boolean preHandle(final MappedStatement mappedStatement, final Object parameter);

	abstract protected void postHandle(final MappedStatement mappedStatement, final Object parameter);

	abstract protected void afterCompletion(final MappedStatement mappedStatement, final Object parameter,
			Exception exception);

	protected boolean isPagination(Page page) {
		return page != null && page.getPageSize() != -1;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		final Object parameter = invocation.getArgs()[1];
		try {
			if (preHandle(mappedStatement, parameter)) {
				getCountAndProcess(mappedStatement, parameter, invocation);
			}
			postHandle(mappedStatement, parameter);
		} catch (Exception exception) {
			afterCompletion(mappedStatement, parameter, exception);
		}
		return invocation.proceed();
	}

	protected void getCountAndProcess(final MappedStatement mappedStatement, final Object parameter,
			Invocation invocation) throws SQLException {

		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		Object parameterObject = boundSql.getParameterObject();

		// 获取分页参数对象
		Page page = null;
		if (parameterObject != null) {
			page = convertParameter(parameterObject, page);
		}

		// 如果设置了分页对象，则进行分页
		if (isPagination(page)) {

			if (StringUtils.isBlank(boundSql.getSql())) {
				throw new RuntimeException("sql is blank");
			}
			String originalSql = boundSql.getSql().trim();

			// 得到总记录数
			page.setTotalRow(SQLHelper.getCount(Global.getConfig(page.getDbName() + ".jdbc.type"), originalSql, null,
					mappedStatement, parameterObject, boundSql, logger));

			// 分页查询 本地化对象 修改数据库注意修改实现
			String pageSql = SQLHelper.generatePageSql(originalSql, page, getDialect(page.getDbName()));
			// if (log.isDebugEnabled()) {
			// log.debug("PAGE SQL:" + StringUtils.replace(pageSql, "\n",
			// ""));
			// }

			BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
					boundSql.getParameterMappings(), boundSql.getParameterObject());
			// 解决MyBatis 分页foreach 参数失效 start
			if (ReflectUtil.getFieldValue(boundSql, "metaParameters") != null) {
				MetaObject mo = (MetaObject) ReflectUtil.getFieldValue(boundSql, "metaParameters");
				ReflectUtil.setFieldValue(newBoundSql, "metaParameters", mo);
			}
			// 解决MyBatis 分页foreach 参数失效 end
			MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));

			invocation.getArgs()[0] = newMs;
		}

	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		super.initProperties(properties);
	}

	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null) {
			for (String keyProperty : ms.getKeyProperties()) {
				builder.keyProperty(keyProperty);
			}
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.cache(ms.getCache());
		return builder.build();
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

}
