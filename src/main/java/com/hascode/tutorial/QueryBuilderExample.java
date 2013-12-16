package com.hascode.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;

// http://lucene.apache.org/core/4_6_0/core/index.html
public class QueryBuilderExample {
	private static final String INDEX = "target/index";

	public static void main(final String... args) throws IOException {
		Directory dir = FSDirectory.open(new File(INDEX));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

		System.out.println("Adding some books to the search index..");
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,
				analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		for (Book book : createBookList()) {
			Document doc = new Document();
			doc.add(new StringField("title", book.getTitle(), Store.YES));
			doc.add(new StringField("author", book.getAuthor(), Store.YES));
			doc.add(new StringField("published", DateTools.dateToString(
					book.getPublished(), Resolution.YEAR), Store.YES));
			for (String tag : book.getTags())
				doc.add(new StringField("tag", tag, Store.YES));
			writer.addDocument(doc);
		}
		writer.commit();
		writer.close();

		System.out.println("Creating some queries using the query builder..");
		QueryBuilder builder = new QueryBuilder(analyzer);
		Query q1 = builder.createBooleanQuery("title", "One book");
		Query q2 = builder.createPhraseQuery("author", "Tim*", 3);

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs td = searcher.search(q1, 10);
		System.out.println(td.totalHits + " hits total");
		for (ScoreDoc scoreDoc : td.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(String.format("search hit: %s %s %s %s",
					doc.get("title"), doc.get("author"), doc.get("published"),
					doc.get("tags")));
		}
	}

	private static List<Book> createBookList() {
		List<Book> books = new ArrayList<>();
		Book b1 = new Book("One book", "Tim Miller", new Date(30000), "comedy",
				"philosophy");
		Book b2 = new Book("Nucky the psycho doll", "Tim Taylor", new Date(
				20000), "horror", "thriller", "mystery");
		books.add(b1);
		books.add(b2);
		return books;
	}

	static class Book {
		private final String title;
		private final String author;
		private final Date published;
		private final Set<String> tags = new HashSet<>();

		public Book(final String title, final String author,
				final Date published, final String... tags) {
			this.title = title;
			this.author = author;
			this.published = published;
			this.tags.addAll(Arrays.asList(tags));
		}

		public final String getTitle() {
			return title;
		}

		public final String getAuthor() {
			return author;
		}

		public final Date getPublished() {
			return published;
		}

		public final Set<String> getTags() {
			return tags;
		}
	}
}
