/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.srv.bidi;

import java.io.Closeable;

/**
 * @author bennyl
 */
public interface ConnectionHandler<T> extends Closeable
{
	/**
	 * sends msg {@code T} to the client. Should be used by <B>send</B> and <B>broadcast</B> in the <B>Connections</B> implementation
	 * @param msg the message to be sent
	 */
	void send(T msg);

	boolean isLoggedIn();

	void setLoggedIn(boolean loggedIn);
}