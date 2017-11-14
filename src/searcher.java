import java.io.IOException;
import java.nio.file.Paths;
 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 
public class searcher
{
	private static String INDEX_DIR = "C:\\Users\\Samrat\\Desktop\\AIR_5\\IR-Assignment5\\index";
 
    public static void main(String[] args) throws Exception
    {
        IndexSearcher searcher = createSearcher();
         
        //Search by ID
        TopDocs foundDocs = searchById(1, searcher);
         
        System.out.println("Found : " + foundDocs.totalHits);
         
        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format(d.get("Year")));
        }
        
        TopDocs foundDocs2 = searchById(2, searcher);
        
        System.out.println("Found :" + foundDocs2.totalHits);
         
        for (ScoreDoc sd : foundDocs2.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format(d.get("Genre")));
        }
        
        TopDocs foundDocs3 = searchByGenre("Crime AND drama", searcher);
        
        System.out.println("Found : " + foundDocs3.totalHits);
         
        for (ScoreDoc sd : foundDocs3.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format(d.get("id")));
        }
        
        fuzzySearchTest(searcher);
         
    }
    
    private static TopDocs searchByYear(String Year, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("Year", new StandardAnalyzer());
        Query firstNameQuery = qp.parse(Year);
        TopDocs hits = searcher.search(firstNameQuery, 10);
        return hits;
    }
    
    private static TopDocs searchByGenre(String genre, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("Genre", new StandardAnalyzer());
        Query firstNameQuery = qp.parse(genre);
        TopDocs hits = searcher.search(firstNameQuery, 10);
        return hits;
    }
    
    
    private static TopDocs searchById(Integer id, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("id", new StandardAnalyzer());
        Query idQuery = qp.parse(id.toString());
        TopDocs hits = searcher.search(idQuery, 10);
        return hits;
    }
    
    private static void fuzzySearchTest(IndexSearcher searcher) throws Exception
    {
    	//QueryParser qp = new QueryParser("Genre", new StandardAnalyzer());
    	System.out.println("Fuzzy Results");
    	Query query = new FuzzyQuery(new Term("Genre", "crim"));
    	TopDocs hits = searcher.search(query, 10);
    	for (ScoreDoc sd : hits.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            
            System.out.println(String.format(d.get("id")));
        }
    }
 
    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}