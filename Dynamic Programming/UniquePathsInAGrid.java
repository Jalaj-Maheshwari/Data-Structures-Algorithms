/*
Problem:

Given a grid of size m * n, lets assume you are starting at (1,1) and your goal is to reach (m,n). At any instance, if you are on (x,y), you can either go to (x, y + 1) or (x + 1, y).

Now consider if some obstacles are added to the grids. How many unique paths would there be?
An obstacle and empty space is marked as 1 and 0 respectively in the grid.

Example :
There is one obstacle in the middle of a 3x3 grid as illustrated below.

[
  [0,0,0],
  [0,1,0],
  [0,0,0]
]
The total number of unique paths is 2.

Solution:
*/

public class UniquePathsInAGrid {
    public int uniquePathsWithObstacles(ArrayList<ArrayList<Integer>> A) {
        int rows = A.size();
        int columns = A.get(0).size();
        
        // Handling the base case where it is a 1x1 matrix
        if(rows == 1 && columns == 1){
            if(A.get(rows - 1).get(columns - 1) == 0){
                return 1;
            } else {
                return 0;    
            } 
        }

        int startingCellValue = (A.get(0).get(0) == 1) ? 0 : 1;
        
        // Populating the first row of the matrix
        for(int i = 1; i < columns; i++){
            if(A.get(0).get(i) == 1){
                A.get(0).set(i, 0);
            } else {
                if(i == 1){
                    A.get(0).set(i, startingCellValue);
                } else {
                    A.get(0).set(i, A.get(0).get(i - 1));    
                }
            }    
        }
        
        // Populating the first column of the matrix
        for(int i = 1; i < rows; i++){
            if(A.get(i).get(0) == 1){
                A.get(i).set(0, 0);
            } else {
                if(i == 1){
                    A.get(i).set(0, startingCellValue);
                } else {
                    A.get(i).set(0, A.get(i - 1).get(0));    
                }
            }    
        }
        
        int waysToReachLeftCell, waysToReachTopCell;
        for(int i = 1; i < rows; i++){
            for(int j = 1; j < columns; j++){
                if(A.get(i).get(j) != 1){
                    waysToReachLeftCell = A.get(i).get(j - 1);
                    waysToReachTopCell = A.get(i - 1).get(j);
                    A.get(i).set(j, waysToReachLeftCell + waysToReachTopCell);
                } else {
                    A.get(i).set(j, 0);
                }
            }
        }
        
        return A.get(rows - 1).get(columns - 1);
    }
}

// Time Complexity: O(rows * columns)
// Space Complexity: O(1) as same input matrix is modified. 
