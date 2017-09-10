import java.security.*;
import java.util.*;

public class Transaction{

	public int id;
	public int input;
	public int amount;

	public String senderAddress;

	transaction(int in, int a, String sa){
		input = in;
		id = in + 2;
		amount = a;
		senderAddress = sa;
	}

	public boolean verifyOwnership(String name){
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(name.getBytes(), 0, name.length());
        byte[] result = md.digest();

        StringBuilder sb = new StringBuilder("");
        for(byte b: result){
            sb.append(String.format("%02x", b & 0xff));
        }

        return senderAddress.equals(sb.toString());
	}

	public boolean verifyInput(){
		return txs.get(input - 1).amount = this.amount;
	}
}
