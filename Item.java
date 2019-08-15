import java.util.Arrays;
import java.util.List;

enum Item {

	ROCK, PAPER, SCISSORS, LIZARD, SPOCK;

	List<Item> losesTo;

	boolean losesTo(Item other) {
		return this.losesTo.contains(other);
	}

	static {
		ROCK.losesTo = Arrays.asList(PAPER, SPOCK);
		PAPER.losesTo = Arrays.asList(SCISSORS, LIZARD);
		SCISSORS.losesTo = Arrays.asList(ROCK, SPOCK);
		LIZARD.losesTo = Arrays.asList(SCISSORS, ROCK);
		SPOCK.losesTo = Arrays.asList(PAPER, LIZARD);
	}
}
