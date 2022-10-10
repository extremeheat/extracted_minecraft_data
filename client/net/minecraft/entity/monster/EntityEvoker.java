package net.minecraft.entity.monster;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityEvoker extends EntitySpellcasterIllager {
   private EntitySheep field_190763_bw;

   public EntityEvoker(World var1) {
      super(EntityType.field_200806_t, var1);
      this.func_70105_a(0.6F, 1.95F);
      this.field_70728_aV = 10;
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityEvoker.AICastingSpell());
      this.field_70714_bg.func_75776_a(2, new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0F, 0.6D, 1.0D));
      this.field_70714_bg.func_75776_a(4, new EntityEvoker.AISummonSpell());
      this.field_70714_bg.func_75776_a(5, new EntityEvoker.AIAttackSpell());
      this.field_70714_bg.func_75776_a(6, new EntityEvoker.AIWololoSpell());
      this.field_70714_bg.func_75776_a(8, new EntityAIWander(this, 0.6D));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityEvoker.class}));
      this.field_70715_bh.func_75776_a(2, (new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)).func_190882_b(300));
      this.field_70715_bh.func_75776_a(3, (new EntityAINearestAttackableTarget(this, EntityVillager.class, false)).func_190882_b(300));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, false));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(12.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(24.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
   }

   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191185_au;
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
   }

   public boolean func_184191_r(Entity var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (super.func_184191_r(var1)) {
         return true;
      } else if (var1 instanceof EntityVex) {
         return this.func_184191_r(((EntityVex)var1).func_190645_o());
      } else if (var1 instanceof EntityLivingBase && ((EntityLivingBase)var1).func_70668_bt() == CreatureAttribute.ILLAGER) {
         return this.func_96124_cp() == null && var1.func_96124_cp() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_191243_bm;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_191245_bo;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_191246_bp;
   }

   private void func_190748_a(@Nullable EntitySheep var1) {
      this.field_190763_bw = var1;
   }

   @Nullable
   private EntitySheep func_190751_dj() {
      return this.field_190763_bw;
   }

   protected SoundEvent func_193086_dk() {
      return SoundEvents.field_191244_bn;
   }

   public class AIWololoSpell extends EntitySpellcasterIllager.AIUseSpell {
      private final Predicate<EntitySheep> field_190879_a = (var0) -> {
         return var0.func_175509_cj() == EnumDyeColor.BLUE;
      };

      public AIWololoSpell() {
         super();
      }

      public boolean func_75250_a() {
         if (EntityEvoker.this.func_70638_az() != null) {
            return false;
         } else if (EntityEvoker.this.func_193082_dl()) {
            return false;
         } else if (EntityEvoker.this.field_70173_aa < this.field_193322_d) {
            return false;
         } else if (!EntityEvoker.this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return false;
         } else {
            List var1 = EntityEvoker.this.field_70170_p.func_175647_a(EntitySheep.class, EntityEvoker.this.func_174813_aQ().func_72314_b(16.0D, 4.0D, 16.0D), this.field_190879_a);
            if (var1.isEmpty()) {
               return false;
            } else {
               EntityEvoker.this.func_190748_a((EntitySheep)var1.get(EntityEvoker.this.field_70146_Z.nextInt(var1.size())));
               return true;
            }
         }
      }

      public boolean func_75253_b() {
         return EntityEvoker.this.func_190751_dj() != null && this.field_193321_c > 0;
      }

      public void func_75251_c() {
         super.func_75251_c();
         EntityEvoker.this.func_190748_a((EntitySheep)null);
      }

      protected void func_190868_j() {
         EntitySheep var1 = EntityEvoker.this.func_190751_dj();
         if (var1 != null && var1.func_70089_S()) {
            var1.func_175512_b(EnumDyeColor.RED);
         }

      }

      protected int func_190867_m() {
         return 40;
      }

      protected int func_190869_f() {
         return 60;
      }

      protected int func_190872_i() {
         return 140;
      }

      protected SoundEvent func_190871_k() {
         return SoundEvents.field_191249_bs;
      }

      protected EntitySpellcasterIllager.SpellType func_193320_l() {
         return EntitySpellcasterIllager.SpellType.WOLOLO;
      }
   }

   class AISummonSpell extends EntitySpellcasterIllager.AIUseSpell {
      private AISummonSpell() {
         super();
      }

      public boolean func_75250_a() {
         if (!super.func_75250_a()) {
            return false;
         } else {
            int var1 = EntityEvoker.this.field_70170_p.func_72872_a(EntityVex.class, EntityEvoker.this.func_174813_aQ().func_186662_g(16.0D)).size();
            return EntityEvoker.this.field_70146_Z.nextInt(8) + 1 > var1;
         }
      }

      protected int func_190869_f() {
         return 100;
      }

      protected int func_190872_i() {
         return 340;
      }

      protected void func_190868_j() {
         for(int var1 = 0; var1 < 3; ++var1) {
            BlockPos var2 = (new BlockPos(EntityEvoker.this)).func_177982_a(-2 + EntityEvoker.this.field_70146_Z.nextInt(5), 1, -2 + EntityEvoker.this.field_70146_Z.nextInt(5));
            EntityVex var3 = new EntityVex(EntityEvoker.this.field_70170_p);
            var3.func_174828_a(var2, 0.0F, 0.0F);
            var3.func_204210_a(EntityEvoker.this.field_70170_p.func_175649_E(var2), (IEntityLivingData)null, (NBTTagCompound)null);
            var3.func_190658_a(EntityEvoker.this);
            var3.func_190651_g(var2);
            var3.func_190653_a(20 * (30 + EntityEvoker.this.field_70146_Z.nextInt(90)));
            EntityEvoker.this.field_70170_p.func_72838_d(var3);
         }

      }

      protected SoundEvent func_190871_k() {
         return SoundEvents.field_191248_br;
      }

      protected EntitySpellcasterIllager.SpellType func_193320_l() {
         return EntitySpellcasterIllager.SpellType.SUMMON_VEX;
      }

      // $FF: synthetic method
      AISummonSpell(Object var2) {
         this();
      }
   }

   class AIAttackSpell extends EntitySpellcasterIllager.AIUseSpell {
      private AIAttackSpell() {
         super();
      }

      protected int func_190869_f() {
         return 40;
      }

      protected int func_190872_i() {
         return 100;
      }

      protected void func_190868_j() {
         EntityLivingBase var1 = EntityEvoker.this.func_70638_az();
         double var2 = Math.min(var1.field_70163_u, EntityEvoker.this.field_70163_u);
         double var4 = Math.max(var1.field_70163_u, EntityEvoker.this.field_70163_u) + 1.0D;
         float var6 = (float)MathHelper.func_181159_b(var1.field_70161_v - EntityEvoker.this.field_70161_v, var1.field_70165_t - EntityEvoker.this.field_70165_t);
         int var7;
         if (EntityEvoker.this.func_70068_e(var1) < 9.0D) {
            float var8;
            for(var7 = 0; var7 < 5; ++var7) {
               var8 = var6 + (float)var7 * 3.1415927F * 0.4F;
               this.func_190876_a(EntityEvoker.this.field_70165_t + (double)MathHelper.func_76134_b(var8) * 1.5D, EntityEvoker.this.field_70161_v + (double)MathHelper.func_76126_a(var8) * 1.5D, var2, var4, var8, 0);
            }

            for(var7 = 0; var7 < 8; ++var7) {
               var8 = var6 + (float)var7 * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
               this.func_190876_a(EntityEvoker.this.field_70165_t + (double)MathHelper.func_76134_b(var8) * 2.5D, EntityEvoker.this.field_70161_v + (double)MathHelper.func_76126_a(var8) * 2.5D, var2, var4, var8, 3);
            }
         } else {
            for(var7 = 0; var7 < 16; ++var7) {
               double var11 = 1.25D * (double)(var7 + 1);
               int var10 = 1 * var7;
               this.func_190876_a(EntityEvoker.this.field_70165_t + (double)MathHelper.func_76134_b(var6) * var11, EntityEvoker.this.field_70161_v + (double)MathHelper.func_76126_a(var6) * var11, var2, var4, var6, var10);
            }
         }

      }

      private void func_190876_a(double var1, double var3, double var5, double var7, float var9, int var10) {
         BlockPos var11 = new BlockPos(var1, var7, var3);
         boolean var12 = false;
         double var13 = 0.0D;

         do {
            if (!EntityEvoker.this.field_70170_p.func_195595_w(var11) && EntityEvoker.this.field_70170_p.func_195595_w(var11.func_177977_b())) {
               if (!EntityEvoker.this.field_70170_p.func_175623_d(var11)) {
                  IBlockState var15 = EntityEvoker.this.field_70170_p.func_180495_p(var11);
                  VoxelShape var16 = var15.func_196952_d(EntityEvoker.this.field_70170_p, var11);
                  if (!var16.func_197766_b()) {
                     var13 = var16.func_197758_c(EnumFacing.Axis.Y);
                  }
               }

               var12 = true;
               break;
            }

            var11 = var11.func_177977_b();
         } while(var11.func_177956_o() >= MathHelper.func_76128_c(var5) - 1);

         if (var12) {
            EntityEvokerFangs var17 = new EntityEvokerFangs(EntityEvoker.this.field_70170_p, var1, (double)var11.func_177956_o() + var13, var3, var9, var10, EntityEvoker.this);
            EntityEvoker.this.field_70170_p.func_72838_d(var17);
         }

      }

      protected SoundEvent func_190871_k() {
         return SoundEvents.field_191247_bq;
      }

      protected EntitySpellcasterIllager.SpellType func_193320_l() {
         return EntitySpellcasterIllager.SpellType.FANGS;
      }

      // $FF: synthetic method
      AIAttackSpell(Object var2) {
         this();
      }
   }

   class AICastingSpell extends EntitySpellcasterIllager.AICastingApell {
      private AICastingSpell() {
         super();
      }

      public void func_75246_d() {
         if (EntityEvoker.this.func_70638_az() != null) {
            EntityEvoker.this.func_70671_ap().func_75651_a(EntityEvoker.this.func_70638_az(), (float)EntityEvoker.this.func_184649_cE(), (float)EntityEvoker.this.func_70646_bf());
         } else if (EntityEvoker.this.func_190751_dj() != null) {
            EntityEvoker.this.func_70671_ap().func_75651_a(EntityEvoker.this.func_190751_dj(), (float)EntityEvoker.this.func_184649_cE(), (float)EntityEvoker.this.func_70646_bf());
         }

      }

      // $FF: synthetic method
      AICastingSpell(Object var2) {
         this();
      }
   }
}
