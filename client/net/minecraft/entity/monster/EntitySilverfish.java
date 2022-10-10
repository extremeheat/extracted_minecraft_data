package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySilverfish extends EntityMob {
   private EntitySilverfish.AISummonSilverfish field_175460_b;

   public EntitySilverfish(World var1) {
      super(EntityType.field_200740_af, var1);
      this.func_70105_a(0.4F, 0.3F);
   }

   protected void func_184651_r() {
      this.field_175460_b = new EntitySilverfish.AISummonSilverfish(this);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(3, this.field_175460_b);
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackMelee(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(5, new EntitySilverfish.AIHideInStone(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
   }

   public double func_70033_W() {
      return 0.1D;
   }

   public float func_70047_e() {
      return 0.1F;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0D);
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187793_eY;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187850_fa;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187795_eZ;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(SoundEvents.field_187852_fb, 0.15F, 1.0F);
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         if ((var1 instanceof EntityDamageSource || var1 == DamageSource.field_76376_m) && this.field_175460_b != null) {
            this.field_175460_b.func_179462_f();
         }

         return super.func_70097_a(var1, var2);
      }
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186438_t;
   }

   public void func_70071_h_() {
      this.field_70761_aq = this.field_70177_z;
      super.func_70071_h_();
   }

   public void func_181013_g(float var1) {
      this.field_70177_z = var1;
      super.func_181013_g(var1);
   }

   public float func_205022_a(BlockPos var1, IWorldReaderBase var2) {
      return BlockSilverfish.func_196466_i(var2.func_180495_p(var1.func_177977_b())) ? 10.0F : super.func_205022_a(var1, var2);
   }

   protected boolean func_70814_o() {
      return true;
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      if (super.func_205020_a(var1, var2)) {
         EntityPlayer var3 = var1.func_184136_b(this, 5.0D);
         return var3 == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.ARTHROPOD;
   }

   static class AIHideInStone extends EntityAIWander {
      private EnumFacing field_179483_b;
      private boolean field_179484_c;

      public AIHideInStone(EntitySilverfish var1) {
         super(var1, 1.0D, 10);
         this.func_75248_a(1);
      }

      public boolean func_75250_a() {
         if (this.field_75457_a.func_70638_az() != null) {
            return false;
         } else if (!this.field_75457_a.func_70661_as().func_75500_f()) {
            return false;
         } else {
            Random var1 = this.field_75457_a.func_70681_au();
            if (this.field_75457_a.field_70170_p.func_82736_K().func_82766_b("mobGriefing") && var1.nextInt(10) == 0) {
               this.field_179483_b = EnumFacing.func_176741_a(var1);
               BlockPos var2 = (new BlockPos(this.field_75457_a.field_70165_t, this.field_75457_a.field_70163_u + 0.5D, this.field_75457_a.field_70161_v)).func_177972_a(this.field_179483_b);
               IBlockState var3 = this.field_75457_a.field_70170_p.func_180495_p(var2);
               if (BlockSilverfish.func_196466_i(var3)) {
                  this.field_179484_c = true;
                  return true;
               }
            }

            this.field_179484_c = false;
            return super.func_75250_a();
         }
      }

      public boolean func_75253_b() {
         return this.field_179484_c ? false : super.func_75253_b();
      }

      public void func_75249_e() {
         if (!this.field_179484_c) {
            super.func_75249_e();
         } else {
            World var1 = this.field_75457_a.field_70170_p;
            BlockPos var2 = (new BlockPos(this.field_75457_a.field_70165_t, this.field_75457_a.field_70163_u + 0.5D, this.field_75457_a.field_70161_v)).func_177972_a(this.field_179483_b);
            IBlockState var3 = var1.func_180495_p(var2);
            if (BlockSilverfish.func_196466_i(var3)) {
               var1.func_180501_a(var2, BlockSilverfish.func_196467_h(var3.func_177230_c()), 3);
               this.field_75457_a.func_70656_aK();
               this.field_75457_a.func_70106_y();
            }

         }
      }
   }

   static class AISummonSilverfish extends EntityAIBase {
      private final EntitySilverfish field_179464_a;
      private int field_179463_b;

      public AISummonSilverfish(EntitySilverfish var1) {
         super();
         this.field_179464_a = var1;
      }

      public void func_179462_f() {
         if (this.field_179463_b == 0) {
            this.field_179463_b = 20;
         }

      }

      public boolean func_75250_a() {
         return this.field_179463_b > 0;
      }

      public void func_75246_d() {
         --this.field_179463_b;
         if (this.field_179463_b <= 0) {
            World var1 = this.field_179464_a.field_70170_p;
            Random var2 = this.field_179464_a.func_70681_au();
            BlockPos var3 = new BlockPos(this.field_179464_a);

            for(int var4 = 0; var4 <= 5 && var4 >= -5; var4 = (var4 <= 0 ? 1 : 0) - var4) {
               for(int var5 = 0; var5 <= 10 && var5 >= -10; var5 = (var5 <= 0 ? 1 : 0) - var5) {
                  for(int var6 = 0; var6 <= 10 && var6 >= -10; var6 = (var6 <= 0 ? 1 : 0) - var6) {
                     BlockPos var7 = var3.func_177982_a(var5, var4, var6);
                     IBlockState var8 = var1.func_180495_p(var7);
                     Block var9 = var8.func_177230_c();
                     if (var9 instanceof BlockSilverfish) {
                        if (var1.func_82736_K().func_82766_b("mobGriefing")) {
                           var1.func_175655_b(var7, true);
                        } else {
                           var1.func_180501_a(var7, ((BlockSilverfish)var9).func_196468_d().func_176223_P(), 3);
                        }

                        if (var2.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
