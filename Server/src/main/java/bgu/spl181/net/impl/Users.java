package bgu.spl181.net.impl;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.List;

public class Users
{
	public static List<User> users;

	private class User
	{
		private String username;
		private String password;
		private String type;
		private String country;

		public void setMovies(List<Movie> movies)
		{
			this.movies=movies;
		}

		private List<Movie> movies;

		private class Movie
		{
			private String id;
			private String name;

			public String getId()
			{
				return id;
			}

			public String getName()
			{
				return name;
			}
		}

		public String getUsername()
		{
			return username;
		}

		public String getPassword()
		{
			return password;
		}

		public String getType()
		{
			return type;
		}

		public String getCountry()
		{
			return country;
		}

		public List<Movie> getMovies()
		{
			return movies;
		}
	}
}