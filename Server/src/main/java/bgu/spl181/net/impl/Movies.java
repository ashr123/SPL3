package bgu.spl181.net.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Movies
{
	private static List<Movie> movies;
	private static final transient ReadWriteLock readWriteLock=new ReentrantReadWriteLock(true);
	private static transient Movies me;
	private static final transient Gson gson=new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();

	static
	{
		synchronized (Movies.class)
		{
			if (me==null)
				try
				{
					me=gson.fromJson(new JsonReader(new FileReader("Movies.json")), Movies.class);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
		}
	}

	public static ReadWriteLock getReadWriteLock()
	{
		return readWriteLock;
	}

	public static class Movie
	{
		private String id;
		private String name;
		private String price;
		private final List<String> bannedCountries;
		private String availableAmount;
		private String totalAmount;
		private transient Semaphore semaphore/*=new Semaphore(1, true)*/;

		public Movie(String id, String name, String price, List<String> bannedCountries, String availableAmount, String totalAmount)
		{
			this.id=id;
			this.name=name;
			this.price=price;
			this.bannedCountries=bannedCountries;
			this.availableAmount=availableAmount;
			this.totalAmount=totalAmount;
			//semaphore=new Semaphore(1, true);
		}

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public String getPrice()
		{
			return price;
		}

		public void setPrice(String price)
		{
			this.price=price;
			toJson();
		}

		public List<String> getBannedCountries()
		{
			return bannedCountries;
		}

		public String getAvailableAmount()
		{
			return availableAmount;
		}

		public void setAvailableAmount(String availableAmount)
		{
			this.availableAmount=availableAmount;
			toJson();
		}

		public String getTotalAmount()
		{
			return totalAmount;
		}

		public void acquire()
		{
			if (semaphore==null)
				synchronized (this)
				{
					if (semaphore==null)
						semaphore=new Semaphore(1, true);
				}
			try
			{
				semaphore.acquire();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		public void release()
		{
			semaphore.release();
		}
	}

	public static boolean remove(Movie movie)
	{
		readWriteLock.writeLock().lock();
		boolean b=movies.remove(movie);
		toJson();
		readWriteLock.writeLock().unlock();
		return b;
	}

	public static boolean add(Movie movie)
	{
		readWriteLock.writeLock().lock();
		boolean b=movies.add(movie);
		toJson();
		readWriteLock.writeLock().unlock();
		return b;
	}

	public static List<Movie> getMovies()
	{
		readWriteLock.readLock().lock();
		return movies;
	}

	private static void toJson()
	{
		try (Writer writer=new FileWriter("Movies.json"))
		{
			gson.toJson(me, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}