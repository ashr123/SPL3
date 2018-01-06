package bgu.spl181.net.impl;

import java.util.List;

public class Users
{
	public List<User> users;

	public class User
	{
		private String username;
		private String password;
		private String type;
		private String country;
		private String balance;

		public void setMovies(List<Movie> movies)
		{
			this.movies=movies;
		}

		private List<Movie> movies;

		public class Movie
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

		public String getBalance()
		{
			return balance;
		}

		public void setBalance(String balance)
		{
			this.balance=balance;
		}
	}
}