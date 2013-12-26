
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.PropertyContainer
import org.neo4j.graphdb.index.AutoIndexer
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.tooling.GlobalGraphOperations
import scala.collection.JavaConversions._

object TestIndex extends App {

  val graph = new GraphDatabaseFactory().newEmbeddedDatabase(args(0))
  val fooIndex = graph.index().forNodes("foo")

  // Add duplicated entries in the index

  val tx = graph.beginTx()
  try {
    val nodeA = graph.createNode()
    nodeA.setProperty("x", "A")

    1 to 10 foreach { _ => fooIndex.add(nodeA, "x", "A") }

    tx.success()

  } finally {
    tx.finish()
  }

  // Search x==A
  for(node <- fooIndex.get("x", "A").iterator)
    println(node)

  graph.shutdown()

}
