/**
 * 
 */
package com.gcit.library;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcit.library.dao.AuthorDao;
import com.gcit.library.dao.BookDao;
import com.gcit.library.dao.BranchDao;
import com.gcit.library.dao.GenreDao;
import com.gcit.library.dao.LoanDao;
import com.gcit.library.dao.PublisherDao;



/**
 * @author gcit
 *
 */

@Configuration
public class LMSConfiguration {
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost/library?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
	private String username = "root";
	private String password = "root";
	
	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driver);
		bds.setUrl(url);
		bds.setUsername(username);
		bds.setPassword(password);
		return bds;
	}
	
	@Bean
	public JdbcTemplate mysqlTemplage() {
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
	public BookDao bdao() {
		return new BookDao();
	}
	
	@Bean
	public AuthorDao adao() {
		return new AuthorDao();
	}
	
	@Bean
	public BranchDao brdao() {
		return new BranchDao();
	}
	
	@Bean
	public GenreDao gdao() {
		return new GenreDao();
	}
	
	@Bean
	public PublisherDao pdao() {
		return new PublisherDao();
	}
	
	@Bean
	public LoanDao ldao() {
		return new LoanDao();
	}
}
