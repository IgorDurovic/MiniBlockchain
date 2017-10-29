import java.io.*;
import java.sql.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main {

    public static String name, idhash;
    public static MulticastSocket s;
    public static InetAddress group;

    public static void main(String[] args) {
    	init();
    }
    
    public static void init() {
    	try {
    		File userInfo = new File("user.info");
    		
    		if(!userInfo.exists()) {
    			userInfo.createNewFile();
    			createUser();
    		}
    		
    		setupDB();
    		
    		group = InetAddress.getByName("228.5.6.7");
            s = new MulticastSocket(60010);
            s.joinGroup(group);
            DatagramPacket localhash = new DatagramPacket(("3 " + idhash).getBytes(), idhash.length() + 2, group, 60010);
            s.send(localhash);
    		
    		//messaging communication
            receiver();
            transmitter();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void createUser() throws NoSuchAlgorithmException, IOException {
    	Scanner scn = new Scanner(System.in);
        System.out.println("Choose a username: ");
        name = scn.next();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(name.getBytes(), 0, name.length());
        byte[] result = md.digest();
        
        idhash = bytesToHex(result);
        System.out.println("Your hash address: " + idhash);
    }

    public static void receiver(){
        Thread receive = new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        DatagramPacket msg = new DatagramPacket(new byte[1000], 1000);
                        s.receive(msg);
                        
                        String command = bytesToHex(msg.getData());

                        /*switch(command.charAt(0)){
                            case '1':
                                //transaction command
                                String tx = command.substring(2);
                                
                                if(!verifyTransaction(tx) break;
                                txqueue.add(tx)

                                if(txqueue.size() == 8){
                                    processBlock();
                                }
                                break;
                            case '2':
                                //block verification command (confirm transaction validity and proof of work)
                                confirm tx stored in block.merkeltree[7 - 14]
                                confirm hash in block.merkeltree
                                check proof of work
                                break;
                            case '3':
                                //new peer command: store hash address of new peer
                                String peerAddress = command.substring(2);
                                check if address exists
                                if not then store peerAddress
                                break;
                            default:
                                System.out.println("wrong command number");
                                break;
                        }*/
                    }
                    catch(IOException e){
                        System.out.println("Error");
                    }
                }
            }
        };

        receive.start();
    }

    public static void transmitter(){
        Thread transmit = new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        Scanner scn = new Scanner(System.in);
                        String msg = "msg";
                        DatagramPacket send = new DatagramPacket(msg.getBytes(), msg.length(), group, 60010);
                        s.send(send);
                    }
                    catch(IOException e){
                        System.out.println("Error");
                    }
                }
            }
        };

        transmit.start();
    }
    
    public static void setupDB() {
    	try(
    			Connection conn = DriverManager.getConnection(
    					"jdbc:mysql://localhost/", "myuser", "xxxx");
    			
    			Statement stmt = conn.createStatement();
    			){
    		
    		String query = "create database if not exists blockchain;";
    		stmt.executeQuery(query);	
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static String bytesToHex(byte[] bytes) {
    	char[] hexArray = "0123456789ABCDEF".toCharArray();
    	char[] hexChars = new char[bytes.length * 2];
        
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
