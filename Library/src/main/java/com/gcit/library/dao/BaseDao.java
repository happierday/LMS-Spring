/**
 * 
 */
package com.gcit.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author gcit
 *
 */
public class BaseDao <T>{
	
	@Autowired
	public JdbcTemplate mysqlTemplate;
}
