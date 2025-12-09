package net.minecraft.client.data.models.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

public class DelegatedModel implements ModelInstance {
   private final Identifier parent;

   public DelegatedModel(Identifier var1) {
      super();
      this.parent = var1;
   }

   public JsonElement get() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("parent", this.parent.toString());
      return var1;
   }

   // $FF: synthetic method
   public Object get() {
      return this.get();
   }
}
