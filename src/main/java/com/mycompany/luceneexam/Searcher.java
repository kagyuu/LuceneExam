package com.mycompany.luceneexam;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.gosen.GosenAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    public static void main(String[] args) throws IOException, ParseException {
        try {
	    File home = new File(System.getProperty("user.home"));

            // テキストの解析方法（アナライザー）を定義
            //Analyzer analyzer = new StandardAnalyzer(); // 英語用
            Analyzer analyzer = new GosenAnalyzer();
            // 検索対象のフィールドを第二引数で指定している
            QueryParser parser = new QueryParser("contents", analyzer);

            // 検索文字列を解析する
            String searchText = args[0];
            Query query = parser.parse(searchText);

            // 検索で使用する IndexSearcher を生成する
            Directory indexDir = FSDirectory.open(new File(home, "Documents/index").toPath());
            IndexReader indexReader = DirectoryReader.open(indexDir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            // 検索を実行する（第二引数は、検索結果の最大数）
            TopDocs results = indexSearcher.search(query, 10);

            // 検索の結果、該当した Document を１つずつ取得する
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);

                // Document の path を取得して出力する
                String author = doc.get("author");
                String title = doc.get("title");
                System.out.format("%s, %s, %f\n", author, title, scoreDoc.score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

