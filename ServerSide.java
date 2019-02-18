/**
 * Created by n00897619 on 9/29/2017.
 */

import java.io.*;
import java.net.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSide{

    private static boolean shutdown = false;

    public static void main (String[] args ){
        
        System.out.println("The server is up");
        ServerSide server = new ServerSide();
            try{
                ServerSocket service = new ServerSocket(4200);
                Socket sock = service.accept();
                server.run(sock, service);
            } catch (Exception e){
                e.printStackTrace();
            }
    }
    private void run (Socket sock, ServerSocket socket){

        try{
            String meta = "192.168.101.121";
            System.out.println("The server has recieved a connection from " + meta);

            DataInputStream input = new DataInputStream(sock.getInputStream());
            PrintStream output = new PrintStream(sock.getOutputStream());
            try{
               
                    int comandAsInt = input.read();
                    int times = input.read();
                    System.out.print("The command wil be executed "+ times +" times");
                    for(int x = 0 ; x < times; x++){
                        System.out.print(x);
                    switch (comandAsInt){
                        case 1:
                            System.out.println("Attempting to get Date and Time " + meta);
                            respondToClient(output,"Host Current Date and Time : " + ResponseWrapper(runCommand("date")));
                            if((x + 1) == times)
                            {
                                respondToClient(output, "200 OK Completed");
                            }
                            break;
                        case 2:
                            System.out.println("Attempting to get Host's Uptime from " + meta);
                            respondToClient(output, "Current Uptime : " + ResponseWrapper(runCommand("uptime")));
                            if((x + 1) == times)
                            {
                                respondToClient(output, "200 OK Completed");
                            }
                            break;
                        case 3:
                            System.out.println("Attempting to get Host's Memory Use " + meta);
                            respondToClient(output, "Memory Use : " + ResponseWrapper(runCommand("free")));
                            if((x + 1) == times)
                            {
                                respondToClient(output, "200 OK Completed");
                            }
                            break;
                        case 4:
                            System.out.println("Attempting to get Host's Network Statistics from "+ meta);
                            respondToClient(output, "Network Statistics : " + ResponseWrapper(runCommand("netstat")));
                            if((x + 1) == times)
                            {
                                respondToClient(output, "200 OK Completed");
                            }
                            break;
                        case 5:
                            System.out.println("Attempting to get Host's Current Users from " + meta);
                            respondToClient(output, "Current User : " + ResponseWrapper(runCommand("who")));
                            if((x + 1) == times)
                            {
                                respondToClient(output, "200 OK Completed");
                            }
                            break;
                        case 6:
                            System.out.println("Attempting to get Host's Current Running Processes from "+ meta);
                            respondToClient(output, "Host Current Running Process: " + ResponseWrapper(runCommand("ps -c")));
                            if((x + 1) == times)
                            {
                                respondToClient(output, "200 OK Completed");
                            }
                            break;
                        case 7:
                            System.out.println("Attempting to shutdown... ");
                            shutdown = true;
                            if (shutdown == true){
                                input.close();
                                output.close();
                                socket.close();
                                sock.close();
                                System.exit(0);
                            }
                            break;
                        }
                    
                }
            } catch (Exception e){
                System.err.println("Format Error");
                e.printStackTrace();
            }
        } catch (Exception e){
                System.err.println("Format Error");
                e.printStackTrace();
            }
    }
    private String ResponseWrapper(Process command){

        String str;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader strOutput = new BufferedReader(new InputStreamReader(command.getInputStream()));
            while ((str = strOutput.readLine()) != null){
                sb.append(str);
                sb.append("\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }
     
    private void respondToClient(PrintStream output, String responseBody){
        try{    
            output.println("");
            output.print("Server Response : " + responseBody);
            output.println("Server 200 OK");
            //output.close();
            System.out.println("The response was sent");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Process runCommand(String command){

        try{
            return Runtime.getRuntime().exec(command);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
