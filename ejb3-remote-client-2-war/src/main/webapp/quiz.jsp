<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Quizzzz</title>
</head>
 
<body>
 
	<%
	String question = "";
	question = request.getParameter( "question" ) ;
 
	int score = 0;
	score = Integer.valueOf(request.getParameter( "score" )) ;
 
	%>		
	
	Score : <%=score%> <br/>
	Question : <%=question%> <br/>
 
	<form action="ws/play/answer" method="post">
		<p>
			Answer : <input type="text" name="playerAnswer" />
		</p>
		<input type="submit" value="Check it!" />
	</form>
	
</body>
</html>