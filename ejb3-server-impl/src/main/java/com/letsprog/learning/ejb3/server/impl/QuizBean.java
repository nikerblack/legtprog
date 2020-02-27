package com.letsprog.learning.ejb3.server.impl;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import com.letsprog.learning.ejb3.server.api.ILocalQuiz;
import com.letsprog.learning.ejb3.server.api.ILocalQuizQuestionGenerator;
import com.letsprog.learning.ejb3.server.api.IQuiz;
import com.letsprog.learning.ejb3.server.api.IRemoteQuiz;
import com.letsprog.learning.ejb3.server.api.entities.LevelQuestion;

@Stateful
@Remote(IRemoteQuiz.class)
@Local(ILocalQuiz.class)
public class QuizBean implements IQuiz{

	@EJB
	ILocalQuizQuestionGenerator levelQuestionGenerator;
	
	private String playerName;
	private String askedQuestion;
	private Long expectedAnswer;
	private int score=0;
	private int level=1;
 
	public void begin(String playerName) {
		this.playerName = playerName;
	}
 
	public String generateQuestionAndAnswer() {
		LevelQuestion levelQuestion = levelQuestionGenerator.generateQuestion(level);
		askedQuestion = levelQuestion.getQuestion(); 
		expectedAnswer = levelQuestion.getExpectedAswer();
		return askedQuestion;
	}
 
	public boolean verifyAnswerAndReward(int result) {
		if(expectedAnswer == result){
			score++;
			level++;
			return true;
		}
		return false;
	}
 
	@Remove
	public void end() {
		System.out.println("QuizBean instance will be removed..");
	}
 
	// Some getters to be able to congratulate the player all with showing him name, score,...
 
	public String getPlayerName() {
		return playerName;
	}
 
	public String getAskedQuestion() {
		return askedQuestion;
	}
 
	public int getScore() {
		return score;
	}
}
