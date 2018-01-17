package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ImplConnections<T> implements Connections<T>
{
	private final Map<Integer, ConnectionHandler<T>> connections=new HashMap<>();
	private final ReadWriteLock
			readWriteLock=new ReentrantReadWriteLock(true),
			readWriteLock2=new ReentrantReadWriteLock(true);

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
		readWriteLock.writeLock().lock();
		for (ConnectionHandler<T> connectionHandler : connections.values())
			connectionHandler.send(msg);
		readWriteLock.writeLock().unlock();
	}

	@Override
	public void disconnect(int connectionId)
	{
		readWriteLock.readLock().lock();
		readWriteLock2.readLock().lock();
//		try
//		{
//			connections.get(connectionId).close();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
		connections.remove(connectionId);
		readWriteLock2.readLock().unlock();
		readWriteLock.readLock().unlock();
	}

	@Override
	public void add(int id, ConnectionHandler<T> connectionHandler)
	{
		readWriteLock.readLock().lock();
		readWriteLock2.readLock().lock();
		connections.put(id, connectionHandler);
		readWriteLock2.readLock().unlock();
		readWriteLock.readLock().unlock();
	}

	@Override
	public ConnectionHandler<T> getConnectionHandler(int connectionId)
	{
		return connections.get(connectionId);
	}

	@Override
	public boolean isConnected(String username)
	{
		readWriteLock2.writeLock().lock();
		for (ConnectionHandler<T> connectionHandler : connections.values())
			if (connectionHandler.getUsername().equals(username))
			{
				readWriteLock2.writeLock().unlock();
				return true;
			}
		readWriteLock2.writeLock().unlock();
		return false;
	}
}