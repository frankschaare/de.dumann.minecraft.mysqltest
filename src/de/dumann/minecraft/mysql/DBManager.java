/**
 * 
 */
package de.dumann.minecraft.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.dumann.minecraft.model.User;


/**
 * Regelt die Verbindung zur Datenbank
 * Lesen: http://de.wikipedia.org/wiki/JavaServer_Faces
 * 
 * Name: ein Name, unter dem die Bean in den Views angesprochen wird
 * Eager: die Bean wird beim Starten des Kontexts (also der Applikation) initialisiert
 *
 */
@ManagedBean (name = "dbm", eager=true)
@SessionScoped
public class DBManager 
{
private Context initialContext = null;
private Context environmentContext  = null;
private DataSource ds = null;
private Connection con = null;
private PreparedStatement ps = null;
private ResultSet rs = null;

private String connectionInfo = "Keine Verbindung zur Datenbank !";

private ArrayList<User> userList = null;
private int anzahlUser = 0;

	/**
	 * Eine englische Beschreibung, wie eine MySQL Datasource konfiguriert werden muss, gibt es hier:
	 * http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html
	 * 
	 * Folgende Schritte sind nötig:
	 * 1: Tomcat Server unter \\Window\Preferences\Server\Server Runtimes einrichten
	 * 	  Im Projekt Explorer (links) sollte dann ein Punkt 'Servers' stehen
	 * 2: Die context.xml der Applikation anpassen
	 * 3: MySQL JDBC Connector von http://dev.mysql.com/downloads/connector/j/ herunterladen
	 *    Ich habe die Version 'Platform Independent (Architecture Independent), ZIP Archive' genommen
	 *    Der entpackte Download ist im Ordner 'mysql-connector-java-5.1.35'	
	 * 4: Der Server muss den MySQL JDBC Connector (also die Datei 'mysql-connector-java-5.1.35-bin.jar') sehen können
	 * 	  Dazu rechte Mausetaste über dem Projekt, dann \\Build Path\Configure Build Path
	 *    Bei mir hat das nicht auf Anhieb funktioniert, ich bekam den Fehler:
	 *    "Cannot load JDBC driver class 'com.mysql.jdbc.Driver'"
	 *    Ich habe dann die Datei mysql-connector-java-5.1.35-bin.jar einfach nach Tomcat 8.0\lib kopiert.
	 * 5: Dann die web.xml anpassen. Dazu folden Eintrag hinzufügen:
	 * 	  <resource-ref>
	 * 	       <description>Referenz auf den DB Eintrag in der Context.xml</description>
	 *         <res-ref-name>jdbc/MySQLTest</res-ref-name>
	 *         <res-type>javax.sql.DataSource</res-type>
	 *         <res-auth>Container</res-auth>
	 *    </resource-ref>		       
	 */
	public DBManager() 
	{
	init();	
	}

	private Connection init() 
	{
		try 
		{
		initialContext = new InitialContext();
		environmentContext  = (Context) initialContext.lookup("java:/comp/env");
		ds = (DataSource) environmentContext.lookup("jdbc/MySQLTest");
		con = ds.getConnection();
		} 
		catch (NamingException | SQLException e) 
		{
		e.printStackTrace();
		}
	return con;	
	}

	/*
	 * Informationen zur Verbindung ausgeben
	 */
	public String getConnectionInfo() 
	{
	String dbProductName = "unbekannt";
	String dbProductVersion = "unbekannt";
	
		try 
		{
		DatabaseMetaData metas = con.getMetaData();
		dbProductName = metas.getDatabaseProductName();
		dbProductVersion = metas.getDatabaseProductVersion();
		
		connectionInfo = "Verbunden mit " + dbProductName + " " + dbProductVersion;
		} 
		catch (SQLException e) 
		{
		e.printStackTrace();
		connectionInfo = "Keine Verbindung zur Datenbank !";		
		}	
	return connectionInfo;
	}
	
	/*
	 * Beispielabfrage der Tabelle 'actor' aus der mySQL-Beispieldatenbank 'sakila'
	 * Die User werden generiert und in einer ArrayList abgelegt.
	 * 
	 * Anschliessend wird die Grösse der Liste ausgegeben.
	 * 
	 */
	public int getAnzahlUser()
	{
	User user = null;	
		
		if (con != null) 
		{
		userList = new ArrayList<User>();
		
			try 
			{
			ps = con.prepareStatement("SELECT * FROM `sakila`.`actor`");
			rs = ps.executeQuery();

				// Durchlaufe das Resultset und baue für jede Zeile einen neuen Benutzer
				while (rs.next()) 
				{
				user = new User();
				user.setId(rs.getInt("actor_id"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				
				userList.add(user);
				}
			// Wenn die Schleife durchlaufen ist, wird die Grösse in 'anzahlUser' abgelegt:
			anzahlUser = userList.size();	
			} 
			catch (SQLException e) 
			{
			e.printStackTrace();
			}	
			// Dieser Block wird IMMER ausgeführt. Gut, um aufzuräumen !
			finally
			{
				if (rs != null) 
				{
					try 
					{
					rs.close();
					} 
					catch (SQLException e) 
					{
					e.printStackTrace();
					}					
				}
				if (ps != null) 
				{
					try 
					{
					ps.close();
					} 
					catch (SQLException e) 
					{
					e.printStackTrace();
					}					
				}
				
			}
		}
		
	return anzahlUser;	
	}

}
