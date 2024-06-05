package net.sashakyotoz.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        System.out.println(server.getAddress());
        while (true){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}