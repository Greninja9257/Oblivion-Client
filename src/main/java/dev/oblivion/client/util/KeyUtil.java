package dev.oblivion.client.util;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyUtil {
    private static final Map<String, Integer> KEY_MAP = new HashMap<>();

    static {
        // Letters
        for (int i = GLFW.GLFW_KEY_A; i <= GLFW.GLFW_KEY_Z; i++) {
            KEY_MAP.put(String.valueOf((char) i), i);
        }
        // Numbers
        for (int i = GLFW.GLFW_KEY_0; i <= GLFW.GLFW_KEY_9; i++) {
            KEY_MAP.put(String.valueOf(i - GLFW.GLFW_KEY_0), i);
        }
        // Function keys
        for (int i = 1; i <= 25; i++) {
            KEY_MAP.put("F" + i, GLFW.GLFW_KEY_F1 + i - 1);
        }
        // Modifiers
        KEY_MAP.put("LSHIFT", GLFW.GLFW_KEY_LEFT_SHIFT);
        KEY_MAP.put("RSHIFT", GLFW.GLFW_KEY_RIGHT_SHIFT);
        KEY_MAP.put("LCTRL", GLFW.GLFW_KEY_LEFT_CONTROL);
        KEY_MAP.put("RCTRL", GLFW.GLFW_KEY_RIGHT_CONTROL);
        KEY_MAP.put("LALT", GLFW.GLFW_KEY_LEFT_ALT);
        KEY_MAP.put("RALT", GLFW.GLFW_KEY_RIGHT_ALT);
        // Special
        KEY_MAP.put("SPACE", GLFW.GLFW_KEY_SPACE);
        KEY_MAP.put("TAB", GLFW.GLFW_KEY_TAB);
        KEY_MAP.put("ENTER", GLFW.GLFW_KEY_ENTER);
        KEY_MAP.put("ESCAPE", GLFW.GLFW_KEY_ESCAPE);
        KEY_MAP.put("BACKSPACE", GLFW.GLFW_KEY_BACKSPACE);
        KEY_MAP.put("DELETE", GLFW.GLFW_KEY_DELETE);
        KEY_MAP.put("INSERT", GLFW.GLFW_KEY_INSERT);
        KEY_MAP.put("HOME", GLFW.GLFW_KEY_HOME);
        KEY_MAP.put("END", GLFW.GLFW_KEY_END);
        KEY_MAP.put("PAGEUP", GLFW.GLFW_KEY_PAGE_UP);
        KEY_MAP.put("PAGEDOWN", GLFW.GLFW_KEY_PAGE_DOWN);
        KEY_MAP.put("UP", GLFW.GLFW_KEY_UP);
        KEY_MAP.put("DOWN", GLFW.GLFW_KEY_DOWN);
        KEY_MAP.put("LEFT", GLFW.GLFW_KEY_LEFT);
        KEY_MAP.put("RIGHT", GLFW.GLFW_KEY_RIGHT);
        KEY_MAP.put("CAPSLOCK", GLFW.GLFW_KEY_CAPS_LOCK);
        KEY_MAP.put("NONE", GLFW.GLFW_KEY_UNKNOWN);
        // Punctuation
        KEY_MAP.put("MINUS", GLFW.GLFW_KEY_MINUS);
        KEY_MAP.put("EQUALS", GLFW.GLFW_KEY_EQUAL);
        KEY_MAP.put("LBRACKET", GLFW.GLFW_KEY_LEFT_BRACKET);
        KEY_MAP.put("RBRACKET", GLFW.GLFW_KEY_RIGHT_BRACKET);
        KEY_MAP.put("SEMICOLON", GLFW.GLFW_KEY_SEMICOLON);
        KEY_MAP.put("COMMA", GLFW.GLFW_KEY_COMMA);
        KEY_MAP.put("PERIOD", GLFW.GLFW_KEY_PERIOD);
        KEY_MAP.put("SLASH", GLFW.GLFW_KEY_SLASH);
        KEY_MAP.put("BACKSLASH", GLFW.GLFW_KEY_BACKSLASH);
        KEY_MAP.put("GRAVE", GLFW.GLFW_KEY_GRAVE_ACCENT);
    }

    public static int getKeyCode(String keyName) {
        if (keyName == null || keyName.isBlank()) return GLFW.GLFW_KEY_UNKNOWN;
        Integer code = KEY_MAP.get(keyName.toUpperCase());
        return code != null ? code : GLFW.GLFW_KEY_UNKNOWN;
    }

    public static String getKeyName(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return "NONE";
        for (Map.Entry<String, Integer> entry : KEY_MAP.entrySet()) {
            if (entry.getValue() == keyCode) return entry.getKey();
        }
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        return name != null ? name.toUpperCase() : "KEY_" + keyCode;
    }
}
