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

	public BookInformationBox(int recordIndex) {

		BookInformationBox.rIndex = recordIndex;

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

		if (recordIndex < 0 || recordIndex >= BookManager.records.size()) {
			JOptionPane.showMessageDialog(null, "Invalid Book Record ID");
			this.dispose();
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(6, 2));
		infoPanel.setPreferredSize(new Dimension(480, 200));

		/*
		 * Fill in the book properties into the textfields
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
			// Validate ISBN
			String isbn = txtISBN.getText().replaceAll("-", "");
			if (isbn.length() == 10) {
				isbn = "978" + isbn;
			}
			if (isbn.length() != 13) {
				JOptionPane.showMessageDialog(null, "ISBN too short or too long.");
				return;
			}
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
