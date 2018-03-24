package com.gcit.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.library.model.Author;
import com.gcit.library.model.Book;

@Component
public class AuthorDao extends BaseDao<Author> implements ResultSetExtractor<List<Author>>{

	public List<Author> getAuthors(String sql,Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public void deleteAuthorByPK(String sql, Object[]values) {
		mysqlTemplate.update(sql,values);
	}
	
	public Integer getAuthorCount(String sql, Object[] values) {
		return mysqlTemplate.queryForObject(sql,values,Integer.class);
	}
	
	public Integer addAuthorGetPK(Author author)  {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		mysqlTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException  {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO tbl_Author (authorName) VALUES (?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, author.getName());
		        return statement;
		    }
		}, holder);

		return holder.getKey().intValue();
	}
	
	public void updateAuthor(Author author) {
		mysqlTemplate.update("update tbl_author set authorName = ? where authorId = ?", new Object[] {author.getName(),author.getId()});
		mysqlTemplate.update("delete from tbl_book_authors where authorId = ?", new Object[] {author.getId()});
		for(Book b: author.getBooks()) {
			mysqlTemplate.update("insert into tbl_book_authors values(?,?);", new Object[] {b.getId(),author.getId()});
		}
	}
	
	public void insertAuthor(Author author) {
		for(Book b : author.getBooks()) {
			mysqlTemplate.update("insert into tbl_book_authors values(?,?)",new Object[] {b.getId(),author.getId()});
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
