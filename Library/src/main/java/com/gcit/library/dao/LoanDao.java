package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.library.model.Book;
import com.gcit.library.model.Branch;
import com.gcit.library.model.Loan;

@Component
public class LoanDao extends BaseDao<Loan> implements ResultSetExtractor<List<Loan>>{
	
	public List<Loan> getLoans(String sql, Object[]values){
		return mysqlTemplate.query(sql, values,this);
	}
	
	public List<Loan> extractData(ResultSet rs) throws SQLException{
		List<Loan> loans = new ArrayList<Loan>();
		Loan loan = null;
		while(rs.next()) {
			loan = new Loan();
			loan.setBookId(rs.getInt(1));
			loan.setBranchId(rs.getInt(2));
			loan.setCardNo(rs.getInt(3));
			loan.setBookTitle(rs.getString(4));
			loan.setBorrowerName(rs.getString(5));
			loan.setBranchName(rs.getString(6));
			loan.setDateOut(rs.getDate(7).toLocalDate());
			loan.setDueDate(rs.getDate(8).toLocalDate());
			if(rs.getDate(9) != null) {
				loan.setDateIn(rs.getDate(9).toLocalDate());
			}
			loans.add(loan);
		}
		return loans;
	}

	public Integer getLoansCount(String sql, Object[] values) {
		return mysqlTemplate.queryForObject(sql,values,Integer.class);
	}

	public void updateLoan(String sql, Object[] values) {
		mysqlTemplate.update(sql,values);
	}

	public void deleteLoan(String sql, Object[] values) {
		mysqlTemplate.update(sql,values);
	}
}
