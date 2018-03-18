/**
 * 
 */
package com.gcit.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	@RequestMapping(value="/getLoans", method=RequestMethod.GET)
	public List<Loan> getLoans(@RequestParam(value="pageNo",required=false) Integer pageNo) {
		List<Loan> loans = null;
		StringBuffer str = new StringBuffer("select book.bookId, branch.branchId, borrower.cardNo,book.title, borrower.name,branch.branchName,loan.dateOut,loan.dueDate,loan.dateIn from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n" + 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo");
		if(pageNo != null) {
			str.append(" limit ?,?");
			loans = ldao.getLoans(str.toString(), new Object[] {(pageNo-1)*10,10});
		} else {
			loans = ldao.getLoans(str.toString(), null);
		}
		return loans;
	}
	
}
