package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.gcit.library.model.Branch;

public class BranchDao extends BaseDao<Branch> implements ResultSetExtractor<List<Branch>>{

	public List<Branch> getBranchForBook(Integer bookId) {
		return mysqlTemplate.query("select branch.branchId, branch.branchName, branch.branchAddress, copy.noOfcopies from tbl_library_branch branch\n" + 
				"join tbl_book_copies copy on copy.branchId = branch.branchId\n" + 
				"where copy.bookId = ?;", new Object[] {bookId},this);
	}

	public List<Branch> getAllBranches(String sql, Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public List<Branch> extractData(ResultSet rs) throws SQLException  {
		List<Branch> branchs = new LinkedList<Branch>();
		Branch branch = null;
		int size = rs.getMetaData().getColumnCount();
		while(rs.next()) {
			branch = new Branch();
			branch.setId(rs.getInt(1));
			branch.setName(rs.getString(2));
			branch.setAddress(rs.getString(3));
			if(size == 4) {
				branch.setCopies(rs.getInt(4));
			}
			branchs.add(branch);
		}
		return branchs;
	}
}
