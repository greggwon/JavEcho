package org.wonderly.ham.echolink;

import java.util.*;
import java.util.logging.*;

public class CountryAccess {
	static ArrayList<CountryEntry> countries;
	static ArrayList<ZoneEntry> zones;
	static Hashtable<String,String> conts = new Hashtable<String,String>();
	static Logger log = Logger.getLogger( CountryAccess.class.getName() );
	
	public static void main( String args[] ) {
		System.out.println("match: "+CountryAccess.entryForCall( "W5BBS-R" ) );
		System.out.println("match: "+CountryAccess.entryForCall( "6K5XCB" ) );
		System.out.println("match: "+CountryAccess.entryForCall( "6M0KK-R" ) );
		System.out.println("match: "+CountryAccess.entryForCall( "UR0VS" ) );
		System.out.println("match: "+CountryAccess.entryForCall( "RU0ACM" ) );
		System.out.println("match: "+CountryAccess.entryForCall( "UT0VA" ) );
		System.out.println("match: "+CountryAccess.entryForCall( "UY9IA" ) );
		System.exit(0);
		
		Iterator i = countries.iterator();
		while(i.hasNext() ) {
			CountryEntry ce = (CountryEntry)i.next();
			Iterator z = zones.iterator();
			boolean found = false;
			while( z.hasNext() ) {
				ZoneEntry ze = (ZoneEntry)z.next();
				if( ce.name.equals(ze.name) ) {
					found = true;
					break;
				}
			}
			if( !found ) {
				System.out.println("no zone info for: "+ce.name );
			}
		}
	}

	public static String continentFor( String call ) {
		CountryEntry ce = entryFor( call );
		if( ce == null )
			return null;
		return ce.zone.continent;
	}

	public static CountryEntry entryFor( String str ) {
//		for( int i=0; i < countries.size(); ++i ) {
//			CountryEntry ent = (CountryEntry)countries.get(i);
//			if( ent.name.equals(str) )
//				return ent;
//		}
		
		int idx = Arrays.binarySearch( countries.toArray(), str, new Comparator<Object>() {
			public int compare(Object a1, Object a2) {
				if( a1 instanceof String ) {
					return ((CountryEntry)a2).name.compareTo( (String)a1 );
				} else if( a2 instanceof String ) {
					return ((CountryEntry)a1).name.compareTo( (String)a2 );
				} else {
					throw new ClassCastException( "Searching for String, found: "+
						a1.getClass().getName()+", "+a2.getClass().getName() );
				}
			}
		});
		
		if( idx >= 0 )
			return ((CountryEntry)countries.get(idx));
		
		return null;
	}		
	
	public static String countryFor( String call ) {
		CountryEntry ce = entryForCall( call );
		if( ce == null )
			return null;
		return ce.name;
	}
	
	public static CountryEntry entryForCall( String call ) {
		log.fine("Find country for: "+call);
		for( int i = 0; i < countries.size(); ++i ) {
			CountryEntry ce = (CountryEntry)countries.get(i);
			log.fine("Check country: "+ce );
			// Can we find this country by call?
			if( ce.prefs == null ) {
				log.finer("No prefixes for: "+ce );
				continue;
			}
			for( int j = 0; j < ce.prefs.length; ++j ) {
				String s[] = ce.prefs[j].split(",");
				for( int k = 0; k < s.length; ++k )
					log.finer("prefix["+k+"]: "+s[k]);
				if( s.length < 2 )
					log.severe("Bad entry in: "+ce);

				String a = "";
				while( s[0].endsWith(a+"A") && s[0].length() - a.length() > 1 )
					a += "A";

				String bcall = call;
				if( call.length() > s[0].length() )
					bcall = call.substring(0, s[0].length());
				else
					continue;

				if( s.length < 2 ) {
					throw new IllegalArgumentException( ce+" not configured correctly");
				}
				log.finer("comparing prefix order: bcall: "+bcall+", s[0]: "+s[0]);
				if( bcall.compareTo( s[1] ) > 0 )
					continue;

				log.finest("Using prefix: s[0]=\""+s[0]+"\", a=\""+a+"\"");
				if( s[0].endsWith( a ) ) {
					s[0] = s[0].substring(0,s[0].length()-a.length());
				} else {
					s[0] = s[0].substring(0,s[0].length()-(s[0].length() > 1 ? 1 : 0));//a.length());
				}

				log.finest("Trimmed to: s[0]=\""+s[0]+"\"");
				while( s.length > 1 && s[1].startsWith(s[0]+"A") )
					s[0] += "A";

				log.finest("Ending Prefix: "+s[1]);
				String tcall = call;
				if( call.length() > s[0].length() )
					tcall = call.substring(0, s[0].length());
//				System.out.println(call+": s[0] ("+tcall.compareTo(s[0])+"): "+s[0]+", s[1] ("+tcall.compareTo( s[1] )+"): "+s[1]+", ce: "+ce ); //+", call seg: "+ call.substring(0,s[0].length()+a.length())+", ce: "+ce);

				log.finer("Compare: tcall=\""+tcall+
					"\", startpref=\""+s[0]+"\" ("+tcall.compareTo(s[0])+
					"), endpref\""+s[1]+"\" ("+tcall.compareTo( s[1] )+")" );
				if( s.length > 1 && tcall.compareTo(s[0]) >= 0 && tcall.compareTo( s[1] ) <= 0 ) {
					if( call.startsWith("E20") ) {
						if( ce.name.equals("Spain") != true )
							return ce;
					} else {
						log.fine("Found entry: "+ce );
						return ce;
					}
				}
			}
		}
		return null;
	}

	public static class CountryEntry implements Comparable<CountryEntry> {
		String[]prefs;
		String name;
		ZoneEntry zone;
		boolean allow = true;
		
		public int compareTo( CountryEntry obj ) {
			return name.compareTo( obj.name );
		}

		public String toString() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode();
		}

		public CountryEntry( String[]prefs, String name ) {
			this.prefs = prefs;
			this.name = name;
		}
	}

	public static class ZoneEntry implements Comparable<ZoneEntry> {
		String name;
		String continent;
		String itu;
		String cq;
		
		public int compareTo( ZoneEntry obj ) {
			return name.compareTo( obj.name );
		}

		public String toString() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode();
		}

		public ZoneEntry( String country, String cont, String itus, String cqs ) {
			this.name = country;
			this.continent = cont;
			this.itu = itus;
			this.cq = cqs;
		}
	}
	
	public static Vector<CountryEntry> getCountries() {
		return new Vector<CountryEntry>(countries);
	}
	
	public static void addCountry( CountryEntry ent ) {
		countries.add(ent);
	}
	
	public static Enumeration<String> continents() {
		return conts.elements();
	}

	public static void addZone( ZoneEntry ent ) {
		conts.put( ent.continent, ent.continent );
		CountryEntry ce = entryFor( ent.name );
		if( ce != null )
			ce.zone = ent;
		else {
			System.out.println("\t\taddCountry( new CountryEntry( new String[]{ \"\"}, \""+ent.name+"\") );");
			ce = new CountryEntry( null, ent.name );
			ce.zone = ent;
			addCountry(ce);
		}
		zones.add(ent);
	}
		
	static {
		countries = new ArrayList<CountryEntry>(200);
		addCountry( new CountryEntry(new String[]{"Z2A,Z2Z"},"Zimbabwe") );
		addCountry( new CountryEntry(new String[]{"9IA,9JZ"},"Zambia") );
		addCountry( new CountryEntry(new String[]{"YTA,YUZ","YZA,YZZ","4NA,4OZ"},"Yugoslavia") );
		addCountry( new CountryEntry(new String[]{"7OA,7OZ"},"Yemen") );
		addCountry( new CountryEntry(new String[]{"5WA,5WZ"},"Samoa") );
		addCountry( new CountryEntry(new String[]{"XVA,XVZ","3WA,3WZ"},"Vietnam") );
		addCountry( new CountryEntry(new String[]{"YVA,YYZ","4MA,4MZ"},"Venezuela") );
		addCountry( new CountryEntry(new String[]{"HVA,HVZ"},"Vatican") );
		addCountry( new CountryEntry(new String[]{"YJA,YJZ"},"Vanuatu") );
		addCountry( new CountryEntry(new String[]{"UJA,UMZ"},"Uzbekistan") );
		addCountry( new CountryEntry(new String[]{"CVA,CXZ"},"Uruguay") );
		addCountry( new CountryEntry(new String[]{"WH0,WH"},"Mariana Is.") );
		addCountry( new CountryEntry(new String[]{"WH1,WH1"},"Baker Howland") );
		addCountry( new CountryEntry(new String[]{"WH2,WH2"},"Guam") );
		addCountry( new CountryEntry(new String[]{"WH3,WH3"},"Johnston Is.") );
		addCountry( new CountryEntry(new String[]{"WH4,WH4"},"Midway Is.") );
		addCountry( new CountryEntry(new String[]{"WH5A,WH5J","WH5L,WH5Z"},"Palmyra Is.") );
		addCountry( new CountryEntry(new String[]{"WH5K,WH5K"},"Kingman Reef") );
		addCountry( new CountryEntry(new String[]{"WH6A,WH6Z","WH7A,WH7J","WH7L,WH7Z"},"Hawaii Is.") );
		addCountry( new CountryEntry(new String[]{"WH8,WH8"},"American Samoa") );
		addCountry( new CountryEntry(new String[]{"WH9,WH9"},"Wake Is.") );
		addCountry( new CountryEntry(new String[]{"WP1,WP1"},"Navassa Is.") );
		addCountry( new CountryEntry(new String[]{"WP2,WP2"},"Virgin Is.") );
		addCountry( new CountryEntry(new String[]{"WP5,WP5"},"Desecheo Is.") );
		addCountry( new CountryEntry(new String[]{"AAA,ALZ","KAA,KZZ","NAA,NZZ","WAA,WZZ"},"United States") );
		addCountry( new CountryEntry(new String[]{"4UA,4UZ","4WA,4WZ"},"United Nations") );
		addCountry( new CountryEntry(new String[]{ "G,G","GX,GX","GAA,GZZ","MAA,MZZ","VPA,VQZ","VSA,VSZ","ZBA,ZJZ","ZNA,ZOZ","ZQA,ZQZ","2AA,2ZZ"},"United Kingdom") );		
		addCountry( new CountryEntry(new String[]{"A6A,A6Z"},"United Arab Em.") );
//		addCountry( new CountryEntry(new String[]{"EMA,EOZ","URA,UZZ"},"Ukraine") );
		addCountry( new CountryEntry(new String[]{"5XA,5XZ"},"Uganda") );
		addCountry( new CountryEntry(new String[]{"T2A,T2Z"},"Tuvalu") );
		addCountry( new CountryEntry(new String[]{"EZA,EZZ"},"Turkmenistan") );
		addCountry( new CountryEntry(new String[]{"TAA,TCZ","YMA,YMZ"},"Turkey") );
		addCountry( new CountryEntry(new String[]{"TSA,TSZ","3VA,3VZ"},"Tunisia") );
		addCountry( new CountryEntry(new String[]{"9YA,9ZZ"},"Trinidad & Tobago") );
		addCountry( new CountryEntry(new String[]{"A3A,A3Z"},"Tonga") );
		addCountry( new CountryEntry(new String[]{"5VA,5VZ"},"Togo") );
		addCountry( new CountryEntry(new String[]{"Z3A,Z3Z"},"Macedonia") );
		addCountry( new CountryEntry(new String[]{"E2A,E2Z","E2O,E2O","HSA,HSZ"},"Thailand") );
		addCountry( new CountryEntry(new String[]{"5HA,5IZ"},"Tanzania") );
		addCountry( new CountryEntry(new String[]{"EYA,EYZ"},"Tajikistan") );
		addCountry( new CountryEntry(new String[]{"BVA,BVZ"},"Taiwan") );
		addCountry( new CountryEntry(new String[]{"YKA,YKZ","6CA,6CZ"},"Syria") );
		addCountry( new CountryEntry(new String[]{"HBA,HBZ","HEA,HEZ"},"Switzerland") );
		addCountry( new CountryEntry(new String[]{"SAA,SMZ","7SA,7SZ","8SA,8SZ"},"Sweden") );
		addCountry( new CountryEntry(new String[]{"3DA,3DM"},"Swaziland") );
		addCountry( new CountryEntry(new String[]{"PZA,PZZ"},"Suriname") );
		addCountry( new CountryEntry(new String[]{"SSN,STZ","6TA,6UZ"}, "Sudan") );
		addCountry( new CountryEntry(new String[]{"4PA,4SZ"}, "Sri Lanka") );
		addCountry( new CountryEntry(new String[]{"AMA,AOZ","EAA,EAZ","EBA,EBZ","ECA,ECZ","EDA,EDZ","EEA,EEZ","EFA,EFZ","EGA,EGZ","EHA,EHZ"},"Spain") );
		addCountry( new CountryEntry(new String[]{"S8A,S8Z","ZRA,ZUZ"},"South Africa") );
		addCountry( new CountryEntry(new String[]{"T5A,T5Z","6OA,6OZ"},"Somalia") );
		addCountry( new CountryEntry(new String[]{"H4A,H4Z"},"Solomon Is.") );
		addCountry( new CountryEntry(new String[]{"S5A,S5Z"},"Slovenia") );
		addCountry( new CountryEntry(new String[]{"OMA,OMZ"},"Slovak Republic") );
		addCountry( new CountryEntry(new String[]{"S6A,S6Z","9VA,9VZ"},"Singapore") );
		addCountry( new CountryEntry(new String[]{"9LA,9LZ"},"Sierra Leone") );
		addCountry( new CountryEntry(new String[]{"S7A,S7Z"},"Seychelles") );
		addCountry( new CountryEntry(new String[]{"6VA,6WZ"},"Senegal") );
		addCountry( new CountryEntry(new String[]{"HZA,HZZ","7ZA,7ZZ","8ZA,8ZZ"},"Saudi Arabia") );
		addCountry( new CountryEntry(new String[]{"S9A,S9Z"},"Sao Tome & Principe") );
		addCountry( new CountryEntry(new String[]{"T7A,T7Z"},"San Marino") );
		addCountry( new CountryEntry(new String[]{"J8A,J8Z"},"St. Vincent") );
		addCountry( new CountryEntry(new String[]{"J6A,J6Z"},"St. Lucia") );
		addCountry( new CountryEntry(new String[]{"V4A,V4Z"},"St. Kitts & Nevis") );
		addCountry( new CountryEntry(new String[]{"9XA,9XZ"},"Rwanda") );
		addCountry( new CountryEntry(new String[]{"RAA,RZZ","UAA,UIZ"},"Russian Fed.") );
		addCountry( new CountryEntry(new String[]{"YOA,YRZ"},"Romania") );
		addCountry( new CountryEntry(new String[]{"A7A,A7Z"},"Qatar") );
		addCountry( new CountryEntry(new String[]{"CQA,CQZ","CRA,CRZ","CSA,CSZ","CTA,CTZ","CUA,CUZ","XXA,XXZ"},"Portugal") );
		addCountry( new CountryEntry(new String[]{"HFA,HFZ","SNA,SRZ","3ZA,3ZZ"},"Poland") );
		addCountry( new CountryEntry(new String[]{"DUA,DZZ","4DA,4IZ"},"Philippines") );
		addCountry( new CountryEntry(new String[]{"OAA,OCZ","4TA,4TZ"},"Peru") );
		addCountry( new CountryEntry(new String[]{"ZPA,ZPZ"},"Paraguay") );
		addCountry( new CountryEntry(new String[]{"P2A,P2Z"},"Papua New Guinea") );
		addCountry( new CountryEntry(new String[]{"HP1,HP1","HOA,HPZ","H3A,H3Z","H8A,H9Z","3EA,3FZ"},"Panama") );
		addCountry( new CountryEntry(new String[]{"E4A,E4Z"},"Palestine") );
		addCountry( new CountryEntry(new String[]{"T8A,T8Z"},"Palau") );
		addCountry( new CountryEntry(new String[]{"APA,ASZ","6PA,6SZ"},"Pakistan") );
		addCountry( new CountryEntry(new String[]{"A4A,A4Z"},"Oman") );
		addCountry( new CountryEntry(new String[]{"JWA,JXZ","LAA,LNZ","3YA,3YZ"},"Norway") );
		addCountry( new CountryEntry(new String[]{"5NA,5OZ"},"Nigeria") );
		addCountry( new CountryEntry(new String[]{"5UA,5UZ"},"Niger") );
		addCountry( new CountryEntry(new String[]{"HTA,HTZ","H6A,H7Z","YNA,YNZ"},"Nicaragua") );
		addCountry( new CountryEntry(new String[]{"ZKA,ZMZ"},"New Zealand") );
		addCountry( new CountryEntry(new String[]{"PAA,PIZ","PJA,PJZ"},"Netherlands") );
		addCountry( new CountryEntry(new String[]{"9NA,9NZ"},"Nepal") );
		addCountry( new CountryEntry(new String[]{"C2A,C2Z"},"Nauru") );
		addCountry( new CountryEntry(new String[]{"V5A,V5Z"},"Namibia") );
		addCountry( new CountryEntry(new String[]{"XYA,XZZ"},"Myanmar") );
		addCountry( new CountryEntry(new String[]{"C8A,C9Z"},"Mozambique") );
		addCountry( new CountryEntry(new String[]{"CNA,CNZ","5CA,5GZ"},"Morocco") );
		addCountry( new CountryEntry(new String[]{"JTA,JVZ"},"Mongolia") );
		addCountry( new CountryEntry(new String[]{"3AA,3AZ"},"Monaco") );
		addCountry( new CountryEntry(new String[]{"ERA,ERZ"},"Moldovia") );
		addCountry( new CountryEntry(new String[]{"V6A,V6Z"},"Micronesia") );
		addCountry( new CountryEntry(new String[]{"XAA,XIZ","4AA,4CZ","6DA,6JZ"}, "Mexico") );
		addCountry( new CountryEntry(new String[]{"3BA,3BZ"},"Mauritius") );
		addCountry( new CountryEntry(new String[]{"5TA,5TZ"},"Mauritania") );
		addCountry( new CountryEntry(new String[]{"V7A,V7Z"},"Marshall Is.") );
		addCountry( new CountryEntry(new String[]{"9HA,9HZ"},"Malta") );
		addCountry( new CountryEntry(new String[]{"TZA,TZZ"},"Mali") );
		addCountry( new CountryEntry(new String[]{"8QA,8QZ"},"Maldives") );
		addCountry( new CountryEntry(new String[]{"9MA,9MZ","9WA,9WZ"},"East Malaysia") );
		addCountry( new CountryEntry(new String[]{"9MA,9MZ","9WA,9WZ"},"West Malaysia") );
		addCountry( new CountryEntry(new String[]{"7QA,7QZ"},"Malawi") );
		addCountry( new CountryEntry(new String[]{"5RA,5SZ","6XA,6XZ"},"Madagascar") );
		addCountry( new CountryEntry(new String[]{"LXA,LXZ"},"Luxembourg") );
		addCountry( new CountryEntry(new String[]{"LYA,LYZ"},"Lithuania") );
		addCountry( new CountryEntry(new String[]{"5AA,5AZ"},"Libya") );
		addCountry( new CountryEntry(new String[]{"A8A,A8Z","D5A,D5Z","ELA,ELZ","5LA,5MZ","6ZA,6ZZ"},"Liberia") );
		addCountry( new CountryEntry(new String[]{"7PA,7PZ"},"Lesotho") );
		addCountry( new CountryEntry(new String[]{"ODA,ODZ"},"Lebanon") );
		addCountry( new CountryEntry(new String[]{"YLA,YLZ"},"Latvia") );
		addCountry( new CountryEntry(new String[]{"XWA,XWZ"},"Laos") );
		addCountry( new CountryEntry(new String[]{"EXA,EXZ"},"Kyrgyzstan") );
		addCountry( new CountryEntry(new String[]{"9KA,9KZ"},"Kuwait") );
		addCountry( new CountryEntry(new String[]{"DSA,DTZ","D7A,D9Z","HLA,HLZ","6KA,6NZ"},"Republic of Korea") );
		addCountry( new CountryEntry(new String[]{"T30,T30"},"W. Kiribati (Gilbert Is.)") );
		addCountry( new CountryEntry(new String[]{"T31,T31"},"C. Kiribati (British  Phoenix Is.)") );
		addCountry( new CountryEntry(new String[]{"T32,T32"},"E. Kiribati (Line Is.)") );
		addCountry( new CountryEntry(new String[]{"5YA,5ZZ"},"Kenya") );
		addCountry( new CountryEntry(new String[]{"UNA,UQZ"},"Kazakhstan") );
		addCountry( new CountryEntry(new String[]{"JYA,JYZ"},"Jordan") );
		addCountry( new CountryEntry(new String[]{"JAA,JSZ","7JA,7NZ","8JA,8NZ"},"Japan") );
		addCountry( new CountryEntry(new String[]{"6YA,6YZ"},"Jamaica") );
		addCountry( new CountryEntry(new String[]{"IAA,IZZ"},"Italy") );
		addCountry( new CountryEntry(new String[]{"4XA,4XZ","4ZA,4ZZ"},"Israel") );
		addCountry( new CountryEntry(new String[]{"EIA,EJZ"},"Ireland") );
		addCountry( new CountryEntry(new String[]{"HNA,HNZ","YIA,YIZ"},"Iraq") );
		addCountry( new CountryEntry(new String[]{"EPA,EQZ","9BA,9DZ"},"Iran") );
		addCountry( new CountryEntry(new String[]{"JZA,JZZ","PKA,POZ","YBA,YHZ","7AA,7IZ","8AA,8IZ"},"Indonesia") );
		addCountry( new CountryEntry(new String[]{"ATA,AWZ","VTA,VWZ","8TA,8YZ"},"India") );
		addCountry( new CountryEntry(new String[]{"TFA,TFZ"},"Iceland") );
		addCountry( new CountryEntry(new String[]{"HAA,HAZ","HGA,HGZ"},"Hungary") );
		addCountry( new CountryEntry(new String[]{"HQA,HRZ"},"Honduras") );
		addCountry( new CountryEntry(new String[]{"HHA,HHZ","4VA,4VZ"},"Haiti") );
		addCountry( new CountryEntry(new String[]{"8RA,8RZ"},"Guyana") );
		addCountry( new CountryEntry(new String[]{"J5A,J5Z"},"Guinea-Bissau") );
		addCountry( new CountryEntry(new String[]{"3XA,3XZ"},"Guinea") );
		addCountry( new CountryEntry(new String[]{"TDA,TDZ","TGA,TGZ"},"Guatemala") );
		addCountry( new CountryEntry(new String[]{"J3A,J3Z"},"Grenada") );
		addCountry( new CountryEntry(new String[]{"J4A,J4Z","SVA,SZZ"},"Greece") );
		addCountry( new CountryEntry(new String[]{"9GA,9GZ"},"Ghana") );
		addCountry( new CountryEntry(new String[]{"DAA,DRZ","Y2A,Y9Z"},"Germany") );
		addCountry( new CountryEntry(new String[]{"4LA,4LZ"},"Georgia") );
		addCountry( new CountryEntry(new String[]{"C5A,C5Z"},"Gambia") );
		addCountry( new CountryEntry(new String[]{"TRA,TRZ"},"Gabon") );
		addCountry( new CountryEntry(new String[]{"FAA,FZZ","HWA,HYZ","THA,THZ","TKA,TKZ","TMA,TMZ","TOA,TQZ","TVA,TXZ"},"France") );
		addCountry( new CountryEntry(new String[]{"OFA,OJZ"},"Finland") );
		addCountry( new CountryEntry(new String[]{"3DN,3DZ"},"Fiji") );
		addCountry( new CountryEntry(new String[]{"ETA,ETZ","9EA,9FZ"},"Ethiopia") );
		addCountry( new CountryEntry(new String[]{"ESA,ESZ"},"Estonia") );
		addCountry( new CountryEntry(new String[]{"E3A,E3Z"},"Eritrea") );
		addCountry( new CountryEntry(new String[]{"3CA,3CZ"},"Equatorial Guinea") );
		addCountry( new CountryEntry(new String[]{"HUA,HUZ","YSA,YSZ"},"El Salvador") );
		addCountry( new CountryEntry(new String[]{"SSA,SSM","SUA,SUZ","6AA,6AZ", "6BA,6BZ"},"Egypt") );
		addCountry( new CountryEntry(new String[]{"HCA,HDZ"},"Ecuador") );
		addCountry( new CountryEntry(new String[]{"HIA,HIZ"},"Dominican Republic") );
		addCountry( new CountryEntry(new String[]{"J7A,J7Z"},"Dominica") );
		addCountry( new CountryEntry(new String[]{"J2A,J2Z"},"Djibouti") );
		addCountry( new CountryEntry(new String[]{"OUA,OZZ","XPA,XPZ","5PA,5QZ"},"Denmark") );
		addCountry( new CountryEntry(new String[]{"9OA,9TZ","HMA,HMZ","P5A,P9Z"},"DPR of Korea") );
		addCountry( new CountryEntry(new String[]{"OKA,OLZ"},"Czech Republic") );
		addCountry( new CountryEntry(new String[]{"C4A,C4Z","H2A,H2Z","P3A,P3Z","5BA,5BZ"},"Cyprus") );
		addCountry( new CountryEntry(new String[]{"CLA,CMZ","COA,COZ","T4A,T4Z"},"Cuba") );
		addCountry( new CountryEntry(new String[]{"9AA,9AZ"},"Croatia") );
		addCountry( new CountryEntry(new String[]{"TUA,TUZ"},"Cote d'Ivoire") );
		addCountry( new CountryEntry(new String[]{"TEA,TEZ","TIA,TIZ"},"Costa Rica") );
		addCountry( new CountryEntry(new String[]{"TNA,TNZ"},"Republic of the Congo") );
		addCountry( new CountryEntry(new String[]{"D6A,D6Z"},"Comoros") );
		addCountry( new CountryEntry(new String[]{"HJA,HKZ","5JA,5KZ"},"Colombia") );
		addCountry( new CountryEntry(new String[]{"BAA,BUZ","BWA,BZZ","XSA,XSZ","3HA,3UZ","VRA,VRZ"},"China") );
		addCountry( new CountryEntry(new String[]{"CAA,CAZ","CBA,CBZ","CCA,CCZ","CDA,CDZ","CEA,CEZ","XQA,XRZ","3GA,3GZ"},"Chile") );
		addCountry( new CountryEntry(new String[]{"TTA,TTZ"},"Chad") );
		addCountry( new CountryEntry(new String[]{"TLA,TLZ"},"Central Africa") );
		addCountry( new CountryEntry(new String[]{"D4A,D4Z"},"Cape Verde") );
		addCountry( new CountryEntry(new String[]{"CFA,CKZ","CYA,CZZ","VAA,VGZ","VOA,VOZ","VXA,VYZ","XJA,XOZ"},"Canada") );
		addCountry( new CountryEntry(new String[]{"TJA,TJZ"},"Cameroon") );
		addCountry( new CountryEntry(new String[]{"XUA,XUZ"},"Cambodia") );
		addCountry( new CountryEntry(new String[]{"9UA,9UZ"},"Burundi") );
		addCountry( new CountryEntry(new String[]{"XTA,XTZ"},"Burkina Faso") );
		addCountry( new CountryEntry(new String[]{"LZA,LZZ"},"Bulgaria") );
		addCountry( new CountryEntry(new String[]{"V8A,V8Z"},"Brunei") );
		addCountry( new CountryEntry(new String[]{"PPA,PYZ","ZVA,ZZZ"},"Brazil") );
		addCountry( new CountryEntry(new String[]{"A2A,A2Z","8OA,8OZ"},"Botswana") );
		addCountry( new CountryEntry(new String[]{"T9A,T9Z"},"Bosnia-Herzegovina") );
		addCountry( new CountryEntry(new String[]{"CPA,CPZ"},"Bolivia") );
		addCountry( new CountryEntry(new String[]{"A5A,A5Z"},"Bhutan") );
		addCountry( new CountryEntry(new String[]{"TYA,TYZ"},"Benin") );
		addCountry( new CountryEntry(new String[]{"V3A,V3Z"},"Belize") );
		addCountry( new CountryEntry(new String[]{"ONA,OTZ"},"Belgium") );
		addCountry( new CountryEntry(new String[]{"EUA,EWZ"},"Belarus") );
		addCountry( new CountryEntry(new String[]{"8PA,8PZ"},"Barbados") );
		addCountry( new CountryEntry(new String[]{"S2A,S3Z"},"Bangladesh") );
		addCountry( new CountryEntry(new String[]{"A9A,A9Z"},"Bahrain") );
		addCountry( new CountryEntry(new String[]{"C6A,C6Z"},"Bahamas") );
		addCountry( new CountryEntry(new String[]{"4JA,4KZ"},"Azerbaijan") );
		addCountry( new CountryEntry(new String[]{"OEA,OEZ"},"Austria") );
		addCountry( new CountryEntry(new String[]{"AXA,AXZ","VHA,VNZ","VZA,VZZ"},"Australia") );
		addCountry( new CountryEntry(new String[]{"P4A,P4Z"},"Aruba") );
		addCountry( new CountryEntry(new String[]{"EKA,EKZ"},"Armenia") );
		addCountry( new CountryEntry(new String[]{"AYA,AZZ","LOA,LWZ","L2A,L9Z"},"Argentina") );
		addCountry( new CountryEntry(new String[]{"V2A,V2Z"},"Antigua & Barbuda") );
		addCountry( new CountryEntry(new String[]{"D2A,D3Z"},"Angola") );
		addCountry( new CountryEntry(new String[]{"C3A,C3Z"},"Andorra") );
		addCountry( new CountryEntry(new String[]{"7RA,7RZ","7TA,7YZ"},"Algeria") );
		addCountry( new CountryEntry(new String[]{"ZAA,ZAZ"},"Albania") );
		addCountry( new CountryEntry(new String[]{"T6A,T6Z","YAA,YAZ"},"Afghanistan") );
//		addCountry( new CountryEntry( new String[]{ ""}, "Auckland & Campbell Is.") );

//		addCountry( new CountryEntry( new String[]{ "ZZZZZZZ" }, "Spratly Is.") );
		addCountry( new CountryEntry( new String[]{ "1A0,1A0" }, "Sov. Mil. Order of Malta") );
		addCountry( new CountryEntry( new String[]{ "3B6,3B6" }, "Agalega & St. Brandon") );
		addCountry( new CountryEntry( new String[]{ "3C0,3C0" }, "Annobon Is.") );
		addCountry( new CountryEntry( new String[]{ "3D2,3D2" }, "Conway Reef") );
		addCountry( new CountryEntry( new String[]{ "3D2,3D2" }, "Rotuma Is.") );
		addCountry( new CountryEntry( new String[]{ "3Y,3Y" }, "Bouvet") );
		addCountry( new CountryEntry( new String[]{ "3Y,3Y" }, "Peter I Is.") );
		addCountry( new CountryEntry( new String[]{ "4UITU,4UITU" }, "ITU HQ") );
		addCountry( new CountryEntry( new String[]{ "4W,4W" }, "Dem Rep of East Timor") );
		addCountry( new CountryEntry( new String[]{ "9I,9J" }, "Zambia") );
		addCountry( new CountryEntry( new String[]{ "9Q,9T" }, "Dem. Rep. of Congo") );
		addCountry( new CountryEntry( new String[]{ "BS7,BS7" }, "Scarborough Reef") );
		addCountry( new CountryEntry( new String[]{ "BV9P,BV9P" }, "Pratas Is.") );
		addCountry( new CountryEntry( new String[]{ "CE0,CE0" }, "Easter Is.") );
		addCountry( new CountryEntry( new String[]{ "CE0,CE0" }, "Juan Fernandez Is.") );
		addCountry( new CountryEntry( new String[]{ "CE0,CE0" }, "San Felix & San Ambrosio") );
		addCountry( new CountryEntry( new String[]{ "CE9,CE9" /*,"KC4,KC4" */ }, "Antarctica") );
		addCountry( new CountryEntry( new String[]{ "CT3,CT3" }, "Madeira Is.") );
		addCountry( new CountryEntry( new String[]{ "CU,CU" }, "Azores") );
		addCountry( new CountryEntry( new String[]{ "CY0,CY0" }, "Sable Is.") );
		addCountry( new CountryEntry( new String[]{ "CY9,CY9" }, "St. Paul Is.") );
		addCountry( new CountryEntry( new String[]{ "EA6,EA6","EB6,EB6","EC6,EC6","ED6,ED6","EE6,EE6","EF6,EF6","EG6,EG6","EH6,EH6" }, "Balearic Is.") );
		addCountry( new CountryEntry( new String[]{ "EA8,EA8","EB8,EB8","EC8,EC8","ED8,ED8","EE8,EE8","EF8,EF8","EG8,EG8","EH8,EH8" }, "Canary Is.") );
		addCountry( new CountryEntry( new String[]{ "EA9,EA9","EB9,EB9","EC9,EC9","ED9,ED9","EE9,EE9","EF9,EF9","EG9,EG9","EH9,EH9" }, "Ceuta & Melilla") );
		addCountry( new CountryEntry( new String[]{ "FG,FG" }, "Guadeloupe") );
		addCountry( new CountryEntry( new String[]{ "FJ,FS" }, "Saint Martin") );
		addCountry( new CountryEntry( new String[]{ "FH,FH" }, "Mayotte") );
		addCountry( new CountryEntry( new String[]{ "FK,FK" }, "New Caledonia") );
		addCountry( new CountryEntry( new String[]{ "FK/C,FK/C" }, "Chesterfield Is.") );
		addCountry( new CountryEntry( new String[]{ "FM,FM" }, "Martinique") );
		addCountry( new CountryEntry( new String[]{ "FO,FO" }, "Austral Is.") );
		addCountry( new CountryEntry( new String[]{ "FO,FO" }, "Clipperton Is.") );
		addCountry( new CountryEntry( new String[]{ "FO,FO" }, "French Polynesia") );
		addCountry( new CountryEntry( new String[]{ "FO,FO" }, "Marquesas Is.") );
		addCountry( new CountryEntry( new String[]{ "FP,FP" }, "St. Pierre & Miquelon") );
		addCountry( new CountryEntry( new String[]{ "FR/G,FR/G" }, "Glorioso Is.") );
		addCountry( new CountryEntry( new String[]{ "FR/J,FR/J","FR/E,FR/E" }, "Juan de Nova, Europa") );
		addCountry( new CountryEntry( new String[]{ "FR,FR" }, "Reunion Is.") );
		addCountry( new CountryEntry( new String[]{ "FR/T,FR/T" }, "Tromelin Is.") );
		addCountry( new CountryEntry( new String[]{ "FT5W,FT5W" }, "Crozet Is.") );
		addCountry( new CountryEntry( new String[]{ "FT5X,FT5X" }, "Kerguelen Is.") );
		addCountry( new CountryEntry( new String[]{ "FT5Z,FT5Z" }, "Amsterdam & St. Paul Is.") );
		addCountry( new CountryEntry( new String[]{ "FW,FW" }, "Wallis & Futuna Is.") );
		addCountry( new CountryEntry( new String[]{ "FY,FY" }, "French Guiana") );
//		addCountry( new CountryEntry( new String[]{ "G,G","GX,GX" }, "England") );
		addCountry( new CountryEntry( new String[]{ "GD,GD","GT,GT" }, "Isle of Man") );
		addCountry( new CountryEntry( new String[]{ "GI,GI","GN,GN" }, "Northern Ireland") );
		addCountry( new CountryEntry( new String[]{ "GJ,GJ","GH,GH" }, "Jersey") );
		addCountry( new CountryEntry( new String[]{ "GM,GM","GS,GS" }, "Scotland") );
		addCountry( new CountryEntry( new String[]{ "GU,GU","GP,GP" }, "Guernsey") );
		addCountry( new CountryEntry( new String[]{ "GW,GW","GC,GC" }, "Wales") );
		addCountry( new CountryEntry( new String[]{ "H40,H40" }, "Temotu Province") );
		addCountry( new CountryEntry( new String[]{ "HB0,HB0" }, "Liechtenstein") );
		addCountry( new CountryEntry( new String[]{ "HC8,HD8" }, "Galapagos Is.") );
		addCountry( new CountryEntry( new String[]{ "HK0,HK0" }, "Malpelo Is.") );
		addCountry( new CountryEntry( new String[]{ "HK0,HK0" }, "San Andres & Providencia") );
		addCountry( new CountryEntry( new String[]{ "IS0,IS0","IM0,IM0" }, "Sardinia") );
		addCountry( new CountryEntry( new String[]{ "JD1,JD1" }, "Minami Torishima") );
		addCountry( new CountryEntry( new String[]{ "JD1,JD1" }, "Ogasawara") );
		addCountry( new CountryEntry( new String[]{ "JW,JW" }, "Svalbard") );
		addCountry( new CountryEntry( new String[]{ "JX,JX" }, "Jan Mayen") );
		addCountry( new CountryEntry( new String[]{ "KG4,KG4" }, "Guantanamo Bay") );
		addCountry( new CountryEntry( new String[]{ "KH0,KH0" }, "Mariana Is.") );
		addCountry( new CountryEntry( new String[]{ "KH1,KH1" }, "Baker & Howland Is.") );
		addCountry( new CountryEntry( new String[]{ "KH2,KH2" }, "Guam") );
		addCountry( new CountryEntry( new String[]{ "KH3,KH3" }, "Johnston Is.") );
		addCountry( new CountryEntry( new String[]{ "KH4,KH4" }, "Midway Is.") );
		addCountry( new CountryEntry( new String[]{ "KH5,KH5" }, "Palmyra & Jarvis Is.") );
		addCountry( new CountryEntry( new String[]{ "KH5K,KH5K" }, "Kingman Reef") );
		addCountry( new CountryEntry( new String[]{ "KH6,KH6","KH7,KH7" }, "Hawaii") );
		addCountry( new CountryEntry( new String[]{ "KH7K,KH7K" }, "Kure Is.") );
		addCountry( new CountryEntry( new String[]{ "KH8,KH8" }, "American Samoa") );
		addCountry( new CountryEntry( new String[]{ "KH9,KH9" }, "Wake Is.") );
		addCountry( new CountryEntry( new String[]{ "KL7,KL7","WLA,WLZ" }, "Alaska") );
		addCountry( new CountryEntry( new String[]{ "KP1,KP1" }, "Navassa Is.") );
		addCountry( new CountryEntry( new String[]{ "KP2,KP2" }, "Virgin Is.") );
		addCountry( new CountryEntry( new String[]{ "KP3,KP3","KP4,KP4","WP3,WP4" }, "Puerto Rico") );
		addCountry( new CountryEntry( new String[]{ "KP5,KP5" }, "Desecheo Is.") );
		addCountry( new CountryEntry( new String[]{ "OH0,OH0" }, "Aland Is.") );
		addCountry( new CountryEntry( new String[]{ "OJ0,OJ0" }, "Market Reef") );
		addCountry( new CountryEntry( new String[]{ "OM,OM" }, "Slovak Republic") );
		addCountry( new CountryEntry( new String[]{ "OX,OX" }, "Greenland") );
		addCountry( new CountryEntry( new String[]{ "OY,OY" }, "Faroe Is.") );
		addCountry( new CountryEntry( new String[]{ "PJ2,PJ2","PJ4,PJ4","PJ9,PJ9" }, "Bonaire, Curacao (Neth. Antilles)") );
		addCountry( new CountryEntry( new String[]{ "PJ5,PJ8" }, "St. Maarten") );
		addCountry( new CountryEntry( new String[]{ "PJ5,PJ8" }, "Saba") );
		addCountry( new CountryEntry( new String[]{ "PJ5,PJ8" }, "St. Eustatius") );
		addCountry( new CountryEntry( new String[]{ "PP0F,PY0F","PP0,PP0" }, "Fernando de Noronha") );
		addCountry( new CountryEntry( new String[]{ "PP0S,PY0S","PP0,PP0" }, "St. Peter & St. Paul Rocks") );
		addCountry( new CountryEntry( new String[]{ "PP0T,PY0T","PP0,PP0" }, "Trindade & Martim Vaz Is.") );
		addCountry( new CountryEntry( new String[]{ "PZ,PZ" }, "Suriname") );
		addCountry( new CountryEntry( new String[]{ "R1FJ,R1FJ" }, "Franz Josef Land") );
		addCountry( new CountryEntry( new String[]{ "R1MV,R1MV" }, "Malyj Vysotskij Is.") );
		addCountry( new CountryEntry( new String[]{ "S0,S0" }, "Western Sahara") );
		addCountry( new CountryEntry( new String[]{ "ST,ST" }, "Sudan") );
		addCountry( new CountryEntry( new String[]{ "SV/A,SV/A" }, "Mount Athos") );
		addCountry( new CountryEntry( new String[]{ "SV5,SV5" }, "Dodecanese") );
		addCountry( new CountryEntry( new String[]{ "SV9,SV9" }, "Crete") );
		addCountry( new CountryEntry( new String[]{ "T2,T2" }, "Tuvalu") );
		addCountry( new CountryEntry( new String[]{ "T30,T30" }, "W. Kiribati (Gilbert Is.)") );
		addCountry( new CountryEntry( new String[]{ "T33,T33" }, "Banaba Is. (Ocean Is.)") );
		addCountry( new CountryEntry( new String[]{ "T5,T5" }, "Somalia") );
		addCountry( new CountryEntry( new String[]{ "TI9,TI9" }, "Cocos Is.") );
		addCountry( new CountryEntry( new String[]{ "TK,TK" }, "Corsica") );
		addCountry( new CountryEntry( new String[]{ "UA1,UI1","UA3,UI3","UA4,UI4","UA6,UI6" }, "European Russia") );
		addCountry( new CountryEntry( new String[]{ "UA2,UA2" }, "Kaliningrad") );
		addCountry( new CountryEntry( new String[]{ "UA8,UI8","UA9,UI9","UA0,UI0" }, "Asiatic Russia") );
		addCountry( new CountryEntry( new String[]{ "UJ,UM" }, "Uzbekistan") );
		addCountry( new CountryEntry( new String[]{ "UR,UZ","EM,EO" }, "Ukraine") );
		addCountry( new CountryEntry( new String[]{ "V4,V4" }, "St. Kitts & Nevis") );
		addCountry( new CountryEntry( new String[]{ "VK0,VK0" }, "Heard Is.") );
		addCountry( new CountryEntry( new String[]{ "VK0,VK0" }, "Macquarie Is.") );
		addCountry( new CountryEntry( new String[]{ "VK9C,VK9C" }, "Cocos-Keeling Is.") );
		addCountry( new CountryEntry( new String[]{ "VK9L,VK9L" }, "Lord Howe Is.") );
		addCountry( new CountryEntry( new String[]{ "VK9M,VK9M" }, "Mellish Reef") );
		addCountry( new CountryEntry( new String[]{ "VK9N,VK9N" }, "Norfolk Is.") );
		addCountry( new CountryEntry( new String[]{ "VK9W,VK9W" }, "Willis Is.") );
		addCountry( new CountryEntry( new String[]{ "VK9X,VK9X" }, "Christmas Is.") );
		addCountry( new CountryEntry( new String[]{ "VP2E,VP2E" }, "Anguilla") );
		addCountry( new CountryEntry( new String[]{ "VP2M,VP2M" }, "Montserrat") );
		addCountry( new CountryEntry( new String[]{ "VP2V,VP2V" }, "British Virgin Is.") );
		addCountry( new CountryEntry( new String[]{ "VP5,VP5" }, "Turks & Caicos Is.") );
		addCountry( new CountryEntry( new String[]{ "VP6,VP6" }, "Pitcairn Is.") );
		addCountry( new CountryEntry( new String[]{ "VP6,VP6" }, "Ducie Is.") );
		addCountry( new CountryEntry( new String[]{ "VP8,VP8" }, "Falkland Is.") );
		addCountry( new CountryEntry( new String[]{ "VP8,VP6","LU,LU" }, "South Georgia Is.") );
		addCountry( new CountryEntry( new String[]{ "VP8,VP8","LU,LU" }, "South Orkney Is.") );
		addCountry( new CountryEntry( new String[]{ "VP8,VP8","LU,LU" }, "South Sandwich Is.") );
		addCountry( new CountryEntry( new String[]{ "VP8,VP8","LU,LU" }, "South Shetland Is.") );
		addCountry( new CountryEntry( new String[]{ "VP9,VP9" }, "Bermuda") );
		addCountry( new CountryEntry( new String[]{ "VQ9,VQ9" }, "Chagos Is.") );
		addCountry( new CountryEntry( new String[]{ "VR,VR" }, "Hong Kong") );
		addCountry( new CountryEntry( new String[]{ "VU,VU" }, "Andaman & Nicobar Is.") );
		addCountry( new CountryEntry( new String[]{ "VU,VU" }, "Lakshadweep Is.") );
		addCountry( new CountryEntry( new String[]{ "XA4,XI4" }, "Revillagigedo") );
		addCountry( new CountryEntry( new String[]{ "XX9,XX9" }, "Macao") );
		addCountry( new CountryEntry( new String[]{ "YJ,YJ" }, "Vanuatu") );
		addCountry( new CountryEntry( new String[]{ "YT,YU","YZ,YZ" }, "Yugoslavia") );
		addCountry( new CountryEntry( new String[]{ "YV0,YV0" }, "Aves Is.") );
		addCountry( new CountryEntry( new String[]{ "Z2,Z2" }, "Zimbabwe") );
		addCountry( new CountryEntry( new String[]{ "ZB2,ZB2" }, "Gibraltar") );
		addCountry( new CountryEntry( new String[]{ "ZC4,ZC4" }, "UK Sov. Base Areas on Cyprus") );
		addCountry( new CountryEntry( new String[]{ "ZD7,ZD7" }, "St. Helena") );
		addCountry( new CountryEntry( new String[]{ "ZD8,ZD8" }, "Ascension Is.") );
		addCountry( new CountryEntry( new String[]{ "ZD9,ZD9" }, "Tristan da Cunha & Gough Is.") );
		addCountry( new CountryEntry( new String[]{ "ZF,ZF" }, "Cayman Is.") );
		addCountry( new CountryEntry( new String[]{ "ZK1,ZK1" }, "N. Cook Is.") );
		addCountry( new CountryEntry( new String[]{ "ZK1,ZK1" }, "S. Cook Is.") );
		addCountry( new CountryEntry( new String[]{ "ZK2,ZK2" }, "Niue") );
		addCountry( new CountryEntry( new String[]{ "ZK3,ZK3" }, "Tokelau Is.") );
		addCountry( new CountryEntry( new String[]{ "ZL7,ZL7" }, "Chatham Is.") );
		addCountry( new CountryEntry( new String[]{ "ZL8,ZL8" }, "Kermadec Is.") );
		addCountry( new CountryEntry( new String[]{ "ZS8,ZS8" }, "Prince Edward & Marion Is.") );
		addCountry( new CountryEntry( new String[]{ "3B9,3B9"}, "Rodrigues Is.") );

		
		Collections.sort( countries );

		zones = new ArrayList<ZoneEntry>(350);
//		addZone( new ZoneEntry("Spratly Is.","AS","50","26"));
		addZone( new ZoneEntry("Sov. Mil. Order of Malta","EU","28","15"));
		addZone( new ZoneEntry("Monaco","EU","27","14"));
		addZone( new ZoneEntry("Agalega & St. Brandon","AF","53","39"));
		addZone( new ZoneEntry("Mauritius","AF","53","39"));
		addZone( new ZoneEntry("Rodrigues Is.","AF","53","39"));
		addZone( new ZoneEntry("Equatorial Guinea","AF","47","36"));
		addZone( new ZoneEntry("Annobon Is.","AF","52","36"));
		addZone( new ZoneEntry("Fiji","OC","56","32"));
		addZone( new ZoneEntry("Conway Reef","OC","56","32"));
		addZone( new ZoneEntry("Rotuma Is.","OC","56","32"));
		addZone( new ZoneEntry("Swaziland","AF","57","38"));
		addZone( new ZoneEntry("Tunisia","AF","37","33"));
		addZone( new ZoneEntry("United Kingdom","EU","49","26"));
		addZone( new ZoneEntry("Russian Fed.","AS","49","26"));
		addZone( new ZoneEntry("Vietnam","AS","49","26"));
		addZone( new ZoneEntry("Guinea","AF","46","35"));
		addZone( new ZoneEntry("Bouvet","AF","67","38"));
		addZone( new ZoneEntry("Peter I Is.","AN","72","12"));
		addZone( new ZoneEntry("Azerbaijan","AS","29","21"));
		addZone( new ZoneEntry("Georgia","AS","29","21"));
		addZone( new ZoneEntry("Sri Lanka","AS","41","22"));
		addZone( new ZoneEntry("ITU HQ","EU","28","14"));
		addZone( new ZoneEntry("United Nations","NA","08","05"));
		addZone( new ZoneEntry("Dem Rep of East Timor","OC","54","28"));
		addZone( new ZoneEntry("Israel","AS","39","20"));
		addZone( new ZoneEntry("Libya","AF","38","34"));
		addZone( new ZoneEntry("Cyprus","AS","39","20"));
		addZone( new ZoneEntry("Tanzania","AF","53","37"));
		addZone( new ZoneEntry("Nigeria","AF","46","35"));
		addZone( new ZoneEntry("Madagascar","AF","53","39"));
		addZone( new ZoneEntry("Mauritania","AF","46","35"));
		addZone( new ZoneEntry("Niger","AF","46","35"));
		addZone( new ZoneEntry("Togo","AF","46","35"));
		addZone( new ZoneEntry("Samoa","OC","62","32"));
		addZone( new ZoneEntry("Uganda","AF","48","37"));
		addZone( new ZoneEntry("Kenya","AF","48","37"));
		addZone( new ZoneEntry("Senegal","AF","46","35"));
		addZone( new ZoneEntry("Jamaica","NA","11","08"));
		addZone( new ZoneEntry("Yemen","AS","39","21"));
		addZone( new ZoneEntry("Lesotho","AF","57","38"));
		addZone( new ZoneEntry("Malawi","AF","53","37"));
		addZone( new ZoneEntry("Algeria","AF","37","33"));
		addZone( new ZoneEntry("Barbados","NA","11","08"));
		addZone( new ZoneEntry("Maldives","AF","41","22"));
		addZone( new ZoneEntry("Guyana","SA","12","09"));
		addZone( new ZoneEntry("Croatia","EU","28","15"));
		addZone( new ZoneEntry("Ghana","AF","46","35"));
		addZone( new ZoneEntry("Malta","EU","28","15"));
		addZone( new ZoneEntry("Zambia","AF","53","36"));
		addZone( new ZoneEntry("Kuwait","AS","39","21"));
		addZone( new ZoneEntry("Sierra Leone","AF","46","35"));
		addZone( new ZoneEntry("West Malaysia","AS","54","28"));
		addZone( new ZoneEntry("East Malaysia","OC","54","28"));
		addZone( new ZoneEntry("Nepal","AS","42","22"));
		addZone( new ZoneEntry("Dem. Rep. of Congo","AF","52","36"));
		addZone( new ZoneEntry("Burundi","AF","52","36"));
		addZone( new ZoneEntry("Singapore","AS","54","28"));
		addZone( new ZoneEntry("Rwanda","AF","52","36"));
		addZone( new ZoneEntry("Trinidad & Tobago","SA","11","09"));
		addZone( new ZoneEntry("Botswana","AF","57","38"));
		addZone( new ZoneEntry("Tonga","OC","62","32"));
		addZone( new ZoneEntry("Oman","AS","39","21"));
		addZone( new ZoneEntry("Bhutan","AS","41","22"));
		addZone( new ZoneEntry("United Arab Em.","AS","39","21"));
		addZone( new ZoneEntry("Qatar","AS","39","21"));
		addZone( new ZoneEntry("Bahrain","AS","39","21"));
		addZone( new ZoneEntry("Pakistan","AS","41","21"));
		addZone( new ZoneEntry("Scarborough Reef","AS","50","27"));
		addZone( new ZoneEntry("Taiwan","AS","44","24"));
		addZone( new ZoneEntry("Pratas Is.","AS","44","24"));
		addZone( new ZoneEntry("China","AS","(A)","23,24"));
		addZone( new ZoneEntry("Nauru","OC","65","31"));
		addZone( new ZoneEntry("Andorra","EU","27","14"));
		addZone( new ZoneEntry("Gambia","AF","46","35"));
		addZone( new ZoneEntry("Bahamas","NA","11","08"));
		addZone( new ZoneEntry("Mozambique","AF","53","37"));
		addZone( new ZoneEntry("Chile","SA","14,16","12"));
		addZone( new ZoneEntry("Easter Is.","SA","63","12"));
		addZone( new ZoneEntry("Juan Fernandez Is.","SA","14","12"));
		addZone( new ZoneEntry("San Felix & San Ambrosio","SA","14","12"));
		addZone( new ZoneEntry("Antarctica","AN","(B)","(C)"));
		addZone( new ZoneEntry("Cuba","NA","11","08"));
		addZone( new ZoneEntry("Morocco","AF","37","33"));
		addZone( new ZoneEntry("Bolivia","SA","12,14","10"));
		addZone( new ZoneEntry("Portugal","EU","37","14"));
		addZone( new ZoneEntry("Madeira Is.","AF","36","33"));
		addZone( new ZoneEntry("Azores","EU","36","14"));
		addZone( new ZoneEntry("Uruguay","SA","14","13"));
		addZone( new ZoneEntry("Sable Is.","NA","09","05"));
		addZone( new ZoneEntry("St. Paul Is.","NA","09","05"));
		addZone( new ZoneEntry("Angola","AF","52","36"));
		addZone( new ZoneEntry("Cape Verde","AF","46","35"));
		addZone( new ZoneEntry("Comoros","AF","53","39"));
		addZone( new ZoneEntry("Germany","EU","28","14"));
		addZone( new ZoneEntry("Philippines","AS","50","27"));
		addZone( new ZoneEntry("Eritrea","AF","48","37"));
		addZone( new ZoneEntry("Palestine","AS","39","20"));
		addZone( new ZoneEntry("Spain","EU","37","14"));
		addZone( new ZoneEntry("Balearic Is.","EU","37","14"));
		addZone( new ZoneEntry("Canary Is.","AF","36","33"));
		addZone( new ZoneEntry("Ceuta & Melilla","AF","37","33"));
		addZone( new ZoneEntry("Ireland","EU","27","14"));
		addZone( new ZoneEntry("Armenia","AS","29","21"));
		addZone( new ZoneEntry("Liberia","AF","46","35"));
		addZone( new ZoneEntry("Iran","AS","40","21"));
		addZone( new ZoneEntry("Moldovia","EU","29","16"));
		addZone( new ZoneEntry("Estonia","EU","29","15"));
		addZone( new ZoneEntry("Ethiopia","AF","48","37"));
		addZone( new ZoneEntry("Belarus","EU","29","16"));
		addZone( new ZoneEntry("Kyrgyzstan","AS","30,31","17"));
		addZone( new ZoneEntry("Tajikistan","AS","30","17"));
		addZone( new ZoneEntry("Turkmenistan","AS","30","17"));
		addZone( new ZoneEntry("France","EU","27","14"));
		addZone( new ZoneEntry("Guadeloupe","NA","11","08"));
		addZone( new ZoneEntry("Saint Martin","NA","11","08"));
		addZone( new ZoneEntry("Mayotte","AF","53","39"));
		addZone( new ZoneEntry("New Caledonia","OC","56","32"));
		addZone( new ZoneEntry("Chesterfield Is.","OC","56","30"));
		addZone( new ZoneEntry("Martinique","NA","11","08"));
		addZone( new ZoneEntry("Austral Is.","OC","63","32"));
		addZone( new ZoneEntry("Clipperton Is.","NA","10","07"));
		addZone( new ZoneEntry("French Polynesia","OC","63","32"));
		addZone( new ZoneEntry("Marquesas Is.","OC","63","31"));
		addZone( new ZoneEntry("St. Pierre & Miquelon","NA","09","05"));
		addZone( new ZoneEntry("Glorioso Is.","AF","53","39"));
		addZone( new ZoneEntry("Juan de Nova, Europa","AF","53","39"));
		addZone( new ZoneEntry("Reunion Is.","AF","53","39"));
		addZone( new ZoneEntry("Tromelin Is.","AF","53","39"));
		addZone( new ZoneEntry("Crozet Is.","AF","68","39"));
		addZone( new ZoneEntry("Kerguelen Is.","AF","68","39"));
		addZone( new ZoneEntry("Amsterdam & St. Paul Is.","AF","68","39"));
		addZone( new ZoneEntry("Wallis & Futuna Is.","OC","62","32"));
		addZone( new ZoneEntry("French Guiana","SA","12","09"));
//		addZone( new ZoneEntry("England","EU","27","14"));
		addZone( new ZoneEntry("Isle of Man","EU","27","14"));
		addZone( new ZoneEntry("Northern Ireland","EU","27","14"));
		addZone( new ZoneEntry("Jersey","EU","27","14"));
		addZone( new ZoneEntry("Scotland","EU","27","14"));
		addZone( new ZoneEntry("Guernsey","EU","27","14"));
		addZone( new ZoneEntry("Wales","EU","27","14"));
		addZone( new ZoneEntry("Solomon Is.","OC","51","28"));
		addZone( new ZoneEntry("Temotu Province","OC","51","32"));
		addZone( new ZoneEntry("Hungary","EU","28","15"));
		addZone( new ZoneEntry("Switzerland","EU","28","14"));
		addZone( new ZoneEntry("Liechtenstein","EU","28","14"));
		addZone( new ZoneEntry("Ecuador","SA","12","10"));
		addZone( new ZoneEntry("Galapagos Is.","SA","12","10"));
		addZone( new ZoneEntry("Haiti","NA","11","08"));
		addZone( new ZoneEntry("Dominican Republic","NA","11","08"));
		addZone( new ZoneEntry("Colombia","SA","12","09"));
		addZone( new ZoneEntry("Malpelo Is.","SA","12","09"));
		addZone( new ZoneEntry("San Andres & Providencia","NA","11","07"));
		addZone( new ZoneEntry("Republic of Korea","AS","44","25"));
		addZone( new ZoneEntry("Panama","SA","11","07"));
		addZone( new ZoneEntry("Honduras","NA","11","07"));
		addZone( new ZoneEntry("Thailand","AS","49","26"));
		addZone( new ZoneEntry("Vatican","EU","28","15"));
		addZone( new ZoneEntry("Saudi Arabia","AS","39","21"));
		addZone( new ZoneEntry("Italy","EU","28","15,33"));
		addZone( new ZoneEntry("Sardinia","EU","28","15"));
		addZone( new ZoneEntry("Djibouti","AF","48","37"));
		addZone( new ZoneEntry("Grenada","NA","11","08"));
		addZone( new ZoneEntry("Guinea-Bissau","AF","46","35"));
		addZone( new ZoneEntry("St. Lucia","NA","11","08"));
		addZone( new ZoneEntry("Dominica","NA","11","08"));
		addZone( new ZoneEntry("St. Vincent","NA","11","08"));
		addZone( new ZoneEntry("Japan","AS","45","25"));
		addZone( new ZoneEntry("Minami Torishima","OC","90","27"));
		addZone( new ZoneEntry("Ogasawara","AS","45","27"));
		addZone( new ZoneEntry("Mongolia","AS","32,33","23"));
		addZone( new ZoneEntry("Svalbard","EU","18","40"));
		addZone( new ZoneEntry("Jan Mayen","EU","18","40"));
		addZone( new ZoneEntry("Jordan","AS","39","20"));
		addZone( new ZoneEntry("United States","NA","6,7,8","3,4,5"));
		addZone( new ZoneEntry("Guantanamo Bay","NA","11","08"));
		addZone( new ZoneEntry("Mariana Is.","OC","64","27"));
		addZone( new ZoneEntry("Baker & Howland Is.","OC","61","31"));
		addZone( new ZoneEntry("Guam","OC","64","27"));
		addZone( new ZoneEntry("Johnston Is.","OC","61","31"));
		addZone( new ZoneEntry("Midway Is.","OC","61","31"));
		addZone( new ZoneEntry("Palmyra & Jarvis Is.","OC","61,62,","31"));
		addZone( new ZoneEntry("Kingman Reef","OC","61","31"));
		addZone( new ZoneEntry("Hawaii","OC","61","31"));
		addZone( new ZoneEntry("Kure Is.","OC","61","31"));
		addZone( new ZoneEntry("American Samoa","OC","62","32"));
		addZone( new ZoneEntry("Wake Is.","OC","65","31"));
		addZone( new ZoneEntry("Alaska","NA","1,2","1"));
		addZone( new ZoneEntry("Navassa Is.","NA","11","08"));
		addZone( new ZoneEntry("Virgin Is.","NA","11","08"));
		addZone( new ZoneEntry("Puerto Rico","NA","11","08"));
		addZone( new ZoneEntry("Desecheo Is.","NA","11","08"));
		addZone( new ZoneEntry("Norway","EU","18","14"));
		addZone( new ZoneEntry("Argentina","SA","14,16","13"));
		addZone( new ZoneEntry("Luxembourg","EU","27","14"));
		addZone( new ZoneEntry("Lithuania","EU","29","15"));
		addZone( new ZoneEntry("Bulgaria","EU","28","20"));
		addZone( new ZoneEntry("Peru","SA","12","10"));
		addZone( new ZoneEntry("Lebanon","AS","39","20"));
		addZone( new ZoneEntry("Austria","EU","28","15"));
		addZone( new ZoneEntry("Finland","EU","18","15"));
		addZone( new ZoneEntry("Aland Is.","EU","18","15"));
		addZone( new ZoneEntry("Market Reef","EU","18","15"));
		addZone( new ZoneEntry("Czech Republic","EU","28","15"));
		addZone( new ZoneEntry("Slovak Republic","EU","28","15"));
		addZone( new ZoneEntry("Belgium","EU","27","14"));
		addZone( new ZoneEntry("Greenland","NA","5,75","40"));
		addZone( new ZoneEntry("Faroe Is.","EU","18","14"));
		addZone( new ZoneEntry("Denmark","EU","18","14"));
		addZone( new ZoneEntry("Papua New Guinea","OC","51","28"));
		addZone( new ZoneEntry("Aruba","SA","11","09"));
		addZone( new ZoneEntry("DPR of Korea","AS","44","25"));
		addZone( new ZoneEntry("Netherlands","EU","27","14"));
		addZone( new ZoneEntry("Bonaire, Curacao (Neth. Antilles)","SA","11","09"));
		addZone( new ZoneEntry("St. Maarten","NA","11","08"));
		addZone( new ZoneEntry("Saba","NA","11","08"));
		addZone( new ZoneEntry("St. Eustatius","NA","11","08"));
		addZone( new ZoneEntry("Brazil","SA","(D)","11"));
		addZone( new ZoneEntry("Fernando de Noronha","SA","13","11"));
		addZone( new ZoneEntry("St. Peter & St. Paul Rocks","SA","13","11"));
		addZone( new ZoneEntry("Trindade & Martim Vaz Is.","SA","15","11"));
		addZone( new ZoneEntry("Suriname","SA","12","09"));
		addZone( new ZoneEntry("Franz Josef Land","EU","75","40"));
		addZone( new ZoneEntry("Malyj Vysotskij Is.","EU","29","16"));
		addZone( new ZoneEntry("Western Sahara","AF","46","33"));
		addZone( new ZoneEntry("Bangladesh","AS","41","22"));
		addZone( new ZoneEntry("Slovenia","EU","28","15"));
		addZone( new ZoneEntry("Seychelles","AF","53","39"));
		addZone( new ZoneEntry("Sao Tome & Principe","AF","47","36"));
		addZone( new ZoneEntry("Sweden","EU","18","14"));
		addZone( new ZoneEntry("Poland","EU","28","15"));
		addZone( new ZoneEntry("Sudan","AF","48","34"));
		addZone( new ZoneEntry("Egypt","AF","38","34"));
		addZone( new ZoneEntry("Greece","EU","28","20"));
		addZone( new ZoneEntry("Mount Athos","EU","28","20"));
		addZone( new ZoneEntry("Dodecanese","EU","28","20"));
		addZone( new ZoneEntry("Crete","EU","28","20"));
		addZone( new ZoneEntry("Tuvalu","OC","65","31"));
		addZone( new ZoneEntry("W. Kiribati (Gilbert Is.)","OC","65","31"));
		addZone( new ZoneEntry("C. Kiribati (British  Phoenix Is.)","OC","62","31"));
		addZone( new ZoneEntry("E. Kiribati (Line Is.)","OC","61,63","31"));
		addZone( new ZoneEntry("Banaba Is. (Ocean Is.)","OC","65","31"));
		addZone( new ZoneEntry("Somalia","AF","48","37"));
		addZone( new ZoneEntry("San Marino","EU","28","15"));
		addZone( new ZoneEntry("Palau","OC","64","27"));
		addZone( new ZoneEntry("Bosnia-Herzegovina","EU","28","15"));
		addZone( new ZoneEntry("Turkey","AS","39","20"));
		addZone( new ZoneEntry("Iceland","EU","17","40"));
		addZone( new ZoneEntry("Guatemala","NA","11","07"));
		addZone( new ZoneEntry("Costa Rica","NA","11","07"));
		addZone( new ZoneEntry("Cocos Is.","NA","11","07"));
		addZone( new ZoneEntry("Cameroon","AF","47","36"));
		addZone( new ZoneEntry("Corsica","EU","28","15"));
		addZone( new ZoneEntry("Central Africa","AF","47","36"));
		addZone( new ZoneEntry("Republic of the Congo","AF","52","36"));
		addZone( new ZoneEntry("Gabon","AF","52","36"));
		addZone( new ZoneEntry("Chad","AF","47","36"));
		addZone( new ZoneEntry("Cote d'Ivoire","AF","46","35"));
		addZone( new ZoneEntry("Benin","AF","46","35"));
		addZone( new ZoneEntry("Mali","AF","46","35"));
		addZone( new ZoneEntry("European Russia","EU","(E)","16"));
		addZone( new ZoneEntry("Kaliningrad","EU","29","15"));
		addZone( new ZoneEntry("Asiatic Russia","AS","(F)","(G)"));
		addZone( new ZoneEntry("Uzbekistan","AS","30","17"));
		addZone( new ZoneEntry("Kazakhstan","AS","29,30,31","17"));
		addZone( new ZoneEntry("Ukraine","EU","29","16"));
		addZone( new ZoneEntry("Antigua & Barbuda","NA","11","08"));
		addZone( new ZoneEntry("Belize","NA","11","07"));
		addZone( new ZoneEntry("St. Kitts & Nevis","NA","11","08"));
		addZone( new ZoneEntry("Namibia","AF","57","38"));
		addZone( new ZoneEntry("Micronesia","OC","65","27"));
		addZone( new ZoneEntry("Marshall Is.","OC","65","31"));
		addZone( new ZoneEntry("Brunei","OC","54","28"));
		addZone( new ZoneEntry("Canada","NA","(H)","1-5"));
		addZone( new ZoneEntry("Australia","OC","(I)","29,30"));
		addZone( new ZoneEntry("Heard Is.","AF","68","39"));
		addZone( new ZoneEntry("Macquarie Is.","OC","60","30"));
		addZone( new ZoneEntry("Cocos-Keeling Is.","OC","54","29"));
		addZone( new ZoneEntry("Lord Howe Is.","OC","60","30"));
		addZone( new ZoneEntry("Mellish Reef","OC","56","30"));
		addZone( new ZoneEntry("Norfolk Is.","OC","60","32"));
		addZone( new ZoneEntry("Willis Is.","OC","55","30"));
		addZone( new ZoneEntry("Christmas Is.","OC","54","29"));
		addZone( new ZoneEntry("Anguilla","NA","11","08"));
		addZone( new ZoneEntry("Montserrat","NA","11","08"));
		addZone( new ZoneEntry("British Virgin Is.","NA","11","08"));
		addZone( new ZoneEntry("Turks & Caicos Is.","NA","11","08"));
		addZone( new ZoneEntry("Pitcairn Is.","OC","63","32"));
		addZone( new ZoneEntry("Ducie Is.","OC","63","32"));
		addZone( new ZoneEntry("Falkland Is.","SA","16","13"));
		addZone( new ZoneEntry("South Georgia Is.","SA","73","13"));
		addZone( new ZoneEntry("South Orkney Is.","SA","73","13"));
		addZone( new ZoneEntry("South Sandwich Is.","SA","73","13"));
		addZone( new ZoneEntry("South Shetland Is.","SA","73","13"));
		addZone( new ZoneEntry("Bermuda","NA","11","05"));
		addZone( new ZoneEntry("Chagos Is.","AF","41","39"));
		addZone( new ZoneEntry("Hong Kong","AS","44","24"));
		addZone( new ZoneEntry("India","AS","41","22"));
		addZone( new ZoneEntry("Andaman & Nicobar Is.","AS","49","26"));
		addZone( new ZoneEntry("Lakshadweep Is.","AS","41","22"));
		addZone( new ZoneEntry("Mexico","NA","10","06"));
		addZone( new ZoneEntry("Revillagigedo","NA","10","06"));
		addZone( new ZoneEntry("Burkina Faso","AF","46","35"));
		addZone( new ZoneEntry("Cambodia","AS","49","26"));
		addZone( new ZoneEntry("Laos","AS","49","26"));
		addZone( new ZoneEntry("Macao","AS","44","24"));
		addZone( new ZoneEntry("Myanmar","AS","49","26"));
		addZone( new ZoneEntry("Afghanistan","AS","40","21"));
		addZone( new ZoneEntry("Indonesia","OC","51,54","28"));
		addZone( new ZoneEntry("Iraq","AS","39","21"));
		addZone( new ZoneEntry("Vanuatu","OC","56","32"));
		addZone( new ZoneEntry("Syria","AS","39","20"));
		addZone( new ZoneEntry("Latvia","EU","29","15"));
		addZone( new ZoneEntry("Nicaragua","NA","11","07"));
		addZone( new ZoneEntry("Romania","EU","28","20"));
		addZone( new ZoneEntry("El Salvador","NA","11","07"));
		addZone( new ZoneEntry("Yugoslavia","EU","28","15"));
		addZone( new ZoneEntry("Venezuela","SA","12","09"));
		addZone( new ZoneEntry("Aves Is.","NA","11","08"));
		addZone( new ZoneEntry("Zimbabwe","AF","53","38"));
		addZone( new ZoneEntry("Macedonia","EU","28","15"));
		addZone( new ZoneEntry("Albania","EU","28","15"));
		addZone( new ZoneEntry("Gibraltar","EU","37","14"));
		addZone( new ZoneEntry("UK Sov. Base Areas on Cyprus","AS","39","20"));
		addZone( new ZoneEntry("St. Helena","AF","66","36"));
		addZone( new ZoneEntry("Ascension Is.","AF","66","36"));
		addZone( new ZoneEntry("Tristan da Cunha & Gough Is.","AF","66","38"));
		addZone( new ZoneEntry("Cayman Is.","NA","11","08"));
		addZone( new ZoneEntry("N. Cook Is.","OC","62","32"));
		addZone( new ZoneEntry("S. Cook Is.","OC","62","32"));
		addZone( new ZoneEntry("Niue","OC","62","32"));
		addZone( new ZoneEntry("Tokelau Is.","OC","62","31"));
		addZone( new ZoneEntry("New Zealand","OC","60","32"));
		addZone( new ZoneEntry("Chatham Is.","OC","60","32"));
		addZone( new ZoneEntry("Kermadec Is.","OC","60","32"));
//		addZone( new ZoneEntry("Auckland & Campbell Is.","OC","60","32"));
		addZone( new ZoneEntry("Paraguay","SA","14","11"));
		addZone( new ZoneEntry("South Africa","AF","57","38"));
		addZone( new ZoneEntry("Prince Edward & Marion Is.","AF","57","38"));
		Collections.sort( zones );
		Collections.sort( countries );
	}
}