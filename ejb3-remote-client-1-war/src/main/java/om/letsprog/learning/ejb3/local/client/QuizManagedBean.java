package om.letsprog.learning.ejb3.local.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.NamingException;

import com.letsprog.learning.ejb3.server.api.IRemotePlayedQuizzesCounter;
import com.letsprog.learning.ejb3.server.api.IRemoteQuiz;
import com.letsprog.learning.jndi.lookup.ejb3.LookerUp;

@ManagedBean(name="quizManagedBean")
@SessionScoped
public class QuizManagedBean {
	private String playerName;
	private int score = 0;
	private String question = "";
	private int answer;
 
	// I'm not DI'ing it!
	private IRemoteQuiz quizProxy;
 
	// Both of the following JNDI names are possible
//	@EJB(mappedName="ejb:/ejb3-server-war//PlayedQuizzesCounterBean!com.letsprog.learning.ejb3.server.api.IRemotePlayedQuizzesCounter")
	@EJB(mappedName="java:global/ejb3-server-war/PlayedQuizzesCounterBean!com.letsprog.learning.ejb3.server.api.IRemotePlayedQuizzesCounter")
	IRemotePlayedQuizzesCounter playedQuizzesCounterProxy;
 
	@PostConstruct
	public void setup(){
		System.out.println("Setting up after creating the JSF managed bean.");
	}
 
	public String start() throws NamingException{
 
		playedQuizzesCounterProxy.increment();
 
		if(quizProxy != null ){
			quizProxy.end();
			quizProxy = null;
			System.out.println("Refreshing Quiz!");
		}
 
		//--- EJB Lookup in same WAR
		String ejbsServerAddress = "127.0.0.1";
		int ejbsServerPort = 8080;
		String earName = ""; // EJB exist in a WAR and not in an EAR.
		String moduleName = "ejb3-server-war";
		String deploymentDistinctName = "";
		String beanName = "QuizBean";
		String interfaceQualifiedName = IRemoteQuiz.class.getName();
 
 
		// No password required <= Component deployed in the same container
		LookerUp wildf9Lookerup = new LookerUp(ejbsServerAddress, ejbsServerPort);
 
		// We could instead the following method by giving the exact JNDI name :
		// wildf9Lookerup.findSessionBean("ejb:/ejb3-server-war//QuizBean!com.letsprog.learning.ejb3.server.api.IRemoteQuiz?stateful");
		quizProxy = (IRemoteQuiz) wildf9Lookerup.findRemoteSessionBean(LookerUp.SessionBeanType.STATEFUL, earName, moduleName,deploymentDistinctName, beanName, interfaceQualifiedName);
 
		quizProxy.begin(playerName);
		setQuestion(quizProxy.generateQuestionAndAnswer());
 
		return "quiz.xhtml";
	}
 
	public String verifyAnswer(){
 
		if(quizProxy == null ){
			System.out.println("quizProxy is null");
		}
 
		boolean correct = quizProxy.verifyAnswerAndReward(answer);
 
		setScore(quizProxy.getScore());
 
		if(!correct){
			System.out.println("Failed Quiz!");
			quizProxy.end();
			quizProxy = null;
			return "end.jsp";
 
		} else {
			setQuestion(quizProxy.generateQuestionAndAnswer());
			return "quiz.xhtml";
		}
 
	}
 
	public long getPlayedQuizzesNumber(){
		return playedQuizzesCounterProxy.getNumber();
	}
 
	@PreDestroy
	public void cleanUp(){
		System.out.println("Cleaning up after destroying the JSF managed bean.");
		quizProxy.end();
		quizProxy = null;
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
}
