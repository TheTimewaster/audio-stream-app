package hoang.db;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BSONObject;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


public class MongoDatabaseConnection
{
	public static final String	DEFAULT_MONGO_HOST	= "localhost";
	public static final int	   DEFAULT_MONGO_PORT	= 27017;
	public static final String	DEFAULT_DATABASE	= "music";
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("mm:ss");

	private MongoDatabase	   mongoDB;

	private MongoClient	       client;

	public MongoDatabaseConnection(String _host, int _port, String _database)
	{
		client = new MongoClient(_host, _port);
		mongoDB = client.getDatabase(_database);
	}

	public MongoDatabase getDbConn()
	{
		return mongoDB;
	}

//	@Deprecated
	public String getAllDocumentsInCollection()
	{
		MongoCollection<Document> musicCollection = mongoDB
		        .getCollection("db_album_metadata");
		
		MongoCursor<Document> cursor = musicCollection.find().iterator();
		
		JSONArray array = new JSONArray();

		try
		{	
			while(cursor.hasNext())
			{				
				Document doc = cursor.next();
				
				Document document = new Document();
				for(String s : doc.keySet())
				{
					if(s.equals("_id"))
					{
						document.put(s, doc.get(s).toString());
					}
					else
					{
						if("tracks".equals(s))
						{
							JSONArray tracks = new JSONArray();
							List<Document> list = (ArrayList<Document>) doc.get(s);
							for(Document innerDoc : list)
							{	
								Document track = new Document();
								for(String innerKey : innerDoc.keySet())
								{
									if("file".equals(innerKey))
									{
										DBRef pointer = (DBRef) innerDoc.get(innerKey);
										track.put(innerKey, pointer.getId().toString());
										System.out.println(pointer.getId().toString());
									}
									else if("length".equals(innerKey))
									{
										
										track.put(innerKey, SDF.format(
												new Date(
														Long.parseLong(
																innerDoc.get(innerKey).toString()
																)
																)
												)
												);
										
									}
									else
									{
										track.put(innerKey, innerDoc.get(innerKey));
									}
								}
								tracks.put(track);
							}
							
							document.put(s, tracks);
						}
						else
						{
							document.put(s, doc.get(s));
						}
					}
				}
				
				array.put(document);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();

		} finally
		{
			cursor.close();
			client.close();
		}

		System.out.println(array.toString());
		
		return array.toString();
	}
	
	public String getAllAlbumsFromCollection()
	{
		MongoCollection<Document> musicCollection = mongoDB
		        .getCollection("db_album_metadata");
		
		Bson eq = new BasicDBObject("tracks",0);
		
		MongoCursor<Document> cursor = musicCollection.find().projection(eq).iterator();
		
		try
		{	
			while(cursor.hasNext())
			{
				Document doc = cursor.next();
				System.out.println(doc.toJson());
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();

		} finally
		{
			cursor.close();
			client.close();
		}
		
		return "";
	}
	
	public String getDocumentInCollection(String _objectID)
	{
		String returnJSONString = "";

		MongoCollection<Document> musicCollection = mongoDB
		        .getCollection("db_album_metadata");
		
		MongoIterable<Document> iterable = musicCollection.find(new BasicDBObject("_id", _objectID));
		
		MongoCursor<Document> cursor = iterable.iterator();

		try
		{
			while (cursor.hasNext())
			{
				Document doc = cursor.next();
				returnJSONString = doc.toJson();
			}
		} catch (Exception e)
		{

		} finally
		{
			cursor.close();

			client.close();
		}

		return returnJSONString;
	}
	
	@Deprecated
	public InputStream getFirstFileFromMongoDB()
	{
		return getFileFromMongoDB("5580899e8c95fd101838af9d");
	}

	public InputStream getFileFromMongoDB(String _objectID)
	{
		GridFS fs = new GridFS(client.getDB("music"), "db_files");
		GridFSDBFile outputFile = fs.findOne(new ObjectId(_objectID));
		return outputFile.getInputStream();
	}
	
	public int getContentLength(String _objectID)
	{
			GridFS fs = new GridFS(client.getDB("music"), "db_files");
			GridFSDBFile outputFile = fs.findOne(new ObjectId(_objectID));
			return (int) outputFile.getLength();		
	}

	public void putFileInMongoDB(String _fullPath)
	{
		@SuppressWarnings("deprecation")
		// replace with new GridFS-API when mongodb-driver 3.1 is available!
		GridFS fs = new GridFS(client.getDB("music"), "db_files");

		File file = new File(_fullPath);
		GridFSInputFile inputFile = null;
		try
		{
			inputFile = fs.createFile(file);
			inputFile.setFilename(file.getName());
			inputFile.save();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
