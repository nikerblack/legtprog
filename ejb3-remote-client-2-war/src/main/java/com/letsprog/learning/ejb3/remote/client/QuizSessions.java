package com.letsprog.learning.ejb3.remote.client;

import java.util.HashMap;
import java.util.Map;

import com.letsprog.learning.ejb3.server.api.IQuiz;

public class QuizSessions {
	static public Map<String,IQuiz> map = new HashMap<String,IQuiz>();
}
