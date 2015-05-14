import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BookInformationBox extends JFrame implements ActionListener {

	/*
	 * The index of the book information editing
	 */
	public static int rIndex;

	/*
	 * GUI Components Declaration
	 */
	public JTextField txtName = new JTextField();
	public JTextField txtAuthor = new JTextField();
	public JTextField txtType = new JTextField();
	public JTextField txtYearPublished = new JTextField();
	public JTextField txtISBN = new JTextField();
	public JTextField txtCoverPrice = new JTextField();

	/*
	 * 	Constructor of BookInformationBox
	 * 
	 * 	Takes in the index of the record, and verifies it, and then
	 * 	displays the corresponding record from the database
	 */
	public BookInformationBox(int recordIndex) {

		BookInformationBox.rIndex = recordIndex;

		/*
		 * 		Construction of the BookInformationBox
		 */
		this.setLocation(300, 300);
		this.setSize(500, 300);
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Book Information");

		txtName.setFont(new Font("Courier New", Font.PLAIN, 15));
		txtAuthor.setFont(new Font("Courier New", Font.PLAIN, 15));
		txtType.setFont(new Font("Courier New", Font.PLAIN, 15));
		txtYearPublished.setFont(new Font("Courier New", Font.PLAIN, 15));
		txtISBN.setFont(new Font("Courier New", Font.PLAIN, 15));
		txtCoverPrice.setFont(new Font("Courier New", Font.PLAIN, 15));

		/*
		 * 	Verifies the current record index, if invalid, the quit program
		 */
		if (recordIndex < 0 || recordIndex >= BookManager.records.size()) {
			JOptionPane.showMessageDialog(null, "Invalid Book Record ID");
			this.dispose();
		}

		/*
		 *  Creates the information panel containing the info
		 */
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(6, 2));
		infoPanel.setPreferredSize(new Dimension(480, 200));

		/*
		 * Fill in the book properties into the text fields
		 */

		txtName.setText(BookManager.records.get(rIndex).name);
		txtAuthor.setText(BookManager.records.get(rIndex).author);
		txtType.setText(BookManager.records.get(rIndex).type);
		txtYearPublished.setText(BookManager.records.get(rIndex).yearPublished);
		txtISBN.setText(BookManager.records.get(rIndex).isbn);
		txtCoverPrice.setText(Double.toString(BookManager.records.get(rIndex).coverPrice));

		/*
		 * Adds the fields onto the JPanel
		 */
		infoPanel.add(new JLabel("Book Name: "));
		infoPanel.add(txtName);
		infoPanel.add(new JLabel("Book Author: "));
		infoPanel.add(txtAuthor);
		infoPanel.add(new JLabel("Book Type:"));
		infoPanel.add(txtType);
		infoPanel.add(new JLabel("Year Published: "));
		infoPanel.add(txtYearPublished);
		infoPanel.add(new JLabel("ISBN: "));
		infoPanel.add(txtISBN);
		infoPanel.add(new JLabel("Cover Price: "));
		infoPanel.add(txtCoverPrice);

		JPanel confirmPanel = new JPanel();
		confirmPanel.setLayout(new FlowLayout());

		JButton updateInfo = new JButton("Update Record");
		updateInfo.addActionListener(this);

		JButton closeWindow = new JButton("Close Window");
		closeWindow.addActionListener(this);

		confirmPanel.add(updateInfo);
		confirmPanel.add(closeWindow);

		this.add(infoPanel);
		this.add(confirmPanel);

		this.setVisible(true);

		this.repaint();
		this.revalidate();
	}

	/*
	 * Event handler to save, or close the window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Update Record") {
			
			/*
			 * 	Checks the ISBN to make sure its valid
			 * 
			 * 	This process consists of a number of steps:
			 * 		1. Check ISBN format, transform to ISBN-13
			 * 		2. Check ISBN length, if invalid, then quit
			 * 		3. Removes all the invalid characters to make the 
			 * 			ISBN pure numbers
			 * 		4. Calculates the ISBN check sum, and varifies with
			 * 			the last check digit
			 * 		5. If the tests passed, then add the book record
			 * 			to the data structure
			 */
			String isbn = txtISBN.getText().replaceAll("-", "").replaceAll(" ", "");
		
			if (isbn.length() == 10) {
				isbn = "978" + isbn;
			}
			if (isbn.length() != 13) {
				JOptionPane.showMessageDialog(null, "ISBN too short or too long.");
				return;
			}
			
			//Check sum verification
			try {
				int tot = 0;
				for (int i = 0; i < 12; i++) {
					int digit = Integer.parseInt(isbn.substring(i, i + 1));
					tot += (i % 2 == 0) ? digit * 1 : digit * 3;
				}
				int checksum = 10 - (tot % 10);
				if (checksum == 10) {
					checksum = 0;
				}

				if (checksum != Integer.parseInt(isbn.substring(12))) {
					JOptionPane.showMessageDialog(null, "Invalid ISBN check code");
					return;
				}
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "ISBN can only contain numeric characters.");
				return;
			}
			isbn = isbn.substring(0, 3) + "-" + isbn.charAt(3) + "-" + isbn.substring(4, 6) + "-" + isbn.substring(6, 12) + "-" + isbn.charAt(12);

			// Validate publication year
			for (int i = 0; i < txtYearPublished.getText().length(); i++) {
				int a = txtYearPublished.getText().charAt(i) - '0';
				if (a < 0 || a > 9) {
					JOptionPane.showMessageDialog(null, "Publication year can only contain numeric characters.");
					return;
				}
			}

			// Validate price
			for (int i = 0; i < txtCoverPrice.getText().length(); i++) {
				int a = txtCoverPrice.getText().charAt(i) - '0';
				if ((a < 0 || a > 9) & a != -2) {
					JOptionPane.showMessageDialog(null, "Cover price can only contain numeric characters.");
					return;
				}
			}
			if (Double.parseDouble(txtCoverPrice.getText()) < 0) {
				JOptionPane.showMessageDialog(null, "Cover price cannot be negative.");
				return;
			}

			/*
			 * 	If everything passed, then adds the record
			 * 	to the data structure
			 */
			BookManager.records.get(rIndex).name = txtName.getText();
			BookManager.records.get(rIndex).author = txtAuthor.getText();
			BookManager.records.get(rIndex).type = txtType.getText();
			BookManager.records.get(rIndex).yearPublished = txtYearPublished.getText();
			BookManager.records.get(rIndex).isbn = isbn;
			BookManager.records.get(rIndex).coverPrice = Double.parseDouble(txtCoverPrice.getText());

			JOptionPane.showMessageDialog(null, "Book Record Updated");
			BookManager.resetBookList();
			this.dispose();
		} else {
			this.dispose();
		}
	}
}
