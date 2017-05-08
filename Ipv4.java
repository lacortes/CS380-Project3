// Luis Cortes
// CS 380
// Project 3

public class Ipv4 {
	private static int datagramLength = 1; 

	private byte version; // version (4 bits) 
	private byte hLen; // header length in words (4 bits)
	private byte tos; // type of service (8 bits)
	private short length;  // total length in bytes( 16 bits)
	private short identity; // identification (16 bits)
	private byte flags; // 3 bits
	private short offset; // 13 bits
	private byte ttl; // time to live  (8 bits)
	private byte protocol; // 8 bits
	private short checksum; // 16 bits
	private int sourceAdd; // source address 32 bits
	private int destinationAdd; // source address 32 bits

	private byte[] bytes; 
	private byte[] packet; 
	private byte[] cksum;


	/**
	 *	Initialize all assumed information
	 */
	public Ipv4() {
		this.datagramLength *=2;
		this.version = 0x4;;
		this.hLen = 0x5; // No options or padding implemented
		this.tos = 0x0; // Not implemented
		this.length = 0x14; // Initial state
		this.identity = 0x0; // Not implemented
		this.flags = 0x02; // Assuming no fragemtation
		this.offset = 0x0; // No implementation
		this.ttl = 0x32; // Assume every packet has ttl of 50
		this.protocol = 0x06; // Assuming TCP for all packets
		this.checksum = 0x0000;
		this.sourceAdd = 0x0A2F9623;
		this.destinationAdd = 0x3425589A;
		this.bytes = new byte[datagramLength];  

		this.populateData();
		this.makePacket();
		this.calculateChecksum(this.packet);
	}

	/**
	 *	Get the packet
	 */
	public byte[] getPacket() {
		return this.packet;
	}

	/**
	 *	Return size of data
	 */
	public int size() {
		return this.datagramLength;
	}

	/**
	 *	Make a packet of bytes
	 */
	private byte[] makePacket() {
		this.packet = new byte[this.length];
		int index = 0; 

		// Put together version and Hlen
		byte versionAndHlen = (byte) ( (this.version << 4) | (this.hLen & 0xF) );
		this.packet[index++] = versionAndHlen;

		// TOS
		this.packet[index++] = this.tos;

		// Length, break down into two bytes
		this.packet[index++] =  (byte) ((this.length >> 8) & 0xFF);
		this.packet[index++] = (byte) (this.length & 0xFF);

		// Identitiy
		this.packet[index++] = 0x00;
		this.packet[index++] = 0x00;

		// Combine flags, and upper part of Offset
		this.packet[index++] = (byte) ((flags<<5) | (0xFF & 0x00));

		// Second half of offset
		this.packet[index++] = 0x00; 

		// TTL
		this.packet[index++] = (byte) (this.ttl & 0xFF);

		// Protocol
		this.packet[index++] = (byte) (this.protocol & 0xFF);

		// Checksum
		this.packet[index++] = 0x00;
		this.packet[index++] = 0x00;

		// Source IP broken down into 4 bytes
		for (int i = 6; i >= 0; i-=2) {

			if (i==0) {
				this.packet[index++]= (byte) (this.sourceAdd & 0xFF);
			} else {
				byte sourceIP = (byte) ((this.sourceAdd >> (i*4)) & 0xFF );
				this.packet[index++] = sourceIP;
			}
		}

		// Destination IP broken down into 4 bytes
		for (int i = 6; i >=0; i-=2) {
			if (i==0) {
				this.packet[index++] = (byte) (this.destinationAdd & 0xFF);
			} else {
				byte destIP = (byte) ((this.destinationAdd >> (i*4)) & 0xFF);
				this.packet[index++] = destIP;
			}
		}

		// Copy data on to packet
		for (byte data : bytes) {
			this.packet[index++] = data; 
		}

		return this.packet;
	}

	/**
	 *  Calculate checksum and put it as a byte arraay
	 */
	private void calculateChecksum(byte[] buf) {
    	int length = buf.length;
    	int i = 0;

    	long sum = 0;
    	long data;

    	// Handle all pairs
    	while (length > 1) {
      		data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
		    sum += data;
 
      		if ((sum & 0xFFFF0000) > 0) {
        		sum = sum & 0xFFFF;
        		sum += 1;
      		}

      		i += 2;
      		length -= 2;
    	}

    	// Handle remaining byte in odd length buffers
    	if (length > 0) {
      		sum += (buf[i] << 8 & 0xFF00);
      		
      		if ((sum & 0xFFFF0000) > 0) {
        		sum = sum & 0xFFFF;
        		sum += 1;
      		}
    	}

    	sum = ~sum;
    	sum = sum & 0xFFFF;

    	byte[] checksumBytes = new byte[2];
    	byte first = (byte) ((sum >> 8) & 0xFF);
    	checksumBytes[0] = first; 
    	
    	byte second = (byte) (sum & 0xFF);
    	checksumBytes[1] = second;

    	// Update packet chesksum
    	this.packet[10] = first;
    	this.packet[11] = second;

    // 	System.out.print(Integer.toHexString(checksumBytes[0] & 0xFF));
  		// System.out.println(Integer.toHexString(checksumBytes[1] & 0xFF));
  	}

  	/**
   	 *  Populate data segment
   	 */
	private void populateData() {

		// Fill data segment one byte at a time
		for (int i=0; i < this.datagramLength; i++) {
			byte data = 0x00; // A byte of data
			this.bytes[i] = data;
		}

		this.length += this.datagramLength;
  	}

}