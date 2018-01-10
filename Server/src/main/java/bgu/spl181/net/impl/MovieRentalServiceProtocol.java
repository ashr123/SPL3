package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MovieRentalServiceProtocol implements BidiMessagingProtocol<String> {
    private boolean shouldTerminate;
    private int connectionId;
    private Connections<String> connections;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(String message) {
        String[] msg = message.split(" ", 6);
        switch (msg[0]) {
            case "REGISTER":
                register(msg);
                break;
            case "LOGIN":
                login(msg);
                break;
            case "SIGNOUT":
                signOut();
                break;
            case "REQUEST":
                request(msg);
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private void register(String[] msg) {
        Boolean contains = true;
        if (msg.length > 2) {
            contains = false;
            for (Users.User user : Users.getUsers()) {
                if (user.getUsername().equals(msg[1]))
                    contains = true;
            }
            Users.getReadWriteLock().readLock().unlock();
        }
        if (!contains) {
            String country = "";
            if (msg.length == 4 && msg[3].contains("country=\"") && (msg[3].indexOf("\"") != msg[3].lastIndexOf("\"")))
                country = msg[3].substring(msg[3].indexOf("\"") + 1, msg[3].lastIndexOf("\""));
            Users.User tmp = new Users.User(msg[1], msg[2], "normal", country, "0");
            Users.add(tmp);
            connections.send(connectionId, "ACK registration succeeded");
            return;
        }
        connections.send(connectionId, "ERROR registration failed");
    }

    private void login(String[] msg) {
        if (msg.length == 3 &&
                !connections.getConnectionHandler(connectionId).isLoggedIn() && connections.isConnected(msg[1]))
            for (Users.User user : Users.getUsers())
                if (user.getUsername().equals(msg[1]) && user.getPassword().equals(msg[2])) {
                    connections.getConnectionHandler(connectionId).setLoggedIn(msg[1]);
                    connections.send(connectionId, "ACK login succeeded");
	                Users.getReadWriteLock().readLock().unlock();
                    return;
                }
        connections.send(connectionId, "ERROR login failed");
	    Users.getReadWriteLock().readLock().unlock();
    }

    private void signOut() {
        if (connections.getConnectionHandler(connectionId).isLoggedIn()) {
            connections.send(connectionId, "ACK signout succeeded");
            connections.disconnect(connectionId);
        } else
            connections.send(connectionId, "ERROR signout failed");
    }

    private void request(String[] msg) {
        switch (msg[1]) {
            case "balance":
                if (msg[2].equals("add"))
                    requestBalanceAdd(msg[3]);
                else
                    requestBalance();
                break;
            case "info":
                if (msg.length == 3)
                    requestInfo(msg[2]);
                else
                    requestInfo();
                break;
            case "rent":
                requestRent(msg[2]);
                break;
            case "return":
                requestReturn(msg[2]);
                break;
            case "addmovie":
                if (msg.length == 5)
                    requestAddMovie(msg[2], msg[3], msg[4], "");
                else
                    requestAddMovie(msg[2], msg[3], msg[4], msg[5]);
                break;
            case "remmovie":
                requestRemoveMovie(msg[2]);
                break;
            case "changeprice":
                requestChangePrice(msg[2], msg[3]);
        }
    }

    private void requestBalance() {
        for (Users.User user : Users.getUsers())
            if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername())) {
                connections.send(connectionId, "ACK balance " + user.getBalance());
	            Users.getReadWriteLock().readLock().unlock();
                return;
            }
        connections.send(connectionId, "ERROR request balance info failed");
	    Users.getReadWriteLock().readLock().unlock();
    }

    private void requestBalanceAdd(String amount) {
        for (Users.User user : Users.getUsers())
            if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername())) {
                user.setBalance(amount);
                connections.send(connectionId, "ACK balance " + user.getBalance() + " added " + amount);
	            Users.getReadWriteLock().readLock().unlock();
                return;
            }
        connections.send(connectionId, "ERROR request balance add failed");
	    Users.getReadWriteLock().readLock().unlock();
    }

    private void requestInfo(String movieName) {
        for (Movies.Movie movie : Movies.getMovies())
            if (movie.getName().equals(movieName)) {
                StringBuilder bannedCountries = new StringBuilder();
                for (String countries : movie.getBannedCountries())
                    bannedCountries.append("\"" + countries + "\" ");
                connections.send(connectionId, "ACK \"" + movieName + "\" " + movie.getAvailableAmount() + " " + movie.getPrice() + " " + bannedCountries);
	            Movies.getReadWriteLock().readLock().unlock();
                return;
            }
        connections.send(connectionId, "ERROR request info failed");
    }

    private void requestInfo() {
        StringBuilder output = new StringBuilder();
        for (Movies.Movie movie : Movies.getMovies())
            output.append("\"" + movie.getName() + "\"" + " ");
        connections.send(connectionId, "ACK" + output.toString());
    }

    private void requestRent(String movieName) {
        for (Users.User user : Users.getUsers())
            if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()))
                for (Movies.Movie movie : Movies.getMovies())
                    if (movie.getName().equals(movieName))
                        if (Integer.parseInt(user.getBalance()) >= Integer.parseInt(movie.getPrice()) &&
                                Integer.parseInt(movie.getAvailableAmount()) > 0 &&
                                !movie.getBannedCountries().contains(user.getCountry()) &&
                                user.addMovie(new Users.User.Movie(movie.getId(), movie.getName()))) {
                            user.setBalance("-" + movie.getPrice());
                            movie.setAvailableAmount("" + (Integer.parseInt(movie.getAvailableAmount()) - 1));
                            connections.send(connectionId, "ACK rent \"" + movie.getName() + "\" success");
                            connections.broadcast(
                                    "BROADCAST movie \"" + movie.getName() + "\" " + movie.getAvailableAmount() + " " + movie.getPrice());
                            return;
                        } else {
                            connections.send(connectionId, "ERROR request rent failed");
                            return;
                        }
        connections.send(connectionId, "ERROR request rent failed");
    }

    private void requestReturn(String movieName) {
        for (Users.User user : Users.getUsers())
            if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername())) {
                Iterator<Users.User.Movie> iterator = user.getMovies().iterator();
                Users.User.Movie movie;
                while (iterator.hasNext()) {
                    movie = iterator.next();
                    if (movie.getName().equals(movieName)) {
                        user.remove(movie);
                        connections.send(connectionId, "ACK return \"" + movieName + "\" success");
                        for (Movies.Movie movie1 : Movies.getMovies())
                            if (movie1.getName().equals(movieName)) {
                                movie1.setAvailableAmount("" + (Integer.parseInt(movie1.getAvailableAmount()) + 1));
                                connections.broadcast(
                                        "BROADCAST movie \"" + movieName + "\" " + movie1.getAvailableAmount() + " " + movie1.getPrice() + " ");
                                return;
                            }
                        return;
                    }
                }
            }
        connections.send(connectionId, "ERROR request return failed");
    }

    private void requestAddMovie(String movieName, String amount, String price, String bannedCountry) {
        if (Integer.parseInt(amount) > 0 || Integer.parseInt(price) > 0) {
            for (Users.User user : Users.getUsers()) {
                if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername()) && user.getType().equals("admin")) {
                    Boolean found = false;
                    for (Movies.Movie movie : Movies.getMovies())
                        if (movie.getName().equals(movieName)) {
                            found = true;
                            break;
                        }
                    if (!found) {
                        bannedCountry = bannedCountry.trim().substring(1, bannedCountry.length() - 2);
                        List<String> list = new ArrayList<>(Arrays.asList(bannedCountry.split("\" \"")));
                        String id = "" + (Integer.parseInt(Movies.getMovies().get(Movies.getMovies().size() - 1).getId()) + 1);
                        Movies.add(new Movies.Movie(id, movieName, price, list, amount, amount));
                        connections.send(connectionId, "ACK addmovie \"" + movieName + "\" success");
                        connections.broadcast("BROADCAST movie \"" + movieName + "\" " + amount + " " + price + " ");
                        return;
                    } else
                        break;
                }
            }
        }
        connections.send(connectionId, "ERROR request addmovie failed");
    }

    private void requestChangePrice(String movieName, String price) {
        if (Integer.parseInt(price) > 0) {
            for (Users.User user : Users.getUsers()) {
                if (user.getUsername()
                        .equals(connections.getConnectionHandler(connectionId).getUsername()) && user.getType().equals("admin")) {
                    for (Movies.Movie movie : Movies.getMovies())
                        if (movie.getName().equals(movieName)) {
                            movie.setPrice(price);
                            connections.send(connectionId, "ACK changeprice \"" + movieName + "\" success");
                            connections.broadcast("BROADCAST movie \"" + movieName + "\" " + movie.getAvailableAmount() + " " + price + " ");
                            return;
                        }
                }
            }
        }
        connections.send(connectionId, "ERROR request changeprice failed");
    }

    private void requestRemoveMovie(String movieName) {
        for (Users.User user : Users.getUsers())
            if (user.getUsername().equals(connections.getConnectionHandler(connectionId).getUsername())) {
                if (!user.getType().equals("admin")) {
                    connections.send(connectionId, "ERROR request remmovie failed");
                    return;
                }
                for (Movies.Movie movie : Movies.getMovies()) {
                    if (movie.getName().equals(movieName))
                        if (movie.getAvailableAmount().equals(movie.getTotalAmount())) {
                            Movies.remove(movie);
                            connections.send(connectionId, "ACK remmovie \"" + movieName + "\" success");
                            connections.broadcast("BROADCAST movie \"" + movieName + "\" removed");
                            return;
                        }
                }
                connections.send(connectionId, "ERROR request remmovie failed");
                return;
            }
        connections.send(connectionId, "ERROR request remmovie failed");
    }
}