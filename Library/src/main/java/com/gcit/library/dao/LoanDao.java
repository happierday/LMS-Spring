package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.gcit.library.model.Book;
import com.gcit.library.model.Branch;
import com.gcit.library.model.Loan;

public class LoanDao extends BaseDao<Loan> implements ResultSetExtractor<List<Loan>>{

	public List<Loan> getLoanForBook(Integer bookId){
		return mysqlTemplate.query("select book.bookId, branch.branchId, borrower.cardNo,book.title, borrower.name,branch.branchName,loan.dateOut,loan.dueDate,loan.dateIn from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n" + 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo\n" + 
				"where loan.dateIn is null and book.bookId = ?;", new Object[] {bookId}, this);
	}
	
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
			loan.setBorrowerName(rs.getString(6));
			loan.setBranchName(rs.getString(5));
			loan.setDateOut(rs.getDate(7).toLocalDate());
			loan.setDueDate(rs.getDate(8).toLocalDate());
			if(rs.getDate(9) != null) {
				loan.setDateIn(rs.getDate(9).toLocalDate());
			}
			loans.add(loan);
		}
		return loans;
	}
}
