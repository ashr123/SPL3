package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.HashMap;
import java.util.Map;

public class ImplConnections<T> implements Connections<T>
{
	private final Map<Integer, ConnectionHandler<T>> connections=new HashMap<>();

	@Override
	public boolean send(int connectionId, T msg)
	{
		if (connections.get(connectionId)!=null)
		{
			connections.get(connectionId).send(msg);
			return true;
		}
		return false;
	}

	@Override
	public void broadcast(T msg)
	{
		for (ConnectionHandler<T> connectionHandler : connections.values())
			connectionHandler.send(msg);
	}

	@Override
	public void disconnect(int connectionId)
	{
		connections.remove(connectionId);
	}

	@Override
	public void add(int id, ConnectionHandler<T> connectionHandler)
	{
		connections.put(id, connectionHandler);
	}

	@Override
	public ConnectionHandler<T> getConnectionHandler(int connectionId)
	{
		return connections.get(connectionId);
	}

	@Override
	public boolean isConnected(String username)
	{
		for (ConnectionHandler<T> connectionHandler : connections.values())
			if(connectionHandler.getUsername().equals(username))
				return true;
		return false;
	}
}