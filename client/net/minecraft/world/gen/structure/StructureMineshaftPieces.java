package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces {
   private static final List<WeightedRandomChestContent> field_175893_a;

   public static void func_143048_a() {
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces.Corridor.class, "MSCorridor");
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces.Cross.class, "MSCrossing");
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces.Room.class, "MSRoom");
      MapGenStructureIO.func_143031_a(StructureMineshaftPieces.Stairs.class, "MSStairs");
   }

   private static StructureComponent func_175892_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
      int var7 = var1.nextInt(100);
      StructureBoundingBox var8;
      if (var7 >= 80) {
         var8 = StructureMineshaftPieces.Cross.func_175813_a(var0, var1, var2, var3, var4, var5);
         if (var8 != null) {
            return new StructureMineshaftPieces.Cross(var6, var1, var8, var5);
         }
      } else if (var7 >= 70) {
         var8 = StructureMineshaftPieces.Stairs.func_175812_a(var0, var1, var2, var3, var4, var5);
         if (var8 != null) {
            return new StructureMineshaftPieces.Stairs(var6, var1, var8, var5);
         }
      } else {
         var8 = StructureMineshaftPieces.Corridor.func_175814_a(var0, var1, var2, var3, var4, var5);
         if (var8 != null) {
            return new StructureMineshaftPieces.Corridor(var6, var1, var8, var5);
         }
      }

      return null;
   }

   private static StructureComponent func_175890_b(StructureComponent var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 8) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 80 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 80) {
         StructureComponent var8 = func_175892_a(var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var8.func_74861_a(var0, var1, var2);
         }

         return var8;
      } else {
         return null;
      }
   }

   static {
      field_175893_a = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5), new WeightedRandomChestContent(Items.field_151100_aR, EnumDyeColor.BLUE.func_176767_b(), 4, 9, 5), new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 2, 3), new WeightedRandomChestContent(Items.field_151044_h, 0, 3, 8, 10), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 1), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150448_aq), 0, 4, 8, 1), new WeightedRandomChestContent(Items.field_151081_bc, 0, 2, 4, 10), new WeightedRandomChestContent(Items.field_151080_bb, 0, 2, 4, 10), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1)});
   }

   public static class Stairs extends StructureComponent {
      public Stairs() {
         super();
      }

      public Stairs(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
      }

      protected void func_143011_b(NBTTagCompound var1) {
      }

      public static StructureBoundingBox func_175812_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var6 = new StructureBoundingBox(var2, var3 - 5, var4, var2, var3 + 2, var4);
         switch(var5) {
         case NORTH:
            var6.field_78893_d = var2 + 2;
            var6.field_78896_c = var4 - 8;
            break;
         case SOUTH:
            var6.field_78893_d = var2 + 2;
            var6.field_78892_f = var4 + 8;
            break;
         case WEST:
            var6.field_78897_a = var2 - 8;
            var6.field_78892_f = var4 + 2;
            break;
         case EAST:
            var6.field_78893_d = var2 + 8;
            var6.field_78892_f = var4 + 2;
         }

         return StructureComponent.func_74883_a(var0, var6) != null ? null : var6;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var4 = this.func_74877_c();
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
               break;
            case SOUTH:
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
               break;
            case WEST:
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.WEST, var4);
               break;
            case EAST:
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.EAST, var4);
            }
         }

      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_175804_a(var1, var3, 0, 5, 0, 2, 7, 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175804_a(var1, var3, 0, 0, 7, 2, 2, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);

            for(int var4 = 0; var4 < 5; ++var4) {
               this.func_175804_a(var1, var3, 0, 5 - var4 - (var4 < 4 ? 1 : 0), 2 + var4, 2, 7 - var4, 2 + var4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            return true;
         }
      }
   }

   public static class Cross extends StructureComponent {
      private EnumFacing field_74953_a;
      private boolean field_74952_b;

      public Cross() {
         super();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74757_a("tf", this.field_74952_b);
         var1.func_74768_a("D", this.field_74953_a.func_176736_b());
      }

      protected void func_143011_b(NBTTagCompound var1) {
         this.field_74952_b = var1.func_74767_n("tf");
         this.field_74953_a = EnumFacing.func_176731_b(var1.func_74762_e("D"));
      }

      public Cross(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74953_a = var4;
         this.field_74887_e = var3;
         this.field_74952_b = var3.func_78882_c() > 3;
      }

      public static StructureBoundingBox func_175813_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var6 = new StructureBoundingBox(var2, var3, var4, var2, var3 + 2, var4);
         if (var1.nextInt(4) == 0) {
            var6.field_78894_e += 4;
         }

         switch(var5) {
         case NORTH:
            var6.field_78897_a = var2 - 1;
            var6.field_78893_d = var2 + 3;
            var6.field_78896_c = var4 - 4;
            break;
         case SOUTH:
            var6.field_78897_a = var2 - 1;
            var6.field_78893_d = var2 + 3;
            var6.field_78892_f = var4 + 4;
            break;
         case WEST:
            var6.field_78897_a = var2 - 4;
            var6.field_78896_c = var4 - 1;
            var6.field_78892_f = var4 + 3;
            break;
         case EAST:
            var6.field_78893_d = var2 + 4;
            var6.field_78896_c = var4 - 1;
            var6.field_78892_f = var4 + 3;
         }

         return StructureComponent.func_74883_a(var0, var6) != null ? null : var6;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var4 = this.func_74877_c();
         switch(this.field_74953_a) {
         case NORTH:
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
            break;
         case SOUTH:
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
            break;
         case WEST:
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            break;
         case EAST:
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
         }

         if (this.field_74952_b) {
            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            }

            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            }

            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
            }

            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            }
         }

      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            if (this.field_74952_b) {
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b + 3 - 1, this.field_74887_e.field_78892_f, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78895_b + 3 - 1, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 2, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78894_e - 2, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            } else {
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78896_c + 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 1, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175804_a(var1, var3, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78896_c + 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175804_a(var1, var3, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);

            for(int var4 = this.field_74887_e.field_78897_a; var4 <= this.field_74887_e.field_78893_d; ++var4) {
               for(int var5 = this.field_74887_e.field_78896_c; var5 <= this.field_74887_e.field_78892_f; ++var5) {
                  if (this.func_175807_a(var1, var4, this.field_74887_e.field_78895_b - 1, var5, var3).func_177230_c().func_149688_o() == Material.field_151579_a) {
                     this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), var4, this.field_74887_e.field_78895_b - 1, var5, var3);
                  }
               }
            }

            return true;
         }
      }
   }

   public static class Corridor extends StructureComponent {
      private boolean field_74958_a;
      private boolean field_74956_b;
      private boolean field_74957_c;
      private int field_74955_d;

      public Corridor() {
         super();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74757_a("hr", this.field_74958_a);
         var1.func_74757_a("sc", this.field_74956_b);
         var1.func_74757_a("hps", this.field_74957_c);
         var1.func_74768_a("Num", this.field_74955_d);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         this.field_74958_a = var1.func_74767_n("hr");
         this.field_74956_b = var1.func_74767_n("sc");
         this.field_74957_c = var1.func_74767_n("hps");
         this.field_74955_d = var1.func_74762_e("Num");
      }

      public Corridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
         this.field_74958_a = var2.nextInt(3) == 0;
         this.field_74956_b = !this.field_74958_a && var2.nextInt(23) == 0;
         if (this.field_74885_f != EnumFacing.NORTH && this.field_74885_f != EnumFacing.SOUTH) {
            this.field_74955_d = var3.func_78883_b() / 5;
         } else {
            this.field_74955_d = var3.func_78880_d() / 5;
         }

      }

      public static StructureBoundingBox func_175814_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var6 = new StructureBoundingBox(var2, var3, var4, var2, var3 + 2, var4);

         int var7;
         for(var7 = var1.nextInt(3) + 2; var7 > 0; --var7) {
            int var8 = var7 * 5;
            switch(var5) {
            case NORTH:
               var6.field_78893_d = var2 + 2;
               var6.field_78896_c = var4 - (var8 - 1);
               break;
            case SOUTH:
               var6.field_78893_d = var2 + 2;
               var6.field_78892_f = var4 + (var8 - 1);
               break;
            case WEST:
               var6.field_78897_a = var2 - (var8 - 1);
               var6.field_78892_f = var4 + 2;
               break;
            case EAST:
               var6.field_78893_d = var2 + (var8 - 1);
               var6.field_78892_f = var4 + 2;
            }

            if (StructureComponent.func_74883_a(var0, var6) == null) {
               break;
            }
         }

         return var7 > 0 ? var6 : null;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var4 = this.func_74877_c();
         int var5 = var3.nextInt(4);
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               if (var5 <= 1) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c - 1, this.field_74885_f, var4);
               } else if (var5 == 2) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, EnumFacing.WEST, var4);
               } else {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, EnumFacing.EAST, var4);
               }
               break;
            case SOUTH:
               if (var5 <= 1) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f + 1, this.field_74885_f, var4);
               } else if (var5 == 2) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f - 3, EnumFacing.WEST, var4);
               } else {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f - 3, EnumFacing.EAST, var4);
               }
               break;
            case WEST:
               if (var5 <= 1) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, this.field_74885_f, var4);
               } else if (var5 == 2) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
               } else {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
               }
               break;
            case EAST:
               if (var5 <= 1) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, this.field_74885_f, var4);
               } else if (var5 == 2) {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d - 3, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
               } else {
                  StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d - 3, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
               }
            }
         }

         if (var4 < 8) {
            int var6;
            int var7;
            if (this.field_74885_f != EnumFacing.NORTH && this.field_74885_f != EnumFacing.SOUTH) {
               for(var6 = this.field_74887_e.field_78897_a + 3; var6 + 3 <= this.field_74887_e.field_78893_d; var6 += 5) {
                  var7 = var3.nextInt(5);
                  if (var7 == 0) {
                     StructureMineshaftPieces.func_175890_b(var1, var2, var3, var6, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4 + 1);
                  } else if (var7 == 1) {
                     StructureMineshaftPieces.func_175890_b(var1, var2, var3, var6, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4 + 1);
                  }
               }
            } else {
               for(var6 = this.field_74887_e.field_78896_c + 3; var6 + 3 <= this.field_74887_e.field_78892_f; var6 += 5) {
                  var7 = var3.nextInt(5);
                  if (var7 == 0) {
                     StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, var6, EnumFacing.WEST, var4 + 1);
                  } else if (var7 == 1) {
                     StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, var6, EnumFacing.EAST, var4 + 1);
                  }
               }
            }
         }

      }

      protected boolean func_180778_a(World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, List<WeightedRandomChestContent> var7, int var8) {
         BlockPos var9 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
         if (var2.func_175898_b(var9) && var1.func_180495_p(var9).func_177230_c().func_149688_o() == Material.field_151579_a) {
            int var10 = var3.nextBoolean() ? 1 : 0;
            var1.func_180501_a(var9, Blocks.field_150448_aq.func_176203_a(this.func_151555_a(Blocks.field_150448_aq, var10)), 2);
            EntityMinecartChest var11 = new EntityMinecartChest(var1, (double)((float)var9.func_177958_n() + 0.5F), (double)((float)var9.func_177956_o() + 0.5F), (double)((float)var9.func_177952_p() + 0.5F));
            WeightedRandomChestContent.func_177630_a(var3, var7, var11, var8);
            var1.func_72838_d(var11);
            return true;
         } else {
            return false;
         }
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            boolean var4 = false;
            boolean var5 = true;
            boolean var6 = false;
            boolean var7 = true;
            int var8 = this.field_74955_d * 5 - 1;
            this.func_175804_a(var1, var3, 0, 0, 0, 2, 1, var8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175805_a(var1, var3, var2, 0.8F, 0, 2, 0, 2, 2, var8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            if (this.field_74956_b) {
               this.func_175805_a(var1, var3, var2, 0.6F, 0, 0, 0, 2, 1, var8, Blocks.field_150321_G.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            int var9;
            int var10;
            for(var9 = 0; var9 < this.field_74955_d; ++var9) {
               var10 = 2 + var9 * 5;
               this.func_175804_a(var1, var3, 0, 0, var10, 0, 1, var10, Blocks.field_180407_aO.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               this.func_175804_a(var1, var3, 2, 0, var10, 2, 1, var10, Blocks.field_180407_aO.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               if (var2.nextInt(4) == 0) {
                  this.func_175804_a(var1, var3, 0, 2, var10, 0, 2, var10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
                  this.func_175804_a(var1, var3, 2, 2, var10, 2, 2, var10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               } else {
                  this.func_175804_a(var1, var3, 0, 2, var10, 2, 2, var10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
               }

               this.func_175809_a(var1, var3, var2, 0.1F, 0, 2, var10 - 1, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.1F, 2, 2, var10 - 1, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.1F, 0, 2, var10 + 1, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.1F, 2, 2, var10 + 1, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.05F, 0, 2, var10 - 2, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.05F, 2, 2, var10 - 2, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.05F, 0, 2, var10 + 2, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.05F, 2, 2, var10 + 2, Blocks.field_150321_G.func_176223_P());
               this.func_175809_a(var1, var3, var2, 0.05F, 1, 2, var10 - 1, Blocks.field_150478_aa.func_176203_a(EnumFacing.UP.func_176745_a()));
               this.func_175809_a(var1, var3, var2, 0.05F, 1, 2, var10 + 1, Blocks.field_150478_aa.func_176203_a(EnumFacing.UP.func_176745_a()));
               if (var2.nextInt(100) == 0) {
                  this.func_180778_a(var1, var3, var2, 2, 0, var10 - 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.field_175893_a, Items.field_151134_bR.func_92114_b(var2)), 3 + var2.nextInt(4));
               }

               if (var2.nextInt(100) == 0) {
                  this.func_180778_a(var1, var3, var2, 0, 0, var10 + 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.field_175893_a, Items.field_151134_bR.func_92114_b(var2)), 3 + var2.nextInt(4));
               }

               if (this.field_74956_b && !this.field_74957_c) {
                  int var11 = this.func_74862_a(0);
                  int var12 = var10 - 1 + var2.nextInt(3);
                  int var13 = this.func_74865_a(1, var12);
                  var12 = this.func_74873_b(1, var12);
                  BlockPos var14 = new BlockPos(var13, var11, var12);
                  if (var3.func_175898_b(var14)) {
                     this.field_74957_c = true;
                     var1.func_180501_a(var14, Blocks.field_150474_ac.func_176223_P(), 2);
                     TileEntity var15 = var1.func_175625_s(var14);
                     if (var15 instanceof TileEntityMobSpawner) {
                        ((TileEntityMobSpawner)var15).func_145881_a().func_98272_a("CaveSpider");
                     }
                  }
               }
            }

            for(var9 = 0; var9 <= 2; ++var9) {
               for(var10 = 0; var10 <= var8; ++var10) {
                  byte var17 = -1;
                  IBlockState var18 = this.func_175807_a(var1, var9, var17, var10, var3);
                  if (var18.func_177230_c().func_149688_o() == Material.field_151579_a) {
                     byte var19 = -1;
                     this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), var9, var19, var10, var3);
                  }
               }
            }

            if (this.field_74958_a) {
               for(var9 = 0; var9 <= var8; ++var9) {
                  IBlockState var16 = this.func_175807_a(var1, 1, -1, var9, var3);
                  if (var16.func_177230_c().func_149688_o() != Material.field_151579_a && var16.func_177230_c().func_149730_j()) {
                     this.func_175809_a(var1, var3, var2, 0.7F, 1, 0, var9, Blocks.field_150448_aq.func_176203_a(this.func_151555_a(Blocks.field_150448_aq, 0)));
                  }
               }
            }

            return true;
         }
      }
   }

   public static class Room extends StructureComponent {
      private List<StructureBoundingBox> field_74949_a = Lists.newLinkedList();

      public Room() {
         super();
      }

      public Room(int var1, Random var2, int var3, int var4) {
         super(var1);
         this.field_74887_e = new StructureBoundingBox(var3, 50, var4, var3 + 7 + var2.nextInt(6), 54 + var2.nextInt(6), var4 + 7 + var2.nextInt(6));
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var4 = this.func_74877_c();
         int var6 = this.field_74887_e.func_78882_c() - 3 - 1;
         if (var6 <= 0) {
            var6 = 1;
         }

         int var5;
         StructureComponent var7;
         StructureBoundingBox var8;
         for(var5 = 0; var5 < this.field_74887_e.func_78883_b(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78883_b());
            if (var5 + 3 > this.field_74887_e.func_78883_b()) {
               break;
            }

            var7 = StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new StructureBoundingBox(var8.field_78897_a, var8.field_78895_b, this.field_74887_e.field_78896_c, var8.field_78893_d, var8.field_78894_e, this.field_74887_e.field_78896_c + 1));
            }
         }

         for(var5 = 0; var5 < this.field_74887_e.func_78883_b(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78883_b());
            if (var5 + 3 > this.field_74887_e.func_78883_b()) {
               break;
            }

            var7 = StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new StructureBoundingBox(var8.field_78897_a, var8.field_78895_b, this.field_74887_e.field_78892_f - 1, var8.field_78893_d, var8.field_78894_e, this.field_74887_e.field_78892_f));
            }
         }

         for(var5 = 0; var5 < this.field_74887_e.func_78880_d(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78880_d());
            if (var5 + 3 > this.field_74887_e.func_78880_d()) {
               break;
            }

            var7 = StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new StructureBoundingBox(this.field_74887_e.field_78897_a, var8.field_78895_b, var8.field_78896_c, this.field_74887_e.field_78897_a + 1, var8.field_78894_e, var8.field_78892_f));
            }
         }

         for(var5 = 0; var5 < this.field_74887_e.func_78880_d(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78880_d());
            if (var5 + 3 > this.field_74887_e.func_78880_d()) {
               break;
            }

            var7 = StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new StructureBoundingBox(this.field_74887_e.field_78893_d - 1, var8.field_78895_b, var8.field_78896_c, this.field_74887_e.field_78893_d, var8.field_78894_e, var8.field_78892_f));
            }
         }

      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f, Blocks.field_150346_d.func_176223_P(), Blocks.field_150350_a.func_176223_P(), true);
            this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b + 1, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, Math.min(this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78894_e), this.field_74887_e.field_78892_f, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            Iterator var4 = this.field_74949_a.iterator();

            while(var4.hasNext()) {
               StructureBoundingBox var5 = (StructureBoundingBox)var4.next();
               this.func_175804_a(var1, var3, var5.field_78897_a, var5.field_78894_e - 2, var5.field_78896_c, var5.field_78893_d, var5.field_78894_e, var5.field_78892_f, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            this.func_180777_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b + 4, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, Blocks.field_150350_a.func_176223_P(), false);
            return true;
         }
      }

      public void func_181138_a(int var1, int var2, int var3) {
         super.func_181138_a(var1, var2, var3);
         Iterator var4 = this.field_74949_a.iterator();

         while(var4.hasNext()) {
            StructureBoundingBox var5 = (StructureBoundingBox)var4.next();
            var5.func_78886_a(var1, var2, var3);
         }

      }

      protected void func_143012_a(NBTTagCompound var1) {
         NBTTagList var2 = new NBTTagList();
         Iterator var3 = this.field_74949_a.iterator();

         while(var3.hasNext()) {
            StructureBoundingBox var4 = (StructureBoundingBox)var3.next();
            var2.func_74742_a(var4.func_151535_h());
         }

         var1.func_74782_a("Entrances", var2);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         NBTTagList var2 = var1.func_150295_c("Entrances", 11);

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            this.field_74949_a.add(new StructureBoundingBox(var2.func_150306_c(var3)));
         }

      }
   }
}
