package by.bsuir.schedule.parser;

public interface ParserListiner {
	void onComplete();
	void onException(Exception e);
}
