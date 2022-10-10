package net.minecraft.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityDrowned extends EntityZombie implements IRangedAttackMob {
   private boolean field_204718_bx;
   protected final PathNavigateSwimmer field_204716_a;
   protected final PathNavigateGround field_204717_b;

   public EntityDrowned(World var1) {
      super(EntityType.field_204724_o, var1);
      this.field_70138_W = 1.0F;
      this.field_70765_h = new EntityDrowned.MoveHelper(this);
      this.func_184644_a(PathNodeType.WATER, 0.0F);
      this.field_204716_a = new PathNavigateSwimmer(this, var1);
      this.field_204717_b = new PathNavigateGround(this, var1);
   }

   protected void func_175456_n() {
      this.field_70714_bg.func_75776_a(1, new EntityDrowned.AIGoToWater(this, 1.0D));
      this.field_70714_bg.func_75776_a(2, new EntityDrowned.AITridentAttack(this, 1.0D, 40, 10.0F));
      this.field_70714_bg.func_75776_a(2, new EntityDrowned.AIAttack(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(5, new EntityDrowned.AIGoToBeach(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityDrowned.AISwimUp(this, 1.0D, this.field_70170_p.func_181545_F()));
      this.field_70714_bg.func_75776_a(7, new EntityAIWander(this, 1.0D));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityDrowned.class}));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, new EntityDrowned.AttackTargetPredicate(this)));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
      this.field_70715_bh.func_75776_a(5, new EntityAINearestAttackableTarget(this, EntityTurtle.class, 10, true, false, EntityTurtle.field_203029_bx));
   }

   protected PathNavigate func_175447_b(World var1) {
      return super.func_175447_b(var1);
   }

   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      var2 = super.func_204210_a(var1, var2, var3);
      if (this.func_184582_a(EntityEquipmentSlot.OFFHAND).func_190926_b() && this.field_70146_Z.nextFloat() < 0.03F) {
         this.func_184201_a(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.field_205157_eZ));
         this.field_82174_bp[EntityEquipmentSlot.OFFHAND.func_188454_b()] = 2.0F;
      }

      return var2;
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      Biome var3 = var1.func_180494_b(new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v));
      if (var3 != Biomes.field_76781_i && var3 != Biomes.field_76777_m) {
         return this.field_70146_Z.nextInt(40) == 0 && this.func_204712_dC() && super.func_205020_a(var1, var2);
      } else {
         return this.field_70146_Z.nextInt(15) == 0 && super.func_205020_a(var1, var2);
      }
   }

   private boolean func_204712_dC() {
      return this.func_174813_aQ().field_72338_b < (double)(this.field_70170_p.func_181545_F() - 5);
   }

   protected boolean func_204900_dz() {
      return false;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_204770_aM;
   }

   protected SoundEvent func_184639_G() {
      return this.func_70090_H() ? SoundEvents.field_204775_aZ : SoundEvents.field_204774_aY;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return this.func_70090_H() ? SoundEvents.field_204779_bd : SoundEvents.field_204778_bc;
   }

   protected SoundEvent func_184615_bR() {
      return this.func_70090_H() ? SoundEvents.field_204777_bb : SoundEvents.field_204776_ba;
   }

   protected SoundEvent func_190731_di() {
      return SoundEvents.field_204781_bf;
   }

   protected SoundEvent func_184184_Z() {
      return SoundEvents.field_204782_bg;
   }

   protected ItemStack func_190732_dj() {
      return ItemStack.field_190927_a;
   }

   protected void func_180481_a(DifficultyInstance var1) {
      if ((double)this.field_70146_Z.nextFloat() > 0.9D) {
         int var2 = this.field_70146_Z.nextInt(16);
         if (var2 < 10) {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_203184_eO));
         } else {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151112_aM));
         }
      }

   }

   protected boolean func_208003_a(ItemStack var1, ItemStack var2, EntityEquipmentSlot var3) {
      if (var2.func_77973_b() == Items.field_205157_eZ) {
         return false;
      } else if (var2.func_77973_b() == Items.field_203184_eO) {
         if (var1.func_77973_b() == Items.field_203184_eO) {
            return var1.func_77952_i() < var2.func_77952_i();
         } else {
            return false;
         }
      } else {
         return var1.func_77973_b() == Items.field_203184_eO ? true : super.func_208003_a(var1, var2, var3);
      }
   }

   protected boolean func_204703_dA() {
      return false;
   }

   public boolean func_205019_a(IWorldReaderBase var1) {
      return var1.func_195587_c(this, this.func_174813_aQ()) && var1.func_195586_b(this, this.func_174813_aQ());
   }

   public boolean func_204714_e(@Nullable EntityLivingBase var1) {
      if (var1 != null) {
         return !this.field_70170_p.func_72935_r() || var1.func_70090_H();
      } else {
         return false;
      }
   }

   public boolean func_96092_aw() {
      return !this.func_203007_ba();
   }

   private boolean func_204715_dF() {
      if (this.field_204718_bx) {
         return true;
      } else {
         EntityLivingBase var1 = this.func_70638_az();
         return var1 != null && var1.func_70090_H();
      }
   }

   public void func_191986_a(float var1, float var2, float var3) {
      if (this.func_70613_aW() && this.func_70090_H() && this.func_204715_dF()) {
         this.func_191958_b(var1, var2, var3, 0.01F);
         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.8999999761581421D;
         this.field_70181_x *= 0.8999999761581421D;
         this.field_70179_y *= 0.8999999761581421D;
      } else {
         super.func_191986_a(var1, var2, var3);
      }

   }

   public void func_205343_av() {
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_70613_aW() && this.func_70090_H() && this.func_204715_dF()) {
            this.field_70699_by = this.field_204716_a;
            this.func_204711_a(true);
         } else {
            this.field_70699_by = this.field_204717_b;
            this.func_204711_a(false);
         }
      }

   }

   protected boolean func_204710_dB() {
      Path var1 = this.func_70661_as().func_75505_d();
      if (var1 != null) {
         PathPoint var2 = var1.func_189964_i();
         if (var2 != null) {
            double var3 = this.func_70092_e((double)var2.field_75839_a, (double)var2.field_75837_b, (double)var2.field_75838_c);
            if (var3 < 4.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      EntityTrident var3 = new EntityTrident(this.field_70170_p, this, new ItemStack(Items.field_203184_eO));
      double var4 = var1.field_70165_t - this.field_70165_t;
      double var6 = var1.func_174813_aQ().field_72338_b + (double)(var1.field_70131_O / 3.0F) - var3.field_70163_u;
      double var8 = var1.field_70161_v - this.field_70161_v;
      double var10 = (double)MathHelper.func_76133_a(var4 * var4 + var8 * var8);
      var3.func_70186_c(var4, var6 + var10 * 0.20000000298023224D, var8, 1.6F, (float)(14 - this.field_70170_p.func_175659_aa().func_151525_a() * 4));
      this.func_184185_a(SoundEvents.field_204780_be, 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.field_70170_p.func_72838_d(var3);
   }

   public void func_204713_s(boolean var1) {
      this.field_204718_bx = var1;
   }

   static class AttackTargetPredicate implements Predicate<EntityPlayer> {
      private final EntityDrowned field_204740_a;

      public AttackTargetPredicate(EntityDrowned var1) {
         super();
         this.field_204740_a = var1;
      }

      public boolean test(@Nullable EntityPlayer var1) {
         return this.field_204740_a.func_204714_e(var1);
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((EntityPlayer)var1);
      }
   }

   static class MoveHelper extends EntityMoveHelper {
      private final EntityDrowned field_204725_i;

      public MoveHelper(EntityDrowned var1) {
         super(var1);
         this.field_204725_i = var1;
      }

      public void func_75641_c() {
         EntityLivingBase var1 = this.field_204725_i.func_70638_az();
         EntityDrowned var10000;
         if (this.field_204725_i.func_204715_dF() && this.field_204725_i.func_70090_H()) {
            if (var1 != null && var1.field_70163_u > this.field_204725_i.field_70163_u || this.field_204725_i.field_204718_bx) {
               var10000 = this.field_204725_i;
               var10000.field_70181_x += 0.002D;
            }

            if (this.field_188491_h != EntityMoveHelper.Action.MOVE_TO || this.field_204725_i.func_70661_as().func_75500_f()) {
               this.field_204725_i.func_70659_e(0.0F);
               return;
            }

            double var2 = this.field_75646_b - this.field_204725_i.field_70165_t;
            double var4 = this.field_75647_c - this.field_204725_i.field_70163_u;
            double var6 = this.field_75644_d - this.field_204725_i.field_70161_v;
            double var8 = (double)MathHelper.func_76133_a(var2 * var2 + var4 * var4 + var6 * var6);
            var4 /= var8;
            float var10 = (float)(MathHelper.func_181159_b(var6, var2) * 57.2957763671875D) - 90.0F;
            this.field_204725_i.field_70177_z = this.func_75639_a(this.field_204725_i.field_70177_z, var10, 90.0F);
            this.field_204725_i.field_70761_aq = this.field_204725_i.field_70177_z;
            float var11 = (float)(this.field_75645_e * this.field_204725_i.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            this.field_204725_i.func_70659_e(this.field_204725_i.func_70689_ay() + (var11 - this.field_204725_i.func_70689_ay()) * 0.125F);
            var10000 = this.field_204725_i;
            var10000.field_70181_x += (double)this.field_204725_i.func_70689_ay() * var4 * 0.1D;
            var10000 = this.field_204725_i;
            var10000.field_70159_w += (double)this.field_204725_i.func_70689_ay() * var2 * 0.005D;
            var10000 = this.field_204725_i;
            var10000.field_70179_y += (double)this.field_204725_i.func_70689_ay() * var6 * 0.005D;
         } else {
            if (!this.field_204725_i.field_70122_E) {
               var10000 = this.field_204725_i;
               var10000.field_70181_x -= 0.008D;
            }

            super.func_75641_c();
         }

      }
   }

   static class AIAttack extends EntityAIZombieAttack {
      private final EntityDrowned field_204726_g;

      public AIAttack(EntityDrowned var1, double var2, boolean var4) {
         super(var1, var2, var4);
         this.field_204726_g = var1;
      }

      public boolean func_75250_a() {
         return super.func_75250_a() && this.field_204726_g.func_204714_e(this.field_204726_g.func_70638_az());
      }

      public boolean func_75253_b() {
         return super.func_75253_b() && this.field_204726_g.func_204714_e(this.field_204726_g.func_70638_az());
      }
   }

   static class AIGoToWater extends EntityAIBase {
      private final EntityCreature field_204730_a;
      private double field_204731_b;
      private double field_204732_c;
      private double field_204733_d;
      private final double field_204734_e;
      private final World field_204735_f;

      public AIGoToWater(EntityCreature var1, double var2) {
         super();
         this.field_204730_a = var1;
         this.field_204734_e = var2;
         this.field_204735_f = var1.field_70170_p;
         this.func_75248_a(1);
      }

      public boolean func_75250_a() {
         if (!this.field_204735_f.func_72935_r()) {
            return false;
         } else if (this.field_204730_a.func_70090_H()) {
            return false;
         } else {
            Vec3d var1 = this.func_204729_f();
            if (var1 == null) {
               return false;
            } else {
               this.field_204731_b = var1.field_72450_a;
               this.field_204732_c = var1.field_72448_b;
               this.field_204733_d = var1.field_72449_c;
               return true;
            }
         }
      }

      public boolean func_75253_b() {
         return !this.field_204730_a.func_70661_as().func_75500_f();
      }

      public void func_75249_e() {
         this.field_204730_a.func_70661_as().func_75492_a(this.field_204731_b, this.field_204732_c, this.field_204733_d, this.field_204734_e);
      }

      @Nullable
      private Vec3d func_204729_f() {
         Random var1 = this.field_204730_a.func_70681_au();
         BlockPos var2 = new BlockPos(this.field_204730_a.field_70165_t, this.field_204730_a.func_174813_aQ().field_72338_b, this.field_204730_a.field_70161_v);

         for(int var3 = 0; var3 < 10; ++var3) {
            BlockPos var4 = var2.func_177982_a(var1.nextInt(20) - 10, 2 - var1.nextInt(8), var1.nextInt(20) - 10);
            if (this.field_204735_f.func_180495_p(var4).func_177230_c() == Blocks.field_150355_j) {
               return new Vec3d((double)var4.func_177958_n(), (double)var4.func_177956_o(), (double)var4.func_177952_p());
            }
         }

         return null;
      }
   }

   static class AIGoToBeach extends EntityAIMoveToBlock {
      private final EntityDrowned field_204727_f;

      public AIGoToBeach(EntityDrowned var1, double var2) {
         super(var1, var2, 8, 2);
         this.field_204727_f = var1;
      }

      public boolean func_75250_a() {
         return super.func_75250_a() && !this.field_204727_f.field_70170_p.func_72935_r() && this.field_204727_f.func_70090_H() && this.field_204727_f.field_70163_u >= (double)(this.field_204727_f.field_70170_p.func_181545_F() - 3);
      }

      public boolean func_75253_b() {
         return super.func_75253_b();
      }

      protected boolean func_179488_a(IWorldReaderBase var1, BlockPos var2) {
         BlockPos var3 = var2.func_177984_a();
         return var1.func_175623_d(var3) && var1.func_175623_d(var3.func_177984_a()) ? var1.func_180495_p(var2).func_185896_q() : false;
      }

      public void func_75249_e() {
         this.field_204727_f.func_204713_s(false);
         this.field_204727_f.field_70699_by = this.field_204727_f.field_204717_b;
         super.func_75249_e();
      }

      public void func_75251_c() {
         super.func_75251_c();
      }
   }

   static class AISwimUp extends EntityAIBase {
      private final EntityDrowned field_204736_a;
      private final double field_204737_b;
      private final int field_204738_c;
      private boolean field_204739_d;

      public AISwimUp(EntityDrowned var1, double var2, int var4) {
         super();
         this.field_204736_a = var1;
         this.field_204737_b = var2;
         this.field_204738_c = var4;
      }

      public boolean func_75250_a() {
         return !this.field_204736_a.field_70170_p.func_72935_r() && this.field_204736_a.func_70090_H() && this.field_204736_a.field_70163_u < (double)(this.field_204738_c - 2);
      }

      public boolean func_75253_b() {
         return this.func_75250_a() && !this.field_204739_d;
      }

      public void func_75246_d() {
         if (this.field_204736_a.field_70163_u < (double)(this.field_204738_c - 1) && (this.field_204736_a.func_70661_as().func_75500_f() || this.field_204736_a.func_204710_dB())) {
            Vec3d var1 = RandomPositionGenerator.func_75464_a(this.field_204736_a, 4, 8, new Vec3d(this.field_204736_a.field_70165_t, (double)(this.field_204738_c - 1), this.field_204736_a.field_70161_v));
            if (var1 == null) {
               this.field_204739_d = true;
               return;
            }

            this.field_204736_a.func_70661_as().func_75492_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, this.field_204737_b);
         }

      }

      public void func_75249_e() {
         this.field_204736_a.func_204713_s(true);
         this.field_204739_d = false;
      }

      public void func_75251_c() {
         this.field_204736_a.func_204713_s(false);
      }
   }

   static class AITridentAttack extends EntityAIAttackRanged {
      private final EntityDrowned field_204728_a;

      public AITridentAttack(IRangedAttackMob var1, double var2, int var4, float var5) {
         super(var1, var2, var4, var5);
         this.field_204728_a = (EntityDrowned)var1;
      }

      public boolean func_75250_a() {
         return super.func_75250_a() && this.field_204728_a.func_184614_ca().func_77973_b() == Items.field_203184_eO;
      }

      public void func_75249_e() {
         super.func_75249_e();
         this.field_204728_a.func_184724_a(true);
      }

      public void func_75251_c() {
         super.func_75251_c();
         this.field_204728_a.func_184724_a(false);
      }
   }
}
