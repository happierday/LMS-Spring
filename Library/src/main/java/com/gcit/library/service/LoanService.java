/**
 * 
 */
package com.gcit.library.service;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.LoanDao;
import com.gcit.library.model.Loan;

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
	@RequestMapping(value="/loans", method=RequestMethod.GET)
	public ResponseEntity<Object> getLoans(@RequestParam(value="pageNo",required=false) Integer pageNo,
			@RequestParam(value="searchTitle",required=false) String searchTitle) {
		List<Loan> loans = null;
		StringBuffer str = new StringBuffer("select book.bookId, branch.branchId, borrower.cardNo,book.title, borrower.name,branch.branchName,loan.dateOut,loan.dueDate,loan.dateIn from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n" + 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo");
		try {
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
			return new ResponseEntity<Object>(loans,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/loans/count",method=RequestMethod.GET)
	public ResponseEntity<Object> getLoansCount(@RequestParam(value="searchTitle",required=false)  String searchTitle)  {
		StringBuffer str = new StringBuffer("select count(*) from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n"+ 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo");
		Integer count = 0;
		try {
			if(searchTitle != null) {
				String searchCondition = "%"+searchTitle+"%";
				str.append(" where book.title like ?");
				count = ldao.getLoansCount(str.toString(),new Object[] {searchCondition});
			}else {
				count = ldao.getLoansCount(str.toString(), null);
			}
			return new ResponseEntity<Object>(count,HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/loans",method=RequestMethod.PUT)
	public ResponseEntity<Object> updateLoan(@RequestBody Loan loan)  {
		StringBuffer str = new StringBuffer("update tbl_book_loans set dueDate = ? where bookId = ? and branchId = ? and cardNo = ?");
		try {
			ldao.updateLoan(str.toString(),
					new Object[] {Date.valueOf(loan.getDueDate()),loan.getBookId(),loan.getBranchId(),loan.getCardNo()});
			return new ResponseEntity<Object>(HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/loans",method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteLoan(@RequestBody Loan loan)  {
		StringBuffer str = new StringBuffer("delete from tbl_book_loans where bookId = ? and branchId = ? and cardNo = ?");
		try {
			ldao.deleteLoan(str.toString(),
					new Object[] {loan.getBookId(),loan.getBranchId(),loan.getCardNo()});
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
