package com.letsprog.learning.ejb3.remote.client;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.letsprog.learning.ejb3.server.api.IQuiz;

@WebListener
public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		
		System.out.println("Session Created : "+se.getSession().getId());
		System.out.println("Session Inactivity Timeout : "+se.getSession().getMaxInactiveInterval());
 
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
 
		String sessionId = se.getSession().getId();
		System.out.println("Session Id : "+sessionId +" has expired");
 
		IQuiz object = QuizSessions.map.get(sessionId);
 
		if(object != null ){
			object.end();
		}
 
	}
}
