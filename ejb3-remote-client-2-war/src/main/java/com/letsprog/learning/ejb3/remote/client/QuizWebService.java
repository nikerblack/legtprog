package com.letsprog.learning.ejb3.remote.client;

import java.io.IOException;
import java.net.URI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.letsprog.learning.ejb3.server.api.IQuiz;
import com.letsprog.learning.ejb3.server.api.IRemotePlayedQuizzesCounter;
import com.letsprog.learning.ejb3.server.api.IRemoteQuiz;
import com.letsprog.learning.jndi.lookup.ejb3.LookerUp;

@Stateless
@Path("/play")
public class QuizWebService {

	@EJB(mappedName="ejb:/ejb3-server-war//PlayedQuizzesCounterBean!com.letsprog.learning.ejb3.server.api.IRemotePlayedQuizzesCounter")
	IRemotePlayedQuizzesCounter playedQuizzesCounterProxy;
	
	@POST
	@Path("/start")
	@Produces(MediaType.TEXT_HTML)
	public Response start(@javax.ws.rs.core.Context HttpServletRequest request,
			@FormParam("playerName") String playerName) throws NamingException, ServletException, IOException {
 
		playedQuizzesCounterProxy.increment();
 
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		System.out.println("Session Id : "+sessionId);
 
		IQuiz quizProxy = QuizSessions.map.get(sessionId);
 
		if(quizProxy != null ){
			quizProxy.end();
			quizProxy = null;
			QuizSessions.map.remove(sessionId);
			System.out.println("Refreshing Quiz!");
		}
 
		// Client and EJBs are in different Containers
		//--- EJB Lookup in same WAR
		String ejbsServerAddress = "127.0.0.1";
		int ejbsServerPort = 8080;
		String earName = ""; // EJB exist in a WAR and not in an EAR.
		String moduleName = "ejb3-server-war";
		String deploymentDistinctName = "";
		String beanName = "QuizBean";
		String interfaceQualifiedName = IRemoteQuiz.class.getName();
 
 
		// UserName and Password are required but are set apart using the file "jboss-ejb-client.xml"
		LookerUp wildf9Lookerup = new LookerUp(ejbsServerAddress, ejbsServerPort);
 
		// We could instead the following method by giving the exact JNDI name :
		// wildf9Lookerup.findSessionBean("ejb:/ejb3-server-war//QuizBean!com.letsprog.learning.ejb3.server.api.ILocalQuiz?stateful");
		quizProxy = (IRemoteQuiz) wildf9Lookerup.findRemoteSessionBean(LookerUp.SessionBeanType.STATEFUL, earName, moduleName,deploymentDistinctName, beanName, interfaceQualifiedName);
		quizProxy.begin(playerName);
 
		QuizSessions.map.put(sessionId, quizProxy);
 
		String question = quizProxy.generateQuestionAndAnswer();
		int score = quizProxy.getScore();
 
		URI uri = UriBuilder.fromPath(request.getContextPath()).path("/../../quiz.jsp")
				.queryParam("question",question)
				.queryParam("score",score)
				.build();
		return Response.seeOther(uri).build(); 
	}
	
	@POST
	@Path("/answer")
	@Produces(MediaType.TEXT_HTML)
	public Response answer(@javax.ws.rs.core.Context HttpServletRequest request,
			@FormParam("playerAnswer") int playerAnswer){
 
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		System.out.println("Session Id : "+sessionId);
 
		IQuiz quizProxy = QuizSessions.map.get(sessionId);
 
		if(quizProxy == null ){
			// Back button of the browser..
			URI uri = UriBuilder.fromPath(request.getContextPath()).path("/../../index.html")
					.build();
			return Response.seeOther(uri).build(); 
		}
 
		boolean correct = quizProxy.verifyAnswerAndReward(playerAnswer);
 
		int score = quizProxy.getScore();
		String playerName = quizProxy.getPlayerName();
 
		URI uri = null;
		if(!correct){
			System.out.println("Failed Quiz!");
			quizProxy.end();
			quizProxy = null;
			QuizSessions.map.remove(sessionId);
 
			uri = UriBuilder.fromPath(request.getContextPath()).path("/../../end.jsp")
					.queryParam("playerName",playerName)
					.queryParam("score",score)
					.build();
 
		} else {
			String question = quizProxy.generateQuestionAndAnswer();
 
			uri = UriBuilder.fromPath(request.getContextPath()).path("/../../quiz.jsp")
					.queryParam("question",question)
					.queryParam("score",score)
					.build();
		}
 
		return Response.seeOther(uri).build(); 
	}
	
	@GET
	@Path("/played-quizzes-number")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPlayedQuizzesNumber() throws NamingException, ServletException, IOException {
		long number = playedQuizzesCounterProxy.getNumber();
		return Response.ok(number).build(); 
	}
}
