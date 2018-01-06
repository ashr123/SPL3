package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.Movies;
import bgu.spl181.net.impl.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class TPCMain
{
	public static Users users;
	public static Movies movies;

	public static void main(String[] args)
	{
		Gson gson=new GsonBuilder().excludeFieldsWithModifiers().create();
		try
		{
			users=gson.fromJson(new JsonReader(new FileReader("Users.json")), Users.class);
			movies=gson.fromJson(new JsonReader(new FileReader("Movies.json")), Movies.class);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
//		Users.users.get(0).getMovies().get(0).getId();
		try (Writer writer=new FileWriter("Output.json"))
		{
			gson.toJson(users, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try (Writer writer=new FileWriter("Output2.json"))
		{
			gson.toJson(movies, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}