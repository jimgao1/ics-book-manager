import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;

public class BookManager {
	
	/*
	 * 		Initialization of GUI components and databases
	 */
	
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

		/*
		 * 		Overriding the compareTo method,
		 * 		used during sorting
		 */
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
	 * 
	 * 		Serialization and de-serialization of the data to
	 * 		a file. Ensures file integrity and also prevents user
	 * 		from directly accessing the file. 
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
	
	/*
	 * 		Resets the book list to the original
	 */
	public static void resetBookList(){
		activeList.clear();
		dataCaption.setText("Book List: ");
		
		for (int i=0; i<BookManager.records.size(); i++){
			if (records.get(i).name == null)
				BookManager.records.remove(i);
		}
		
		for (BookRecord r : BookManager.records)
			activeList.addElement(r.name);
	}
	
	/*
	 * 		Part of the fuzzy-matching algorithm, ignores cases
	 * 		and also spaces in search query
	 */
	public static String searchFilter(String text){
		if (text == null) return "";
		return text.replace(" ", "").toUpperCase();
	}
	
	/*
	 * 		Edit Distance Dynamic Programming algorithm to perform 
	 * 		FUZZY-MATCHING of the names, and authors. 
	 * 
	 * 		Calculates the operations (add, remove, edit) to transform
	 * 		String t1 into t2, to calculate similarity. 
	 * 
	 * 		Two strings are considered the same if the edit distance
	 * 		is less than 5.
	 */
	public static int editDistance(String t1, String t2){
		int[][] dp = new int[t1.length()][t2.length()];
		
		for (int i=0; i<t1.length(); i++){
			for (int j=0; j<t2.length(); j++){
				if (i == 0)
					dp[i][j] = j;
				else if (j == 0)
					dp[i][j] = i;
				else
					if (t1.charAt(i) == t2.charAt(j))
						dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1]);
					else
						dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + 1);
			}
		}
		
		return dp[t1.length() - 1][t2.length() - 1];
	}
	
	public static boolean fuzzyMatch(String t1, String t2){
		return editDistance(t1, t2) < 5;
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
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.setLayout(new BorderLayout());
			this.setLocation(100, 100);
			
			this.setVisible(true);
			
			this.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					BookManager.serializeToFile();
					System.exit(0);
				}
			});
			
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
			JScrollPane scroll = new JScrollPane();
			scroll.setViewportView(bookList);
			bookList.setFont(new Font("Courier New", Font.PLAIN, 18));
			
			
			infoPanel.add(scroll, BorderLayout.CENTER);
			
			infoPanel.add(new JLabel("Double-Click to EDIT, Click [RESET LIST] to VIEW ALL"), BorderLayout.SOUTH);
			
			this.add(infoPanel, BorderLayout.CENTER);
			
					
			/*
			 * 		Adding Listeners
			 * 
			 * 		I am using independent action listener for every single button, 
			 * 		doing so can significantly decrease runtime complexity since
			 * 		no string comparison (time complexity o(n)) is required. This 
			 * 		also makes code more managable. 
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
			
			/*
			 * 		Create Book button:
			 * 			Creates new empty record in database, open
			 * 			a information editor to edit the record
			 */
			createBook.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					BookManager.records.add(new BookRecord());
					new BookInformationBox(BookManager.records.size() - 1);
				}
			});
			
			/*
			 * 		Delete Book button
			 * 			Gets the selected book, check the global database
			 * 			for index, and then removes it
			 */
			deleteBook.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String bookName = (String) bookList.getSelectedValue();
					
					if (bookName != null){
					
			        	int index = -1;
			        	for (int i=0; i<BookManager.records.size(); i++)
			        		if (BookManager.records.get(i).name == bookName){
			        			index = i;
			        			break;
			        		}
			        	
			        	BookManager.records.remove(index);
			        	BookManager.resetBookList();
					} else {
						JOptionPane.showMessageDialog(null, "Please select an item");
					}
				}
			});
			
			/*
			 * 		Search by Name:
			 * 			Iterate through the array, uses the fuzzy search algorithm, 
			 * 			add ONLY the items that satisfy into the final array
			 */
			searchName.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String bookName = JOptionPane.showInputDialog(null, "Enter Book Name");
					bookName = searchFilter(bookName);
					
					BookManager.dataCaption.setText("Search results for " + bookName);
					
					BookManager.activeList.clear();
					
					for (BookRecord b : BookManager.records)
						if (fuzzyMatch(bookName, searchFilter(b.name)))
							BookManager.activeList.addElement(b.name);
					
					if (bookName.equals(""))
						BookManager.resetBookList();
				}
			});
			
			/*
			 * 		Search by Author:
			 * 			See "Search by Name"
			 */
			searchAuthor.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String authorName = JOptionPane.showInputDialog(null, "Enter Author Name");
					authorName = searchFilter(authorName);
					
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
			
			
			/*
			 * 		Search by ISBN:
			 * 			Same as "Search by Name", except exact match
			 */
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
			
			/*
			 * 		Pushes the update in the DB to the file, 
			 * 		reads from it, and updates the list
			 */
			updateFile.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {	
					BookManager.serializeToFile();
					BookManager.deserializeFromFile();
					BookManager.resetBookList();
					
				}
			});
			
			/*
			 * 		Asks the user for the sort criteria, updates the static
			 * 		var in class (see class def), and uses quick sort
			 * 		to sort the records
			 */
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
			
			/*
			 * 		About button:
			 * 			Opens a pop-up with credit information
			 */
			about.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					String aboutText = "<html><h2>Book Manager</h2><br><b>Author: </b>Jim Gao<br><b>Build Version: </b>2015_05_06<br><br></html>";
					JOptionPane.showMessageDialog(null, aboutText);
				}
				
			});

			/*
			 * 		Quit button:
			 * 			Safely quit the program by saving DB into 
			 * 			file and exiting. 
			 * 
			 * 			This EXACT code is also overloaded to the windowClosing
			 * 			event, so either is fine. 
			 */
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
