package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.JsonUtils;

public class ItemTransformVec3f {
   public static final ItemTransformVec3f field_178366_a = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f field_178364_b;
   public final Vector3f field_178365_c;
   public final Vector3f field_178363_d;

   public ItemTransformVec3f(Vector3f var1, Vector3f var2, Vector3f var3) {
      super();
      this.field_178364_b = new Vector3f(var1);
      this.field_178365_c = new Vector3f(var2);
      this.field_178363_d = new Vector3f(var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         ItemTransformVec3f var2 = (ItemTransformVec3f)var1;
         return this.field_178364_b.equals(var2.field_178364_b) && this.field_178363_d.equals(var2.field_178363_d) && this.field_178365_c.equals(var2.field_178365_c);
      }
   }

   public int hashCode() {
      int var1 = this.field_178364_b.hashCode();
      var1 = 31 * var1 + this.field_178365_c.hashCode();
      var1 = 31 * var1 + this.field_178363_d.hashCode();
      return var1;
   }

   static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      private static final Vector3f field_178362_a = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f field_178360_b = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f field_178361_c = new Vector3f(1.0F, 1.0F, 1.0F);

      Deserializer() {
         super();
      }

      public ItemTransformVec3f deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Vector3f var5 = this.func_199340_a(var4, "rotation", field_178362_a);
         Vector3f var6 = this.func_199340_a(var4, "translation", field_178360_b);
         var6.func_195898_a(0.0625F);
         var6.func_195901_a(-5.0F, 5.0F);
         Vector3f var7 = this.func_199340_a(var4, "scale", field_178361_c);
         var7.func_195901_a(-4.0F, 4.0F);
         return new ItemTransformVec3f(var5, var6, var7);
      }

      private Vector3f func_199340_a(JsonObject var1, String var2, Vector3f var3) {
         if (!var1.has(var2)) {
            return var3;
         } else {
            JsonArray var4 = JsonUtils.func_151214_t(var1, var2);
            if (var4.size() != 3) {
               throw new JsonParseException("Expected 3 " + var2 + " values, found: " + var4.size());
            } else {
               float[] var5 = new float[3];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  var5[var6] = JsonUtils.func_151220_d(var4.get(var6), var2 + "[" + var6 + "]");
               }

               return new Vector3f(var5[0], var5[1], var5[2]);
            }
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
