package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, java.io.Closeable, ConnectionHandler<T>
{
	private final BidiMessagingProtocol<T> protocol;
	private final MessageEncoderDecoder<T> encdec;
	private final Socket sock;
	private BufferedInputStream in;
	private BufferedOutputStream out;
	private volatile boolean
			connected=true;
	private String username="";

	public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol)
	{
		this.sock=sock;
		this.encdec=reader;
		this.protocol=protocol;
	}

	@Override
	public void run()
	{
		try (Socket sock=this.sock)
		{ //just for automatic closing
			int read;

			in=new BufferedInputStream(sock.getInputStream());
			out=new BufferedOutputStream(sock.getOutputStream());

			while (!protocol.shouldTerminate() && connected && (read=in.read()) >= 0)
			{
				T nextMessage=encdec.decodeNextByte((byte)read);
				if (nextMessage!=null)
				{
					/*T response=*/
					protocol.process(nextMessage);
					//					if (response!=null)
					//					{
					//						out.write(encdec.encode(response));
					//						out.flush();
					//					}
				}
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException
	{
		connected=false;
		sock.close();
	}

	@Override
	public void send(T msg)
	{
		try
		{
			out.write(encdec.encode(msg));
			out.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isLoggedIn()
	{
		return !username.equals("");
	}

	@Override
	public void setLoggedIn(String username)
	{
		this.username=username;
	}

	@Override
	public String getUsername()
	{
		return username;
	}
}