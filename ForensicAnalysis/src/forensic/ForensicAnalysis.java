package forensic;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Callsf buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {
          // Read the number of STRs
          int numberOfSTRs = StdIn.readInt();

          // Create an ArrayList to store STR objects
          List<STR> strList = new ArrayList<>();
  
          // Loop to read each STR
          for (int i = 0; i < numberOfSTRs; i++) {
              // Read the STR name and occurrences
              String strName = StdIn.readString();
              int occurrences = StdIn.readInt();
  
              // Create a new STR object and add it to the list
              strList.add(new STR(strName, occurrences));
          }
  
          // Convert ArrayList to array, ensuring it's never null
          STR[] strArray = strList.toArray(new STR[strList.size()]);
  
          // Create and return the Profile object
          return new Profile(strArray);
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        // WRITE YOUR CODE HERE
        treeRoot = insertPerson(treeRoot, name, newProfile);
}

private TreeNode insertPerson(TreeNode x, String name, Profile newProfile) {
    if (x == null) {
        return new TreeNode(name, newProfile, null, null);
    }
    int cmp = name.compareTo(x.getName());
    if (cmp < 0) {
        x.setLeft(insertPerson(x.getLeft(), name, newProfile));
    } else if (cmp > 0) {
        x.setRight(insertPerson(x.getRight(), name, newProfile));
    } else {
        x.setProfile(newProfile); // Update profile if name already exists
    }
    return x;
}

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {
        
        // WRITE YOUR CODE HERE
         return getMatchingProfileCountHelper(treeRoot, isOfInterest);
        }
        
        /**
         * Helper method to recursively count matching profiles.
         *
         * @param node the current node being examined
         * @param isOfInterest the search mode
         * @return the count of matching profiles in the subtree rooted at node
         */
        private int getMatchingProfileCountHelper(TreeNode node, boolean isOfInterest) {
            if (node == null) {
                return 0;
            }
        
            int count = 0;
            
            // Check if the current node's profile matches the search criteria
            if (node.getProfile().getMarkedStatus() == isOfInterest) {
                count = 1;
            }
        
            // Recursively check left and right subtrees
            count += getMatchingProfileCountHelper(node.getLeft(), isOfInterest);
            count += getMatchingProfileCountHelper(node.getRight(), isOfInterest);
        
            return count;         // update this line
        }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {

        // WRITE YOUR CODE HERE
        flagProfilesOfInterest(treeRoot);
    }
    private void flagProfilesOfInterest(TreeNode node) {
        if (node == null) {
            return;
        }
    
        // Process current node
        Profile profile = node.getProfile();
        STR[] strArray = profile.getStrs();
        int totalSTRs = strArray.length;
        int matchingSTRs = 0;
    
        String combinedSequence = firstUnknownSequence + secondUnknownSequence;
    
        for (STR str : strArray) {
            int profileOccurrences = str.getOccurrences();
            int sequenceOccurrences = numberOfOccurrences(combinedSequence, str.getStrString());
    
            if (profileOccurrences == sequenceOccurrences) {
                matchingSTRs++;
            }
        }
    
        // Mark profile if at least half of STRs match (round up)
        int threshold = (totalSTRs + 1) / 2; // This rounds up for odd numbers
        if (matchingSTRs >= threshold) {
            profile.setInterestStatus(true);
        }
    
        // Recursively process left and right subtrees
        flagProfilesOfInterest(node.getLeft());
        flagProfilesOfInterest(node.getRight());
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        // WRITE YOUR CODE HERE
            // Get the count of unmarked profiles
    int unmarkedCount = getMatchingProfileCount(false);
    
    // Create an array to store unmarked people's names
    String[] unmarkedPeople = new String[unmarkedCount];
    
    // If the tree is empty, return the empty array
    if (treeRoot == null) {
        return unmarkedPeople;
    }

    // Initialize a queue for level-order traversal
    Queue<TreeNode> queue = new Queue<>();
    queue.enqueue(treeRoot);
    
    int index = 0;

    // Perform level-order traversal
    while (!queue.isEmpty()) {
        TreeNode currentNode = queue.dequeue();
        
        // Check if the current node's profile is unmarked
        if (!currentNode.getProfile().getMarkedStatus()) {
            // Add the person's name (key) to the array
            unmarkedPeople[index++] = currentNode.getName();
        }

        // Enqueue the left child if it exists
        if (currentNode.getLeft() != null) {
            queue.enqueue(currentNode.getLeft());
        }

        // Enqueue the right child if it exists
        if (currentNode.getRight() != null) {
            queue.enqueue(currentNode.getRight());
        }
    }

    return unmarkedPeople;
    }// update this line


    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        treeRoot = removePersonHelper(treeRoot, fullName);
    }
    
    private TreeNode removePersonHelper(TreeNode node, String fullName) {
        if (node == null) return null;
    
        int cmp = fullName.compareTo(node.getName());
        if (cmp < 0) {
            node.setLeft(removePersonHelper(node.getLeft(), fullName));
        } else if (cmp > 0) {
            node.setRight(removePersonHelper(node.getRight(), fullName));
        } else {
            // Node to delete found
            
            // Case 1: No children or only one child
            if (node.getLeft() == null) return node.getRight();
            if (node.getRight() == null) return node.getLeft();
    
            // Case 2: Two children
            TreeNode minNode = findMin(node.getRight());
            node.setName(minNode.getName());
            node.setProfile(minNode.getProfile());
            node.setRight(removePersonHelper(node.getRight(), minNode.getName()));
        }
        return node;
    }
    
    private TreeNode findMin(TreeNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }        

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
         // Get the array of unmarked people
    String[] unmarkedPeople = getUnmarkedPeople();

    // Remove each unmarked person from the tree
    for (String fullName : unmarkedPeople) {
        removePerson(fullName);
    }
    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
