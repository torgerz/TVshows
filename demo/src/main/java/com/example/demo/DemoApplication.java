package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(DemoApplication.class, args);

		/*
		MongoClient client = MongoClients.create("mongodb://mongo:gHpBNq1VOo6hMQ4k@ac-ymvfq6n-shard-00-00.jdmytti.mongodb.net:27017,ac-ymvfq6n-shard-00-01.jdmytti.mongodb.net:27017,ac-ymvfq6n-shard-00-02.jdmytti.mongodb.net:27017/?ssl=true&replicaSet=atlas-90iccj-shard-0&authSource=admin&retryWrites=true&w=majority");

		MongoDatabase db = client.getDatabase("MediaData");

		MongoCollection col = db.getCollection("TVdata");

		Document testData = new Document("_id", "1").append("name", "Johnny English");

		col.insertOne(testData);
		*/

		//String apiData = ApiHandler.getJSONFromApi("girls");
		//MongoDB.setShowEpisodes(apiData);
		//System.out.print(ReadFile.ReadF());
		//ApiHandler.listThroughApi(ReadFile.ReadF()); // Read text file --> API --> Add in db
		//MongoDB.emptyDb();
		//MongoDB.insertBigShowList(ApiHandler.getBigShowList(), "ShowDB");
		//System.out.println(MongoDB.topThreeGenres());
		System.out.println(MongoDB.getRelevantShows(MongoDB.topThreeGenres()));
	}

}
