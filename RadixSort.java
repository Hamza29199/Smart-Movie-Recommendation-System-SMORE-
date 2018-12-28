import java.util.ArrayList;

public class RadixSort {


    public ArrayList<String> a = new ArrayList<String>();

    // find longest length string in string[] a.
    public static int findLongestLength(ArrayList<String> arr) {
        int longest = 0;
        for (int i = 0; i < arr.size(); ++i) {
            if (arr.get(i).length() > longest) {
                longest = arr.get(i).length();
            }
        }
        return longest;
    }

    // if d >= 0 && d < a[i].length(), return a[i].charAt(d);
    // else , return 0, which means least value to sort.
    public static int findCharAtInString(int i, int d, ArrayList<String> arr) {
        if (d < 0 || d >= arr.get(i).length()) {
            return 0;
        }
        return arr.get(i).charAt(d);
    }

    // Rearranges the array of variable-length strings.
    public static void sort(ArrayList<String> arr) {
        int n = arr.size();
        int R = 256;    // extended ASCII alphabet size.
        String[] aux = new String[n];
        int w = findLongestLength(arr);  // w is the length of longest string in a.
        for (int d = w - 1; d >= 0; d--) {
            // sort by key-indexed counting on dth character

            // compute frequency counts
            int[] count = new int[R + 1];
            for (int i = 0; i < n; ++i) {
                int c = findCharAtInString(i, d, arr);
                count[c + 1]++;
            }

            // compute cumulates
            for (int r = 0; r < R; ++r) {
                count[r + 1] += count[r];
            }

            // move data
            for (int i = 0; i < n; ++i) {
                int c = findCharAtInString(i, d, arr);
                aux[count[c]++] = arr.get(i);
            }

            // copy back
            for (int i = 0; i < n; ++i) {
                arr.set(i, aux[i]);
            }
        }
    }
    /*public static void main(String[] args) {

        a.add("hamza");
        a.add("malik");
        a.add("osama");
        a.add("bondu");
        int n = a
        // sort the strings
        sort(a);

        // prints results
        for (int i = 0; i < n; ++i) {
            System.out.println(a[i]);
        }

    }*/


}
