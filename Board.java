import java.util.*;
import java.io.*;
import java.lang.*;

public class Board {
	Tile[] tile_array;
	boolean has_won;
	boolean has_lost;
	int size;
	
	public Board() {
		this.size = 4;
		this.tile_array = new Tile[size*size];
		this.has_won = false;
		this.has_lost = false;
	}
	
	public Board(int size) {
		this.size = size;
		this.tile_array = new Tile[size*size];
		this.has_won = false;
		this.has_lost = false;
	}
	
	public void addTile(int value, int xPosition, int yPosition) {
		this.tile_array[xPosition + this.size*yPosition] = new Tile(value, xPosition, yPosition);
	}

	public void removeTile(int xPosition, int yPosition) {
		this.tile_array[xPosition + size*yPosition] = null;
	}

	public boolean slideTile(Tile currTile, String direction) {
		int x_dir = 0, y_dir = 0;
		boolean moved = false;
		//System.out.println("1");
		if (direction == "up") {
			y_dir = -1;
		} else if (direction == "down") {
			y_dir = 1;
		} else if (direction == "left") {
			x_dir = -1;
		} else if (direction == "right") {
			x_dir = 1;
		} else {
			return false;
		}
	
		int x = currTile.xPosition + x_dir, y = currTile.yPosition + y_dir;
		int next_index = array_index(x, y);
		while (within_boundaries(x, y) && next_index >= 0 && next_index < size*size && !(this.tile_array[next_index] instanceof Tile)) {
			//System.out.println(next_index + " " + x_dir + " " + y_dir + " " + (next_index % this.size - x_dir) + " " + (next_index / this.size - y_dir));
			this.addTile(currTile.getValue(), next_index % this.size, next_index / this.size);
			this.removeTile(currTile.xPosition, currTile.yPosition);	
			currTile = this.tile_array[next_index];
			x += x_dir;
			y += y_dir;
			next_index = array_index(x, y);
			moved = true;
		}
		//System.out.println("At least I tried!");
		if (next_index >= 0 && next_index < size*size && this.tile_array[next_index] != null) {
			if (currTile.getValue() == this.tile_array[next_index].getValue() && !this.tile_array[next_index].hasMerged()) {
				this.tile_array[next_index] = currTile.combine(this.tile_array[next_index]);
				this.removeTile(currTile.xPosition, currTile.yPosition);
				moved = true;
			}
		}	
		return moved;
	}

	public boolean within_boundaries(int x, int y) {
		return x > -1 && x < this.size && y > -1 && y < this.size;
	}

	public int array_index(int x, int y) {
		return x + this.size*y;
	}

	public Board boardCopy() {
		Board output = new Board(size);
		for (int i = 0; i < size*size; i++) {		
			Tile currTile = tile_array[i];
			if (currTile != null) {
				output.tile_array[i] = new Tile(currTile.getValue(), currTile.getX(), currTile.getY(), currTile.hasMerged());
			}
		}
		return output;	
	}

	public void genRandTile() {
		double num_empty = 0;
		for (int i = 0; i < size*size; i++) {
			if (!(tile_array[i] instanceof Tile)) {
				num_empty++;
			}
		}
	
		for (int i = 0; i < size*size; i++) {
			if (!(tile_array[i] instanceof Tile)) {
				if (Math.random() < 1/num_empty) {
					//Random rand = new Random();
					//int rand_int = rand.nextInt(10) + 1;
					//rand_int = (int) Math.round(Math.pow((double)2, rand_int));
					int value = (Math.random() < 0.90) ? 2:4;
					this.addTile(value, i % size, i / size);
					return;
				}	
				num_empty--;			
			}
		}
	}

	public void genRandTile(int num) {
                double num_empty = 0;
                for (int i = 0; i < size*size; i++) {
                        if (!(tile_array[i] instanceof Tile)) {
                                num_empty++;
                        }
                }

                for (int i = 0; i < size*size; i++) {
                        if (!(tile_array[i] instanceof Tile)) {
                                if (Math.random() < 1/num_empty) {
                                        int value = num;
                                        this.addTile(value, i % size, i / size);
                                        return;
                                }
                                num_empty--;
                        }
                }
	}	

	public boolean hasEmpty() {
		for (int i = 0; i < size*size; i++) {
			if (!(this.tile_array[i] instanceof Tile)) {
				return true;
			}
		}
		return false;
	}

	public int numTiles() {
		int total = 0;
		for (int i = 0; i < size*size; i++) {
			if (tile_array[i] instanceof Tile) {
				total++;
			}
		}
		return total;
	}

	public int maxTile() {
		int maximum = 0;
		for (int i = 0; i < size*size; i++) {
			if (tile_array[i] instanceof Tile) {
				maximum = (tile_array[i].getValue() > maximum) ? tile_array[i].getValue():maximum;
			}	
		}
		return maximum;
	}

	public int[] maxLocation() {
		int maximum = 0;
		int[] maxLoc = new int[2];
		for (int i = 0; i < size*size; i++) {
			if (tile_array[i] instanceof Tile) {
				Tile currTile = tile_array[i];
				if (currTile.getValue() > maximum) {
					maximum = currTile.getValue();
					maxLoc[0] = currTile.getX();
					maxLoc[1] = currTile.getY();
				}	
			}
		}	
		return maxLoc;
	}

	public String nextMove(char next) {
		String next_move;
		if (next == 'w') {
			next_move = "up";
		} else if (next == 'a') {
			next_move = "left";
		} else if (next == 's') {
			next_move = "down";
		} else if (next == 'd') {
			next_move = "right";
		} else {
			next_move = "nothing";
		}
		return next_move;
	}

	public boolean applyMove(String nextMove) {
		int start_index= -1, change = 0;
		boolean moved = false;
		if (nextMove == "up" || nextMove == "left") {
			start_index = 0;
			change = 1;
		} else if (nextMove == "right" || nextMove == "down") {
			start_index = this.size*this.size - 1;
			change = -1;
		}

		while (start_index >= 0 && start_index < this.size*this.size) {
			//System.out.println("lol " + start_index + " " + this.tile_array[start_index]);
			if (this.tile_array[start_index] instanceof Tile) {
			//System.out.println("I really tried dammit");
				moved = this.slideTile(this.tile_array[start_index], nextMove) || moved;
			}
			start_index += change;
		}
		return moved;
	}

        public void resetMerge() {
                for (int i = 0; i < size*size; i++) {
                        Tile currTile = tile_array[i];
                        if (currTile != null) {
                                currTile.merged = false;
                        }
                }
        }

	public boolean equals(Board inputBoard) {
		//System.out.println("-----------");
		//System.out.println(this);
		//System.out.println(inputBoard);
		for (int i = 0; i < size*size; i++) {
			Tile currTile = tile_array[i];
			Tile otherTile = inputBoard.tile_array[i];
			//System.out.println(currTile + " " + otherTile);
			if (currTile instanceof Tile && otherTile instanceof Tile) {
				if (currTile.getValue() != otherTile.getValue()) {
					return false;
				} 
			} else {
				//System.out.println("I'm here");
				if (currTile != null || otherTile != null) { 
					return false;
				}
			}
		}
		//System.out.println(true);
		return true;
	}

	public boolean hasEnded() {
		char[] moves = {'w', 'a', 's', 'd'};
		int numPossible = 0;
		for (char move: moves) {
			Board currBoard = this.boardCopy();
			currBoard.applyMove(this.nextMove(move));
			if (!this.equals(currBoard)) {
				numPossible++;
			}
		}
		return numPossible == 0;
	}

	public String toString() {
		String arr = new String("");
		for (int i = 0; i < size*size; i++) {
			if (i % size == 0) {
				arr += new String("\n|");
			}
		
			String curr = ((this.tile_array[i] instanceof Tile)) ? new String(Integer.toString(this.tile_array[i].getValue())):
															new String(" ");
			arr += curr;
			arr += "|";
		}
		return arr;
	}

	public static void main(String[] args) {

		int [] vals = new int[12];
		for (int i: vals) {
			i = 0;
		}
		//for (int i = 0; i < 100; i++) {
		Board gameBoard = new Board();		
		//for (int i = 0; i < 4; i++) {
			//gameBoard.genRandTile();
		//}
		Scanner input = new Scanner(System.in);
		boolean moved = false;
		gameBoard.genRandTile();
		while (!gameBoard.hasEnded()) {
			if (moved) {
				gameBoard.genRandTile();
				gameBoard.resetMerge();
			}
			System.out.println(gameBoard);
			//System.out.println(MachinePlayer.isDecreasing(gameBoard));
			//System.out.println(MachinePlayer.evaluate(gameBoard));
			//char next_move = input.next().charAt(0);
			char next_move = MachinePlayer.makeMove2(gameBoard, 7);
			moved = gameBoard.applyMove(gameBoard.nextMove(next_move));
			/*try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.exit(1);
			}*/
			//System.out.println(next_move);
		}
		/*	
		if (i % 10 == 0) {
		System.out.println(i);
		}
		System.out.println(gameBoard);
		System.out.println(MachinePlayer.log2(gameBoard.maxTile()));
		vals[MachinePlayer.log2(gameBoard.maxTile())]++;
		if (MachinePlayer.log2(gameBoard.maxTile()) == 11) {
			System.out.println(gameBoard);
		}
		}*/
		
		for (int i: vals) {
			System.out.print(i + " ");
		}
	}

}
