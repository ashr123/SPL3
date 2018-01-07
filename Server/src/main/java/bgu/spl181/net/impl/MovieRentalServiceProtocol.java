package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.util.Iterator;

public class MovieRentalServiceProtocol implements BidiMessagingProtocol<String>
{
	private boolean shouldTerminate;
	private int connectionId;
	private Connections<String> connections;

	@Override
	public void start(int connectionId, Connections<String> connections)
	{
		this.connectionId=connectionId;
		this.connections=connections;
	}

	@Override
	public void process(String message)
	{
		String[] msg=message.split(" ", 4);
		switch (msg[0])
		{
			case "REGISTER":
				register(msg);
				break;
			case "LOGIN":
				login(msg);
				break;
			case "SIGNOUT":
				signOut();
				break;
			case "REQUEST":
				request(msg);
		}
	}

	@Override
	public boolean shouldTerminate()
	{
		return shouldTerminate;
	}

	private void register(String[] msg)
	{
		Boolean contains=true;
		if (msg.length>2)
		{
			contains=false;
			for (Users.User user : Users.users)
			{
				if (user.getUsername().equals(msg[1]))
					contains=true;
			}
		}
		if (!contains)
		{
			String country="";
			if (msg.length==4 && msg[3].contains("country=\"") && (msg[3].indexOf("\"")!=msg[3].lastIndexOf("\"")))
				country=msg[3].substring(msg[3].indexOf("\"")+1, msg[3].lastIndexOf("\""));
			Users.User tmp=new Users.User(msg[1], msg[2], "normal", country, "0");
			Users.users.add(tmp);
			connections.send(connectionId, "ACK registration succeeded");
			return;
		}
		connections.send(connectionId, "ERROR registration failed");
	}

	private void login(String[] msg)
	{
		if (msg.length==3 &&
		    !connections.getConnectionHandler(connectionId).isLoggedIn() && connections.isConnected(msg[1]))
				for (Users.User user : Users.users)
					if (user.getUsername().equals(msg[1]) && user.getPassword().equals(msg[2]))
					{
						connections.getConnectionHandler(connectionId).setLoggedIn(msg[1]);
						connections.send(connectionId, "ACK login succeeded");
						return;
					}
		connections.send(connectionId, "ERROR login failed");
	}

	private void signOut()
	{
		if (connections.getConnectionHandler(connectionId).isLoggedIn())
		{
			connections.send(connectionId, "ACK signout succeeded");
			connections.disconnect(connectionId);
		}
		else
			connections.send(connectionId, "ERROR signout failed");
	}

	private void request(String[] msg)
	{
		switch (msg[1])
		{
			case "balance":
				if (msg[2].equals("add"))
					requestBalanceAdd(msg[3]);
				else
					requestBalance();
				break;
			case "info":
				if (msg.length==3)
					requestInfo(msg[2]);
				else
					requestInfo();
				break;
			case "rent":
				requestRent(msg[2]);
				break;
			case "return":
				requestReturn(msg[2]);
				break;
			case "addmovie":
				if(msg.length==6)

				break;
			case "remmovie":
				break;
			case "changeprice":
		}
	}

	private void requestBalance()
	{
		for (Users.User user : Users.users)
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				connections.send(connectionId, "ACK balance "+user.getBalance());
				return;
			}
		connections.send(connectionId, "ERROR request balance info failed");
	}

	private void requestBalanceAdd(String amount)
	{
		for (Users.User user : Users.users)
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				user.setBalance(amount);
				connections.send(connectionId, "ACK balance "+user.getBalance()+" added "+amount);
				return;
			}
		connections.send(connectionId, "ERROR request balance add failed");
	}

	private void requestInfo(String movieName)
	{
		for (Movies.Movie movie : Movies.movies)
			if (movie.getName().equals(movieName))
			{
				connections.send(connectionId,
				                 "ACK \""+movieName+"\" "+movie.getAvailableAmount()+" "+movie.getPrice()+" "+movie.getBannedCountries());
				return;
			}
		connections.send(connectionId, "ERROR request info failed");
	}

	private void requestInfo()
	{
		StringBuilder output=new StringBuilder();
		for (Movies.Movie movie : Movies.movies)
			output.append("\""+movie.getName()+"\""+" ");
		connections.send(connectionId, "ACK"+output.toString());
	}

	private void requestRent(String movieName)
	{
		for (Users.User user : Users.users)
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
				for (Movies.Movie movie : Movies.movies)
					if (movie.getName().equals(movieName))
						if (Integer.parseInt(user.getBalance())>=Integer.parseInt(movie.getPrice()) &&
						    Integer.parseInt(movie.getAvailableAmount())>0 &&
						    user.addMovie(new Users.User.Movie(movie.getId(), movie.getName())))
							{
								user.setBalance("-"+movie.getPrice());
								movie.setAvailableAmount(""+(Integer.parseInt(movie.getAvailableAmount())-1));
								connections.send(connectionId, "ACK rent \""+movie.getName()+"\" success");
								return;
							}
						else
						{
							connections.send(connectionId, "ERROR request rent failed");
							return;
						}
		connections.send(connectionId, "ERROR request rent failed");
	}

	private void requestReturn(String movieName)
	{
		for (Users.User user : Users.users)
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				Iterator<Users.User.Movie> iterator=user.getMovies().iterator();
				while (iterator.hasNext())
				{
					if (iterator.next().getName().equals(movieName))
					{
						iterator.remove();
						connections.send(connectionId, "ACK return \""+movieName+"\" success");
						for (Movies.Movie movie : Movies.movies)
							if (movie.getName().equals(movieName))
							{
								movie.setAvailableAmount(""+(Integer.parseInt(movie.getAvailableAmount())+1));
								connections.broadcast("BROADCAST movie \""+movieName+"\" "+movie.getAvailableAmount()+" "+movie.getPrice()+" ");
								return;
							}
					}
				}
			}
		connections.send(connectionId, "ERROR request return failed");
	}

	private void requestAddmovie(String namemovie,String amount,String price,String bannedcountry)
	{

	}
}