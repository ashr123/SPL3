package bgu.spl181.net.api.bidi;

public interface Connections<T>
{
	/**
	 * sends a message {@code T} to client represented by the given connectionId
	 *
	 * @param connectionId represents the client
	 * @param msg          the message to be sent
	 * @return {@code true} if the message sent, {@code false} otherwise
	 */
	boolean send(int connectionId, T msg);

	/**
	 * sends a message {@code T} to all active clients. This includes clients that has not yet completed log-in by the User service text based protocol. Remember, {@link Connections} belongs to the server pattern implementation, not the protocol!
	 *
	 * @param msg
	 */
	void broadcast(T msg);

	/**
	 * removes active client connId from map
	 * @param connectionId the client to be removed
	 */
	void disconnect(int connectionId);
}