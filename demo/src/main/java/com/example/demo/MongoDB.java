package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.*;

import static com.mongodb.client.model.Aggregates.limit;

/**
 * API til Database
 * Metoder for å hente ut informasjon om show+episode osv.
 * Må sikkert ha litt sikrere metoder en public
 */

public class MongoDB {
    public static String uri = "mongodb://mongo:gHpBNq1VOo6hMQ4k@ac-ymvfq6n-shard-00-00.jdmytti.mongodb.net:27017,ac-ymvfq6n-shard-00-01.jdmytti.mongodb.net:27017,ac-ymvfq6n-shard-00-02.jdmytti.mongodb.net:27017/?ssl=true&replicaSet=atlas-90iccj-shard-0&authSource=admin&retryWrites=true&w=majority";

    /**
     * Connect to collection in MongoDB.
     */
    public static MongoCollection<Document> getCollection(String collection) {
        com.mongodb.client.MongoClient client = MongoClients.create(uri);
        MongoDatabase database = client.getDatabase("MediaData");
        return database.getCollection(collection);
    }

    /**
     *  Filter collection of choice by rating, weight, runtime or avg runtime.
     *  Takes in collection, type of filter, number of results.
     */
    public static ArrayList<Document> filterShows(String collection, String filter, int resNumber) {
        MongoCollection<Document> coll = getCollection(collection);
        ArrayList<Document> result = new ArrayList<>();

        FindIterable<Document> cursor = coll.find().sort(new BasicDBObject(filter, -1)).limit(resNumber);

        for (Document document : cursor) {
            result.add(document);
        }
        return result;
    }

     /**
     *  Filter collection of choice by Network.
     *
     */
    public static ArrayList<Document> filterShowsByNetwork(String collection, String network) {
        MongoCollection<Document> coll = getCollection(collection);
        ArrayList<Document> result = new ArrayList<>();

        BasicDBObject query = new BasicDBObject("network", "name");
        FindIterable<Document> cursor = coll.find(query).sort(new BasicDBObject(network, 1).append("rating.average", -1));

        for (Document document : cursor) {
            result.add(document);
        }
        return result;
    }

    /**
     * Method for setting shows from api to database.
     */
    public static void setShowEpisodes(String apiData, String collection){
        MongoCollection<Document> coll = getCollection(collection);

        List<InsertOneModel<Document>> docs = new ArrayList<>();

        try{
            docs.add(new  InsertOneModel<>(Document.parse(apiData)));
            coll.bulkWrite(docs, new BulkWriteOptions().ordered(false));

        } catch (MongoWriteException e) {
            System.out.println("Error");
        }

    }

    /**
     * Takes 250 (ish) shows from api and adds to database for recommendation calculation.
     */
    public static void insertBigShowList(String apiData, String collection) throws JsonProcessingException {
        List<InsertOneModel<Document>> docs = new ArrayList<>();
        MongoCollection<Document> coll = getCollection(collection);
        JsonParser parser = JsonParserFactory.getJsonParser();
        List<Object> list = parser.parseList(apiData);

        for(Object o : list){
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(o);

            try{
                docs.add(new InsertOneModel<>(Document.parse(json)));
                coll.bulkWrite(docs, new BulkWriteOptions().ordered(false));


            } catch (MongoWriteException e) {
                System.out.println("Error");
            }
            docs.clear();

        }

    }
    /**
     * Takes top 3 genres and returns highest rated 3 of each within them from a bigger database from API
     */

    public static ArrayList<Document> getRelevantShows(ArrayList<String> favGenres) {
        MongoCollection<Document> coll = getCollection("ShowDB");
        ArrayList<Document> result = new ArrayList<>();
        for (String gen : favGenres) {
            BasicDBObject query = new BasicDBObject("genres", gen);
            FindIterable<Document> cursor = coll.find(query).sort(new BasicDBObject("rating", -1)).limit(3);


            for (Document document : cursor) {
                result.add(document);
                //System.out.println(document);
            }
            query.clear();
        }


        return result;
    }

    public static boolean checkEmptyCollection(){
        com.mongodb.client.MongoClient client = MongoClients.create(uri);
        MongoDatabase database = client.getDatabase("MediaData");
        MongoCollection<Document> coll = database.getCollection("TVdata");
        if(coll.countDocuments() == 0){
            return true;
        }
        return false;
    }

    /**
     * Method for empty the user TV show data from database.
     */
    public static void emptyDb(){
        if (!checkEmptyCollection()) {
            try(MongoClient mc = MongoClients.create(uri)){
                MongoDatabase database = mc.getDatabase("MediaData");
                MongoCollection<Document> coll = database.getCollection("TVdata");
                Document query = new Document();
                try {
                    DeleteResult result = coll.deleteMany(query);
                    System.out.println("Deleted document count: " + result.getDeletedCount());
                } catch (MongoException me) {
                    System.err.println("Unable to delete due to an error: " + me);
                }

            }
        }
    }

    public static HashMap<String, Integer> makeHashmap(ArrayList<String> alString, ArrayList<Integer> alInt) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (int i = 0; i<alString.size()-1; i++) {
            hashMap.put(alString.get(i), alInt.get(i));
        }

        return hashMap;

    }

    /**
     * Gå gjennom alle, legge på listene om topp3, fjerne fra listen.
     */
    public static ArrayList<String> listCounter(ArrayList<String> list) {
        String tempGenre = "";
        int tempCounter = 0;
        ArrayList<String> tempList;
        tempList = list;
        ArrayList<Integer> topNum = new ArrayList<>();
        ArrayList<String> topGenres = new ArrayList<>();

        while(!tempList.isEmpty()) {
            tempGenre = tempList.get(0);
            tempList.remove(0);
            tempCounter++;
            ListIterator<String> name = tempList.listIterator();
            while (name.hasNext()) {
                if (name.next().equals(tempGenre)) {
                    tempCounter++;
                    name.remove();
                }
            }
            topGenres.add(tempGenre);
            topNum.add(tempCounter);

            tempCounter = 0;
        }

        HashMap<String, Integer> mapTotal = makeHashmap(topGenres,topNum);

        //Three most popular genre

        ArrayList<String> finalThreeList = new ArrayList<String>();
        for(int i = 0; i<3; i++) {
            finalThreeList.add(Collections.max(mapTotal.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey());
            mapTotal.remove(Collections.max(mapTotal.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey());
        }

        return finalThreeList;
    }

    /**
     *  Finds the 3 most popular genres.
     */
    public static ArrayList<String> topThreeGenres() {
        ArrayList<ArrayList<String>> topList = new ArrayList<>();
        ArrayList<String> completeGenresList = new ArrayList<>();
        try (MongoClient mc = MongoClients.create(uri);){

            MongoDatabase database = mc.getDatabase("MediaData");
            MongoCollection<Document> coll = database.getCollection("TVdata");

            // return all documents with only the name field
            Bson filter = Filters.empty();
            Bson projection = Projections.fields(Projections.include("genres"), Projections.excludeId());
            coll.find(filter).projection(projection).forEach(doc -> topList.add((ArrayList<String>) doc.get("genres")));//System.out.println(doc.get("genres")));

            for (ArrayList<String> list : topList) {
                completeGenresList.addAll(list);
            }

        } catch (MongoException me) {
            System.err.println("An error occurred: " + me);
        }

        return listCounter(completeGenresList);

    }

}
