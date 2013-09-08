package integration;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IntegrationTest.class })
public class IntegrationTestSuite {

    @AfterClass
    public static void after() {
        System.out.println("------------ after");
    }

}
