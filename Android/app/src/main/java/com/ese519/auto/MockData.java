package com.ese519.auto;

import java.util.Random;

/**
 * Created by jonathanfields on 11/30/15.
 */
public class MockData {

    public static Point getDataFromReceiver(int x) {
        return new Point(x,generateRandomData());
    }

    public static int generateRandomData() {
        Random random = new Random();
        return random.nextInt(40);
    }
}
