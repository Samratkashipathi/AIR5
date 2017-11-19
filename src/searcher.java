import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
        
        Scanner sn = new Scanner(System.in);
        System.out.println("Enter the Query");
        String toFind = sn.nextLine();
        System.out.println("Enter the Search Type:\n1.Boolean Search\n2.Boosted Search\n3.Fuzzy Search\n4.Phrase Search\n5.Regular Search\n6.Wild Card Search");
        int type = sn.nextInt();
        System.out.println(toFind);
        switch(type) {
        case 1:
        case 2:
        case 4:
        case 5:
        case 6:	TopDocs TopFoundDocs = searchByType2(toFind, searcher);
        		displayTopResults(TopFoundDocs,searcher);
        		break;
        case 3: TopDocs TopFuzzyDocs = fuzzySearchTest(toFind, searcher);
				displayTopResults(TopFuzzyDocs,searcher);
				break;
        }
         
    }
    
    private static TopDocs searchByType(String type,String toFind, IndexSearcher searcher) throws Exception
    {
    	System.out.println(type+" Results:");
        QueryParser qp = new QueryParser(type, new StandardAnalyzer());
        Query query = qp.parse(toFind);
        System.out.println(query);
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }
    
    private static TopDocs searchByType2(String toFind, IndexSearcher searcher) throws Exception
    {
    	String types[] = {"Title","Director","Cast","Genre","Notes"};
//    	System.out.println(types+" Results:");
        MultiFieldQueryParser qp = new MultiFieldQueryParser(types, new StandardAnalyzer());
        Query query = qp.parse(toFind);
        System.out.println(query);
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }
    
    private static TopDocs fuzzySearchTest(String toFind,IndexSearcher searcher) throws Exception
    {
    	//QueryParser qp = new QueryParser("Genre", new StandardAnalyzer());
    	String types[] = {"Title","Director","Cast","Genre","Notes"};
    	System.out.println("Fuzzy Results");
//    	BooleanQuery booleanQuery = new BooleanQuery();
    	Query query = new FuzzyQuery(new Term("Genre", toFind),1);
    	System.out.println(query);
    	TopDocs hits = searcher.search(query, 10);
    	//System.out.println(hits);
    	 return hits;
    }
 
    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
    
    private static void displayTopResults(TopDocs foundDocs,IndexSearcher searcher) throws Exception{
    	System.out.println("Found : " + foundDocs.totalHits);
        
        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println("Id: "+String.format(d.get("id")));
            System.out.println("Title: "+String.format(d.get("Title")));
            System.out.println("Year: "+String.format(d.get("Year")));
            System.out.println("Director: "+String.format(d.get("Director")));
            System.out.println("Cast: "+String.format(d.get("Cast")));
            System.out.println("Genre: "+String.format(d.get("Genre")));
            System.out.println("Notes: "+String.format(d.get("Notes")));
        }
        System.out.println("\n");
    }
}