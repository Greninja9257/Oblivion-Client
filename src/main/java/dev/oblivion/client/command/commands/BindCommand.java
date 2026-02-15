package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;
import dev.oblivion.client.util.KeyUtil;
import org.lwjgl.glfw.GLFW;

public class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Binds a module to a key", "b");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            ChatUtil.error("Usage: " + getUsage());
            return;
        }
        Module module = OblivionClient.get().moduleManager.get(args[0]);
        if (module == null) {
            ChatUtil.error("Unknown module: " + args[0]);
            return;
        }
        int key = KeyUtil.getKeyCode(args[1]);
        if (key == GLFW.GLFW_KEY_UNKNOWN && !args[1].equalsIgnoreCase("NONE")) {
            ChatUtil.error("Unknown key: " + args[1]);
            return;
        }
        module.setKeybind(key);
        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            ChatUtil.success("Unbound " + module.name);
        } else {
            ChatUtil.success("Bound " + module.name + " to " + KeyUtil.getKeyName(key));
        }
    }

    @Override
    public String getUsage() {
        return "bind <module> <key>";
    }
}
