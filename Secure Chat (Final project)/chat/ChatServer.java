import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;


public class ChatServer {
  public static final int portNum = Config.getAsInt("ServerPortNum");

  private Set activeSenders = Collections.synchronizedSet(new HashSet());

  public ChatServer(byte[] myPrivateKey) {
    // This constructor never returns.
    try{
      SecureServerSocket ss;
      System.out.println("I am listening on "+Integer.toString(portNum));
      ss = new SecureServerSocket(portNum, myPrivateKey);
      for(;;){
  // wait for a new client to connect, then hook it up properly
	SecureSocket sock = ss.accept();
	CodeInputStream  in  = sock.getInputStream();
	CodeOutputStream out = sock.getOutputStream();
  String username = getAuth(in);
  if(username!=null)
  {
  System.err.println("Got connection from " + username);
  SenderThread st = new SenderThread(out);
  new ReceiverThread(in, st, username);
  }
  else 
  {System.err.println("Trying to login as another user");}
  }
  }catch(IOException x){
      System.err.println("Dying: IOException");
    }
  }

  public static void main(String[] argv) throws FileNotFoundException {
      File f = new File("private.txt");
      Scanner sc = new Scanner(f);
      String k = "";
      while (sc.hasNext())
          k = sc.next();
      BigInteger pr = new BigInteger(k);
      new ChatServer(pr.toByteArray());
  }

  private String getAuth(CodeInputStream in) throws IOException {
    try{
      ObjectInputStream ois = new ObjectInputStream(in);
      Object o = ois.readObject();
      AuthenticationInfo auth = (AuthenticationInfo)o;
      return auth.getUserName();   // will return null if authentication fails
    }catch(ClassNotFoundException x){
      x.printStackTrace();
      return null;
    }
  }

  class SenderThread extends Thread {
    // forwards messages to a client
    // messages are queued
    // we take them from the queue and send them along

    private CodeOutputStream out;
    private Queue        queue;

    SenderThread(CodeOutputStream outStream) {
      out = outStream;
      queue = new Queue();
      activeSenders.add(this);
      start();
    }

    public void queueForSending(byte[] message){
      // queue a message, to be sent as soon as possible

      queue.put(message);
    }

    public void run() {
      // suck messages out of the queue and send them out
      try{
          out.start = true;
	for(;;){
	  Object o = queue.get();
	  byte[] barr = (byte[])o;
	  out.write(barr);
	  out.flush();
	}
      }catch(IOException x){
	// unexpected exception -- stop relaying messages
	x.printStackTrace();
	try{
	  out.close();
	}catch(IOException x2){}
      }
      activeSenders.remove(this);
    }
  }

  class ReceiverThread extends Thread {
    // receives messages from a client, and forwards them to everybody else

    private CodeInputStream  in;
    private SenderThread me;
    private byte[]       userNameBytes;

    ReceiverThread(CodeInputStream inStream, SenderThread mySenderThread,
		   String name) {
        System.out.println("amir");
      in = inStream;
      me = mySenderThread;
      String augmentedName = "[" + name + "] ";
      userNameBytes = augmentedName.getBytes();
      start();
    }

    public void run() {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      for(;;){
	// read in a message, terminated by carriage-return
	// buffer the message in baos, until we see EOF or carriage-return
	// then send it out to all the other clients
	try{
	  baos.write(userNameBytes);
	  int c;
	  in.start = true;
	  do{
	    c = in.read();
	    if(c == -1){
	      // got EOF -- send what we have, then quit
	      sendToOthers(baos);
	      return;
	    }
	    baos.write(c);
	  }while(c != '\n');
	  sendToOthers(baos);
	}catch(IOException x){
	  // send what we have, then quit
	  sendToOthers(baos);
	  return;
	}
      }
    }

    private final SenderThread[] stArr = new SenderThread[1];

    private void sendToOthers(ByteArrayOutputStream baos) {
      // extract the contents of baos, and queue them for sending to all
      // other clients; 
      // also, reset baos so it is empty and can be reused

      byte[] message = baos.toByteArray();
      baos.reset();

      SenderThread[] guys = (SenderThread[])(activeSenders.toArray(stArr));
      for(int i=0; i<guys.length; ++i){
	SenderThread st = guys[i];
	if(st != me)	st.queueForSending(message);
      }
    }
  }
}

