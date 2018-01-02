
package andersonesClient;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;

/**
 *
 * @author Ethan
 */
public class FXMLDocumentController implements Initializable 
{
    @FXML private TextField userInputTextField;
    @FXML private Label label;
    @FXML private Label label2;
    @FXML private ListView<String> OutputListView = new ListView<String>();
    @FXML private ListView<String> userNameView = new ListView<String>();
    @FXML private Label userNameLabel;
    @FXML private Label passwordLabel;
    @FXML public  TextField userNameField;
    @FXML public  TextField passwordField;    
    @FXML private Button button;
    @FXML private Button LoginButton;
    @FXML private Button messageButton;
    @FXML private TextField fileNameField;
    @FXML private Button sendFileButton;
    @FXML private Label fileLabel;
  
    
    
    public static final ObservableList names = FXCollections.observableArrayList();
    private static final ObservableList input = FXCollections.observableArrayList();
    public static String newMessage = "";
    public static int loggedIn = 0;
    public static int clientStarted = 0;
    public static int checkPassword = 0;
    public static int imageNumber = 0;
    
   
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException
    {
           if (userNameField.getText().length() > 0 && passwordLabel.getText().length() > 0){
           if (loggedIn==0){
            try {
            EthanChatClient.oos.writeObject("PasswordRequest111"+ " " + userNameField.getText()+ " " + passwordField.getText());
            } 
            catch(IOException ioe)
             {
                String errMsg = "ERROR: Connection to the Server has failed.";

                System.out.println(errMsg);

             } 
             //changeView();
             loggedIn=1;
           }
           
         
           
       }
       else 
       { 
           userNameLabel.setText("Incorrect login, try again \nUsername");
           return;
       }
           
       
            
        
        
       
    
    }
    
    @FXML
    private void handleLoginButton(ActionEvent event) throws IOException, ClassNotFoundException{
       // button.setVisible(false);
        LoginButton.setText("Login");
        if (userNameField.getText().length() > 0 && passwordField.getText().length()>0){
        EthanChatClient.oos.writeObject("LoginUser111"+" "+ userNameField.getText()+" " + passwordField.getText() );
                
      
        }
        
       
        
    }
    @FXML 
    private void handleMessageAction(ActionEvent event) throws IOException{
        if (loggedIn == 1 || checkPassword == 1){
            String chat = userInputTextField.getText().trim();//remove leading/trailing blanks
        System.out.println(chat);
	if (chat.length() == 0) return; // returns if empty
        if(chat.startsWith("sendfile")){
                String filepath = chat.split(" ")[1];
                try{
                    File imgpath = new File(filepath);
                    System.out.println(filepath);
                    System.out.println("File found");
                    byte[] fileContent = Files.readAllBytes(imgpath.toPath());
                    //OutputStream out = new BufferedOutputStream(new FileOutputStream("newimage.png"));
                    //out.write(fileContent);
                    //out.close();
                   // System.out.println("done writiing");
                    EthanChatClient.oos.writeObject(fileContent);
                   //EthanChatClient.oos.reset();
                           // (byte[]) fileContent);
                }
                catch(FileNotFoundException e){
                    System.out.println("ERROR");
                    System.out.println(e.getMessage());
                    
                }
                
           // EthanChatClient.oos.writeObject((byte[]));
            
        }
        else{
	System.out.println("Sending: " + chat); // for debugging 
	userInputTextField.setText(""); // After sent clear TextField

	// send chat to server
        try {
            EthanChatClient.oos.writeObject(chat);
            } 
        catch(IOException ioe)
             {
                String errMsg = "ERROR: Connection to the Server has failed.";

                System.out.println(errMsg);

             } 
        }
        
            
        }
        
    }
    @FXML 
    private void handleFileSendAction(ActionEvent event) throws IOException{
        String filename = fileNameField.getText().trim();
        boolean check = new File(filename).exists();
        if (filename.length() > 0 && check  && (loggedIn== 1 || checkPassword ==1)){
        
        
        Path path = Paths.get(filename);
        byte[] sendfile = Files.readAllBytes(path);
        EthanChatClient.oos.writeObject(sendfile);
        }
        fileNameField.setText("");
        
        
        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
          OutputListView.setItems(input);
        userNameView.setItems(names);
      

    
        clientStarted= 1;
        
        
  
      
    }
 
    
      
    public void showIncomingMessage(String messageFromServer)
    {
        
      
        Platform.runLater(new Runnable(){
            @Override
               public void run(){
                    
                    if (messageFromServer instanceof String){
                        String stringMessage = (String)messageFromServer;
                    input.add(stringMessage);
                    if(stringMessage.contains("SERVER")&& clientStarted == 1){
                        System.out.println("CHANGE VIEW HERE");
                        System.out.println(Platform.isFxApplicationThread());
                        //LoginButton.fire();
                        
                        
        
                        loggedIn = 1;
//                       
                       
                        
                    }
                    if (!names.contains((stringMessage.substring(0,stringMessage.indexOf(" "))))){

                    names.add(stringMessage.substring(0,stringMessage.indexOf(" ")));
                    // Brute force solution will fixed later
                   
                    names.remove("Welcome");
                    names.remove("SERVER");
                    names.remove("A");
        
                    }
       
                }
                
    }
        });
        

    }
    
    public void showIncomingMessage(byte[] messageFromServer) throws FileNotFoundException, IOException{
        
        FileOutputStream fos = new FileOutputStream("image"+Integer.toString(imageNumber)+".png");
        fos.write(messageFromServer);
        fos.close();
        imageNumber++;
    }
     
   

   
}

