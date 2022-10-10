package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class WoodlandMansionPieces {
   public static void func_191153_a() {
      StructureIO.func_143031_a(WoodlandMansionPieces.MansionTemplate.class, "WMP");
   }

   public static void func_191152_a(TemplateManager var0, BlockPos var1, Rotation var2, List<WoodlandMansionPieces.MansionTemplate> var3, Random var4) {
      WoodlandMansionPieces.Grid var5 = new WoodlandMansionPieces.Grid(var4);
      WoodlandMansionPieces.Placer var6 = new WoodlandMansionPieces.Placer(var0, var4);
      var6.func_191125_a(var1, var2, var3, var5);
   }

   static class ThirdFloor extends WoodlandMansionPieces.SecondFloor {
      private ThirdFloor() {
         super(null);
      }

      // $FF: synthetic method
      ThirdFloor(Object var1) {
         this();
      }
   }

   static class SecondFloor extends WoodlandMansionPieces.RoomCollection {
      private SecondFloor() {
         super(null);
      }

      public String func_191104_a(Random var1) {
         return "1x1_b" + (var1.nextInt(4) + 1);
      }

      public String func_191099_b(Random var1) {
         return "1x1_as" + (var1.nextInt(4) + 1);
      }

      public String func_191100_a(Random var1, boolean var2) {
         return var2 ? "1x2_c_stairs" : "1x2_c" + (var1.nextInt(4) + 1);
      }

      public String func_191098_b(Random var1, boolean var2) {
         return var2 ? "1x2_d_stairs" : "1x2_d" + (var1.nextInt(5) + 1);
      }

      public String func_191102_c(Random var1) {
         return "1x2_se" + (var1.nextInt(1) + 1);
      }

      public String func_191101_d(Random var1) {
         return "2x2_b" + (var1.nextInt(5) + 1);
      }

      public String func_191103_e(Random var1) {
         return "2x2_s1";
      }

      // $FF: synthetic method
      SecondFloor(Object var1) {
         this();
      }
   }

   static class FirstFloor extends WoodlandMansionPieces.RoomCollection {
      private FirstFloor() {
         super(null);
      }

      public String func_191104_a(Random var1) {
         return "1x1_a" + (var1.nextInt(5) + 1);
      }

      public String func_191099_b(Random var1) {
         return "1x1_as" + (var1.nextInt(4) + 1);
      }

      public String func_191100_a(Random var1, boolean var2) {
         return "1x2_a" + (var1.nextInt(9) + 1);
      }

      public String func_191098_b(Random var1, boolean var2) {
         return "1x2_b" + (var1.nextInt(5) + 1);
      }

      public String func_191102_c(Random var1) {
         return "1x2_s" + (var1.nextInt(2) + 1);
      }

      public String func_191101_d(Random var1) {
         return "2x2_a" + (var1.nextInt(4) + 1);
      }

      public String func_191103_e(Random var1) {
         return "2x2_s1";
      }

      // $FF: synthetic method
      FirstFloor(Object var1) {
         this();
      }
   }

   abstract static class RoomCollection {
      private RoomCollection() {
         super();
      }

      public abstract String func_191104_a(Random var1);

      public abstract String func_191099_b(Random var1);

      public abstract String func_191100_a(Random var1, boolean var2);

      public abstract String func_191098_b(Random var1, boolean var2);

      public abstract String func_191102_c(Random var1);

      public abstract String func_191101_d(Random var1);

      public abstract String func_191103_e(Random var1);

      // $FF: synthetic method
      RoomCollection(Object var1) {
         this();
      }
   }

   static class SimpleGrid {
      private final int[][] field_191148_a;
      private final int field_191149_b;
      private final int field_191150_c;
      private final int field_191151_d;

      public SimpleGrid(int var1, int var2, int var3) {
         super();
         this.field_191149_b = var1;
         this.field_191150_c = var2;
         this.field_191151_d = var3;
         this.field_191148_a = new int[var1][var2];
      }

      public void func_191144_a(int var1, int var2, int var3) {
         if (var1 >= 0 && var1 < this.field_191149_b && var2 >= 0 && var2 < this.field_191150_c) {
            this.field_191148_a[var1][var2] = var3;
         }

      }

      public void func_191142_a(int var1, int var2, int var3, int var4, int var5) {
         for(int var6 = var2; var6 <= var4; ++var6) {
            for(int var7 = var1; var7 <= var3; ++var7) {
               this.func_191144_a(var7, var6, var5);
            }
         }

      }

      public int func_191145_a(int var1, int var2) {
         return var1 >= 0 && var1 < this.field_191149_b && var2 >= 0 && var2 < this.field_191150_c ? this.field_191148_a[var1][var2] : this.field_191151_d;
      }

      public void func_197588_a(int var1, int var2, int var3, int var4) {
         if (this.func_191145_a(var1, var2) == var3) {
            this.func_191144_a(var1, var2, var4);
         }

      }

      public boolean func_191147_b(int var1, int var2, int var3) {
         return this.func_191145_a(var1 - 1, var2) == var3 || this.func_191145_a(var1 + 1, var2) == var3 || this.func_191145_a(var1, var2 + 1) == var3 || this.func_191145_a(var1, var2 - 1) == var3;
      }
   }

   static class Grid {
      private final Random field_191117_a;
      private final WoodlandMansionPieces.SimpleGrid field_191118_b;
      private final WoodlandMansionPieces.SimpleGrid field_191119_c;
      private final WoodlandMansionPieces.SimpleGrid[] field_191120_d;
      private final int field_191121_e;
      private final int field_191122_f;

      public Grid(Random var1) {
         super();
         this.field_191117_a = var1;
         boolean var2 = true;
         this.field_191121_e = 7;
         this.field_191122_f = 4;
         this.field_191118_b = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.field_191118_b.func_191142_a(this.field_191121_e, this.field_191122_f, this.field_191121_e + 1, this.field_191122_f + 1, 3);
         this.field_191118_b.func_191142_a(this.field_191121_e - 1, this.field_191122_f, this.field_191121_e - 1, this.field_191122_f + 1, 2);
         this.field_191118_b.func_191142_a(this.field_191121_e + 2, this.field_191122_f - 2, this.field_191121_e + 3, this.field_191122_f + 3, 5);
         this.field_191118_b.func_191142_a(this.field_191121_e + 1, this.field_191122_f - 2, this.field_191121_e + 1, this.field_191122_f - 1, 1);
         this.field_191118_b.func_191142_a(this.field_191121_e + 1, this.field_191122_f + 2, this.field_191121_e + 1, this.field_191122_f + 3, 1);
         this.field_191118_b.func_191144_a(this.field_191121_e - 1, this.field_191122_f - 1, 1);
         this.field_191118_b.func_191144_a(this.field_191121_e - 1, this.field_191122_f + 2, 1);
         this.field_191118_b.func_191142_a(0, 0, 11, 1, 5);
         this.field_191118_b.func_191142_a(0, 9, 11, 11, 5);
         this.func_191110_a(this.field_191118_b, this.field_191121_e, this.field_191122_f - 2, EnumFacing.WEST, 6);
         this.func_191110_a(this.field_191118_b, this.field_191121_e, this.field_191122_f + 3, EnumFacing.WEST, 6);
         this.func_191110_a(this.field_191118_b, this.field_191121_e - 2, this.field_191122_f - 1, EnumFacing.WEST, 3);
         this.func_191110_a(this.field_191118_b, this.field_191121_e - 2, this.field_191122_f + 2, EnumFacing.WEST, 3);

         while(this.func_191111_a(this.field_191118_b)) {
         }

         this.field_191120_d = new WoodlandMansionPieces.SimpleGrid[3];
         this.field_191120_d[0] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.field_191120_d[1] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.field_191120_d[2] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.func_191116_a(this.field_191118_b, this.field_191120_d[0]);
         this.func_191116_a(this.field_191118_b, this.field_191120_d[1]);
         this.field_191120_d[0].func_191142_a(this.field_191121_e + 1, this.field_191122_f, this.field_191121_e + 1, this.field_191122_f + 1, 8388608);
         this.field_191120_d[1].func_191142_a(this.field_191121_e + 1, this.field_191122_f, this.field_191121_e + 1, this.field_191122_f + 1, 8388608);
         this.field_191119_c = new WoodlandMansionPieces.SimpleGrid(this.field_191118_b.field_191149_b, this.field_191118_b.field_191150_c, 5);
         this.func_191115_b();
         this.func_191116_a(this.field_191119_c, this.field_191120_d[2]);
      }

      public static boolean func_191109_a(WoodlandMansionPieces.SimpleGrid var0, int var1, int var2) {
         int var3 = var0.func_191145_a(var1, var2);
         return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4;
      }

      public boolean func_191114_a(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, int var4, int var5) {
         return (this.field_191120_d[var4].func_191145_a(var2, var3) & '\uffff') == var5;
      }

      @Nullable
      public EnumFacing func_191113_b(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, int var4, int var5) {
         Iterator var6 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var7;
         do {
            if (!var6.hasNext()) {
               return null;
            }

            var7 = (EnumFacing)var6.next();
         } while(!this.func_191114_a(var1, var2 + var7.func_82601_c(), var3 + var7.func_82599_e(), var4, var5));

         return var7;
      }

      private void func_191110_a(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, EnumFacing var4, int var5) {
         if (var5 > 0) {
            var1.func_191144_a(var2, var3, 1);
            var1.func_197588_a(var2 + var4.func_82601_c(), var3 + var4.func_82599_e(), 0, 1);

            EnumFacing var7;
            for(int var6 = 0; var6 < 8; ++var6) {
               var7 = EnumFacing.func_176731_b(this.field_191117_a.nextInt(4));
               if (var7 != var4.func_176734_d() && (var7 != EnumFacing.EAST || !this.field_191117_a.nextBoolean())) {
                  int var8 = var2 + var4.func_82601_c();
                  int var9 = var3 + var4.func_82599_e();
                  if (var1.func_191145_a(var8 + var7.func_82601_c(), var9 + var7.func_82599_e()) == 0 && var1.func_191145_a(var8 + var7.func_82601_c() * 2, var9 + var7.func_82599_e() * 2) == 0) {
                     this.func_191110_a(var1, var2 + var4.func_82601_c() + var7.func_82601_c(), var3 + var4.func_82599_e() + var7.func_82599_e(), var7, var5 - 1);
                     break;
                  }
               }
            }

            EnumFacing var10 = var4.func_176746_e();
            var7 = var4.func_176735_f();
            var1.func_197588_a(var2 + var10.func_82601_c(), var3 + var10.func_82599_e(), 0, 2);
            var1.func_197588_a(var2 + var7.func_82601_c(), var3 + var7.func_82599_e(), 0, 2);
            var1.func_197588_a(var2 + var4.func_82601_c() + var10.func_82601_c(), var3 + var4.func_82599_e() + var10.func_82599_e(), 0, 2);
            var1.func_197588_a(var2 + var4.func_82601_c() + var7.func_82601_c(), var3 + var4.func_82599_e() + var7.func_82599_e(), 0, 2);
            var1.func_197588_a(var2 + var4.func_82601_c() * 2, var3 + var4.func_82599_e() * 2, 0, 2);
            var1.func_197588_a(var2 + var10.func_82601_c() * 2, var3 + var10.func_82599_e() * 2, 0, 2);
            var1.func_197588_a(var2 + var7.func_82601_c() * 2, var3 + var7.func_82599_e() * 2, 0, 2);
         }
      }

      private boolean func_191111_a(WoodlandMansionPieces.SimpleGrid var1) {
         boolean var2 = false;

         for(int var3 = 0; var3 < var1.field_191150_c; ++var3) {
            for(int var4 = 0; var4 < var1.field_191149_b; ++var4) {
               if (var1.func_191145_a(var4, var3) == 0) {
                  byte var5 = 0;
                  int var7 = var5 + (func_191109_a(var1, var4 + 1, var3) ? 1 : 0);
                  var7 += func_191109_a(var1, var4 - 1, var3) ? 1 : 0;
                  var7 += func_191109_a(var1, var4, var3 + 1) ? 1 : 0;
                  var7 += func_191109_a(var1, var4, var3 - 1) ? 1 : 0;
                  if (var7 >= 3) {
                     var1.func_191144_a(var4, var3, 2);
                     var2 = true;
                  } else if (var7 == 2) {
                     byte var6 = 0;
                     int var8 = var6 + (func_191109_a(var1, var4 + 1, var3 + 1) ? 1 : 0);
                     var8 += func_191109_a(var1, var4 - 1, var3 + 1) ? 1 : 0;
                     var8 += func_191109_a(var1, var4 + 1, var3 - 1) ? 1 : 0;
                     var8 += func_191109_a(var1, var4 - 1, var3 - 1) ? 1 : 0;
                     if (var8 <= 1) {
                        var1.func_191144_a(var4, var3, 2);
                        var2 = true;
                     }
                  }
               }
            }
         }

         return var2;
      }

      private void func_191115_b() {
         ArrayList var1 = Lists.newArrayList();
         WoodlandMansionPieces.SimpleGrid var2 = this.field_191120_d[1];

         int var4;
         int var6;
         for(int var3 = 0; var3 < this.field_191119_c.field_191150_c; ++var3) {
            for(var4 = 0; var4 < this.field_191119_c.field_191149_b; ++var4) {
               int var5 = var2.func_191145_a(var4, var3);
               var6 = var5 & 983040;
               if (var6 == 131072 && (var5 & 2097152) == 2097152) {
                  var1.add(new Tuple(var4, var3));
               }
            }
         }

         if (var1.isEmpty()) {
            this.field_191119_c.func_191142_a(0, 0, this.field_191119_c.field_191149_b, this.field_191119_c.field_191150_c, 5);
         } else {
            Tuple var11 = (Tuple)var1.get(this.field_191117_a.nextInt(var1.size()));
            var4 = var2.func_191145_a((Integer)var11.func_76341_a(), (Integer)var11.func_76340_b());
            var2.func_191144_a((Integer)var11.func_76341_a(), (Integer)var11.func_76340_b(), var4 | 4194304);
            EnumFacing var12 = this.func_191113_b(this.field_191118_b, (Integer)var11.func_76341_a(), (Integer)var11.func_76340_b(), 1, var4 & '\uffff');
            var6 = (Integer)var11.func_76341_a() + var12.func_82601_c();
            int var7 = (Integer)var11.func_76340_b() + var12.func_82599_e();

            for(int var8 = 0; var8 < this.field_191119_c.field_191150_c; ++var8) {
               for(int var9 = 0; var9 < this.field_191119_c.field_191149_b; ++var9) {
                  if (!func_191109_a(this.field_191118_b, var9, var8)) {
                     this.field_191119_c.func_191144_a(var9, var8, 5);
                  } else if (var9 == (Integer)var11.func_76341_a() && var8 == (Integer)var11.func_76340_b()) {
                     this.field_191119_c.func_191144_a(var9, var8, 3);
                  } else if (var9 == var6 && var8 == var7) {
                     this.field_191119_c.func_191144_a(var9, var8, 3);
                     this.field_191120_d[2].func_191144_a(var9, var8, 8388608);
                  }
               }
            }

            ArrayList var13 = Lists.newArrayList();
            Iterator var14 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var14.hasNext()) {
               EnumFacing var10 = (EnumFacing)var14.next();
               if (this.field_191119_c.func_191145_a(var6 + var10.func_82601_c(), var7 + var10.func_82599_e()) == 0) {
                  var13.add(var10);
               }
            }

            if (var13.isEmpty()) {
               this.field_191119_c.func_191142_a(0, 0, this.field_191119_c.field_191149_b, this.field_191119_c.field_191150_c, 5);
               var2.func_191144_a((Integer)var11.func_76341_a(), (Integer)var11.func_76340_b(), var4);
            } else {
               EnumFacing var15 = (EnumFacing)var13.get(this.field_191117_a.nextInt(var13.size()));
               this.func_191110_a(this.field_191119_c, var6 + var15.func_82601_c(), var7 + var15.func_82599_e(), var15, 4);

               while(this.func_191111_a(this.field_191119_c)) {
               }

            }
         }
      }

      private void func_191116_a(WoodlandMansionPieces.SimpleGrid var1, WoodlandMansionPieces.SimpleGrid var2) {
         ArrayList var3 = Lists.newArrayList();

         int var4;
         for(var4 = 0; var4 < var1.field_191150_c; ++var4) {
            for(int var5 = 0; var5 < var1.field_191149_b; ++var5) {
               if (var1.func_191145_a(var5, var4) == 2) {
                  var3.add(new Tuple(var5, var4));
               }
            }
         }

         Collections.shuffle(var3, this.field_191117_a);
         var4 = 10;
         Iterator var19 = var3.iterator();

         while(true) {
            int var7;
            int var8;
            do {
               if (!var19.hasNext()) {
                  return;
               }

               Tuple var6 = (Tuple)var19.next();
               var7 = (Integer)var6.func_76341_a();
               var8 = (Integer)var6.func_76340_b();
            } while(var2.func_191145_a(var7, var8) != 0);

            int var9 = var7;
            int var10 = var7;
            int var11 = var8;
            int var12 = var8;
            int var13 = 65536;
            if (var2.func_191145_a(var7 + 1, var8) == 0 && var2.func_191145_a(var7, var8 + 1) == 0 && var2.func_191145_a(var7 + 1, var8 + 1) == 0 && var1.func_191145_a(var7 + 1, var8) == 2 && var1.func_191145_a(var7, var8 + 1) == 2 && var1.func_191145_a(var7 + 1, var8 + 1) == 2) {
               var10 = var7 + 1;
               var12 = var8 + 1;
               var13 = 262144;
            } else if (var2.func_191145_a(var7 - 1, var8) == 0 && var2.func_191145_a(var7, var8 + 1) == 0 && var2.func_191145_a(var7 - 1, var8 + 1) == 0 && var1.func_191145_a(var7 - 1, var8) == 2 && var1.func_191145_a(var7, var8 + 1) == 2 && var1.func_191145_a(var7 - 1, var8 + 1) == 2) {
               var9 = var7 - 1;
               var12 = var8 + 1;
               var13 = 262144;
            } else if (var2.func_191145_a(var7 - 1, var8) == 0 && var2.func_191145_a(var7, var8 - 1) == 0 && var2.func_191145_a(var7 - 1, var8 - 1) == 0 && var1.func_191145_a(var7 - 1, var8) == 2 && var1.func_191145_a(var7, var8 - 1) == 2 && var1.func_191145_a(var7 - 1, var8 - 1) == 2) {
               var9 = var7 - 1;
               var11 = var8 - 1;
               var13 = 262144;
            } else if (var2.func_191145_a(var7 + 1, var8) == 0 && var1.func_191145_a(var7 + 1, var8) == 2) {
               var10 = var7 + 1;
               var13 = 131072;
            } else if (var2.func_191145_a(var7, var8 + 1) == 0 && var1.func_191145_a(var7, var8 + 1) == 2) {
               var12 = var8 + 1;
               var13 = 131072;
            } else if (var2.func_191145_a(var7 - 1, var8) == 0 && var1.func_191145_a(var7 - 1, var8) == 2) {
               var9 = var7 - 1;
               var13 = 131072;
            } else if (var2.func_191145_a(var7, var8 - 1) == 0 && var1.func_191145_a(var7, var8 - 1) == 2) {
               var11 = var8 - 1;
               var13 = 131072;
            }

            int var14 = this.field_191117_a.nextBoolean() ? var9 : var10;
            int var15 = this.field_191117_a.nextBoolean() ? var11 : var12;
            int var16 = 2097152;
            if (!var1.func_191147_b(var14, var15, 1)) {
               var14 = var14 == var9 ? var10 : var9;
               var15 = var15 == var11 ? var12 : var11;
               if (!var1.func_191147_b(var14, var15, 1)) {
                  var15 = var15 == var11 ? var12 : var11;
                  if (!var1.func_191147_b(var14, var15, 1)) {
                     var14 = var14 == var9 ? var10 : var9;
                     var15 = var15 == var11 ? var12 : var11;
                     if (!var1.func_191147_b(var14, var15, 1)) {
                        var16 = 0;
                        var14 = var9;
                        var15 = var11;
                     }
                  }
               }
            }

            for(int var17 = var11; var17 <= var12; ++var17) {
               for(int var18 = var9; var18 <= var10; ++var18) {
                  if (var18 == var14 && var17 == var15) {
                     var2.func_191144_a(var18, var17, 1048576 | var16 | var13 | var4);
                  } else {
                     var2.func_191144_a(var18, var17, var13 | var4);
                  }
               }
            }

            ++var4;
         }
      }
   }

   static class Placer {
      private final TemplateManager field_191134_a;
      private final Random field_191135_b;
      private int field_191136_c;
      private int field_191137_d;

      public Placer(TemplateManager var1, Random var2) {
         super();
         this.field_191134_a = var1;
         this.field_191135_b = var2;
      }

      public void func_191125_a(BlockPos var1, Rotation var2, List<WoodlandMansionPieces.MansionTemplate> var3, WoodlandMansionPieces.Grid var4) {
         WoodlandMansionPieces.PlacementData var5 = new WoodlandMansionPieces.PlacementData();
         var5.field_191139_b = var1;
         var5.field_191138_a = var2;
         var5.field_191140_c = "wall_flat";
         WoodlandMansionPieces.PlacementData var6 = new WoodlandMansionPieces.PlacementData();
         this.func_191133_a(var3, var5);
         var6.field_191139_b = var5.field_191139_b.func_177981_b(8);
         var6.field_191138_a = var5.field_191138_a;
         var6.field_191140_c = "wall_window";
         if (!var3.isEmpty()) {
         }

         WoodlandMansionPieces.SimpleGrid var7 = var4.field_191118_b;
         WoodlandMansionPieces.SimpleGrid var8 = var4.field_191119_c;
         this.field_191136_c = var4.field_191121_e + 1;
         this.field_191137_d = var4.field_191122_f + 1;
         int var9 = var4.field_191121_e + 1;
         int var10 = var4.field_191122_f;
         this.func_191130_a(var3, var5, var7, EnumFacing.SOUTH, this.field_191136_c, this.field_191137_d, var9, var10);
         this.func_191130_a(var3, var6, var7, EnumFacing.SOUTH, this.field_191136_c, this.field_191137_d, var9, var10);
         WoodlandMansionPieces.PlacementData var11 = new WoodlandMansionPieces.PlacementData();
         var11.field_191139_b = var5.field_191139_b.func_177981_b(19);
         var11.field_191138_a = var5.field_191138_a;
         var11.field_191140_c = "wall_window";
         boolean var12 = false;

         int var14;
         for(int var13 = 0; var13 < var8.field_191150_c && !var12; ++var13) {
            for(var14 = var8.field_191149_b - 1; var14 >= 0 && !var12; --var14) {
               if (WoodlandMansionPieces.Grid.func_191109_a(var8, var14, var13)) {
                  var11.field_191139_b = var11.field_191139_b.func_177967_a(var2.func_185831_a(EnumFacing.SOUTH), 8 + (var13 - this.field_191137_d) * 8);
                  var11.field_191139_b = var11.field_191139_b.func_177967_a(var2.func_185831_a(EnumFacing.EAST), (var14 - this.field_191136_c) * 8);
                  this.func_191131_b(var3, var11);
                  this.func_191130_a(var3, var11, var8, EnumFacing.SOUTH, var14, var13, var14, var13);
                  var12 = true;
               }
            }
         }

         this.func_191123_a(var3, var1.func_177981_b(16), var2, var7, var8);
         this.func_191123_a(var3, var1.func_177981_b(27), var2, var8, (WoodlandMansionPieces.SimpleGrid)null);
         if (!var3.isEmpty()) {
         }

         WoodlandMansionPieces.RoomCollection[] var33 = new WoodlandMansionPieces.RoomCollection[]{new WoodlandMansionPieces.FirstFloor(), new WoodlandMansionPieces.SecondFloor(), new WoodlandMansionPieces.ThirdFloor()};

         for(var14 = 0; var14 < 3; ++var14) {
            BlockPos var15 = var1.func_177981_b(8 * var14 + (var14 == 2 ? 3 : 0));
            WoodlandMansionPieces.SimpleGrid var16 = var4.field_191120_d[var14];
            WoodlandMansionPieces.SimpleGrid var17 = var14 == 2 ? var8 : var7;
            String var18 = var14 == 0 ? "carpet_south_1" : "carpet_south_2";
            String var19 = var14 == 0 ? "carpet_west_1" : "carpet_west_2";

            for(int var20 = 0; var20 < var17.field_191150_c; ++var20) {
               for(int var21 = 0; var21 < var17.field_191149_b; ++var21) {
                  if (var17.func_191145_a(var21, var20) == 1) {
                     BlockPos var22 = var15.func_177967_a(var2.func_185831_a(EnumFacing.SOUTH), 8 + (var20 - this.field_191137_d) * 8);
                     var22 = var22.func_177967_a(var2.func_185831_a(EnumFacing.EAST), (var21 - this.field_191136_c) * 8);
                     var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "corridor_floor", var22, var2));
                     if (var17.func_191145_a(var21, var20 - 1) == 1 || (var16.func_191145_a(var21, var20 - 1) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "carpet_north", var22.func_177967_a(var2.func_185831_a(EnumFacing.EAST), 1).func_177984_a(), var2));
                     }

                     if (var17.func_191145_a(var21 + 1, var20) == 1 || (var16.func_191145_a(var21 + 1, var20) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "carpet_east", var22.func_177967_a(var2.func_185831_a(EnumFacing.SOUTH), 1).func_177967_a(var2.func_185831_a(EnumFacing.EAST), 5).func_177984_a(), var2));
                     }

                     if (var17.func_191145_a(var21, var20 + 1) == 1 || (var16.func_191145_a(var21, var20 + 1) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var18, var22.func_177967_a(var2.func_185831_a(EnumFacing.SOUTH), 5).func_177967_a(var2.func_185831_a(EnumFacing.WEST), 1), var2));
                     }

                     if (var17.func_191145_a(var21 - 1, var20) == 1 || (var16.func_191145_a(var21 - 1, var20) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var19, var22.func_177967_a(var2.func_185831_a(EnumFacing.WEST), 1).func_177967_a(var2.func_185831_a(EnumFacing.NORTH), 1), var2));
                     }
                  }
               }
            }

            String var34 = var14 == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String var35 = var14 == 0 ? "indoors_door_1" : "indoors_door_2";
            ArrayList var36 = Lists.newArrayList();

            for(int var23 = 0; var23 < var17.field_191150_c; ++var23) {
               for(int var24 = 0; var24 < var17.field_191149_b; ++var24) {
                  boolean var25 = var14 == 2 && var17.func_191145_a(var24, var23) == 3;
                  if (var17.func_191145_a(var24, var23) == 2 || var25) {
                     int var26 = var16.func_191145_a(var24, var23);
                     int var27 = var26 & 983040;
                     int var28 = var26 & '\uffff';
                     var25 = var25 && (var26 & 8388608) == 8388608;
                     var36.clear();
                     if ((var26 & 2097152) == 2097152) {
                        Iterator var29 = EnumFacing.Plane.HORIZONTAL.iterator();

                        while(var29.hasNext()) {
                           EnumFacing var30 = (EnumFacing)var29.next();
                           if (var17.func_191145_a(var24 + var30.func_82601_c(), var23 + var30.func_82599_e()) == 1) {
                              var36.add(var30);
                           }
                        }
                     }

                     EnumFacing var37 = null;
                     if (!var36.isEmpty()) {
                        var37 = (EnumFacing)var36.get(this.field_191135_b.nextInt(var36.size()));
                     } else if ((var26 & 1048576) == 1048576) {
                        var37 = EnumFacing.UP;
                     }

                     BlockPos var38 = var15.func_177967_a(var2.func_185831_a(EnumFacing.SOUTH), 8 + (var23 - this.field_191137_d) * 8);
                     var38 = var38.func_177967_a(var2.func_185831_a(EnumFacing.EAST), -1 + (var24 - this.field_191136_c) * 8);
                     if (WoodlandMansionPieces.Grid.func_191109_a(var17, var24 - 1, var23) && !var4.func_191114_a(var17, var24 - 1, var23, var14, var28)) {
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var37 == EnumFacing.WEST ? var35 : var34, var38, var2));
                     }

                     BlockPos var31;
                     if (var17.func_191145_a(var24 + 1, var23) == 1 && !var25) {
                        var31 = var38.func_177967_a(var2.func_185831_a(EnumFacing.EAST), 8);
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var37 == EnumFacing.EAST ? var35 : var34, var31, var2));
                     }

                     if (WoodlandMansionPieces.Grid.func_191109_a(var17, var24, var23 + 1) && !var4.func_191114_a(var17, var24, var23 + 1, var14, var28)) {
                        var31 = var38.func_177967_a(var2.func_185831_a(EnumFacing.SOUTH), 7);
                        var31 = var31.func_177967_a(var2.func_185831_a(EnumFacing.EAST), 7);
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var37 == EnumFacing.SOUTH ? var35 : var34, var31, var2.func_185830_a(Rotation.CLOCKWISE_90)));
                     }

                     if (var17.func_191145_a(var24, var23 - 1) == 1 && !var25) {
                        var31 = var38.func_177967_a(var2.func_185831_a(EnumFacing.NORTH), 1);
                        var31 = var31.func_177967_a(var2.func_185831_a(EnumFacing.EAST), 7);
                        var3.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var37 == EnumFacing.NORTH ? var35 : var34, var31, var2.func_185830_a(Rotation.CLOCKWISE_90)));
                     }

                     if (var27 == 65536) {
                        this.func_191129_a(var3, var38, var2, var37, var33[var14]);
                     } else {
                        EnumFacing var39;
                        if (var27 == 131072 && var37 != null) {
                           var39 = var4.func_191113_b(var17, var24, var23, var14, var28);
                           boolean var32 = (var26 & 4194304) == 4194304;
                           this.func_191132_a(var3, var38, var2, var39, var37, var33[var14], var32);
                        } else if (var27 == 262144 && var37 != null && var37 != EnumFacing.UP) {
                           var39 = var37.func_176746_e();
                           if (!var4.func_191114_a(var17, var24 + var39.func_82601_c(), var23 + var39.func_82599_e(), var14, var28)) {
                              var39 = var39.func_176734_d();
                           }

                           this.func_191127_a(var3, var38, var2, var39, var37, var33[var14]);
                        } else if (var27 == 262144 && var37 == EnumFacing.UP) {
                           this.func_191128_a(var3, var38, var2, var33[var14]);
                        }
                     }
                  }
               }
            }
         }

      }

      private void func_191130_a(List<WoodlandMansionPieces.MansionTemplate> var1, WoodlandMansionPieces.PlacementData var2, WoodlandMansionPieces.SimpleGrid var3, EnumFacing var4, int var5, int var6, int var7, int var8) {
         int var9 = var5;
         int var10 = var6;
         EnumFacing var11 = var4;

         do {
            if (!WoodlandMansionPieces.Grid.func_191109_a(var3, var9 + var4.func_82601_c(), var10 + var4.func_82599_e())) {
               this.func_191124_c(var1, var2);
               var4 = var4.func_176746_e();
               if (var9 != var7 || var10 != var8 || var11 != var4) {
                  this.func_191131_b(var1, var2);
               }
            } else if (WoodlandMansionPieces.Grid.func_191109_a(var3, var9 + var4.func_82601_c(), var10 + var4.func_82599_e()) && WoodlandMansionPieces.Grid.func_191109_a(var3, var9 + var4.func_82601_c() + var4.func_176735_f().func_82601_c(), var10 + var4.func_82599_e() + var4.func_176735_f().func_82599_e())) {
               this.func_191126_d(var1, var2);
               var9 += var4.func_82601_c();
               var10 += var4.func_82599_e();
               var4 = var4.func_176735_f();
            } else {
               var9 += var4.func_82601_c();
               var10 += var4.func_82599_e();
               if (var9 != var7 || var10 != var8 || var11 != var4) {
                  this.func_191131_b(var1, var2);
               }
            }
         } while(var9 != var7 || var10 != var8 || var11 != var4);

      }

      private void func_191123_a(List<WoodlandMansionPieces.MansionTemplate> var1, BlockPos var2, Rotation var3, WoodlandMansionPieces.SimpleGrid var4, @Nullable WoodlandMansionPieces.SimpleGrid var5) {
         int var6;
         int var7;
         BlockPos var8;
         boolean var9;
         BlockPos var10;
         for(var6 = 0; var6 < var4.field_191150_c; ++var6) {
            for(var7 = 0; var7 < var4.field_191149_b; ++var7) {
               var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 8 + (var6 - this.field_191137_d) * 8);
               var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), (var7 - this.field_191136_c) * 8);
               var9 = var5 != null && WoodlandMansionPieces.Grid.func_191109_a(var5, var7, var6);
               if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6) && !var9) {
                  var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof", var8.func_177981_b(3), var3));
                  if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 + 1, var6)) {
                     var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 6);
                     var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_front", var10, var3));
                  }

                  if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 - 1, var6)) {
                     var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 0);
                     var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 7);
                     var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_front", var10, var3.func_185830_a(Rotation.CLOCKWISE_180)));
                  }

                  if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 - 1)) {
                     var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 1);
                     var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_front", var10, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
                  }

                  if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 + 1)) {
                     var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 6);
                     var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
                     var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_front", var10, var3.func_185830_a(Rotation.CLOCKWISE_90)));
                  }
               }
            }
         }

         if (var5 != null) {
            for(var6 = 0; var6 < var4.field_191150_c; ++var6) {
               for(var7 = 0; var7 < var4.field_191149_b; ++var7) {
                  var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 8 + (var6 - this.field_191137_d) * 8);
                  var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), (var7 - this.field_191136_c) * 8);
                  var9 = WoodlandMansionPieces.Grid.func_191109_a(var5, var7, var6);
                  if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6) && var9) {
                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 + 1, var6)) {
                        var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall", var10, var3));
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 - 1, var6)) {
                        var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 1);
                        var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall", var10, var3.func_185830_a(Rotation.CLOCKWISE_180)));
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 - 1)) {
                        var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 0);
                        var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.NORTH), 1);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall", var10, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 + 1)) {
                        var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 6);
                        var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 7);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall", var10, var3.func_185830_a(Rotation.CLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 + 1, var6)) {
                        if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 - 1)) {
                           var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
                           var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.NORTH), 2);
                           var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall_corner", var10, var3));
                        }

                        if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 + 1)) {
                           var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 8);
                           var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 7);
                           var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall_corner", var10, var3.func_185830_a(Rotation.CLOCKWISE_90)));
                        }
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 - 1, var6)) {
                        if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 - 1)) {
                           var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 2);
                           var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.NORTH), 1);
                           var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall_corner", var10, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
                        }

                        if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 + 1)) {
                           var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 1);
                           var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 8);
                           var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "small_wall_corner", var10, var3.func_185830_a(Rotation.CLOCKWISE_180)));
                        }
                     }
                  }
               }
            }
         }

         for(var6 = 0; var6 < var4.field_191150_c; ++var6) {
            for(var7 = 0; var7 < var4.field_191149_b; ++var7) {
               var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 8 + (var6 - this.field_191137_d) * 8);
               var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), (var7 - this.field_191136_c) * 8);
               var9 = var5 != null && WoodlandMansionPieces.Grid.func_191109_a(var5, var7, var6);
               if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6) && !var9) {
                  BlockPos var11;
                  if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 + 1, var6)) {
                     var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 6);
                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 + 1)) {
                        var11 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_corner", var11, var3));
                     } else if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7 + 1, var6 + 1)) {
                        var11 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 5);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_inner_corner", var11, var3));
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 - 1)) {
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_corner", var10, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7 + 1, var6 - 1)) {
                        var11 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 9);
                        var11 = var11.func_177967_a(var3.func_185831_a(EnumFacing.NORTH), 2);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_inner_corner", var11, var3.func_185830_a(Rotation.CLOCKWISE_90)));
                     }
                  }

                  if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7 - 1, var6)) {
                     var10 = var8.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 0);
                     var10 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 0);
                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 + 1)) {
                        var11 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_corner", var11, var3.func_185830_a(Rotation.CLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7 - 1, var6 + 1)) {
                        var11 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 8);
                        var11 = var11.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 3);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_inner_corner", var11, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.Grid.func_191109_a(var4, var7, var6 - 1)) {
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_corner", var10, var3.func_185830_a(Rotation.CLOCKWISE_180)));
                     } else if (WoodlandMansionPieces.Grid.func_191109_a(var4, var7 - 1, var6 - 1)) {
                        var11 = var10.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 1);
                        var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "roof_inner_corner", var11, var3.func_185830_a(Rotation.CLOCKWISE_180)));
                     }
                  }
               }
            }
         }

      }

      private void func_191133_a(List<WoodlandMansionPieces.MansionTemplate> var1, WoodlandMansionPieces.PlacementData var2) {
         EnumFacing var3 = var2.field_191138_a.func_185831_a(EnumFacing.WEST);
         var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "entrance", var2.field_191139_b.func_177967_a(var3, 9), var2.field_191138_a));
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.SOUTH), 16);
      }

      private void func_191131_b(List<WoodlandMansionPieces.MansionTemplate> var1, WoodlandMansionPieces.PlacementData var2) {
         var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var2.field_191140_c, var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.EAST), 7), var2.field_191138_a));
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.SOUTH), 8);
      }

      private void func_191124_c(List<WoodlandMansionPieces.MansionTemplate> var1, WoodlandMansionPieces.PlacementData var2) {
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.SOUTH), -1);
         var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, "wall_corner", var2.field_191139_b, var2.field_191138_a));
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.SOUTH), -7);
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.WEST), -6);
         var2.field_191138_a = var2.field_191138_a.func_185830_a(Rotation.CLOCKWISE_90);
      }

      private void func_191126_d(List<WoodlandMansionPieces.MansionTemplate> var1, WoodlandMansionPieces.PlacementData var2) {
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.SOUTH), 6);
         var2.field_191139_b = var2.field_191139_b.func_177967_a(var2.field_191138_a.func_185831_a(EnumFacing.EAST), 8);
         var2.field_191138_a = var2.field_191138_a.func_185830_a(Rotation.COUNTERCLOCKWISE_90);
      }

      private void func_191129_a(List<WoodlandMansionPieces.MansionTemplate> var1, BlockPos var2, Rotation var3, EnumFacing var4, WoodlandMansionPieces.RoomCollection var5) {
         Rotation var6 = Rotation.NONE;
         String var7 = var5.func_191104_a(this.field_191135_b);
         if (var4 != EnumFacing.EAST) {
            if (var4 == EnumFacing.NORTH) {
               var6 = var6.func_185830_a(Rotation.COUNTERCLOCKWISE_90);
            } else if (var4 == EnumFacing.WEST) {
               var6 = var6.func_185830_a(Rotation.CLOCKWISE_180);
            } else if (var4 == EnumFacing.SOUTH) {
               var6 = var6.func_185830_a(Rotation.CLOCKWISE_90);
            } else {
               var7 = var5.func_191099_b(this.field_191135_b);
            }
         }

         BlockPos var8 = Template.func_191157_a(new BlockPos(1, 0, 0), Mirror.NONE, var6, 7, 7);
         var6 = var6.func_185830_a(var3);
         var8 = var8.func_190942_a(var3);
         BlockPos var9 = var2.func_177982_a(var8.func_177958_n(), 0, var8.func_177952_p());
         var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var7, var9, var6));
      }

      private void func_191132_a(List<WoodlandMansionPieces.MansionTemplate> var1, BlockPos var2, Rotation var3, EnumFacing var4, EnumFacing var5, WoodlandMansionPieces.RoomCollection var6, boolean var7) {
         BlockPos var8;
         if (var5 == EnumFacing.EAST && var4 == EnumFacing.SOUTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3));
         } else if (var5 == EnumFacing.EAST && var4 == EnumFacing.NORTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3, Mirror.LEFT_RIGHT));
         } else if (var5 == EnumFacing.WEST && var4 == EnumFacing.NORTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.CLOCKWISE_180)));
         } else if (var5 == EnumFacing.WEST && var4 == EnumFacing.SOUTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3, Mirror.FRONT_BACK));
         } else if (var5 == EnumFacing.SOUTH && var4 == EnumFacing.EAST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
         } else if (var5 == EnumFacing.SOUTH && var4 == EnumFacing.WEST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.CLOCKWISE_90)));
         } else if (var5 == EnumFacing.NORTH && var4 == EnumFacing.WEST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
         } else if (var5 == EnumFacing.NORTH && var4 == EnumFacing.EAST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191100_a(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
         } else if (var5 == EnumFacing.SOUTH && var4 == EnumFacing.NORTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.NORTH), 8);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191098_b(this.field_191135_b, var7), var8, var3));
         } else if (var5 == EnumFacing.NORTH && var4 == EnumFacing.SOUTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 7);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 14);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191098_b(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.CLOCKWISE_180)));
         } else if (var5 == EnumFacing.WEST && var4 == EnumFacing.EAST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 15);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191098_b(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.CLOCKWISE_90)));
         } else if (var5 == EnumFacing.EAST && var4 == EnumFacing.WEST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.WEST), 7);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191098_b(this.field_191135_b, var7), var8, var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90)));
         } else if (var5 == EnumFacing.UP && var4 == EnumFacing.EAST) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 15);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191102_c(this.field_191135_b), var8, var3.func_185830_a(Rotation.CLOCKWISE_90)));
         } else if (var5 == EnumFacing.UP && var4 == EnumFacing.SOUTH) {
            var8 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
            var8 = var8.func_177967_a(var3.func_185831_a(EnumFacing.NORTH), 0);
            var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191102_c(this.field_191135_b), var8, var3));
         }

      }

      private void func_191127_a(List<WoodlandMansionPieces.MansionTemplate> var1, BlockPos var2, Rotation var3, EnumFacing var4, EnumFacing var5, WoodlandMansionPieces.RoomCollection var6) {
         byte var7 = 0;
         byte var8 = 0;
         Rotation var9 = var3;
         Mirror var10 = Mirror.NONE;
         if (var5 == EnumFacing.EAST && var4 == EnumFacing.SOUTH) {
            var7 = -7;
         } else if (var5 == EnumFacing.EAST && var4 == EnumFacing.NORTH) {
            var7 = -7;
            var8 = 6;
            var10 = Mirror.LEFT_RIGHT;
         } else if (var5 == EnumFacing.NORTH && var4 == EnumFacing.EAST) {
            var7 = 1;
            var8 = 14;
            var9 = var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90);
         } else if (var5 == EnumFacing.NORTH && var4 == EnumFacing.WEST) {
            var7 = 7;
            var8 = 14;
            var9 = var3.func_185830_a(Rotation.COUNTERCLOCKWISE_90);
            var10 = Mirror.LEFT_RIGHT;
         } else if (var5 == EnumFacing.SOUTH && var4 == EnumFacing.WEST) {
            var7 = 7;
            var8 = -8;
            var9 = var3.func_185830_a(Rotation.CLOCKWISE_90);
         } else if (var5 == EnumFacing.SOUTH && var4 == EnumFacing.EAST) {
            var7 = 1;
            var8 = -8;
            var9 = var3.func_185830_a(Rotation.CLOCKWISE_90);
            var10 = Mirror.LEFT_RIGHT;
         } else if (var5 == EnumFacing.WEST && var4 == EnumFacing.NORTH) {
            var7 = 15;
            var8 = 6;
            var9 = var3.func_185830_a(Rotation.CLOCKWISE_180);
         } else if (var5 == EnumFacing.WEST && var4 == EnumFacing.SOUTH) {
            var7 = 15;
            var10 = Mirror.FRONT_BACK;
         }

         BlockPos var11 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), var7);
         var11 = var11.func_177967_a(var3.func_185831_a(EnumFacing.SOUTH), var8);
         var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var6.func_191101_d(this.field_191135_b), var11, var9, var10));
      }

      private void func_191128_a(List<WoodlandMansionPieces.MansionTemplate> var1, BlockPos var2, Rotation var3, WoodlandMansionPieces.RoomCollection var4) {
         BlockPos var5 = var2.func_177967_a(var3.func_185831_a(EnumFacing.EAST), 1);
         var1.add(new WoodlandMansionPieces.MansionTemplate(this.field_191134_a, var4.func_191103_e(this.field_191135_b), var5, var3, Mirror.NONE));
      }
   }

   static class PlacementData {
      public Rotation field_191138_a;
      public BlockPos field_191139_b;
      public String field_191140_c;

      private PlacementData() {
         super();
      }

      // $FF: synthetic method
      PlacementData(Object var1) {
         this();
      }
   }

   public static class MansionTemplate extends TemplateStructurePiece {
      private String field_191082_d;
      private Rotation field_191083_e;
      private Mirror field_191084_f;

      public MansionTemplate() {
         super();
      }

      public MansionTemplate(TemplateManager var1, String var2, BlockPos var3, Rotation var4) {
         this(var1, var2, var3, var4, Mirror.NONE);
      }

      public MansionTemplate(TemplateManager var1, String var2, BlockPos var3, Rotation var4, Mirror var5) {
         super(0);
         this.field_191082_d = var2;
         this.field_186178_c = var3;
         this.field_191083_e = var4;
         this.field_191084_f = var5;
         this.func_191081_a(var1);
      }

      private void func_191081_a(TemplateManager var1) {
         Template var2 = var1.func_200220_a(new ResourceLocation("woodland_mansion/" + this.field_191082_d));
         PlacementSettings var3 = (new PlacementSettings()).func_186222_a(true).func_186220_a(this.field_191083_e).func_186214_a(this.field_191084_f);
         this.func_186173_a(var2, this.field_186178_c, var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74778_a("Template", this.field_191082_d);
         var1.func_74778_a("Rot", this.field_186177_b.func_186215_c().name());
         var1.func_74778_a("Mi", this.field_186177_b.func_186212_b().name());
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_191082_d = var1.func_74779_i("Template");
         this.field_191083_e = Rotation.valueOf(var1.func_74779_i("Rot"));
         this.field_191084_f = Mirror.valueOf(var1.func_74779_i("Mi"));
         this.func_191081_a(var2);
      }

      protected void func_186175_a(String var1, BlockPos var2, IWorld var3, Random var4, MutableBoundingBox var5) {
         if (var1.startsWith("Chest")) {
            Rotation var6 = this.field_186177_b.func_186215_c();
            IBlockState var7 = Blocks.field_150486_ae.func_176223_P();
            if ("ChestWest".equals(var1)) {
               var7 = (IBlockState)var7.func_206870_a(BlockChest.field_176459_a, var6.func_185831_a(EnumFacing.WEST));
            } else if ("ChestEast".equals(var1)) {
               var7 = (IBlockState)var7.func_206870_a(BlockChest.field_176459_a, var6.func_185831_a(EnumFacing.EAST));
            } else if ("ChestSouth".equals(var1)) {
               var7 = (IBlockState)var7.func_206870_a(BlockChest.field_176459_a, var6.func_185831_a(EnumFacing.SOUTH));
            } else if ("ChestNorth".equals(var1)) {
               var7 = (IBlockState)var7.func_206870_a(BlockChest.field_176459_a, var6.func_185831_a(EnumFacing.NORTH));
            }

            this.func_191080_a(var3, var5, var4, var2, LootTableList.field_191192_o, var7);
         } else if ("Mage".equals(var1)) {
            EntityEvoker var8 = new EntityEvoker(var3.func_201672_e());
            var8.func_110163_bv();
            var8.func_174828_a(var2, 0.0F, 0.0F);
            var3.func_72838_d(var8);
            var3.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 2);
         } else if ("Warrior".equals(var1)) {
            EntityVindicator var9 = new EntityVindicator(var3.func_201672_e());
            var9.func_110163_bv();
            var9.func_174828_a(var2, 0.0F, 0.0F);
            var9.func_204210_a(var3.func_175649_E(new BlockPos(var9)), (IEntityLivingData)null, (NBTTagCompound)null);
            var3.func_72838_d(var9);
            var3.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 2);
         }

      }
   }
}
