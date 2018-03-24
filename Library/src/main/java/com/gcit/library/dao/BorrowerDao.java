/**
 * 
 */
package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.library.model.Borrower;

/**
 * @author gcit
 *
 */
@Component
public class BorrowerDao extends BaseDao<Borrower> implements ResultSetExtractor<List<Borrower>>{
	
	public List<Borrower> getBorrowerByPK(String sql,Object[]values) {
		return mysqlTemplate.query(sql, values,this);
	}

	
	public List<Borrower> extractData(ResultSet rs) throws SQLException{
		List<Borrower> borrowers = new ArrayList<Borrower>();
		Borrower borrower = null;
		while(rs.next()) {
			borrower = new Borrower();
			borrower.setCardNo(rs.getInt(1));
			borrower.setName(rs.getString(2));
			borrower.setAddress(rs.getString(3));
			borrower.setPhone(rs.getString(4));
			borrowers.add(borrower);
		}
		return borrowers;
	}
	
}
