package com.gcit.library.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.gcit.library.model.Author;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthorService {
	
	private String url = "http://localhost:8080";
	
	@Autowired
	AuthorDao adao;
	
	@Autowired
	BookDao bdao;
	
	@Transactional
	@RequestMapping(value="/authors",method=RequestMethod.GET)
	public ResponseEntity<Object> getAuthors(@RequestParam(value="pageNo",required=false) Integer pageNo,
			@RequestParam(value="search",required=false) String search){
		StringBuffer str = new StringBuffer("select * from tbl_author");
		List<Author> authors;
		try {
			if(pageNo == null && search == null) {
				authors = adao.getAuthors(str.toString(),null);
			}else {
				if(search != null) {
					str.append(" where authorName = ?");
				}
				if(pageNo != null) {
					str.append(" limit ?,?");
				}
				authors = adao.getAuthors(str.toString(), new Object[] {(pageNo-1)*10,10});
			}
			for(Author a : authors) {
				a.setBooks(bdao.getBooks("select book.bookId, book.title from tbl_book book \n" + 
						"join tbl_book_authors ba on book.bookId = ba.bookId\n" + 
						"where ba.authorId = ?",new Object[] {a.getId()}));
			}
			return new ResponseEntity<Object>(authors,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/authors/{authorId}",method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteAuthor(@PathVariable(value="authorId") Integer authorId){
		StringBuffer str = new StringBuffer("delete from tbl_author where authorId = ?");
		try {
			adao.deleteAuthorByPK(str.toString(), new Object[] {authorId});
			return new ResponseEntity<Object>(HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/authors/{authorId}",method=RequestMethod.PUT, consumes= {"application/json"},produces= {"application/json"})
	public ResponseEntity<Object> updateAuthor(@RequestBody Author author, @PathVariable(value="authorId") Integer authorId){
		try {
			adao.updateAuthor(author);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create(url+"/authors/"+authorId));
			return new ResponseEntity<Object>(headers,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/authors/{authorId}",method=RequestMethod.GET)
	public ResponseEntity<Object> getAuthorByPK(@PathVariable(value="authorId") Integer authorId){
		List<Author> authors = null;
		try {
			authors = adao.getAuthors("select * from tbl_author where authorId = ?", new Object[] {authorId});
			if(authors.size() == 0) {
				return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
			}
			Author author = authors.get(0);
			author.setBooks(bdao.getBooks("select book.bookId, book.title from tbl_book book \n" + 
					"join tbl_book_authors ba on book.bookId = ba.bookId\n" + 
					"where ba.authorId = ?", new Object[] {authorId}));
			return new ResponseEntity<Object>(author,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/authors",method=RequestMethod.POST, consumes= {"application/json"},produces= {"application/json"})
	public ResponseEntity<Object> addAuthor(@RequestBody Author author){
		try {
			Integer authorId = adao.addAuthorGetPK(author);
			author.setId(authorId);
			adao.insertAuthor(author);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create(url+"/authors/"+authorId));
			return new ResponseEntity<Object>(headers,HttpStatus.CREATED);
		} catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/authors/count",method=RequestMethod.POST)
	public ResponseEntity<Object> getAuthorCount(@RequestParam(value="search",required=false)String search){
		StringBuffer str = new StringBuffer("select count(*) from tbl_author ");
		Integer count = 0;
		try {
			if(search != null) {
				String searchCondition = "%"+search+"%";
				str.append("where authorName like ?");
				count = adao.getAuthorCount(str.toString(),new Object[] {searchCondition});
			} else {
				count = adao.getAuthorCount(str.toString(),null);
			}
			return new ResponseEntity<Object>(count,HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
