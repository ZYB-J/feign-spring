package test.logback;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * Created by zhouyongbo on 2019/12/4.
 */
public class CompressedStackTraceConverter extends ThrowableProxyConverter {
    public static final String LINE_SEP_FOR_ES = "##";
    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        String original = super.throwableProxyToString(tp);

        // replace the new line characters with something,
        // use your own replacement value here
        return original.replaceAll("\n", LINE_SEP_FOR_ES);
    }
}
