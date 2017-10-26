import java.security.*;
import java.util.*;

public class Transaction{

	public int id;
	public int input;
	public int amount;

	public String senderAddress;

	Transaction(int in, int a, String sa){
		input = in;
		id = in + 2;
		amount = a;
		senderAddress = sa;
	}

	public boolean verifyOwnership(String name) {
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(name.getBytes(), 0, name.length());
	        byte[] result = md.digest();
	        
	        return senderAddress.equals(Main.bytesToHex(result));
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean verifyInput(){
		return txs.get(input - 1).amount = this.amount;
	}
}
