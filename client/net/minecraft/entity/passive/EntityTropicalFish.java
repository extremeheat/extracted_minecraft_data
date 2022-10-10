package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityTropicalFish extends AbstractGroupFish {
   private static final DataParameter<Integer> field_204223_b;
   private static final ResourceLocation[] field_204224_c;
   private static final ResourceLocation[] field_204225_bx;
   private static final ResourceLocation[] field_204226_by;
   public static final int[] field_204227_bz;
   private boolean field_204228_bA = true;

   private static int func_204214_a(EntityTropicalFish.Type var0, EnumDyeColor var1, EnumDyeColor var2) {
      return var0.func_212550_a() & 255 | (var0.func_212551_b() & 255) << 8 | (var1.func_196059_a() & 255) << 16 | (var2.func_196059_a() & 255) << 24;
   }

   public EntityTropicalFish(World var1) {
      super(EntityType.field_204262_at, var1);
      this.func_70105_a(0.5F, 0.4F);
   }

   public static String func_212324_b(int var0) {
      return "entity.minecraft.tropical_fish.predefined." + var0;
   }

   public static EnumDyeColor func_212326_d(int var0) {
      return EnumDyeColor.func_196056_a(func_204216_dH(var0));
   }

   public static EnumDyeColor func_212323_p(int var0) {
      return EnumDyeColor.func_196056_a(func_204212_dI(var0));
   }

   public static String func_212327_q(int var0) {
      int var1 = func_212325_s(var0);
      int var2 = func_204213_dJ(var0);
      return "entity.minecraft.tropical_fish.type." + EntityTropicalFish.Type.func_212548_a(var1, var2);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_204223_b, 0);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Variant", this.func_204221_dB());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_204215_a(var1.func_74762_e("Variant"));
   }

   public void func_204215_a(int var1) {
      this.field_70180_af.func_187227_b(field_204223_b, var1);
   }

   public boolean func_204209_c(int var1) {
      return !this.field_204228_bA;
   }

   public int func_204221_dB() {
      return (Integer)this.field_70180_af.func_187225_a(field_204223_b);
   }

   protected void func_204211_f(ItemStack var1) {
      super.func_204211_f(var1);
      NBTTagCompound var2 = var1.func_196082_o();
      var2.func_74768_a("BucketVariantTag", this.func_204221_dB());
   }

   protected ItemStack func_203707_dx() {
      return new ItemStack(Items.field_204272_aO);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_204311_aI;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_204411_iV;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_204412_iW;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_204414_iY;
   }

   protected SoundEvent func_203701_dz() {
      return SoundEvents.field_204413_iX;
   }

   private static int func_204216_dH(int var0) {
      return (var0 & 16711680) >> 16;
   }

   public float[] func_204219_dC() {
      return EnumDyeColor.func_196056_a(func_204216_dH(this.func_204221_dB())).func_193349_f();
   }

   private static int func_204212_dI(int var0) {
      return (var0 & -16777216) >> 24;
   }

   public float[] func_204222_dD() {
      return EnumDyeColor.func_196056_a(func_204212_dI(this.func_204221_dB())).func_193349_f();
   }

   public static int func_212325_s(int var0) {
      return Math.min(var0 & 255, 1);
   }

   public int func_204217_dE() {
      return func_212325_s(this.func_204221_dB());
   }

   private static int func_204213_dJ(int var0) {
      return Math.min((var0 & '\uff00') >> 8, 5);
   }

   public ResourceLocation func_204220_dF() {
      return func_212325_s(this.func_204221_dB()) == 0 ? field_204225_bx[func_204213_dJ(this.func_204221_dB())] : field_204226_by[func_204213_dJ(this.func_204221_dB())];
   }

   public ResourceLocation func_204218_dG() {
      return field_204224_c[func_212325_s(this.func_204221_dB())];
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      Object var9 = super.func_204210_a(var1, var2, var3);
      if (var3 != null && var3.func_150297_b("BucketVariantTag", 3)) {
         this.func_204215_a(var3.func_74762_e("BucketVariantTag"));
         return (IEntityLivingData)var9;
      } else {
         int var4;
         int var5;
         int var6;
         int var7;
         if (var9 instanceof EntityTropicalFish.GroupData) {
            EntityTropicalFish.GroupData var8 = (EntityTropicalFish.GroupData)var9;
            var4 = var8.field_204263_a;
            var5 = var8.field_204264_b;
            var6 = var8.field_204265_c;
            var7 = var8.field_204266_d;
         } else if ((double)this.field_70146_Z.nextFloat() < 0.9D) {
            int var10 = field_204227_bz[this.field_70146_Z.nextInt(field_204227_bz.length)];
            var4 = var10 & 255;
            var5 = (var10 & '\uff00') >> 8;
            var6 = (var10 & 16711680) >> 16;
            var7 = (var10 & -16777216) >> 24;
            var9 = new EntityTropicalFish.GroupData(this, var4, var5, var6, var7);
         } else {
            this.field_204228_bA = false;
            var4 = this.field_70146_Z.nextInt(2);
            var5 = this.field_70146_Z.nextInt(6);
            var6 = this.field_70146_Z.nextInt(15);
            var7 = this.field_70146_Z.nextInt(15);
         }

         this.func_204215_a(var4 | var5 << 8 | var6 << 16 | var7 << 24);
         return (IEntityLivingData)var9;
      }
   }

   static {
      field_204223_b = EntityDataManager.func_187226_a(EntityTropicalFish.class, DataSerializers.field_187192_b);
      field_204224_c = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
      field_204225_bx = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
      field_204226_by = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
      field_204227_bz = new int[]{func_204214_a(EntityTropicalFish.Type.STRIPEY, EnumDyeColor.ORANGE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.GRAY, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.GRAY, EnumDyeColor.BLUE), func_204214_a(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.SUNSTREAK, EnumDyeColor.BLUE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.KOB, EnumDyeColor.ORANGE, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.SPOTTY, EnumDyeColor.PINK, EnumDyeColor.LIGHT_BLUE), func_204214_a(EntityTropicalFish.Type.BLOCKFISH, EnumDyeColor.PURPLE, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.RED), func_204214_a(EntityTropicalFish.Type.SPOTTY, EnumDyeColor.WHITE, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.GLITTER, EnumDyeColor.WHITE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.ORANGE), func_204214_a(EntityTropicalFish.Type.DASHER, EnumDyeColor.CYAN, EnumDyeColor.PINK), func_204214_a(EntityTropicalFish.Type.BRINELY, EnumDyeColor.LIME, EnumDyeColor.LIGHT_BLUE), func_204214_a(EntityTropicalFish.Type.BETTY, EnumDyeColor.RED, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.SNOOPER, EnumDyeColor.GRAY, EnumDyeColor.RED), func_204214_a(EntityTropicalFish.Type.BLOCKFISH, EnumDyeColor.RED, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.WHITE, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.KOB, EnumDyeColor.RED, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.SUNSTREAK, EnumDyeColor.GRAY, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.DASHER, EnumDyeColor.CYAN, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.YELLOW, EnumDyeColor.YELLOW)};
   }

   static class GroupData extends AbstractGroupFish.GroupData {
      private final int field_204263_a;
      private final int field_204264_b;
      private final int field_204265_c;
      private final int field_204266_d;

      private GroupData(EntityTropicalFish var1, int var2, int var3, int var4, int var5) {
         super(var1);
         this.field_204263_a = var2;
         this.field_204264_b = var3;
         this.field_204265_c = var4;
         this.field_204266_d = var5;
      }

      // $FF: synthetic method
      GroupData(EntityTropicalFish var1, int var2, int var3, int var4, int var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   static enum Type {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int field_212552_m;
      private final int field_212553_n;
      private static final EntityTropicalFish.Type[] field_212554_o = values();

      private Type(int var3, int var4) {
         this.field_212552_m = var3;
         this.field_212553_n = var4;
      }

      public int func_212550_a() {
         return this.field_212552_m;
      }

      public int func_212551_b() {
         return this.field_212553_n;
      }

      public static String func_212548_a(int var0, int var1) {
         return field_212554_o[var1 + 6 * var0].func_212549_c();
      }

      public String func_212549_c() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
