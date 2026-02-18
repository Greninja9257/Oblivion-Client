package dev.oblivion.client.addon;

import com.google.gson.JsonObject;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public class Addon {
    public enum Type { JAVA_SOURCE, JSON_CONFIG }
    public enum Status { NOT_INSTALLED, INSTALLED, UPDATE_AVAILABLE }

    private final String id;
    private final String name;
    private final String author;
    private final String description;
    private final String version;
    private final Type type;
    private final Category category;
    private Status status;
    private Module loadedModule;

    public Addon(String id, String name, String author, String description, String version, Type type, Category category) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.type = type;
        this.category = category;
        this.status = Status.NOT_INSTALLED;
    }

    public static Addon fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        String author = json.get("author").getAsString();
        String description = json.has("description") ? json.get("description").getAsString() : "";
        String version = json.has("version") ? json.get("version").getAsString() : "1.0.0";
        String typeStr = json.has("type") ? json.get("type").getAsString() : "java";
        Type type = typeStr.equals("json") ? Type.JSON_CONFIG : Type.JAVA_SOURCE;
        Category category = Category.MISC;
        if (json.has("category")) {
            try {
                category = Category.valueOf(json.get("category").getAsString());
            } catch (IllegalArgumentException ignored) {}
        }
        return new Addon(id, name, author, description, version, type, category);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("author", author);
        json.addProperty("description", description);
        json.addProperty("version", version);
        json.addProperty("type", type == Type.JSON_CONFIG ? "json" : "java");
        json.addProperty("category", category.name());
        return json;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public Type getType() { return type; }
    public Category getCategory() { return category; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Module getLoadedModule() { return loadedModule; }
    public void setLoadedModule(Module module) { this.loadedModule = module; }
}
