package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms {
   public static final ItemCameraTransforms field_178357_a = new ItemCameraTransforms();
   public static float field_181690_b = 0.0F;
   public static float field_181691_c = 0.0F;
   public static float field_181692_d = 0.0F;
   public static float field_181693_e = 0.0F;
   public static float field_181694_f = 0.0F;
   public static float field_181695_g = 0.0F;
   public static float field_181696_h = 0.0F;
   public static float field_181697_i = 0.0F;
   public static float field_181698_j = 0.0F;
   public final ItemTransformVec3f field_178355_b;
   public final ItemTransformVec3f field_178356_c;
   public final ItemTransformVec3f field_178353_d;
   public final ItemTransformVec3f field_178354_e;
   public final ItemTransformVec3f field_181699_o;
   public final ItemTransformVec3f field_181700_p;

   private ItemCameraTransforms() {
      this(ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a);
   }

   public ItemCameraTransforms(ItemCameraTransforms var1) {
      super();
      this.field_178355_b = var1.field_178355_b;
      this.field_178356_c = var1.field_178356_c;
      this.field_178353_d = var1.field_178353_d;
      this.field_178354_e = var1.field_178354_e;
      this.field_181699_o = var1.field_181699_o;
      this.field_181700_p = var1.field_181700_p;
   }

   public ItemCameraTransforms(ItemTransformVec3f var1, ItemTransformVec3f var2, ItemTransformVec3f var3, ItemTransformVec3f var4, ItemTransformVec3f var5, ItemTransformVec3f var6) {
      super();
      this.field_178355_b = var1;
      this.field_178356_c = var2;
      this.field_178353_d = var3;
      this.field_178354_e = var4;
      this.field_181699_o = var5;
      this.field_181700_p = var6;
   }

   public void func_181689_a(ItemCameraTransforms.TransformType var1) {
      ItemTransformVec3f var2 = this.func_181688_b(var1);
      if (var2 != ItemTransformVec3f.field_178366_a) {
         GlStateManager.func_179109_b(var2.field_178365_c.x + field_181690_b, var2.field_178365_c.y + field_181691_c, var2.field_178365_c.z + field_181692_d);
         GlStateManager.func_179114_b(var2.field_178364_b.y + field_181694_f, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(var2.field_178364_b.x + field_181693_e, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(var2.field_178364_b.z + field_181695_g, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179152_a(var2.field_178363_d.x + field_181696_h, var2.field_178363_d.y + field_181697_i, var2.field_178363_d.z + field_181698_j);
      }

   }

   public ItemTransformVec3f func_181688_b(ItemCameraTransforms.TransformType var1) {
      switch(var1) {
      case THIRD_PERSON:
         return this.field_178355_b;
      case FIRST_PERSON:
         return this.field_178356_c;
      case HEAD:
         return this.field_178353_d;
      case GUI:
         return this.field_178354_e;
      case GROUND:
         return this.field_181699_o;
      case FIXED:
         return this.field_181700_p;
      default:
         return ItemTransformVec3f.field_178366_a;
      }
   }

   public boolean func_181687_c(ItemCameraTransforms.TransformType var1) {
      return !this.func_181688_b(var1).equals(ItemTransformVec3f.field_178366_a);
   }

   static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      Deserializer() {
         super();
      }

      public ItemCameraTransforms deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ItemTransformVec3f var5 = this.func_181683_a(var3, var4, "thirdperson");
         ItemTransformVec3f var6 = this.func_181683_a(var3, var4, "firstperson");
         ItemTransformVec3f var7 = this.func_181683_a(var3, var4, "head");
         ItemTransformVec3f var8 = this.func_181683_a(var3, var4, "gui");
         ItemTransformVec3f var9 = this.func_181683_a(var3, var4, "ground");
         ItemTransformVec3f var10 = this.func_181683_a(var3, var4, "fixed");
         return new ItemCameraTransforms(var5, var6, var7, var8, var9, var10);
      }

      private ItemTransformVec3f func_181683_a(JsonDeserializationContext var1, JsonObject var2, String var3) {
         return var2.has(var3) ? (ItemTransformVec3f)var1.deserialize(var2.get(var3), ItemTransformVec3f.class) : ItemTransformVec3f.field_178366_a;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static enum TransformType {
      NONE,
      THIRD_PERSON,
      FIRST_PERSON,
      HEAD,
      GUI,
      GROUND,
      FIXED;

      private TransformType() {
      }
   }
}
