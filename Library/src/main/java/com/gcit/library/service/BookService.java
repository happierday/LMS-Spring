package com.gcit.library.service;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.gcit.library.dao.BranchDao;
import com.gcit.library.dao.GenreDao;
import com.gcit.library.dao.LoanDao;
import com.gcit.library.dao.PublisherDao;
import com.gcit.library.model.Book;
import com.gcit.library.model.Loan;
import com.gcit.library.model.ResponseBody;

@CrossOrigin(origins="http://localhost:3000")
@RestController
public class BookService {

	@Autowired
	BookDao bdao;

	@Autowired
	AuthorDao adao;

	@Autowired
	BranchDao brdao;

	@Autowired
	GenreDao gdao;

	@Autowired
	PublisherDao pdao;
	
	@Autowired
	LoanDao ldao;

	@Transactional
	@RequestMapping(value="/getBooks",method=RequestMethod.GET)
	public ResponseEntity<Object> getAllBooks(@RequestParam(value="pageNo",required=false) Integer pageNo,
			@RequestParam(value="search",required=false) String search) {
		StringBuffer str = new StringBuffer("select * from tbl_book");
		List<Book> books = new LinkedList<Book>();
		if(pageNo != null && search != null) {
			books = bdao.getBookByName(search, pageNo-1);
		}else {
			if(pageNo != null) {
				str.append(" limit ?,?;");
				books = bdao.getALlBook(str.toString(),new Object[] {(pageNo-1)*10,10});
			} else {
				books = bdao.getALlBook(str.toString(),null);
			}
		}
		for(Book book: books) {
			book.setAuthors(adao.getAuthorForBook(book.getId()));
			book.setBranches(brdao.getBranchForBook(book.getId()));
			book.setGenres(gdao.getGenreForBook(book.getId()));
			book.setPublisher(pdao.getPublisherForBook(book.getId()).get(0));
		}
		return ResponseEntity.ok(books);
	}

	@Transactional
	@RequestMapping(value="/getBooks/{bookId}",method=RequestMethod.GET)
	public ResponseEntity<Object> getBookByPK(@PathVariable("bookId") String bookId)  {
		if(!bookId.matches("[0-9]+")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not acceptable url, expected to have an integer id to get a specific book!");
		}
		Book book = bdao.getByPK(Integer.parseInt(bookId)).get(0);
		book.setAuthors(adao.getAuthorForBook(book.getId()));
		book.setBranches(brdao.getBranchForBook(book.getId()));
		book.setGenres(gdao.getGenreForBook(book.getId()));
		book.setPublisher(pdao.getPublisherForBook(book.getId()).get(0));
		return ResponseEntity.ok(book);
	}


	@Transactional
	@RequestMapping(value="/getBooksCount",method=RequestMethod.GET)
	public Integer getBookCount(@RequestParam(value="search",required=false)  String search)  {
		StringBuffer str = new StringBuffer("select count(*) from tbl_book ");
		if(search != null) {
			String searchCondition = "%"+search+"%";
			str.append("where title like ?");
			return bdao.getBookCount(str.toString(),new Object[] {searchCondition});
		}
		return bdao.getBookCount(str.toString(),null);
	}

	/*
	{
		"title":"test",
		"authors":[
			{"id":1},{"id":2},{"id":3}	
		],
		"genres":[{"id":3},{"id":4},{"id":5}],
		"publisher": {"id":1},
		"branches":[
				{"id":1,"copies":10}
			]
	}
	 */
	@Transactional
	@RequestMapping(value="/updateBook",method=RequestMethod.POST, consumes = {"application/json"},produces= {"application/json"})
	public ResponseBody updateBook(@RequestBody Book book)  {
		ResponseBody rb = new ResponseBody();
		try{
			bdao.updateBook(book);
			rb.setSuccess(true);
			rb.setMessage("Update Book Successful!");
		} catch(Exception e) {
			rb.setSuccess(false);
			rb.setMessage("Update Book failed!");
		}
		return rb;
	}
	
	
	public List<Loan> isReturned(Integer bookId)  {
		return ldao.getLoanForBook(bookId);
	}

	@Transactional
	@RequestMapping(value="/deleteBook/{bookId}", method=RequestMethod.GET,produces= {"application/json"})
	public ResponseBody deleteBookByPK(@PathVariable("bookId") Integer bookId)  {
		ResponseBody response = new ResponseBody();
		List<Loan> loans = isReturned(bookId);
		if(loans != null && loans.size() > 0 ) {
			response.setData(loans);
			response.setSuccess(false);
			response.setMessage("Must return the book before delete it!");
			
		}else {
			bdao.deleteByPK(bookId);
			response.setSuccess(true);
			response.setMessage("Book Deleted!");
		}
		return response;
	}
//
//	public List<Book> getBookByName(String search,Integer pageNo)  {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			BookDAO bdao = new BookDAO(conn);
//			return bdao.getBookByName(search,pageNo);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		} finally{
//			if(conn!=null){
//				conn.close();
//			}
//		}
//		return null;
//	}
	
	@Transactional
	@RequestMapping(value="/addBook", method=RequestMethod.POST,consumes= {"application/json"},produces= {"application/json"})
	public ResponseBody addBook(@RequestBody Book book)  {
		ResponseBody res = new ResponseBody();
		Integer pk = bdao.addBookGetPK(book);
		try {
			book.setId(pk);
			bdao.insertBook(book);
			res.setMessage("Inserted Book Successful!");
			res.setSuccess(true);
		} catch(Exception e) {
			res.setMessage("Inserted Book Failed");
			res.setSuccess(false);
		}
		return res;
	}
	//
	//	public void insertBook(Book book,Integer[] genres, Integer[] authors, Integer pubId, List<Branch> branch)  {
	//		Connection conn = null;
	//		try {
	//			conn = connUtil.getConnection();
	//			BookDAO bdao = new BookDAO(conn);
	//			bdao.insertBook(book,genres,authors,pubId,branch);
	//			conn.commit();
	//		} catch (ClassNotFoundException | SQLException e) {
	//			e.printStackTrace();
	//			if(conn!=null){
	//				conn.rollback();
	//			}
	//		} finally{
	//			if(conn!=null){
	//				conn.close();
	//			}
	//		}
	//	}
}
