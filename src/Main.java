
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main {

    public static String name, idhash;
    public static MulticastSocket s;
    public static InetAddress group;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Scanner scn = new Scanner(System.in);
        System.out.println("Choose a username: ");
        name = scn.next();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(name.getBytes(), 0, name.length());
        byte[] result = md.digest();

        StringBuilder sb = new StringBuilder("");
        for(byte b: result){
            sb.append(String.format("%02x", b & 0xff));
        }

        idhash = sb.toString();
        System.out.println("Your hash address: " + idhash);

        group = InetAddress.getByName("228.5.6.7");
        s = new MulticastSocket(60010);
        s.joinGroup(group);
        DatagramPacket localhash = new DatagramPacket(("3 " + idhash).getBytes(), idhash.length() + 2, group, 60010);
        s.send(localhash);

        //messaging communication
        receiver();
        transmitter();
    }

    public static void receiver(){
        Thread receive = new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        byte[] buffer = new byte[1000];
                        DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
                        s.receive(msg);

                        StringBuilder sb = new StringBuilder("");
                        for(byte b: msg.getData()){
                            sb.append(String.format("%02x", b & 0xff));
                        }
                        String command = sb.toString();

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
}
