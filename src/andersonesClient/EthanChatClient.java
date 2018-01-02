/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package andersonesClient;

//import static andersonesClient.FXMLDocumentController.checkPassword;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Ethan
 */
public class EthanChatClient extends Application
{
    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;
   
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
       
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        // lambda expression way to end program on window close
        stage.setOnCloseRequest(e-> System.exit(0));
        
        String chatName = UUID.randomUUID().toString();
        // creates a unique identifier for the server to track the user. Seperate from final username so that users with same username wont be confused.
	String password = UUID.randomUUID().toString();
	String serverAddress = "localhost";
        
	if (chatName.contains(" ") || password.contains(" ") || serverAddress.contains(" "))
	   {
	   System.out.println("Password, chatName or ServerAddress is invalid");	
       return;
	   }
	System.out.println("Connecting to the chat room server at " + serverAddress);
        String newLine = System.lineSeparator();
	int serverPort = 3333;
    
	
	
	Socket s = null;
	try {
	    s = new Socket(serverAddress, serverPort);
		System.out.println("Connected to the computer at " + serverAddress 
				         + " on port "                     + serverPort);
        }
	catch (Exception e)
        {
		System.out.println("The Chat Room Server is not responding. Make sure the Server is accepting Connections on port 3333");
        return;
        } 
        
        
	
	
	
	
	
	String     serverReply = null;
	try {
        System.out.println("Sending join request for " + chatName);
		oos = new ObjectOutputStream(s.getOutputStream());
                
               
	    oos.writeObject(chatName + " " + password); // 1st message
        ois = new ObjectInputStream (s.getInputStream());
        System.out.println("Waiting for server reply to join request.");
	    serverReply = (String) ois.readObject();
          
	    }
    catch(Exception e) // problem sending to/receiving from the server
        {
    	System.out.println("Problem with connection. Restart EthanChatClient");
        return; // terminate.
        }
	
    
    System.out.println(serverReply);
	
	if (!serverReply.startsWith("Welcome")) return; // stop if error msg. 

	
	System.out.println("Loading the GUI program.");
        // Run GUI in seperate thread
          Thread thread = new Thread(){
            public void run(){
                FXMLDocumentController fx = new FXMLDocumentController();
                
                System.out.println("The main thread is entering the receive ois loop in EthanChatClient.");
                 
             
	try { //A receive error exits the loop and terminates the client.	
	    while(true)
	    	 {     
                     Object o = ois.readObject();
                     
                     if (o instanceof String){
		     String messageFromServer = (String)o;
                     
                    
		     System.out.println("Received from server: " + messageFromServer);
                     if (messageFromServer.contains("SERVER MESSAGE:")){
                       
                        fx.checkPassword= 1;
                       
                        try{
                          //  fx.changeView();
                            
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                            
                        }
                     }
                     
		     fx.showIncomingMessage(messageFromServer);
                   
		     }
                     else if (o instanceof byte[])
                     {
                         byte[] messageFromServer = (byte[])o;
                         try{
                          fx.showIncomingMessage(messageFromServer);
                          fx.showIncomingMessage("A file was sent a file to everyone");
                         }
                         catch(Exception e){
                             System.out.println(e.getMessage());
                         }
                         
                     }
                   
                 }
	    }
	catch(IOException e)// we're done if connection fails!
	     {             //(so catch is OUTSIDE the loop)
	     String errMsg = "ERROR: Receive connection to the Server has failed. " + e;
		 fx.showIncomingMessage(errMsg);
		 System.out.println(errMsg);
		 
	     }
        catch(ClassNotFoundException e){
              String errMsg = "ERROR: Receive connection to the Server has failed. " + e;
		 fx.showIncomingMessage(errMsg);
		 System.out.println(errMsg);    
             
        }
             
                
            }
              
    
            
           };

          thread.start();
        
    }

    /**
     * @param args the command line arguments
     */
   
    
    public static void main (String args[])	{
             
           // FXMLDocumentController fx = new FXMLDocumentController();

	// Currently Random username and Password will be updated later
	
          launch();
	}
    
}
