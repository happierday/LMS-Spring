package com.gcit.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.BranchDao;
import com.gcit.library.model.Branch;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class BranchService {
	
	@Autowired
	BranchDao brdao;
	
	@Transactional
	@RequestMapping(value="getBranches",method=RequestMethod.GET)
	public List<Branch> getAllBranch(@RequestParam(value="pageNo",required=false) Integer pageNo){
		StringBuffer str = new StringBuffer("select * from tbl_library_branch");
		if(pageNo != null) {
			str.append(" limit ?,?");
			return brdao.getAllBranches(str.toString(),new Object[] {(pageNo-1)*10,10});
		}

		return brdao.getAllBranches(str.toString(),null);
	}
}
