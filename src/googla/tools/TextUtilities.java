package googla.tools;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import googla.objects.Article;

/**
 * 
 * @author Ricardo Silva
 * Aluno LEI-PL n:69454
 *
 */
public class TextUtilities{
	
	/**
	 * This method is only used by server to create article objects from the news in the folder
	 * @param s folder name
	 * @return arraylist of article objects
	 */
	public static ArrayList<Article> carregarNoticias(String s) {
		ArrayList<Article> jornal = new ArrayList<>();
		File dir = new File(s);
		FilenameFilter filter = (dir1, name) -> name.endsWith(".txt");    
        File[] filesList = dir.listFiles(filter);
        if (filesList != null) {
            for (File file : filesList) {           	
                BufferedReader in = null;                
                try {
                    in = new BufferedReader(new FileReader(file.getPath()));                    
                } catch (FileNotFoundException e) {
                    System.out.println("TextUtilities -> Error reading the file: " + file.getName() + " in path: " + file.getAbsolutePath());
                }              
                try {
                    String linha;
                    String titulo = null;
                    String corpo = null;
                    assert in != null; // ensure in is not null
                    for (int l=0; (linha=in.readLine())!=null; l++){
                        if (l==0)
                            titulo = linha;
                        else {
                            if (corpo == null)
                                corpo = linha;                            
                        }
                    }                    
                    Article article = new Article(jornal.size(), titulo, corpo);
                    jornal.add(article);
                } catch (IOException e) {
                    System.out.println("TextUtilities -> error in creating Article");
                } finally {
                    try {
                    	assert in != null;
                        in.close();
                    } catch (IOException e) {
                        System.out.println("TextUtilities -> couldn't close BufferedReader");
                    }
                }             
            }
        } else {
            System.out.println("TextUtilities -> the folder is empty of txt files");
        }
        return jornal;
    }	
		
	/**
	 * Function implementing KMP algorithm for string search - static method
	 * @param searchText text to search
	 * @param wordToSearch word to search
	 * @return integer array with positions where the word is found if none found it returns empty array
	 */
	public static ArrayList<Integer> searchWordMethod(String searchText, String wordToSearch){
		ArrayList<Integer> posicoes = new ArrayList<>();
		//  wordToSearch is null or empty
		if (wordToSearch == null || wordToSearch.length() == 0){
			System.out.println("TextUtilities -> wordToSearch is empty");			
		}
		// searchText is null or searchText's length is less than that of wordToSearch's
		assert wordToSearch != null;
		if (searchText == null || wordToSearch.length() > searchText.length()){
			System.out.println("TextUtilities -> there is no text to search");			
		}
		char[] chars = wordToSearch.toCharArray();
		// next[i] stores the index of next best partial match
		int[] next = new int[wordToSearch.length() + 1];
		for (int i = 1; i < wordToSearch.length(); i++){
			int j = next[i + 1];
			while (j > 0 && chars[j] != chars[i])
				j = next[j];
			if (j > 0 || chars[j] == chars[i])
				next[i + 1] = j + 1;
		}
		assert searchText != null;
		for (int i = 0, j = 0; i < searchText.length(); i++){
			if (j < wordToSearch.length() && searchText.charAt(i) == wordToSearch.charAt(j)){
				if (++j == wordToSearch.length()){					
					posicoes.add(i - j + 1);
				}
			}
			else if (j > 0){
				j = next[j];
				i--;
			}
		}		
		return posicoes;
	}
		
	
	private static MyHighlightPainter myHighlightPainter = new MyHighlightPainter(Color.LIGHT_GRAY);
	
	/**
	 * Method for highlighting word after search
	 * @param textComp newsbody display
	 * @param pattern word search
	 * @param positions position of the word to highlight
	 * @throws Exception error in highlighting
	 */
	public static void highlight(JTextComponent textComp, String pattern, ArrayList<Integer> positions) throws Exception {
		removeHighlights(textComp);
		Highlighter hilite = textComp.getHighlighter();
		for(Integer pos : positions)
			hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);		
	}
	
	/**
	 * Method to clean the text highlights 
	 * @param textComp newsbody text component
	 */
	private static void removeHighlights(JTextComponent textComp) {
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (Highlighter.Highlight highlight : hilites) {
			if (highlight.getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(highlight);
			}
		}
	}
	
	
	
}

class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

    MyHighlightPainter(Color color) {
        super(color);
    }
}
