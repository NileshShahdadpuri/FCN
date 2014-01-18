import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class client {

        Socket echoSocket;
        PrintWriter outp;
        BufferedReader in;
        String neuId;
        
        client(String port, String hostName, String neuId){
                this.neuId = neuId;
                int portNumber = Integer.parseInt(port);

                try {
                    echoSocket = new Socket(hostName, portNumber);
                    outp = new PrintWriter(echoSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                }
                catch(Exception e) {
                        e.printStackTrace();
                }
                
                communicate();
        }
        
        public void communicate() {
                // send hello message to the server to initiate communication
                String toSend = "cs5700spring2014 HELLO " + neuId;
                outp.println(toSend);
                
                while (true) {
                        boolean result;
                        try {
                                String inp;
                                /*while((inp =  in.readLine()) != null)
                                        System.out.println("UserIn : " + inp);*/
                                //System.out.println(in.read());
                                result = processInput(in.readLine());
                                if(result) {
                                        // close socket connection and streams
                                        echoSocket.close();
                                        outp.close();
                                        in.close();
                                        break;
                                }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
        
        public boolean processInput(String inputLine) {
                StringTokenizer st = new StringTokenizer(inputLine);
                ArrayList<String> tokens = new ArrayList<String>();
                
                // get all tokens
                while(st.hasMoreTokens()) {
                        tokens.add(st.nextToken());
                }
                
                if(tokens.get(0).equals("cs5700spring2014") && tokens.get(1).equals("STATUS")) {
                        //do something
                        if(tokens.size() != 5) {
                                System.err.println("Wrong Message: STATUS: Incorrect number of arguments");
                                System.exit(1);
                        }
                        
                        try {
                                int operand1 = Integer.parseInt(tokens.get(2));
                                int operand2 = Integer.parseInt(tokens.get(4));
                                int solution = 0;
                                
                                switch(tokens.get(3).charAt(0)) {
                                case '+': 
                                        solution = operand1 + operand2;
                                        break;
                                case '-':
                                        solution = operand1 - operand2;
                                        break;
                                case '*':
                                        solution = operand1 * operand2;
                                        break;
                                case '/': 
                                        if(0 == operand2) {
                                                System.err.println("Wrong Message: STATUS: Divide by zero");
                                                System.exit(1);                                                
                                        }
                                        solution = operand1 / operand2;
                                        break;
                                default:
                                        System.err.println("Wrong Message: STATUS: Illegal operator");
                                        System.exit(1);                                        
                                }
                                
                                outp.println("cs5700spring2014 " + solution);
                                
                        } catch(NumberFormatException e) {
                                e.printStackTrace();
                                System.err.println("Wrong Message: STATUS: Illegal operand");
                                System.exit(1);
                        } catch(Exception e) {
                                // error in sending data to server
                                e.printStackTrace();
                                System.err.println("No response from server while sending solution");
                                System.exit(1);                                
                        }
                                
                        return false;
                }
                // handling for bye message
                else if(tokens.get(0).equals("cs5700spring2014") && tokens.get(2).equals("BYE")) {
                        System.out.println(tokens.get(1));
                        return true;
                }
                // the message is unknown
                else {
                        System.err.println("Wrong Message");
                        System.exit(1);
                }
                
                return true;
        }
        
        public static void main(String args[]) {
                if(args.length == 4) {
                        new client(args[1], args[2], args[3]);
                }
                else if(args.length == 2)
                        new client("27993", args[0], args[1]);
                else {
                        System.err.println("Illegal number of arguments to program");
                }
        }
}