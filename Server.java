import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Server {
    private ServerSocket serverSocket = null;
    private static Socket socket = null;
    private static ObjectInputStream inStream = null;
    private static PublicKey public_key;
    private ObjectInputStream inputStream = null;
    private static ObjectOutputStream outputStream = null;

    public Server() {

    }

    public static void recieveKeys() throws Exception {
        try {
            inStream = new ObjectInputStream(socket.getInputStream());
            Frame frame = (Frame) inStream.readObject();
            byte[] pubKey = frame.data;
            System.out.println("Frame: " + pubKey);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(pubKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            public_key = kf.generatePublic(ks);
            System.out.println("Object received = " + public_key);
            System.out.println("Secure Shell activated");
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error obtaining server public key 2.");
            System.exit(0);
        } catch (InvalidKeySpecException e) {
            System.out.println("Error obtaining server public key 3.");
            System.exit(0);
        }
    }

    public void communicate() throws Exception {
        try {
            serverSocket = new ServerSocket(4445);
            socket = serverSocket.accept();
            System.out.println("Connected");
        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(Process p) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        // reader.readLine();
        String line;
        String normal = "";
        String encrypted = "";
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        while ((line = reader.readLine()) != null) {
            // System.out.println(">> Normal Text: " + line);
            normal = normal + "\n" + line;
            byte[] data_encrypt = encryption.encrypt(line.getBytes(), public_key);
            // System.out.println(">> Encrypted Text:" + data_encrypt);
            encrypted = encrypted + "\n" + new String(data_encrypt);
            Frame frame1 = new Frame();
            frame1.data = data_encrypt;
            outputStream.writeObject(frame1);
            // System.out.println(">> Frame" + frame1.data);
            outputStream.flush();
        }
        if (line == null) {
            String send = "Execution complete";
            byte[] data_encrypt = encryption.encrypt(send.getBytes(), public_key);
            // System.out.println(">> Encrypted Text:" + data_encrypt);
            normal = normal + "\n" + send;
            encrypted = encrypted + "\n" + new String(data_encrypt);
            Frame frame1 = new Frame();
            frame1.data = data_encrypt;
            outputStream.writeObject(frame1);
            outputStream.flush();
        }
        System.out.println(">> Normal Data:\n" + normal);
        System.out.println("\n>> Encrypted Data:\n" + encrypted);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.communicate();
        boolean auth=isloggedin();
        if(Boolean.compare(auth,true)==0){
        recieveKeys();
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str=br.readLine();
        while (!str.equalsIgnoreCase("exit")) {
            System.out.println(str);
            Process p = Runtime.getRuntime().exec(str);
            send(p);
            p.destroy();
            System.out.print(">>");
            str=br.readLine();
        }
    socket.close();
    }
    else{
        socket.close();
        System.out.println("Authentication Failed");
    }
}
public static boolean isloggedin() throws IOException{
    boolean is_logged=false;	
    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String username=br.readLine();
    
    String pwd= br.readLine();
    System.out.println(username+" "+pwd);
    
    OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
    PrintWriter out = new PrintWriter(osw);
    
    if(username.equals("admin") && pwd.equals("admin")){
        out.println("USER AUTHENTICATED");
        out.flush();
        is_logged=true;
    }
    else{
        out.println("USER NOT AUTHENTICATED");
        out.flush();
    } 
    return is_logged;
}
}