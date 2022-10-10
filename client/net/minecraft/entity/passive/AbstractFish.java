package net.minecraft.entity.passive;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWanderSwim;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractFish extends EntityWaterMob implements IAnimal {
   private static final DataParameter<Boolean> field_203711_b;

   public AbstractFish(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_70765_h = new AbstractFish.MoveHelper(this);
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.65F;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(3.0D);
   }

   public boolean func_104002_bU() {
      return this.func_203705_dA() || super.func_104002_bU();
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      BlockPos var3 = new BlockPos(this);
      return var1.func_180495_p(var3).func_177230_c() == Blocks.field_150355_j && var1.func_180495_p(var3.func_177984_a()).func_177230_c() == Blocks.field_150355_j ? super.func_205020_a(var1, var2) : false;
   }

   public boolean func_70692_ba() {
      return !this.func_203705_dA() && !this.func_145818_k_();
   }

   public int func_70641_bl() {
      return 8;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_203711_b, false);
   }

   private boolean func_203705_dA() {
      return (Boolean)this.field_70180_af.func_187225_a(field_203711_b);
   }

   public void func_203706_r(boolean var1) {
      this.field_70180_af.func_187227_b(field_203711_b, var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("FromBucket", this.func_203705_dA());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_203706_r(var1.func_74767_n("FromBucket"));
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(0, new EntityAIPanic(this, 1.25D));
      this.field_70714_bg.func_75776_a(2, new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0F, 1.6D, 1.4D, EntitySelectors.field_180132_d));
      this.field_70714_bg.func_75776_a(4, new AbstractFish.AISwim(this));
   }

   protected PathNavigate func_175447_b(World var1) {
      return new PathNavigateSwimmer(this, var1);
   }

   public void func_191986_a(float var1, float var2, float var3) {
      if (this.func_70613_aW() && this.func_70090_H()) {
         this.func_191958_b(var1, var2, var3, 0.01F);
         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.8999999761581421D;
         this.field_70181_x *= 0.8999999761581421D;
         this.field_70179_y *= 0.8999999761581421D;
         if (this.func_70638_az() == null) {
            this.field_70181_x -= 0.005D;
         }
      } else {
         super.func_191986_a(var1, var2, var3);
      }

   }

   public void func_70636_d() {
      if (!this.func_70090_H() && this.field_70122_E && this.field_70124_G) {
         this.field_70181_x += 0.4000000059604645D;
         this.field_70159_w += (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 0.05F);
         this.field_70179_y += (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 0.05F);
         this.field_70122_E = false;
         this.field_70160_al = true;
         this.func_184185_a(this.func_203701_dz(), this.func_70599_aP(), this.func_70647_i());
      }

      super.func_70636_d();
   }

   protected boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (var3.func_77973_b() == Items.field_151131_as && this.func_70089_S()) {
         this.func_184185_a(SoundEvents.field_203814_aa, 1.0F, 1.0F);
         var3.func_190918_g(1);
         ItemStack var4 = this.func_203707_dx();
         this.func_204211_f(var4);
         if (!this.field_70170_p.field_72995_K) {
            CriteriaTriggers.field_204813_j.func_204817_a((EntityPlayerMP)var1, var4);
         }

         if (var3.func_190926_b()) {
            var1.func_184611_a(var2, var4);
         } else if (!var1.field_71071_by.func_70441_a(var4)) {
            var1.func_71019_a(var4, false);
         }

         this.func_70106_y();
         return true;
      } else {
         return super.func_184645_a(var1, var2);
      }
   }

   protected void func_204211_f(ItemStack var1) {
      if (this.func_145818_k_()) {
         var1.func_200302_a(this.func_200201_e());
      }

   }

   protected abstract ItemStack func_203707_dx();

   protected boolean func_212800_dy() {
      return true;
   }

   protected abstract SoundEvent func_203701_dz();

   protected SoundEvent func_184184_Z() {
      return SoundEvents.field_203817_bZ;
   }

   static {
      field_203711_b = EntityDataManager.func_187226_a(AbstractFish.class, DataSerializers.field_187198_h);
   }

   static class MoveHelper extends EntityMoveHelper {
      private final AbstractFish field_203781_i;

      MoveHelper(AbstractFish var1) {
         super(var1);
         this.field_203781_i = var1;
      }

      public void func_75641_c() {
         AbstractFish var10000;
         if (this.field_203781_i.func_208600_a(FluidTags.field_206959_a)) {
            var10000 = this.field_203781_i;
            var10000.field_70181_x += 0.005D;
         }

         if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO && !this.field_203781_i.func_70661_as().func_75500_f()) {
            double var1 = this.field_75646_b - this.field_203781_i.field_70165_t;
            double var3 = this.field_75647_c - this.field_203781_i.field_70163_u;
            double var5 = this.field_75644_d - this.field_203781_i.field_70161_v;
            double var7 = (double)MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5);
            var3 /= var7;
            float var9 = (float)(MathHelper.func_181159_b(var5, var1) * 57.2957763671875D) - 90.0F;
            this.field_203781_i.field_70177_z = this.func_75639_a(this.field_203781_i.field_70177_z, var9, 90.0F);
            this.field_203781_i.field_70761_aq = this.field_203781_i.field_70177_z;
            float var10 = (float)(this.field_75645_e * this.field_203781_i.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            this.field_203781_i.func_70659_e(this.field_203781_i.func_70689_ay() + (var10 - this.field_203781_i.func_70689_ay()) * 0.125F);
            var10000 = this.field_203781_i;
            var10000.field_70181_x += (double)this.field_203781_i.func_70689_ay() * var3 * 0.1D;
         } else {
            this.field_203781_i.func_70659_e(0.0F);
         }
      }
   }

   static class AISwim extends EntityAIWanderSwim {
      private final AbstractFish field_203788_h;

      public AISwim(AbstractFish var1) {
         super(var1, 1.0D, 40);
         this.field_203788_h = var1;
      }

      public boolean func_75250_a() {
         return this.field_203788_h.func_212800_dy() && super.func_75250_a();
      }
   }
}
