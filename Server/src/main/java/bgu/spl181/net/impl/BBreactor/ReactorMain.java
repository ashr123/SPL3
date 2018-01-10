package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.MovieRentalServiceProtocol;
import bgu.spl181.net.srv.Server;

public class ReactorMain
{

	public static void main(String[] args)
	{
		Server.reactor(Runtime.getRuntime().availableProcessors(),
		               Integer.parseInt(args[0]), //port
		               MovieRentalServiceProtocol::new, //protocol factory
		               LineMessageEncoderDecoder::new //message encoder decoder factory
		                ).serve();
	}
}