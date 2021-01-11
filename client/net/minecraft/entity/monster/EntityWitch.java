package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWitch extends EntityMob implements IRangedAttackMob {
   private static final UUID field_110184_bp = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier field_110185_bq;
   private static final Item[] field_82199_d;
   private int field_82200_e;

   public EntityWitch(World var1) {
      super(var1);
      this.func_70105_a(0.6F, 1.95F);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
      this.field_70714_bg.func_75776_a(2, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(3, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_70096_w().func_75682_a(21, (byte)0);
   }

   protected String func_70639_aQ() {
      return null;
   }

   protected String func_70621_aR() {
      return null;
   }

   protected String func_70673_aS() {
      return null;
   }

   public void func_82197_f(boolean var1) {
      this.func_70096_w().func_75692_b(21, Byte.valueOf((byte)(var1 ? 1 : 0)));
   }

   public boolean func_82198_m() {
      return this.func_70096_w().func_75683_a(21) == 1;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(26.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
   }

   public void func_70636_d() {
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_82198_m()) {
            if (this.field_82200_e-- <= 0) {
               this.func_82197_f(false);
               ItemStack var6 = this.func_70694_bm();
               this.func_70062_b(0, (ItemStack)null);
               if (var6 != null && var6.func_77973_b() == Items.field_151068_bn) {
                  List var5 = Items.field_151068_bn.func_77832_l(var6);
                  if (var5 != null) {
                     Iterator var3 = var5.iterator();

                     while(var3.hasNext()) {
                        PotionEffect var4 = (PotionEffect)var3.next();
                        this.func_70690_d(new PotionEffect(var4));
                     }
                  }
               }

               this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111124_b(field_110185_bq);
            }
         } else {
            short var1 = -1;
            if (this.field_70146_Z.nextFloat() < 0.15F && this.func_70055_a(Material.field_151586_h) && !this.func_70644_a(Potion.field_76427_o)) {
               var1 = 8237;
            } else if (this.field_70146_Z.nextFloat() < 0.15F && this.func_70027_ad() && !this.func_70644_a(Potion.field_76426_n)) {
               var1 = 16307;
            } else if (this.field_70146_Z.nextFloat() < 0.05F && this.func_110143_aJ() < this.func_110138_aP()) {
               var1 = 16341;
            } else if (this.field_70146_Z.nextFloat() < 0.25F && this.func_70638_az() != null && !this.func_70644_a(Potion.field_76424_c) && this.func_70638_az().func_70068_e(this) > 121.0D) {
               var1 = 16274;
            } else if (this.field_70146_Z.nextFloat() < 0.25F && this.func_70638_az() != null && !this.func_70644_a(Potion.field_76424_c) && this.func_70638_az().func_70068_e(this) > 121.0D) {
               var1 = 16274;
            }

            if (var1 > -1) {
               this.func_70062_b(0, new ItemStack(Items.field_151068_bn, 1, var1));
               this.field_82200_e = this.func_70694_bm().func_77988_m();
               this.func_82197_f(true);
               IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
               var2.func_111124_b(field_110185_bq);
               var2.func_111121_a(field_110185_bq);
            }
         }

         if (this.field_70146_Z.nextFloat() < 7.5E-4F) {
            this.field_70170_p.func_72960_a(this, (byte)15);
         }
      }

      super.func_70636_d();
   }

   public void func_70103_a(byte var1) {
      if (var1 == 15) {
         for(int var2 = 0; var2 < this.field_70146_Z.nextInt(35) + 10; ++var2) {
            this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_WITCH, this.field_70165_t + this.field_70146_Z.nextGaussian() * 0.12999999523162842D, this.func_174813_aQ().field_72337_e + 0.5D + this.field_70146_Z.nextGaussian() * 0.12999999523162842D, this.field_70161_v + this.field_70146_Z.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D);
         }
      } else {
         super.func_70103_a(var1);
      }

   }

   protected float func_70672_c(DamageSource var1, float var2) {
      var2 = super.func_70672_c(var1, var2);
      if (var1.func_76346_g() == this) {
         var2 = 0.0F;
      }

      if (var1.func_82725_o()) {
         var2 = (float)((double)var2 * 0.15D);
      }

      return var2;
   }

   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(3) + 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = this.field_70146_Z.nextInt(3);
         Item var6 = field_82199_d[this.field_70146_Z.nextInt(field_82199_d.length)];
         if (var2 > 0) {
            var5 += this.field_70146_Z.nextInt(var2 + 1);
         }

         for(int var7 = 0; var7 < var5; ++var7) {
            this.func_145779_a(var6, 1);
         }
      }

   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      if (!this.func_82198_m()) {
         EntityPotion var3 = new EntityPotion(this.field_70170_p, this, 32732);
         double var4 = var1.field_70163_u + (double)var1.func_70047_e() - 1.100000023841858D;
         var3.field_70125_A -= -20.0F;
         double var6 = var1.field_70165_t + var1.field_70159_w - this.field_70165_t;
         double var8 = var4 - this.field_70163_u;
         double var10 = var1.field_70161_v + var1.field_70179_y - this.field_70161_v;
         float var12 = MathHelper.func_76133_a(var6 * var6 + var10 * var10);
         if (var12 >= 8.0F && !var1.func_70644_a(Potion.field_76421_d)) {
            var3.func_82340_a(32698);
         } else if (var1.func_110143_aJ() >= 8.0F && !var1.func_70644_a(Potion.field_76436_u)) {
            var3.func_82340_a(32660);
         } else if (var12 <= 3.0F && !var1.func_70644_a(Potion.field_76437_t) && this.field_70146_Z.nextFloat() < 0.25F) {
            var3.func_82340_a(32696);
         }

         var3.func_70186_c(var6, var8 + (double)(var12 * 0.2F), var10, 0.75F, 8.0F);
         this.field_70170_p.func_72838_d(var3);
      }
   }

   public float func_70047_e() {
      return 1.62F;
   }

   static {
      field_110185_bq = (new AttributeModifier(field_110184_bp, "Drinking speed penalty", -0.25D, 0)).func_111168_a(false);
      field_82199_d = new Item[]{Items.field_151114_aO, Items.field_151102_aT, Items.field_151137_ax, Items.field_151070_bp, Items.field_151069_bo, Items.field_151016_H, Items.field_151055_y, Items.field_151055_y};
   }
}
