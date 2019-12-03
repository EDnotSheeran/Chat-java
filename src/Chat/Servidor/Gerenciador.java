package Chat.Servidor;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
//EDSON RODRIGUES DOS SANTOS JUNIOR CG3001229

public class Gerenciador extends Thread {

    private Socket cliente;  //socket de conexao com o cliente
    private Scanner ler; //objeto Scanner para leitura a leitura da mgm recebida
    private PrintWriter escrever; //objeto PrintWriter para enviar msg ao cliente
    private String nomeUsuario = "você"; //nome do usuario
    private static ArrayList<String> listaUsuarios = new ArrayList<String>();//ArrayList com o nome dos usuarios
    private boolean login = false; //variavel que verifica se o usuario esta logado ou nao
    private static Map<String, Gerenciador> USUARIOS = new HashMap<>();// ArrayMap com os objetos Gerenciador(contendo os IPs de cada cliente)

    public Gerenciador(Socket cliente) {
        this.cliente = cliente;//recebe o cliente por parametro da classe Servidor
        start();//starta o Thread Gerenciador assim que construido
    }

    @Override
    public void run() {//sobreescreve o metodo run da classe Thread
        try {
            ler = new Scanner(cliente.getInputStream());//recebe uma string vinda de Cliente
            escrever = new PrintWriter(cliente.getOutputStream(), true);//envia uma string a CLiente//flush automatico
            String msg;
            while (true) {
                msg = ler.nextLine();//le a msg vinda de cliente
// sair
                if (msg.equalsIgnoreCase("sair")) { //se a msg for igual a sair
                    listaUsuarios.remove(nomeUsuario);//remove this usuario da lista
                    //envia a lista atualizada a todos os usuarios conectados
                    for(int i = 0; i < listaUsuarios.size(); i++){
                            Gerenciador destino = USUARIOS.get(listaUsuarios.get(i));
                            destino.listaUsuarios();
                        }
                    
                    this.cliente.close();//fecha o Cliente
                } //  Fazer Login
                else if (msg.toLowerCase().startsWith("login:")) {
                    nomeUsuario = msg.substring(6, msg.length());
                    if (!listaUsuarios.contains(nomeUsuario) && login == false) {
                        listaUsuarios.add(nomeUsuario); //adiciona o nome do usuario ao Array com todos os nomes de usuario
                        escrever.println("login:true");
                        login = true; // controla se o usuarios esta logado
                        USUARIOS.put(nomeUsuario, this); // salva esse objeto no Array com todos objetos Gerenciador
                        // imprime os usuarios para o cliente
                        listaUsuarios();
                    } else {
                        escrever.println("login:false");
                    }
                } //  manda mesnagem privada
                else if (msg.toLowerCase().startsWith("mensagem:")) {
                    String nomeANDmensagem = msg.substring(9, msg.length());//corta "mensagem:" da string
                    String destinoNome[] = (nomeANDmensagem.substring(0, (nomeANDmensagem.indexOf(":")))).split(";");//corta o/os nomes do/dos destinatarios
                    String mensagem = nomeANDmensagem.substring(nomeANDmensagem.indexOf(":") + 1);//corta a mensagem que sera enviada
                    ArrayList<String> destinatarios = new ArrayList<>();//Array com todos os destinatarios
                    
                    if (destinoNome[0].equalsIgnoreCase("*")) {
                        //envia mensagem para todos os usuarios
                        for(int i = 0; i < listaUsuarios.size(); i++){
                            Gerenciador destino = USUARIOS.get(listaUsuarios.get(i));
                            if (!destino.equals(nomeUsuario)) {
                                destino.escrever.println("transmitir:"+nomeUsuario+":*:"+ mensagem);
                            }
                        }
                    } else {
                        //envia mensagem para cada destinatario
                        for (int i = 0; i < destinoNome.length; i++) {//de 0 ate a quantidade de destinos
                            Gerenciador destino = USUARIOS.get(destinoNome[i]);//seleciona o destinatario
                            if (destino == null) {//se o destinatario for nulo
                                System.out.println("Cliente nao existe");
                            } else {//senao
                                destino.escrever.println("transmitir:"+nomeUsuario+":"+nomeANDmensagem);//envia a mensagem ao destinatario
                            }
                        }
                    }
                } //   Escreve a mensagem
                else {
                    escrever.println("transmitir:" + nomeUsuario + ":" + msg);
                }
            }
        } catch (Exception e) {
            System.err.print("O cliente ");
            System.err.print("[" + nomeUsuario + "]");
            System.err.println(" fechou a conexao");
        }
    }
//    lista usuarios

    public void listaUsuarios() {
        String usuarios = "";
        for (int i = 0; i < listaUsuarios.size(); i++) {
            usuarios += listaUsuarios.get(i) + ";";
        }
        escrever.println("lista_usuarios:" + usuarios);
    }
}
