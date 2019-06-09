/*

- High level Approach:

1. Calculate the centre of the list from the total number of nodes and then reverse the list after that centre. 
   centreNode = (nodeCount / 2) + 1 [Works well for both even & odd length list]

2. Now compare the node value from start till centre - 1 / centre - 2 [depending on odd / even list] vs centre + 1 till end of list.

3. If all the values match return 1 else return 0.

- Note: 
  For list with even length, it is imp to check whether the centre node and centreNode - 1 are same, 
  because if that holds true then only the list will be palindrome. 

- Time Complexity: O(n)
- Space Complexity: O(1)

*/

/**
 * Definition for singly-linked list.
 * class ListNode {
 *     public int val;
 *     public ListNode next;
 *     ListNode(int x) { val = x; next = null; }
 * }
 */

public class Solution {
    public int lPalin(ListNode A) {
        int centre = 0;
        int nodeCount = 0;
        ListNode temp = A;
        while(temp != null){
            nodeCount++;
            temp = temp.next;
        }
        if(nodeCount == 1){
            return 1;
        }
        
        centre = nodeCount / 2 + 1;
        ListNode curr = A;
        ListNode prev = null;
        
        int counter = 1;
        
        ListNode centreNode = null;
        temp = null;
        
        while(curr != null){
            if(counter == centre){
                if((nodeCount % 2 == 0) && prev.val != curr.val){
                    return 0;
                } else {
                    centreNode = curr;
                }
            }    
            if(counter >= (centre + 2)){
                temp = curr.next;
                curr.next = prev;
                if(counter == centre + 2){
                    prev.next = null;
                } 
            }
            prev = curr;
            
            if(counter <= centre + 1){
                curr = curr.next;
            } else {    
                if(counter == nodeCount){
                    curr = null;
                } else {
                    curr = temp;    
                }
            }
            counter++;
        }
        centreNode.next = prev;
        
        // checking whether remaining half is identical
        curr = A;
        temp = centreNode.next;
        
        while(curr != centreNode && temp != null){
            if(curr.val != temp.val){
                return 0;
            } else {
                curr = curr.next;   
                temp = temp.next;
            }
        }
        return 1;
    }
}
