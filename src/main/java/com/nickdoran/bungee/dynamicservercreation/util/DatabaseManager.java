package com.nickdoran.bungee.dynamicservercreation.util;

import com.nickdoran.bungee.dynamicservercreation.DynamicServer;
import com.nickdoran.bungee.dynamicservercreation.DynamicServerCreation;
import com.nickdoran.bungee.dynamicservercreation.reply.DynamicServerReply;
import com.nickdoran.bungee.dynamicservercreation.reply.ServerExistReply;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public final class DatabaseManager {

    private static final String TABLE = "servers";

    private ExecutorService service = Executors.newFixedThreadPool(5);
    private Connection connection;
    private String host, user, password, database;
    private int port;

    /**
     * @param host     The MySQL Server Host.
     * @param user     The MySQL Server user.
     * @param password The password associated with {@param user}.
     * @param database The database, {@param user} and {@param password} have access to.
     * @param port     The port of the MySQL Server.
     */
    public DatabaseManager(String host, String user, String password, String database, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    /**
     * Kill the executorService, & shutdown MySQL connection.
     */
    public void shutdown() {
        service.shutdown();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a connection with the MySQL Server, initializes connection.
     *
     * @throws SQLException           An exception that provides information on a database access error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name.
     */
    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            final String URL = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
            connection = DriverManager.getConnection(URL, this.user, this.password);
        }
    }

    /**
     * Checks the table integrity of the database, ensures tables exist.
     */
    public void checkTableIntegrity() throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, TABLE, null);
        if (!rs.next()) {
            DynamicServerCreation.getInstance().getLogger().log(Level.INFO, "Database Table: {0} was not found! Creating it now!", TABLE);

            //Try with resource block automatically closes the PreparedStatement.
            try (
                    PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS dynamicservers . `" + TABLE + "` (\n" +
                            "  server_id   INT                  AUTO_INCREMENT,\n" +
                            "  server_name VARCHAR(30) NOT NULL,\n" +
                            "  server_type VARCHAR(15) NOT NULL,\n" +
                            "  server_ip   BINARY(16)  NOT NULL,\n" +
                            "  server_port INT(16)     NOT NULL DEFAULT 25565,\n" +
                            "  PRIMARY KEY (server_id)\n" +
                            ");")
            ) {
                statement.executeUpdate();
            }

            DynamicServerCreation.getInstance().getLogger().log(Level.INFO, "Successfully created Table: {0}!", TABLE);
        } else {
            DynamicServerCreation.getInstance().getLogger().log(Level.INFO, "Database Table: {0} was successfully found!", TABLE);
        }
    }

    /**
     * Adds the Dyanmic Server into the database.
     *
     * @param server Dynamic Server {@link DynamicServer}
     */
    public void addServer(DynamicServer server) {
        service.submit(() -> {
            try (
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + TABLE + "` (server_name, server_type, server_ip, server_port) " +
                            "VALUES ('" + server.getServerName() + "', '" + server.getServerType() + "','" + server.getInetAddress().getHostAddress() + "'," + server.getPort() + ");");
            ) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Checks database to see if server exist with {@param serverName}.
     *
     * @param serverName Name of target {@link DynamicServer}
     * @param callback   {@link ServerExistReply}
     */
    public void doesServerNameExist(String serverName, Callback<ServerExistReply> callback) {
        service.submit(() -> {
            try (
                    PreparedStatement statement = connection.prepareStatement("SELECT server_name FROM " + TABLE + " where server_name='" + serverName + "';");
                    ResultSet resultSet = statement.executeQuery();
            ) {
                callback.onCallback(new ServerExistReply(resultSet.next()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Non-Asynchronous, intentionally.
     */
    public void getDynamicServers(Callback<DynamicServerReply> callback) {
        try (
                PreparedStatement statement = connection.prepareStatement("SELECT server_id, server_name, server_type, server_ip, server_port FROM " + TABLE + ";");
                ResultSet resultSet = statement.executeQuery();
        ) {
            ArrayList<DynamicServer> temp = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("server_id");
                String serverName = resultSet.getString("server_name");
                String serverType = resultSet.getString("server_type");
                String serverIp = resultSet.getString("server_ip");
                int serverPort = resultSet.getInt("server_port");
                DynamicServer dynamicServer = new DynamicServer(serverName, serverType, serverIp, serverPort);
                temp.add(dynamicServer);
            }
            callback.onCallback(new DynamicServerReply(temp));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
