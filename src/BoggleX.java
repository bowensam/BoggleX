/*===============================================================================================================================
Boggle X
Bowen Sam
March 25, 2013
Java, Eclipse SDK 3.4.1
=================================================================================================================================
Problem Definition - Required to develop a Boggle game that displays a board of random letters (5x5), and 
					 allows the user to enter words found on the board.  The program will report if the word 
					 entered by the user is indeed on the board.  With the exception of the first letter,
					 the search algorithm must be implemented recursively for every letter of the input word.

Input - The user's confirmation to welcome message in JOptionPane
	  - The user's preference for the size of the Boggle Board via keyboard input in JOptionPane
	  - The user's preference to enable/disable the dictionary during the game play in JOptionPane
	  - The user's word of which they found on the Boggle Board via keyboard input in JTextField
	  - The user's preference to exit the game in JButton
	  - The user's confirmation to new high score prompt in JOptionPane (if applicable)
	  - The user's confirmation to final score prompt in JOptionPane
	  - The user's confirmation to copyright prompt in JOptionPane

Output - Welcome message, boggle board size prompt, dictionary prompt, user interface (title, descriptions, 
		 used word list, boggle board, score, high score, input box, status)(updated accordingly depending 
		 upon an event), exit button, new high score prompt (if applicable), final score prompt, goodbye messages

Processing - Use the user keyboard input to determine the size of the Boggle Board, the size is limited to 4-10
				- If the user decides to enter a null input, the default 5x5 boggle board will be generated
		   - The boggle board is then randomly generated
		   - Use user's mouse click to determine whether dictionary check should be enabled throughout the 
		   	 duration of the program
		   - Sets up the user interface according to user's preference and default settings
		   - Upon and depending on the event (user input), do the following: 
		   		- If a key is released/typed
		   			- Change the letter to upper-case, in the case of all other character, they are remained untouched
		   		- If a key is released
		   			- Determine if an <Enter> has been released,
		   			 	If not:
		   			 	- Change the released key to upper-case (as another precaution)
		   				If yes:
			   			- Determine if the input is valid by checking its length
			   			- Determine if the input is valid by checking for the input's individual ASCII values
			   			- Determine if the input is valid by matching the input with a track list to see if the input 
			   			  reuses words
			   			- Determine if the input is valid by matching the word with a dictionary
			   			- Determine if the word is valid by using iteration method to search for all possible duplicates
			   			  of the first letter, their relative location (index values) are stored in an array
			   				- Continue to evaluate the validity of the word by recursively searching the 8 surrounding
			   				  of the last valid letter on the board and matching it for the current letter in question
			   				  	- This is done for the remaining letters of the word
			   				- At any point, if a letter is not found, the stack is popped off and the algorithm will
			   			      back-track to duplicates of the previous letter and determine if a new "path" will form
			   			      the user input (word)
			   				- A word is considered valid once the String index value of the letter equals to the length
			   			      of the word plus one.  At this point, the word is considered valid.
			   			  	- The stack will be popped off and any stacked back-track operation will be skipped to
			   			  	  avoid unnecessary overhead.
			   			  	- A valid word will be written to the used word track list
			   			- At any given point, except for the search method, if the input is proven invalid, subsequent
			   			  codes are skipped
			   			- After the aforementioned evaluation, the validity of the input is prompt to the user
			   			- Score is modified accordingly based on validity and length of input
			   			- Score is updated and prompt to user
			   			- If the score is higher than the high score (record stored in a file), the score is written
			   			  to the file which overwrites the previous high score.
			   			  	- Depending on whether dictionary is turned on, the high score value will be different
			   			- For the following scenario, the reset method is called to reset the text in the input JTextField 
			   				- When a valid word is entered
			   				- When an invalid word is entered
		   		- If there is a clicked on the EXIT button
		   			- If the user made a new high score, the user is prompted
		   			- The user is prompted for its final score and related goodbye messages are output
=================================================================================================================================*/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.border.Border;

import java.io.*;
import java.util.Scanner;

/*=====================================================================================================================================
  BoggleV10 class
=======================================================================================================================================
 List of Identifiers - Local variables and objects will be listed in each methods
   					 - Class variables, class arrays and class objects are listed in the following:
	 				 	- let myBoard represent the object required to access the boggle board initialized in the Board class (type Board)
	 					- let status represent the program's current status and as a notification bar for
	 					  the user (type JLabel)
	 					- let score represent the user's current score in the current game session (type long)
	 					- let scoreLabel represent the bar used to notify the user for his/her current score (type JLabel)
	 					- let highScoreLabel represent the bar used to notify the user for the game session's
	 					  current high score (type JLabel)
	 					- let word represent the user's input for the word he/she finds on the boggle board (type JTextField)
	 					- let usedWords represent a list of valid words used previously by the user (type JTextArea)
	 					- let newHighScore represent whether the user has made a new high score (type boolean)
	 					- let dictionary represent the user's preference to enable/disable the dictionary (type boolean)				  

	 					NOTE: The class variables above are required as opposed to instance variables because these variables
	 					needs to be actively updated throughout the runtime of the program. Certain methods such as actionPerformed,
	 					keyListerner does not allow the passing of arguments, hence the static modifier is required.
	 					(Specific explanations are shown beside the declaration of each class variables)
======================================================================================================================================*/
@SuppressWarnings("serial")
public class BoggleX extends JApplet implements ActionListener, KeyListener{
	static Board myBoard; //Object is required as init method needs to instantiate the BOARD through the constructor in the board class 
	static JLabel status = new JLabel(); //Required as the status label is actively updated in various methods including the keyPressed method
	static long score = 0; //Required since the score needs to be updated by the updateScore method in the keyPressed action handler
	static JLabel scoreLabel = new JLabel(" Score: "+score+" "); //Since score is static, the notification bar must be static to allow updates as well
	static JLabel highScoreLabel = new JLabel(); //Mutation required by the init method and the updateHighScore method
	static JTextField word = new JTextField(); //Required to be actively mutated by user in the keyReleased, keyTyped, keyPressed events 
	static JTextArea usedWords = new JTextArea(); //Required to be actively updated when a valid user input is entered
	static boolean newHighScore = false; //This flag is governed by the highScoreCheck method and is used in the goodBye method, because
	//goodBye method may be called by a method in the init method, this variable must be at the class level
	static boolean dictionary; //Required in the init method so that the setup is properly sequenced, thus it must be initialised in the init method
	//On top of that, other methods require access to this variable as well, thus this must be declared static 

	/** init method
	 * This procedural method initialize the applet and construct the GUI.
	 * The methods that could be called by this method are listed in the following:
	 * 		1) welcome method
	 * 		2) createNewUsedWordTracklist method
	 *  	3) updateHighScore method
	 *  	4) getBoardPreference
	 *  	5) getDictionaryPreference method
	 *  	6) goodBye method
	 *  
	 * This method also calls the following class:
	 * 		1) Board class
	 * 		2) Audio class
	 *  
	 * The following is performed:
	 *	 	1) Sets the title of the main JFrame
	 *		2) Sets the LookAndFeel of the applet with the system's LookAndFeel
	 *		3) Sets the default close operation of the main JFrame to exit on close
	 *		4) Modifies the value of dictionary based on the user's preference
	 *		5) Calls the Board constructor to initialize the Boggle board then assigns
	 *		   the board to the class variable (This is done because other methods in
	 *		   this class requires access to the same board)
	 *		6) Set up the Boggle grid (JButton array)
	 *		7) Add the Boggle grid to the JPanel boardPanel
	 *		8) Set up and add the boardPanel JPanel to the main JFrame
	 *		9) Set up and add the exit JButton to the controlPanel JPanel
	 *		10) Set up and add the controlPanel JPanel to the main JFrame
	 *		11) Set up and add the following JLabel to the statusPanel
	 *			- scoreLabel
	 *			- highScoreLabel
	 *			- dictionaryStatus
	 *			- gap
	 *			- description1
	 *			- description2
	 *			- description3
	 *			- description4
	 *			- status
	 *		12) Add a vertical glue between description4 (JLabel) and usedWords (JTextArea)
	 *		13) Set up and add JTextArea usedWords to JScrollPane scroll
	 *		14) Set up and JScrollPane scroll to JFrame main
	 *		15) Set up and add JTextField word to JFrame main
	 *		16) Set up and add JLabel status to JFrame main
	 *		17) Set up and add JPanel statusPanel to JFrame main
	 *		18) Pack main JFrame
	 *		19) Set main JFrame as visible
	 *  	20) Set main JFrame location relative to null so that the frame appears in the middle of the screen
	 *  	21) Request focus on the words JTextField
	 *  	22) Call the Audio constructor (Credits to painkiller on stackoverflow.com)
	 *   	 
	 * @param none
	 * @return void
	 * 
	 *  ===========================================================================================================
	 * List of Identifiers - let bog represent the object required to implement actionListener to specific components (type BoggleV10) 
	 * 					   - let main represent the main window used to show the user's GUI (type JFrame)
	 * 					   - let dialog represent the window used to show UFP (type JFrame)
	 * 					   - let statusPanel represent the panel used to hold all components related to showing the
	 * 						 status of the current game session (type JPanel)
	 * 					   - let boardPanel represent the panel used to hold all components related to showing the
	 * 						 Boggle board (type JPanel)
	 * 					   - let controlPanel represent the panel used to hold all components related to showing the
	 * 						 control of the current game session (type JPanel)
	 * 					   - let title represent the bar used to show the title of the game to the user (type JLabel)
	 * 					   - let dictionaryStatus represent the bar used to show the current status of the dictionary
	 * 						 setting (whether it is enabled or disabled) (type JLabel)
	 * 					   - let description1 represent a bar used to show a description related to the game (type JLabel)
	 * 					   - let description2 represent a bar used to show a description related to the game (type JLabel)
	 * 					   - let description3 represent a bar used to show a description related to the game (type JLabel)
	 * 					   - let description4 represent a bar used to show a description related to the game (type JLabel) 
	 * 					   		NOTE: 4 JLabels are used instead of a JTextArea due to a major alignment error in BoxLayout
	 * 					   - let gap represent an empty space used to align the GUI (type JLabel)
	 * 					   - let exit represent a button used to confirm the user's preference to quit (type JButton)
	 * 					   - let scroll represent a scroll bar used for the usedWords JTextArea (type JScrollBar)
	 * 					   - let defaultFont represent the font with the default font name, font style and font
	 *					     size; this would be based on the defaults of the font used in the top-level container (type Font)
	 *					   - let line represent the border style used to set the borders of certain components (type Border)
	 *					   - let BOARDSIDE represent the user's preference for the size of the Boggle Board (type byte)
	 *					   - let dictPref represent the user's preference to use the dictionary (type boolean)
	 *					   - let myBoard represent the object used to call the Board constructor (type Board)
	 *					   - let bgm represent the object used to call the Audio constructor (type Audio)
	 * =========================================================================================================== 
	 */
	public void init(){		
		BoggleX bog = new BoggleX();
		JFrame main = new JFrame("Boggle X");
		JFrame dialog = new JFrame();
		JPanel statusPanel = new JPanel(); //Title, Score, HighScore, Status, TextField (Word), WordList
		JPanel boardPanel; 
		JPanel controlPanel = new JPanel(new GridLayout(1, 1));
		JLabel title = new JLabel("Boggle X ", JLabel.CENTER);
		JLabel dictionaryStatus = new JLabel();
		JLabel description1 = new JLabel("  Found a word? Type it and press <Enter> "); //Did not use JTextArea as it caused major alignment issues  
		JLabel description2 = new JLabel("  Scoring: +100 pts for every valid letter ");
		JLabel description3 = new JLabel("                   -100 pts for every invalid letter");
		JLabel description4 = new JLabel("  To Quit, click the EXIT button on the right ");
		JLabel gap = new JLabel("   ");
		JButton exit = new JButton("EXIT");
		JScrollPane scroll;
		Font defaultFont = super.getFont();
		Border line = BorderFactory.createLineBorder(Color.black);
		final byte BOARDSIDE;
		final byte LETTERSIZE = 80;
		byte dictPref;

		//Setup UI with system's LookAndFeel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) {} 
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {} 
		catch (UnsupportedLookAndFeelException e) {}

		//Setup JFrame main's default close operation
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		welcome();

		//Get user's preferences
		BOARDSIDE = getBoardPreference();
		dictPref = getDictionaryPreference();

		//Preparing game mechanics
		if (dictPref == 0){
			dictionary = true;
			dictionaryStatus.setText("<html>&nbsp;Dictionary:&nbsp;<font color='green'>ON </font></html>");
		}
		else{ //dictPref must == 1 since 2 is handled inside the getDictionaryPreference method
			dictionary = false;
			dictionaryStatus.setText("<html>&nbsp;Dictionary:&nbsp;<font color='red'>OFF </font></html>");
		}

		try {
			createNewUsedWordTracklist();
			updateHighScore();
		}
		catch (IOException e) {}


		//boardPanel setup and the addition of its components------------------------------------------------------
		//JPanel boardPanel and JLabels myBoard.getBoard()
		boardPanel = new JPanel(new GridLayout(BOARDSIDE, BOARDSIDE));

		myBoard = new Board(BOARDSIDE);

		for (int i = 0; i < myBoard.getBoard().length; i++){
			for (int j = 0; j < myBoard.getBoard()[i].length; j++){
				myBoard.getBoard()[i][j].setPreferredSize(new Dimension(LETTERSIZE, LETTERSIZE));
				myBoard.getBoard()[i][j].setOpaque(false);
				myBoard.getBoard()[i][j].setFont(new Font(defaultFont.getFontName(), Font.BOLD, 40));
				myBoard.getBoard()[i][j].setHorizontalAlignment(JLabel.CENTER);
				myBoard.getBoard()[i][j].setBorder(line);
				boardPanel.add(myBoard.getBoard()[i][j]);
			}
		}
		boardPanel.setPreferredSize(new Dimension(BOARDSIDE*LETTERSIZE, BOARDSIDE*LETTERSIZE));
		main.getContentPane().add(boardPanel, BorderLayout.CENTER); //addition of boardPanel to the main frame
		//-----------------------------------------------------------------------------------------------------------

		//controlPanel setup and the addition of its component-------------------------------------------------------
		//JButton exit
		exit.addActionListener(this);
		exit.setFont(new Font(defaultFont.getFontName(), Font.BOLD, 26));
		controlPanel.add(exit);

		//JPanel controlPanel
		controlPanel.setPreferredSize(new Dimension(LETTERSIZE+20, BOARDSIDE*LETTERSIZE));
		main.getContentPane().add(controlPanel, BorderLayout.LINE_END); //addition of controlPanel to the main frame
		//-----------------------------------------------------------------------------------------------------------

		//statusPanel setup and the addition of its components-------------------------------------------------------
		//JLabels title, scoreLabel, highScoreLabel
		title.setFont(new Font(defaultFont.getFontName(), Font.BOLD+Font.ITALIC, 55));
		statusPanel.add(title);
		scoreLabel.setFont(new Font(defaultFont.getFontName(), Font.ITALIC, 18));		
		statusPanel.add(scoreLabel);
		highScoreLabel.setFont(new Font(defaultFont.getFontName(), Font.ITALIC, 18));
		statusPanel.add(highScoreLabel);

		//JLabels dictionaryStatus, gap, description1, description2, description3, description4 
		dictionaryStatus.setFont(new Font(defaultFont.getFontName(), Font.ITALIC, 18));
		statusPanel.add(dictionaryStatus);
		statusPanel.add(gap);
		statusPanel.add(description1);
		statusPanel.add(description2);
		statusPanel.add(description3);
		statusPanel.add(description4);

		statusPanel.add(Box.createVerticalGlue());

		//JTextArea usedWords and JScrollPane scroll
		usedWords.setOpaque(false);
		usedWords.setEnabled(false);
		usedWords.setDisabledTextColor(new Color(0,0,0));
		usedWords.setMaximumSize(new Dimension(500, 100));
		scroll = new JScrollPane(usedWords);
		scroll.setPreferredSize(usedWords.getPreferredSize());
		statusPanel.add(scroll);	

		//JTextField word
		word.addKeyListener(bog);
		word.setOpaque(false);
		word.setPreferredSize(new Dimension(500, 25));
		word.setMaximumSize(word.getPreferredSize());
		statusPanel.add(word);

		//JLabel status
		status.setText("Waiting for input...");
		status.setFont(new Font(defaultFont.getFontName(), Font.BOLD, 14));
		status.setOpaque(false);
		statusPanel.add(status);

		//JPanel statusPanel
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.PAGE_AXIS));
		statusPanel.setPreferredSize(new Dimension(500, BOARDSIDE*LETTERSIZE));
		main.getContentPane().add(statusPanel, BorderLayout.LINE_START); //addition of statusPanel to the main frame
		//----------------------------------------------------------------------------------------------------------

		//Finalizing JFrame main and show GUI
		main.pack();
		main.setVisible(true);
		main.setLocationRelativeTo(null); //Centres the game

		//UFP
		JOptionPane.showMessageDialog(dialog, "<html><i>Tip: Turn up your volume for a richer Boggle experience</i></html>", "Boggle X", JOptionPane.INFORMATION_MESSAGE);

		//Set focus ("The blinking cursor") to the main JFrame then set the focus to JTextField word where the game waits for user input
		main.setVisible(true);
		word.requestFocusInWindow();
	}

	/** actionPerformed method
	 * This procedural method serves as an event handler.
	 * Specific actions and specific methods will be called/performed based on the user's interactions
	 *  
	 * The method that could be called by this method are listed in the following:
	 * 		1) goodBye method
	 *
	 * It also performs the following:
	 *   	1) Respond to a mouse click on the <EXIT> JButton and checks the action command
	 *      2) If the action command equals to "EXIT" (which will always be true given that is the only click-able interaction)
	 *         the goodBye method will be called
	 * 
	 * @param event - an event, or an user's interaction (type ActionEvent)
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("EXIT"))
			goodBye();
	}
	/** keyReleased method
	 * This procedural method serves as an event handler.
	 * Specific actions and specific methods will be called/performed based on the user's interactions
	 *  
	 * It performs the following:
	 *   	1) Respond to the release of a key
	 *      2) Delete any leading or tailing space in the text of the input in word JTextField
	 *      3) Converts all letters of the text of the input in word JTextField to upper case
	 * 
	 * @param event - an event, or an user's interaction (type KeyEvent)
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	public void keyReleased(KeyEvent event) {
		word.setText(word.getText().trim().toUpperCase()); //Visual appearance, technically not required
	}
	/** keyTyped method
	 * This procedural method serves as an event handler.
	 * Specific actions and specific methods will be called/performed based on the user's interactions
	 *  
	 * It performs the following:
	 *   	1) Respond to the holding of a key
	 *      2) Delete any leading or tailing space in the text of the input in word JTextField
	 *      3) Converts all letters of the text of the input in word JTextField to upper case
	 * 
	 * @param event - an event, or an user's interaction (type KeyEvent)
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	public void keyTyped(KeyEvent event) {
		word.setText(word.getText().trim().toUpperCase()); //Visual appearance, technically not required
	}
	/** keyPressed method
	 * This procedural method serves as an event handler.
	 * Specific actions and specific methods will be called/performed based on the user's interactions
	 *  
	 * The methods that could be called by this method are listed in the following:
	 * 		1) wordReuseCheck method
	 * 		2) checkEnglishDictionary method
	 * 		3) checkWordValidity method
	 * 		4) writeUsedWordTracklist method
	 * 		5) updateUsedWordsTextArea method
	 * 		6) highScoreCheck method
	 * 		7) writenewHighScore method
	 * 		8) updateHighScore method
	 * 		9) ouputValidityResult method
	 * 		10) reset method
	 *  
	 * It performs the following:
	 *   	1) Responds when a key is pressed before it is released
	 *      2) Delete any leading or tailing space in the text of the input in word JTextField (Done as a precaution)
	 *      3) Converts all letters of the text of the input in word JTextField to upper case (Done as a precaution)
	 *      4) Check if the key pressed was an <Enter>
	 *      5) If so, perform checks on the input in the JTextField word by calling their respective methods
	 *      	- Checks if the input is of adequate length
	 *      	- Checks if the input is composed only of alphabetical letters
	 *      	- Checks if the input is a word that had been previously used
	 *      	- Checks if the input is an English word (only applicable when dictionary is On)
	 *      	- Checks if the input is actually present on the Boggle board
	 *      6) The validity of the input is changed accordingly throughout
	 *      	- At any point, if the input is proven to be invalid, subsequent checks are skipped
	 *      	- Subsequent checks are only performed if the input passes the previous check
	 *      7) If the aforementioned checks are passed, the input is considered valid, a method is call to add the valid
	 *         word to the used word track list
	 *      8) A method is called to update the usedWord JTextArea
	 *      9) A method is called to update the score
	 *      10) A method is called to compare the newly updated score to the high score
	 *      	- If the new score is higher than the high score, a method is called to update the new high score
	 *      11) By calling a method, the validity of the input is prompted to the user
	 *      12) The reset method is called to clear the text in JTextField regardless of valid or invalid input 
	 *      
	 *      RECALL: All these are done ONLY when the key pressed is detected to be an <Enter>
	 * 
	 * @param event - an event, or an user's interaction (type KeyEvent)
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let validInput represent the validity of the input (type boolean)
	 * ==================================================================================================================
	 */
	public void keyPressed (KeyEvent event){
		boolean validInput = true;
		word.setText(word.getText().trim().toUpperCase()); //In case the last letter was not capitalized when a key is held while pressing <enter> AND/OR <space> is held while pressing <enter>

		if (Character.toString(event.getKeyChar()).equals("\n")){
			if (word.getText().length() < 3){
				status.setText("The word is too short! Your word needs to be at least 3 letters long! ");
				scoreLabel.setText(" Score: "+score+" ");
			}
			else{
				for (int character = 0; character < word.getText().length(); character++){ //Scanning each character of the input
					if ((word.getText().charAt(character) < 65) || (word.getText().charAt(character) > 90)){ //Characters with ASCII values less than 65 or greater than 90 are classified as invalid (They are not A-Z characters)
						validInput = false;
						break;
					}
				} 
				if (validInput == true){
					try{
						validInput = wordReuseCheck();
						if (validInput == true){
							if (dictionary == true)
								validInput = checkEnglishDictionary();
							if (validInput == true){
								validInput = checkWordValidity();
								if (validInput == true){
									writeUsedWordTracklist();
									updateUsedWordsTextArea();
								}
							}
							updateScore(validInput);
							newHighScore = highScoreCheck();
							if (newHighScore == true){
								writeNewHighScore();
								updateHighScore();
							}
							outputValidityResult(validInput);
						}
					}
					catch (IOException e){}
				}
				else{
					status.setText("That was not an English word! Remove the non-alphabetical letters!");
					scoreLabel.setText(" Score: "+score+" ");
				}
			}
			reset();
		}
	}
	/** welcome method
	 * This procedural method output a message to welcome the user to Boggle X
	 * 
	 * @param none
	 * @return void
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show UFP (type JFrame)
	 * ==================================================================================================================
	 */
	private void welcome(){
		JFrame dialog = new JFrame();
		JOptionPane.showMessageDialog(dialog, "<html><b>Welcome to Boggle X</b></html>" +
				"\n\nTry to form words with adjacent letters." +
				"\n\n<html><u>Rules</u></html>" +
				"\n - Your word can only be formed with ADJACENT letters (i.e. each letter must be in contact with one another in the surrounding 8 blocks)" +
				"\n - Within the same word, you are not allowed to jump or reuse letters." +
				"\n - You are not allowed to enter words you have entered before." +
				"\n\nSimple? Click OK to Begin!", "Boggle X", JOptionPane.INFORMATION_MESSAGE);
	}
	/** getBoardPreference method
	 * This functional method prompts the user to enter the size of the Boggle board in which they play in.  
	 * The user preference is then change to a value between 4-10 (inclusive) as byte back to the calling block.
	 * 
	 * If the user left the field blank and hit <Enter>, the default size of 5 is assumed
	 * If the user click <Cancel> or <Close>, the goodBye method is called and the user's preference to quit is assumed true  
	 * 
	 * The methods that could be called by this method are listed in the following:
	 * 		1) goodBye method
	 * 
	 * @param none
	 * @return the user's preference of boggle board size (type byte)
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show UFP (type JFrame)
	 * 					   - let input represent the user's response to the dialog (type String)
	 * 					   - let boardSize represent the size of the boggle board; this value is based on the value of 
	 * 					 	 input after a series of checks and evaluation (type int) 
	 * ==================================================================================================================
	 */
	private byte getBoardPreference () {
		JFrame dialog = new JFrame();
		String input;
		int boardSize; 

		while(true){
			input = JOptionPane.showInputDialog(dialog, "<html><b>How big would you like your Boggle Board to be?</b></html>" +
					"\nType in the desired board size (4-10) and press Enter." +
					"\n(Leave blank for the default 5x5 board)", "Boggle X", JOptionPane.QUESTION_MESSAGE);
			if (input == null)
				goodBye();
			else if (input.equals(""))
				return 5;
			try{ 
				boardSize = Integer.parseInt(input);
				if (boardSize < 4)
					JOptionPane.showMessageDialog(dialog, "Your board is too small! Please select a board size of at least 4.", "Boggle X", JOptionPane.ERROR_MESSAGE);
				else if (boardSize > 10)
					JOptionPane.showMessageDialog(dialog, "Your board is too large! A smaller board would be easier to play with.", "Boggle X", JOptionPane.ERROR_MESSAGE);
				else
					return (byte)(boardSize);
			}
			catch (NumberFormatException e){
				JOptionPane.showMessageDialog(dialog, "Please enter an Integer value.", "Boggle X", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/** getDictionaryPreference method
	 * This functional method prompts the user to enter their preference to enable or disable the dictionary.  
	 * The user preference is sent as a byte value between 0-2 (inclusive) back to the calling block.
	 * 
	 * If the user click <Cancel> or <Close>, the goodBye method is called and the user's preference to quit is assumed true  
	 * 
	 * The methods that could be called by this method are listed in the following:
	 * 		1) goodBye method
	 * 
	 * @param none
	 * @return the user's preference to enable/disable dictionary check (type byte)
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show UFP (type JFrame)
	 * 					   - let dictPref represent a value used to determine the preference to enable or disable dictionary check (type byte)
	 * ==================================================================================================================
	 */
	private byte getDictionaryPreference () {
		JFrame dialog = new JFrame();
		byte dictPref;

		dictPref = (byte)(JOptionPane.showConfirmDialog(dialog, "<html><b>Would you like to turn on Dictionary?</b></html>", "Boggle X", JOptionPane.YES_NO_CANCEL_OPTION));
		if (dictPref == 2)
			goodBye();
		return dictPref;
	}

	/** checkEnglishDictionary method
	 * This functional method match the user input with a file containing more than 88000 English words that is greater than 3
	 * letters long. If there is a match between user input and an entry in the file, the word is considered valid at this point,
	 * and a true value is returned, vice versa.
	 * 
	 * @param none
	 * @return the validity of the user input in terms of whether or not it is an English word (type boolean)
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show UFP (type JFrame)
	 * 					   - let line represent a holder used to temporary store one entry of the dictionary file (type String)
	 * 					   - let dict represent an object used to read the dictionary file (type BufferedReader)
	 * ==================================================================================================================
	 */
	private boolean checkEnglishDictionary () throws IOException{
		JFrame dialog = new JFrame();
		try{
			BufferedReader dict = new BufferedReader(new FileReader("./DATA/Dictionary.txt"));
			String line = dict.readLine().toUpperCase();
			while(!line.equals(" ")){
				if (word.getText().equals(line))
					return true;
				line = dict.readLine().toUpperCase();
			}
			return false;
		}
		catch (FileNotFoundException e){
			JOptionPane.showMessageDialog(dialog, "Error: Dictionary Not Found."+"\nAll input will now be assumed as valid English words.", "Boggle X", JOptionPane.WARNING_MESSAGE);
			return true;
		}
	}
	/** letterReuseCheck method
	 * This functional method match the index location of the current letter of the user input with the index locations 
	 * of previously validated letters.  If there is a match, the current letter is considered a "reuse" and the validity
	 * of the word is returned to be false, vice versa.
	 * 
	 * @param checkLetter - the letter to be checked for reuse (type int)
	 * 		  wordLocation - the index location of all the validated letters (two dimensional array of int)
	 * @return the validity of the user input in terms of whether or not there is a reuse of letters (type boolean)
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show UFP (type JFrame)
	 * 					   - let rowIndexValid represent the validity of the row index of the current letter of the input (type boolean)
	 * 					   - let colIndexValid represent the validity of the col index of the current letter of the input (type boolean)
	 * ==================================================================================================================
	 */
	private boolean letterReuseCheck (int checkLetter, int[][] wordLocation){
		boolean rowIndexValid, colIndexValid; //Separate variable are required for the two dimensions because having one index the same is not enough to prove that the letters had been reused (that the current letter is invalid)

		for (int usedLetterIndex = checkLetter-1; usedLetterIndex >= 0; usedLetterIndex--){
			rowIndexValid = true;
			colIndexValid = true;

			//Row Coordinate Check
			if (wordLocation[0][usedLetterIndex] == wordLocation[0][checkLetter]){
				rowIndexValid = false;
			}

			//Column Coordinate Check
			if (wordLocation[1][usedLetterIndex] == wordLocation[1][checkLetter]){
				colIndexValid = false;
			}

			if (rowIndexValid == false && colIndexValid == false){
				return false; // letter is not valid at this location
			}
		}
		return true;
	}
	/** checkWordValidity method
	 * This functional method is the controller of the checkLetterValidity recursive search algorithm.
	 * This method first locates all duplicates of the first letter of the input on the board and stores them in an 
	 * int array through iteration.  Based on the concept of trial and error, the first duplicate of the first letter is
	 * used as a starting point and the checkLetterValidity is called to search the surrounding of this starting point 
	 * (More details in the checkLetterValidity method).  If checkLetterValidity returns false, this method uses the
	 * next duplicate of the first letter and calls the checkLetterValidity method again until either a true value is
	 * returned or there are no more duplicates of the first letter.  In such case, a false value is return to the
	 * calling block.
	 * 
	 * @param none
	 * @return the validity of the user input in terms of whether or not it exists on the board (type boolean)
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let wordValid represent the validity of the word (type boolean)
	 * 					   - let totalFirstDuplicate represent the total number of duplicates for the first letter (type int)
	 * 					   - let wordLocation represents a storage used to store the index locations of all valid letters
	 * 						 (two dimensional array of type int)
	 * 					   - let firstLetterLocation represent the index locations of all first letter duplicates
	 * 						 (two dimensional array of type int)
	 * 					   - let duplicate represent the duplicate of first letter currently being checked (type int)
	 * ==================================================================================================================
	 */
	private boolean checkWordValidity(){
		boolean wordValid;
		int totalFirstDuplicate = 0;
		int[][] wordLocation = new int [2][word.getText().length()];

		status.setText("Loading...");

		for (int i = 0; i < wordLocation.length; i++)
			for (int j = 0; j < wordLocation[i].length; j++)
				wordLocation[i][j] = -1;

		for (int i = 0; i < myBoard.getBoard().length; i++){
			for (int j = 0; j < myBoard.getBoard()[i].length; j++){	
				if (myBoard.getBoard()[i][j].getText().charAt(0) == word.getText().charAt(0))
					totalFirstDuplicate++;
			}
		}	

		if (totalFirstDuplicate == 0)
			wordValid = false;
		else
			wordValid = true;

		if (wordValid == true){
			int[][] firstLetterLocation = new int[2][totalFirstDuplicate];
			int duplicate = 0;

			for (int i = 0; i < myBoard.getBoard().length; i++){
				for (int j = 0; j < myBoard.getBoard()[i].length; j++){
					if (myBoard.getBoard()[i][j].getText().charAt(0) == word.getText().charAt(0)){
						firstLetterLocation[0][duplicate] = i;
						firstLetterLocation[1][duplicate] = j;
						duplicate++;
					}
					if (duplicate == totalFirstDuplicate){
						break;
					}
				}
				if (duplicate == totalFirstDuplicate)
					break;
			}

			wordValid = false; //Now checking for second letter location, assuming false input
			for (int i = 0; i < firstLetterLocation[0].length; i++){
				wordLocation[0][0] = firstLetterLocation[0][i]; //int row
				wordLocation[1][0] = firstLetterLocation[1][i]; //int col

				wordValid = checkLetterValidity(0, wordLocation);
				if (wordValid == true)
					break;
			}
		}
		return wordValid;
	}
	/** checkLetterValidity method
	 * This functional method is the core of the recursive search algorithm.
	 * Given the location of the first letter, this algorithm searches the surrounding 8 blocks on the board, looking for
	 * a match in the next letter of the user input.  If there is a match, a recursive call is made, and the method proceeds
	 * to checking the next letter until the letter value reaches the length of the user input.  At this point, the word
	 * will be considered valid on the boggle board.  Note that if any given point, there is no match in the surroundings,
	 * a false value will be returned.  In such case, the stack is popped off and the algorithm back tracks at alternate
	 * duplicates of previous letters until there is a match.  It is important to note that when a value of true is
	 * returned, the operation on the stacks are popped off but redundant operations will be skipped to reduce overhead.
	 * 
	 * The method also fills in the wordLocation array as letters are validated
	 * 
	 * A side note on the order of which the base cases are arranged.  It is specifically arranged this way to speed
	 * up the searching process.  Given dictionary is off, random user input typically goes from left to right and top
	 * to bottom due to the way English words are formatted. 
	 * 
	 * @param letter - the current letter/letter in question to be checked (type int)
	 * 		  wordLocation - the index location of all the validated letters (two dimensional array of int)
	 * @return the validity of the user input in terms of whether the next letter exist in the surrounding of the 
	 * 		   current letter (type boolean)
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let letterValid represent the validity of the current letter (type boolean)
	 * 					   - let row represent the rowIndex location of the current letter (type int)
	 * 					   - let col represent the colIndex location of the current letter (type int)
	 * ==================================================================================================================
	 */
	private boolean checkLetterValidity (int letter, int[][] wordLocation){
		boolean letterValid = false;

		status.setText("Loading");
		if (letter+1 == word.getText().length()){
			return true;
		}
		else{
			int row = wordLocation[0][letter];
			int col = wordLocation[1][letter];

			if (letterValid == false && (col+1) < myBoard.getBoard().length && word.getText().charAt(letter+1) == myBoard.getBoard()[row][col+1].getText().charAt(0)){ //5
				wordLocation[0][letter+1] = row;
				wordLocation[1][letter+1] = col+1;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true) //If the letter in question is proven to be a reuse, the checkLetterValidity operation is skipped
					letterValid = checkLetterValidity(letter+1, wordLocation); //The variable is assigned a value so that the stacked operations can be skipped accordingly
			}
			if (letterValid == false && (row+1) < myBoard.getBoard().length && word.getText().charAt(letter+1) == myBoard.getBoard()[row+1][col].getText().charAt(0)){ //7
				wordLocation[0][letter+1] = row+1;
				wordLocation[1][letter+1] = col;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}
			if (letterValid == false && (row+1) < myBoard.getBoard().length && (col+1) < myBoard.getBoard().length && word.getText().charAt(letter+1) == myBoard.getBoard()[row+1][col+1].getText().charAt(0)){ //8
				wordLocation[0][letter+1] = row+1;
				wordLocation[1][letter+1] = col+1;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}
			if (letterValid == false && (col-1) > -1 && word.getText().charAt(letter+1) == myBoard.getBoard()[row][col-1].getText().charAt(0)){ //4
				wordLocation[0][letter+1] = row;
				wordLocation[1][letter+1] = col-1;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}
			if (letterValid == false && (row-1) > -1 && word.getText().charAt(letter+1) == myBoard.getBoard()[row-1][col].getText().charAt(0)){ //2
				wordLocation[0][letter+1] = row-1;
				wordLocation[1][letter+1] = col;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}
			if (letterValid == false && (row-1) > -1 && (col-1) > -1 && word.getText().charAt(letter+1) == myBoard.getBoard()[row-1][col-1].getText().charAt(0)){ //1
				wordLocation[0][letter+1] = row-1;
				wordLocation[1][letter+1] = col-1;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}
			if (letterValid == false && (row-1) > -1 && (col+1) < myBoard.getBoard().length && word.getText().charAt(letter+1) == myBoard.getBoard()[row-1][col+1].getText().charAt(0)){ //3
				wordLocation[0][letter+1] = row-1;
				wordLocation[1][letter+1] = col+1;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}		
			if (letterValid == false && (row+1) < myBoard.getBoard().length && (col-1) > -1 && word.getText().charAt(letter+1) == myBoard.getBoard()[row+1][col-1].getText().charAt(0)){ //6
				wordLocation[0][letter+1] = row+1;
				wordLocation[1][letter+1] = col-1;
				letterValid = letterReuseCheck(letter+1, wordLocation);
				if (letterValid == true)
					letterValid = checkLetterValidity(letter+1, wordLocation);
			}
		}//end of else
		status.setText("Loading...");
		return letterValid;
	}//end of method

	/** outputValidityResult method
	 * This procedural method prompts the user whether their word is valid by setting the text in the status JLabel
	 * 
	 * @param wordValid - the validity of the user input/word (type boolean)
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	private void outputValidityResult(boolean wordValid){
		if (wordValid == true)
			//JOptionPane.showMessageDialog(dialog, "Correct!", "Boggle X", JOptionPane.INFORMATION_MESSAGE);
			status.setText("CORRECT! +"+word.getText().length()*100+" pts!");
		else
			//JOptionPane.showMessageDialog(dialog, "Incorrect!", "Boggle X", JOptionPane.INFORMATION_MESSAGE);
			status.setText("INCORRECT! "+word.getText().length()*100*-1+" pts!");
		
		reEnableMyBoard();
	}
	
	private void reEnableMyBoard () {
		for (int i = 0; i < myBoard.Board.Length; i++) {
			for (int j = 0; j < myBoard[i].Board.Length; j++) {
				
			}
		}
	}
	
	/** createNewUsedWordTracklist method
	 * This procedural method is created as a precaution to ensure proper program functionality in case of the following circumstances:
	 * 	1) Overwrite the <Used Word Tracklist.txt> created from the last program execution 
	 * 		- The old file cannot be used as it contains irrelevant data (ie. words that the current user have not used or does not exist on the current board)
	 * 	2) User created a copy of/edited <Used Word Tracklist.txt> prior to launching the program
	 * 		- The edited <Used Word Tracklist.txt> may contain irrelevant data that interferes with the functioning of the program
	 * 
	 * 	This method addresses the aforementioned problem(s) by overwriting/creating a fresh, new copy of <Used Word Tracklist.txt> 
	 * 	This method is also used as an exception handler for the FileNotFoundException
	 * 
	 * @param none
	 * @return void
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let usedWordList represent the object used to create the Used Word Tracklist (type BufferedWriter)
	 * ==================================================================================================================
	 * 
	 */
	private void createNewUsedWordTracklist () throws IOException{
		BufferedWriter usedWordList = new BufferedWriter(new FileWriter("./USERDATA/Used Word Tracklist.txt"));
		usedWordList.close();
	}
	/** wordReuseCheck method
	 * This functional method checks whether the user input is a reuse of a previous validated word.
	 * The method accomplishes that by matching the user input with a used word track list file.
	 * If there is a match between user input and an entry in the file, the word is considered invalid at this point,
	 * and a false value is returned, vice versa.
	 * 
	 * This method also implements an anti-cheating feature that deducts points if the user deleted the used words
	 * track list.
	 * 
	 * @param none
	 * @return the validity of the user input in terms of whether or not it is a reuse (type boolean)
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let usedWordList represent the object used to read the Used Word Tracklist (type Scanner)
	 * 					   - let usedWord represents an entry in the usedWordList file (type String)
	 * ==================================================================================================================
	 * 
	 */
	private boolean wordReuseCheck () throws IOException {
		JFrame dialog = new JFrame();
		try{
			Scanner usedWordList = new Scanner(new File("./USERDATA/Used Word Tracklist.txt"));
			String usedWord;

			while (usedWordList.hasNextLine()){
				usedWord = usedWordList.nextLine();
				if (usedWord.equals(word.getText())){
					status.setText("You have already used this word! Try another word!");
					scoreLabel.setText(" Score: "+score+" ");
					return false;
				}
			}
			return true;
		}
		catch (FileNotFoundException e){
			status.setText("Used Word Tracklist NOT FOUND");
			JOptionPane.showMessageDialog(dialog, "Used Word Tracklist NOT FOUND. All words will now be considered invalid." +
					"\n<html><b>Please do not cheat!</b></html>", "Boggle X", JOptionPane.WARNING_MESSAGE);
			updateScore(false);
			return false;
		}
	}
	/** writeUsedWordTracklist method
	 * This procedural method writes the new validated word into the used word tracklist file.
	 * 
	 * @param none
	 * @return void
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let usedWordList represent the object used to write the Used Word Tracklist (type BufferedWriter)
	 * ==================================================================================================================
	 * 
	 */
	private void writeUsedWordTracklist () throws IOException{
		while(true){
			try{
				BufferedWriter usedWordList = new BufferedWriter(new FileWriter("./USERDATA/Used Word Tracklist.txt", true));
				usedWordList.write(word.getText());
				usedWordList.newLine();
				usedWordList.close();
				break;
			}
			catch (FileNotFoundException e){
				createNewUsedWordTracklist();
			}
		}
	}
	/** updateScore method
	 * This procedural method updates the current score of the user and show the new changes in the JLabel scoreLabel.
	 * 
	 * @param the validity of the user input in terms of whether or not it exists on the board (type boolean) 
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let scoreMod represent the variable used to determine the new score (type byte)
	 * ==================================================================================================================
	 * 
	 */
	private void updateScore (boolean validInput){
		byte scoreMod = 1;

		if (validInput == true){
			scoreLabel.setText("<html>&nbsp;Score: "+(score+(word.getText().length()*100*scoreMod))+"&nbsp;&nbsp;<font color='green'>&nbsp;+"+(word.getText().length()*100*scoreMod)+"&nbsp;pts</font></html> ");
			score = score+(word.getText().length()*100*scoreMod);
		}
		else{
			scoreMod = -1;
			scoreLabel.setText("<html>&nbsp;Score: "+(score+(word.getText().length()*100*scoreMod))+"&nbsp;&nbsp;<font color='red'>&nbsp;"+(word.getText().length()*100*scoreMod)+"&nbsp;pts</font></html> ");
			score = score+(word.getText().length()*100*scoreMod);
		}
	}
	/** updateUsedWordsTextArea method
	 * This procedural method updates the usedWords JTextArea to show the new used validated words. 
	 * 
	 * @param none 
	 * @return void
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let usedWordList represent the object used to read the Used Word Tracklist file (type BufferedReader)
	 * ==================================================================================================================
	 * 
	 */
	private void updateUsedWordsTextArea () throws IOException{
		try{
			BufferedReader usedWordList = new BufferedReader(new FileReader("./USERDATA/Used Word Tracklist.txt"));
			usedWords.read(usedWordList, null);
		}
		catch (FileNotFoundException e){} //Deliberately left unhandled as part of the anti-cheating feature implemented in wordReuseCheck method
	}
	/** createHighScoreFile method
	 * This procedural method is created as a precaution to ensure proper program functionality in case the high score 
	 * file is not found. For example, if the file is corrupted or deleted. 
	 *  
	 * This method addresses the aforementioned problem(s) by overwriting/creating a fresh, new copy of <High Score.txt> 
	 * This method is also used as an exception handler for the FileNotFoundException
	 * 
	 *
	 * @param none 
	 * @return void
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let highScoreFile represent the object used to write the High Score file (type BufferedWriter)
	 * ==================================================================================================================
	 * 
	 */
	private void createHighScoreFile () throws IOException{
		BufferedWriter highScoreFile = new BufferedWriter(new FileWriter("./USERDATA/High Score.txt"));
		highScoreFile.write("0");
		highScoreFile.newLine();
		highScoreFile.write("0");
		highScoreFile.newLine();
		highScoreFile.close();
	}
	/** highScoreCheck method
	 * This functional method checks whether the user's current score is higher than the record high score.	 
	 * Note that if dictionary is on, a different high score record is compared.
	 *
	 * @param none 
	 * @return whether the current score is a new high score (type Boolean) 
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let highScore represent the high score from the record file (type long)
	 * 					   - let line represent an entry of the high score file (type String)
	 * 					   - let highScoreFile represent the object used to read the High Score file (type BufferedReader)
	 * ==================================================================================================================
	 * 
	 */
	private boolean highScoreCheck () throws IOException{
		long highScore = 0;
		String line = "0";

		while(true){
			try{
				BufferedReader highScoreFile = new BufferedReader(new FileReader("./USERDATA/High Score.txt"));
				if (dictionary == true){
					line = highScoreFile.readLine();
					highScore = Long.parseLong(line);
				}
				else if (dictionary == false){
					for (int i = 0; i <= 1; i++)
						line = highScoreFile.readLine();
					highScore = Long.parseLong(line);
				}

				if (score > highScore)
					return true;
				else
					return false;
			}
			catch (FileNotFoundException e){
				createHighScoreFile();
			}
			catch (NumberFormatException e){
				createHighScoreFile();
			}
		}
	}
	/** writeNewHighScore method
	 * This procedural method writes the new high score to the record file.	 
	 * Note that if dictionary is on, a different high score record is overwritten.
	 *
	 * @param none 
	 * @return void 
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let newHighScore represent the new high score (type String);
	 * 					   - let highScore represents an array of record highScore (one-dimensional array of String)
	 * 					   - let highScoreFileIn represent the object used to read the High Score file (type BufferedReader)
	 * 					   - let highScoreFileOut represent the object used to write the High Score file (type BufferedWriter)
	 * ==================================================================================================================
	 * 
	 */
	private void writeNewHighScore () throws IOException{
		String newHighScore = Long.toString(score);
		String[] highScore = new String[2];

		while(true){
			try{
				BufferedReader highScoreFileIn = new BufferedReader(new FileReader("./USERDATA/High Score.txt"));
				highScore[0] = highScoreFileIn.readLine();
				highScore[1] = highScoreFileIn.readLine();

				BufferedWriter highScoreFileOut = new BufferedWriter(new FileWriter("./USERDATA/High Score.txt"));
				if (dictionary == true){
					highScoreFileOut.write(newHighScore);
					highScoreFileOut.newLine();
					highScoreFileOut.write(highScore[1]);
					highScoreFileOut.newLine();
					highScoreFileOut.close();
				}
				else if (dictionary == false){
					highScoreFileOut.write(highScore[0]);
					highScoreFileOut.newLine();
					highScoreFileOut.write(newHighScore);
					highScoreFileOut.newLine();
					highScoreFileOut.close();
				}
				break;
			}
			catch (FileNotFoundException e){
				createHighScoreFile();
			}
		}
	}
	/** updateHighScore method
	 * This procedural method update the highScoreLabel JLabel with the new high score.	 
	 *
	 * @param none 
	 * @return void 
	 * @throws IOException
	 * ==================================================================================================================
	 * List of Identifiers - let input represent an entry from the high score file (type String)
	 * 					   - let highScoreFile represent the object used to read the High Score file (type BufferedReader)
	 * ==================================================================================================================
	 * 
	 */
	private void updateHighScore () throws IOException{
		String input = "0";
		long highScore = 0;

		while(true){
			try{
				BufferedReader highScoreFile = new BufferedReader(new FileReader("./USERDATA/High Score.txt"));

				if (dictionary == true){
					input = highScoreFile.readLine();
					highScore = Long.parseLong(input);
				}
				else if (dictionary == false){
					for (int i = 0; i <= 1; i++)
						input = highScoreFile.readLine();
					highScore = Long.parseLong(input);
				}
				break;
			}
			catch (FileNotFoundException e){
				createHighScoreFile();
			}
			catch (NumberFormatException e){
				createHighScoreFile();
			}
		}
		highScoreLabel.setText(" High Score: "+highScore+" ");

	}
	/** newHighScorePrompt method
	 * This procedural method prompts the user for new high score	 
	 *
	 * @param none 
	 * @return void 
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show the prompt (type JFrame)
	 * ==================================================================================================================
	 * 
	 */
	private void newHighScorePrompt () {
		JFrame dialog = new JFrame();
		JOptionPane.showMessageDialog(dialog, "<html><Font size=5><b>CONGRATULATION!! New Boggle X High Score!</b></Font></html>", "Boggle X", JOptionPane.WARNING_MESSAGE);
	}
	/** reset method
	 * This procedural method resets the JTextArea word.	 
	 *
	 * @param none 
	 * @return void 
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 * 
	 */
	private void reset(){
		word.setText("");
		word.requestFocusInWindow();
	}
	/** goodBye method
	 * This procedural method outputs a user-friendly message that informs the user that the game will quit
	 *
	 * @param none 
	 * @return void 
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - let dialog represent the window used to show the prompt (type JFrame)
	 * ==================================================================================================================
	 */
	private void goodBye(){
		JFrame dialog = new JFrame();
		JOptionPane.showMessageDialog(dialog, "<html><b>Your Final Score is "+score+"!</b></html>", "Boggle X", JOptionPane.INFORMATION_MESSAGE);
		if (newHighScore == true)
			newHighScorePrompt();
		JOptionPane.showMessageDialog(dialog, "<html><b>Thank You for Playing Boggle X</b></html>"+"\nCopyright \u00a9 2013", "Boggle X", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
}


/*=====================================================================================================================================
Board class - The Board class is used to separate the implementation of the Boggle Board from its use in the BoggleV10 class
=======================================================================================================================================
List of Identifiers - Instance arrays are listed in the following:
	 					- let BOARD represent the Boggle Board (two dimensional array of JLabel)				  
======================================================================================================================================*/
class Board implements ActionListener{
	private final JButton[][] BOARD;
	private final BOARDSIZE;

	/** Board method
	 * This default constructor initializes the state of BOARD with a size of 5.
	 * Note: The default constructor will never be called upon but is implemented as a precaution
	 * 
	 * @param none
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	Board(){		
		BOARD = new JButton[5][5];
		for (int i = 0; i < BOARD.length; i++) {
			for (int j = 0; j < BOARD[i].length; j++) {
				BOARD[i][j] = new JButton(genLetter());
				BOARD[i][j].addActionListener(this);
				BOARD[i][j].setActionCommand(i+" "+j); //Sets a unique "ID" for each JButton on the grid using their indices as "coordinates"
			}
		}
	}
	
	/** Board method
	 * This constructor initializes the state of board based on the user's preference of the board size.
	 *
	 * @param the user's preference of the board size (type int)
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	Board(int BOARDSIDE){
		BOARD = new JButton[BOARDSIDE][BOARDSIDE];
		for (int i = 0; i < BOARD.length; i++) {
			for (int j = 0; j < BOARD[i].length; j++) {
				BOARD[i][j] = new JButton(genLetter());
				BOARD[i][j].addActionListener(this);
				BOARD[i][j].setActionCommand(i+" "+j); //Sets a unique "ID" for each JButton on the grid using their indices as "coordinates"
			}
		}
	}

	
	/** genLetter method
	 * This functional method randomly generates a list of numbers which are used to randomly set the letter for the
	 * boggle board.
	 *
	 * @param none
	 * @return void
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	private String genLetter (){
		return Character.toString((char)(Math.round(Math.random()*25)+65));
	}
	/** genLetter method
	 * This accessor allows access to the instance variable <board> from the use class.
	 *
	 * @param none
	 * @return the boggle board (two dimensional array of JLabel) 
	 * 
	 * ==================================================================================================================
	 * List of Identifiers - none
	 * ==================================================================================================================
	 */
	public JButton[][] getBoard (){
		return BOARD;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String[] token = event.getActionCommand().split(" ");
		int row = Integer.parseInt(token[0]);
		int col = Integer.parseInt(token[1]);
		
		BoggleX.word.setText(BoggleX.word.getText() + BOARD[row][col].getText());
		BOARD[row][col].setEnabled(false);
	}
}
