
import java.io.File
import org.apache.lucene.index.IndexReader
import org.apache.lucene.store.FSDirectory
import scala.collection.JavaConversions._

object DumpIndex extends App {

  val index = IndexReader.open(FSDirectory.open(new File(args(0))), true)

  println("Field info")
  for {
    reader <- index.getSequentialSubReaders
    fieldInfo <- reader.getFieldInfos.iterator
  } println(s"${fieldInfo.number} ${fieldInfo.name} ${fieldInfo.indexOptions} ${fieldInfo.isIndexed}")

  println(s"\n${index.numDocs} documents")
  for(numDoc <- 0 until index.numDocs) {
    println(s"#$numDoc")
    for(field <- index.document(numDoc).getFields)
      println(s"\t$field")
  }

  index.close()
}
