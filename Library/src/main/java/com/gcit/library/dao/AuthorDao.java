package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.gcit.library.model.Author;
import com.gcit.library.model.Book;

public class AuthorDao extends BaseDao<Author> implements ResultSetExtractor<List<Author>>{


	public List<Author> getAuthorForBook(Integer bookId) {
		return mysqlTemplate.query("select author.authorId, author.authorName from tbl_author author\n" + 
				"join tbl_book_authors authors on author.authorId = authors.authorId\n" + 
				"where authors.bookId = ?", new Object[] {bookId},this);
	}

	public List<Author> getAllAuthors(String sql,Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public void deleteAuthorByPK(Integer authorId) {
		mysqlTemplate.update("delete from tbl_author where authorId = ?", new Object[] {authorId},this);
	}
	
	public Integer getAuthorCount(String sql, Object[] values) {
		return mysqlTemplate.queryForObject(sql,values,Integer.class);
	}
	

	public void updateAuthor(Author author) {
		mysqlTemplate.update("update tbl_author set authorName = ? where authorId = ?", new Object[] {author.getName(),author.getId()});
		mysqlTemplate.update("delete from tbl_book_authors where authorId = ?", new Object[] {author.getId()});
		for(Book b: author.getBooks()) {
			mysqlTemplate.update("insert into tbl_book_authors values(?,?);", new Object[] {b.getId(),author.getId()});
		}
	}
	
	public List<Author> extractData(ResultSet rs) throws SQLException   {
		List<Author> authors = new LinkedList<Author>();
		Author author = null;
		while(rs.next()) {
			author = new Author();
			author.setId(rs.getInt(1));
			author.setName(rs.getString(2));
			authors.add(author);
		}
		return authors;
	}
}
