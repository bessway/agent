package zll.practice;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @BeforeClass
    public void beforeClass() {
        System.out.println("this is before class");
    }

    @Parameters({"otherName"})
    @Test
    //@Test(parameters = {"otherName"})
    public void TestNgLearn(String name) {
        System.out.println("this is TestNG test case "+name);
    }

    @AfterClass
    public void afterClass() {
        System.out.println("this is after class");
    }
}
