package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

public class MovieRentalServiceProtocol<T> implements BidiMessagingProtocol<T>
{
	private boolean shouldTerminate;
	private int connectionId;
	private Connections<T> connections;

	@Override
	public void start(int connectionId, Connections<T> connections)
	{
		this.connectionId=connectionId;
		this.connections=connections;
	}

	@Override
	public void process(T message)
	{

	}

	@Override
	public boolean shouldTerminate()
	{
		return shouldTerminate;
	}
}