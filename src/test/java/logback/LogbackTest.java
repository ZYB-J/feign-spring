package logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouyongbo on 2019/12/6.
 */
public class LogbackTest {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("kafka"+LogbackTest.class);
        logger.info("1111");
    }
}
