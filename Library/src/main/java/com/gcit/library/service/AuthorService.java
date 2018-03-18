package com.gcit.library.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.AuthorDao;
import com.gcit.library.dao.BookDao;
import com.gcit.library.model.Author;
import com.gcit.library.model.ResponseBody;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthorService {
	
	@Autowired
	AuthorDao adao;
	
	@Autowired
	BookDao bdao;
	
	@Transactional
	@RequestMapping(value="/getAuthors",method=RequestMethod.GET)
	public List<Author> getAllAuthors(@RequestParam(value="pageNo",required=false) Integer pageNo){
		StringBuffer str = new StringBuffer("select * from tbl_author");
		List<Author> authors;
		if(pageNo == null) {
			authors = adao.getAllAuthors(str.toString(),null);
		}else {
			str.append(" limit ?,?;");
			authors = adao.getAllAuthors(str.toString(), new Object[] {(pageNo-1)*10,10});
		}
		for(Author a : authors) {
			a.setBooks(bdao.getBooksForAuthor(a.getId()));
		}
		return authors;
	}
	
	@Transactional
	@RequestMapping(value="/deleteAuthor/{authorId}",method=RequestMethod.GET)
	public ResponseBody deleteAuthor(@PathVariable(value="authorId") Integer authorId){
		StringBuffer str = new StringBuffer("select * from tbl_author");
		ResponseBody rb = new ResponseBody();
		try {
			adao.deleteAuthorByPK(authorId);
			rb.setMessage("Delete Author successful");
			rb.setSuccess(true);
		}catch(Exception e) {
			rb.setMessage("Delete Author failed");
			rb.setSuccess(false);
		}
		return rb;
	}
	
	@Transactional
	@RequestMapping(value="/getAuthorCount",method=RequestMethod.GET)
	public Integer getBookCount(@RequestParam(value="search",required=false)  String search)  {
		StringBuffer str = new StringBuffer("select count(*) from tbl_author ");
		if(search != null) {
			String searchCondition = "%"+search+"%";
			str.append("where authorName like ?");
			return adao.getAuthorCount(str.toString(),new Object[] {searchCondition});
		}
		return adao.getAuthorCount(str.toString(),null);
	}
	
	@Transactional
	@RequestMapping(value="/updateAuthor",method=RequestMethod.POST, consumes= {"application/json"},produces= {"application/json"})
	public ResponseBody deleteAuthor(@RequestBody Author author){
//		System.out.println(author.getName());
		ResponseBody rb = new ResponseBody();
		try {
			adao.updateAuthor(author);
			rb.setMessage("Update Author successful");
			rb.setSuccess(true);
		}catch(Exception e) {
			e.printStackTrace();
			rb.setMessage("Update Author failed");
			rb.setSuccess(false);
		}
		return rb;
	}
	
//	public Author getAuthorByPK(Integer authorId) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			AuthorDAO bdao = new AuthorDAO(conn);
//			return bdao.getByPK(authorId).get(0);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		} finally{
//			if(conn!=null){
//				conn.close();
//			}
//		}
//		return null;
//	}
	
//	public Integer getAuthorCount(String search) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			AuthorDAO bdao = new AuthorDAO(conn);
//			return bdao.getAuthorCount(search);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		} finally{
//			if(conn!=null){
//				conn.close();
//			}
//		}
//		return null;
//	}
}
