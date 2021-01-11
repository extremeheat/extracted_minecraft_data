package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging {
   public EntityPainting.EnumArt field_70522_e;

   public EntityPainting(World var1) {
      super(var1);
   }

   public EntityPainting(World var1, BlockPos var2, EnumFacing var3) {
      super(var1, var2);
      ArrayList var4 = Lists.newArrayList();
      EntityPainting.EnumArt[] var5 = EntityPainting.EnumArt.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EntityPainting.EnumArt var8 = var5[var7];
         this.field_70522_e = var8;
         this.func_174859_a(var3);
         if (this.func_70518_d()) {
            var4.add(var8);
         }
      }

      if (!var4.isEmpty()) {
         this.field_70522_e = (EntityPainting.EnumArt)var4.get(this.field_70146_Z.nextInt(var4.size()));
      }

      this.func_174859_a(var3);
   }

   public EntityPainting(World var1, BlockPos var2, EnumFacing var3, String var4) {
      this(var1, var2, var3);
      EntityPainting.EnumArt[] var5 = EntityPainting.EnumArt.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EntityPainting.EnumArt var8 = var5[var7];
         if (var8.field_75702_A.equals(var4)) {
            this.field_70522_e = var8;
            break;
         }
      }

      this.func_174859_a(var3);
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74778_a("Motive", this.field_70522_e.field_75702_A);
      super.func_70014_b(var1);
   }

   public void func_70037_a(NBTTagCompound var1) {
      String var2 = var1.func_74779_i("Motive");
      EntityPainting.EnumArt[] var3 = EntityPainting.EnumArt.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EntityPainting.EnumArt var6 = var3[var5];
         if (var6.field_75702_A.equals(var2)) {
            this.field_70522_e = var6;
         }
      }

      if (this.field_70522_e == null) {
         this.field_70522_e = EntityPainting.EnumArt.KEBAB;
      }

      super.func_70037_a(var1);
   }

   public int func_82329_d() {
      return this.field_70522_e.field_75703_B;
   }

   public int func_82330_g() {
      return this.field_70522_e.field_75704_C;
   }

   public void func_110128_b(Entity var1) {
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         if (var1 instanceof EntityPlayer) {
            EntityPlayer var2 = (EntityPlayer)var1;
            if (var2.field_71075_bZ.field_75098_d) {
               return;
            }
         }

         this.func_70099_a(new ItemStack(Items.field_151159_an), 0.0F);
      }
   }

   public void func_70012_b(double var1, double var3, double var5, float var7, float var8) {
      BlockPos var9 = this.field_174861_a.func_177963_a(var1 - this.field_70165_t, var3 - this.field_70163_u, var5 - this.field_70161_v);
      this.func_70107_b((double)var9.func_177958_n(), (double)var9.func_177956_o(), (double)var9.func_177952_p());
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      BlockPos var11 = this.field_174861_a.func_177963_a(var1 - this.field_70165_t, var3 - this.field_70163_u, var5 - this.field_70161_v);
      this.func_70107_b((double)var11.func_177958_n(), (double)var11.func_177956_o(), (double)var11.func_177952_p());
   }

   public static enum EnumArt {
      KEBAB("Kebab", 16, 16, 0, 0),
      AZTEC("Aztec", 16, 16, 16, 0),
      ALBAN("Alban", 16, 16, 32, 0),
      AZTEC_2("Aztec2", 16, 16, 48, 0),
      BOMB("Bomb", 16, 16, 64, 0),
      PLANT("Plant", 16, 16, 80, 0),
      WASTELAND("Wasteland", 16, 16, 96, 0),
      POOL("Pool", 32, 16, 0, 32),
      COURBET("Courbet", 32, 16, 32, 32),
      SEA("Sea", 32, 16, 64, 32),
      SUNSET("Sunset", 32, 16, 96, 32),
      CREEBET("Creebet", 32, 16, 128, 32),
      WANDERER("Wanderer", 16, 32, 0, 64),
      GRAHAM("Graham", 16, 32, 16, 64),
      MATCH("Match", 32, 32, 0, 128),
      BUST("Bust", 32, 32, 32, 128),
      STAGE("Stage", 32, 32, 64, 128),
      VOID("Void", 32, 32, 96, 128),
      SKULL_AND_ROSES("SkullAndRoses", 32, 32, 128, 128),
      WITHER("Wither", 32, 32, 160, 128),
      FIGHTERS("Fighters", 64, 32, 0, 96),
      POINTER("Pointer", 64, 64, 0, 192),
      PIGSCENE("Pigscene", 64, 64, 64, 192),
      BURNING_SKULL("BurningSkull", 64, 64, 128, 192),
      SKELETON("Skeleton", 64, 48, 192, 64),
      DONKEY_KONG("DonkeyKong", 64, 48, 192, 112);

      public static final int field_180001_A = "SkullAndRoses".length();
      public final String field_75702_A;
      public final int field_75703_B;
      public final int field_75704_C;
      public final int field_75699_D;
      public final int field_75700_E;

      private EnumArt(String var3, int var4, int var5, int var6, int var7) {
         this.field_75702_A = var3;
         this.field_75703_B = var4;
         this.field_75704_C = var5;
         this.field_75699_D = var6;
         this.field_75700_E = var7;
      }
   }
}
