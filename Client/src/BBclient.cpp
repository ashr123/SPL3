#include "../include/connectionHandler.h"
#include <boost/thread.hpp>

bool toBeContinued = true;

class Task
{
private:
	ConnectionHandler &connectionHandler;

public:
	explicit Task(ConnectionHandler &connectionHandler) : connectionHandler(connectionHandler)
	{}
	
	void run()
	{
		while (true)
		{
			// We can use one of three options to read data from the server:
			// 1. Read a fixed number of characters
			// 2. Read a line (up to the newline character using the getLine() buffered reader
			// 3. Read up to the null character
			string answer;
			// Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
			// We could also use: connectionHandler.getLine(answer) and then get the answer without the newline char at the end
			if (!connectionHandler.getLine(answer))
			{
				cout<<"Disconnected. Exiting...\n"<<endl;
				break;
			}

			unsigned long len=answer.length();
			// A C string must end with a 0 char delimiter. When we filled the answer buffer from the socket
			// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
			answer.resize(len-1);
			cout<<answer<<endl;
			if (answer=="ACK signout succeeded")
			{
				toBeContinued=false;
				connectionHandler.close();
				cout<<"Ready to exit. press enter"<<endl;
				return;
			}
		}
	}
};

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main(int argc, char *argv[])
{
	if (argc<3)
	{
		cerr<<"Usage: "<<argv[0]<<" host port"<<endl<<endl;
		return -1;
	}
	string host=argv[1];
	long port=strtol(argv[2], nullptr, 10);
	
	ConnectionHandler connectionHandler(host, port);
	Task task(connectionHandler);
	boost::thread thread(&Task::run, &task);
	if (!connectionHandler.connect())
	{
		cerr<<"Cannot connect to "<<host<<":"<<port<<endl;
		return 1;
	}
	//From here we will see the rest of the echo client implementation:
	while(toBeContinued)
	{
		const short bufSize=1024;
		char buf[bufSize];
		cin.getline(buf, bufSize);
		string line(buf);
		if (toBeContinued && !connectionHandler.sendLine(line))
		{
			cout<<"Disconnected. Exiting...\n"<<endl;
			break;
		}
		// connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
		//cout<<"Sent "<<len+1<<" bytes to server"<<endl;
	}
	return 0;
}