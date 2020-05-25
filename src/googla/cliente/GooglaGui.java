package googla.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;



import googla.objects.TaskDoneToServer;
import googla.tools.TextUtilities;

/**
 * Client Side Gui App
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 */

class GooglaGui {
	
	
	private JFrame frame;
	private JTextField searchWord;
	private JButton searchButton;
	private JList<TaskDoneToServer> resultsFromSearch;
	private JEditorPane newsBody;
	private ClientGooglaMain client;
	private DefaultListModel<TaskDoneToServer> model;
	
	
	/**
	 * Class constructor creates the gui 
	 * @param client a client must be assigned to a gui
	 */
	GooglaGui(ClientGooglaMain client) { 
		this.client = client;
		frame = new JFrame("Googla-lhes - Ricardo Silva - LEI-PL - #69454");
		frame.setLayout(new BorderLayout());
		initGui();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); //adapta-se aos componentes
		centerWindow(frame);		
		frame.setVisible(true);		
	}
	
	/**
	 * Initializes the logic for the search word textfield and app center for the list and article view
	 */
	private void initGui() {
		searchElements();
		newsViewer();
	}
	
	/**
	 * Creates the panel for the search. Creates the textfield with a listener for placeholder text and add a button
	 */
	private void searchElements() {
		JPanel panel = new JPanel();
		searchWord = new JTextField();
		searchWord.setText("Type your word search");		
		searchWord.addMouseListener(new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {searchWord.setText("");}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(searchWord.getText().equals("Type your word search"))
					searchWord.setText("");			
			}
		});
		searchWord.setMargin(new Insets(3, 3, 3, 3));
		searchWord.setColumns(20);
		panel.add(searchWord);		
		createSearchButton();
        panel.add(searchButton);
        frame.add(panel, BorderLayout.NORTH);		
	}
	
	/**
	 * creates a button, when pressed it sends a message to the server for a wordsearch
	 */
	private void createSearchButton() {
		searchButton = new JButton("Search");
		searchButton.addActionListener(e -> {
            disableSearchButton();
            resultsFromSearch.setListData(new TaskDoneToServer[0]);
            newsBody.setText("");
            client.doSendSearchMessage(searchWord.getText());
        });
	}
	
	
	/**
	 * creates the center panel of the app where later we add the list of results and show the article
	 */
	private void newsViewer() 
	{
		JPanel panel = new JPanel(new GridLayout(1,0));
		resultsList(panel);
		newsBodyView(panel);
		frame.add(panel, BorderLayout.CENTER);		
	}
	
	/**
	 * Creates the a Jlist with the results and sets a listener in the list that sends a message for a article client request 
	 * @param panel we add this list to a panel
	 */
	private void resultsList(JPanel panel) {
        resultsFromSearch = new JList<>();
        resultsFromSearch.setLayoutOrientation(JList.VERTICAL);        
        resultsFromSearch.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !resultsFromSearch.isSelectionEmpty()) {
                client.doSendArticleRequest(resultsFromSearch.getSelectedValue().getSearchedArticleId());                
            }

        }); 
        JScrollPane scrollPane = new JScrollPane(resultsFromSearch);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        panel.add(scrollPane);
    }
	
	/**
	 * Creates the text viewer and adds it to a panel
	 * @param panel we add this article to a panel
	 */
	private void newsBodyView(JPanel panel) {
		newsBody = new JEditorPane();
		newsBody.setMargin(new Insets(10,7,10,7));
        JScrollPane scrollPane = new JScrollPane(newsBody);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        newsBody.setEditable(false);
        //newsBody.setLineWrap(true);
        panel.add(scrollPane);
	}	
	
	/**
	 * Method for centering the gui 
	 * @param frame JFrame
	 */
	private void centerWindow (JFrame frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);	    
	    frame.setLocation(x, y);	    
	}
	
	/**
	 * Displays the list of results from a word search if the word is not found it displays a message informing to the gui
	 * @param results array of TaskDoneToServer with results from search
	 */
	private void showResultsList(ArrayList<TaskDoneToServer> results ) {
		if(results.size()==0)
			JOptionPane.showMessageDialog(frame, "GooglaGUI -> Couldn't find the word in the news folder");
		model = new DefaultListModel<>();
		for(TaskDoneToServer task : results) {
			model.addElement(task);
		}
		resultsFromSearch.setModel(model);		
	}
	
	/**
	 * public interface for showing the results of a search frequency of word and article title
	 * @param results array of TaskDoneToServer with results from search
	 */
	void doShowListResults(ArrayList<TaskDoneToServer> results ) {
		showResultsList(results);
	}
	
	/**
	 * Displays the text in the left panel and highlights the words
	 * @param s article text
	 * @param pos positions where the searched string existis for use in highlighter
	 */
	private void showArticleText(String s,ArrayList<Integer> pos){
		newsBody.setText(s);
		try {
			TextUtilities.highlight(newsBody, searchWord.getText(), pos);
		} catch (Exception e) {
			System.out.println("GooglaGUI -> Something went wrong in highliter method");
		}	
	}
	
	/**
	 * public interface to show an article requested
	 * @param s article text
	 * @param pos positions where the searched string existis for use in highlighter
	 */
	void doShowArticleText(String s,ArrayList<Integer> pos) {
		showArticleText(s, pos);		
	}
	
	/**
	 * Enables the search button and clears the text being viewed when a search is done 
	 */
	void enableSearchButton() {		
		newsBody.setText("");
		this.searchButton.setEnabled(true);
	}
	
	/**
	 * Disables search button, for when a search is ocurring
	 */
	private void disableSearchButton() {		
		this.searchButton.setEnabled(false);
	}
	/**
	 * Message Box informing connection status
	 * @param choice if 0 the server wasn't up if 1 the server was disconnected
	 */
	void serverConnectionLost(int choice) {
		if(choice == 0){
			JOptionPane.showMessageDialog(frame, "GooglaGUI -> Can't connect to server. Please try again later");
			System.exit(0);			
		}else if (choice == 1) {
			JOptionPane.showMessageDialog(frame, "GooglaGUI -> Lost connection to server. Please try again later");
			System.exit(0);			
		}else 
			return;
	}
	
	
}
