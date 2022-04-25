package util;
import java.util.Random;

public class Utils {


    public static String getRandomCardNo(){
        int iSeries = 6400+new Random().nextInt(599);
        int uSeries = 4000+new Random().nextInt(999);
        int fSeries = 2000+new Random().nextInt(999);
        int cSeries = 1000+new Random().nextInt(999);

        return iSeries + "" + uSeries + "" + fSeries + "" + cSeries;
    }

    public static long getRandomCvv(){
        int cvv = 100+new Random().nextInt(899);
        return cvv;
    }

    public static long getRandomExpMonth(){
        int expMonth = 1+new Random().nextInt(11);
        return expMonth ;
    }
    public static  long getRandomExpYear(){
        int expYear = 2022+new Random().nextInt(10);
        return expYear;
    }
}
