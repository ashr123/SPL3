package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.MessagingProtocol;

import java.io.Closeable;
import java.util.function.Supplier;

public interface Server<T> extends Closeable
{
	/**
	 * This function returns a new instance of a thread per client pattern server
	 *
	 * @param port                  The port for the server socket
	 * @param protocolFactory       A factory that creates new MessagingProtocols
	 * @param encoderDecoderFactory A factory that creates new MessageEncoderDecoder
	 * @param <T>                   The Message Object for the protocol
	 * @return A new Thread per client server
	 */
	static <T> Server<T> threadPerClient(int port,
	                                     Supplier<MessagingProtocol<T>> protocolFactory,
	                                     Supplier<MessageEncoderDecoder<T>>
			                                     encoderDecoderFactory)
	{
		return new BaseServer<T>(port, protocolFactory, encoderDecoderFactory)
		{
			@Override
			protected void execute(BlockingConnectionHandler<T> handler)
			{
				new Thread(handler).start();
			}
		};
	}

	/**
	 * This function returns a new instance of a reactor pattern server
	 *
	 * @param nThreads              Number of threads available for protocol processing
	 * @param port                  The port for the server socket
	 * @param protocolFactory       A factory that creates new MessagingProtocols
	 * @param encoderDecoderFactory A factory that creates new MessageEncoderDecoder
	 * @param <T>                   The Message Object for the protocol
	 * @return A new reactor server
	 */
	static <T> Server<T> reactor(int nThreads,
	                             int port,
	                             Supplier<MessagingProtocol<T>> protocolFactory,
	                             Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory)
	{
		return new Reactor<>(nThreads, port, protocolFactory, encoderDecoderFactory);
	}

	/**
	 * The main loop of the server, Starts listening and handling new clients.
	 */
	void serve();
}