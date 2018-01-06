package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.Movies;
import bgu.spl181.net.impl.Users;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TPCMain
{
	public static Users users;
	public static Movies movies;

	public static void main(String[] args)
	{
		try
		{
			users=new Gson().fromJson(new JsonReader(new FileReader("Users.json")), Users.class);
			movies=new Gson().fromJson(new JsonReader(new FileReader("Movies.json")), Movies.class);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}