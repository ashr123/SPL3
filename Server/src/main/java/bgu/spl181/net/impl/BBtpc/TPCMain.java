package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.MovieRentalServiceProtocol;
import bgu.spl181.net.impl.Movies;
import bgu.spl181.net.impl.Users;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TPCMain
{
//	private static final Gson gson=new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();
//	private static Users users=new Users();

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
		//String[] msg="a b    c  d  e  g  j r   e    x".split(" ", 6);
//		String str="Location \"Welcome  to india\" Bangalore "+
//		           "Channai     \"IT city\"     Mysore";
//
//		List<String> list=new ArrayList<>();
//		Matcher m=Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
//		while (m.find())
//			list.add(m.group(1).replace("\"", ""));

		Server.threadPerClient(
				Integer.parseInt(args[0]), //port
				MovieRentalServiceProtocol::new, //protocol factory
				LineMessageEncoderDecoder::new //message encoder decoder factory
		                      ).serve();
//		Users.users.get(0).getMovies().get(0).getId();
		//Users.add(new Users.User("bbb", "aaa", "xcx", "israel", "3456"));
//		String[] msg="\"aaz\" \"sss\" \"ddd\" \"fff\"".replaceAll("\"", "").split(" ");
//		List<String> list=Arrays.asList(msg);
//
//		try (Writer writer=new FileWriter("Output.json"))
//		{
//			gson.toJson(users, writer);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		try (Writer writer=new FileWriter("Output2.json"))
//		{
//			gson.toJson(movies, writer);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
		//String balance="100";
		//balance=""+(Integer.parseInt(balance)+Integer.parseInt("-"+"5"));
	}
}