package com.mycompany.luceneexam;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.gosen.GosenAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    public static void main(String[] args) {
        try {
            // インデックスの出力先を定義
            Directory indexDir = FSDirectory.open(new File("~/Documents/index").toPath());
            // テキストの解析方法（アナライザー）を定義
            //Analyzer analyzer = new StandardAnalyzer(); // 英語用
            Analyzer analyzer = new GosenAnalyzer();
            // 解析方法の設定
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // インデックスが既に存在する場合の動作を定義する（OpenMode.CREATE の場合、新規に作成して上書きする）
            config.setOpenMode(OpenMode.CREATE);
            try (IndexWriter writer = new IndexWriter(indexDir, config)) {
                File root = new File("~/Documents/novels");
                gatherDocs(writer, root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gatherDocs(IndexWriter writer, File parent) throws IOException {
        for (File child : parent.listFiles()) {
            if (child.isDirectory()) {
                gatherDocs(writer, child);
                continue;
            }
            
            String name = child.getName();
            if (name.startsWith(".")) {
                continue;
            }
            if (name.endsWith("txt")) {                
                try (BufferedReader br = Files.newBufferedReader(child.toPath(), Charset.forName("Windows-31J"))) {
                    
                    String title = br.readLine();
                    String author = br.readLine();
                    
                    System.out.format("%s %s\n", title, author);
                    
                    // Document に、インデックスに保存する各ファイルの情報を設定する
                    Document doc = new Document();
                    doc.add(new StringField("author", author, Store.YES));
                    doc.add(new StringField("title", title, Store.YES));
                    doc.add(new TextField("contents", br));

                    // インデックスを書き出す
                    writer.addDocument(doc);
                }
            }
        }
    }
}

