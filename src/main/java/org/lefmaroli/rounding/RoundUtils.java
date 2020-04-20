package org.lefmaroli.rounding;

public class RoundUtils {

    public static boolean isPowerOfTwo(int n) {
        return n>0 && (n&n-1)==0;
    }

    public static int ceilToPowerOfTwo(int n){
        if(isPowerOfTwo(n)){
            return n;
        }else{
            int power = 0;
            while (n !=0){  //000000
                n >>= 1;
                power++;
            }
            return 1 << power;
        }
    }

    public static int floorToPowerOfTwo(int n){
        if(isPowerOfTwo(n)){
            return n;
        }else{
            int power = 0;
            while (n !=1){  //000001
                n >>= 1;
                power++;
            }
            return 1 << power;
        }
    }
}
