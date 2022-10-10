package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class EntitySpellcasterIllager extends AbstractIllager {
   private static final DataParameter<Byte> field_193088_c;
   protected int field_193087_b;
   private EntitySpellcasterIllager.SpellType field_193089_bx;

   protected EntitySpellcasterIllager(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_193089_bx = EntitySpellcasterIllager.SpellType.NONE;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_193088_c, (byte)0);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_193087_b = var1.func_74762_e("SpellTicks");
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("SpellTicks", this.field_193087_b);
   }

   public AbstractIllager.IllagerArmPose func_193077_p() {
      return this.func_193082_dl() ? AbstractIllager.IllagerArmPose.SPELLCASTING : AbstractIllager.IllagerArmPose.CROSSED;
   }

   public boolean func_193082_dl() {
      if (this.field_70170_p.field_72995_K) {
         return (Byte)this.field_70180_af.func_187225_a(field_193088_c) > 0;
      } else {
         return this.field_193087_b > 0;
      }
   }

   public void func_193081_a(EntitySpellcasterIllager.SpellType var1) {
      this.field_193089_bx = var1;
      this.field_70180_af.func_187227_b(field_193088_c, (byte)var1.field_193345_g);
   }

   protected EntitySpellcasterIllager.SpellType func_193083_dm() {
      return !this.field_70170_p.field_72995_K ? this.field_193089_bx : EntitySpellcasterIllager.SpellType.func_193337_a((Byte)this.field_70180_af.func_187225_a(field_193088_c));
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      if (this.field_193087_b > 0) {
         --this.field_193087_b;
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K && this.func_193082_dl()) {
         EntitySpellcasterIllager.SpellType var1 = this.func_193083_dm();
         double var2 = var1.field_193346_h[0];
         double var4 = var1.field_193346_h[1];
         double var6 = var1.field_193346_h[2];
         float var8 = this.field_70761_aq * 0.017453292F + MathHelper.func_76134_b((float)this.field_70173_aa * 0.6662F) * 0.25F;
         float var9 = MathHelper.func_76134_b(var8);
         float var10 = MathHelper.func_76126_a(var8);
         this.field_70170_p.func_195594_a(Particles.field_197625_r, this.field_70165_t + (double)var9 * 0.6D, this.field_70163_u + 1.8D, this.field_70161_v + (double)var10 * 0.6D, var2, var4, var6);
         this.field_70170_p.func_195594_a(Particles.field_197625_r, this.field_70165_t - (double)var9 * 0.6D, this.field_70163_u + 1.8D, this.field_70161_v - (double)var10 * 0.6D, var2, var4, var6);
      }

   }

   protected int func_193085_dn() {
      return this.field_193087_b;
   }

   protected abstract SoundEvent func_193086_dk();

   static {
      field_193088_c = EntityDataManager.func_187226_a(EntitySpellcasterIllager.class, DataSerializers.field_187191_a);
   }

   public static enum SpellType {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      private final int field_193345_g;
      private final double[] field_193346_h;

      private SpellType(int var3, double var4, double var6, double var8) {
         this.field_193345_g = var3;
         this.field_193346_h = new double[]{var4, var6, var8};
      }

      public static EntitySpellcasterIllager.SpellType func_193337_a(int var0) {
         EntitySpellcasterIllager.SpellType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EntitySpellcasterIllager.SpellType var4 = var1[var3];
            if (var0 == var4.field_193345_g) {
               return var4;
            }
         }

         return NONE;
      }
   }

   public abstract class AIUseSpell extends EntityAIBase {
      protected int field_193321_c;
      protected int field_193322_d;

      protected AIUseSpell() {
         super();
      }

      public boolean func_75250_a() {
         if (EntitySpellcasterIllager.this.func_70638_az() == null) {
            return false;
         } else if (EntitySpellcasterIllager.this.func_193082_dl()) {
            return false;
         } else {
            return EntitySpellcasterIllager.this.field_70173_aa >= this.field_193322_d;
         }
      }

      public boolean func_75253_b() {
         return EntitySpellcasterIllager.this.func_70638_az() != null && this.field_193321_c > 0;
      }

      public void func_75249_e() {
         this.field_193321_c = this.func_190867_m();
         EntitySpellcasterIllager.this.field_193087_b = this.func_190869_f();
         this.field_193322_d = EntitySpellcasterIllager.this.field_70173_aa + this.func_190872_i();
         SoundEvent var1 = this.func_190871_k();
         if (var1 != null) {
            EntitySpellcasterIllager.this.func_184185_a(var1, 1.0F, 1.0F);
         }

         EntitySpellcasterIllager.this.func_193081_a(this.func_193320_l());
      }

      public void func_75246_d() {
         --this.field_193321_c;
         if (this.field_193321_c == 0) {
            this.func_190868_j();
            EntitySpellcasterIllager.this.func_184185_a(EntitySpellcasterIllager.this.func_193086_dk(), 1.0F, 1.0F);
         }

      }

      protected abstract void func_190868_j();

      protected int func_190867_m() {
         return 20;
      }

      protected abstract int func_190869_f();

      protected abstract int func_190872_i();

      @Nullable
      protected abstract SoundEvent func_190871_k();

      protected abstract EntitySpellcasterIllager.SpellType func_193320_l();
   }

   public class AICastingApell extends EntityAIBase {
      public AICastingApell() {
         super();
         this.func_75248_a(3);
      }

      public boolean func_75250_a() {
         return EntitySpellcasterIllager.this.func_193085_dn() > 0;
      }

      public void func_75249_e() {
         super.func_75249_e();
         EntitySpellcasterIllager.this.field_70699_by.func_75499_g();
      }

      public void func_75251_c() {
         super.func_75251_c();
         EntitySpellcasterIllager.this.func_193081_a(EntitySpellcasterIllager.SpellType.NONE);
      }

      public void func_75246_d() {
         if (EntitySpellcasterIllager.this.func_70638_az() != null) {
            EntitySpellcasterIllager.this.func_70671_ap().func_75651_a(EntitySpellcasterIllager.this.func_70638_az(), (float)EntitySpellcasterIllager.this.func_184649_cE(), (float)EntitySpellcasterIllager.this.func_70646_bf());
         }

      }
   }
}
