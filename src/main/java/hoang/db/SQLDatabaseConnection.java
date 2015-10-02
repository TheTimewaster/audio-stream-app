package hoang.db;


import hoang.data.AlbumObject;
import hoang.data.StreamingTrackObject;
import hoang.data.TrackObject;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;


public class SQLDatabaseConnection
{
	Connection	     conn;

	SimpleDateFormat	SDF	= new SimpleDateFormat("mm:ss");

	public SQLDatabaseConnection() throws DBConnectionException
	{
		try
		{
			//root:5udI2EWa - system_music_db:xod31Alo
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(
			        "jdbc:mariadb://localhost:3306/music_db", "system_music_db", "xod31Alo");

		} 
		catch (ClassNotFoundException e)
		{
			throw new DBConnectionException(e.getMessage());
		} 
		catch (SQLException e)
		{
			throw new DBConnectionException(e.getMessage());
		}
	}
	
	public boolean authenticateUser(String _user, String _md5)
	{
		try
        {
	        PreparedStatement stmt = conn.prepareStatement("SELECT username, password FROM users WHERE username = '" + _user + "';");
	        
	        ResultSet rs = stmt.executeQuery();
	        
	        while(rs.next())
	        {
	        	if(rs.getString(1).equals(_user) && rs.getString(2).equals(_md5))
	        	{
	        		return true;
	        	}
	        }
        } 
		catch (SQLException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		return false;
	}

	public boolean isConnSuccess()
	{
		try
		{
			PreparedStatement stmt = conn
			        .prepareStatement("SELECT * from album;");
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				System.out.println(rs.getString("name"));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public JSONArray getAlbums() throws DBConnectionException
	{
		PreparedStatement stmt;
		JSONArray albumArray = new JSONArray();
		try
		{
			stmt = conn
			        .prepareStatement("SELECT alb.ID, alb.name, art.name as artist, alb.year, alb.cover"
			                + " FROM album alb"
			                + " LEFT JOIN artist art"
			                + " ON alb.artist = art.ID"
			                + " ORDER BY art.name ASC, alb.name ASC;");

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				AlbumObject albumObj = new AlbumObject(rs.getInt("ID"), rs.getString("name"), rs.getString("artist"), new Date(rs.getInt("year")), rs.getBytes("cover"));
		
				albumArray.put(albumObj.convertToJSON());
			}
		} catch (SQLException e)
		{	
			e.printStackTrace();

			try
			{
				conn.close();
			} catch (SQLException | NullPointerException e1)
			{

			}

			throw new DBConnectionException(e.getMessage());
		}
		
		return albumArray;
	}

	public JSONObject getAlbum(Integer _id) throws DBConnectionException
	{
		PreparedStatement stmt;

		List<TrackObject> trackArray = new ArrayList<TrackObject>();
//		JSONObject albumObj = new JSONObject();
		
		AlbumObject albumObj = null;
		try
		{
			stmt = conn
			        .prepareStatement("SELECT alb.ID, alb.name, art.name as artist, alb.year, alb.cover"
			                + " FROM album alb"
			                + " LEFT JOIN artist art"
			                + " ON alb.artist = art.ID"
			                + " WHERE alb.ID = "
			                + _id);

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				albumObj = new AlbumObject(rs.getInt("ID"), rs.getString("name"), rs.getString("artist"), new Date(rs.getInt("year")), rs.getBytes("cover"));
			}

			stmt = conn
			        .prepareStatement("SELECT track.ID, track_no, track_name, track_length"
			                + " FROM track"
			                + " LEFT JOIN album"
			                + " ON album.ID = track.album"
			                + " LEFT JOIN artist"
			                + " ON artist.ID = track.artist"
			                + " WHERE album.ID = " + _id);

			rs = stmt.executeQuery();

			while (rs.next())
			{
				TrackObject trackObj = new TrackObject(rs.getInt("ID"), rs.getString("track_name"), rs.getInt("track_no"), rs.getInt("track_length"));
				trackArray.add(trackObj);
			}
			
			albumObj.setTracks(trackArray);
			
		} catch (SQLException e)
		{
			e.printStackTrace();
			try
			{
				conn.close();
			} catch (SQLException | NullPointerException e1)
			{

			}

			throw new DBConnectionException(e.getMessage());
		}

		return albumObj.convertToJSON();
	}

	public JSONArray getArtists() throws DBConnectionException
	{
		PreparedStatement stmt;

		JSONArray artistArray = new JSONArray();
		try
		{
			stmt = conn.prepareStatement("SELECT * FROM artist;");

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				JSONObject artistObj = new JSONObject();
				artistObj.put("artistID", rs.getInt("ID"));
				artistObj.put("artist", rs.getString("name"));

				Random ran = new Random();
				String colorHex = Integer.toHexString(Color.HSBtoRGB(
				        ran.nextFloat(), 1f, 0.7f));
				artistObj.put("color",
				        "background-color: #" + colorHex.substring(2) + ";");

				artistArray.put(artistObj);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			try
			{
				conn.close();
			} catch (SQLException | NullPointerException e1)
			{

			}

			throw new DBConnectionException(e.getMessage());
		}

		return artistArray;
	}

	public StreamingTrackObject getTrack(int _trackID) throws DBConnectionException
	{
		PreparedStatement stmt;

		StreamingTrackObject track = null;

		try
		{
			stmt = conn
			        .prepareStatement("SELECT track.ID, track.file, track.file_length FROM track WHERE track.ID = "
			                + _trackID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				InputStream stream = new ByteArrayInputStream(
				        rs.getBytes("file"));

				track = new StreamingTrackObject(rs.getInt("file_length"), stream);
			}
		} catch (SQLException e)
		{
			throw new DBConnectionException(e.getMessage());
		}

		return track;
	}

	public JSONObject getArtist(int _artistID) throws DBConnectionException
	{
		PreparedStatement stmt;

		JSONObject artistObj = new JSONObject();

		JSONArray albumsArray = new JSONArray();

		try
		{
			stmt = conn
			        .prepareStatement("SELECT track.ID, track.track_name, track.track_no, track.track_length, album.name as album, album.year, album.cover, artist.name as artist"
			                + " FROM track"
			                + " LEFT JOIN album ON track.album = album.ID"
			                + " LEFT JOIN artist ON album.artist = artist.ID"
			                + " WHERE artist.ID = "
			                + _artistID
			                + " ORDER BY album.year, track.track_no");

			ResultSet rs = stmt.executeQuery();

			String albumNameString = "";

			JSONObject albumObject = null;
			JSONArray tracksArray = new JSONArray();

			while (rs.next())
			{
				artistObj.put("artist", rs.getString("artist"));
				
				if (!albumNameString.equals(rs.getString("album")))
				{
					if (albumNameString != null && albumObject != null)
					{
						albumObject.put("tracks", tracksArray);
						albumsArray.put(albumObject);
					}

					albumObject = new JSONObject();
					albumNameString = rs.getString("album");
					albumObject.put("album", albumNameString);
					albumObject.put("year", rs.getInt("year"));
					String encodedCoverImg = "data:image/png;base64,"
					        + Base64.encodeBase64String(rs.getBytes("cover"));
					albumObject.put("coverImg", new String(encodedCoverImg));

					tracksArray = new JSONArray();

				}

				JSONObject trackObj = new JSONObject();
				trackObj.put("trackID", rs.getInt("ID"));
				trackObj.put("track", rs.getInt("track_no"));
				trackObj.put("name", rs.getString("track_name"));
				trackObj.put("length",
				        SDF.format(new Date(rs.getInt("track_length"))));

				tracksArray.put(trackObj);
			}

			albumObject.put("tracks", tracksArray);
			albumsArray.put(albumObject);

			artistObj.put("albums", albumsArray);
		} catch (SQLException e)
		{
			throw new DBConnectionException(e.getMessage());
		}

		return artistObj;
	}
}
