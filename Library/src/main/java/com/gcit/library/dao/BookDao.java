package com.gcit.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.gcit.library.model.Author;
import com.gcit.library.model.Book;
import com.gcit.library.model.Branch;
import com.gcit.library.model.Genre;

public class BookDao extends BaseDao<Book> implements ResultSetExtractor<List<Book>>{

	public List<Book> getALlBook(String sql, Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public Integer getBookCount(String sql,Object[]values)  {
		return mysqlTemplate.queryForObject(sql,values,Integer.class);
	}
	
	public List<Book> getByPK(Integer bookId)  {
		return mysqlTemplate.query("select * from tbl_book where bookId = ?", new Object[] {bookId},this);
	}


	public List<Book> getBookByName(String search, Integer pageNo)  {
		String query = "%" + search + "%";
		if(pageNo == null) {
			return mysqlTemplate.query("select * from tbl_book where title like ?", new Object[] {query},this);
		}
		return mysqlTemplate.query("select * from tbl_book where title like ? limit ?,?", new Object[] {query,pageNo * 10,10},this);
	}
	

	public void updateBook(Book book) {
		mysqlTemplate.update("update tbl_book set title = ? where bookId = ?", new Object[] {book.getTitle(),book.getId()});
		mysqlTemplate.update("delete from tbl_book_genres where bookId = ?", new Object[] {book.getId()});
		if(book.getGenres()!= null) {
			for(Genre g : book.getGenres()) {
				mysqlTemplate.update("insert into tbl_book_genres values(?,?)", new Object[] {g.getId(),book.getId()});
			}
		}
		mysqlTemplate.update("delete from tbl_book_authors where bookId = ?", new Object[] {book.getId()});
		if(book.getAuthors() != null) {
			for(Author a : book.getAuthors()) {
				mysqlTemplate.update("insert into tbl_book_authors values(?,?)", new Object[] {book.getId(),a.getId()});
			}
		}
		mysqlTemplate.update("update tbl_book set pubId = ? where bookId = ?", new Object[] {book.getPublisher().getId(),book.getId()});
		mysqlTemplate.update("delete from tbl_book_copies where bookId = ?", new Object[] {book.getId()});
		if(book.getBranches() != null) {
			for(Branch b : book.getBranches()) {
				mysqlTemplate.update("insert into tbl_book_copies values(?,?,?)", new Object[] {book.getId(),b.getId(),b.getCopies()});
			}
		}
	}

	public void deleteByPK(Integer bookId){
		mysqlTemplate.update("delete from tbl_book where bookId = ?", new Object[] {bookId});
	}

	public Integer addBookGetPK(Book book)  {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		mysqlTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException  {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO tbl_book (title) VALUES (?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, book.getTitle());
		        return statement;
		    }
		}, holder);

		return holder.getKey().intValue();
	}

	public void insertBook(Book book) {
		for(Genre g: book.getGenres()) {
			mysqlTemplate.update("insert into tbl_book_genres values(?,?)", new Object[] {g.getId(),book.getId()});
		}
		for(Author a: book.getAuthors()) {
			mysqlTemplate.update("insert into tbl_book_authors values(?,?)", new Object[] {book.getId(),a.getId()});
		}
		mysqlTemplate.update("update tbl_book set pubId = ?  where bookId = ?", new Object[] {book.getPublisher().getId(),book.getId()});
		for(Branch branch: book.getBranches()) {
			mysqlTemplate.update("insert into tbl_book_copies values(?,?,?)", new Object[] {book.getId(),branch.getId(),branch.getCopies()});
		}
	}

	public List<Book> getBooksForAuthor(Integer authorId){
		return mysqlTemplate.query("select book.bookId, book.title from tbl_book book\n" + 
				"join tbl_book_authors ba on book.bookId = ba.bookId\n" + 
				"where ba.authorId = ?",new Object[] {authorId},this);
	}
	
	public List<Book> extractData(ResultSet rs) throws SQLException {
		List<Book> books = new ArrayList<Book>();
		Book book = null;
		while(rs.next()) {
			book = new Book();
			book.setId(rs.getInt(1));
			book.setTitle(rs.getString(2));
			books.add(book);
		}
		return books;
	}
}
