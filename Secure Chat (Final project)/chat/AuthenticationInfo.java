import java.io.Serializable;
import java.io.*;


public class AuthenticationInfo implements Serializable {
  // The fields of this object are set by the client, and used by the 
  // server to validate the client's identity.  The client constructs this
  // object (by calling the constructor).  The client software (in another
  // source code file) then sends the object across to the server.  Finally,
  // the server verifies the object by calling isValid().

  private String username;
  private String password;

  public AuthenticationInfo(String name, String pass) {
    // This is called by the client to initialize the object.

    username = name;
    password = pass;
  }

  public boolean isValid() {
    // This is called by the server to make sure the user is who he/she 
    // claims to be.

    // Presently, this is totally insecure -- the server just accepts the
    // client's assertion without checking anything.  Homework assignment 1
    // is to make this more secure.

    //making hash for the received password
    HashFunction hf = new HashFunction();
    byte[] bts = password.getBytes();
    hf.update(bts);
    String passHash = new String(hf.digest());
    try {
      File myObj = new File("users.txt");
      BufferedReader br = new BufferedReader(new FileReader(myObj)); 
      String usr; 
      String pass;
      while ((usr = br.readLine()) != null) { 
        pass = br.readLine();    
        if ( usr.equals(username) && pass.equals(passHash) )
          { return true;}
        if( usr.equals(username) ) 
          { return false;}
         } 
         br.close();
      } catch (Exception e) { 
      System.out.println(e); 
      } 
      /*
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) 
      {
        String name = myReader.nextLine();
        String pass = myReader.nextLine();
        System.out.println(pass);
    
        if ( name.equals(username) && pass.equals(passHash) )
          { return true;}
        if( name.equals(username) ) 
          { System.out.println("WRong."); return false;}
        
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    */
    //if new user sent data
    try {
      FileWriter myWriter = new FileWriter("users.txt", true);
      myWriter.write( username + "\n");
      myWriter.write(passHash + "\n");
      myWriter.close();
      System.out.println("User successfully added to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  
    return true;
  }

  public String getUserName() {
    return isValid() ? username : null;
  }
}
