package com.jianglei.mstransfer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jianglei.mstransfer.utils.Pagination;
import com.jianglei.mstransfer.utils.ReflectHelper;




/**
 * 数据访问对象<br>
 * 全系统仅用这个对象访问数据库，为避免被继承，设置成final
 * 
 * @author wuhao
 *
 */
@Component
public final class CommonDao extends SqlSessionDaoSupport {

	@Value("${def.page_size}")
	private int defaultPageSize;

	@Resource
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public <T> T selectOne(String sqlId) {
		return super.getSqlSession().selectOne(sqlId);
	}

	public <T> T selectOne(String sqlId, Object parameter) {
		return super.getSqlSession().selectOne(sqlId, parameter);
	}

	public int insert(String sqlId, Object parameter) {
		return super.getSqlSession().insert(sqlId, parameter);
	}

	public int update(String sqlId, Object parameter) {
		return super.getSqlSession().update(sqlId, parameter);
	}

	public int delete(String sqlId, Object parameter) {
		return super.getSqlSession().delete(sqlId, parameter);
	}

	public int delete(String sqlId) {
		return super.getSqlSession().delete(sqlId);
	}

	public <T> List<T> selectList(String sqlId) {
		return super.getSqlSession().selectList(sqlId);
	}

	public <T> List<T> selectList(String sqlId, Object parameter) {
		return super.getSqlSession().selectList(sqlId, parameter);
	}

	public <K, V> Map<K, V> selectMap(String sqlId, String key) {
		return super.getSqlSession().selectMap(sqlId, key);
	}

	public <K, V> Map<K, V> selectMap(String sqlId, Object parameter, String key) {
		return super.getSqlSession().selectMap(sqlId, parameter, key);
	}

	/**
	 * insert多个记录
	 * 
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	public int batchInsert(String sqlId, Collection<?> parameters) {
		if (parameters == null) {
			throw new IllegalArgumentException("parameters 不能为null");
		}

		return batchInsert(sqlId, parameters.toArray());
	}

	/**
	 * insert多个记录
	 * 
	 * @param sqlId
	 * @param parameters
	 * @return
	 */
	public int batchInsert(String sqlId, Object[] parameters) {
		if (parameters == null) {
			throw new IllegalArgumentException("parameters 不能为null");
		}

		SqlSession sqlSession = super.getSqlSession();
		int ret = 0;
		for (Object parameter : parameters) {
			ret += sqlSession.insert(sqlId, parameter);
		}
		return ret;
	}

	/**
	 * 按条件分页查找数据
	 * 
	 * @param sqlId
	 * @param pagination
	 * @return
	 */

	public <T> Pagination selectListPaginated(String sqlId, Pagination pagination) {
		return selectListPaginated(sqlId, pagination, null);
	}

	/**
	 * 按条件分页查找数据
	 * 
	 * @param sqlId
	 * @param pagination
	 * @param parameter
	 * @return
	 */

	public <T> Pagination selectListPaginated(String sqlId, Pagination pagination, Object parameter) {

		if (pagination.getPageIndex() == null) { // 未设置页码则页码为1
			pagination.setPageIndex(1);
		}

		if (pagination.getPageSize() == null) { // 未设置每页记录条数，则从系统参数中取默认值
			pagination.setPageSize(defaultPageSize);
		}

		int totalCnt = -1;
		try {
			totalCnt = getTotalRecordCnt(sqlId, parameter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pagination.setTotalRecordCnt(totalCnt);

		// 如果页码超出最大页数，重置页码
		if (pagination.getPageIndex() > pagination.getTotalPageCnt()) {
			pagination.setPageIndex(pagination.getTotalPageCnt());
		}

		List<T> dataList = super.getSqlSession().selectList(sqlId, parameter,
				new RowBounds(pagination.offset(), pagination.getPageSize()));
		pagination.setDataList(dataList);
		return pagination;
	}

	/**
	 * 将普通列表查询自动包装成count查询，以获取总记录数
	 * 
	 * @param sqlId
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int getTotalRecordCnt(String sqlId, Object parameter) throws SQLException {

		SqlSession sqlSession = super.getSqlSession();
		MappedStatement mappedStatement = sqlSession.getConfiguration().getMappedStatement(sqlId);
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);

		// 根据当前上下文解析出动态sql字符串
		String strSql = boundSql.getSql();
		String countSql = getCountSql(strSql);

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
			ps = conn.prepareStatement(countSql);

			/* ============== 处理参数 ======================= */
			BoundSql cntBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql,
					boundSql.getParameterMappings(), parameter);

			// addtionalParameters & metaParameters，当使用foreach查询时会产生
			ReflectHelper.copyFieldValue(boundSql, cntBoundSql, "additionalParameters");
			ReflectHelper.copyFieldValue(boundSql, cntBoundSql, "metaParameters");

			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameter, cntBoundSql);
			parameterHandler.setParameters(ps);
			/* ============== end of 处理参数 ======================= */

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return -1;
	}

	/**
	 * 查询符合条件的数据的总数
	 * @version 2017-6-16 优化sql语句
	 * @param sql
	 * @return 返回总数
	 */
	private String getCountSql(String sql) {
		String orderRegexp = "order\\s+by\\s+.+";
		Pattern pattern = Pattern.compile(orderRegexp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		sql = matcher.replaceAll("");
		int start = sql.indexOf("select");//第一个select的位置
		int end = sql.indexOf("from");//第一个from的位置
		StringBuilder sb = new StringBuilder(sql);
		sb = sb.replace(start+6, end, " count(0) ");//替换查询项为count(0)
		String ret = sb.toString();
		//以下原版代码暂时注释，分页功能完成后进行处理
		//String ret = "select count(1) from (" + sql + ") tmp";
		return ret;
	}
	/**
	 * 按条件进行物理分页查找数据[新增物理分页方式,待测试]
	 * @version 2017-6-16 
	 * @param sqlId
	 * @param pagination
	 * @param parameter
	 * @return 分页对象
	 */
	public <T> Pagination selectPageList(String sqlId, Pagination pagination, Object parameter) {
		if (pagination.getPageIndex() == null) { // 未设置页码则页码为1
			pagination.setPageIndex(1);
		}

		if (pagination.getPageSize() == null) { // 未设置每页记录条数，则从系统参数中取默认值
			pagination.setPageSize(defaultPageSize);
		}
		int totalCnt = -1;
		try {			
			totalCnt = getTotalRecordCnt(sqlId, parameter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pagination.setTotalRecordCnt(totalCnt);

		// 如果页码超出最大页数，重置页码
		if (pagination.getPageIndex() > pagination.getTotalPageCnt()) {
			pagination.setPageIndex(pagination.getTotalPageCnt());
		}
		//以下代码为原有逻辑分页代码，暂时注释
		/*List<T> dataList = super.getSqlSession().selectList(sqlId, parameter,
				new RowBounds(pagination.offset(), pagination.getPageSize()));*/
		List<T> dataList = getCurrentList(sqlId, parameter,pagination);
		pagination.setDataList(dataList);
		return pagination;
	}
	/**
	 * 查询指定页码的数据，mapper中的id命名需要符合规则
	 * @version 2017-6-16 
	 * @param sqlId
	 * @param parameter
	 * @param pagination
	 * @return 出问题返回null；否则返回正确的结果集合
	 */
	public List getCurrentList(String sqlId, Object parameter,Pagination pagination){
		SqlSession sqlSession = super.getSqlSession();
		MappedStatement mappedStatement = sqlSession.getConfiguration().getMappedStatement(sqlId);
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		// 根据当前上下文解析出动态sql字符串
		String strSql = boundSql.getSql();
		int offset = pagination.offset();
		int pageSize = pagination.getPageSize();
		String pageSql = strSql+" limit "+offset+","+pageSize;//在sql中加入limit函数
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		List dataList = new ArrayList();
		int columnCount = 0;
			try {
				conn = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
			} catch (SQLException e5) {
				// TODO Auto-generated catch block
				e5.printStackTrace();
			}
			try {
				ps = conn.prepareStatement(pageSql);
			} catch (SQLException e5) {
				// TODO Auto-generated catch block
				e5.printStackTrace();
			}

			/* ============== 处理参数 ======================= */
			BoundSql cntBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
					boundSql.getParameterMappings(), parameter);

			// addtionalParameters & metaParameters，当使用foreach查询时会产生
			try {
				ReflectHelper.copyFieldValue(boundSql, cntBoundSql, "additionalParameters");
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				ReflectHelper.copyFieldValue(boundSql, cntBoundSql, "metaParameters");
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameter, cntBoundSql);
			try {
				parameterHandler.setParameters(ps);
			} catch (SQLException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			/* ============== end of 处理参数 ======================= */

			try {
				rs = ps.executeQuery();
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			try {
				md = rs.getMetaData();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} //获得结果集结构信息,元数据  
	        try {
				columnCount = md.getColumnCount();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}   //获得列数   
	        try {
	        	//遍历结果集放入结果集合中
				while (rs.next()) {  
				    Map<String,Object> rowData = new HashMap<String,Object>();  
				    for (int i = 1; i <= columnCount; i++) {  
				        try {
							rowData.put(md.getColumnName(i), rs.getObject(i));
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
				    }  
				    dataList.add(rowData);  
  
				}
				return dataList;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		return null;
	}
	@Override
	public SqlSession getSqlSession() {
		throw new RuntimeException("禁止直接使用sqlsession");
	}

	@Override
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		throw new RuntimeException("禁止直接使用sqlsessionTemplate");
	}
}
