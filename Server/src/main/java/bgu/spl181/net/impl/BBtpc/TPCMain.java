package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.Movies;
import bgu.spl181.net.impl.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.List;

public class TPCMain
{
	private static final Gson gson=new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();

//	static
//	{
//		try
//		{
//			users=gson.fromJson(new JsonReader(new FileReader("Users.json")), Users.class);
//			movies=gson.fromJson(new JsonReader(new FileReader("Movies.json")), Movies.class);
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//	}

	public static void main(String[] args)
	{
//		Users.users.get(0).getMovies().get(0).getId();
		//Users.add(new Users.User("bbb", "aaa", "xcx", "israel", "3456"));
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
		//String balance="100";
		//balance=""+(Integer.parseInt(balance)+Integer.parseInt("-"+"5"));
	}
}