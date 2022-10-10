package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;

public class ItemCameraTransforms {
   public static final ItemCameraTransforms field_178357_a = new ItemCameraTransforms();
   public static float field_181690_b;
   public static float field_181691_c;
   public static float field_181692_d;
   public static float field_181693_e;
   public static float field_181694_f;
   public static float field_181695_g;
   public static float field_181696_h;
   public static float field_181697_i;
   public static float field_181698_j;
   public final ItemTransformVec3f field_188036_k;
   public final ItemTransformVec3f field_188037_l;
   public final ItemTransformVec3f field_188038_m;
   public final ItemTransformVec3f field_188039_n;
   public final ItemTransformVec3f field_178353_d;
   public final ItemTransformVec3f field_178354_e;
   public final ItemTransformVec3f field_181699_o;
   public final ItemTransformVec3f field_181700_p;

   private ItemCameraTransforms() {
      this(ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a, ItemTransformVec3f.field_178366_a);
   }

   public ItemCameraTransforms(ItemCameraTransforms var1) {
      super();
      this.field_188036_k = var1.field_188036_k;
      this.field_188037_l = var1.field_188037_l;
      this.field_188038_m = var1.field_188038_m;
      this.field_188039_n = var1.field_188039_n;
      this.field_178353_d = var1.field_178353_d;
      this.field_178354_e = var1.field_178354_e;
      this.field_181699_o = var1.field_181699_o;
      this.field_181700_p = var1.field_181700_p;
   }

   public ItemCameraTransforms(ItemTransformVec3f var1, ItemTransformVec3f var2, ItemTransformVec3f var3, ItemTransformVec3f var4, ItemTransformVec3f var5, ItemTransformVec3f var6, ItemTransformVec3f var7, ItemTransformVec3f var8) {
      super();
      this.field_188036_k = var1;
      this.field_188037_l = var2;
      this.field_188038_m = var3;
      this.field_188039_n = var4;
      this.field_178353_d = var5;
      this.field_178354_e = var6;
      this.field_181699_o = var7;
      this.field_181700_p = var8;
   }

   public void func_181689_a(ItemCameraTransforms.TransformType var1) {
      func_188034_a(this.func_181688_b(var1), false);
   }

   public static void func_188034_a(ItemTransformVec3f var0, boolean var1) {
      if (var0 != ItemTransformVec3f.field_178366_a) {
         int var2 = var1 ? -1 : 1;
         GlStateManager.func_179109_b((float)var2 * (field_181690_b + var0.field_178365_c.func_195899_a()), field_181691_c + var0.field_178365_c.func_195900_b(), field_181692_d + var0.field_178365_c.func_195902_c());
         float var3 = field_181693_e + var0.field_178364_b.func_195899_a();
         float var4 = field_181694_f + var0.field_178364_b.func_195900_b();
         float var5 = field_181695_g + var0.field_178364_b.func_195902_c();
         if (var1) {
            var4 = -var4;
            var5 = -var5;
         }

         GlStateManager.func_199294_a(new Matrix4f(new Quaternion(var3, var4, var5, true)));
         GlStateManager.func_179152_a(field_181696_h + var0.field_178363_d.func_195899_a(), field_181697_i + var0.field_178363_d.func_195900_b(), field_181698_j + var0.field_178363_d.func_195902_c());
      }
   }

   public ItemTransformVec3f func_181688_b(ItemCameraTransforms.TransformType var1) {
      switch(var1) {
      case THIRD_PERSON_LEFT_HAND:
         return this.field_188036_k;
      case THIRD_PERSON_RIGHT_HAND:
         return this.field_188037_l;
      case FIRST_PERSON_LEFT_HAND:
         return this.field_188038_m;
      case FIRST_PERSON_RIGHT_HAND:
         return this.field_188039_n;
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
      return this.func_181688_b(var1) != ItemTransformVec3f.field_178366_a;
   }

   static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      Deserializer() {
         super();
      }

      public ItemCameraTransforms deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ItemTransformVec3f var5 = this.func_181683_a(var3, var4, "thirdperson_righthand");
         ItemTransformVec3f var6 = this.func_181683_a(var3, var4, "thirdperson_lefthand");
         if (var6 == ItemTransformVec3f.field_178366_a) {
            var6 = var5;
         }

         ItemTransformVec3f var7 = this.func_181683_a(var3, var4, "firstperson_righthand");
         ItemTransformVec3f var8 = this.func_181683_a(var3, var4, "firstperson_lefthand");
         if (var8 == ItemTransformVec3f.field_178366_a) {
            var8 = var7;
         }

         ItemTransformVec3f var9 = this.func_181683_a(var3, var4, "head");
         ItemTransformVec3f var10 = this.func_181683_a(var3, var4, "gui");
         ItemTransformVec3f var11 = this.func_181683_a(var3, var4, "ground");
         ItemTransformVec3f var12 = this.func_181683_a(var3, var4, "fixed");
         return new ItemCameraTransforms(var6, var5, var8, var7, var9, var10, var11, var12);
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
      THIRD_PERSON_LEFT_HAND,
      THIRD_PERSON_RIGHT_HAND,
      FIRST_PERSON_LEFT_HAND,
      FIRST_PERSON_RIGHT_HAND,
      HEAD,
      GUI,
      GROUND,
      FIXED;

      private TransformType() {
      }
   }
}
