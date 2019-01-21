import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
//import jpcap.*;





public class TestTcpSend implements PacketReceiver {
	NetworkInterface[] devices = null;
	String inData = null;
	static JpcapSender sender = null;
//	String fileWriteTitleData = null;

	static int ConsoleFlag = 0;
	static int HexCodeFlag = 0;
	static int LangFlag = 0;
	static final int KSC5601 = 0;
	static final int ISO_8859_1 = 1;
	static final int EUC_KR = 2;
	static final int MS949 = 3;
	static final int UTF_8 = 4;
	
	
	public void getInterface() throws Exception {
		devices = JpcapCaptor.getDeviceList();

		System.out.println("usage: java TestTcpSend <select a number from the following>");
//		fileWriteTitleData = "usage: java TestTcpSend <select a number from the following>";

		for (int i = 0; i < devices.length; i++) {
			System.out.println(i + " :" + devices[i].name + "(" + devices[i].description + ")");
			System.out.println("    data link:" + devices[i].datalink_name + "(" + devices[i].datalink_description + ")");
			System.out.print("    MAC address:");
			for (byte b : devices[i].mac_address)
				System.out.print(Integer.toHexString(b & 0xff) + ":");
			System.out.println();
			for (NetworkInterfaceAddress a : devices[i].addresses)
				System.out.println("    address:" + a.address + " " + a.subnet + " " + a.broadcast);
		}
		
		// ������Ʈ��ũ�� �⺻������ ��Ŷ�� �ڱ�������ϰ� ��� ������ ����(Non-Promiscuous Mode)�̹Ƿ� �̰��� ���������� ������� �ٲ���Ѵ�.
		// To work around this behavior, force the adapter into Compatibility mode:
//		At a command prompt, type netsh bridge show adapter .
//		Locate the identification number of the NIC that is not responding.
//		If the NIC is not in Compatibility mode, you can change it manually if you type the following command, where 1 is the number of the NIC that is displayed in the first step:
//		netsh bridge set adapter 1 forcecompatmode=enable
//		Run the netsh bridge show adapter command again to verify that the ForceCompatabilityMode field for the NIC is displayed as Enabled .
		//������忡�� netsh bridge show adapter
		// netsh bridge set adapter 1 forcecompatmode=enable // 1�� ��Ͱ� �����ϰ��...
//		//���� �׳� ����ѹ��ε�... �̰� ���ϸ� ������Ʈ��ũ 0�� ���ý� ��������... ���ϱ�?
//		//������Ʈ��ũ�� 1���� �ؿ��� ���ص� �����ȳ���...
//		// Windows���� lookup�Ǿ��� Device Name�� �״�� �� ����� ����
//		System.out.println("*** All Devices name without fix*******************");
//		for (int i = 0; i < devices.length; i++) {
//		System.out.println("[" + i + "] " + devices[i]);
//		}
//		System.out.println("");
//		// Windows���� lookup�Ǿ��� Device Nameing���� �ذ� - �������� ������µ� �ϴ�...
//		Util.fixWindowsNameingProblem(devices.toString());
//		System.out.println("*** All Devices name fixed*************************");
//		for (int i = 0; i < devices.length; i++) {
//		System.out.println("[" + i + "] " + devices[i]);
//		}
		
		

		BufferedReader br = null;
		int device_num, byte_num ; //, port_num;
		boolean AllORMe=false;
		//�ڵ�ȭ�� �ٲ�鼭 �ϸ�ũ��...

		StringBuffer buf = new StringBuffer();
		buf.append("===============================\n\n");
		buf.append("<== ���ϴ� Netwokr Card ��ȣ �Է� : ");
		System.out.println(buf.toString());
//////////////////////////////////////////////////////////////////////////////////////////
		br = new BufferedReader(new InputStreamReader(System.in));
		inData = br.readLine();
		if (inData.length()<1){
			System.out.println("\n����̽��� �������� �����̽��ϴ�.");
			return;
		}
		device_num = Integer.parseInt(inData);
		System.out.println("<== ������ ��Ŷ��(Byte) �Է� (�⺻ 64): ");	br = new BufferedReader(new InputStreamReader(System.in));	inData = br.readLine();
		byte_num = Integer.parseInt(inData); // 64����Ʈ�� �⺻��.
		
		System.out.println("<== HexCode�� �����ұ��? �����ڴ� TAB Char(9)��. (�⺻ N) Y/N: ");	
		br = new BufferedReader(new InputStreamReader(System.in));
		inData = br.readLine();
		if(inData.equals("y") || inData.equals("Y")) {HexCodeFlag = 1; } else {HexCodeFlag = 0;} // 0�� �⺻��. ����.
		
		System.out.println("<== ���� ���ڵ��� �����ұ��? (�⺻  KSC5601 = 0, ISO-8859-1 = 1, EUC-KR = 2, MS949 = 3, UTF-8 = 4 ) : ");
		br = new BufferedReader(new InputStreamReader(System.in));
		inData = br.readLine();
		if (inData == null || inData.length() < 1 ) inData = Integer.toString(KSC5601);
		switch (Integer.parseInt(inData)) {
		case UTF_8:
			LangFlag = UTF_8;
			break;
		case MS949:
			LangFlag = MS949;
			break;
		case EUC_KR:
			LangFlag = EUC_KR;
			break;
		case ISO_8859_1:
			LangFlag = ISO_8859_1;
			break;
		default:
			LangFlag = KSC5601;
			break;
		}
		System.out.println("<== ����� ȭ�鿡 ����ұ��? (�⺻ N) Y/N: ");	
		br = new BufferedReader(new InputStreamReader(System.in));
		inData = br.readLine();
		if(inData.equals("y") || inData.equals("Y")) {ConsoleFlag = 1; } else {ConsoleFlag = 0;} // 0�� �⺻��. ����.
		
		System.out.println("<== ���ȸ���� ���Ѱ˻�(1), ��PC�� ȣ��Ʈ����Ŷ�� �˻�(0) ���� (�⺻�� 0)");	br = new BufferedReader(new InputStreamReader(System.in));	inData = br.readLine();
		if (inData != null || Integer.parseInt(inData) == 1){AllORMe=true;}
		//������Ʈ��ũ�� �����Ҷ� 0���� �����ؾ��Ѵ�.
/////////////////////////////////////////////////////////////////////////////////////////////
//		System.out.println("<== ������ ��Ʈ��ȣ (�⺻ 80): ");	br = new BufferedReader(new InputStreamReader(System.in));	inData = br.readLine();
//		port_num = Integer.parseInt(inData); 

//		device_num = 5;byte_num = 128;AllORMe = true; //����� �ʰ� ������ �����ϱ�... �׷��� ������ ����ũ�ؾ��Ѵ�...
		System.out.println("\n�Է��� ���� : �����ѵ���̽�( " + device_num + " ), ��������Ŷ��( " + byte_num + " ), ���ȸ�� ���� true or false ( " + AllORMe + " )\n\n" );

		// interface, ��Ŷ��(byte), true(��� �������� ȸ�� ��Ŷ) false(�� PC�� ȣ��Ʈ�� ��Ŷ),
		// timeout(ms)
		JpcapCaptor jpcap = JpcapCaptor.openDevice(devices[device_num], byte_num, AllORMe, -1);
		sender = JpcapSender.openDevice(devices[device_num]);

//		jpcap.setFilter("port " + port_num, true); // ��Ʈ�� �����Ҷ�... ""�� �����Ʈ�̴�....
		jpcap.setFilter("", true); // ��Ʈ�� �ƴ϶� ��ü ��Ŷ�� ���� ��������???
		// int ����� ��Ŷ��, -1 �� ���ѵ�
		jpcap.loopPacket(-1, new TestTcpSend());
		
	}

	public void receivePacket(Packet packet) {
		//TCPPacket tcpPacket = (TCPPacket) packet;
		if (packet instanceof TCPPacket) {
//			System.out.println("���� : " + sender.toString());
//			System.out.println("��Ŷ : " + packet.toString());
			
			TCPPacket tcpPacket = (TCPPacket)packet;
			byte[] data = tcpPacket.data;
			String srcHost = tcpPacket.src_ip + ":" + tcpPacket.src_port ;
			String dstHost = tcpPacket.dst_ip + ":" + tcpPacket.dst_port ;
			String isoData = null;
			try {
				switch (LangFlag) {
				case UTF_8:
					isoData = new String(data, "UTF-8");
					break;
				case MS949:
					isoData = new String(data, "MS949");
					break;
				case EUC_KR:
					isoData = new String(data, "EUC-KR");
					break;
				case ISO_8859_1:
					isoData = new String(data, "ISO-8859-1");
					break;
				default:
					isoData = new String(data, "KSC5601");
					break;
				}
				if(HexCodeFlag==1)isoData = isoData + "	" + Util.getHexa(data) ;
				
//				//isoData = new String(data, "ISO-8859-1");
//				//isoData = new String(data.toString().getBytes("KSC5601"),"EUC-KR");
//				isoData = new String(data, "KSC5601");
//				//isoData = new String(data, "ISO-8859-1") + "	" + Util.getHexa(data) ;
//				//�ȵȴ�//isoData = new String(data, "UTF-8") + "	" + Util.getHexa(data) ;
//				//�ȵȴ�//isoData = new String(data, "EUC-KR") + "	" + Util.getHexa(data) ;
//				//�ȵȴ�//isoData = new String(data, "MS949") + "	" + Util.getHexa(data) ;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			Date today = new Date(); 
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd"); 
            date.format(today).toString();  

            String str=isoData; // GetData(isoData);
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(date.format(today).toString() + ".txt",true)); //������ ������ ������ʰ� �̾��.
				out.write(srcHost+" -> " + dstHost + "=(DATA+HEX)" + str); out.newLine();
				//out.write(srcHost+" -> " + dstHost + "=(DATA+HEX)" + isoData); out.newLine();
				out.close();
			} catch (IOException e) {
			    System.err.println(e); // ������ �ִٸ� �޽��� ���
			    //System.exit(1);
			}

			if (ConsoleFlag == 1) System.out.println(srcHost+" -> " + dstHost + "=(DATA+HEX)" + str); //ȭ�鿡 ����� �Ѹ���...              
		}

/*
		// GET ���� ���Ѵ�
		if(tcpPacket.ack&&tcpPacket.psh&&!tcpPacket.dst_ip.toString().equals("/211.45.156.89")){
			byte[] bBytes = new byte[3];
			System.arraycopy(tcpPacket.data, 0, bBytes, 0, 3);
	
			// GET�ϰ�츸 SEND�Ѵ�.
			if (new String(bBytes).equals("GET")) {
				sendPacket(sender, packet); // �̰ɿ��� ��� ��û�� http://www.aromit.com�� �����Ѵ�.
			}
		}
		*/

	}
	
	
	private String GetData(String isoData) {
		// TODO Auto-generated method stub
		String [] aStr = null;
		aStr=isoData.split(";�ѽ�:");
		if (aStr[1]==null) return "";
		aStr[0]=aStr[1];
		aStr[1]=null;
		aStr=aStr[1].split("<br/>");
		if (aStr[1]==null || aStr[0].length()>15) return aStr[0].substring(0, 15);
		return aStr[0];
	}
	
	
	
	public void sendPacket(JpcapSender sender, Packet packet){
		//��Ŷ ������ �����ϰų�, ����, ���/������ �����Ǹ� �����Ͽ� �ٽ� ������ �ִ�.
		try {
			TCPPacket tcpPacket = (TCPPacket)packet;
			
			EthernetPacket ethernet = (EthernetPacket)packet.datalink;
			
			EthernetPacket ether=new EthernetPacket();
			ether.frametype=EthernetPacket.ETHERTYPE_IP;
			ether.src_mac=ethernet.dst_mac;
			ether.dst_mac=ethernet.src_mac;
			
			TCPPacket p=new TCPPacket(tcpPacket.dst_port,tcpPacket.src_port,tcpPacket.ack_num,tcpPacket.sequence+206,
					false, true,true,false,false,true,false,false,0,tcpPacket.urgent_pointer);
			p.setIPv4Parameter(tcpPacket.priority,tcpPacket.d_flag,tcpPacket.t_flag,tcpPacket.r_flag,tcpPacket.rsv_tos,false,
					false,false,tcpPacket.offset,tcpPacket.ident+23187,226,tcpPacket.protocol,tcpPacket.dst_ip,tcpPacket.src_ip);
			p.data=("HTTP/1.0 302 Redirect\r\nLocation: http://www.aromit.com\r\n\r\n").getBytes();
			p.datalink=ether;
			sender.sendPacket(p);
			
		} catch (Exception e) {
			System.out.println("sendPacketException : " + e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
//		if (args.length == 1){
//			fileName = args[0];			
//		} else {
//			fileName = 
//		}
//		String javaLibraryPath = System.getProperty("java.library.path");
//		if(javaLibraryPath.indexOf("C:\\Windows\\Sun\\Java\\bin")>-1)
//			System.setProperty("java.library.path", "C:\\Windows\\Sun\\Java\\lib\\ext;" + javaLibraryPath);
//		System.out.println(System.getProperty("java.library.path"));
//		System.out.println(System.getProperty("os.version"));
//		System.out.println(System.getProperty("java.vm.version"));
//		System.out.println(System.getProperty("java.vm.vendor"));
//		System.out.println(System.getProperty("java.vm.name")); //JRockit
//		System.out.println(System.getProperty("java.ext.dirs"));
//		System.out.println(System.getProperty("java.version"));
//		System.out.println(System.getProperty("java.vendor"));
//		System.out.println(System.getProperty("java.vendor.url"));
//		System.out.println(System.getProperty("java.vm.specification.version"));
//		System.out.println(System.getProperty("java.vm.specification.vendor"));
//		System.out.println(System.getProperty("java.vm.specification.name"));
//		System.out.println(System.getProperty("java.specification.version"));
//		System.out.println(System.getProperty("java.specification.vendor"));
//		System.out.println(System.getProperty("java.specification.name"));
//		System.out.println(System.getProperty("java.class.version"));

		boolean isRun = false;
		if(System.getProperty("file.separator").equals("\\")
				&& System.getProperty("java.vm.version").indexOf("64")==-1
				&& System.getProperty("java.vm.name").indexOf("64")==-1) {
			String[] extPaths = System.getProperty("java.ext.dirs").split(";");
			for (int i = 0; i < extPaths.length; i++) {
				if(new File(extPaths[i] + "\\jpcap.jar").exists()){
					isRun=true;
					break;
				}
			}
		}
		
		if(isRun)
			new TestTcpSend().getInterface();
		else
			System.out.println(
					new StringBuilder()
					.append("1. ������ JDK 32��Ʈ�� �Ͽ��� �Ѵ�.(Jrocket�ȵ�)\n")
					.append("\n")
					.append("2. jpcapSetup-0.7.exe�� ��ġ�Ͽ� �ش� ������ jpcap.jar�� �־�� �Ѵ�.\n")
					.append("   jpcap.jar�� �����������ؿ� Sun\\Java\\lib\\ext �ȿ� �־�� �Ѵ�.\n")
					.append("   �Ǵ� java.ext.dirs ȯ���ο� ����Ǿ� �־�� �Ѵ�. �̰�� Jrocket����\n")
					.append("\n")
					.append("3. WinPcap_4_1_2.exe�� ��ġ�ϸ�, ����⸦ ����͸� �� �� �ְ�\n")
					.append("   ����� �ε��� �� �ִºκп� üũ�Ͽ��� �Ѵ�.(��ġ�� ���)\n")
					.append("\n")
					.append("=== 64��Ʈ �������� ��쿡�� JDK 32��Ʈ�� ��ġ�Ͽ� ���� �����Ͽ� �����ϸ� �ȴ�.\n")
					.append("\n")
					.toString());
	}
}
