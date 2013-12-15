package com.hascode.tutorial;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// http://lucene.apache.org/core/4_6_0/core/index.html
public class QueryBuilderExample {
	private static final String INDEX = "target/facet/index";

	public static void main(final String... args) throws IOException {
		Directory dir = FSDirectory.open(new File(INDEX));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,
				analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		writer.commit();
		writer.close();
	}
}
