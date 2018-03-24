package com.gcit.library.service;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@CrossOrigin(origins="http://localhost:3000")
@RestController
public class BookService{
	
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
	@RequestMapping(value="/books",method=RequestMethod.GET)
	public ResponseEntity<Object> getBooks(@RequestParam(value="pageNo",required=false) Integer pageNo,
			@RequestParam(value="search",required=false) String search) {
		StringBuffer str = new StringBuffer("select * from tbl_book");
		List<Book> books = new LinkedList<Book>();
		try {
			if(pageNo != null && search != null) {
				String query = "%"+search+"%";
				str.append(" where title like ? limit ?,?");
				books = bdao.getBooks(str.toString(),new Object[] {query,(pageNo-1)*10,10});
			}else {
				if(pageNo != null) {
					str.append(" limit ?,?;");
					books = bdao.getBooks(str.toString(),new Object[] {(pageNo-1)*10,10});
				} else if(search != null){
					String query = "%"+search+"%";
					str.append(" where title like ?;");
					books = bdao.getBooks(str.toString(),new Object[] {query});
				} else {
					books = bdao.getBooks(str.toString(),null);
				}
			}
			for(Book book: books) {
				book.setAuthors(adao.getAuthors("select author.authorId, author.authorName from tbl_author author\n" + 
						"join tbl_book_authors authors on author.authorId = authors.authorId\n" + 
						"where authors.bookId = ?",new Object[] {book.getId()}));
				book.setBranches(brdao.getBranches("select branch.branchId, branch.branchName, branch.branchAddress, copy.noOfcopies from tbl_library_branch branch\n" + 
						"join tbl_book_copies copy on copy.branchId = branch.branchId\n" + 
						"where copy.bookId = ?;", new Object[] {book.getId()}));
				book.setGenres(gdao.getGenreForBook(book.getId()));
				book.setPublisher(pdao.getPublishers("select pub.publisherId,pub.publisherName,pub.publisherAddress,pub.publisherPhone from tbl_publisher pub\n" + 
						"join tbl_book book on book.pubId = pub.publisherId\n" + 
						"where book.bookId = ?;", new Object[] {book.getId()}).get(0));
			}
			return new ResponseEntity<Object>(books,HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/books/{bookId}",method=RequestMethod.GET)
	public ResponseEntity<Object> getBookByPK(@PathVariable(value="bookId") Integer bookId) {
		StringBuffer str = new StringBuffer("select * from tbl_book where bookId = ?");
		List<Book> books = new LinkedList<Book>();
		try {
			books = bdao.getBooks(str.toString(), new Object[] {bookId});
			if(books.size() == 0) {
				return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
			} 
			Book book = books.get(0);
			book.setAuthors(adao.getAuthors("select author.authorId, author.authorName from tbl_author author\n" + 
					"join tbl_book_authors authors on author.authorId = authors.authorId\n" + 
					"where authors.bookId = ?", new Object[] {book.getId()}));
			book.setBranches(brdao.getBranches("select branch.branchId, branch.branchName, branch.branchAddress, copy.noOfcopies from tbl_library_branch branch\n" + 
					"join tbl_book_copies copy on copy.branchId = branch.branchId\n" + 
					"where copy.bookId = ?;", new Object[] {book.getId()}));
			book.setGenres(gdao.getGenreForBook(book.getId()));
			book.setPublisher(pdao.getPublishers("select pub.publisherId,pub.publisherName,pub.publisherAddress,pub.publisherPhone from tbl_publisher pub\n" + 
						"join tbl_book book on book.pubId = pub.publisherId\n" + 
						"where book.bookId = ?;", new Object[] {book.getId()}).get(0));
			return new ResponseEntity<Object>(book,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/books/count",method=RequestMethod.GET)
	public ResponseEntity<Object> getBookCount(@RequestParam(value="search",required=false)  String search)  {
		StringBuffer str = new StringBuffer("select count(*) from tbl_book ");
		Integer count = 0;
		try {
			if(search != null) {
				String searchCondition = "%"+search+"%";
				str.append("where title like ?");
				count = bdao.getBookCount(str.toString(),new Object[] {searchCondition});
			} else {
				count = bdao.getBookCount(str.toString(),null);
			}
			return new ResponseEntity<Object>(count,HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	@RequestMapping(value="/books/{bookId}",method=RequestMethod.PUT)
	public ResponseEntity<Object> updateBook(@RequestBody Book book, @PathVariable(value="bookId") Integer bookId)  {
		try{
			bdao.updateBook(book);
			URI location = URI.create("/books/"+bookId);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(location);
			return new ResponseEntity<Object>(headers,HttpStatus.NO_CONTENT);
		} catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	public List<Loan> isReturned(Integer bookId)  {
		return ldao.getLoans("select book.bookId, branch.branchId, borrower.cardNo,book.title, borrower.name,branch.branchName,loan.dateOut,loan.dueDate,loan.dateIn from tbl_book book\n" + 
				"join tbl_book_loans loan on book.bookId = loan.bookId\n" + 
				"join tbl_library_branch branch on loan.branchId = branch.branchId\n" + 
				"join tbl_borrower borrower on loan.cardNo = borrower.cardNo\n" + 
				"where loan.dateIn is null and book.bookId = ?;",new Object[] {bookId});
	}

	@Transactional
	@RequestMapping(value="/books/{bookId}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteBookByPK(@PathVariable("bookId") Integer bookId)  {
		try {
			List<Loan> loans = isReturned(bookId);
			if(loans != null && loans.size() > 0 ) {
				return new ResponseEntity<Object>(loans,HttpStatus.OK);
			}
			bdao.deleteByPK(bookId);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Transactional
	@RequestMapping(value="/books", method=RequestMethod.POST)
	public ResponseEntity<Object> addBook(@RequestBody Book book)  {
		try {
			Integer pk = bdao.addBookGetPK(book);
			book.setId(pk);
			bdao.insertBook(book);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create("/books/"+pk));
			return new ResponseEntity<Object>(headers,HttpStatus.CREATED);
		} catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
