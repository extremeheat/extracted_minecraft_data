package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class RenderSkybox {
   private final Minecraft field_209145_a;
   private final RenderSkyboxCube field_209146_b;
   private float field_209147_c;

   public RenderSkybox(RenderSkyboxCube var1) {
      super();
      this.field_209146_b = var1;
      this.field_209145_a = Minecraft.func_71410_x();
   }

   public void func_209144_a(float var1) {
      this.field_209147_c += var1;
      this.field_209146_b.func_209142_a(this.field_209145_a, MathHelper.func_76126_a(this.field_209147_c * 0.001F) * 5.0F + 25.0F, -this.field_209147_c * 0.1F);
      this.field_209145_a.field_195558_d.func_198094_a();
   }
}
