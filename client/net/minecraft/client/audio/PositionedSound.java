package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class PositionedSound implements ISound {
   protected final ResourceLocation field_147664_a;
   protected float field_147662_b = 1.0F;
   protected float field_147663_c = 1.0F;
   protected float field_147660_d;
   protected float field_147661_e;
   protected float field_147658_f;
   protected boolean field_147659_g = false;
   protected int field_147665_h = 0;
   protected ISound$AttenuationType field_147666_i = ISound$AttenuationType.LINEAR;

   protected PositionedSound(ResourceLocation var1) {
      super();
      this.field_147664_a = var1;
   }

   @Override
   public ResourceLocation func_147650_b() {
      return this.field_147664_a;
   }

   @Override
   public boolean func_147657_c() {
      return this.field_147659_g;
   }

   @Override
   public int func_147652_d() {
      return this.field_147665_h;
   }

   @Override
   public float func_147653_e() {
      return this.field_147662_b;
   }

   @Override
   public float func_147655_f() {
      return this.field_147663_c;
   }

   @Override
   public float func_147649_g() {
      return this.field_147660_d;
   }

   @Override
   public float func_147654_h() {
      return this.field_147661_e;
   }

   @Override
   public float func_147651_i() {
      return this.field_147658_f;
   }

   @Override
   public ISound$AttenuationType func_147656_j() {
      return this.field_147666_i;
   }
}
