package common;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Map ↔ JSON 변환 유틸 — Jackson 사용
 */
public class JsonUtil {

    private static final ObjectMapper om = new ObjectMapper();

    public static String toJson(Object obj) {
        try { return om.writeValueAsString(obj); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static Map<String, Object> parse(String json) {
        try { return om.readValue(json, new TypeReference<Map<String, Object>>() {}); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static String getString(Map<String, Object> m, String key, String def) {
        Object o = m.get(key);
        return o == null ? def : o.toString();
    }

    public static int getInt(Map<String, Object> m, String key, int def) {
        Object o = m.get(key);
        if (o instanceof Number) return ((Number) o).intValue();
        try { return o == null ? def : Integer.parseInt(o.toString()); }
        catch (NumberFormatException e) { return def; }
    }
}
