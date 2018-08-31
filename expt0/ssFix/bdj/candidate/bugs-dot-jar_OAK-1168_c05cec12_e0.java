package com.jsandusky.dungeon;


//Generates random nested rooms for more interesting dungeons
public class InnerRoomPostFX implements DungeonPass {

	@Override
	public void execute(RandomDungeon d) {
		for (int cycles = 0; cycles < 400; ++cycles) {
			int randX = d.getRand(1, d.xSize() - 2);
			int randY = d.getRand(1, d.ySize() - 2);
			int randDim = d.getRand(6,12);
			boolean jump = false;
			for (int i = randX; i < randX+randDim && !jump; ++i) {
				for (int j = randY; j < randY+randDim && ! jump; ++j) {
					if (d.getCell(i,j) != 2) //dirt floor
						jump = true; //do go
				}
			}
			if (jump)
				continue;
			for (int i = randX+1; i < randX+randDim-1; ++i) {
				for (int j = randY+1; j < randY+randDim-1; ++j) {
					if (i == randX+1 || i == randX+randDim-2)
						d.setCell(i,j,1);
					else if (j == randY+1 || j == randY+randDim-2)
						d.setCell(i,j,1);
					else
						d.setCell(i,j, 2);
				}
			}
			//give it a door
			int dir = d.getRand(0,3);
			switch (dir) {
				case 0: //north
					if (d.usingDoors())
						d.setCell(randX+(randDim/2),randY+1, 5);
					else
						d.setCell(randX+(randDim/2),randY+1, d.tileDirtFloor);
					break;
				case 1: //east
					if (d.usingDoors())
						d.setCell(randX+1,randY+(randDim/2), 5);
					else
						d.setCell(randX+1,randY+(randDim/2), d.tileDirtFloor);
					break;
				case 2: //south
					if (d.usingDoors())
						d.setCell(randX+(randDim/2), randY+randDim-2, 5);
					else
						d.setCell(randX+(randDim/2), randY+randDim-2, d.tileDirtFloor);
					break;
				case 3: //west
					if (d.usingDoors())
						d.setCell(randX+randDim-2,randY+(randDim/2), 5);
					else
						d.setCell(randX+randDim-2,randY+(randDim/2), d.tileDirtFloor);
					break;
			}
		}
	}

}
