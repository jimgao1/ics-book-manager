
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

public class BookInformationBox extends JFrame implements ActionListener{
	
	/*
	 * 		The index of the book information editing
	 */
	public static int rIndex;
	
	/*
	 * 		GUI Components Declaration
	 */
	public JTextField txtName = new JTextField();
	public JTextField txtAuthor = new JTextField();
	public JTextField txtType = new JTextField();
	public JTextField txtYearPublished = new JTextField();
	public JTextField txtISBN = new JTextField();
	public JTextField txtCoverPrice = new JTextField();
	
	public BookInformationBox(int recordIndex){
		
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
		
		if (recordIndex < 0 || recordIndex >= BookManager.records.size()){
			JOptionPane.showMessageDialog(null, "Invalid Book Record ID");
			this.dispose();
		}
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(6, 2));
		infoPanel.setPreferredSize(new Dimension(480, 200));
		
		/*
		 * 		Fill in the book properties into the textfields
		 */
		
		txtName.setText(BookManager.records.get(rIndex).name);
		txtAuthor.setText(BookManager.records.get(rIndex).author);
		txtType.setText(BookManager.records.get(rIndex).type);
		txtYearPublished.setText(BookManager.records.get(rIndex).yearPublished);
		txtISBN.setText(BookManager.records.get(rIndex).isbn);
		txtCoverPrice.setText(Double.toString(BookManager.records.get(rIndex).coverPrice));
		
		/*
		 * 		Adds the fields onto the JPanel
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
	 * 		Event handler to save, or close the window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Update Record"){
			BookManager.records.get(rIndex).name = txtName.getText();
			BookManager.records.get(rIndex).author = txtAuthor.getText();
			BookManager.records.get(rIndex).type = txtType.getText();
			BookManager.records.get(rIndex).yearPublished = txtYearPublished.getText();
			BookManager.records.get(rIndex).isbn = txtISBN.getText();
			BookManager.records.get(rIndex).coverPrice = Double.parseDouble(txtCoverPrice.getText());
			
			JOptionPane.showMessageDialog(null, "Book Record Updated");
			this.dispose();
		} else {
			this.dispose();
		}
	}
}
