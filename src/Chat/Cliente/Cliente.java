package Chat.Cliente;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
//EDSON RODRIGUES DOS SANTOS JUNIOR CG3001229
public class Cliente {

    static int socket = 2424;
    static String ip = "127.0.0.1";

    public static void main(String[] args) {

        try {
            final Socket cliente = new Socket(ip, socket);
//lendo msg do servidor
            new Thread(){
                @Override
                public void run(){
                    try {
                        Scanner ler = new Scanner(cliente.getInputStream());
                        while(true){
                            String msg = ler.nextLine();
                            System.out.println(msg);
                        }
                    } catch (Exception e) {
                        System.err.println("erro ao receber a mensagem");
                    }
                }
            }.start();
//escrevendo para o servidor
    PrintWriter escrever = new PrintWriter(cliente.getOutputStream(),true);//envia para o servidor
    Scanner lerTerminal = new Scanner(System.in);//recebe a msg do terminal
    String msgTerminal = "";
            
    while(true){
        msgTerminal = lerTerminal.nextLine();
        escrever.println(msgTerminal);
        if(msgTerminal.equalsIgnoreCase("sair")){
            System.exit(0);
        };
        Thread.sleep(1); //delay entre escrever e receber
    }
        } catch (Exception e) {
            System.err.println("erro ao enviar para o servidor");
        }
    }
    
}
