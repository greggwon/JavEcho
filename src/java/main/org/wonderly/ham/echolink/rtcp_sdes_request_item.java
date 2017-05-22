package org.wonderly.ham.echolink;

//struct rtcp_sdes_request_item {
//    unsigned char r_item;
//    char *r_text;
//};
public class rtcp_sdes_request_item {
    public byte r_item;
    public byte r_text[];
    public rtcp_sdes_request_item() {
//    	r_text = new byte[200];
    }
    public rtcp_sdes_request_item(int len) {
    	r_text = new byte[len];
    }
};
