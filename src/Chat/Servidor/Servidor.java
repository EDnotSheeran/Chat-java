package Chat.Servidor;

import java.net.ServerSocket;
import java.net.Socket;
//EDSON RODRIGUES DOS SANTOS JUNIOR CG3001229
public class Servidor {

    static int socket = 2424;

    public static void main(String args[]) {
        try {
            System.out.println("Iniciando o servidor...");
            ServerSocket servidor = new ServerSocket(socket);
            System.out.println("Servidor Aberto na porta: "+socket);
            
            while(true){
                Socket cliente = servidor.accept();
                new Gerenciador(cliente);
            }
            
        }catch(Exception e){
            System.err.println("Porta Ocupada");
        }
    }
}
