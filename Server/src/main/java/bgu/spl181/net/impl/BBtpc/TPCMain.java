package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.MovieRentalServiceProtocol;
import bgu.spl181.net.srv.Server;

public class TPCMain
{
	public static void main(String[] args)
	{
		Server.threadPerClient(Integer.parseInt(args[0]), //port
		                       MovieRentalServiceProtocol::new, //protocol factory
		                       LineMessageEncoderDecoder::new //message encoder decoder factory
		                       ).serve();
	}
}