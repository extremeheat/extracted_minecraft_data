package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Vector3f;
import java.lang.reflect.Type;
import net.minecraft.util.GsonHelper;

public class ItemTransform {
   public static final ItemTransform NO_TRANSFORM = new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;

   public ItemTransform(Vector3f var1, Vector3f var2, Vector3f var3) {
      super();
      this.rotation = new Vector3f(var1);
      this.translation = new Vector3f(var2);
      this.scale = new Vector3f(var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         ItemTransform var2 = (ItemTransform)var1;
         return this.rotation.equals(var2.rotation) && this.scale.equals(var2.scale) && this.translation.equals(var2.translation);
      }
   }

   public int hashCode() {
      int var1 = this.rotation.hashCode();
      var1 = 31 * var1 + this.translation.hashCode();
      var1 = 31 * var1 + this.scale.hashCode();
      return var1;
   }

   public static class Deserializer implements JsonDeserializer<ItemTransform> {
      private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

      protected Deserializer() {
         super();
      }

      public ItemTransform deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Vector3f var5 = this.getVector3f(var4, "rotation", DEFAULT_ROTATION);
         Vector3f var6 = this.getVector3f(var4, "translation", DEFAULT_TRANSLATION);
         var6.mul(0.0625F);
         var6.clamp(-5.0F, 5.0F);
         Vector3f var7 = this.getVector3f(var4, "scale", DEFAULT_SCALE);
         var7.clamp(-4.0F, 4.0F);
         return new ItemTransform(var5, var6, var7);
      }

      private Vector3f getVector3f(JsonObject var1, String var2, Vector3f var3) {
         if (!var1.has(var2)) {
            return var3;
         } else {
            JsonArray var4 = GsonHelper.getAsJsonArray(var1, var2);
            if (var4.size() != 3) {
               throw new JsonParseException("Expected 3 " + var2 + " values, found: " + var4.size());
            } else {
               float[] var5 = new float[3];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  var5[var6] = GsonHelper.convertToFloat(var4.get(var6), var2 + "[" + var6 + "]");
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
