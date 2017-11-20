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
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
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
        case 5:
        case 6:	TopDocs TopFoundDocs = searchByType2(toFind, searcher);
        		displayTopResults(TopFoundDocs,searcher);
        		break;
        case 4:	TopDocs TopPhraseDocs = searchPhrase(toFind, searcher);
				displayTopResults(TopPhraseDocs,searcher);
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
        MultiFieldQueryParser qp = new MultiFieldQueryParser(types, new StandardAnalyzer());
        Query query = qp.parse(toFind);
        System.out.println(query);
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }
    
//    private static TopDocs searchPhrase(String toFind, IndexSearcher searcher) throws Exception
//    {
//    	String types[] = {"Title","Director","Cast","Genre","Notes"};
//    	MultiPhraseQuery.Builder builder = new MultiPhraseQuery.Builder();
//		builder.add(new Term("Title", "harry"));
//		builder.add(new Term("Title", "chamber"));
//		builder.setSlop(4);
//		MultiPhraseQuery.Builder builder1 = new MultiPhraseQuery.Builder();
//		builder1.add(new Term("Notes", "harry"));
//		builder1.add(new Term("Notes", "chamber"));
//		builder1.setSlop(4);
//		MultiPhraseQuery pq = builder.build();
//		MultiPhraseQuery pq1 = builder1.build();
//        System.out.println(pq);
//        System.out.println(pq1);
//        TopDocs hits[] = new TopDocs[2];
//        hits[0]=searcher.search(pq, 10);
//        hits[1]=searcher.search(pq1, 10);
//        TopDocs finalHits = TopDocs.merge(10, hits);
//        return finalHits;
//    }
    
    private static TopDocs searchPhrase(String toFind, IndexSearcher searcher) throws Exception
    {
    	String types[] = {"Title","Director","Cast","Genre","Notes"};
    	TopDocs hits[] = new TopDocs[5];
    	String slopeValue = toFind.split("~")[1];
    	String words = toFind.split("~")[0];
    	String[] terms = words.split(" ");
    	for(int i=0;i<types.length;i++) {
    		MultiPhraseQuery.Builder builder = new MultiPhraseQuery.Builder();
    		for(int j=0;j<terms.length;j++) {
    			builder.add(new Term(types[i], terms[j].toLowerCase()));
    		}
    		builder.setSlop(4);
    		MultiPhraseQuery pq = builder.build();
    		System.out.println(pq);
    		hits[i]=searcher.search(pq, 10);
    	}
    	TopDocs finalHits = TopDocs.merge(10, hits);
    	return finalHits;
    }
    
    
    private static TopDocs fuzzySearchTest(String toFind,IndexSearcher searcher) throws Exception
    {
    	String types[] = {"Title","Director","Cast","Genre","Notes"};
    	TopDocs hits[] = new TopDocs[5];
    	System.out.println("Fuzzy Results");
    	for(int i=0;i<types.length;i++) {
    		Query query = new FuzzyQuery(new Term(types[i], toFind),1);
        	System.out.println(query);
        	hits[i] = searcher.search(query, 10);
    	}
    	TopDocs finalHits = TopDocs.merge(10, hits);
        return finalHits;
    }
 
    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
    
    private static void displayTopResults(TopDocs foundDocs,IndexSearcher searcher) throws Exception{
    	System.out.println("Found : " + foundDocs.totalHits);
        
    	System.out.println("\n");
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
            System.out.println("\n");
        }
        
    }
}