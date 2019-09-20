package test.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class JsonHelper {
    private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class.getSimpleName());
    private static ObjectMapper objectMapper = new ObjectMapper();

    public JsonHelper() {
    }

    private static double getAppVersionAsDouble(String v, double defultValue) {
        try {
            String tempVer = v.replaceFirst("[^0-9\\.].*", "");
            String[] tempArr = StringUtils.split(tempVer, ".");
            tempVer = StringUtils.replace(tempVer, ".", "");
            StringBuilder subd = new StringBuilder(tempVer);
            tempVer = subd.insert(tempArr[0].length(), ".").toString();
            return parseDouble(tempVer, defultValue);
        } catch (Exception var6) {
            return defultValue;
        }
    }

    private static Double parseDouble(Object val, Double defultValue) {
        try {
            return Double.parseDouble(val.toString().trim());
        } catch (Exception var3) {
            return defultValue;
        }
    }

    public static String GetJsonResult(Object src) {
        StringWriter sw = new StringWriter();

        try {
            objectMapper.writeValue(sw, src);
            String var2 = sw.getBuffer().toString();
            return var2;
        } catch (Exception var12) {
            logger.error("GetJsonResult err:", var12);
        } finally {
            sw.flush();

            try {
                sw.close();
            } catch (IOException var11) {
                var11.printStackTrace();
            }

        }

        return "";
    }

    public static <T> T GetObjByJson(String json, Class<T> type) throws Exception {
        return objectMapper.readValue(json, type);
    }

    public static <T, E> T GetObjByJson(String json, Class<T> type, Class<E> eleType) throws Exception {
        JavaType javaType1 = objectMapper.getTypeFactory().constructParametricType(type, new Class[]{eleType});
        return objectMapper.readValue(json, javaType1);
    }

    public static void SaveJsonFile(Object src, String filePath) {
        try {
            FileWriter fw = new FileWriter(filePath, false);
            objectMapper.writeValue(fw, src);
        } catch (Exception var3) {
            throw new RuntimeException("JsonHelper.SaveJsonFile Error", var3);
        }
    }

    public static Object ReadJsonFile(String filePath, Class type) {
        try {
            Object result = objectMapper.readValue(new File(filePath), type);
            return result;
        } catch (Exception var3) {
            throw new RuntimeException("JsonHelper.ReadJsonFile Error", var3);
        }
    }

    public static JsonNode GetJsonObjByJson(String json) throws IOException {
        JsonNode node = objectMapper.readTree(json);
        return node;
    }

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
