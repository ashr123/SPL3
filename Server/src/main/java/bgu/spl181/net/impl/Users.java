package bgu.spl181.net.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Users
{
	public static List<User> users;

	public static class User
	{
		private String username;
		private String password;
		private String type;
		private String country;
		private String balance;
		private List<Movie> movies;

		public User(String username, String password, String type, String country, String balance)
		{
			this.username=username;
			this.password=password;
			this.type=type;
			this.country=country;
			this.balance=balance;
			this.movies=new ArrayList<>();
		}

		public boolean addMovie(Movie movie)
		{
			return !movies.contains(movie) && movies.add(movie);
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
			this.balance=""+(Integer.parseInt(getBalance())+Integer.parseInt(balance));
		}

		public static class Movie
		{
			private String id;
			private String name;

			public Movie(String id, String name)
			{
				this.id=id;
				this.name=name;
			}

			@Override
			public boolean equals(Object o)
			{
				if (this==o)
					return true;
				if (!(o instanceof Movie))
					return false;
				Movie movie=(Movie)o;
				return Objects.equals(getId(), movie.getId()) &&
				       Objects.equals(getName(), movie.getName());
			}

			public String getId()
			{
				return id;
			}

			public String getName()
			{
				return name;
			}
		}
	}
}