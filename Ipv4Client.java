
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.*;



// Luis Cortes
// CS 380
// Project 3

public class Ipv4Client {
	public static void main(String[] args) {

		try {
			// Get IP Address
			InetAddress address = InetAddress.getByName(
				new URL("http://codebank.xyz").getHost());
			String ip = address.getHostAddress();

			// Connect to server
			Socket socket = new Socket(ip, 38003);
			System.out.println("Connected to server");

			PrintStream outStream = new PrintStream(socket.getOutputStream(), true);
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);


			for (int i=0; i < 12; i++) {
				Ipv4 datagram = new Ipv4();
				byte[] packet = new byte[datagram.size()];
				packet = datagram.getPacket();

				System.out.println("data length: "+datagram.size());
				outStream.write(packet, 0, packet.length); // Send to server
				String info = br.readLine();

				System.out.println(info);
			}

		} catch (Exception e) {e.printStackTrace();}

	}
}