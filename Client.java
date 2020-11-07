import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.security.KeyPair; 
import java.security.KeyPairGenerator; 
import java.security.PrivateKey; 
import java.security.PublicKey; 
import java.security.SecureRandom; 
import java.util.Scanner; 
import javax.crypto.Cipher;

public class Client {
    private static Socket socket = null;
    private static ObjectInputStream inputStream = null;
    private static ObjectOutputStream outputStream = null;
    private KeyPair keypair;
    private static PrivateKey private_key;
    private static PublicKey public_key;
    private boolean isConnected = false;

    public void createKeys() throws Exception {
        keypair = encryption.generateRSAKkeyPair();
        private_key = keypair.getPrivate();
        public_key = keypair.getPublic();
    }

    public Client() throws Exception {
        createKeys();
    }

    public void communicate() throws Exception {
        while (!isConnected) {
            try {
                socket = new Socket("localHost", 4445);
                System.out.println("Connected");
                isConnected = true;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendKeys() throws Exception {
        Frame frame = new Frame();
        frame.data = public_key.getEncoded();
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Public Key = " + public_key);
        System.out.println("Object to be written = " + frame);
        outputStream.writeObject(frame);
        System.out.println("Client Side");
        System.out.println("Secure Shell activated");
    }

    public static void recieve() throws Exception {
        inputStream = new ObjectInputStream(socket.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Frame frame1 = new Frame();
        frame1 = (Frame) inputStream.readObject();
        byte[] data = frame1.data;
        // System.out.println(">> Frame " + data);
        byte[] result = encryption.decrypt(data, private_key);
        String res = new String(result);
        String final_res = res;
        String dec_res = new String(data);
        while (!res.equals("Execution complete")) {
            // System.out.println(">> Decrypted Text " + res);
            Frame frame2 = new Frame();
            frame2 = (Frame) inputStream.readObject();
            data = frame2.data;
            // System.out.println(">> Frame " + data);
            result = encryption.decrypt(data, private_key);
            res = new String(result);
            dec_res = dec_res + "\n" + new String(data);
            final_res = final_res + "\n" + res;
        }
        System.out.println(">> Encrypted Data:\n" + dec_res);
        System.out.println("\n>> Decrypted Data:\n" + final_res);
        System.out.print(">>");
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.communicate();
        boolean auth=authentication();
        if(Boolean.compare(auth,true)==0){
        sendKeys();
        OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter out = new PrintWriter(osw);
        System.out.print(">>");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        while (!(str.equalsIgnoreCase("exit"))) {
            out.println(str);
            out.flush();
            recieve();
            str = sc.nextLine();
        }
    }
    else{
        System.out.println("Authentication Failed");
    }
}
public static boolean authentication() throws IOException
    {
    	Boolean loggedin= false;
    	Scanner sc= new Scanner(System.in);
    	System.out.println("Enter Authentication Details: ");
    	System.out.print("Username:");
    	String user= sc.nextLine();
    	System.out.print("Password:");
    	String pwd= sc.nextLine();
    	OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter out = new PrintWriter(osw);
        out.println(user);
        out.flush();
        out.println(pwd);
        out.flush();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str=br.readLine();
        System.out.println(str);
        if (str.equals("USER AUTHENTICATED"))
        	loggedin= true;
   
    	return loggedin;
    }
}