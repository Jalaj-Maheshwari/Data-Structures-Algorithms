/*

Problem:

Given a singly linked list, determine if its a palindrome. Return 1 or 0 denoting if its a palindrome or not, respectively.

Notes:

Expected solution is linear in time and constant in space.
For example,

List 1-->2-->1 is a palindrome.
List 1-->2-->3 is not a palindrome.

*/

/**
 * Definition for singly-linked list.
 * class ListNode {
 *     public int val;
 *     public ListNode next;
 *     ListNode(int x) { val = x; next = null; }
 * }
 */

public class PalindromeList {
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

// Time Complexity: O(n)
// Space Complexity: O(1)

