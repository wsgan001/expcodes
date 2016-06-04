package service.client;

import org.apache.thrift.TException; 
import org.apache.thrift.protocol.TBinaryProtocol; 
import org.apache.thrift.protocol.TProtocol; 
import org.apache.thrift.transport.TSocket; 
import org.apache.thrift.transport.TTransport; 
import org.apache.thrift.transport.TTransportException; 
import service.demo.Hello; 
import service.demo.Person;

/** 
 * @author ���
 * @version 1.0
 * @datetime 2015-12-28 ����03:28:03 
 * ��˵�� 
 */
public class HelloServiceClient {

	public static void main(String[] args) { 
		try {
			TTransport transport = new TSocket("172.168.8.36", 9813, 3000);
	        transport.open(); 
	        TProtocol protocol = new TBinaryProtocol(transport); 
	        Hello.Client client = new Hello.Client(protocol); 
	        long s = System.currentTimeMillis();
			//for(int i=0; i<10000; i++) {
	        	String str = client.helloString("test");
	        	System.out.println(str);
	        //}	
			long e = System.currentTimeMillis();
			System.out.println("��ʱ��" + (e-s)/1000 + "��");
			transport.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        //ThreadTest tt1 = new ThreadTest();
        //tt1.start();
    }
	
}

class ThreadTest extends Thread  {
	
	TTransport transport = null;
	Hello.Client client = null;
	
	public ThreadTest() {
		try {
			//���õ��õķ����ַΪ172.168.8.36���˿�Ϊ9813 socketTimeout��connectionTimeoutΪ3��
			transport = new TSocket("172.168.63.235", 9813, 3000);
	        transport.open(); 
	        //���ô���Э��Ϊ TBinaryProtocol 
	        TProtocol protocol = new TBinaryProtocol(transport); 
	        client = new Hello.Client(protocol); 
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			long s = System.currentTimeMillis();
			for(int i=0; i<100000; i++) {
	        	String str = client.helloString("test");
	        	//System.out.println(str);
	        }	
			long e = System.currentTimeMillis();
			System.out.println("��ʱ��" + (e-s)/1000 + "��");
			
			transport.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}