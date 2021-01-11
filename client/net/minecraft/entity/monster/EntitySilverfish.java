package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob {
   private EntitySilverfish.AISummonSilverfish field_175460_b;

   public EntitySilverfish(World var1) {
      super(var1);
      this.func_70105_a(0.4F, 0.3F);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(3, this.field_175460_b = new EntitySilverfish.AISummonSilverfish(this));
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
      this.field_70714_bg.func_75776_a(5, new EntitySilverfish.AIHideInStone(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
   }

   public double func_70033_W() {
      return 0.2D;
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

   protected String func_70639_aQ() {
      return "mob.silverfish.say";
   }

   protected String func_70621_aR() {
      return "mob.silverfish.hit";
   }

   protected String func_70673_aS() {
      return "mob.silverfish.kill";
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         if (var1 instanceof EntityDamageSource || var1 == DamageSource.field_76376_m) {
            this.field_175460_b.func_179462_f();
         }

         return super.func_70097_a(var1, var2);
      }
   }

   protected void func_180429_a(BlockPos var1, Block var2) {
      this.func_85030_a("mob.silverfish.step", 0.15F, 1.0F);
   }

   protected Item func_146068_u() {
      return null;
   }

   public void func_70071_h_() {
      this.field_70761_aq = this.field_70177_z;
      super.func_70071_h_();
   }

   public float func_180484_a(BlockPos var1) {
      return this.field_70170_p.func_180495_p(var1.func_177977_b()).func_177230_c() == Blocks.field_150348_b ? 10.0F : super.func_180484_a(var1);
   }

   protected boolean func_70814_o() {
      return true;
   }

   public boolean func_70601_bi() {
      if (super.func_70601_bi()) {
         EntityPlayer var1 = this.field_70170_p.func_72890_a(this, 5.0D);
         return var1 == null;
      } else {
         return false;
      }
   }

   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   static class AIHideInStone extends EntityAIWander {
      private final EntitySilverfish field_179485_a;
      private EnumFacing field_179483_b;
      private boolean field_179484_c;

      public AIHideInStone(EntitySilverfish var1) {
         super(var1, 1.0D, 10);
         this.field_179485_a = var1;
         this.func_75248_a(1);
      }

      public boolean func_75250_a() {
         if (this.field_179485_a.func_70638_az() != null) {
            return false;
         } else if (!this.field_179485_a.func_70661_as().func_75500_f()) {
            return false;
         } else {
            Random var1 = this.field_179485_a.func_70681_au();
            if (var1.nextInt(10) == 0) {
               this.field_179483_b = EnumFacing.func_176741_a(var1);
               BlockPos var2 = (new BlockPos(this.field_179485_a.field_70165_t, this.field_179485_a.field_70163_u + 0.5D, this.field_179485_a.field_70161_v)).func_177972_a(this.field_179483_b);
               IBlockState var3 = this.field_179485_a.field_70170_p.func_180495_p(var2);
               if (BlockSilverfish.func_176377_d(var3)) {
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
            World var1 = this.field_179485_a.field_70170_p;
            BlockPos var2 = (new BlockPos(this.field_179485_a.field_70165_t, this.field_179485_a.field_70163_u + 0.5D, this.field_179485_a.field_70161_v)).func_177972_a(this.field_179483_b);
            IBlockState var3 = var1.func_180495_p(var2);
            if (BlockSilverfish.func_176377_d(var3)) {
               var1.func_180501_a(var2, Blocks.field_150418_aU.func_176223_P().func_177226_a(BlockSilverfish.field_176378_a, BlockSilverfish.EnumType.func_176878_a(var3)), 3);
               this.field_179485_a.func_70656_aK();
               this.field_179485_a.func_70106_y();
            }

         }
      }
   }

   static class AISummonSilverfish extends EntityAIBase {
      private EntitySilverfish field_179464_a;
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

            for(int var4 = 0; var4 <= 5 && var4 >= -5; var4 = var4 <= 0 ? 1 - var4 : 0 - var4) {
               for(int var5 = 0; var5 <= 10 && var5 >= -10; var5 = var5 <= 0 ? 1 - var5 : 0 - var5) {
                  for(int var6 = 0; var6 <= 10 && var6 >= -10; var6 = var6 <= 0 ? 1 - var6 : 0 - var6) {
                     BlockPos var7 = var3.func_177982_a(var5, var4, var6);
                     IBlockState var8 = var1.func_180495_p(var7);
                     if (var8.func_177230_c() == Blocks.field_150418_aU) {
                        if (var1.func_82736_K().func_82766_b("mobGriefing")) {
                           var1.func_175655_b(var7, true);
                        } else {
                           var1.func_180501_a(var7, ((BlockSilverfish.EnumType)var8.func_177229_b(BlockSilverfish.field_176378_a)).func_176883_d(), 3);
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
