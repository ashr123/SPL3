package bgu.spl181.net.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Users
{
	private static Collection<User> users;
	private static final transient ReadWriteLock readWriteLock=new ReentrantReadWriteLock(true);
	private static transient Users me;
	private static final transient String fileName="./Database/Users.json";
	private static final transient Gson gson=new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();

	static
	{
		synchronized (Users.class)
		{
			if (me==null)
				try(JsonReader jsonReader=new JsonReader(new FileReader(fileName)))
				{
					me=gson.fromJson(jsonReader, Users.class);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}

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
			if (movies.contains(movie))
				return false;
			boolean b=movies.add(movie);
			if (b)
				toJson();
			return b;
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
			if (movies.contains(movie))
				return false;
			boolean b=movies.add(movie);
			if (b)
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
				return getId().equals(movie.getId()) && getName().equals(movie.getName());
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
		readWriteLock.writeLock().lock();
		boolean b=users.remove(user);
		toJson();
		readWriteLock.writeLock().unlock();
		return b;
	}

	public static boolean add(User user)
	{
		readWriteLock.writeLock().lock();
		boolean b=users.add(user);
		toJson();
		readWriteLock.writeLock().unlock();
		return b;
	}

	public static Collection<User> getUsers()
	{
		readWriteLock.readLock().lock();
		return users;
	}

	public static ReadWriteLock getReadWriteLock()
	{
		return readWriteLock;
	}

	private static void toJson()
	{
		try (Writer writer=new FileWriter(fileName))
		{
			gson.toJson(me, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}