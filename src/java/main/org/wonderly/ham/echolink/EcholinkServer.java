package org.wonderly.ham.echolink;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.logging.*;
import org.wonderly.awt.*;
import org.wonderly.log.*;
import java.util.Timer;
import org.wonderly.io.*;
import java.rmi.*;

/**
 *  This interface defines the programatic access to the echolink
 *  servers that is possible using Jini/Java RMI
 */
public interface EcholinkServer extends Remote {
}