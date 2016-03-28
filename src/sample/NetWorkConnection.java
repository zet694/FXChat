package sample;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class NetWorkConnection {

    private Consumer<Serializable> onReceiveCallBack;
    private ConnectionThread connectionThread = new ConnectionThread();

    public NetWorkConnection(Consumer<Serializable> onReceiveCallBack){
        this.onReceiveCallBack = onReceiveCallBack;
        connectionThread.setDaemon(true);
    }

    public void StartConnection() throws Exception{
        connectionThread.start();
    }

    public void send(Serializable data) throws Exception{
        connectionThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception{
        connectionThread.socket.close();
    }

    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread{
        private Socket socket;
        private ObjectOutputStream out;



        @Override
        public void run() {
            try(ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
                Socket socket = isServer() ? server.accept() : new Socket( getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (true){
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallBack.accept(data);
                }

            }catch (Exception e){
                onReceiveCallBack.accept("Connection closed");
            }
        }
    }

}
