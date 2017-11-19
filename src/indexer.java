import java.util.*;
import java.util.stream.Stream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class indexer {
	
	private static String DOC_DIR = "C:\\Users\\Samrat\\Desktop\\AIR_5\\IR-Assignment5\\docs";
	private static String INDEX_DIR = "C:\\Users\\Samrat\\Desktop\\AIR_5\\IR-Assignment5\\index";
	
	public indexer() throws IOException {
		Arrays.stream(new File(INDEX_DIR).listFiles()).forEach(File::delete);
		IndexWriter writer = createWriter();
		List<Document> documents = new ArrayList<>();
		File dir = new File(DOC_DIR);
		File [] files = dir.listFiles();
		BufferedReader br = null;
		FileReader fr = null;
	    for (int i = 0; i < files.length; i++){
	        if (files[i].isFile()){ 
	            //System.out.println(i+"."+files[i]);
	        	String [] items = new String[6];
	        	int cur = 0;
	        	try {

	    			fr = new FileReader(files[i].toString());
	    			br = new BufferedReader(fr);

	    			String sCurrentLine;

	    			while ((sCurrentLine = br.readLine()) != null) {
	    				items[cur] = sCurrentLine.split(":")[1];
	    				cur++;
	    			}
	    			
	    		Document d = getDocument(i+1,items[0].trim(),items[1].trim(),items[2].trim(),items[3].trim(),items[4].trim(),items[5].trim());
    			documents.add(d);
    			
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	        }
	    }
	    writer.deleteAll();
	    writer.addDocuments(documents);
        writer.commit();
        writer.close();
	}
	
	public Document getDocument(Integer docNum,String Title,String Year,String Director,String Cast,String Genre,String Notes) {
		Document doc = new Document();
//		Field senderField = new Field("sender",Title,Field.Store.YES,Field.Index.NOT_ANALYZED);
//		System.out.println(docNum+Title+Year+Director+Cast+Genre+Notes);
		doc.add(new StringField("id", docNum.toString() , Field.Store.YES));
		doc.add(new TextField("Title", Title , Field.Store.YES));
		doc.add(new StringField("Year", Year , Field.Store.YES));
		doc.add(new TextField("Director", Director , Field.Store.YES));
		doc.add(new TextField("Cast", Cast , Field.Store.YES));
		doc.add(new TextField("Genre", Genre , Field.Store.YES));
		doc.add(new TextField("Notes", Notes , Field.Store.YES));

		
		return doc;
	}
	
	public static IndexWriter createWriter() throws IOException {
		FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	    IndexWriter writer = new IndexWriter(dir, config);
		return writer;
	}
	
}
