package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
		String[] msg=message.split(" ", 6);
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
			for (Users.User user : Users.getUsers())
				if (user.getUsername().equals(msg[1]))
					contains=true;
			Users.getReadWriteLock().readLock().unlock();
		}
		if (!contains)
		{
			String country="";
			if (msg.length==4 && msg[3].contains("country=\"") && (msg[3].indexOf("\"")!=msg[3].lastIndexOf("\"")))
				country=msg[3].substring(msg[3].indexOf("\"")+1, msg[3].lastIndexOf("\""));
			Users.User tmp=new Users.User(msg[1], msg[2], "normal", country, "0");
			Users.add(tmp);
			connections.send(connectionId, "ACK registration succeeded");
			return;
		}
		connections.send(connectionId, "ERROR registration failed");
	}

	private void login(String[] msg)
	{
		if (msg.length==3 &&
		    !connections.getConnectionHandler(connectionId).isLoggedIn() && connections.isConnected(msg[1]))
			for (Users.User user : Users.getUsers())
				if (user.getUsername().equals(msg[1]) && user.getPassword().equals(msg[2]))
				{
					Users.getReadWriteLock().readLock().unlock();
					connections.getConnectionHandler(connectionId).setLoggedIn(msg[1]);
					connections.send(connectionId, "ACK login succeeded");
					return;
				}
		connections.send(connectionId, "ERROR login failed");
		Users.getReadWriteLock().readLock().unlock();
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
				if (msg.length==5)
					requestAddMovie(msg[2], msg[3], msg[4], "");
				else
					requestAddMovie(msg[2], msg[3], msg[4], msg[5]);
				break;
			case "remmovie":
				requestRemoveMovie(msg[2]);
				break;
			case "changeprice":
				requestChangePrice(msg[2], msg[3]);
		}
	}

	private void requestBalance()
	{
		for (Users.User user : Users.getUsers())
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				Users.getReadWriteLock().readLock().unlock();
				connections.send(connectionId, "ACK balance "+user.getBalance());
				return;
			}
		Users.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ERROR request balance info failed");
	}

	private void requestBalanceAdd(String amount)
	{
		for (Users.User user : Users.getUsers())
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				user.setBalance(amount);
				Users.getReadWriteLock().readLock().unlock();
				connections.send(connectionId, "ACK balance "+user.getBalance()+" added "+amount);
				return;
			}
		Users.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ERROR request balance add failed");
	}

	private void requestInfo(String movieName)
	{
		for (Movies.Movie movie : Movies.getMovies())
			if (movie.getName().equals(movieName))
			{
				StringBuilder bannedCountries=new StringBuilder();
				for (String countries : movie.getBannedCountries())
					bannedCountries.append("\""+countries+"\" ");
				Movies.getReadWriteLock().readLock().unlock();
				connections.send(connectionId, "ACK \""+movieName+"\" "+movie.getAvailableAmount()+" "+movie.getPrice()+" "+bannedCountries);
				return;
			}
		Movies.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ERROR request info failed");
	}

	private void requestInfo()
	{
		StringBuilder output=new StringBuilder();
		for (Movies.Movie movie : Movies.getMovies())
			output.append("\""+movie.getName()+"\""+" ");
		Movies.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ACK"+output.toString());
	}

	private void requestRent(String movieName)
	{
		for (Users.User user : Users.getUsers())
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				for (Movies.Movie movie : Movies.getMovies())
					if (movie.getName().equals(movieName))
					{
						movie.acquire();
						if (Integer.parseInt(user.getBalance()) >= Integer.parseInt(movie.getPrice()) &&
						    Integer.parseInt(movie.getAvailableAmount())>0 &&
						    !movie.getBannedCountries().contains(user.getCountry()) &&
						    user.addMovie(new Users.User.Movie(movie.getId(), movie.getName())))
						{
							user.setBalance("-"+movie.getPrice());
							movie.setAvailableAmount(""+(Integer.parseInt(movie.getAvailableAmount())-1));
							movie.release();
							Movies.getReadWriteLock().readLock().unlock();
							Users.getReadWriteLock().readLock().unlock();
							connections.send(connectionId, "ACK rent \""+movie.getName()+"\" success");
							connections.broadcast(
									"BROADCAST movie \""+movie.getName()+"\" "+movie.getAvailableAmount()+" "+movie.getPrice());
							return;
						}
						else
						{
							movie.release();
							Movies.getReadWriteLock().readLock().unlock();
							Users.getReadWriteLock().readLock().unlock();
							connections.send(connectionId, "ERROR request rent failed");
							return;
						}
					}
				Movies.getReadWriteLock().readLock().unlock();
				Users.getReadWriteLock().readLock().unlock();
				connections.send(connectionId, "ERROR request rent failed");
				return;
			}
		Users.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ERROR request rent failed");
	}

	private void requestReturn(String movieName)
	{
		for (Users.User user : Users.getUsers())
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				Iterator<Users.User.Movie> iterator=user.getMovies().iterator();
				Users.User.Movie movie;
				while (iterator.hasNext())
				{
					movie=iterator.next();
					if (movie.getName().equals(movieName))
					{
						user.remove(movie);
						connections.send(connectionId, "ACK return \""+movieName+"\" success");
						for (Movies.Movie movie1 : Movies.getMovies())
							if (movie1.getName().equals(movieName))
							{
								movie1.acquire();
								if (Integer.parseInt(movie1.getAvailableAmount())>0)
								{
									movie1.setAvailableAmount(""+(Integer.parseInt(movie1.getAvailableAmount())+1));
									movie1.release();
									Movies.getReadWriteLock().readLock().unlock();
									Users.getReadWriteLock().readLock().unlock();
									connections.broadcast("BROADCAST movie \""+movieName+"\" "+movie1.getAvailableAmount()+" "+movie1.getPrice()+" ");
									return;
								}
								movie1.release();
							}
					}
//					break;
				}
			}
		Movies.getReadWriteLock().readLock().unlock();
		Users.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ERROR request return failed");
	}

	private void requestAddMovie(String movieName, String amount, String price, String bannedCountry)
	{
		if (Integer.parseInt(amount)>0 || Integer.parseInt(price)>0)
		{
			for (Users.User user : Users.getUsers())
			{
				if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()) && user.getType().equals("admin"))
				{
					Boolean found=false;
					for (Movies.Movie movie : Movies.getMovies())
						if (movie.getName().equals(movieName))
						{
							found=true;
							break;
						}
					Movies.getReadWriteLock().readLock().unlock();
					if (!found)
					{
						bannedCountry=bannedCountry.trim().substring(1, bannedCountry.length()-2);
						List<String> list=new ArrayList<>(Arrays.asList(bannedCountry.split("\" \"")));
						String id=""+(Integer.parseInt(Movies.getMovies().get(Movies.getMovies().size()-1).getId())+1);
						Movies.add(new Movies.Movie(id, movieName, price, list, amount, amount));
						Users.getReadWriteLock().readLock().unlock();
						connections.send(connectionId, "ACK addmovie \""+movieName+"\" success");
						connections.broadcast("BROADCAST movie \""+movieName+"\" "+amount+" "+price+" ");
						return;
					}
					else
						break;
				}
			}
			Users.getReadWriteLock().readLock().unlock();
		}
		connections.send(connectionId, "ERROR request addmovie failed");
	}

	private void requestChangePrice(String movieName, String price)
	{
		if (Integer.parseInt(price)>0)
		{
			for (Users.User user : Users.getUsers())
			{
				if (user.getUsername()
				        .equals(connections.getConnectionHandler(connectionId).getUsername()) && user.getType().equals("admin"))
				{
					for (Movies.Movie movie : Movies.getMovies())
						if (movie.getName().equals(movieName))
						{
							movie.setPrice(price);
							Movies.getReadWriteLock().readLock().unlock();
							Users.getReadWriteLock().readLock().unlock();
							connections.send(connectionId, "ACK changeprice \""+movieName+"\" success");
							connections.broadcast("BROADCAST movie \""+movieName+"\" "+movie.getAvailableAmount()+" "+price+" ");
							return;
						}
					Movies.getReadWriteLock().readLock().unlock();
				}
			}
			Users.getReadWriteLock().readLock().unlock();
		}
		connections.send(connectionId, "ERROR request changeprice failed");
	}

	private void requestRemoveMovie(String movieName)
	{
		for (Users.User user : Users.getUsers())
			if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
			{
				if (!user.getType().equals("admin"))
				{
					Users.getReadWriteLock().readLock().unlock();
					connections.send(connectionId, "ERROR request remmovie failed");
					return;
				}
				for (Movies.Movie movie : Movies.getMovies())
				{
					if (movie.getName().equals(movieName))
					{
						movie.acquire();
						if (movie.getAvailableAmount().equals(movie.getTotalAmount()))
						{
							Movies.getReadWriteLock().readLock().unlock();
							Movies.remove(movie);
							movie.release();
							Users.getReadWriteLock().readLock().unlock();
							connections.send(connectionId, "ACK remmovie \""+movieName+"\" success");
							connections.broadcast("BROADCAST movie \""+movieName+"\" removed");
							return;
						}
						movie.release();
					}
				}
				Movies.getReadWriteLock().readLock().unlock();
				Users.getReadWriteLock().readLock().unlock();
				connections.send(connectionId, "ERROR request remmovie failed");
				return;
			}
		Users.getReadWriteLock().readLock().unlock();
		connections.send(connectionId, "ERROR request remmovie failed");
	}
}