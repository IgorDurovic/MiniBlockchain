import java.security.*;
import java.util.*;

public class Block{

	//block header content
	public int id;
	public String hashprev;
	public String merkelroot;

	//transaction container as merkel tree (heap)
	public String[] merkeltree = new String[15];

	Block(int i, String hp){
		id = i;
		hashprev = hp;

		merkeltree = buildMerkelTree();
		merkelroot = merkeltree[0];
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("");
		sb.append(this.id);
		sb.append("\n" + this.hashprev);
		sb.append("\n" + this.merkelroot + "\n");

		for(int i = 0; i < this.merkeltree; i++){
			sb.append(this.merkeltree[i] + " ");
		}

		return sb.toString();
	}

	public String getHeader(){
		return this.id + " " + this.hashprev + " " + this.merkelroot;
	}

	//returns the hash of the block header (functions as a hash pointer)
	public String blockHash() throws NoSuchAlgorithmException{
		String header = this.getHeader();

		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(header.getBytes(), 0, header.length());
        byte[] result = md.digest();

        StringBuilder sb = new StringBuilder("");
        for(byte b: result){
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
	}

	//modified proof of work algorithm
	//a nonce (arbitrary number) is appended to the beginning of the
	//header then the hash is computed.
	//If the hash contains the specified prefix then the proof
	//of work is complete.
	public void proofOfWork(){
		Thread work = new Thread(){
			@Override
			public void run(){
				int nonce = 0;
				while(true){
					String header = this.getHeader();

					MessageDigest md = MessageDigest.getInstance("SHA-256");
			        md.update((nonce + header).getBytes(), 0, header.length());
			        byte[] result = md.digest();

			        StringBuilder sb = new StringBuilder("");
			        for(byte b: result){
			            sb.append(String.format("%02x", b & 0xff));
			        }

			        if(sb.toString().substring(0, 3).equals("11")){
			        	break;
			        }

			        nonce++;
				}
			}
		};

		work.start();
	}

	//top down recursive construction of merkel tree
	public static String[] buildMerkelTree(Stack<Transaction> txs){
		String[] tree = new String[15];
		buildMerkelRoot(txs, tree, 0);

		return tree;
	}

	public static String buildMerkelRoot(Stack<Transaction> txs, String[] tree, int index) throws NoSuchAlgorithmException{
		if(index > 6){
			return txs.pop().toString();
		}

		String children = buildMerkelRoot(txs, tree, 2 * index) + buildMerkelRoot(txs, tree, 2 * index + 1);

		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(children.getBytes(), 0, children.length());
        byte[] result = md.digest();

        StringBuilder sb = new StringBuilder("");
        for(byte b: result){
            sb.append(String.format("%02x", b & 0xff));
        }

        return tree[index] = sb.toString();
	}
}
