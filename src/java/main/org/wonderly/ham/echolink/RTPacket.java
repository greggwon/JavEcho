package org.wonderly.ham.echolink;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 *  Some constants for use here and elsewhere
 */
interface rtcp_type_t {
	public static final int RTCP_SR   = 200;
	public static final int RTCP_RR   = 201;
	public static final int RTCP_SDES = 202;
	public static final int RTCP_BYE  = 203;
	public static final int RTCP_APP  = 204;
}

/**
 *  more constants
 */
interface rtcp_sdes_type_t {
	public static final int RTCP_SDES_END    =  0;
	public static final int RTCP_SDES_CNAME  =  1;
	public static final int RTCP_SDES_NAME   =  2;
	public static final int RTCP_SDES_EMAIL  =  3;
	public static final int RTCP_SDES_PHONE  =  4;
	public static final int RTCP_SDES_LOC    =  5;
	public static final int RTCP_SDES_TOOL   =  6;
	public static final int RTCP_SDES_NOTE   =  7;
	public static final int RTCP_SDES_PRIV   =  8; 
	public static final int RTCP_SDES_IMG    =  9;
	public static final int RTCP_SDES_DOOR   = 10;
	public static final int RTCP_SDES_SOURCE = 11;
	static final String[]types = {
		"END",
		"CNAME",
		"NAME",
		"EMAIL",
		"PHONE",
		"LOC",
		"TOOL",
		"NOTE",
		"PRIV",
		"IMG",
		"DOOR",
		"SOURCE",
	};
}

/**
 *  An input and output buffered pipe of byte[] data
 */
class ByteDataStream {
	byte data[];
	int maxlen;
	int off;
	
	public String toString() {
		int i = 0;
		int cnt = 0;
		String str = "";
		byte[]hex = "0123456789abcdef".getBytes();
		while( i < off ) {
			if( cnt == 16 )
				str += "\n";
			str += "0x";
			String add = Long.toHexString( i )+"";
			add = "0000".substring(add.length())+add;
			str += add+"  ";
			String cstr = "";
			int j = 0;
			for( ; j < 16 && i + j < off; j += 2 ) {
				int v1 = (data[i+j]&0xf0) >> 4;
				int v2 = (data[i+j]&0xf);
				str += (char)hex[v1];
				str += (char)hex[v2];
				if( i+j+1 < off ) {
					int v3 = (data[i+j+1]&0xf0) >> 4;
					int v4 = (data[i+j+1]&0xf);
					str += (char)hex[v3];
					str += (char)hex[v4];
				} else {
					str += "  ";
				}
				str += " ";
				if( data[i+j] <= ' ' || data[i+j] == 127 )
					cstr += ".";
				else
					cstr += (char)data[i+j];
				if( i+j+1 < off ) {
					if( data[i+j+1] <= ' ' || data[i+j+1] == 127 )
						cstr += ".";
					else
						cstr += (char)data[i+j+1];
				} else {
					cstr += " ";
				}
			}
			while( j < 16 ) {
				str += "  ";
				if( (j & 1) != 0 )
					str += " ";
				++j;
			}
			i += 16;
			str += "     "+cstr+"\n";
		}
		return str;
	}

	public ByteDataStream( byte arr[], int sz ) {
		data = new byte[sz];
		System.arraycopy( arr, 0, data, 0, sz );
		off = sz;
	}

	public ByteDataStream( int sz ) {
		data = new byte[sz];
	}
	public void write( int val ) {
		data[off++] = (byte)(val&0xff);
		if( off > maxlen) maxlen = off;
	}
	public void writeBytes( String str ) {
		byte[]arr = str.getBytes();
		write( arr );
	}
	public void write( byte[]arr ) {
		System.arraycopy( arr, 0, data, off, arr.length );
		off += arr.length;
		if( off > maxlen) maxlen = off;
	}
	public int size() {
		return off;
	}
	public void seek( int off ) {
		this.off = off;
	}
	public void writeShort( int val ) {
		data[off++] = (byte)((val>>8)&0xff);
		data[off++] = (byte)(val&0xff);
		if( off > maxlen) maxlen = off;
	}
	public void writeByte( int val ) {
		data[off++] = (byte)(val&0xff);
		if( off > maxlen) maxlen = off;
	}
	public void writeInt( int val ) {
		data[off++] = (byte)((val>>24)&0xff);
		data[off++] = (byte)((val>>16)&0xff);
		data[off++] = (byte)((val>>8)&0xff);
		data[off++] = (byte)(val&0xff);
		if( off > maxlen) maxlen = off;
	}
	public byte readByte() {
		if( off+1 > maxlen) maxlen = off+1;
		return data[off++];
	}
	public short readShort() {
		short val = (short)((data[off]<<8) | data[off+1]);
		off += 2;
		if( off > maxlen) maxlen = off;
		return val;
	}
	public byte[] getBytes() {
		byte[]arr = new byte[off];
		System.arraycopy( data, 0, arr, 0, off );
		if( off > maxlen) maxlen = off;
		return arr;
	}
}
/**
 *  This class contains the processing of RTCP/RTP packet
 *  data for the Javecho application.
 */
public class RTPacket {
	private String callsign;
	private String name;
	private ByteDataStream os;
	private byte[] bytes;
	private String camera;
	private String homepage;
	
	/**  Set the camera URL string to be embedded in our connection packet */
	public void setCameraURL( String str ) {
		camera = str;
	}

	/** Set the home page URL string to be embedded in our connection packet */
	public void setHomepageURL( String str ) {
		homepage = str;
	}

	/** Version Tree of RTP is what we use */
	public static final int RTP_VERSION = 3;

	/** Construct a receive packet object that will use the 
	 *  passed parameters
	 */
	public RTPacket( byte[] arr, int sz ) {
		os = new ByteDataStream(arr,sz);
	}

	/**
	 *  Construct an outbound packet object
	 */
	public RTPacket( String call, String name ) {
		this.callsign = call;
		this.name = name;
		os = new ByteDataStream(512*4);
	}
	
	/**
	 *  Add an SDES element for the indicated type of item
	 *  and content.
	 *  @param item one of the rtcp_sdes_type_t constants
	 *  @param text the text to put into this type item
	 */
	private void addSDES( int item, String text ) {
		os.write( item );
		os.write( text.length() );
		os.writeBytes( text );
	}
	
	/**
	 *  Add an SDES element for the indicated type of item
	 *  and content.
	 *  @param item one of the rtcp_sdes_type_t constants
	 *  @param data the data to put into this type item
	 */
	private void addSDES( int item, byte data[] ) {
		os.write( item );
		os.write( data.length );
		os.write( data );
	}

	/** Get the byte data in the packet (so far) */
	public byte[]getPacketData() {
		return os.getBytes();
	}

	/** Reset the packet content to a 1K buffer */
	public void reset() {
		reset(512*4);
	}

	/** Reset the packet content to the passed size */
	public void reset(int sz) {
		os = new ByteDataStream(sz);
	}

	/**
	 *  Put len as a short value at poff offset in
	 *  the packet
	 */
	private void setLength( int len, int poff ) {
		int off = os.size( );
		os.seek( poff );
		os.writeShort(len);
		os.seek(off);
	}
	
	/**
	 *  Set the length at offset 10 to the passed
	 *  value
	 */
	private void setLength( int len ) {
		setLength( len, 10 );
	}

	/** Encode the passed IP address as a 4 byte integer
	 *  value.  Remember that Java uses signed bytes.  The
	 *  bits will be correct, but the value will be negative
	 *  for networks where the first octet is greater than
	 *  127
	 */
	private int intAddress( String saddr ) {
		try {
		InetAddress addr = InetAddress.getByName( saddr );
		byte[]b = addr.getAddress();
		return 
			((b[0]&0xff) << 24) |
			((b[1]&0xff) << 16) |
			((b[2]&0xff) << 8) |
			((b[3]&0xff) << 0);
		} catch( Exception ex ) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	/**
	 *  Make an SDES packet with the indicated ip address source.
	 *  @param ssrc unused argument
	 *  @param strict build a strictly formmated packet?
	 *  @param data the data to put into the tool item
	 *  @param webPage the webpage URL to send out due to a
	 *         page change by the user during a follow-me web
	 *         sesion.  If set to null, no page item is added
	 */
	public int make_sdes( String ssrc, boolean strict, String data,
			String webPage, String tocall ) {	
//		int ssrc_i = intAddress( ssrc );
	    if (strict) {
	    	os.write( RTP_VERSION << 6 );
			os.write( rtcp_type_t.RTCP_RR );
			os.write( 0 );
			os.write( 1 );
			os.writeInt( 0 );
	    }
		
	    int poff = os.size();
	    os.writeShort( (RTP_VERSION << 14) | rtcp_type_t.RTCP_SDES | (1 << 8) );
	
	    os.writeShort( 0 );
	    os.writeInt( 0 );
	
	    addSDES(rtcp_sdes_type_t.RTCP_SDES_CNAME, tocall );
	
	    String str = callsign;
	    str += "              ".substring(callsign.length()) + name;
	    addSDES(rtcp_sdes_type_t.RTCP_SDES_NAME, str);
	    addSDES(rtcp_sdes_type_t.RTCP_SDES_TOOL, data );
		if( webPage != null ) {
			String msg = "web("+webPage+")";
			addSDES(rtcp_sdes_type_t.RTCP_SDES_PRIV, msg.getBytes() );
		}
	
	    addSDES(rtcp_sdes_type_t.RTCP_SDES_EMAIL, "CALLSIGN");
	 
	    addSDES(rtcp_sdes_type_t.RTCP_SDES_PHONE, "08:30");
	    
	    PropertyManager pm = new PropertyManager();
	    pm.addProperty( "homepage", homepage );
	    pm.addProperty( "camera", camera );
	    byte[] dataa = pm.getData();
	    if( dataa != null )
	    	addSDES( rtcp_sdes_type_t.RTCP_SDES_PRIV, dataa );

	    addSDES( rtcp_sdes_type_t.RTCP_SDES_END, "" );
	
		int l2 = ((os.size() - 8 + 3) / 4) - 1;
		setLength(l2);
	    int l = ((l2 + 1) * 4) + 8;
	
	    /* Okay, if the total length of this packet is not an odd
	       multiple of 4 bytes, we're going to put a pad at the
	       end of it.  Why?  Because we may encrypt the packet
	       later and that requires it be a multiple of 8 bytes,
	       and we don't want the encryption code to have to
	       know all about our weird composite packet structure.
	       Oh yes, there's no reason to do this if strict isn't
	       set, since we never encrypt packets sent to a Look
	       Who's Listening server.
	
	       Why an odd multiple of 4 bytes, I head you ask?
	       Because when we encrypt an RTCP packet, we're required
	       to prefix it with four random bytes to deter a known
	       plaintext attack, and since the total buffer we
	       encrypt, including the random bytes, has to be a
	       multiple of 8 bytes, the message needs to be an odd
	       multiple of 4. */
	
	    if (strict) {
			int pl = ((l & 3)!=0) ? l : l + 4;
//			System.out.println("pl: "+pl+", l: "+l+", size: "+os.size() );
			if (pl > l) {
			    int pad = pl - os.size();
				for( int i = 0; i < pad; ++i )
					os.write(0);
			    pad = pl - l;
				int off = os.size();
				os.seek(poff);
				byte v1 = os.readByte();
				v1 |= 0x20;
				os.seek(poff);
				os.writeByte(v1);
				os.seek(off-1);
				os.writeByte(pad);
				off = os.size();
				os.seek(10);
				short vl = os.readShort();
				os.seek(10);
				vl += (pad/4);
				os.writeShort( vl );
				os.seek(off);
			    l = pl;		      /* Include pad in length of packet */
			}
	    }
//		System.out.println("packet:\n"+os );
		return l;
	}
	
	public static class PropertyManager {
		int len;
		int i;
		Vector<String> names;
		Vector<byte[]> datas;

		public static Hashtable getProperties(byte[]data) {
			int i = 0;
			Hashtable<String,byte[]> h = new Hashtable<String,byte[]>();
			while( i < data.length ) {
				int len = (data[i++] << 8) & 0xff;
				len += data[i++] & 0xff;
				int nl = data[i++];
				String name = new String(data, i, nl );
				i += nl;
				len -= nl + 1;
				byte[]datas = new byte[len];
				System.arraycopy( data, i, datas, 0, len );
				i += len;
				System.out.println( "Found "+name+" property ("+len+")" );
				h.put( name, datas );
			}
			return h;
		}

		public PropertyManager() {
			names = new Vector<String>();
			datas = new Vector<byte[]>();
		}

		public void addProperty( String name, String data ) {
			if( data == null )
				return;
			addProperty( name, data.getBytes() );
		}
		public void addProperty( String name, byte[]data ) {
			if( data == null )
				return;
			names.addElement(name);
			datas.addElement(data);
		}

		public byte[] getData() {
			if( names.size() == 0 )
				return null;
			int len = 0;
			int lens[] = new int[names.size()];
			for( int i = 0; i < names.size(); ++i ) {
				String name = (String)names.elementAt(i);
				byte[]data = (byte[])datas.elementAt(i);
				lens[i] += name.length() + data.length + 1;
				len += name.length() + 3;
				len += data.length;
			}

			byte[]dataa = new byte[len];
//			System.out.println("dataa.length: "+dataa.length );
			int idx = 0;
			for( int i = 0; i < names.size(); ++i ) {
				String name = (String)names.elementAt(i);
//				System.out.println(name+": goes in at: "+idx+", lens: "+lens[i] );
				byte[]data = (byte[])datas.elementAt(i);
				dataa[idx++] = (byte)((lens[i]>>8) & 0xff);
				dataa[idx++] = (byte)(lens[i] & 0xff);
				dataa[idx++] = (byte)name.length();
				System.arraycopy( name.getBytes(), 0, dataa, idx, name.length() );
				idx += name.length();
				System.arraycopy( data, 0, dataa, idx, data.length );
				idx += data.length;
			}
			return dataa;
		}
	}
	
	int addPrivateProperty( byte buf[], byte data[], int i ) {
		buf[i++] = (byte)((data.length >> 8) & 0xff);
		buf[i++] = (byte)(data.length & 0xff);
		System.arraycopy( data, 0, buf, i, data.length );
		i += data.length;
		return i;
	}

	void doPadding( int l ) {
			int pl = ((l & 3)!=0) ? l : l + 4;
			//l = Math.min(l,os.size());
//			System.out.println("pl: "+pl+", l: "+l+", size: "+os.size() );
			if (pl > l) {
			    int pad = pl - os.size();
				for( int i = 0; i < pad; ++i )
					os.write(0);
			    pad = pl - l;
				int off = os.size();
				os.seek(8);
				byte v1 = os.readByte();
				v1 |= 0x20;
				os.seek(8);
				os.writeByte(v1);
				os.seek(off-1);
				os.writeByte(pad);
				off = os.size();
				os.seek(10);
				short vl = os.readShort();
				os.seek(10);
				vl += (pad/4);
				os.writeShort( vl );
				os.seek(off);
			    l = pl;		      /* Include pad in length of packet */
			}
	}
	
	/************* RTP_MAKE_BYE ***************/
	
	int rtp_make_bye( int ssrc_i, String raison, boolean strict)
	//  unsigned long ssrc_i;
	//  char *raison;
	//  int strict;
	{
	//    rtcp_t *rp;
	//    unsigned char *ap, *zp;
	    int l, hl;
	
	    /* If requested, prefix the packet with a null receiver
	       report.  This is required by the RTP spec, but is not
	       required in packets sent only to the Look Who's Listening
	       server. */
	
	       os.seek(0);
	//    zp = p;
	    hl = 0;
	    if (strict) {
	    	os.write( RTP_VERSION << 6 );
	        os.write( rtcp_type_t.RTCP_RR );
	        os.write( 0 );
	        os.write( 1 );
	        os.writeInt( ssrc_i );
	        hl = 8;
	    }
	
	//    rp = (rtcp_t *) p;
	//#ifdef RationalWorld
	//    rp->common.version = RTP_VERSION;
	//    rp->common.p = 0;
	//    rp->common.count = 1;
	//    rp->common.pt = RTCP_BYE;
	//#else
	    os.writeShort( (RTP_VERSION << 14) | rtcp_type_t.RTCP_BYE | (1 << 8) );
	//    *((short *) p) = htons((RTP_VERSION << 14) | RTCP_BYE | (1 << 8));
	//#endif  
	    os.writeInt( ssrc_i );
	    os.writeShort(0);
	//    rp->r.bye.src[0] = htonl(ssrc_i);
	
	//    ap = (unsigned char *) rp->r.sdes.item;
	
	    l = 0;
	    if (raison != null) {
	        l = raison.length();
	        if (l > 0) {
	        	os.write(l);
	            //*ap++ = l;
	        	os.writeBytes( raison );
	//            bcopy(raison, ap, l);
	//            ap += l;
	        }
	    }
	
	    while ( (os.size() & 3) != 0 )
	        os.write( 0 );
	    l = os.size();
	
	    int nl = ((l - hl) / 4) - 1;
	    setLength(nl);
	
	    l = os.size();
	
	    /* If strict, pad the composite packet to an odd multiple of 4
	       bytes so that if we decide to encrypt it we don't have to worry
	       about padding at that point. */
	
	    if (strict) {
	    	doPadding(l);
	    }
	
	    return l;
	}
	
	/*  PARSESDES  --  Look for an SDES message in a possibly composite
			   RTCP packet and extract pointers to selected items
	                   into the caller's structure.  */
	
	/***************************************************/
	
	                  
	static int shortAt( byte[]p, int offset ) {
		return ((p[offset]<<8)&0xff) | ((p[offset+1]&0xff));
	}
	static int intAt( byte[]p, int offset ) {
		return ((p[offset]<<24)&0xff000000) | 
		 ((p[offset+1]<<16)&0xff0000) | 
		 ((p[offset+2]<<8)&0xff00) | ((p[offset+3]&0xff));
	
	}

	public boolean parseSDES(rtcp_sdes_request r)
	  //unsigned char *packet;
	  //struct rtcp_sdes_request *r;
	{
	    int i;
	    boolean success = false;
	//    unsigned char *p = packet;
	
	    /* Initialise all the results in the request packet to NULL. */
	//
	//    for (i = 0; i < r->nitems; i++) {
	//	r->item[i].r_text = NULL;
	//    }
	
	    /* Walk through the individual items in a possibly composite
	       packet until we locate an SDES. This allows us to accept
	       packets that comply with the RTP standard that all RTCP packets
	       begin with an SR or RR. */
	
	       byte p[] = os.getBytes();
	       int base = 0;
//	    	System.out.println("head ("+p.length+"): "+(p[base+0]&0xff));
	    while( base < p.length && (((p[base+0] >> 6) & 3) == RTP_VERSION || ((p[base+0] >> 6) & 3) == 1)) {
//	    	System.out.println("type: "+(p[base+1]&0xff));
			if (( (p[base+1]&0xff) == rtcp_type_t.RTCP_SDES) && ((p[base+0] & 0x1F) > 0)) {
			    int cp = base + 8;
			    int len = shortAt(p, base+2);
				int lp = cp + (len + 1) * 4;
				
	//		    bcopy(p + 4, r->ssrc, 4);
			    r.ssrc = intAt( p, base + 4);
			    while (cp < lp && cp < p.length ) {
					int itype = p[cp]&0xff;
		
//					System.out.println("itype: "+itype );
					if (itype == rtcp_sdes_type_t.RTCP_SDES_END) {
					    break;
					}
		
				/* Search for a match in the request and fill the
				   first unused matching item.	We do it this way to
				   permit retrieval of multiple PRIV items in the same
				   packet. */
		
					for (i = 0; i < r.nitems; i++) {
//						System.out.println("Check if need["+i+"] "+r.item[i].r_item+" == "+itype );
					    if (r.item[i].r_item == itype && r.item[i].r_text == null ) {
//							System.out.println("*** found type: "+itype);
		                    r.item[i].r_text = new byte[len];
		                    System.arraycopy( p, cp+2, r.item[i].r_text, 0, len );
	//	                    r.item[i].r_text = (char *) cp; 
							success = true;
//							break;
				    	}
					}
					if( cp+1 < p.length )
						cp += (p[cp+1]&0xff) + 2;
			    }
		    	break;
//			} else {
//				System.out.println("     check next, this not SDES" );
			}
		    int len = (shortAt(p, base+2)+1) * 4;
//		    System.out.println("next portion "+len+" ahead of "+base );
			base += len;
			/* If not of interest to us, skip to next subpacket. */
	//		p += (ntohs(*((short *) (p + 2))) + 1) * 4;
	    }
	    return success;
	}

	public Vector<rtcp_sdes_request_item> parseSDES() {
	    int i;
	    Vector<rtcp_sdes_request_item> v = new Vector<rtcp_sdes_request_item>();
	    byte p[] = os.getBytes();
	    int base = 0;
	    while( base < p.length && (((p[base+0] >> 6) & 3) == RTP_VERSION || ((p[base+0] >> 6) & 3) == 1)) {
			if (( (p[base+1]&0xff) == rtcp_type_t.RTCP_SDES) && ((p[base+0] & 0x1F) > 0)) {
			    int cp = base + 8;
			    int len = shortAt(p, base+2);
				int lp = cp + (len + 1) * 4;
				
			    while (cp < lp && cp < p.length ) {
					byte itype = p[cp];
			
					rtcp_sdes_request_item r = new rtcp_sdes_request_item();
					v.addElement(r);
					int dlen = p[cp+1];
			
					r.r_text = new byte[dlen];
			        r.r_item = (byte)itype;
			        System.arraycopy( p, cp+2, r.r_text, 0, dlen );
			
					if (itype == rtcp_sdes_type_t.RTCP_SDES_END) {
					    break;
					}
			        if( cp+1 < p.length )
						cp += (p[cp+1]&0xff) + 2;
			    }
				break;
			}
		    int len = (shortAt(p, base+2)+1) * 4;
		    base += len;
	    }
	    return v;
	}
	
	public void dumpSdes()
	  //unsigned char *packet;
	  //struct rtcp_sdes_request *r;
	{
	    int i;
	//    unsigned char *p = packet;
	
	    /* Initialise all the results in the request packet to NULL. */
	//
	//    for (i = 0; i < r->nitems; i++) {
	//	r->item[i].r_text = NULL;
	//    }
	
	    /* Walk through the individual items in a possibly composite
	       packet until we locate an SDES. This allows us to accept
	       packets that comply with the RTP standard that all RTCP packets
	       begin with an SR or RR. */
	
	       byte p[] = os.getBytes();
	       int base = 0;
//	    	System.out.println("head ("+p.length+"): "+(p[base+0]&0xff));
	    while( base < p.length && (((p[base+0] >> 6) & 3) == RTP_VERSION || ((p[base+0] >> 6) & 3) == 1)) {
//	    	System.out.println("type: "+(p[base+1]&0xff));
			if (( (p[base+1]&0xff) == rtcp_type_t.RTCP_SDES) && ((p[base+0] & 0x1F) > 0)) {
			    int cp = base + 8;
			    int len = shortAt(p, base+2);
				int lp = cp + (len + 1) * 4;
				
	//		    bcopy(p + 4, r->ssrc, 4);
			    int idx = 0;
			    System.out.println( "SDES src: "+ intAt( p, base + 4) );

			    while (cp < lp && cp < p.length ) {
					int itype = p[cp]&0xff;
		
//					System.out.println("itype: "+itype );
					if (itype == rtcp_sdes_type_t.RTCP_SDES_END) {
					    break;
					}
		
				/* Search for a match in the request and fill the
				   first unused matching item.	We do it this way to
				   permit retrieval of multiple PRIV items in the same
				   packet. */
		
				      System.out.println( " ["+idx+"]: ("+rtcp_sdes_type_t.types[itype]+") "+(p[cp+1]+2)+" bytes: "+showBytes(p,cp,p[cp+1]+2));
					if( cp+1 < p.length )
						cp += (p[cp+1]&0xff) + 2;
					++idx;
			    }
		    	    break;
//			} else {
//				System.out.println("     check next, this not SDES" );
			}
		    int len = (shortAt(p, base+2)+1) * 4;
//		    System.out.println("next portion "+len+" ahead of "+base );
			base += len;
			/* If not of interest to us, skip to next subpacket. */
	//		p += (ntohs(*((short *) (p + 2))) + 1) * 4;
	    }
	}
	
	String showBytes( byte[]arr, int start, int len ) {
		String str = "";
		for( int i = start; i < start+len; ++i ) {
			if( arr[i] >= 32 && arr[i] <= 127 )
				str += "'"+((char)arr[i])+"' " ;
			else
				str += "0x"+Long.toHexString(arr[i])+" ";
		}
		return str;
	}

	/*************************************/
	/*  COPYSDESITEM  --  Copy an SDES item to a zero-terminated user
	                      string.  */
	
	void copySDESitem( byte s[], byte d[])
	//  char *s, *d;
	{
	    int len = s[1] & 0xFF;
	
	    System.arraycopy( d, 0, s, 2, len );
	//    bcopy(s + 2, d, len);
	    d[len] = 0;
	}
	
	/************************************/
	/*  ISRTCPBYEPACKET  --  Test if this RTCP packet contains a BYE.  */
	
	public  boolean isRTCPByepacket() {
		byte[] b = os.getBytes();
		return isRTCPByepacket( b, b.length );
	}
	public static boolean isRTCPByepacket(byte []p, int len)
	//  unsigned char *p;
	//  int len;
	{
	//    unsigned char *end;
		int end;
		boolean sawbye = false;
//		System.out.println( "p[0]>>6: "+((p[0]>>6)&3)+", p[0] & 0x20: "+((p[0]&0x20))+", p[1]: "+(p[1]&0xff));
	                                                   /* Version incorrect ? */
	    if ((((p[0] >> 6) & 3) != RTP_VERSION && ((p[0] >> 6) & 3) != 1) ||
	        ((p[0] & 0x20) != 0) ||                    /* Padding in first packet ? */
	        (((p[1]&0xff) != rtcp_type_t.RTCP_SR) && ((p[1]&0xff) != rtcp_type_t.RTCP_RR))) { /* First item not SR or RR ? */
	        return false;
	    }
	    end = len;
	
	    int base = 0;
	    do {
	        if ((p[base+1]&0xff) == rtcp_type_t.RTCP_BYE) {
	            sawbye = true;
	        }
	        /* Advance to next subpacket */
		    int tlen = (shortAt(p, base+2)+1) * 4;
		    base += tlen;
	//        p += (ntohs(*((short *) (p + 2))) + 1) * 4;
	    } while (base < end && (((p[base+0] >> 6) & 3) == RTP_VERSION));
	
	    return (sawbye);
	}
	
	/************************************/
	/*  ISRTCPSDESPACKET  --  Test if this RTCP packet contains a BYE.  */
	
	public boolean isRTCPSdespacket() {
		byte[] b = os.getBytes();
		return isRTCPSdespacket( b, b.length );
	}
	public static boolean isRTCPSdespacket(byte[]p, int len)
	//  unsigned char *p;
	//  int len;
	{
	//    unsigned char *end;
		int end;
	    boolean sawsdes = false;
	                                                   /* Version incorrect ? */
	    if ((((p[0] >> 6) & 3) != RTP_VERSION && ((p[0] >> 6) & 3) != 1) ||
	        ((p[0] & 0x20) != 0) ||                    /* Padding in first packet ? */
	        ((p[1] != rtcp_type_t.RTCP_SR) && (p[1] != rtcp_type_t.RTCP_RR))) { /* First item not SR or RR ? */
	        return false;
	    }
	    end = len;
	
	    int base = 0;
	    do {
	        if (p[base+1] == rtcp_type_t.RTCP_SDES) {
	            sawsdes = true;
	        }
	        /* Advance to next subpacket */
		    int tlen = (shortAt(p, base+2)+1) * 4;
		    base += tlen;
	//        p += (ntohs(*((short *) (p + 2))) + 1) * 4;
	    } while (base < end && (((p[base+0] >> 6) & 3) == RTP_VERSION));
	
	    return (sawsdes);
	}
	public static String sdesType( int val ) {
		switch( val ) {
			case rtcp_sdes_type_t.RTCP_SDES_END: return "SDES_END";
			case rtcp_sdes_type_t.RTCP_SDES_CNAME: return "SDES_CNAME";
			case rtcp_sdes_type_t.RTCP_SDES_NAME: return "SDES_NAME";
			case rtcp_sdes_type_t.RTCP_SDES_EMAIL: return "SDES_EMAIL";
			case rtcp_sdes_type_t.RTCP_SDES_PHONE: return "SDES_PHONE";
			case rtcp_sdes_type_t.RTCP_SDES_LOC: return "SDES_LOC";
			case rtcp_sdes_type_t.RTCP_SDES_TOOL: return "SDES_TOOL";
			case rtcp_sdes_type_t.RTCP_SDES_NOTE: return "SDES_NOTE";
			case rtcp_sdes_type_t.RTCP_SDES_PRIV : return "SDES_PRIV";
			case rtcp_sdes_type_t.RTCP_SDES_IMG: return "SDES_IMG";
			case rtcp_sdes_type_t.RTCP_SDES_DOOR: return "SDES_DOOR";
			case rtcp_sdes_type_t.RTCP_SDES_SOURCE: return "SDES_SOURCE";
		}
		return "UNKNOWN: "+val;
	}
}
