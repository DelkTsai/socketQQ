package cn.gg.tools;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cn.gg.comon.Message;
import cn.gg.comon.MessageType;
import cn.gg.view.GGServer;

public class ServerSocketThread extends Thread {

	private Socket socket;
	private ObjectOutputStream write;
	private ObjectInputStream read;

	public ServerSocketThread() {

	}

	public ServerSocketThread(Socket s) {
		this.socket = s;
	}

	public void run() {
		try {
			//read = new ObjectInputStream(socket.getInputStream());
			int count=0;
			while (true) {
				if(count++==0){
					read = new ObjectInputStream(socket.getInputStream());
				}
				Message msg = (Message) read.readObject();
				Socket getter = GGServerSocketManager.getServerSocket(msg
						.getGetter());
				if (msg.getMessageType() == MessageType.chat_msg) {
					
					System.out.println(msg.getSender()+" �� "+
							msg.getGetter()+"��"+msg.getDate()+"ʱ˵: "+msg.getContent());
					GGServer.loggerStr+=msg.getSender()+" �� "+
							msg.getGetter()+"��"+msg.getDate()+"ʱ˵: "+msg.getContent();
					GGServer.gg.putLoggerInfo();
					
					write = new ObjectOutputStream(getter.getOutputStream());
					write.writeObject(msg);
				} else if (msg.getMessageType() == MessageType.checklogin) {
					write = new ObjectOutputStream(socket.getOutputStream());
					if (msg.getContent().equals("kkkkkk")) {
						msg.setContent("ok");
						msg.setGetter(msg.getSender());
						write.writeObject(msg);
						GGServerSocketManager.addServerSocket(msg.getSender(),
								socket);
					} else {
						msg.setContent("fiald");
						write.writeObject(msg);
					}

				} else if (msg.getMessageType() == MessageType.getfriend) {
					msg.setMessageType(MessageType.returnfriend);
					
					System.out.println(msg.getSender()+"��Ҫ��ȡ���ߺ���!");
					GGServer.loggerStr+=msg.getSender()+"��Ҫ��ȡ���ߺ���!";
					GGServer.gg.putLoggerInfo();
					String[] keys = GGServerSocketManager.getServerSocketList();
					StringBuffer sbf=new StringBuffer();
					for (int i = 0; i < keys.length; i++) {
						System.out.println("��ȡ�ĺ���:"+keys[i]);
						GGServer.loggerStr+="��ȡ�ĺ���:"+keys[i];
						GGServer.gg.putLoggerInfo();
						sbf.append(keys[i]+"_");
						if(keys[i].equals(msg.getSender()))
							continue;
						Socket s=GGServerSocketManager.getServerSocket(keys[i]);
						write=new ObjectOutputStream(s.getOutputStream());
						Message m=new Message();
						m.setMessageType(MessageType.returnfriend);
						m.setSender(msg.getSender());
						m.setContent(msg.getSender());
						m.setGetter(keys[i]);
						System.out.println(msg.getSender()+"����� "+keys[i]+",��������!");
						GGServer.loggerStr+=msg.getSender()+"����� "+keys[i]+",��������!";
						GGServer.gg.putLoggerInfo();
						write.writeObject(m);
					}
					write=new ObjectOutputStream(socket.getOutputStream());
					msg.setContent(sbf.toString());
					msg.setGetter(msg.getSender());
					write.writeObject(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
