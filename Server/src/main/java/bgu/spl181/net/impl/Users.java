package bgu.spl181.net.impl;

import bgu.spl181.net.impl.BBtpc.TPCMain;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Users
{
	public static List<User> users;
	private static Users me;

	public Users()
	{
		me=this;
	}

	public static class User
	{
		private String username;
		private String password;
		private String type;
		private String country;
		private String balance;
		private  List<Movie> movies;
		private User me;

		public User(String username, String password, String type, String country, String balance)
		{
			me=this;
			this.username=username;
			this.password=password;
			this.type=type;
			this.country=country;
			this.balance=balance;
			this.movies=new ArrayList<>();
		}

		public boolean addMovie(Movie movie)
		{
			if (movies.contains(movie))
				return false;
			if (!movies.add(movie))
				return false;
			toJson();
			return true;
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
			toJson();
		}

		public boolean remove(Movie movie)
		{
			boolean b=movies.remove(movie);
			toJson();
			return b;
		}

		public boolean add(Movie movie)
		{
			boolean b=movies.add(movie);
			toJson();
			return b;
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

	public static boolean remove(User user)
	{
		boolean b=users.remove(user);
		toJson();
		return b;
	}

	public static boolean add(User user)
	{
		boolean b=users.add(user);
		toJson();
		return b;
	}

	private static void toJson()
	{
		try (Writer writer=new FileWriter("Users.json"))
		{
			new GsonBuilder().excludeFieldsWithModifiers().create().toJson(me, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}