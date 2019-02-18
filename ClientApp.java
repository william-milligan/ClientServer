
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Willi
 */
public class ClientApp{

    private static String TargetIP; 
    public int TargetPort;
    public final String EndCommand = "Quit";
    public static Scanner scanner = new Scanner(System.in);
    public static ArrayList<Long> responseTime = new ArrayList<>();
    public int NumClientsNoThread;
    


    public static void main (String args[]){

        ClientApp client = new ClientApp();
        DetermineIP(scanner);
        client.DetermineNumberOfNonThreadedClients();
        client.DetermineAction(TargetIP);
        client.DeterminAverageTime();
        
    }
    
        public static void DetermineIP (Scanner s){

        System.out.print("Client Initilized \n");
        System.out.println("Enter the Server's IP");
        TargetIP = s.nextLine();
        
        
        while(!isValidIP(TargetIP)){
            if(TargetIP.equalsIgnoreCase("Quit"))
            {
                System.out.print("Ending Client Program");
                return;
            }
            System.err.println("[ERROR] Invalid IP Address");
            TargetIP = s.nextLine();
        }

    }
    

        
        private void DetermineAction(String serverIP){


        System.out.println("The IP is " + serverIP);
        System.out.println("\n*");
        System.out.println("A Request has started");
        System.out.println("*");
        System.out.println("\nChoose an action\n" );

        String[] actions = new String[]{
           "1 - Date & Time",
           "2 - Uptime",
           "3 - Memory Use",
           "4 - Network Statistics",
           "5 - Current Users",
           "6 - Running Processes",
           "7 - End Connection"
        };
        for(String ui : actions){
            System.out.println(ui);
        }

        int requestIndex = scanner.nextInt();

        while (!isValidNumber(requestIndex)){
            System.err.println("Please choose a number 1 - 7");
            requestIndex = scanner.nextInt();
        }
       
        ClientToServerConnection(TargetIP, requestIndex );
       


    }
        
    private static boolean isValidNumber(int input){
        return input > 0 && input < 8;
    }
    
    public void DetermineNumberOfNonThreadedClients(){
        System.out.print("\nHow many clients would you like to have? : ");
        int numClients = scanner.nextInt();
        while (numClients < 1)
        {
            System.out.print("\nMust have atleaste 1 cleint");
            numClients = scanner.nextInt();
        }
        NumClientsNoThread = numClients;
    }
    
    public static boolean isValidIP(String InputIP){

        String VALID_IPADDRESS = 
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        
        Pattern ipPattern = Pattern.compile(VALID_IPADDRESS);
        if(ipPattern.matcher(InputIP).matches())
            return true;
        else 
            return false;

    }

    private void ClientToServerConnection(String serverIP, int requestedData){
        Socket requestingSocket = null;
        DataOutputStream output = null;
        BufferedReader in = null;
        Long endingTime = (long)0;

        try{
            System.out.println("The request is " + requestedData);
            System.out.println("Client :  Connecting " + serverIP);
            requestingSocket = new Socket(serverIP, 4200);
            
            System.out.println("Client : Connecting to Server on port 4200");

            output = new DataOutputStream(requestingSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(requestingSocket.getInputStream(), "UTF-8"));

            System.out.println("Attempting to retrieve data from Server...");


            if (requestingSocket != null && output != null && in != null){
                
                try{
                        output.flush();
                        Long startTime = System.currentTimeMillis();
                        output.write(requestedData);
                        output.write(NumClientsNoThread);
                        System.out.println("The Client has sent data Starting Timer");

                        String Response;

                        while ((Response = in.readLine()) != null){
                            System.out.println(Response);

                            if (Response.contains("200 OK Completed")){
                                System.out.println("Ending time has been recorded");
                                endingTime = System.currentTimeMillis();
                                break;
                            }
                        }
                        System.out.println("The response time is " + (startTime - endingTime));
                        responseTime.add(startTime - endingTime);
                    
                } catch(Exception e){
                    System.err.println("Error in multi client for loop");
                }
            }
        } catch(UnknownHostException UnknownHostIP){
            System.err.println("Error in connection");
        } catch (IOException ioexcept){
            System.err.println("Could not connect to the server");
            System.exit(0);
        } finally {

            try{

                System.out.println("Closing the connection");
                requestingSocket.close();
                in.close();
                output.close();
                

            } catch (IOException ioExcept){
                System.exit(0);
            } 
        }
    }

    private void DeterminAverageTime() {
        long sum = (long)0;
        for( long x : responseTime ){
            sum += x;
        }
        System.out.println("The average server response time is : " + sum/responseTime.size() + " ms");
    }
}



