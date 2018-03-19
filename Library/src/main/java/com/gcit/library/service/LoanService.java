/**
 * 
 */
package com.gcit.library.service;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.LoanDao;
import com.gcit.library.model.Loan;
import com.gcit.library.model.ResponseBody;

/**
 * @author gcit
 *
 */
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class LoanService {
	
	@Autowired
	LoanDao ldao;
	
	@Transactional
	@RequestMapping(value="/getLoans", method=RequestMethod.GET, produces= {"application/json"})
	public List<Loan> getLoans(@RequestParam(value="pageNo",required=false) Integer pageNo,
			@RequestParam(value="searchTitle",required=false) String searchTitle) {
		List<Loan> loans = null;
		StringBuffer str = new StringBuffer("select book.bookId, branch.branchId, borrower.cardNo,book.title, borrower.name,branch.branchName,loan.dateOut,loan.dueDate,loan.dateIn from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n" + 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo");
		if(searchTitle != null && pageNo != null) {
			String searchCondition = "%"+searchTitle+"%";
			str.append(" where book.title like ? limit ?,?;");
			loans = ldao.getLoans(str.toString(), new Object[] {searchCondition,(pageNo-1)*10,10});
		} else {
			if(pageNo != null) {
				str.append(" limit ?,?");
				loans = ldao.getLoans(str.toString(), new Object[] {(pageNo-1)*10,10});
			} else {
				loans = ldao.getLoans(str.toString(), null);
			}
		}
		return loans;
	}
	
	@Transactional
	@RequestMapping(value="/getLoansCount",method=RequestMethod.GET,produces= {"application/json"})
	public Integer getLoansCount(@RequestParam(value="searchTitle",required=false)  String searchTitle)  {
		StringBuffer str = new StringBuffer("select count(*) from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n"+ 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo");
		if(searchTitle != null) {
			String searchCondition = "%"+searchTitle+"%";
			str.append(" where book.title like ?");
			return ldao.getLoansCount(str.toString(),new Object[] {searchCondition});
		}
		return ldao.getLoansCount(str.toString(),null);
	}
	
	@Transactional
	@RequestMapping(value="/updateLoan",method=RequestMethod.POST, consumes= {"application/json"},produces= {"application/json"})
	public ResponseBody updateLoan(@RequestBody Loan loan)  {
		StringBuffer str = new StringBuffer("");
		ResponseBody rs = new ResponseBody();
		try {
			ldao.updateLoan("update tbl_book_loans set dueDate = ? where bookId = ? and branchId = ? and cardNo = ?",
					new Object[] {Date.valueOf(loan.getDueDate()),loan.getBookId(),loan.getBranchId(),loan.getCardNo()});
			rs.setMessage("Update due date Successful");
			rs.setSuccess(true);
		}catch(Exception e) {
			rs.setMessage("Update due date failed");
			rs.setSuccess(false);
		}
		return rs;
	}
	
	@Transactional
	@RequestMapping(value="/deleteLoan",method=RequestMethod.POST, consumes= {"application/json"},produces= {"application/json"})
	public ResponseBody deleteLoan(@RequestBody Loan loan)  {
		StringBuffer str = new StringBuffer("");
		ResponseBody rs = new ResponseBody();
		try {
			ldao.deleteLoan("delete from tbl_book_loans where bookId = ? and branchId = ? and cardNo = ?",
					new Object[] {loan.getBookId(),loan.getBranchId(),loan.getCardNo()});
			rs.setMessage("Delete loan Successful");
			rs.setSuccess(true);
		}catch(Exception e) {
			rs.setMessage("Delete loan failed");
			rs.setSuccess(false);
		}
		return rs;
	}
}
