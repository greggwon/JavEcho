package org.wonderly.ham.echolink;

import java.net.*;
import java.io.*;
import java.util.*;
import org.wonderly.io.*;
import java.util.prefs.*;
import java.util.logging.*;

public class Parameters {
	List<String> serverList;
	volatile String file;
//	String site = "";
	List<String> servers;
	volatile int netbuffs = 5;
	volatile int pcbuffs = 5;
	volatile int retryTimeout = 10;
	volatile float minPttDownTime = 1.2f;
	volatile boolean userMode = true;
	volatile int connTimeout = 120;
	volatile int pttTimeout = 250;
	volatile int rcvTimeLimit = 0;
	volatile int inactTimeout = 0;
	volatile int rcvHang = 1200;
	volatile boolean fulldup;
	volatile boolean allowMulti;
	volatile int audio = 0;
	List<CountryAccess.CountryEntry> deniedCountries;
	volatile int listUpdIntv = 300;
	volatile int loginIntv = 360;
	volatile boolean stationListAutoUpdate = true;
	volatile boolean updListWhenConn = true;
	volatile boolean repsInStationList = true;
	volatile boolean linksInStationList = true;
	volatile boolean usersInStationList = true;
	volatile boolean confsInStationList = true;
	volatile boolean freeInStationList = true;
	volatile boolean busyInStationList = true;
	volatile boolean alarmedOnlyInStationList;
	volatile boolean onInOnOffList = true;
	volatile boolean offInOnOffList = true;
	volatile boolean freeInFreeBusy = true;
	volatile boolean busyInFreeBusy = true;
	volatile boolean freeInList = true;
	volatile boolean busyInList = true; 
	volatile boolean alarmedOnlyInList;
	List<String> deniedCallsList;
	volatile boolean allowConfs;
	volatile int confCount = 1;
	volatile boolean updLocEntryWithStatus;
	volatile boolean sendStationsToAll;
	volatile String freeStatus="";
	volatile String busyStatus="";
	volatile boolean showConnConf;
	volatile String infoFile;
	volatile boolean acceptFromReps = true;
	volatile boolean acceptFromLinks = true;
	volatile boolean acceptFromUsers = true;
	volatile boolean audTrc;	
	volatile boolean acceptFromConfs = true;
	volatile boolean limitByAcceptedCalls;
	volatile String connSoundFile;
	volatile String discoSoundFile;
	volatile String alarmSoundFile;
	volatile String overSoundFile;
	volatile boolean playConnected;
	volatile boolean playDisconnected;
	volatile boolean playAlarm;
	volatile boolean playOver;
	volatile double audioAmp = 0;
	volatile boolean raiseMuted;
	volatile boolean beeping;
	volatile int dataPort = 5198;
	volatile int controlPort = 5199;
	volatile String latLon = "0000.00N/00000.00W";
	volatile String homepage, camera;
	volatile boolean openHomePage, showCamera;
	volatile boolean sendCurrentPage, followUsersPage, toggleSendFollow;
	volatile int voxLimit;
	final Logger log = Logger.getLogger( "org.wonderly.ham.echolink" );
	volatile int kmlport;
	volatile String kmlbindaddr;
	
	private String 
		call     = "",
		password = "", 
		location = "",
		name     = "",
		email    = "";

	private String infoText = "";

	public static final int ACTION_CONNECT = 0;
	public static final int ACTION_DISCONNECT = 1;
	public static final int ACTION_ALARM = 2;
	public static final int ACTION_OVER = 3;
	boolean islinux;
	boolean holdAudio = true;
	boolean useSelAudio;

	Javecho je;
	public Parameters( Javecho je ) {
		this.je = je;
		String name = System.getProperty("os.name");
		if(name != null && name.equals("Linux"))
			islinux = true;
		serverList = new Vector<String>();
		servers = new Vector<String>();
		deniedCallsList = new Vector<String>();
		deniedCountries = new Vector<CountryAccess.CountryEntry>();
	}

	public String getKMLBindAddress() {
		return kmlbindaddr;
	}
	
	public void setKMLBindAddress( String val ) {
		kmlbindaddr = val;
	}
	
	public int getKMLPort() {
		return kmlport;
	}
	
	public void setKMLPort( int val ) {
		kmlport = val;
	}

	public void setUseSelectedAudio( boolean how ) {
		useSelAudio = how;
	}

	public float getMinPttDownTime() {
		return minPttDownTime;
	}

	public void setMinPttDownTime( float val ) {
		minPttDownTime = val;
	}

	public boolean useSelectedAudio() {
		return useSelAudio;
	}

	public int getLogonInterval() {
		return loginIntv;
	}
	public void setLogonInterval( int secs) {
		loginIntv = secs;
	}

	public int getAudioDevice() {
		return audio;
	}
	public void setAudioDevice( int val ) {
		audio = val;
	}
	public boolean isFollowingUsersPage() {
		return followUsersPage;
	}
	public void setFollowingUsersPage( boolean how ) {
		followUsersPage = how;
	}
	public boolean isSendingCurrentPage() {
		return sendCurrentPage;
	}
	public void setSendingCurrentPage( boolean how ) {
		sendCurrentPage = how;
	}
	public boolean isToggleSendFollow() {
		return toggleSendFollow;
	}
	public void setToggleSendFollow( boolean how ) {
		toggleSendFollow = how;
	}
	public boolean isShowCamera() {
		return showCamera;
	}
	public void setShowCamera(boolean how) {
		showCamera =how;
	}
	public boolean isOpenHomePage() {
		return openHomePage;
	}
	public void setOpenHomePage(boolean how) {
		openHomePage = how;
	}
	public void setHomepageURL( String url ) {
		homepage = url;
	}
	public int getVoxLimit() {
		return voxLimit;
	}
	public void setVoxLimit( int val ) {
		voxLimit = val;
	}
	public boolean isAllowMulti() {
		return allowMulti;
	}
	public void setAllowMulti( boolean how ) {
		allowMulti = how;
	}
	public String getHomepageURL() {
		return homepage;
	}
	public void setCameraURL( String url ) {
		camera = url;
	}
	public String getCameraURL() {
		return camera;
	}

	public void setLatLon( String location ) {
		latLon = location;
	}
	public String getLatLon() {
		return latLon;
	}
	public void setDataPort( int port ) {
		dataPort = port;
	}
	public void setControlPort( int port ) {
		controlPort = port;
	}
	public int getDataPort() {
		return dataPort;
	}
	public int getControlPort() {
		return controlPort;
	}
	public void setBeeping( boolean how ) {
		beeping = how;
	}
	public boolean isBeeping() {
		return beeping;
	}
	public boolean isRaiseOnMutedContact() {
		return raiseMuted;
	}
	public void setRaiseOnMutedContact( boolean how ) {
		raiseMuted = how;
	}
	public double getAudioAmplification() {
		return audioAmp;
	}
	public void setAudioAmplification( double val ) {
		audioAmp = val;
	}
	public boolean isLinux() {
		return islinux;
	}
	public boolean isAudioTrace() {
		return audTrc;
	}
	public void setAudioTrace( boolean how ) {
		audTrc = how;
	}
	public String getDefaultSoundFor( int which ) {
		return null;
	}
	public boolean isStationListUpdateAuto() {
		return stationListAutoUpdate;
	}
	public void setStationListUpdateAuto( boolean upd ) {
		stationListAutoUpdate = upd;
	}
	public int getListUpdateInterval() {
		return listUpdIntv;
	}
	public void setListUpdateInterval( int val ) {
		listUpdIntv = val;
	}
	public boolean isListUpdateWhenConnected() {
		return updListWhenConn;
	}
	public void setListUpdateWhenConnected( boolean how ) {
		updListWhenConn = how;
	}
	public boolean isRepeatersInStationList() {
		return repsInStationList;
	}
	public void setRepeatersInStationList( boolean how ) {
		repsInStationList = how;
	}
	public boolean isLinksInStationList() {
		return linksInStationList;
	}
	public void setLinksInStationList( boolean how ) {
		linksInStationList = how;
	}
	public boolean isUsersInStationList() {
		return usersInStationList;
	}
	public void setUsersInStationList( boolean how ) {
		usersInStationList = how;
	}
	public boolean isConferencesInStationList() {
		return confsInStationList;
	}
	public void setConferencesInStationList( boolean how ) {
		confsInStationList = how;
	}
	public boolean isFreeInStationList() {
		return freeInStationList;
	}
	public void setFreeInStationList( boolean how ) {
		freeInStationList = how;
	}
	public boolean isBusyInStationList() {
		return busyInStationList;
	}
	public void setBusyInStationList( boolean how ) {
		busyInStationList = how;
	}
	public boolean isAlarmedStationsOnlyList() {
		return alarmedOnlyInStationList;
	}
	public void setAlarmedStationsOnlyList( boolean how ) {
		alarmedOnlyInStationList = how;
	}
	public boolean isStationsOnInOnOffList() {
		return onInOnOffList;
	}
	public void setStationsOnInOnOffList( boolean how ) {
		onInOnOffList = how;
	}
	public boolean isStationsOffInOnOffList() {
		return offInOnOffList;
	}
	public void setStationsOffInOnOffList( boolean how ) {
		offInOnOffList = how;
	}
	public boolean isFreeStationsInFreeBusyList() {
		return freeInFreeBusy;
	}
	public void setFreeStationsInFreeBusyList( boolean how ) {
		freeInFreeBusy = how;
	}
	public boolean isBusyStationsInFreeBusyList() {
		return busyInFreeBusy;
	}
	public void setBusyStationsInFreeBusyList( boolean how ) {
		busyInFreeBusy = how;
	}
	public boolean isBusyStationsInList() {
		return busyInList;
	}
	public void setBusyStationsInList( boolean how ) {
		busyInList = how;
	}
	public boolean isFreeStationsInList() {
		return freeInList;
	}
	public void setFreeStationsInList( boolean how ) {
		freeInList = how;
	}
	public boolean isAlarmedStationsOnlyInList() {
		return alarmedOnlyInList;
	}
	public void setAlarmedStationsOnlyInList( boolean how ) {
		alarmedOnlyInList = how;
	}
	public List<String> getDeniedCallsList() {
		return deniedCallsList;
	}
	public void setDeniedCallsList( Vector<String> list ) {
		deniedCallsList = list;
	}
	public boolean isAcceptFromRepeaters() {
		return acceptFromReps;
	}
	public void setAcceptFromRepeaters( boolean how ) {
		acceptFromReps = how;
	}
	public boolean isAcceptFromLinks() {
		return acceptFromLinks;
	}
	public void setAcceptFromLinks( boolean how ) {
		acceptFromLinks = how;
	}
	public boolean isAcceptFromUsers() {
		return acceptFromUsers;
	}
	public void setAcceptFromUsers( boolean how ) {
		acceptFromUsers = how;
	}
	public boolean isAcceptFromConfs() {
		return acceptFromConfs;
	}
	public void setAcceptFromConfs( boolean how ) {
		acceptFromConfs = how;
	}
	public boolean isAcceptOnlyCalls() {
		return limitByAcceptedCalls;
	}
	public void setAcceptOnlyCalls( boolean how ) {
		limitByAcceptedCalls = how;
	}
	public String getCustomSoundForConnected() {
		return connSoundFile;
	}
	public void setCustomSoundForConnected( String name ) {
		connSoundFile = name;
	}
	public String getCustomSoundForDisconnected() {
		return discoSoundFile;
	}
	public void setCustomSoundForDisconnected( String name ) {
		discoSoundFile = name;
	}
	public String getCustomSoundForAlarm() {
		return alarmSoundFile;
	}
	public void setCustomSoundForAlarm( String name ) {
		alarmSoundFile = name;
	}
	public String getCustomSoundForOver() {
		return overSoundFile;
	}
	public void setCustomSoundForOver( String name ) {
		overSoundFile = name;
	}
	public boolean isSoundForConnected() {
		return playConnected;
	}
	public void setSoundForConnected( boolean how ) {
		playConnected = how;
	}
	public boolean isSoundForDisconnected() {
		return playDisconnected;
	}
	public void setSoundForDisconnected( boolean how ) {
		playDisconnected = how;
	}
	public boolean isSoundForAlarm() {
		return playAlarm;
	}
	public void setSoundForAlarm( boolean how ) {
		playAlarm = how;
	}
	public boolean isSoundForOver() {
		return playOver;
	}
	public void setSoundForOver( boolean how ) {
		playOver = how;
	}
	
	public int getReceiveTimeLimit() {
		return rcvTimeLimit;
	}
	public void setReceiveTimeLimit( int how ) {
		rcvTimeLimit = how;
	}
	public int getInactiveTimeout() {
		return inactTimeout;
	}

	public void setInactiveTimeout(int val) {
		inactTimeout = val;
	}

	public int getReceiveHangTimeout() {
		return rcvHang;
	}

	public void setReceiveHangTimeout( int val ) {
		rcvHang = val;
	}

	public void setPTTTimeout( int val ) {
		pttTimeout = val;
	}

	public int getPTTTimeout() {
		return pttTimeout;
	}

	public void setConnectAttemptTimeout( int val ) {
		connTimeout = val;
	}

	public int getConnectAttemptTimeout() {
		return connTimeout;
	}

	public boolean isUserMode() {
		return userMode;
	}

	public void setUserMode( boolean how ) {
		userMode = how;
	}
	
	public List<CountryAccess.CountryEntry> getDeniedCountries() {
		return deniedCountries;
	}
	
	public void setDeniedCountries( List<CountryAccess.CountryEntry> v ) {
		deniedCountries = v;
	}

	public boolean isAllowConferences() {
		return allowConfs;
	}
	public void setAllowConferences( boolean how ) {
		allowConfs = how;
	}
	public int getConferenceCount() {
		return confCount;
	}
	public void setConferenceCount( int val ) {
		confCount = val;
	}
	public boolean isUpdateLocationEntryWithStatus() {
		return updLocEntryWithStatus;
	}
	public void setUpdateLocationEntryWithStatus( boolean how ) {
		updLocEntryWithStatus = how;
	}
	public boolean isStationListSentToAllStations() {
		return sendStationsToAll;
	}
	public void setStationListSentToAllStations( boolean how ) {
		sendStationsToAll = how;
	}
	public String getFreeStatusText() {
		return freeStatus;
	}
	public void setFreeStatusText( String val ) {
		freeStatus = val;
	}
	public String getBusyStatusText() {
		return busyStatus;
	}
	public void setBusyStatusText( String val ) {
		busyStatus = val;
	}
	public boolean isShowNameConnConf() {
		return showConnConf;
	}
	public void setShowNameConnConf( boolean how ) {
		showConnConf = how;
	}
	public String getInfoFileName() {
		return infoFile;
	}
	public void setInfoFileName( String val ) {
		infoFile = val;
	}

	public void saveData() {
		try {
			FileOutputStream os = new FileOutputStream( userFile( "data" ) );
			try {
				ObjectOutputStream oos = new ObjectOutputStream(os);
				try {
					oos.writeInt( 16 ); //version
					oos.writeObject( call );
					oos.writeObject( password );
					oos.writeObject( location );
					oos.writeObject( name );
					oos.writeObject( email );
					oos.writeObject( servers );
					oos.writeInt( netbuffs );
					oos.writeInt( pcbuffs );
					oos.writeInt( retryTimeout );
					oos.writeBoolean( userMode );
					oos.writeInt( connTimeout );
					oos.writeInt( pttTimeout );
					oos.writeInt( inactTimeout );
					oos.writeInt( rcvHang );
//					oos.writeObject( "" );
					oos.writeBoolean( fulldup );
					oos.writeObject( deniedCountries );
					oos.writeInt( listUpdIntv );
					oos.writeBoolean( stationListAutoUpdate );
					oos.writeBoolean( updListWhenConn );
					oos.writeBoolean( repsInStationList );
					oos.writeBoolean( linksInStationList );
					oos.writeBoolean( usersInStationList );
					oos.writeBoolean( confsInStationList );
					oos.writeBoolean( freeInStationList );
					oos.writeBoolean( busyInStationList );
					oos.writeBoolean( alarmedOnlyInStationList );
					oos.writeBoolean( onInOnOffList );
					oos.writeBoolean( offInOnOffList );
					oos.writeBoolean( freeInFreeBusy );
					oos.writeBoolean( busyInFreeBusy );
					oos.writeBoolean( freeInList );
					oos.writeBoolean( busyInList );
					oos.writeBoolean( alarmedOnlyInList );
					oos.writeObject( deniedCallsList );
					oos.writeBoolean( allowConfs );
					oos.writeInt( confCount );
					oos.writeBoolean( updLocEntryWithStatus );
					oos.writeBoolean( sendStationsToAll );
					oos.writeObject( freeStatus );
					oos.writeObject( busyStatus );
					oos.writeBoolean( showConnConf);
					oos.writeObject( infoFile );
					oos.writeBoolean( acceptFromReps );
					oos.writeBoolean( acceptFromLinks );
					oos.writeBoolean( acceptFromUsers );
					oos.writeBoolean( acceptFromConfs );
					oos.writeBoolean( limitByAcceptedCalls );
					oos.writeBoolean( connSoundFile != null );
					if( connSoundFile != null )
						oos.writeObject( connSoundFile );
					oos.writeBoolean( discoSoundFile != null );
					if( discoSoundFile != null )
						oos.writeObject( discoSoundFile );
					oos.writeBoolean( alarmSoundFile != null );
					if( alarmSoundFile != null )
						oos.writeObject( alarmSoundFile );
					oos.writeBoolean( overSoundFile != null );
					if( overSoundFile != null )
						oos.writeObject( overSoundFile );
					oos.writeBoolean( playConnected );
					oos.writeBoolean( playDisconnected );
					oos.writeBoolean( playAlarm );
					oos.writeBoolean( playOver );
					oos.writeBoolean( audTrc );
					oos.writeBoolean( holdAudio );
					oos.writeDouble(audioAmp);
					oos.writeBoolean(raiseMuted);
					oos.writeBoolean(beeping);
					oos.writeInt(dataPort);
					oos.writeInt(controlPort);
					oos.writeObject( latLon );
					oos.writeObject( homepage );
					oos.writeObject( camera );
					oos.writeBoolean( openHomePage );
					oos.writeBoolean( showCamera );
					oos.writeBoolean( sendCurrentPage );
					oos.writeBoolean( followUsersPage );
					oos.writeBoolean( toggleSendFollow );
					oos.writeInt( audio );
					oos.writeBoolean( useSelAudio );
					oos.writeInt( voxLimit );
					oos.writeFloat( minPttDownTime );
					oos.writeInt( kmlport );
					oos.writeObject( kmlbindaddr );
				} finally {
					oos.close();
				}
			} finally {
				os.close();
			}
		} catch( Exception ex ) {
			je.reportException(ex);
		}
		try {
			FileWriter os = new FileWriter( userFile( "info.txt" ) );
			try {
				PrintWriter pw = new PrintWriter( os );
				try {
					pw.println( infoText.trim() );
				} finally {
					pw.close();
				}
			} finally {
				os.close();
			}
		} catch( Exception ex ) {
			je.reportException(ex);
		}
	}

	File userFile( String name ) {
		File d = new File( System.getProperty("user.home"), ".javecho");
		log.info("open: "+name+" in "+d );

		if( d.exists() == false )
			d.mkdirs();
		return new File( d, name );
	}

	public void loadData() {
		try {
			try {
				FileInputStream is = new FileInputStream( userFile( "data" ) );
				try {
					ObjectInputStream iis = new ObjectInputStream(is);
					try {
						int vers = iis.readInt(); //version
						call = (String)iis.readObject();
						password = (String)iis.readObject();
						location = (String)iis.readObject();
						name = (String)iis.readObject();
						email = (String)iis.readObject();
						servers = new VectorReader<String>().read(iis);
						netbuffs = iis.readInt();
						pcbuffs = iis.readInt();
						retryTimeout = iis.readInt();
						userMode = iis.readBoolean();
						if( vers > 0 ) {
							connTimeout = iis.readInt();
							pttTimeout = iis.readInt();
							inactTimeout = iis.readInt();
							rcvHang = iis.readInt();
							if( vers < 12 )
								iis.readObject();
							fulldup = iis.readBoolean();
						}
						if( vers > 1 ) {
							deniedCountries = new VectorReader<CountryAccess.CountryEntry>().read(iis);
							listUpdIntv = iis.readInt();
													
							stationListAutoUpdate = iis.readBoolean();
							updListWhenConn = iis.readBoolean();
							repsInStationList = iis.readBoolean();
							linksInStationList = iis.readBoolean();
							usersInStationList = iis.readBoolean();
							confsInStationList = iis.readBoolean();
							freeInStationList = iis.readBoolean();
							busyInStationList = iis.readBoolean();
							alarmedOnlyInStationList = iis.readBoolean();
							onInOnOffList = iis.readBoolean();
							offInOnOffList = iis.readBoolean();
							freeInFreeBusy = iis.readBoolean();
							busyInFreeBusy = iis.readBoolean();
							freeInList = iis.readBoolean();
							busyInList = iis.readBoolean();
							alarmedOnlyInList = iis.readBoolean();
							deniedCallsList = new VectorReader<String>().read(iis);
							allowConfs = iis.readBoolean();
							confCount = iis.readInt();
							updLocEntryWithStatus = iis.readBoolean();
							sendStationsToAll = iis.readBoolean();
							freeStatus = (String)iis.readObject();
							busyStatus = (String)iis.readObject();
							showConnConf = iis.readBoolean();
							infoFile = (String)iis.readObject();
							acceptFromReps = iis.readBoolean();
							acceptFromLinks = iis.readBoolean();
							acceptFromUsers = iis.readBoolean();
							acceptFromConfs = iis.readBoolean();
							limitByAcceptedCalls = iis.readBoolean();
							connSoundFile = discoSoundFile = 
								alarmSoundFile = overSoundFile = null;
							if( iis.readBoolean() )
								connSoundFile = (String)iis.readObject();
							if( iis.readBoolean() )
								discoSoundFile = (String)iis.readObject();
							if( iis.readBoolean() )
								alarmSoundFile = (String)iis.readObject();
							if( iis.readBoolean() )
								overSoundFile = (String)iis.readObject();
							playConnected = iis.readBoolean();
							playDisconnected = iis.readBoolean();
							playAlarm = iis.readBoolean();
							playOver = iis.readBoolean();
						}
						if( vers > 2 ) {
							audTrc = iis.readBoolean();
						}
						if( vers > 3 ) {
							holdAudio = iis.readBoolean();
						}
						if( vers > 4 ) {
							audioAmp = iis.readDouble();
						}
						if( vers > 5 ) {
							raiseMuted = iis.readBoolean();
						}
						if( vers > 6 ) {
							beeping = iis.readBoolean();
						}
						if( vers > 7 ) {
							dataPort = iis.readInt();
							controlPort = iis.readInt();
						}
						if( vers > 8 ) {
							latLon = (String)iis.readObject();
						}
						if( vers > 9 ) {
							homepage = (String)iis.readObject();
							camera = (String)iis.readObject();
							openHomePage = iis.readBoolean();
							showCamera = iis.readBoolean();
						}
						if( vers > 10 ) {
							sendCurrentPage = iis.readBoolean();
							followUsersPage = iis.readBoolean();
							toggleSendFollow = iis.readBoolean();
						}
						if( vers > 11 ) {
							audio = iis.readInt();
						}
						if( vers > 12 ) {
							useSelAudio = iis.readBoolean();
						}
						if( vers > 13 ) {
							voxLimit = iis.readInt();
						}
						if( vers > 14 ) {
							minPttDownTime = iis.readFloat();
						}
						if( vers > 15 ) {
							kmlport = iis.readInt();
							kmlbindaddr = (String)iis.readObject();
						}
					} finally {
						iis.close();
					}
				} finally {
					is.close();
				}
			} catch( FileNotFoundException ex ) {
			}

			try {
				FileReader rd = new FileReader( userFile( "servers.txt" ) );
				try {
					BufferedReader br = new BufferedReader( rd );
					try {
						String s;
						serverList = new ArrayList<String>();
						while( ( s = br.readLine() ) != null )
							serverList.add( s );
					} finally {
						br.close();
					}
				} finally {
					rd.close();
				}
			} catch( FileNotFoundException ex ) {
				serverList = new ArrayList<String>();
				serverList.add( "nasouth.echolink.org" );
				serverList.add( "naeast.echolink.org" );
				serverList.add( "servers.echolink.org" );
				serverList.add( "backup.echolink.org" );
				serverList.add( "localhost" );
				if( servers.size() == 0 ) {
//					servers.removeAllElements();
					servers.add( "nasouth.echolink.org" );
					servers.add( "naeast.echolink.org" );
					servers.add( "servers.echolink.org" );
					servers.add( "backup.echolink.org" );
					servers.add( "localhost" );
				}
			}
//			servers.removeAllElements();
//			servers.addElement("127.0.0.1");
//			serverList.removeAllElements();
//			serverList.addElement("127.0.0.1");
			try {
				FileReader rd = new FileReader( userFile( "info.txt" ) );
				try {
					BufferedReader br = new BufferedReader( rd );
					try {
						String s;
						infoText = "";
						while( ( s = br.readLine() ) != null )
							infoText += s+"\n";
					} finally {
						br.close();
					}
				} finally {
					rd.close();
				}
			} catch( FileNotFoundException ex ) {
				infoText = "";
			}
		} catch( Exception ex ) {
			je.reportException(ex);
		}
	}

	public List<String> getServers() {
		return servers;
	}

	public int getRetryTimeout() {
		return retryTimeout;
	}

	public void setRetryTimeout( int val ) {
		retryTimeout = val;
	}

	/**
	 *  List of all available servers
	 */
	public List<String> getServerList() {
		return serverList;
	}

	public int getNetBuffering() {
		return netbuffs;
	}

	public int getPCBuffering() {
		return pcbuffs;
	}

	public void setServerN( int n, String srv ) {
		while( n >= servers.size() )
			servers.add(null);
		servers.set(n,srv);
	}

	public String getServerN( int n ) {
		if( n >= servers.size() )
			return null;
		return (String)servers.get(n);
	}

	public void addServer( String srv ) {
		if( servers.contains( srv ) == false )
			servers.add( srv );
	}

	public String getPassword() {
		return password;
	}

	public String getQTH() {
		return location;
	}

	public String getEmail() {
		return email;
	}

	public void setPassword( String pass ) {
		password = pass;
	}

	public void setQTH( String loc ) {
		location = loc;
	}

	public void setEmail( String em ) {
		email = em;
	}
	
	public void setCallSign( String call ) {
		this.call = call;
	}

	public String getCallSign() {
		return call;
	}


	public void setUserName( String name ) {
		this.name = name;
	}

	public String getUserName() {
		return name;
	}

	public String getInfoText() {
		return infoText;
	}
	
	public PTTControlParms getPttControlParms() {
		return new PTTControlParms( isTxCtrlPttVox(),
		isTxCtrlPttAsciiserial() ? PTTControlParms.SerialPttType.ASCII :
		isTxCtrlPttRts() ? PTTControlParms.SerialPttType.RTS :
		PTTControlParms.SerialPttType.DTR, getTxCtrlSerialport(), 
		isTxCtrlKeyonlocal() );
	}

	public void setInfoText( String val ) {
		infoText = val;
	}
	private Preferences curPrefs() {
		Preferences pr = Preferences.userNodeForPackage( SysopSettings.class );
		return pr.node("SysopSettings").node(getCallSign());
	}
	public boolean isDTMFEnableremotepad() {
		return curPrefs().node("DTMF").getBoolean( "enableremotepad", false);
	}
	public void setDTMFEnableremotepad( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "enableremotepad", val );
	}
	public boolean isDTMFDisabled() {
		return curPrefs().node("DTMF").getBoolean( "disabled", false);
	}
	public void setDTMFDisabled( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "disabled", val );
	}
	public int getDTMFDeadkeyprefix() {
		return curPrefs().node("DTMF").getInt( "deadkeyprefix", 0);
	}
	public void setDTMFDeadkeyprefix( int val ) {
		curPrefs().node("DTMF").putInt( "deadkeyprefix", val );
	}
	public int getDTMFMininterdigittime() {
		return curPrefs().node("DTMF").getInt( "mininterdigittime", 0);
	}
	public void setDTMFMininterdigittime( int val ) {
		curPrefs().node("DTMF").putInt( "mininterdigittime", val );
	}
	public boolean isDTMFExternal() {
		return curPrefs().node("DTMF").getBoolean( "external", false);
	}
	public void setDTMFExternal( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "external", val );
	}
	public boolean isDTMFUsedeadkeyprefix() {
		return curPrefs().node("DTMF").getBoolean( "usedeadkeyprefix", false);
	}
	public void setDTMFUsedeadkeyprefix( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "usedeadkeyprefix", val );
	}
	public boolean isDTMFInternal() {
		return curPrefs().node("DTMF").getBoolean( "internal", false);
	}
	public void setDTMFInternal( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "internal", val );
	}
	public boolean isDTMFDisableduringptt() {
		return curPrefs().node("DTMF").getBoolean( "disableduringptt", false);
	}
	public void setDTMFDisableduringptt( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "disableduringptt", val );
	}
	public boolean isDTMFLogall() {
		return curPrefs().node("DTMF").getBoolean( "logall", false);
	}
	public void setDTMFLogall( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "logall", val );
	}
	public boolean isDTMFAutomute() {
		return curPrefs().node("DTMF").getBoolean( "automute", false);
	}
	public void setDTMFAutomute( boolean val ) {
		curPrefs().node("DTMF").putBoolean( "automute", val );
	}
	public int getDTMFSnratio() {
		return curPrefs().node("DTMF").getInt( "snratio", 0);
	}
	public void setDTMFSnratio( int val ) {
		curPrefs().node("DTMF").putInt( "snratio", val );
	}
	public int getDTMFTwistdb() {
		return curPrefs().node("DTMF").getInt( "twistdb", 0);
	}
	public void setDTMFTwistdb( int val ) {
		curPrefs().node("DTMF").putInt( "twistdb", val );
	}
	public int getDTMFFinetuning() {
		return curPrefs().node("DTMF").getInt( "finetuning", 0);
	}
	public void setDTMFFinetuning( int val ) {
		curPrefs().node("DTMF").putInt( "finetuning", val );
	}
	public int getDTMFFreqtolerance() {
		return curPrefs().node("DTMF").getInt( "freqtolerance", 0);
	}
	public void setDTMFFreqtolerance( int val ) {
		curPrefs().node("DTMF").putInt( "freqtolerance", val );
	}
	public String getDTMFActionsConnect() {
		return curPrefs().node("DTMF").node("actions").get( "connect", "");
	}
	public void setDTMFActionsConnect( String val ) {
		curPrefs().node("DTMF").node("actions").put( "connect", val );
	}
	public String getDTMFActionsConnectbycall() {
		return curPrefs().node("DTMF").node("actions").get( "connectbycall", "");
	}
	public void setDTMFActionsConnectbycall( String val ) {
		curPrefs().node("DTMF").node("actions").put( "connectbycall", val );
	}
	public String getDTMFActionsDisconnect() {
		return curPrefs().node("DTMF").node("actions").get( "disconnect", "");
	}
	public void setDTMFActionsDisconnect( String val ) {
		curPrefs().node("DTMF").node("actions").put( "disconnect", val );
	}
	public String getDTMFActionsLinkdown() {
		return curPrefs().node("DTMF").node("actions").get( "linkdown", "");
	}
	public void setDTMFActionsLinkdown( String val ) {
		curPrefs().node("DTMF").node("actions").put( "linkdown", val );
	}
	public String getDTMFActionsLinkup() {
		return curPrefs().node("DTMF").node("actions").get( "linkup", "");
	}
	public void setDTMFActionsLinkup( String val ) {
		curPrefs().node("DTMF").node("actions").put( "linkup", val );
	}
	public String getDTMFActionsListenonlyoff() {
		return curPrefs().node("DTMF").node("actions").get( "listenonlyoff", "");
	}
	public void setDTMFActionsListenonlyoff( String val ) {
		curPrefs().node("DTMF").node("actions").put( "listenonlyoff", val );
	}
	public String getDTMFActionsListenonlyon() {
		return curPrefs().node("DTMF").node("actions").get( "listenonlyon", "");
	}
	public void setDTMFActionsListenonlyon( String val ) {
		curPrefs().node("DTMF").node("actions").put( "listenonlyon", val );
	}
	public String getDTMFActionsPlayinfo() {
		return curPrefs().node("DTMF").node("actions").get( "playinfo", "");
	}
	public void setDTMFActionsPlayinfo( String val ) {
		curPrefs().node("DTMF").node("actions").put( "playinfo", val );
	}
	public String getDTMFActionsProfileselect() {
		return curPrefs().node("DTMF").node("actions").get( "profileselect", "");
	}
	public void setDTMFActionsProfileselect( String val ) {
		curPrefs().node("DTMF").node("actions").put( "profileselect", val );
	}
	public String getDTMFActionsQuerybycall() {
		return curPrefs().node("DTMF").node("actions").get( "querybycall", "");
	}
	public void setDTMFActionsQuerybycall( String val ) {
		curPrefs().node("DTMF").node("actions").put( "querybycall", val );
	}
	public String getDTMFActionsQuerybynode() {
		return curPrefs().node("DTMF").node("actions").get( "querybynode", "");
	}
	public void setDTMFActionsQuerybynode( String val ) {
		curPrefs().node("DTMF").node("actions").put( "querybynode", val );
	}
	public String getDTMFActionsRandomconf() {
		return curPrefs().node("DTMF").node("actions").get( "randomconf", "");
	}
	public void setDTMFActionsRandomconf( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomconf", val );
	}
	public String getDTMFActionsRandomfavconf() {
		return curPrefs().node("DTMF").node("actions").get( "randomfavconf", "");
	}
	public void setDTMFActionsRandomfavconf( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomfavconf", val );
	}
	public String getDTMFActionsRandomfavlink() {
		return curPrefs().node("DTMF").node("actions").get( "randomfavlink", "");
	}
	public void setDTMFActionsRandomfavlink( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomfavlink", val );
	}
	public String getDTMFActionsRandomfavnode() {
		return curPrefs().node("DTMF").node("actions").get( "randomfavnode", "");
	}
	public void setDTMFActionsRandomfavnode( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomfavnode", val );
	}
	public String getDTMFActionsRandomfavuser() {
		return curPrefs().node("DTMF").node("actions").get( "randomfavuser", "");
	}
	public void setDTMFActionsRandomfavuser( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomfavuser", val );
	}
	public String getDTMFActionsRandomlink() {
		return curPrefs().node("DTMF").node("actions").get( "randomlink", "");
	}
	public void setDTMFActionsRandomlink( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomlink", val );
	}
	public String getDTMFActionsRandomnode() {
		return curPrefs().node("DTMF").node("actions").get( "randomnode", "");
	}
	public void setDTMFActionsRandomnode( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomnode", val );
	}
	public String getDTMFActionsRandomuser() {
		return curPrefs().node("DTMF").node("actions").get( "randomuser", "");
	}
	public void setDTMFActionsRandomuser( String val ) {
		curPrefs().node("DTMF").node("actions").put( "randomuser", val );
	}
	public String getDTMFActionsReconnect() {
		return curPrefs().node("DTMF").node("actions").get( "reconnect", "");
	}
	public void setDTMFActionsReconnect( String val ) {
		curPrefs().node("DTMF").node("actions").put( "reconnect", val );
	}
	public String getDTMFActionsStatus() {
		return curPrefs().node("DTMF").node("actions").get( "status", "");
	}
	public void setDTMFActionsStatus( String val ) {
		curPrefs().node("DTMF").node("actions").put( "status", val );
	}
	public int getDTMFShortcutsCount() {
		return curPrefs().node("DTMF").node("shortcuts").getInt( "count", 0);
	}
	public void setDTMFShortcutsCount( int val ) {
		curPrefs().node("DTMF").node("shortcuts").putInt( "count", val );
	}
	public String getDTMFShortcuts0Call() {
		return curPrefs().node("DTMF").node("shortcuts").node("0").get( "call", "");
	}
	public void setDTMFShortcuts0Call( String val ) {
		curPrefs().node("DTMF").node("shortcuts").node("0").put( "call", val );
	}
	public int getDTMFShortcuts0Code() {
		return curPrefs().node("DTMF").node("shortcuts").node("0").getInt( "code", 0);
	}
	public void setDTMFShortcuts0Code( int val ) {
		curPrefs().node("DTMF").node("shortcuts").node("0").putInt( "code", val );
	}
	public boolean isIdentWhileactive() {
		return curPrefs().node("Ident").getBoolean( "whileactive", false);
	}
	public void setIdentWhileactive( boolean val ) {
		curPrefs().node("Ident").putBoolean( "whileactive", val );
	}
	public boolean isIdentEachconnect() {
		return curPrefs().node("Ident").getBoolean( "eachconnect", false);
	}
	public void setIdentEachconnect( boolean val ) {
		curPrefs().node("Ident").putBoolean( "eachconnect", val );
	}
	public boolean isIdentEachdisconnect() {
		return curPrefs().node("Ident").getBoolean( "eachdisconnect", false);
	}
	public void setIdentEachdisconnect( boolean val ) {
		curPrefs().node("Ident").putBoolean( "eachdisconnect", val );
	}
	public boolean isIdentWhileinactive() {
		return curPrefs().node("Ident").getBoolean( "whileinactive", false);
	}
	public void setIdentWhileinactive( boolean val ) {
		curPrefs().node("Ident").putBoolean( "whileinactive", val );
	}
	public boolean isIdentWaitclearfreq() {
		return curPrefs().node("Ident").getBoolean( "waitclearfreq", false);
	}
	public void setIdentWaitclearfreq( boolean val ) {
		curPrefs().node("Ident").putBoolean( "waitclearfreq", val );
	}
	public boolean isIdentEndxmit() {
		return curPrefs().node("Ident").getBoolean( "endxmit", false);
	}
	public void setIdentEndxmit( boolean val ) {
		curPrefs().node("Ident").putBoolean( "endxmit", val );
	}
	public String getIdentAudioFile() {
		return curPrefs().node("Ident").node("audio").get( "file", "");
	}
	public void setIdentAudioFile( String val ) {
		curPrefs().node("Ident").node("audio").put( "file", val );
	}
	public boolean isIdentAudiofileOn() {
		return curPrefs().node("Ident").node("audiofile").getBoolean( "on", false);
	}
	public void setIdentAudiofileOn( boolean val ) {
		curPrefs().node("Ident").node("audiofile").putBoolean( "on", val );
	}
	public int getIdentEndxmitTime() {
		return curPrefs().node("Ident").node("endxmit").getInt( "time", 0);
	}
	public void setIdentEndxmitTime( int val ) {
		curPrefs().node("Ident").node("endxmit").putInt( "time", val );
	}
	public boolean isIdentMorseOn() {
		return curPrefs().node("Ident").node("morse").getBoolean( "on", false);
	}
	public void setIdentMorseOn( boolean val ) {
		curPrefs().node("Ident").node("morse").putBoolean( "on", val );
	}
	public String getIdentMorseId() {
		return curPrefs().node("Ident").node("morse").get( "id", "");
	}
	public void setIdentMorseId( String val ) {
		curPrefs().node("Ident").node("morse").put( "id", val );
	}
	public int getIdentMorsePitch() {
		return curPrefs().node("Ident").node("morse").getInt( "pitch", 0);
	}
	public void setIdentMorsePitch( int val ) {
		curPrefs().node("Ident").node("morse").putInt( "pitch", val );
	}
	public int getIdentMorseVolume() {
		return curPrefs().node("Ident").node("morse").getInt( "volume", 0);
	}
	public void setIdentMorseVolume( int val ) {
		curPrefs().node("Ident").node("morse").putInt( "volume", val );
	}
	public int getIdentMorseSpeed() {
		return curPrefs().node("Ident").node("morse").getInt( "speed", 0);
	}
	public void setIdentMorseSpeed( int val ) {
		curPrefs().node("Ident").node("morse").putInt( "speed", val );
	}
	public boolean isIdentSpeechOn() {
		return curPrefs().node("Ident").node("speech").getBoolean( "on", false);
	}
	public void setIdentSpeechOn( boolean val ) {
		curPrefs().node("Ident").node("speech").putBoolean( "on", val );
	}
	public String getIdentSpeechId() {
		return curPrefs().node("Ident").node("speech").get( "id", "");
	}
	public void setIdentSpeechId( String val ) {
		curPrefs().node("Ident").node("speech").put( "id", val );
	}
	public int getIdentWhileactiveTime() {
		return curPrefs().node("Ident").node("whileactive").getInt( "time", 0);
	}
	public void setIdentWhileactiveTime( int val ) {
		curPrefs().node("Ident").node("whileactive").putInt( "time", val );
	}
	public int getIdentWhileinactiveTime() {
		return curPrefs().node("Ident").node("whileinactive").getInt( "time", 0);
	}
	public void setIdentWhileinactiveTime( int val ) {
		curPrefs().node("Ident").node("whileinactive").putInt( "time", val );
	}
	public int getInfoHaat() {
		return curPrefs().node("Info").getInt( "haat", 0);
	}
	public void setInfoHaat( int val ) {
		curPrefs().node("Info").putInt( "haat", val );
	}
	public String getInfoFrequency() {
		return curPrefs().node("Info").get( "frequency", "");
	}
	public void setInfoFrequency( String val ) {
		curPrefs().node("Info").put( "frequency", val );
	}
	public String getInfoPltone() {
		return curPrefs().node("Info").get( "pltone", "");
	}
	public void setInfoPltone( String val ) {
		curPrefs().node("Info").put( "pltone", val );
	}
	public int getInfoDirectivity() {
		return curPrefs().node("Info").getInt( "directivity", 0);
	}
	public void setInfoDirectivity( int val ) {
		curPrefs().node("Info").putInt( "directivity", val );
	}
	public int getInfoAntennagain() {
		return curPrefs().node("Info").getInt( "antennagain", 0);
	}
	public void setInfoAntennagain( int val ) {
		curPrefs().node("Info").putInt( "antennagain", val );
	}
	public int getInfoPowerout() {
		return curPrefs().node("Info").getInt( "powerout", 0);
	}
	public void setInfoPowerout( int val ) {
		curPrefs().node("Info").putInt( "powerout", val );
	}
	public int getInfoLatDeg() {
		return curPrefs().node("Info").node("Lat").getInt( "deg", 0);
	}
	public void setInfoLatDeg( int val ) {
		curPrefs().node("Info").node("Lat").putInt( "deg", val );
	}
	public String getInfoLatMin() {
		return curPrefs().node("Info").node("Lat").get( "min", "");
	}
	public void setInfoLatMin( String val ) {
		curPrefs().node("Info").node("Lat").put( "min", val );
	}
	public int getInfoLatRegion() {
		return curPrefs().node("Info").node("Lat").getInt( "region", 0);
	}
	public void setInfoLatRegion( int val ) {
		curPrefs().node("Info").node("Lat").putInt( "region", val );
	}
	public String getInfoLonMin() {
		return curPrefs().node("Info").node("Lon").get( "min", "");
	}
	public void setInfoLonMin( String val ) {
		curPrefs().node("Info").node("Lon").put( "min", val );
	}
	public int getInfoLonRegion() {
		return curPrefs().node("Info").node("Lon").getInt( "region", 0);
	}
	public void setInfoLonRegion( int val ) {
		curPrefs().node("Info").node("Lon").putInt( "region", val );
	}
	public int getInfoLonDeg() {
		return curPrefs().node("Info").node("Lon").getInt( "deg", 0);
	}
	public void setInfoLonDeg( int val ) {
		curPrefs().node("Info").node("Lon").putInt( "deg", val );
	}
	public int getInfoReportAPRSUnproto() {
		return curPrefs().node("Info").node("Report").node("APRS").getInt( "unproto", 0);
	}
	public void setInfoReportAPRSUnproto( int val ) {
		curPrefs().node("Info").node("Report").node("APRS").putInt( "unproto", val );
	}
	public boolean isInfoReportAPRSAutoinit() {
		return curPrefs().node("Info").node("Report").node("APRS").getBoolean( "autoinit", false);
	}
	public void setInfoReportAPRSAutoinit( boolean val ) {
		curPrefs().node("Info").node("Report").node("APRS").putBoolean( "autoinit", val );
	}
	public String getInfoReportAPRSComment() {
		return curPrefs().node("Info").node("Report").node("APRS").get( "comment", "");
	}
	public void setInfoReportAPRSComment( String val ) {
		curPrefs().node("Info").node("Report").node("APRS").put( "comment", val );
	}
	public String getInfoReportAPRSTncport() {
		return curPrefs().node("Info").node("Report").node("APRS").get( "tncport", "COM1");
	}
	public void setInfoReportAPRSTncport( String val ) {
		curPrefs().node("Info").node("Report").node("APRS").put( "tncport", val );
	}
	public boolean isInfoReportAPRSOn() {
		return curPrefs().node("Info").node("Report").node("APRS").getBoolean( "on", false);
	}
	public void setInfoReportAPRSOn( boolean val ) {
		curPrefs().node("Info").node("Report").node("APRS").putBoolean( "on", val );
	}
	public boolean isInfoReportAPRSStatustext() {
		return curPrefs().node("Info").node("Report").node("APRS").getBoolean( "statustext", false);
	}
	public void setInfoReportAPRSStatustext( boolean val ) {
		curPrefs().node("Info").node("Report").node("APRS").putBoolean( "statustext", val );
	}
	public boolean isOptionsPlaywelcomeonconnect() {
		return curPrefs().node("Options").getBoolean( "playwelcomeonconnect", false);
	}
	public void setOptionsPlaywelcomeonconnect( boolean val ) {
		curPrefs().node("Options").putBoolean( "playwelcomeonconnect", val );
	}
	public boolean isOptionsPlaycourtesytone() {
		return curPrefs().node("Options").getBoolean( "playcourtesytone", false);
	}
	public void setOptionsPlaycourtesytone( boolean val ) {
		curPrefs().node("Options").putBoolean( "playcourtesytone", val );
	}
	public int getOptionsMaxkeydown() {
		return curPrefs().node("Options").getInt( "maxkeydown", 0);
	}
	public void setOptionsMaxkeydown( int val ) {
		curPrefs().node("Options").putInt( "maxkeydown", val );
	}
	public boolean isOptionsIncludecallsign() {
		return curPrefs().node("Options").getBoolean( "includecallsign", false);
	}
	public void setOptionsIncludecallsign( boolean val ) {
		curPrefs().node("Options").putBoolean( "includecallsign", val );
	}
	public int getOptionsDeadcarrier() {
		return curPrefs().node("Options").getInt( "deadcarrier", 0);
	}
	public void setOptionsDeadcarrier( int val ) {
		curPrefs().node("Options").putInt( "deadcarrier", val );
	}
	public String getOptionsWelcomeMessageFile() {
		return curPrefs().node("Options").get( "welcomeMessageFile", "");
	}
	public void setOptionsWelcomeMessageFile( String val ) {
		curPrefs().node("Options").put( "welcomeMessageFile", val );
	}
	public int getOptionsAnnouncePredelay() {
		return curPrefs().node("Options").node("announce").getInt( "predelay", 0);
	}
	public void setOptionsAnnouncePredelay( int val ) {
		curPrefs().node("Options").node("announce").putInt( "predelay", val );
	}
	public int getOptionsAnnounceContacts() {
		return curPrefs().node("Options").node("announce").getInt( "contacts", 0);
	}
	public void setOptionsAnnounceContacts( int val ) {
		curPrefs().node("Options").node("announce").putInt( "contacts", val );
	}
	public int getOptionsAnnounceDisconnects() {
		return curPrefs().node("Options").node("announce").getInt( "disconnects", 0);
	}
	public void setOptionsAnnounceDisconnects( int val ) {
		curPrefs().node("Options").node("announce").putInt( "disconnects", val );
	}
	public int getOptionsAnnounceMuting() {
		return curPrefs().node("Options").node("announce").getInt( "muting", 0);
	}
	public void setOptionsAnnounceMuting( int val ) {
		curPrefs().node("Options").node("announce").putInt( "muting", val );
	}
	public boolean isOptionsReminderPlay() {
		return curPrefs().node("Options").node("reminder").getBoolean( "play", false);
	}
	public void setOptionsReminderPlay( boolean val ) {
		curPrefs().node("Options").node("reminder").putBoolean( "play", val );
	}
	public int getOptionsReminderIntervalsecs() {
		return curPrefs().node("Options").node("reminder").getInt( "intervalsecs", 0);
	}
	public void setOptionsReminderIntervalsecs( int val ) {
		curPrefs().node("Options").node("reminder").putInt( "intervalsecs", val );
	}
	public String getRemoteDialinPassword() {
		return curPrefs().node("Remote").node("dialin").get( "password", "");
	}
	public void setRemoteDialinPassword( String val ) {
		curPrefs().node("Remote").node("dialin").put( "password", val );
	}
	public boolean isRemoteDialinMonitor() {
		return curPrefs().node("Remote").node("dialin").getBoolean( "monitor", false);
	}
	public void setRemoteDialinMonitor( boolean val ) {
		curPrefs().node("Remote").node("dialin").putBoolean( "monitor", val );
	}
	public String getRemoteDialinPort() {
		return curPrefs().node("Remote").node("dialin").get( "port", "COM1");
	}
	public void setRemoteDialinPort( String val ) {
		curPrefs().node("Remote").node("dialin").put( "port", val );
	}
	public int getRemoteDialinLevel() {
		return curPrefs().node("Remote").node("dialin").getInt( "level", 0);
	}
	public void setRemoteDialinLevel( int val ) {
		curPrefs().node("Remote").node("dialin").putInt( "level", val );
	}
	public int getRemoteDialinTimeout() {
		return curPrefs().node("Remote").node("dialin").getInt( "timeout", 0);
	}
	public void setRemoteDialinTimeout( int val ) {
		curPrefs().node("Remote").node("dialin").putInt( "timeout", val );
	}
	public int getRemoteDialinAnsweronring() {
		return curPrefs().node("Remote").node("dialin").getInt( "answeronring", 0);
	}
	public void setRemoteDialinAnsweronring( int val ) {
		curPrefs().node("Remote").node("dialin").putInt( "answeronring", val );
	}
	public boolean isRemoteDialinActive() {
		return curPrefs().node("Remote").node("dialin").getBoolean( "active", false);
	}
	public void setRemoteDialinActive( boolean val ) {
		curPrefs().node("Remote").node("dialin").putBoolean( "active", val );
	}
	public String getRemoteWebUser() {
		return curPrefs().node("Remote").node("web").get( "user", "");
	}
	public void setRemoteWebUser( String val ) {
		curPrefs().node("Remote").node("web").put( "user", val );
	}
	public int getRemoteWebTcpport() {
		return curPrefs().node("Remote").node("web").getInt( "tcpport", 0);
	}
	public void setRemoteWebTcpport( int val ) {
		curPrefs().node("Remote").node("web").putInt( "tcpport", val );
	}
	public String getRemoteWebPasswd() {
		return curPrefs().node("Remote").node("web").get( "passwd", "");
	}
	public void setRemoteWebPasswd( String val ) {
		curPrefs().node("Remote").node("web").put( "passwd", val );
	}
	public boolean isRemoteWebAccess() {
		return curPrefs().node("Remote").node("web").getBoolean( "access", false);
	}
	public void setRemoteWebAccess( boolean val ) {
		curPrefs().node("Remote").node("web").putBoolean( "access", val );
	}
	public boolean isRxCtrlAntitrip() {
		return curPrefs().node("RxCtrl").getBoolean( "antitrip", false);
	}
	public void setRxCtrlAntitrip( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "antitrip", val );
	}
	public boolean isRxCtrlSerialcd() {
		return curPrefs().node("RxCtrl").getBoolean( "serialcd", false);
	}
	public void setRxCtrlSerialcd( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "serialcd", val );
	}
	public boolean isRxCtrlVox() {
		return curPrefs().node("RxCtrl").getBoolean( "vox", false);
	}
	public void setRxCtrlVox( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "vox", val );
	}
	public int getRxCtrlAntithump() {
		return curPrefs().node("RxCtrl").getInt( "antithump", 0);
	}
	public void setRxCtrlAntithump( int val ) {
		curPrefs().node("RxCtrl").putInt( "antithump", val );
	}
	public int getRxCtrlVoxdelay() {
		return curPrefs().node("RxCtrl").getInt( "voxdelay", 0);
	}
	public void setRxCtrlVoxdelay( int val ) {
		curPrefs().node("RxCtrl").putInt( "voxdelay", val );
	}
	public String getRxCtrlSerialport() {
		return curPrefs().node("RxCtrl").get( "serialport", "COM1");
	}
	public void setRxCtrlSerialport( String val ) {
		curPrefs().node("RxCtrl").put( "serialport", val );
	}
	public boolean isRxCtrlInvertsense() {
		return curPrefs().node("RxCtrl").getBoolean( "invertsense", false);
	}
	public void setRxCtrlInvertsense( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "invertsense", val );
	}
	public boolean isRxCtrlSerialdsr() {
		return curPrefs().node("RxCtrl").getBoolean( "serialdsr", false);
	}
	public void setRxCtrlSerialdsr( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "serialdsr", val );
	}
	public int getRxCtrlDuration() {
		return curPrefs().node("RxCtrl").getInt( "duration", 0);
	}
	public void setRxCtrlDuration( int val ) {
		curPrefs().node("RxCtrl").putInt( "duration", val );
	}
	public boolean isRxCtrlSerialcts() {
		return curPrefs().node("RxCtrl").getBoolean( "serialcts", false);
	}
	public void setRxCtrlSerialcts( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "serialcts", val );
	}
	public boolean isRxCtrlManual() {
		return curPrefs().node("RxCtrl").getBoolean( "manual", false);
	}
	public void setRxCtrlManual( boolean val ) {
		curPrefs().node("RxCtrl").putBoolean( "manual", val );
	}
	public int getRxCtrlClearfreqdelay() {
		return curPrefs().node("RxCtrl").getInt( "clearfreqdelay", 0);
	}
	public void setRxCtrlClearfreqdelay( int val ) {
		curPrefs().node("RxCtrl").putInt( "clearfreqdelay", val );
	}
	public int getSignalsEventsSelected() {
		return curPrefs().node("Signals").node("events").getInt( "selected", 0);
	}
	public void setSignalsEventsSelected( int val ) {
		curPrefs().node("Signals").node("events").putInt( "selected", val );
	}
	public boolean isSignalsEventsActivityReminderDefault() {
		return curPrefs().node("Signals").node("events").node("Activity Reminder").getBoolean( "default", false);
	}
	public void setSignalsEventsActivityReminderDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Activity Reminder").putBoolean( "default", val );
	}
	public String getSignalsEventsActivityReminderMsg() {
		return curPrefs().node("Signals").node("events").node("Activity Reminder").get( "msg", "");
	}
	public void setSignalsEventsActivityReminderMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Activity Reminder").put( "msg", val );
	}
	public boolean isSignalsEventsConnectedDefault() {
		return curPrefs().node("Signals").node("events").node("Connected").getBoolean( "default", false);
	}
	public void setSignalsEventsConnectedDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Connected").putBoolean( "default", val );
	}
	public String getSignalsEventsConnectedMsg() {
		return curPrefs().node("Signals").node("events").node("Connected").get( "msg", "");
	}
	public void setSignalsEventsConnectedMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Connected").put( "msg", val );
	}
	public boolean isSignalsEventsCourtesyToneDefault() {
		return curPrefs().node("Signals").node("events").node("Courtesy Tone").getBoolean( "default", false);
	}
	public void setSignalsEventsCourtesyToneDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Courtesy Tone").putBoolean( "default", val );
	}
	public String getSignalsEventsCourtesyToneMsg() {
		return curPrefs().node("Signals").node("events").node("Courtesy Tone").get( "msg", "");
	}
	public void setSignalsEventsCourtesyToneMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Courtesy Tone").put( "msg", val );
	}
	public boolean isSignalsEventsDisconnectedDefault() {
		return curPrefs().node("Signals").node("events").node("Disconnected").getBoolean( "default", false);
	}
	public void setSignalsEventsDisconnectedDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Disconnected").putBoolean( "default", val );
	}
	public String getSignalsEventsDisconnectedMsg() {
		return curPrefs().node("Signals").node("events").node("Disconnected").get( "msg", "");
	}
	public void setSignalsEventsDisconnectedMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Disconnected").put( "msg", val );
	}
	public boolean isSignalsEventsLinkDownDefault() {
		return curPrefs().node("Signals").node("events").node("Link Down").getBoolean( "default", false);
	}
	public void setSignalsEventsLinkDownDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Link Down").putBoolean( "default", val );
	}
	public String getSignalsEventsLinkDownMsg() {
		return curPrefs().node("Signals").node("events").node("Link Down").get( "msg", "");
	}
	public void setSignalsEventsLinkDownMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Link Down").put( "msg", val );
	}
	public boolean isSignalsEventsLinkUpDefault() {
		return curPrefs().node("Signals").node("events").node("Link Up").getBoolean( "default", false);
	}
	public void setSignalsEventsLinkUpDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Link Up").putBoolean( "default", val );
	}
	public String getSignalsEventsLinkUpMsg() {
		return curPrefs().node("Signals").node("events").node("Link Up").get( "msg", "");
	}
	public void setSignalsEventsLinkUpMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Link Up").put( "msg", val );
	}
	public boolean isSignalsEventsStationInfoDefault() {
		return curPrefs().node("Signals").node("events").node("Station Info").getBoolean( "default", false);
	}
	public void setSignalsEventsStationInfoDefault( boolean val ) {
		curPrefs().node("Signals").node("events").node("Station Info").putBoolean( "default", val );
	}
	public String getSignalsEventsStationInfoMsg() {
		return curPrefs().node("Signals").node("events").node("Station Info").get( "msg", "");
	}
	public void setSignalsEventsStationInfoMsg( String val ) {
		curPrefs().node("Signals").node("events").node("Station Info").put( "msg", val );
	}
	public int getSignalsSpeechSpeed() {
		return curPrefs().node("Signals").node("speech").getInt( "speed", 0);
	}
	public void setSignalsSpeechSpeed( int val ) {
		curPrefs().node("Signals").node("speech").putInt( "speed", val );
	}
	public int getSignalsToneburstDuration() {
		return curPrefs().node("Signals").node("toneburst").getInt( "duration", 0);
	}
	public void setSignalsToneburstDuration( int val ) {
		curPrefs().node("Signals").node("toneburst").putInt( "duration", val );
	}
	public int getSignalsToneburstSend() {
		return curPrefs().node("Signals").node("toneburst").getInt( "send", 0);
	}
	public void setSignalsToneburstSend( int val ) {
		curPrefs().node("Signals").node("toneburst").putInt( "send", val );
	}
	public int getSignalsToneburstFreq() {
		return curPrefs().node("Signals").node("toneburst").getInt( "freq", 0);
	}
	public void setSignalsToneburstFreq( int val ) {
		curPrefs().node("Signals").node("toneburst").putInt( "freq", val );
	}
	public boolean isTxCtrlSerialspeed() {
		return curPrefs().node("TxCtrl").getBoolean( "serialspeed", false);
	}
	public void setTxCtrlSerialspeed( boolean val ) {
		curPrefs().node("TxCtrl").putBoolean( "serialspeed", val );
	}
	public String getTxCtrlSerialport() {
		return curPrefs().node("TxCtrl").get( "serialport", "COM1");
	}
	public void setTxCtrlSerialport( String val ) {
		curPrefs().node("TxCtrl").put( "serialport", val );
	}
	public boolean isTxCtrlKeyonlocal() {
		return curPrefs().node("TxCtrl").getBoolean( "keyonlocal", false);
	}
	public void setTxCtrlKeyonlocal( boolean val ) {
		curPrefs().node("TxCtrl").putBoolean( "keyonlocal", val );
	}
	public boolean isTxCtrlPttRts() {
		return curPrefs().node("TxCtrl").node("ptt").getBoolean( "rts", false);
	}
	public void setTxCtrlPttRts( boolean val ) {
		curPrefs().node("TxCtrl").node("ptt").putBoolean( "rts", val );
	}
	public boolean isTxCtrlPttVox() {
		return curPrefs().node("TxCtrl").node("ptt").getBoolean( "vox", false);
	}
	public void setTxCtrlPttVox( boolean val ) {
		curPrefs().node("TxCtrl").node("ptt").putBoolean( "vox", val );
	}
	public boolean isTxCtrlPttAsciiserial() {
		return curPrefs().node("TxCtrl").node("ptt").getBoolean( "asciiserial", false);
	}
	public void setTxCtrlPttAsciiserial( boolean val ) {
		curPrefs().node("TxCtrl").node("ptt").putBoolean( "asciiserial", val );
	}
	public boolean isTxCtrlPttDtr() {
		return curPrefs().node("TxCtrl").node("ptt").getBoolean( "dtr", false);
	}
	public void setTxCtrlPttDtr( boolean val ) {
		curPrefs().node("TxCtrl").node("ptt").putBoolean( "dtr", val );
	}																				
}