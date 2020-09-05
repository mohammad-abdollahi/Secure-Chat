import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigInteger;


public class ChatClient {
  public ChatClient(String username, String password,
		    String serverHost, int serverPort,
		    byte[] clientPrivateKey, byte[] serverPublicKey)
                                                         throws IOException {

    SecureSocket sock = new SecureSocket(serverHost, serverPort,
            clientPrivateKey, serverPublicKey);

    CodeOutputStream out = sock.getOutputStream();
    sendAuth(username, password, out);

    new ReceiverThread(sock.getInputStream());
    out.start = true;
    for(;;){
      int c = System.in.read();
      if(c == -1)    break;
      out.write(c);
      if(c == '\n')    out.flush();
    }
    out.close();
  }

  public static void main(String[] argv){
    String username = argv[0];
    String password = argv[1];
    String hostname = (argv.length<=2) ? "localhost" : argv[2];
    try{
      BigInteger pub = new BigInteger("65537");
      new ChatClient(username, password, hostname, ChatServer.portNum, null, pub.toByteArray());
    }catch(IOException x){
      x.printStackTrace();
    }
  }

  private void sendAuth(String username, String password, CodeOutputStream out) throws IOException {
    // create an AuthInfo object to authenticate the local user, 
    // and send the AuthInfo to the server

    AuthenticationInfo auth = new AuthenticationInfo(username, password);
    if(auth.isValid()==false)
    {
      System.out.println("Wrong password");
      throw new IOException();
    }
    ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(auth);
    oos.flush();
  }

  class ReceiverThread extends Thread {
    // gather incoming messages, and display them

    private CodeInputStream in;

    ReceiverThread(CodeInputStream inStream) {
      in = inStream;
      start();
    }

    public void run() {
      try{
	ByteArrayOutputStream baos;  // queues up stuff until carriage-return
	baos = new ByteArrayOutputStream();
	in.start = true;
	for(;;){
	  int c = in.read();
	  if(c == -1){
	    spew(baos);
	    break;
	  }
	  baos.write(c);
	  if(c == '\n')    spew(baos);
	}
      }catch(IOException x){ }
    }

    private void spew(ByteArrayOutputStream baos) throws IOException {
      byte[] message = baos.toByteArray();
      baos.reset();
      System.out.write(message);
    }
  }
}
