package com.shenji.test;




import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.SynonymAnalyzer;

import com.shenji.search.dic.CommonSynonymDic;



public class IKAnalyzerDemo {

	/**
	 * @param args
	 * @throws IOException 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {

        String text1 = "quick 单反的增值税发票";
        String text2 = "holle quick apple an";
        /*String text3 = "���ķִ�,����";*/
        String fieldName = "word";
        //SysnonymDict services=new SysnonymDict();
       // Analyzer analyzer = new SynonymAnalyzer(services);
        Analyzer analyzer2=new StandardAnalyzer(Version.LUCENE_30);
        Analyzer analyzer3=new IKAnalyzer();
        //RAMDirectory directory = new RAMDirectory();
        Directory directory=FSDirectory.open(new File("D:\\Program Files\\tomcat\\webapps\\test\\synonm"));
        //IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_30, analyzer);
        /*  IndexWriter indexWriter = new IndexWriter(directory,analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
        Document document1 = new Document();
        document1.add(new Field("ID", "1", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document1.add(new Field(fieldName, text1, Field.Store.YES, Field.Index.ANALYZED));
        indexWriter.addDocument(document1);
        
       Document document2 = new Document();
        document2.add(new Field("ID", "2", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document2.add(new Field(fieldName, text2, Field.Store.YES, Field.Index.ANALYZED));
        indexWriter.addDocument(document2);*/
        
       /* Document document3 = new Document();
        document3.add(new Field("ID", "2", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document3.add(new Field(fieldName, text3, Field.Store.YES, Field.Index.ANALYZED));
        indexWriter.addDocument(document3);*/
       // indexWriter.close();
        
       
        long startTime=System.currentTimeMillis();
        IndexReader indexReader = IndexReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        String request = "叫好声";
        QueryParser parser=new QueryParser(
                Version.LUCENE_30,
                "word",analyzer2);

        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        try {
			Query query = parser.parse(request);
			TopDocs topDocs = searcher.search(query, 10);
			System.out.println("命中数:"+topDocs.totalHits);
			ScoreDoc[] docs = topDocs.scoreDocs;
			for(ScoreDoc doc : docs){
				Document d = searcher.doc(doc.doc);
				System.out.println(d.getValues("syn")[2]);
				//System.out.println("内容:"+d.get("syn"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(indexReader != null){
				try{
					indexReader.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(directory != null){
				try{
					directory.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
        long endTime=System.currentTimeMillis(); //��ȡ����ʱ��
    	System.out.println("时间 "+(endTime-startTime)+"ms");
	}
	
}
