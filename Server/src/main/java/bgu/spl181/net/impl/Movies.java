package bgu.spl181.net.impl;

import java.util.List;

public class Movies
{
	public static List<Movie> movies;

	private class Movie
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
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name=name;
		}

		public String getPrice()
		{
			return price;
		}

		public void setPrice(String price)
		{
			this.price=price;
		}

		public List<String> getBannedCountries()
		{
			return bannedCountries;
		}

		public void setBannedCountries(List<String> bannedCountries)
		{
			this.bannedCountries=bannedCountries;
		}

		public String getAvailableAmount()
		{
			return availableAmount;
		}

		public void setAvailableAmount(String availableAmount)
		{
			this.availableAmount=availableAmount;
		}

		public String getTotalAmount()
		{
			return totalAmount;
		}

		public void setTotalAmount(String totalAmount)
		{
			this.totalAmount=totalAmount;
		}
	}
}