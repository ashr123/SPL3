package bgu.spl181.net.impl;

import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Movies
{
	public static List<Movie> movies;

	public static class Movie
	{
		private String id;
		private String name;
		private String price;
		private List<String> bannedCountries;
		private String availableAmount;
		private String totalAmount;

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

		public List<String> getBannedCountries()
		{
			return bannedCountries;
		}

		public void setBannedCountries(List<String> bannedCountries)
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

		private void toJson()
		{
			try (Writer writer=new FileWriter("Movies.json"))
			{
				new GsonBuilder().excludeFieldsWithModifiers().create().toJson(this, writer);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}