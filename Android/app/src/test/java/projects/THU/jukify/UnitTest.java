package projects.THU.jukify;

import android.content.Context;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void doesGenerateRandom() {
        // keep track whether set contains unique
        boolean isUnique = true;
        // random number of ids
        Random rand = new Random(System.currentTimeMillis());
        int randomNumber = rand.nextInt();
        Set<String> idSet = new HashSet<String>();
        // generate id strings
        String generated;
        for(int i = 0 ; i < randomNumber && isUnique ; ++i){
            generated = IDgenerator.getBase62(40);
            if(idSet.contains(generated)){ // if set contains that string already, swap tracker to false
                isUnique = false;
            } else {
                idSet.add(generated);
            }
        }
        assert(isUnique);
    }
    @Test
    public void doesSingletonHTTPWork(){
        HTTPClient client1 = HTTPClient.getInstance();
        HTTPClient client2 = HTTPClient.getInstance();
        assertEquals(client1, client2);
    }
}