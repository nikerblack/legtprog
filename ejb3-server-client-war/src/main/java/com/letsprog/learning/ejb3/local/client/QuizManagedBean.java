package com.letsprog.learning.ejb3.local.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.NamingException;

import com.letsprog.learning.ejb3.server.api.ILocalPlayedQuizzesCounter;
import com.letsprog.learning.ejb3.server.api.ILocalQuiz;
import com.letsprog.learning.jndi.lookup.ejb3.LookerUp;

@ManagedBean(name="quizManagedBean")
@SessionScoped
public class QuizManagedBean {

	private String playerName;
	private int score = 0;
	private String question = "";
	private int answer;
 
	// I'm not DI'ing it!
	private ILocalQuiz quizProxy;
 
	@EJB
	private ILocalPlayedQuizzesCounter playedQuizzesCounterProxy;
 
	@PostConstruct
	public void setup(){
		System.out.println("Setting up after creating the JSF managed bean.");
	}
	
	public String start() throws NamingException{
		
		playedQuizzesCounterProxy.increment();
 
		if(quizProxy != null ){
			quizProxy.end();
			quizProxy = null;
			System.out.println("Refreshing Quizz!");
		}
		
		//--- EJB Lookup in same WAR
		String moduleName = "ejb3-server-client-war"; // WAR name OR ejb-jar.xml module-name
		String beanName = "QuizBean";
		String interfaceQualifiedName = ILocalQuiz.class.getName();
		
		// No password required <= Component deployed in the same container
		LookerUp wildf9Lookerup = new LookerUp();
		
		// We could instead the following method by giving the exact JNDI name :
		// wildf9Lookerup.findSessionBean("java:global/ejb3-server-client-war/QuizBean!com.letsprog.learning.ejb3.server.api.ILocalQuiz")
		quizProxy = (ILocalQuiz) wildf9Lookerup.findLocalSessionBean(moduleName,beanName,interfaceQualifiedName); // Good
		
		quizProxy.begin(playerName);
		setQuestion(quizProxy.generateQuestionAndAnswer());
		
		return "quiz.xhtml";
	}
 
	public String verifyAnswer(){
 
		if(quizProxy == null ){
			System.out.println("quizProxy is null");
			return "index.xhtml";
		}
		
		boolean correct = quizProxy.verifyAnswerAndReward(answer);
 
		setScore(quizProxy.getScore());
 
		if(!correct){
			System.out.println("Failed Quizz!");
			quizProxy.end();
			quizProxy = null;
			return "end.xhtml";
 
		} else {
			setQuestion(quizProxy.generateQuestionAndAnswer());
			return "quiz.xhtml";
		}
 
	}
	
	public long getPlayedQuizzesNumber(){
		return playedQuizzesCounterProxy.getNumber();
	}
 
	public int getScore() {
		return score;
	}
 
	public void setScore(int score) {
		this.score = score;
	}
 
	public String getQuestion() {
		return question;
	}
 
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getPlayerName() {
		return playerName;
	}
 
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public int getAnswer() {
		return answer;
	}
 
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	
	@PreDestroy
	public void cleanUp(){
		System.out.println("Cleaning up before destroying the JSF managed bean.");
		quizProxy.end();
	}
}
