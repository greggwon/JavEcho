package de.tu_berlin;
//    $Id: InvalidGSMFrameException.java,v 1.1 2004-05-31 17:59:13 greggwon Exp $	

//    This file is part of the GSM 6.10 audio decoder library for Java
//    Copyright (C) 1998 Steven Pickles (pix@test.at)

//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Library General Public
//    License as published by the Free Software Foundation; either
//    version 2 of the License, or (at your option) any later version.

//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Library General Public License for more details.

//    You should have received a copy of the GNU Library General Public
//    License along with this library; if not, write to the Free
//    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.



//  This software is a port of the GSM Library provided by
//  Jutta Degener (jutta@cs.tu-berlin.de) and 
//  Carsten Bormann (cabo@cs.tu-berlin.de), 
//  Technische Universitaet Berlin



public class InvalidGSMFrameException extends Exception {
	public InvalidGSMFrameException() {
		super();
	}
	public InvalidGSMFrameException( String str ) {
		super(str);
	}
}
