package com.baizhi;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class TestLucene {
    @Test
    public void Test1() throws IOException {
        Directory directory= FSDirectory.open(new File("e:/index"));
      //  Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_44);
        Analyzer analyzer=new IKAnalyzer();
      //  test(analyzer,"我是一个下笑笑鸟，想要飞的更高,金科汇智");
        IndexWriterConfig indexWriterConfig= new IndexWriterConfig(Version.LUCENE_44,analyzer);
        IndexWriter indexWriter=new IndexWriter(directory,indexWriterConfig);
        Document doc=new Document();
        doc.add(new IntField("id",2, Field.Store.YES));
        doc.add(new StringField("title","java 培训的龙头老大", Field.Store.YES));
        doc.add(new TextField("content", "我是一个下笑笑鸟，想要飞的更高金科汇智", Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.commit();
        indexWriter.close();
    }
    @Test
    public void Test2() throws IOException, ParseException, InvalidTokenOffsetsException {
        FSDirectory directory=FSDirectory.open(new File("e:/index"));
        DirectoryReader open = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(open);
        String [] fields={"title","content"};
        Analyzer analyzer=new IKAnalyzer();
        MultiFieldQueryParser multiFieldQueryParser=new MultiFieldQueryParser(Version.LUCENE_44,fields,analyzer);
        Query query=multiFieldQueryParser.parse("金科汇智");

//        高亮
        Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
        QueryTermScorer queryTermScorer = new QueryTermScorer(query);
        Highlighter highlighter = new Highlighter(formatter, queryTermScorer);


        TopDocs search = indexSearcher.search(query, 100);
        ScoreDoc[] scoreDocs=search.scoreDocs;
        for(int i=0;i<scoreDocs.length;i++){
            ScoreDoc scoreDo=scoreDocs[i];
            int doc=scoreDo.doc;
            Document document = indexSearcher.doc(doc);
            System.out.println(document.get("id"));
            System.out.println(document.get("title"));
            System.out.println(document.get("content"));
            System.out.println("-----------------");
            System.out.println(highlighter.getBestFragment(analyzer, "content", document.get("content")));

            String bestFragment = highlighter.getBestFragment(analyzer, "id", document.get("id"));
            if(bestFragment==null){
                System.out.println(document.get("id"));
            }
        }






//        FSDirectory directory= FSDirectory.open(new File("e:/index"));
//        DirectoryReader reader=DirectoryReader.open(directory);
//        IndexSearcher indexSearcher = new IndexSearcher(reader);
//        List<Artice> list =new ArrayList<Artice>();
//        Query termQuery = new TermQuery(new Term("content", "金科汇智"));
//        TopDocs search = indexSearcher.search(termQuery, 100);
//        ScoreDoc[] scoreDocs=search.scoreDocs;
//        for(int i=0;i<scoreDocs.length;i++){
//            ScoreDoc scoreDo=scoreDocs[i];
//            int doc=scoreDo.doc;
//            Document document = indexSearcher.doc(doc);
//            System.out.println(document.get("id"));
//            System.out.println(document.get("title"));
//            System.out.println(document.get("content"));
//        }
    }

    public static void  test(Analyzer analyzer,String text) throws IOException{

        System.out.println("当前分词器:--->"+analyzer.getClass().getName());

        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));

        tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while(tokenStream.incrementToken()){
            CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
            System.out.println(attribute.toString());
        }

        tokenStream.end();
    }
    @Test
    public void Test5() throws IOException {
        FSDirectory directory = FSDirectory.open(new File("e:/index"));
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        Query termQuery =  new TermQuery(new Term("content", "金科汇智"));
        TopDocs search =  indexSearcher.search(termQuery, 100);
        ScoreDoc[] scoreDocs=search.scoreDocs;
        for(int i=0;i<scoreDocs.length;i++){
            ScoreDoc scoreDo=scoreDocs[i];
            int doc=scoreDo.doc;
            Document document = indexSearcher.doc(doc);
            System.out.println(document.get(" id"));
            System.out.println(document.get(" title"));
            System.out.println(document.get(" content"));
        }
    }



}
