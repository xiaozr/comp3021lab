package base;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import base.Folder;
import base.Note;
import base.NoteBook;
import base.TextNote;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


/**
 * 
 * NoteBook GUI with JAVAFX
 * 
 * COMP 3021
 * 
 * 
 * @author valerio
 *
 */
public class NoteBookWindow extends Application {

	/**
	 * TextArea containing the note
	 */
	final TextArea textAreaNote = new TextArea("");
	/**
	 * list view showing the titles of the current folder
	 */
	final ListView<String> titleslistView = new ListView<String>();
	/**
	 * 
	 * Combobox for selecting the folder
	 * 
	 */
	final ComboBox<String> foldersComboBox = new ComboBox<String>();
	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;
	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";
	/**
	 * current search string
	 */
	String currentSearch = "";
	
	String currentNote="";
	
	Stage stage;

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void start(Stage stage) {
		loadNoteBook();
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		VBox vbox = new VBox();
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes
		
		ImageView saveView = null;
		try {
			saveView = new ImageView(new File("D:\\java_projects\\comp3021lab\\src\\base\\save.png").toURL().toString());
			saveView.setFitHeight(18);
			saveView.setFitWidth(18);
			saveView.setPreserveRatio(true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		Button buttonSaveNote = new Button("Save Note");
		buttonSaveNote.setPrefSize(100, 20);
		
		buttonSaveNote.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if (currentFolder.equals("") || currentNote.equals("")) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning");
					alert.setContentText("Please select a folder and a note");
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				} else {
					ArrayList<Folder> folders = noteBook.getFolders();
					Folder folder = null;
					for (Folder f : folders) {
						if (f.getName().equals(currentFolder)) {
							folder = f;
							break;
						}
					}
					Note note = null;
					for (Note n : folder.getNotes()) {
						if (n.getTitle().equals(currentNote)) {
							note = n;
							break;
						}
					}
					if (note instanceof TextNote) {
						((TextNote)note).setTextNoteContent(textAreaNote.getText());
					}
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Succeed");
					alert.setContentText("Your note has been successfully saved");
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				}
			}
		});
		
		ImageView dltView = null;
		try {
			dltView= new ImageView(new File("D:\\java_projects\\comp3021lab\\src\\base\\delete.png").toURL().toString());
			dltView.setFitHeight(18);
			dltView.setFitWidth(18);
			dltView.setPreserveRatio(true);			
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
	
		Button buttonDeleteNote = new Button("Delete Note");
		buttonDeleteNote.setPrefSize(100, 20);
		
		buttonDeleteNote.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if (currentFolder.equals("") || currentNote.equals("")) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning");
					alert.setContentText("Please select a folder and a note");
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				} else {
					if (removeNotes(currentNote)) {
						updateListView(false);
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Succeed!");
						alert.setContentText("Your note has been successfully removed");
						alert.showAndWait().ifPresent(rs -> {
							if (rs == ButtonType.OK) {
								System.out.println("Pressed OK.");
							}
						});
					}
				}
			}
		});
		hbox.getChildren().addAll(saveView, buttonSaveNote, dltView, buttonDeleteNote);
		vbox.getChildren().addAll(hbox, addGridPane());
		
		border.setCenter(vbox);
		
		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();		
	}

	/**
	 * This create the top section
	 * 
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load");
		buttonLoad.setPrefSize(100, 20);
		//buttonLoad.setDisable(true);
		Button buttonSave = new Button("Save");
		buttonSave.setPrefSize(100, 20);
		//buttonSave.setDisable(true);
		Label labelSearch = new Label("Search : ");
		TextField textSearch = new TextField();
		Button buttonSearch = new Button("Search");
		Button buttonRemove = new Button("Clear Search");
		
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				currentSearch = textSearch.getText();
				textAreaNote.setText("");
				updateListView(true);
			}
		});
		
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Please Choose An File Which Contains a NoteBoook Object!");
				FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("Serilaized Object File(*.ser)","*.ser");
				fc.getExtensionFilters().add(ef);
				
				File file = fc.showOpenDialog(stage);
				if(file!=null) {
					loadNoteBook(file);
					foldersComboBox.getItems().clear();
					ArrayList<Folder> folders = noteBook.getFolders();
					for(Folder f: folders) {
					foldersComboBox.getItems().add(f.getName());
					}
					updateListView(false);
				}
			}
		});
		
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose An File To Save:");
				
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);
				
				File file = fileChooser.showOpenDialog(stage);
				if(file!=null) {
					noteBook.save(file.getAbsolutePath());
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Sucessfully saved");
					alert.setContentText("You file has been saved to file " + file.getName());
					alert.showAndWait().ifPresent(rs->{
						if(rs==ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				}
			}
		});
		
		buttonRemove.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				currentSearch = "";
				textSearch.setText("");
				textAreaNote.setText("");
				updateListView(false);
			}
		});                                                                  

		hbox.getChildren().addAll(buttonLoad, buttonSave, labelSearch, textSearch, buttonSearch, buttonRemove);
		

		return hbox;
	}

	/**
	 * this create the section on the left
	 * 
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10                              
		vbox.setSpacing(8); // Gap between nodes
		
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(5)); // Set all sides to 10                              
		hbox.setSpacing(10); // Gap between nodes		
		// TODO: This line is a fake folder list. We should display the folders in noteBook variable! Replace this with your implementation
		for(Folder f : noteBook.getFolders()) {
			foldersComboBox.getItems().addAll(f.getName());
		}
		

		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				currentFolder = t1.toString();
				// this contains the name of the folder selected
				// TODO update listview
				updateListView(false);
			}

		});

		foldersComboBox.setValue("-----");
		
		Button buttonAddFolder = new Button("Add a Folder");
		buttonAddFolder.setPrefSize(100, 20);
		
		buttonAddFolder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				TextInputDialog dialog = new TextInputDialog("Add a Folder");
				dialog.setTitle("Input");
				dialog.setHeaderText("Add a new folder for your notebook:");
				dialog.setContentText("Please enter the name you want to create:");
				Optional<String> result = dialog.showAndWait();
				if(result.isPresent()) {
					if(result.get()!="") {
						ArrayList<Folder> notebook = noteBook.getFolders();
						boolean found = false;
						for(Folder f : notebook) {
							if(f.getName().equals(result.get())){
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("Warning");
								alert.setContentText("You already habe a folder named with " + result.get());
								alert.showAndWait().ifPresent(rs->{
									if(rs==ButtonType.OK) {
										System.out.println("Pressed OK.");
									}
								});
								found = true;
								break;
							}
						}
						if(found!=true) {
							noteBook.addFolder(result.get());
							foldersComboBox.getItems().add(result.get());
						}						
					}
					else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("Please input an valid folder name");
						alert.showAndWait().ifPresent(rs->{
							if(rs==ButtonType.OK) {
								System.out.println("Pressed OK.");
							}
						});
					}
				}	
			}
			
		});
						
		hbox.getChildren().addAll(foldersComboBox, buttonAddFolder);				

		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				// This is the selected title
				// TODO load the content of the selected note in
				// textAreNote
				String content = "";
				for(Folder f : noteBook.getFolders()) {
					if(f.getName()==currentFolder) {
						for(Note n : f.getNotes()) {
							if(n.getTitle()==title) {
								TextNote tn = (TextNote) n;
								content = (tn).content;
								currentNote=title;
							}
						}
					}
				}
				textAreaNote.setText(content);

			}
		});
		
		Button buttonAddNote = new Button("Add a Note");
		buttonAddNote.setPrefSize(100, 20);
		
		buttonAddNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String currentFolderName = foldersComboBox.getValue();
				if (currentFolderName == null || currentFolderName.equals("-----")) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning");
					alert.setContentText("Please choose a folder first!");
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				} else {
					TextInputDialog dialog = new TextInputDialog("Add a Note");
					dialog.setTitle("Input");
					dialog.setHeaderText("Add a new note to current folder");
					dialog.setContentText("Please enter the name of your note:");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						ArrayList<Folder> folders = noteBook.getFolders();
						Folder currentFolder;
						for (Folder f : folders) {
							if (f.getName().equals(currentFolderName)) {
								currentFolder = f;
								break;
							}
						}
						noteBook.createTextNote(currentFolderName, result.get());
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Successful!");
						alert.setContentText("Inset note " + result.get() + " to folder " + currentFolderName + " successfully!");
						alert.showAndWait().ifPresent(rs -> {
							if (rs == ButtonType.OK) {
								System.out.println("Pressed OK.");
							}
						});
						updateListView(false);
					}
				}
			}
		});		
		
		vbox.getChildren().add(new Label("Choose folder: "));
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);
		vbox.getChildren().add(buttonAddNote);

		return vbox;
	}
	
	public boolean removeNotes(String title) {
		ArrayList<Folder> folders = noteBook.getFolders();
		Folder folder = null;
		for (Folder f : folders) {
			if (f.getName().equals(currentFolder)) {
				folder = f;
				break;
			}
		}
		ArrayList<Note> folderList = folder.getNotes();
		for (Note n : folderList) {
			if (n.getTitle().equals(title)) {
				folderList.remove(n);
				return true;
			}
		}
		return false;		
	}

	private void updateListView(boolean flag) {
		ArrayList<String> list = new ArrayList<String>();

		// TODO populate the list object with all the TextNote titles of the
		// currentFolder
		if(!flag) {
			for(Folder f : noteBook.getFolders()) {
				if(f.getName()==currentFolder) {
					for(Note n : f.getNotes()) {
						list.add(n.getTitle());
					}
				}
			}
		}
		else {
			List<Note> notes = noteBook.searchNotes(currentSearch);
			for(Note n : notes) {
				list.add(n.getTitle());
			}
		}
		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		// 0 0 is the position in the grid
		grid.add(textAreaNote, 0, 0);

		return grid;
	}

	@SuppressWarnings("unchecked")
	private void loadNoteBook(File file) {
		noteBook = new NoteBook(file.getAbsolutePath());
	}
	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called 鈥渢he most shocking play in NFL history鈥� and the Washington Redskins dubbed the 鈥淭hrowback Special鈥�: the November 1985 play in which the Redskins鈥� Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Award鈥搘inning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everything鈥攗ntil it wasn鈥檛. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliant鈥攁 part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwether鈥檚 Daddy Was a Number Runner and Dorothy Allison鈥檚 Bastard Out of Carolina, Jacqueline Woodson鈥檚 Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthood鈥攖he promise and peril of growing up鈥攁nd exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;

	}

}
