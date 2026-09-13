package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
   private static FloatBuffer field_74522_a = GLAllocation.func_74529_h(16);
   private static final Vec3 field_82884_b = Vec3.func_72443_a(0.20000000298023224, 1.0, -0.699999988079071).func_72432_b();
   private static final Vec3 field_82885_c = Vec3.func_72443_a(-0.20000000298023224, 1.0, 0.699999988079071).func_72432_b();

   public static void func_74518_a() {
      GL11.glDisable(2896);
      GL11.glDisable(16384);
      GL11.glDisable(16385);
      GL11.glDisable(2903);
   }

   public static void func_74519_b() {
      GL11.glEnable(2896);
      GL11.glEnable(16384);
      GL11.glEnable(16385);
      GL11.glEnable(2903);
      GL11.glColorMaterial(1032, 5634);
      float var0 = 0.4F;
      float var1 = 0.6F;
      float var2 = 0.0F;
      GL11.glLight(16384, 4611, func_74517_a(field_82884_b.field_72450_a, field_82884_b.field_72448_b, field_82884_b.field_72449_c, 0.0));
      GL11.glLight(16384, 4609, func_74521_a(var1, var1, var1, 1.0F));
      GL11.glLight(16384, 4608, func_74521_a(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16384, 4610, func_74521_a(var2, var2, var2, 1.0F));
      GL11.glLight(16385, 4611, func_74517_a(field_82885_c.field_72450_a, field_82885_c.field_72448_b, field_82885_c.field_72449_c, 0.0));
      GL11.glLight(16385, 4609, func_74521_a(var1, var1, var1, 1.0F));
      GL11.glLight(16385, 4608, func_74521_a(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16385, 4610, func_74521_a(var2, var2, var2, 1.0F));
      GL11.glShadeModel(7424);
      GL11.glLightModel(2899, func_74521_a(var0, var0, var0, 1.0F));
   }

   private static FloatBuffer func_74517_a(double var0, double var2, double var4, double var6) {
      return func_74521_a((float)var0, (float)var2, (float)var4, (float)var6);
   }

   private static FloatBuffer func_74521_a(float var0, float var1, float var2, float var3) {
      ((Buffer)field_74522_a).clear();
      field_74522_a.put(var0).put(var1).put(var2).put(var3);
      ((Buffer)field_74522_a).flip();
      return field_74522_a;
   }

   public static void func_74520_c() {
      GL11.glPushMatrix();
      GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
      func_74519_b();
      GL11.glPopMatrix();
   }
}
