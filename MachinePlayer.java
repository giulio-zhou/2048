public class MachinePlayer {

	Board currBoard;
	Board[] possibleMoves;

	public MachinePlayer(Board currBoard) {
		this.currBoard = currBoard;
		this.possibleMoves = new Board[4];
	}	

	public static double boardEval(Board inputBoard) {
		int numEmpty = inputBoard.size*inputBoard.size - inputBoard.numTiles();
		double val  = 0;		
		Board currBoard = inputBoard;		

		for (int j = 0; j < numEmpty; j++) {
			if (currBoard.tile_array[j] == null) {
				Board newBoard = currBoard.boardCopy();
				newBoard.addTile(2, j % newBoard.size, j / newBoard.size);
				Board newBoard4 = currBoard.boardCopy();
				newBoard.addTile(4, j % newBoard.size, j / newBoard.size);
				val += 0.9*((double) isDecreasing(newBoard4) + (-25)*(currBoard.size * currBoard.size - newBoard4.numTiles())
												 + 1.5*smoothness(newBoard4))/numEmpty;
				val += 0.1*((double) isDecreasing(newBoard4) + (-25)*(currBoard.size * currBoard.size - newBoard4.numTiles())
												 + 1.5*smoothness(newBoard4))/numEmpty;
			}
		}
		return val;
	}

	public static char makeMove2(Board inputBoard, int depth) {
		char[] moves = {'w', 'a', 's', 'd'};
		double minimum = Double.MAX_VALUE;
		char bestMove = 'c';
		for (int i = 0; i < 4; i++) {
			Board currBoard = inputBoard.boardCopy();
			boolean moved = currBoard.applyMove(currBoard.nextMove(moves[i]));
			if (moved) {
				double boardVal = evaluate2(currBoard, depth);
				if (boardVal < minimum) {
					minimum = boardVal;
					bestMove = moves[i];
				}
			}
		}
		return bestMove;
	}

	public static double evaluate2(Board inputBoard, int depth) {
		char[] moves = {'w', 'a', 's', 'd'};
		double minimum = Double.MAX_VALUE;
		char bestMove = 'c';
		double average = 0;
		int num_elems = 0;

		//if (depth == 0) {
			for (int i = 0; i < 4; i++) {
				Board currBoard = inputBoard.boardCopy();
				boolean moved = currBoard.applyMove(currBoard.nextMove(moves[i]));
				if (moved) {
					double boardVal = 0;
					 if (currBoard.hasEnded()) {
						boardVal = 400;
					} else if (depth == 0) {
						boardVal = boardEval(currBoard);
					} else {
						boardVal = evaluate2(currBoard, depth - 1);
					}
						average += boardVal;
						num_elems++;
					/*if (boardVal < minimum) {
						minimum = boardVal;
						bestMove = moves[i];
					}*/
					if (currBoard.maxTile() == 2048) {	
						boardVal -= -300;
					}

				}
			}
			return average/num_elems;
		//}
	}

	public static char evaluate(Board inputBoard) {
		char[] moves = {'w', 'a', 's', 'd'};
		double minimum = Double.MAX_VALUE;
		char bestMove = 'c';		

		for (int i = 0; i < 4; i++) {
			Board currBoard = inputBoard.boardCopy();
			boolean moved = currBoard.applyMove(currBoard.nextMove(moves[i]));
			if (moved) {
				double boardVal = boardEval(currBoard);
				/*
				int numEmpty = currBoard.size*currBoard.size - currBoard.numTiles();
				double boardVal = 0;
				for (int j = 0; j < numEmpty; j++) {
					if (currBoard.tile_array[j] == null) {
						
						Board newBoard = currBoard.boardCopy();	
						newBoard.addTile(2, j % newBoard.size, j / newBoard.size);
						Board newBoard4 = currBoard.boardCopy();		
						newBoard4.addTile(4, j % newBoard.size, j / newBoard.size);
						boardVal += 0.9*((double) isDecreasing(newBoard) + 
								(-15)*(currBoard.size * currBoard.size - 
									newBoard.numTiles()) + 1.5*smoothness(newBoard))/numEmpty;
						boardVal += 0.1*((double) isDecreasing(newBoard4) +
								(-15)*(currBoard.size * currBoard.size -
									newBoard4.numTiles()) + 1.5*smoothness(newBoard4))/numEmpty;
					}*/
				
				//}
				//System.out.println(currBoard);
				//System.out.println("This board has boardVal = " + boardVal + " and bestMove: " + moves[i]);
			
				if (boardVal < minimum) {
					minimum = boardVal;
					bestMove = moves[i];
				}
			}
		}
		return bestMove;
	}

	public static int isDecreasing(Board inputBoard) {
		int minimum = Integer.MAX_VALUE;
		for (int x = 0; x < inputBoard.size; x += inputBoard.size-1) {
			for (int y = 0; y < inputBoard.size; y += inputBoard.size-1) {		
				int xChange = (x == 0) ? 1:-1;
				int yChange = (y == 0) ? 1:-1;
				int horzScore = 0;
				
				if (inputBoard.tile_array[x + y*inputBoard.size] == null) {
					horzScore += 50;
				}	
				
				for (int i = y; i > -1 && i < inputBoard.size; i += yChange) {
					for (int j = x; j > -1 && j < inputBoard.size; j += xChange) {
						Tile currTile = inputBoard.tile_array[j + i*inputBoard.size];
						if (currTile instanceof Tile) {
							int spaceCounter = j + xChange;
							Tile nextTile = null;
							int tempTotal = -1;
							while (spaceCounter > -1 && spaceCounter < inputBoard.size &&
													 !(nextTile instanceof Tile)){
								nextTile =  inputBoard.tile_array[i*inputBoard.size + spaceCounter];
								spaceCounter += xChange;
								tempTotal += 1;
							}
							if (nextTile instanceof Tile) {
								//horzScore += (currTile.getValue() > nextTile.getValue()) ? 
								   //log2(currTile.getValue()) - log2(nextTile.getValue()):
								   //log2(nextTile.getValue()) - log2(currTile.getValue()) + 8;
								horzScore += log2(nextTile.getValue()) - log2(currTile.getValue()) + tempTotal;
							}
						}
					}
				}
				
				int vertScore = 0;
				for (int i = x; i > -1 && i < inputBoard.size; i += xChange) {
					for (int j = y; j > -1 && j < inputBoard.size; j += yChange) {
						Tile currTile = inputBoard.tile_array[i + j*inputBoard.size];
						if (currTile instanceof Tile) {
							int spaceCounter = j += yChange;
							Tile nextTile = null;
							int tempTotal = -1;
							while (spaceCounter > -1 && spaceCounter < inputBoard.size &&
													!(nextTile instanceof Tile)) {
								nextTile = inputBoard.tile_array[i + spaceCounter*inputBoard.size];
								spaceCounter += yChange;
								tempTotal += 1;
							}
							if (nextTile instanceof Tile) {
								//vertScore += (currTile.getValue() > nextTile.getValue()) ?                                                                   			  log2(currTile.getValue()) - log2(nextTile.getValue()):
                                                                   //log2(nextTile.getValue()) - log2(currTile.getValue()) + 8;
								vertScore += log2(nextTile.getValue()) - log2(currTile.getValue()) + tempTotal;
							}
						}
					}
				}	
				int local_min = horzScore + vertScore;
				Tile cornerTile = inputBoard.tile_array[x + y*inputBoard.size];
				if (cornerTile instanceof Tile && inputBoard.maxTile() == cornerTile.getValue()) { 
					local_min -= 50;
				}
				//System.out.println("horzScore is: " + horzScore + " and vertScore is: " + vertScore);	
				//int[] maxLoc = inputBoard.maxLocation();
				//if (maxLoc[0] == 0 || maxLoc[0] == inputBoard.size - 1 || maxLoc[1] == 0 || maxLoc[1] == inputBoard.size - 1) {
				//	local_min -= 80;
				//}
	
				minimum = (local_min < minimum) ? local_min:minimum;
			}
		}

		return minimum;		
	}

	public static int log2(int num) {
		int output = 1;
		while (1 << output < num) {
			output++;
		}
		return output;
	}

	public static int smoothness(Board inputBoard) {
		int total = 0;
		for (int i = 0; i < inputBoard.size; i++) {
			for (int j = 0; j < inputBoard.size; j++ ){
				Tile currTile = inputBoard.tile_array[i + j*inputBoard.size];
				if (currTile instanceof Tile) {
					int xCount = i+1, yCount = j+1;
					Tile nextTile = null;
					while (xCount < inputBoard.size && nextTile == null) {
						nextTile = inputBoard.tile_array[xCount + j*inputBoard.size];
						xCount++;					
					}
	
					if (nextTile instanceof Tile) {
						total += Math.abs(log2(currTile.getValue()) - log2(nextTile.getValue()));	
					}	

					nextTile = null;
					while (yCount < inputBoard.size && nextTile == null) {
						nextTile = inputBoard.tile_array[i + yCount*inputBoard.size];
						yCount++;
					}

					if (nextTile instanceof Tile) {
						total += Math.abs(log2(currTile.getValue()) - log2(nextTile.getValue()));
					}
				}
			}
		}	
		return total;
	}

	public static int pieceDiff(Board prev, Board after) {
		return 0*(prev.numTiles() - after.numTiles());
	}

	public static byte[] BoardToByte(Board inputBoard) {
		byte[] output = new byte[inputBoard.size * inputBoard.size];
		for (int i = 0; i < inputBoard.size*inputBoard.size; i++) {
			if (inputBoard.tile_array[i] != null) {
				output[i] = (byte)inputBoard.tile_array[i].getValue();
			}
		}
		return output;
	}

	public static void main(String[] args) {
		Board m1 = new Board();
		//byte[] m_array = BoardToByte(m);
		//for (int i = 0; i < 16; i++ ){
			//System.out.print(m_array[i] + " ");
		//}
		//for (int i = 0; i < 6; i++) {
		//	m.genRandTile();
		//}
		for (int i = 0 ; i < m1.size; i++) {
			for (int j = 0; j < m1.size; j++) {
				m1.addTile(2, i, j);
			}
		}

		System.out.println(m1);
		System.out.println(isDecreasing(m1));
		System.out.println(smoothness(m1));

		m1.addTile(128, 0, 0);
		//System.out.println(
		System.out.println(m1);
		System.out.println(isDecreasing(m1));
		System.out.println(smoothness(m1));

		m1.addTile(128, 0, 3);
		System.out.println(m1);
		System.out.println(isDecreasing(m1));
		System.out.println(smoothness(m1));
	}

}
