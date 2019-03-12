package com.riozenc.titanTool.mybatis.pagination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.riozenc.titanTool.common.reflect.ReflectUtil;
import com.riozenc.titanTool.mybatis.pagination.dialect.Dialect;

/**
 * SQL工具类
 * 
 */
public class SQLHelper {

	/**
	 * 单纯执行SQL,暂时先用于获取版面数量
	 * 
	 * @throws SQLException
	 */
	public static int excuteSql(String sql, MappedStatement mappedStatement, Log log) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try {

			if (log.isDebugEnabled()) {
				log.debug("COUNT SQL: " + sql);
			}
			conn = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
			ps = conn.prepareStatement(sql);
			rs = null;
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return count;

	}

	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter. DefaultParameterHandler
	 *
	 * @param ps              表示预编译的 SQL 语句的对象。
	 * @param mappedStatement MappedStatement
	 * @param boundSql        SQL
	 * @param parameterObject 参数对象
	 * @throws java.sql.SQLException 数据库异常
	 */
	@SuppressWarnings("unchecked")
	public static void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
			Object parameterObject) throws SQLException {
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
							&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = configuration.newMetaObject(value)
									.getValue(propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					@SuppressWarnings("rawtypes")
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
								+ " of statement " + mappedStatement.getId());
					}
					typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
				}
			}
		}
	}

	/**
	 * 查询总纪录数
	 * 
	 * @param sql             SQL语句
	 * @param connection      数据库连接
	 * @param mappedStatement mapped
	 * @param parameterObject 参数
	 * @param boundSql        boundSql
	 * @return 总记录数
	 * @throws SQLException sql查询错误
	 */
	public static int getCount(final String jdbcType, final String sql, final Connection connection,
			final MappedStatement mappedStatement, final Object parameterObject, final BoundSql boundSql, Log log)
			throws SQLException {

		final String countSql;
		if ("oracle".equals(jdbcType)) {
			countSql = "select count(1) from (" + sql + ") tmp_count";
		} else {
			countSql = "select count(1) from (" + removeOrders(sql) + ") tmp_count";
			// countSql = "select count(1) " + removeSelect(removeOrders(sql));
		}
		Connection conn = connection;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("COUNT SQL: " + countSql);
			}
			if (conn == null) {
				conn = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
			}
			ps = conn.prepareStatement(countSql);
			BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
					boundSql.getParameterMappings(), parameterObject);
			// 解决MyBatis 分页foreach 参数失效 start
			if (ReflectUtil.getFieldValue(boundSql, "metaParameters") != null) {
				MetaObject mo = (MetaObject) ReflectUtil.getFieldValue(boundSql, "metaParameters");
				ReflectUtil.setFieldValue(countBS, "metaParameters", mo);
			}
			// 解决MyBatis 分页foreach 参数失效 end
			SQLHelper.setParameters(ps, mappedStatement, countBS, parameterObject);
			rs = ps.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			return count;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * 根据数据库方言，生成特定的分页sql
	 * 
	 * @param sql     Mapper中的Sql语句
	 * @param page    分页对象
	 * @param dialect 方言类型
	 * @return 分页SQL
	 */
	public static String generatePageSql(String sql, Page page, Dialect dialect) {
		if (dialect.supportsLimit()) {
			return dialect.getLimitString(sql, page.getFirstResult(), page.getMaxResults());
		} else {
			return sql;
		}
	}

	/**
	 * 去除qlString的select子句。
	 * 
	 * @param hql
	 * @return
	 */

	@SuppressWarnings("unused")
	private static String removeSelect(String qlString) {
		int beginPos = qlString.toLowerCase().indexOf("from");
		return qlString.substring(beginPos);
	}

	/**
	 * 去除hql的orderBy子句。
	 * 
	 * @param hql
	 * @return
	 */

	private static String removeOrders(String qlString) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(qlString);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

}
