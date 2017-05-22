package org.wonderly.ham.echolink;

//
//struct rtcp_sdes_request {
//    int nitems; 		      /* Number of items requested */
//    unsigned char ssrc[4];	      /* Source identifier */
//    struct rtcp_sdes_request_item item[10]; /* Request items */
//};
public class rtcp_sdes_request {
    public int nitems; 		      /* Number of items requested */
    public int ssrc;	      /* Source identifier */
    public rtcp_sdes_request_item item[]; /* Request items */
    	public rtcp_sdes_request() {
    		this(1);
    	}
    	public rtcp_sdes_request(int nitems) {
    		this.nitems = nitems;
    		item = new rtcp_sdes_request_item[nitems];
    		for( int i =0 ;i < nitems;++i ) {
    			item[i] = new rtcp_sdes_request_item();
    		}
    	}
};
