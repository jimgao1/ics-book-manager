import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.*;
import com.jgoodies.*;
import com.jgoodies.common.base.SystemUtils;

public class BookManager {
	
	public static ArrayList<BookRecord> records;
	public static DefaultListModel<String> activeList;
	
	public static JButton createBook = new JButton("Create Book Record");
	public static JButton deleteBook = new JButton("Delete Book Record");
	
	public static JButton searchName = new JButton("Search by NAME");
	public static JButton searchAuthor = new JButton("Search by AUTHOR");
	public static JButton searchISBN = new JButton("Search by ISBN");
	
	public static JButton sortBooks = new JButton("Sort Books");
	public static JButton updateFile = new JButton("Reset List");
	
	public static JButton about = new JButton("About");
	public static JButton quit = new JButton("Quit");
	
	public static JLabel dataCaption = new JLabel("Book List: ");
	
	public static JList bookList;
	
	public static class BookRecord implements Serializable, Comparable<BookRecord>{

		/*
			 Sorting Criteria: Used when sorting
			 
			 1 - Book Name
			 2 - Book Author
			 3 - Year Published
			 4 - ISBN
			 5 - Cover Price
		 */
		public static int sortCriteria;
		
		public String name;
		public String author;
		public String type;
		public String yearPublished;
		public String isbn;
		public double coverPrice;
		
		public boolean equals(Object o){
			BookRecord b = (BookRecord)o;
			
			return this.name.equals(b.name) &&
					this.author.equals(b.author) &&
					this.type.equals(b.type) &&
					this.yearPublished.equals(b.yearPublished) &&
					this.isbn.equals(b.isbn) && 
					this.coverPrice == b.coverPrice;
		}

		@Override
		public int compareTo(BookRecord b) {
			switch (BookRecord.sortCriteria){
			case 1:
				return this.name.compareTo(b.name);
			case 2:
				return this.author.compareTo(b.author);
			case 3:
				return this.yearPublished.compareTo(b.yearPublished);
			case 4:
				return this.isbn.compareTo(b.isbn);
			case 5:
				return new Double(this.coverPrice).compareTo(new Double(b.coverPrice));
			}
			
			return 0;
		}
		
	}
	
	/*
	 * 		FILE OPEATIONS
	 */
	
	public static void serializeToFile(){
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("database.ser"));
			out.writeObject(BookManager.records);
			out.close();
		} catch (IOException ex){
			JOptionPane.showMessageDialog(null, "IOException: " + ex.getLocalizedMessage());
		}
	}
	
	public static void deserializeFromFile(){
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("database.ser"));
			BookManager.records = null;
			BookManager.records = (ArrayList<BookRecord>) in.readObject();
			in.close();
		} catch (Exception ex){
			JOptionPane.showMessageDialog(null, "IOException: " + ex.getLocalizedMessage());
		}
	}
	
	/*
	 * 		LIST OPERATIONS
	 */
	
	public static void resetBookList(){
		activeList.clear();
		dataCaption.setText("Book List: ");
		
		for (BookRecord r : BookManager.records)
			activeList.addElement(r.name);
	}
	
	public static String searchFilter(String text){
		if (text == null) return "";
		return text.replace(" ", "").toUpperCase();
	}

	
	/*
	 * 		GUI CONSTRUCTION
	 */
	
	public static class MainGUI extends JFrame{
		
		public MainGUI(){
			records = new ArrayList<BookRecord>();
			activeList = new DefaultListModel<String>();
			bookList = new JList(activeList);
			
			deserializeFromFile();
			resetBookList();
			/*
			 * 		Basic Construction
			 */
			
			
			try {
				UIManager.setLookAndFeel(new PlasticLookAndFeel());
			} catch (Exception ex){
				ex.printStackTrace();
			}
			
			
			this.setTitle("Book Manager");
			this.setSize(800, 600);
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			this.setLayout(new BorderLayout());
			this.setLocation(100, 100);
			
			this.setVisible(true);
			
			JPanel operationsPanel = new JPanel();
			operationsPanel.setLayout(new GridLayout(12, 1));
			
			operationsPanel.add(createBook);
			operationsPanel.add(deleteBook);
			operationsPanel.add(new JLabel());
			operationsPanel.add(searchName);
			operationsPanel.add(searchAuthor);
			operationsPanel.add(searchISBN);
			operationsPanel.add(new JLabel());
			operationsPanel.add(sortBooks);
			operationsPanel.add(updateFile);
			operationsPanel.add(new JLabel());
			operationsPanel.add(about);
			operationsPanel.add(quit);
			
			this.add(operationsPanel, BorderLayout.WEST);
			
			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new BorderLayout());
			
			dataCaption.setFont(new Font("Courier New", Font.BOLD, 18));
			
			infoPanel.add(dataCaption, BorderLayout.NORTH);
			
			bookList.setFont(new Font("Courier New", Font.PLAIN, 18));
			
			
			infoPanel.add(bookList, BorderLayout.CENTER);
			
			this.add(infoPanel, BorderLayout.CENTER);
			
					
			/*
			 * 		Adding Listeners
			 */
			
			bookList.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent evt) {
			        JList list = (JList)evt.getSource();
			        if (evt.getClickCount() == 2) {
			        	String bookName = (String) list.getSelectedValue();
			        	int index = -1;
			        	for (int i=0; i<BookManager.records.size(); i++)
			        		if (BookManager.records.get(i).name == bookName){
			        			index = i;
			        			break;
			        		}
			        	
			        	new BookInformationBox(index);
			        	BookManager.resetBookList();
			        }
				}
			});
			
			createBook.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					BookManager.records.add(new BookRecord());
					new BookInformationBox(BookManager.records.size() - 1);
					BookManager.resetBookList();
				}
			});
			
			deleteBook.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String bookName = (String) bookList.getSelectedValue();
		        	int index = -1;
		        	for (int i=0; i<BookManager.records.size(); i++)
		        		if (BookManager.records.get(i).name == bookName){
		        			index = i;
		        			break;
		        		}
		        	
		        	BookManager.records.remove(index);
		        	BookManager.resetBookList();
				}
			});
			
			searchName.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String bookName = JOptionPane.showInputDialog(null, "Enter Book Name");
					
					BookManager.dataCaption.setText("Search results for " + bookName);
					bookName = searchFilter(bookName);
					
					BookManager.activeList.clear();
					
					for (BookRecord b : BookManager.records)
						if (searchFilter(b.name).equals(bookName))
							BookManager.activeList.addElement(b.name);
					
					if (bookName.equals(""))
						BookManager.resetBookList();
				}
			});
			
			searchAuthor.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String authorName = JOptionPane.showInputDialog(null, "Enter Author Name");
					
					BookManager.dataCaption.setText("Search results for " + authorName);
					authorName = searchFilter(authorName);
					
					BookManager.activeList.clear();
					
					for (BookRecord b : BookManager.records)
						if (searchFilter(b.author).equals(authorName))
							BookManager.activeList.addElement(b.name);
					
					if (authorName.equals(""))
						BookManager.resetBookList();
				}
			});
			
			searchISBN.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String bookName = JOptionPane.showInputDialog(null, "Enter ISBN:");
					
					BookManager.dataCaption.setText("Search results for " + bookName);

					
					BookManager.activeList.clear();
					
					for (BookRecord b : BookManager.records)
						if (b.isbn.equals(bookName))
							BookManager.activeList.addElement(b.name);
					
					if (bookName.equals(""))
						BookManager.resetBookList();
				}
			});
			
			updateFile.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {	
					BookManager.serializeToFile();
					BookManager.deserializeFromFile();
					BookManager.resetBookList();
					
				}
			});
			
			sortBooks.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Object[] selections = {
						"Book Name", "Book Author", "Year Published", "ISBN", "Cover Price"	
					};
					
					String s = (String) JOptionPane.showInputDialog(null,
							"What do you want to sort by? ",
							"Sort",
							JOptionPane.QUESTION_MESSAGE, 
							null, 
							selections,
							"Book Name");
					
					if (s.equals("Book Name")){
						BookRecord.sortCriteria = 1;
					} else if (s.equals("Book Author")){
						BookRecord.sortCriteria = 2;
					} else if (s.equals("Year Published")){
						BookRecord.sortCriteria = 3;
					} else if (s.equals("ISBN")){
						BookRecord.sortCriteria = 4;
					} else if (s.equals("Cover Price")){
						BookRecord.sortCriteria = 5;
					}
					
					Collections.sort(records);
					BookManager.resetBookList();
				}
			});
			
			about.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					String aboutText = "<html><h2>Book Manager</h2><br><b>Author: </b>Jim Gao<br><b>Build Version: </b>2015_05_06<br><br></html>";
					JOptionPane.showMessageDialog(null, aboutText);
				}
				
			});
			
			quit.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					serializeToFile();
					System.exit(0);
				}
			});
			
			this.repaint();
			this.revalidate();
		}

	}
	
	public static void main(String[] args) {
		new MainGUI();
	}
}
