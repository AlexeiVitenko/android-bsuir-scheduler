package by.bsuir.scheduler.parser;

public interface ParserListiner {
	void onComplete();
	void onException(Exception e);
}
