import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class RPSLS implements Serializable {

	private static final Random RANDOM = new Random();
	private static final Scanner sc = new Scanner(System.in);
	private static DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.##");
	private Item last;
	private String username;
	private int[][] markovChain;
	private int[] stats;
	private int nbThrows;
	private int length;

	RPSLS() {
		this.stats = new int[]{0, 0, 0};
		this.nbThrows = 0;
		this.length = Item.values().length;
		this.markovChain = new int[this.length][this.length];
		this.username = this.readUsername();
		this.loadAI();
	}

	void play() {

		System.out.println("ROCK - PAPER - SCISSORS - LIZARD - SPOCK");
		System.out.println("Type STOP to stop the game");
		System.out.println("Make your choice: ");
		while (sc.hasNextLine()) {

			String input = sc.nextLine();
			if (input.equals("STOP")) {
				this.saveAI();
				break;
			}

			Item choice;
			try {
				choice = Item.valueOf(input.toUpperCase());
			} catch (Exception e) {
				System.out.println("Invalid choice!");
				continue;
			}

			Item aiChoice = this.nextMove(this.last);
			this.nbThrows++;

			if (this.last != null)
				this.updateMarkovChain(this.last, choice);

			this.last = choice;

			System.out.println("Computer choice: " + aiChoice);

			if (aiChoice.equals(choice)) {
				System.out.println("Tie!");
				this.stats[1]++;
			} else if (aiChoice.losesTo(choice)) {
				System.out.println("You win!");
				this.stats[0]++;
			} else {
				System.out.println("You lose!");
				this.stats[2]++;
			}

			System.out.println("Make your choice: ");
		}
		sc.close();

		this.displayStats();
	}

	private String readUsername() {

		System.out.println("Type your username: ");
		return sc.nextLine();
	}

	private void updateMarkovChain(Item previous, Item next) {
		this.markovChain[previous.ordinal()][next.ordinal()]++;
	}

	private Item nextMove(Item previous) {

		if (this.nbThrows < 1)
			return Item.values()[RANDOM.nextInt(this.length)];

		int nextIndex = 0;

		for (int i = 0; i < this.length; i++) {

			int previousIndex = previous.ordinal();
			if (this.markovChain[previousIndex][i] > this.markovChain[previousIndex][nextIndex])
				nextIndex = i;
		}

		Item predictedNext = Item.values()[nextIndex];
		List<Item> losesTo = predictedNext.losesTo;
		return losesTo.get(RANDOM.nextInt(losesTo.size()));
	}

	private void displayStats() {

		System.out.println("Win stats");

		int total = this.stats[0] + this.stats[1] + this.stats[2];

		System.out.println("You: " + this.stats[0] + "/" + total + " (" + DECIMAL_FORMATTER.format(this.stats[0] / (float) total * 100f) + "%)");
		System.out.println("Tie: " + this.stats[1] + "/" + total + " (" + DECIMAL_FORMATTER.format(this.stats[1] / (float) total * 100f) + "%)");
		System.out.println("AI: " + this.stats[2] + "/" + total + " (" + DECIMAL_FORMATTER.format(this.stats[2] / (float) total * 100f) + "%)");
	}

	private void loadAI() {

		String filename = this.username + ".save";
		if (!(new File(filename).exists())) {
			for (int[] row : this.markovChain)
				Arrays.fill(row, 0);
			System.out.println("AI created successfully");
			return;
		}
		try {
			FileInputStream file = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(file);
			this.markovChain = (int[][]) in.readObject();
			in.close();
			file.close();
			System.out.println("AI loaded successfully");
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error while loading AI");
		}
	}

	private void saveAI() {

		String filename = this.username + ".save";
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(this.markovChain);
			out.close();
			file.close();
			System.out.println("AI saved");
		} catch (IOException e) {
			System.out.println("Error while saving AI");
		}
	}
}
