import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processor of HTTP request.
 */
public class Processor implements Runnable {
    private final Socket socket;
    private final HttpRequest request;

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    public void process() throws IOException {
        // Print request that we received.
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        // To send response back to the client.
        PrintWriter output = new PrintWriter(socket.getOutputStream());
        System.out.println("Output: " + request.getRequestLine());
        String[] requestSplit = request.getRequestLine().split(" ");

        if (requestSplit[1].equals("/list")){
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"bash","-c","ls -R /home/magzhan"});
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );
                String line;
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: text/html; charset=utf-8");
                output.println();
                output.println("<html>");
                output.println("<head><title>hello</title></head>");


                while ((line = reader.readLine()) != null){
                    output.println("<body><p>" + line + "</p></body>");

                }
                output.println("</html>");
                output.flush();
                reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
//            output.println("HTTP/1.1 200 OK");
//            output.println("Content-Type: text/html; charset=utf-8");
//            output.println();
//            output.println("<html>");
//            output.println("<head><title>hello</title></head>");
//            output.println("<body><p>create item requested</p></body>");
//            output.println("</html>");
//            output.flush();
            socket.close();
        } else if(requestSplit[1].contains("/create")){
            String filename = requestSplit[1].replaceAll("/create/", "");
            Runtime.getRuntime().exec(new String[]{"bash","-c","touch /home/magzhan/IdeaProjects/untitled/src/" + filename});

            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>hello</title></head>");
            output.println("<body><p>create file named: " + requestSplit[1].replaceAll("/create/", "") + "</p></body>");
            output.println("</html>");
            output.flush();
            socket.close();
        } else if(requestSplit[1].contains("/factor")){
            String factor = requestSplit[1].replaceAll("/factor/", "");
            Process process = Runtime.getRuntime().exec(new String[]{"bash","-c","factor " + factor});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String line;
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>hello</title></head>");


            while ((line = reader.readLine()) != null){
                output.println("<body><p> factor of " + line + "</p></body>");

            }
            output.println("</html>");
            output.flush();
            reader.close();
            socket.close();
        } else {
            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>COMMAND NOT FOUND</p></body>");
            output.println("</html>");
            output.flush();
            socket.close();
        }


        socket.close();
    }
    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
