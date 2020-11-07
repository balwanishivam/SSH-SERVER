import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;
import javax.crypto.Cipher; 
// import java.xml.bind.DatatypeConverter;

public class encryption { 
	public static final String RSA 
		= "RSA";
	private static final Key Key = null; 
	public static Scanner sc; 

	  /*Generating public & private keys 
	  using RSA algorithm. */
	public static KeyPair generateRSAKkeyPair() throws Exception 
	{ 
		SecureRandom secureRandom 
			= new SecureRandom(); 
		KeyPairGenerator keyPairGenerator 
			= KeyPairGenerator.getInstance(RSA); 

		keyPairGenerator.initialize( 
			2048, secureRandom); 
		return keyPairGenerator 
			.generateKeyPair(); 
	} 

	  /*Encryption function which converts 
	  the plainText into a cipherText 
	  using private Key. */
	  public static byte[] encrypt(byte[] data, PublicKey key) {
		try {
		  Cipher cipher = Cipher.getInstance("RSA");
		  cipher.init(Cipher.ENCRYPT_MODE, key);
		  return cipher.doFinal(data);
		}
		catch (Exception e) {
		  System.out.println("encrypt exception: " + e.getMessage());
		  return new byte[0];
		}
	  }
	   
	  public static byte[] decrypt(byte[] data, PrivateKey key) {
		try {
		  Cipher cipher = Cipher.getInstance("RSA");
		  cipher.init(Cipher.DECRYPT_MODE, key);
		  return cipher.doFinal(data);
		}
		catch (Exception e) {
		System.out.println("decrypt exception: " + e.getMessage());
		return new byte[0];
		}
	  }

	//  Driver code; 
	//  public static void main(String args[]) 
	//   	throws Exception 
	//   { 
	//   	KeyPair keypair 
	//   		= generateRSAKkeyPair(); 

	//   	String plainText = "This is the PlainText "
	//   					+ "I want to Encrypt using RSA."; 

	//   	byte[] cipherText 
	//   		= do_RSAEncryption( 
	//   			plainText, 
	//   			keypair.getPrivate()); 

	//   	System.out.println( 
	//   		"The Public Key is: "
	//   		+ DatatypeConverter.printHexBinary( 
	//   			keypair.getPublic().getEncoded())); 

	//   	System.out.println( 
	//   		"The Private Key is: "
	//   		+ DatatypeConverter.printHexBinary( 
	//   			keypair.getPrivate().getEncoded())); 

	//   	System.out.print("The Encrypted Text is: "); 

	//   	System.out.println( 
	//   		DatatypeConverter.printHexBinary( 
	//   			cipherText)); 

	//   	String decryptedText 
	//   		= do_RSADecryption( 
	//   			cipherText, 
	//   			keypair.getPublic()); 

	//   	System.out.println( 
	//   		"The decrypted text is: "
	//   		+ decryptedText); 
	//   } 
}
