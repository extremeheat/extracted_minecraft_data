package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Corridor extends StructureComponent {
   private boolean field_74958_a;
   private boolean field_74956_b;
   private boolean field_74957_c;
   private int field_74955_d;

   public StructureMineshaftPieces$Corridor() {
      super();
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74757_a("hr", this.field_74958_a);
      var1.func_74757_a("sc", this.field_74956_b);
      var1.func_74757_a("hps", this.field_74957_c);
      var1.func_74768_a("Num", this.field_74955_d);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      this.field_74958_a = var1.func_74767_n("hr");
      this.field_74956_b = var1.func_74767_n("sc");
      this.field_74957_c = var1.func_74767_n("hps");
      this.field_74955_d = var1.func_74762_e("Num");
   }

   public StructureMineshaftPieces$Corridor(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
      this.field_74958_a = var2.nextInt(3) == 0;
      this.field_74956_b = !this.field_74958_a && var2.nextInt(23) == 0;
      if (this.field_74885_f != 2 && this.field_74885_f != 0) {
         this.field_74955_d = var3.func_78883_b() / 5;
      } else {
         this.field_74955_d = var3.func_78880_d() / 5;
      }
   }

   public static StructureBoundingBox func_74954_a(List var0, Random var1, int var2, int var3, int var4, int var5) {
      StructureBoundingBox var6 = new StructureBoundingBox(var2, var3, var4, var2, var3 + 2, var4);

      int var7;
      for(var7 = var1.nextInt(3) + 2; var7 > 0; --var7) {
         int var8 = var7 * 5;
         switch(var5) {
            case 0:
               var6.field_78893_d = var2 + 2;
               var6.field_78892_f = var4 + (var8 - 1);
               break;
            case 1:
               var6.field_78897_a = var2 - (var8 - 1);
               var6.field_78892_f = var4 + 2;
               break;
            case 2:
               var6.field_78893_d = var2 + 2;
               var6.field_78896_c = var4 - (var8 - 1);
               break;
            case 3:
               var6.field_78893_d = var2 + (var8 - 1);
               var6.field_78892_f = var4 + 2;
         }

         if (StructureComponent.func_74883_a(var0, var6) == null) {
            break;
         }
      }

      return var7 > 0 ? var6 : null;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      int var4 = this.func_74877_c();
      int var5 = var3.nextInt(4);
      switch(this.field_74885_f) {
         case 0:
            if (var5 <= 1) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78892_f + 1,
                  this.field_74885_f,
                  var4
               );
            } else if (var5 == 2) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a - 1,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78892_f - 3,
                  1,
                  var4
               );
            } else {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d + 1,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78892_f - 3,
                  3,
                  var4
               );
            }
            break;
         case 1:
            if (var5 <= 1) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a - 1,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c,
                  this.field_74885_f,
                  var4
               );
            } else if (var5 == 2) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c - 1,
                  2,
                  var4
               );
            } else {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78892_f + 1,
                  0,
                  var4
               );
            }
            break;
         case 2:
            if (var5 <= 1) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c - 1,
                  this.field_74885_f,
                  var4
               );
            } else if (var5 == 2) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78897_a - 1,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c,
                  1,
                  var4
               );
            } else {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d + 1,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c,
                  3,
                  var4
               );
            }
            break;
         case 3:
            if (var5 <= 1) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d + 1,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c,
                  this.field_74885_f,
                  var4
               );
            } else if (var5 == 2) {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d - 3,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78896_c - 1,
                  2,
                  var4
               );
            } else {
               StructureMineshaftPieces.access$000(
                  var1,
                  var2,
                  var3,
                  this.field_74887_e.field_78893_d - 3,
                  this.field_74887_e.field_78895_b - 1 + var3.nextInt(3),
                  this.field_74887_e.field_78892_f + 1,
                  0,
                  var4
               );
            }
      }

      if (var4 < 8) {
         if (this.field_74885_f != 2 && this.field_74885_f != 0) {
            for(int var8 = this.field_74887_e.field_78897_a + 3; var8 + 3 <= this.field_74887_e.field_78893_d; var8 += 5) {
               int var9 = var3.nextInt(5);
               if (var9 == 0) {
                  StructureMineshaftPieces.access$000(
                     var1, var2, var3, var8, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, var4 + 1
                  );
               } else if (var9 == 1) {
                  StructureMineshaftPieces.access$000(
                     var1, var2, var3, var8, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, var4 + 1
                  );
               }
            }
         } else {
            for(int var6 = this.field_74887_e.field_78896_c + 3; var6 + 3 <= this.field_74887_e.field_78892_f; var6 += 5) {
               int var7 = var3.nextInt(5);
               if (var7 == 0) {
                  StructureMineshaftPieces.access$000(
                     var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, var6, 1, var4 + 1
                  );
               } else if (var7 == 1) {
                  StructureMineshaftPieces.access$000(
                     var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, var6, 3, var4 + 1
                  );
               }
            }
         }
      }
   }

   @Override
   protected boolean func_74879_a(
      World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, WeightedRandomChestContent[] var7, int var8
   ) {
      int var9 = this.func_74865_a(var4, var6);
      int var10 = this.func_74862_a(var5);
      int var11 = this.func_74873_b(var4, var6);
      if (var2.func_78890_b(var9, var10, var11) && var1.func_147439_a(var9, var10, var11).func_149688_o() == Material.field_151579_a) {
         int var12 = var3.nextBoolean() ? 1 : 0;
         var1.func_147465_d(var9, var10, var11, Blocks.field_150448_aq, this.func_151555_a(Blocks.field_150448_aq, var12), 2);
         EntityMinecartChest var13 = new EntityMinecartChest(var1, (double)((float)var9 + 0.5F), (double)((float)var10 + 0.5F), (double)((float)var11 + 0.5F));
         WeightedRandomChestContent.func_76293_a(var3, var7, var13, var8);
         var1.func_72838_d(var13);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         boolean var4 = false;
         boolean var5 = true;
         boolean var6 = false;
         boolean var7 = true;
         int var8 = this.field_74955_d * 5 - 1;
         this.func_151549_a(var1, var3, 0, 0, 0, 2, 1, var8, Blocks.field_150350_a, Blocks.field_150350_a, false);
         this.func_151551_a(var1, var3, var2, 0.8F, 0, 2, 0, 2, 2, var8, Blocks.field_150350_a, Blocks.field_150350_a, false);
         if (this.field_74956_b) {
            this.func_151551_a(var1, var3, var2, 0.6F, 0, 0, 0, 2, 1, var8, Blocks.field_150321_G, Blocks.field_150350_a, false);
         }

         for(int var9 = 0; var9 < this.field_74955_d; ++var9) {
            int var10 = 2 + var9 * 5;
            this.func_151549_a(var1, var3, 0, 0, var10, 0, 1, var10, Blocks.field_150422_aJ, Blocks.field_150350_a, false);
            this.func_151549_a(var1, var3, 2, 0, var10, 2, 1, var10, Blocks.field_150422_aJ, Blocks.field_150350_a, false);
            if (var2.nextInt(4) == 0) {
               this.func_151549_a(var1, var3, 0, 2, var10, 0, 2, var10, Blocks.field_150344_f, Blocks.field_150350_a, false);
               this.func_151549_a(var1, var3, 2, 2, var10, 2, 2, var10, Blocks.field_150344_f, Blocks.field_150350_a, false);
            } else {
               this.func_151549_a(var1, var3, 0, 2, var10, 2, 2, var10, Blocks.field_150344_f, Blocks.field_150350_a, false);
            }

            this.func_151552_a(var1, var3, var2, 0.1F, 0, 2, var10 - 1, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.1F, 2, 2, var10 - 1, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.1F, 0, 2, var10 + 1, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.1F, 2, 2, var10 + 1, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.05F, 0, 2, var10 - 2, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.05F, 2, 2, var10 - 2, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.05F, 0, 2, var10 + 2, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.05F, 2, 2, var10 + 2, Blocks.field_150321_G, 0);
            this.func_151552_a(var1, var3, var2, 0.05F, 1, 2, var10 - 1, Blocks.field_150478_aa, 0);
            this.func_151552_a(var1, var3, var2, 0.05F, 1, 2, var10 + 1, Blocks.field_150478_aa, 0);
            if (var2.nextInt(100) == 0) {
               this.func_74879_a(
                  var1,
                  var3,
                  var2,
                  2,
                  0,
                  var10 - 1,
                  WeightedRandomChestContent.func_92080_a(StructureMineshaftPieces.access$100(), Items.field_151134_bR.func_92114_b(var2)),
                  3 + var2.nextInt(4)
               );
            }

            if (var2.nextInt(100) == 0) {
               this.func_74879_a(
                  var1,
                  var3,
                  var2,
                  0,
                  0,
                  var10 + 1,
                  WeightedRandomChestContent.func_92080_a(StructureMineshaftPieces.access$100(), Items.field_151134_bR.func_92114_b(var2)),
                  3 + var2.nextInt(4)
               );
            }

            if (this.field_74956_b && !this.field_74957_c) {
               int var11 = this.func_74862_a(0);
               int var12 = var10 - 1 + var2.nextInt(3);
               int var13 = this.func_74865_a(1, var12);
               var12 = this.func_74873_b(1, var12);
               if (var3.func_78890_b(var13, var11, var12)) {
                  this.field_74957_c = true;
                  var1.func_147465_d(var13, var11, var12, Blocks.field_150474_ac, 0, 2);
                  TileEntityMobSpawner var14 = (TileEntityMobSpawner)var1.func_147438_o(var13, var11, var12);
                  if (var14 != null) {
                     var14.func_145881_a().func_98272_a("CaveSpider");
                  }
               }
            }
         }

         for(int var15 = 0; var15 <= 2; ++var15) {
            for(int var17 = 0; var17 <= var8; ++var17) {
               byte var19 = -1;
               Block var21 = this.func_151548_a(var1, var15, var19, var17, var3);
               if (var21.func_149688_o() == Material.field_151579_a) {
                  byte var22 = -1;
                  this.func_151550_a(var1, Blocks.field_150344_f, 0, var15, var22, var17, var3);
               }
            }
         }

         if (this.field_74958_a) {
            for(int var16 = 0; var16 <= var8; ++var16) {
               Block var18 = this.func_151548_a(var1, 1, -1, var16, var3);
               if (var18.func_149688_o() != Material.field_151579_a && var18.func_149730_j()) {
                  this.func_151552_a(var1, var3, var2, 0.7F, 1, 0, var16, Blocks.field_150448_aq, this.func_151555_a(Blocks.field_150448_aq, 0));
               }
            }
         }

         return true;
      }
   }
}
