package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityHorse extends AbstractHorse {
   private static final UUID field_184786_bD = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final DataParameter<Integer> field_184789_bG;
   private static final DataParameter<Integer> field_184791_bI;
   private static final String[] field_110268_bz;
   private static final String[] field_110269_bA;
   private static final String[] field_110291_bB;
   private static final String[] field_110292_bC;
   private String field_110286_bQ;
   private final String[] field_110280_bR = new String[3];

   public EntityHorse(World var1) {
      super(EntityType.field_200762_B, var1);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184789_bG, 0);
      this.field_70180_af.func_187214_a(field_184791_bI, HorseArmorType.NONE.func_188579_a());
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Variant", this.func_110202_bQ());
      if (!this.field_110296_bG.func_70301_a(1).func_190926_b()) {
         var1.func_74782_a("ArmorItem", this.field_110296_bG.func_70301_a(1).func_77955_b(new NBTTagCompound()));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_110235_q(var1.func_74762_e("Variant"));
      if (var1.func_150297_b("ArmorItem", 10)) {
         ItemStack var2 = ItemStack.func_199557_a(var1.func_74775_l("ArmorItem"));
         if (!var2.func_190926_b() && HorseArmorType.func_188577_b(var2.func_77973_b())) {
            this.field_110296_bG.func_70299_a(1, var2);
         }
      }

      this.func_110232_cE();
   }

   public void func_110235_q(int var1) {
      this.field_70180_af.func_187227_b(field_184789_bG, var1);
      this.func_110230_cF();
   }

   public int func_110202_bQ() {
      return (Integer)this.field_70180_af.func_187225_a(field_184789_bG);
   }

   private void func_110230_cF() {
      this.field_110286_bQ = null;
   }

   private void func_110247_cG() {
      int var1 = this.func_110202_bQ();
      int var2 = (var1 & 255) % 7;
      int var3 = ((var1 & '\uff00') >> 8) % 5;
      HorseArmorType var4 = this.func_184783_dl();
      this.field_110280_bR[0] = field_110268_bz[var2];
      this.field_110280_bR[1] = field_110291_bB[var3];
      this.field_110280_bR[2] = var4.func_188574_d();
      this.field_110286_bQ = "horse/" + field_110269_bA[var2] + field_110292_bC[var3] + var4.func_188573_b();
   }

   public String func_110264_co() {
      if (this.field_110286_bQ == null) {
         this.func_110247_cG();
      }

      return this.field_110286_bQ;
   }

   public String[] func_110212_cp() {
      if (this.field_110286_bQ == null) {
         this.func_110247_cG();
      }

      return this.field_110280_bR;
   }

   protected void func_110232_cE() {
      super.func_110232_cE();
      this.func_146086_d(this.field_110296_bG.func_70301_a(1));
   }

   public void func_146086_d(ItemStack var1) {
      HorseArmorType var2 = HorseArmorType.func_188580_a(var1);
      this.field_70180_af.func_187227_b(field_184791_bI, var2.func_188579_a());
      this.func_110230_cF();
      if (!this.field_70170_p.field_72995_K) {
         this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_188479_b(field_184786_bD);
         int var3 = var2.func_188578_c();
         if (var3 != 0) {
            this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_111121_a((new AttributeModifier(field_184786_bD, "Horse armor bonus", (double)var3, 0)).func_111168_a(false));
         }
      }

   }

   public HorseArmorType func_184783_dl() {
      return HorseArmorType.func_188575_a((Integer)this.field_70180_af.func_187225_a(field_184791_bI));
   }

   public void func_76316_a(IInventory var1) {
      HorseArmorType var2 = this.func_184783_dl();
      super.func_76316_a(var1);
      HorseArmorType var3 = this.func_184783_dl();
      if (this.field_70173_aa > 20 && var2 != var3 && var3 != HorseArmorType.NONE) {
         this.func_184185_a(SoundEvents.field_187702_cm, 0.5F, 1.0F);
      }

   }

   protected void func_190680_a(SoundType var1) {
      super.func_190680_a(var1);
      if (this.field_70146_Z.nextInt(10) == 0) {
         this.func_184185_a(SoundEvents.field_187705_cn, var1.func_185843_a() * 0.6F, var1.func_185847_b());
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)this.func_110267_cL());
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(this.func_110203_cN());
      this.func_110148_a(field_110271_bv).func_111128_a(this.func_110245_cM());
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K && this.field_70180_af.func_187223_a()) {
         this.field_70180_af.func_187230_e();
         this.func_110230_cF();
      }

   }

   protected SoundEvent func_184639_G() {
      super.func_184639_G();
      return SoundEvents.field_187696_ck;
   }

   protected SoundEvent func_184615_bR() {
      super.func_184615_bR();
      return SoundEvents.field_187708_co;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      super.func_184601_bQ(var1);
      return SoundEvents.field_187717_cr;
   }

   protected SoundEvent func_184785_dv() {
      super.func_184785_dv();
      return SoundEvents.field_187699_cl;
   }

   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186396_D;
   }

   public boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      boolean var4 = !var3.func_190926_b();
      if (var4 && var3.func_77973_b() instanceof ItemSpawnEgg) {
         return super.func_184645_a(var1, var2);
      } else {
         if (!this.func_70631_g_()) {
            if (this.func_110248_bS() && var1.func_70093_af()) {
               this.func_110199_f(var1);
               return true;
            }

            if (this.func_184207_aI()) {
               return super.func_184645_a(var1, var2);
            }
         }

         if (var4) {
            if (this.func_190678_b(var1, var3)) {
               if (!var1.field_71075_bZ.field_75098_d) {
                  var3.func_190918_g(1);
               }

               return true;
            }

            if (var3.func_111282_a(var1, this, var2)) {
               return true;
            }

            if (!this.func_110248_bS()) {
               this.func_190687_dF();
               return true;
            }

            boolean var5 = HorseArmorType.func_188580_a(var3) != HorseArmorType.NONE;
            boolean var6 = !this.func_70631_g_() && !this.func_110257_ck() && var3.func_77973_b() == Items.field_151141_av;
            if (var5 || var6) {
               this.func_110199_f(var1);
               return true;
            }
         }

         if (this.func_70631_g_()) {
            return super.func_184645_a(var1, var2);
         } else {
            this.func_110237_h(var1);
            return true;
         }
      }
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (!(var1 instanceof EntityDonkey) && !(var1 instanceof EntityHorse)) {
         return false;
      } else {
         return this.func_110200_cJ() && ((AbstractHorse)var1).func_110200_cJ();
      }
   }

   public EntityAgeable func_90011_a(EntityAgeable var1) {
      Object var2;
      if (var1 instanceof EntityDonkey) {
         var2 = new EntityMule(this.field_70170_p);
      } else {
         EntityHorse var3 = (EntityHorse)var1;
         var2 = new EntityHorse(this.field_70170_p);
         int var5 = this.field_70146_Z.nextInt(9);
         int var4;
         if (var5 < 4) {
            var4 = this.func_110202_bQ() & 255;
         } else if (var5 < 8) {
            var4 = var3.func_110202_bQ() & 255;
         } else {
            var4 = this.field_70146_Z.nextInt(7);
         }

         int var6 = this.field_70146_Z.nextInt(5);
         if (var6 < 2) {
            var4 |= this.func_110202_bQ() & '\uff00';
         } else if (var6 < 4) {
            var4 |= var3.func_110202_bQ() & '\uff00';
         } else {
            var4 |= this.field_70146_Z.nextInt(5) << 8 & '\uff00';
         }

         ((EntityHorse)var2).func_110235_q(var4);
      }

      this.func_190681_a(var1, (AbstractHorse)var2);
      return (EntityAgeable)var2;
   }

   public boolean func_190677_dK() {
      return true;
   }

   public boolean func_190682_f(ItemStack var1) {
      return HorseArmorType.func_188577_b(var1.func_77973_b());
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      Object var5 = super.func_204210_a(var1, var2, var3);
      int var4;
      if (var5 instanceof EntityHorse.GroupData) {
         var4 = ((EntityHorse.GroupData)var5).field_190885_a;
      } else {
         var4 = this.field_70146_Z.nextInt(7);
         var5 = new EntityHorse.GroupData(var4);
      }

      this.func_110235_q(var4 | this.field_70146_Z.nextInt(5) << 8);
      return (IEntityLivingData)var5;
   }

   static {
      field_184789_bG = EntityDataManager.func_187226_a(EntityHorse.class, DataSerializers.field_187192_b);
      field_184791_bI = EntityDataManager.func_187226_a(EntityHorse.class, DataSerializers.field_187192_b);
      field_110268_bz = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
      field_110269_bA = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
      field_110291_bB = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
      field_110292_bC = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   }

   public static class GroupData implements IEntityLivingData {
      public int field_190885_a;

      public GroupData(int var1) {
         super();
         this.field_190885_a = var1;
      }
   }
}
