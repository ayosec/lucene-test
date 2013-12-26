
import java.io.File
import org.apache.lucene.index.Term
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.Field.Index
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.util.Version.LUCENE_36
import org.apache.lucene.store.FSDirectory
import org.neo4j.index.impl.lucene.LowerCaseKeywordAnalyzer
import scala.collection.JavaConversions._
import scala.language.postfixOps

object RemoveDuplicates extends App {

  val directory = FSDirectory.open(new File(args(0)))
  val reader = IndexReader.open(directory)
  val writer = new IndexWriter(directory, new IndexWriterConfig(LUCENE_36, new LowerCaseKeywordAnalyzer))

  try {
    println(s"${writer.numDocs} documents")
    for(numDoc <- 0 until writer.numDocs) {

      val doc = reader.document(numDoc)
      val fields = doc.getFields groupBy { field => field.name + "=" + field.stringValue } map {
        case (_, xs) => (xs.head, xs.size)
      }

      val duplicatedFields = fields.map { case (_, k) => k - 1 } sum

      if(duplicatedFields > 0) {

        val newDoc = new Document
        for((field, _) <- fields)
          newDoc.add(field)

        println(s"$numDoc ($duplicatedFields fields) adapted to $newDoc")
        writer.deleteDocuments(new Term("_id_", newDoc.get("_id_").toString()))
        writer.addDocument(newDoc)
      }
    }

    writer.commit()
    writer.forceMerge(1, true)
  } finally {
    reader.close()
    writer.close()
  }

}
