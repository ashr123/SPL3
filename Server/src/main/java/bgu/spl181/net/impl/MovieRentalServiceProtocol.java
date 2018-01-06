package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.util.ArrayList;

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
				login(msg[1], msg[2]);
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
		if(msg.length>2)
		{
			contains=false;
			for (Users.User user : Users.users)
			{
				if (user.getUsername()==msg[1])
					contains=true;
			}
		}
		if(!contains)
		{
			String country="";
			if(msg.length==4 && msg[3].contains("country=\"") && (msg[3].indexOf("\"")!=msg[3].lastIndexOf("\"")) )
				country=msg[3].substring(msg[3].indexOf("\"")+1,msg[3].lastIndexOf("\""));
			Users.User tmp= new Users.User(msg[1], msg[2], "normal", country, "0", new ArrayList<>());
			Users.users.add(tmp);
			connections.send(connectionId, " ACK registration succeeded");
			return;
		}
		connections.send(connectionId,"ERROR registration failed");
	}

	private void login(String username, String password)
	{

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
				break;
			case "rent":
				break;
			case "return":
				break;
			case "addmovie":
				break;
			case "remmovie":
				break;
			case "changeprice":
				break;
//				connections.send(connectionId, "ERROR "+msg[1]+" failed");
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
}