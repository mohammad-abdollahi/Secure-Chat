// This file implements a secure (encrypted) version of the Socket class.
// (Actually, it is insecure as written, and students will fix the insecurities
// as part of their homework.)
//
// This class is meant to work in tandem with the SecureServerSocket class.
// The idea is that if you have a program that uses java.net.Socket and
// java.net.ServerSocket, you can make that program secure by replacing 
// java.net.Socket by this class, and java.net.ServerSocket by 
// SecureServerSocket.
//
// Like the ordinary Socket interface, this one differentiates between the
// client and server sides of a connection.  A server waits for connections
// on a SecureServerSocket, and a client uses this class to connect to a 
// server.
// 
// A client makes a connection like this:
//        String          serverHostname = ...
//        int             serverPort = ...
//        byte[]          myPrivateKey = ...
//        byte[]          serverPublicKey = ...
//        SecureSocket sock;
//        sock = new SecureSocket(serverHostname, serverPort,
//                                   myPrivateKey, serverPublicKey);
// 
// The keys are in a key-exchange protocol (which students will write), to
// establish a shared secret key that both the client and server know.
//
// Having created a SecureSocket, a program can get an associated
// InputStream (for receiving data that arrives on the socket) and an
// associated OutputStream (for sending data on the socket):
//
//         InputStream inStream = sock.getInputStream();
//         OutputStream outStream = sock.getOutputStream();


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;
import java.math.BigInteger;



public class SecureSocket {
  private Socket sock;
  private CodeInputStream  in;
  private CodeOutputStream out;

  public SecureSocket(String hostname, int port,
                      byte[] clientPrivateKey, byte[] serverPublicKey)
                         throws IOException, UnknownHostException {
    // this constructor is called by a client who wants to make a secure
    // socket connection to a server

    sock = new Socket(hostname, port);

    byte[] symmetricKey = keyExchange(clientPrivateKey, serverPublicKey, true);

    setupStreams(sock, symmetricKey, false);
  }

  public SecureSocket(Socket s, byte[] myPrivateKey) throws IOException {
    // don't call this yourself
    // this is meant to be called by SecureServerSocket

    sock = s;

    byte[] symmetricKey = keyExchange(myPrivateKey, null, false);

    setupStreams(sock, symmetricKey, true);
  }

  private byte[] keyExchange(byte[] myPrivateKey, 
			     byte[] hisPublicKey,  // null if I am server
			     boolean iAmClient ) throws IOException {
    // Assignment 4: replace this with a secure key-exchange algorithm

    // This is hopelessly insecure; it's just here as a placeholder.
    InputStream instream = sock.getInputStream();
    OutputStream outstream = sock.getOutputStream();
    HashFunction hash = new HashFunction();
    if(iAmClient){
        BigInteger k = new BigInteger(256,100,new Random());
        BigInteger coded = k.modPow(new BigInteger(hisPublicKey),new BigInteger("9766363518701585168611805246185350884589140222463339388220561164472814529567892214082740672745170203767544240696028690135689572310182877915376690456093977"));
        byte[] outbytes = coded.toByteArray();
        outstream.write(outbytes, 0, outbytes.length);
        outstream.flush();
        hash.update(k.toByteArray());
        return hash.digest();
    }
    
    byte[] inb = new byte[64];
    int num = instream.read(inb, 0, inb.length);
    if(num != inb.length)    throw new RuntimeException();
    BigInteger coded = new BigInteger(inb);
    BigInteger decoded = coded.modPow(new BigInteger(myPrivateKey),new BigInteger("9766363518701585168611805246185350884589140222463339388220561164472814529567892214082740672745170203767544240696028690135689572310182877915376690456093977"));
    hash.update(decoded.toByteArray());
    return hash.digest();
  }

  private void setupStreams(Socket ssock, 
			    byte[] symmetricKey, boolean iAmClient ) 
                                   throws IOException {
    // Assignment 2: replace this with something that creates streams that
    //               use crypto in a way that makes them secure

    // This is hopelessly insecure; streams are totally unprotected from
    // eavesdropping or tampering
    in = new CodeInputStream(ssock.getInputStream(),symmetricKey);
    out = new CodeOutputStream(ssock.getOutputStream(),symmetricKey);
  }

  public CodeInputStream getInputStream() throws IOException {
    return in;
  }

  public CodeOutputStream getOutputStream() throws IOException {
    return out;
  }

  public void close() throws IOException {
    in.close();
    out.close();
    sock.close();
  }
}
