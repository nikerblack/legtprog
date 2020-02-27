package com.letsprog.learning.ejb3.server.api;

import com.letsprog.learning.ejb3.server.api.entities.LevelQuestion;

public interface ILocalQuizQuestionGenerator {
	LevelQuestion generateQuestion(int level);
}
