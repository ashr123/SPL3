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

public class Movies
{
	public static List<Movie> movies;
	private static transient Movies me;
	private static transient Gson gson=new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();

	static
	{
		try
		{
			me=gson.fromJson(new JsonReader(new FileReader("Movies.json")), Users.class);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public Movies()
	{
		me=this;
	}

	public static class Movie
	{
		private String id;
		private String name;
		private String price;
		private String bannedCountries;
		private String availableAmount;
		private String totalAmount;

		public Movie(String id, String name, String price, String bannedCountries, String availableAmount, String totalAmount)
		{
			this.id=id;
			this.name=name;
			this.price=price;
			this.bannedCountries=bannedCountries;
			this.availableAmount=availableAmount;
			this.totalAmount=totalAmount;
		}

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id=id;
			toJson();
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name=name;
			toJson();
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

		public String getBannedCountries()
		{
			return bannedCountries;
		}

		public void setBannedCountries(String bannedCountries)
		{
			this.bannedCountries=bannedCountries;
			toJson();
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

		public void setTotalAmount(String totalAmount)
		{
			this.totalAmount=totalAmount;
			toJson();
		}
	}

	public static boolean remove(Movie movie)
	{
		boolean b=movies.remove(movie);
		toJson();
		return b;
	}

	public static boolean add(Movie movie)
	{
		boolean b=movies.add(movie);
		toJson();
		return b;
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