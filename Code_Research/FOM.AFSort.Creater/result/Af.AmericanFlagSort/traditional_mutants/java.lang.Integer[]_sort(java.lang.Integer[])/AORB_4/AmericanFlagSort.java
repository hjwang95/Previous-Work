// This is a mutant program.
// Author : ysma

package Af;


public class AmericanFlagSort
{

    private static final int NUMBER_OF_BUCKETS = 10;

    private AmericanFlagSort()
    {
    }

    public static  java.lang.Integer[] sort( java.lang.Integer[] unsorted )
    {
        int numberOfDigits = getMaxNumberOfDigits( unsorted );
        int max = 1;
        for (int i = 0; i < numberOfDigits + 1; i++) {
            max *= 10;
        }
        sort( unsorted, 0, unsorted.length, max );
        return unsorted;
    }

    public static  void sort( java.lang.Integer[] unsorted, int start, int length, int divisor )
    {
        int[] count = new int[NUMBER_OF_BUCKETS];
        int[] offset = new int[NUMBER_OF_BUCKETS];
        int digit = 0;
        for (int i = start; i < length; i++) {
            int d = unsorted[i];
            digit = getDigit( d, divisor );
            count[digit]++;
        }
        offset[0] = start + 0;
        for (int i = 1; i < NUMBER_OF_BUCKETS; i++) {
            offset[i] = count[i - 1] + offset[i - 1];
        }
        for (int b = 0; b < NUMBER_OF_BUCKETS; b++) {
            while (count[b] > 0) {
                int origin = offset[b];
                int from = origin;
                int num = unsorted[from];
                unsorted[from] = -1;
                do {
                    digit = getDigit( num, divisor );
                    int to = offset[digit]++;
                    count[digit]--;
                    int temp = unsorted[to];
                    unsorted[to] = num;
                    num = temp;
                    from = to;
                } while (from != origin);
            }
        }
        if (divisor > 1) {
            for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
                int begin = i > 0 ? offset[i - 1] : start;
                int end = offset[i];
                if (end - begin > 1) {
                    sort( unsorted, begin, end, divisor / 10 );
                }
            }
        }
    }

    private static  int getMaxNumberOfDigits( java.lang.Integer[] unsorted )
    {
        int max = Integer.MIN_VALUE;
        int temp = 0;
        for (int i: unsorted) {
            temp = (int) Math.log10( i ) + 1;
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    private static  int getDigit( int integer, int divisor )
    {
        return integer / divisor % 10;
    }

    public static  void main( java.lang.String[] args )
    {
    }

}
