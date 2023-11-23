import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MongoDBConnector(uri: String, dbName: String, collectionName: String) {
  private val client: MongoClient = MongoClient(uri)
  private val database: MongoDatabase = client.getDatabase(dbName)
  private val collection: MongoCollection[ChatMessage] = database.getCollection(collectionName)

  def insertMessage(message: ChatMessage): Future[Completed] = {
    collection.insertOne(message).toFuture()
  }

  def getMessages(sender: String, receiver: String): Future[Seq[ChatMessage]] = {
    collection.find(and(equal("sender", sender), equal("receiver", receiver))).toFuture()
  }

  def updateMessage(originalText: String, updatedText: String): Future[Any] = {
    collection.updateOne(equal("text", originalText), set("text", updatedText)).toFuture()
  }

  def deleteMessage(text: String): Future[Any] = {
    collection.deleteOne(equal("text", text)).toFuture()
  }

  def close(): Unit = {
    client.close()
  }
}

object MongoDBConnector {
  def apply(uri: String, dbName: String, collectionName: String): MongoDBConnector = {
    new MongoDBConnector(uri, dbName, collectionName)
  }
}

