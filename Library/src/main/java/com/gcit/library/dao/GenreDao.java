package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.library.model.Genre;

@Component
public class GenreDao extends BaseDao<Genre> implements ResultSetExtractor<List<Genre>>{

	public List<Genre> getGenreForBook(Integer bookId) {
		return mysqlTemplate.query("select genre.genre_Id, genre.genre_Name from tbl_genre genre\n" + 
				"join tbl_book_genres genres on genre.genre_Id = genres.genre_Id\n" + 
				"where genres.bookId = ?", new Object[] {bookId},this);
	}

	public List<Genre> getAllGenres(String sql, Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public List<Genre> extractData(ResultSet rs) throws SQLException  {
		List<Genre> genres = new LinkedList<Genre>();
		Genre genre = null;
		while(rs.next()) {
			genre = new Genre();
			genre.setId(rs.getInt(1));
			genre.setName(rs.getString(2));
			genres.add(genre);
		}
		return genres;
	}
	
}
