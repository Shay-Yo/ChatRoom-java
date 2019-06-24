
import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;
public class  ChatServer {
    Vector<String> users = new Vector<String>();
    Vector<HandleClient> clients = new Vector<HandleClient>();
    public void process() throws Exception  {
        //Creating a new server for clients to join.
        ServerSocket server = new ServerSocket(7777,10);
        out.println("Server Started...");

        //keeps on checking if clients are trying to connect to the server.
        while( true) {
            Socket client = server.accept();
            HandleClient c = new HandleClient(client);
            clients.add(c);
        }
    }


    //the main creates an object of the server.
    public static void main(String ... args) throws Exception {
        new ChatServer().process();
    }



    public void broadcast(String user, String message, boolean endOrStart)  {
        // send message to all connected users
        for ( HandleClient c : clients )
                c.sendMessage(user,message, endOrStart);
    }



    class  HandleClient extends Thread {
        String name = "";
        BufferedReader input;
        PrintWriter output;
        public HandleClient(Socket  client) throws Exception {
            // get input and output streams
            input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
            output = new PrintWriter ( client.getOutputStream(),true);

            // read name
            name  = input.readLine();
            users.add(name); // add the new participant to the name list

            start();

            broadcast(name,"has joined the chat",true); //notify all users that a new person as joined the chat.
        }
        public void sendMessage(String uname,String  msg, boolean isEndOrStart)  {
            if(isEndOrStart)
                output.println(uname+" "+msg);
            else
                output.println( uname + ":" + msg);
        }


        public void run()  {
            String line;
            try    {
                sleep(100);

                //sending the new participant list because a new participant as joined the chat.
                String user="";
                for ( String s : users )
                    user+=s+",";
                broadcast(name,"name-"+user,true);

                while(true)   {
                    line = input.readLine();

                    if ( line.equals("end") ){
                        broadcast(name,"has left the chat.", true);
                        clients.remove(this);
                        users.remove(name);

                        //sending the new participant list because a new participant as left the chat.
                        user="";
                        for ( String s : users )
                            user+=s+",";
                        broadcast(name,"name-"+user,true);

                        break;
                    }
                    broadcast(name,line,false); // send messages to all participants of the chat
                }
            }
            catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        } // end of run()
    } // end of inner class
} // end of Server