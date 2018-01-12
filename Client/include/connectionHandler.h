#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__

#include <iostream>
#include <boost/asio.hpp>

using boost::asio::ip::tcp;

using namespace std;

class ConnectionHandler
{
private:
	const std::string host_;
	long port_;
	
	/**
	 * Provides core I/O functionality
	 */
	boost::asio::io_service io_service_;
	tcp::socket socket_;

public:
	ConnectionHandler(std::string host, long port);
	
	virtual ~ConnectionHandler();
	
	/**
	 * Connect to the remote machine
	 * @return true if connection succeeded
	 */
	bool connect();
	
	/**
	 * Read a fixed number of bytes from the server - blocking.
	 * @param bytes
	 * @param bytesToRead
	 * @return false in case the connection is closed before bytesToRead bytes can be read
	 */
	bool getBytes(char bytes[], unsigned int bytesToRead);
	
	/**
	 * Send a fixed number of bytes from the client - blocking
	 * @param bytes
	 * @param bytesToWrite
	 * @return false in case the connection is closed before all the data is sent
	 */
	bool sendBytes(const char bytes[], int bytesToWrite);
	
	/**
	 * Read an ascii line from the server
	 * @param line
	 * @return false in case connection closed before a newline can be read
	 */
	bool getLine(string &line);
	
	/**
	 * Send an ascii line from the server
	 * @param line
	 * @return false in case connection closed before all the data is sent
	 */
	bool sendLine(string &line);
	
	/**
	 * Get Ascii data from the server until the delimiter character
	 * @param frame
	 * @param delimiter
	 * @return false in case connection closed before null can be read
	 */
	bool getFrameAscii(string &frame, char delimiter);
	
	/**
	 * Send a message to the remote host
	 * @param frame
	 * @param delimiter
	 * @return false in case connection is closed before all the data is sent
	 */
	bool sendFrameAscii(const string &frame, char delimiter);
	
	/**
	 * Close down the connection properly
	 */
	void close();
	
};

#endif