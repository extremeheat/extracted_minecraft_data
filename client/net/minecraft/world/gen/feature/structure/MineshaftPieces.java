package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class MineshaftPieces {
   public static void func_143048_a() {
      StructureIO.func_143031_a(MineshaftPieces.Corridor.class, "MSCorridor");
      StructureIO.func_143031_a(MineshaftPieces.Cross.class, "MSCrossing");
      StructureIO.func_143031_a(MineshaftPieces.Room.class, "MSRoom");
      StructureIO.func_143031_a(MineshaftPieces.Stairs.class, "MSStairs");
   }

   private static MineshaftPieces.Piece func_189940_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, @Nullable EnumFacing var5, int var6, MineshaftStructure.Type var7) {
      int var8 = var1.nextInt(100);
      MutableBoundingBox var9;
      if (var8 >= 80) {
         var9 = MineshaftPieces.Cross.func_175813_a(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineshaftPieces.Cross(var6, var1, var9, var5, var7);
         }
      } else if (var8 >= 70) {
         var9 = MineshaftPieces.Stairs.func_175812_a(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineshaftPieces.Stairs(var6, var1, var9, var5, var7);
         }
      } else {
         var9 = MineshaftPieces.Corridor.func_175814_a(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineshaftPieces.Corridor(var6, var1, var9, var5, var7);
         }
      }

      return null;
   }

   private static MineshaftPieces.Piece func_189938_b(StructurePiece var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 8) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 80 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 80) {
         MineshaftStructure.Type var8 = ((MineshaftPieces.Piece)var0).field_189920_a;
         MineshaftPieces.Piece var9 = func_189940_a(var1, var2, var3, var4, var5, var6, var7 + 1, var8);
         if (var9 != null) {
            var1.add(var9);
            var9.func_74861_a(var0, var1, var2);
         }

         return var9;
      } else {
         return null;
      }
   }

   public static class Stairs extends MineshaftPieces.Piece {
      public Stairs() {
         super();
      }

      public Stairs(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4, MineshaftStructure.Type var5) {
         super(var1, var5);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public static MutableBoundingBox func_175812_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         MutableBoundingBox var6 = new MutableBoundingBox(var2, var3 - 5, var4, var2, var3 + 3 - 1, var4);
         switch(var5) {
         case NORTH:
         default:
            var6.field_78893_d = var2 + 3 - 1;
            var6.field_78896_c = var4 - 8;
            break;
         case SOUTH:
            var6.field_78893_d = var2 + 3 - 1;
            var6.field_78892_f = var4 + 8;
            break;
         case WEST:
            var6.field_78897_a = var2 - 8;
            var6.field_78892_f = var4 + 3 - 1;
            break;
         case EAST:
            var6.field_78893_d = var2 + 8;
            var6.field_78892_f = var4 + 3 - 1;
         }

         return StructurePiece.func_74883_a(var0, var6) != null ? null : var6;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.func_74877_c();
         EnumFacing var5 = this.func_186165_e();
         if (var5 != null) {
            switch(var5) {
            case NORTH:
            default:
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
               break;
            case SOUTH:
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
               break;
            case WEST:
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.WEST, var4);
               break;
            case EAST:
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.EAST, var4);
            }
         }

      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_175804_a(var1, var3, 0, 5, 0, 2, 7, 1, field_202556_l, field_202556_l, false);
            this.func_175804_a(var1, var3, 0, 0, 7, 2, 2, 8, field_202556_l, field_202556_l, false);

            for(int var5 = 0; var5 < 5; ++var5) {
               this.func_175804_a(var1, var3, 0, 5 - var5 - (var5 < 4 ? 1 : 0), 2 + var5, 2, 7 - var5, 2 + var5, field_202556_l, field_202556_l, false);
            }

            return true;
         }
      }
   }

   public static class Cross extends MineshaftPieces.Piece {
      private EnumFacing field_74953_a;
      private boolean field_74952_b;

      public Cross() {
         super();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("tf", this.field_74952_b);
         var1.func_74768_a("D", this.field_74953_a.func_176736_b());
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74952_b = var1.func_74767_n("tf");
         this.field_74953_a = EnumFacing.func_176731_b(var1.func_74762_e("D"));
      }

      public Cross(int var1, Random var2, MutableBoundingBox var3, @Nullable EnumFacing var4, MineshaftStructure.Type var5) {
         super(var1, var5);
         this.field_74953_a = var4;
         this.field_74887_e = var3;
         this.field_74952_b = var3.func_78882_c() > 3;
      }

      public static MutableBoundingBox func_175813_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         MutableBoundingBox var6 = new MutableBoundingBox(var2, var3, var4, var2, var3 + 3 - 1, var4);
         if (var1.nextInt(4) == 0) {
            var6.field_78894_e += 4;
         }

         switch(var5) {
         case NORTH:
         default:
            var6.field_78897_a = var2 - 1;
            var6.field_78893_d = var2 + 3;
            var6.field_78896_c = var4 - 4;
            break;
         case SOUTH:
            var6.field_78897_a = var2 - 1;
            var6.field_78893_d = var2 + 3;
            var6.field_78892_f = var4 + 3 + 1;
            break;
         case WEST:
            var6.field_78897_a = var2 - 4;
            var6.field_78896_c = var4 - 1;
            var6.field_78892_f = var4 + 3;
            break;
         case EAST:
            var6.field_78893_d = var2 + 3 + 1;
            var6.field_78896_c = var4 - 1;
            var6.field_78892_f = var4 + 3;
         }

         return StructurePiece.func_74883_a(var0, var6) != null ? null : var6;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.func_74877_c();
         switch(this.field_74953_a) {
         case NORTH:
         default:
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
            break;
         case SOUTH:
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
            break;
         case WEST:
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            break;
         case EAST:
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
         }

         if (this.field_74952_b) {
            if (var3.nextBoolean()) {
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            }

            if (var3.nextBoolean()) {
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, var4);
            }

            if (var3.nextBoolean()) {
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, var4);
            }

            if (var3.nextBoolean()) {
               MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            }
         }

      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            IBlockState var5 = this.func_189917_F_();
            if (this.field_74952_b) {
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b + 3 - 1, this.field_74887_e.field_78892_f, field_202556_l, field_202556_l, false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78895_b + 3 - 1, this.field_74887_e.field_78892_f - 1, field_202556_l, field_202556_l, false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 2, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, field_202556_l, field_202556_l, false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78894_e - 2, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, field_202556_l, field_202556_l, false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78892_f - 1, field_202556_l, field_202556_l, false);
            } else {
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, field_202556_l, field_202556_l, false);
               this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, field_202556_l, field_202556_l, false);
            }

            this.func_189923_b(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78894_e);
            this.func_189923_b(var1, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 1, this.field_74887_e.field_78894_e);
            this.func_189923_b(var1, var3, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78894_e);
            this.func_189923_b(var1, var3, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 1, this.field_74887_e.field_78894_e);

            for(int var6 = this.field_74887_e.field_78897_a; var6 <= this.field_74887_e.field_78893_d; ++var6) {
               for(int var7 = this.field_74887_e.field_78896_c; var7 <= this.field_74887_e.field_78892_f; ++var7) {
                  if (this.func_175807_a(var1, var6, this.field_74887_e.field_78895_b - 1, var7, var3).func_196958_f() && this.func_189916_b(var1, var6, this.field_74887_e.field_78895_b - 1, var7, var3)) {
                     this.func_175811_a(var1, var5, var6, this.field_74887_e.field_78895_b - 1, var7, var3);
                  }
               }
            }

            return true;
         }
      }

      private void func_189923_b(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6) {
         if (!this.func_175807_a(var1, var3, var6 + 1, var5, var2).func_196958_f()) {
            this.func_175804_a(var1, var2, var3, var4, var5, var3, var6, var5, this.func_189917_F_(), field_202556_l, false);
         }

      }
   }

   public static class Corridor extends MineshaftPieces.Piece {
      private boolean field_74958_a;
      private boolean field_74956_b;
      private boolean field_74957_c;
      private int field_74955_d;

      public Corridor() {
         super();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("hr", this.field_74958_a);
         var1.func_74757_a("sc", this.field_74956_b);
         var1.func_74757_a("hps", this.field_74957_c);
         var1.func_74768_a("Num", this.field_74955_d);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74958_a = var1.func_74767_n("hr");
         this.field_74956_b = var1.func_74767_n("sc");
         this.field_74957_c = var1.func_74767_n("hps");
         this.field_74955_d = var1.func_74762_e("Num");
      }

      public Corridor(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4, MineshaftStructure.Type var5) {
         super(var1, var5);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
         this.field_74958_a = var2.nextInt(3) == 0;
         this.field_74956_b = !this.field_74958_a && var2.nextInt(23) == 0;
         if (this.func_186165_e().func_176740_k() == EnumFacing.Axis.Z) {
            this.field_74955_d = var3.func_78880_d() / 5;
         } else {
            this.field_74955_d = var3.func_78883_b() / 5;
         }

      }

      public static MutableBoundingBox func_175814_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         MutableBoundingBox var6 = new MutableBoundingBox(var2, var3, var4, var2, var3 + 3 - 1, var4);

         int var7;
         for(var7 = var1.nextInt(3) + 2; var7 > 0; --var7) {
            int var8 = var7 * 5;
            switch(var5) {
            case NORTH:
            default:
               var6.field_78893_d = var2 + 3 - 1;
               var6.field_78896_c = var4 - (var8 - 1);
               break;
            case SOUTH:
               var6.field_78893_d = var2 + 3 - 1;
               var6.field_78892_f = var4 + var8 - 1;
               break;
            case WEST:
               var6.field_78897_a = var2 - (var8 - 1);
               var6.field_78892_f = var4 + 3 - 1;
               break;
            case EAST:
               var6.field_78893_d = var2 + var8 - 1;
               var6.field_78892_f = var4 + 3 - 1;
            }

            if (StructurePiece.func_74883_a(var0, var6) == null) {
               break;
            }
         }

         return var7 > 0 ? var6 : null;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.func_74877_c();
         int var5 = var3.nextInt(4);
         EnumFacing var6 = this.func_186165_e();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
            default:
               if (var5 <= 1) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c - 1, var6, var4);
               } else if (var5 == 2) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, EnumFacing.WEST, var4);
               } else {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, EnumFacing.EAST, var4);
               }
               break;
            case SOUTH:
               if (var5 <= 1) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f + 1, var6, var4);
               } else if (var5 == 2) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f - 3, EnumFacing.WEST, var4);
               } else {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f - 3, EnumFacing.EAST, var4);
               }
               break;
            case WEST:
               if (var5 <= 1) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, var6, var4);
               } else if (var5 == 2) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
               } else {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
               }
               break;
            case EAST:
               if (var5 <= 1) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c, var6, var4);
               } else if (var5 == 2) {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d - 3, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
               } else {
                  MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d - 3, this.field_74887_e.field_78895_b - 1 + var3.nextInt(3), this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
               }
            }
         }

         if (var4 < 8) {
            int var7;
            int var8;
            if (var6 != EnumFacing.NORTH && var6 != EnumFacing.SOUTH) {
               for(var7 = this.field_74887_e.field_78897_a + 3; var7 + 3 <= this.field_74887_e.field_78893_d; var7 += 5) {
                  var8 = var3.nextInt(5);
                  if (var8 == 0) {
                     MineshaftPieces.func_189938_b(var1, var2, var3, var7, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4 + 1);
                  } else if (var8 == 1) {
                     MineshaftPieces.func_189938_b(var1, var2, var3, var7, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4 + 1);
                  }
               }
            } else {
               for(var7 = this.field_74887_e.field_78896_c + 3; var7 + 3 <= this.field_74887_e.field_78892_f; var7 += 5) {
                  var8 = var3.nextInt(5);
                  if (var8 == 0) {
                     MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, var7, EnumFacing.WEST, var4 + 1);
                  } else if (var8 == 1) {
                     MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, var7, EnumFacing.EAST, var4 + 1);
                  }
               }
            }
         }

      }

      protected boolean func_186167_a(IWorld var1, MutableBoundingBox var2, Random var3, int var4, int var5, int var6, ResourceLocation var7) {
         BlockPos var8 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
         if (var2.func_175898_b(var8) && var1.func_180495_p(var8).func_196958_f() && !var1.func_180495_p(var8.func_177977_b()).func_196958_f()) {
            IBlockState var9 = (IBlockState)Blocks.field_150448_aq.func_176223_P().func_206870_a(BlockRail.field_176565_b, var3.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.func_175811_a(var1, var9, var4, var5, var6, var2);
            EntityMinecartChest var10 = new EntityMinecartChest(var1.func_201672_e(), (double)((float)var8.func_177958_n() + 0.5F), (double)((float)var8.func_177956_o() + 0.5F), (double)((float)var8.func_177952_p() + 0.5F));
            var10.func_184289_a(var7, var3.nextLong());
            var1.func_72838_d(var10);
            return true;
         } else {
            return false;
         }
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            boolean var5 = false;
            boolean var6 = true;
            boolean var7 = false;
            boolean var8 = true;
            int var9 = this.field_74955_d * 5 - 1;
            IBlockState var10 = this.func_189917_F_();
            this.func_175804_a(var1, var3, 0, 0, 0, 2, 1, var9, field_202556_l, field_202556_l, false);
            this.func_189914_a(var1, var3, var2, 0.8F, 0, 2, 0, 2, 2, var9, field_202556_l, field_202556_l, false, false);
            if (this.field_74956_b) {
               this.func_189914_a(var1, var3, var2, 0.6F, 0, 0, 0, 2, 1, var9, Blocks.field_196553_aF.func_176223_P(), field_202556_l, false, true);
            }

            int var11;
            int var12;
            for(var11 = 0; var11 < this.field_74955_d; ++var11) {
               var12 = 2 + var11 * 5;
               this.func_189921_a(var1, var3, 0, 0, var12, 2, 2, var2);
               this.func_189922_a(var1, var3, var2, 0.1F, 0, 2, var12 - 1);
               this.func_189922_a(var1, var3, var2, 0.1F, 2, 2, var12 - 1);
               this.func_189922_a(var1, var3, var2, 0.1F, 0, 2, var12 + 1);
               this.func_189922_a(var1, var3, var2, 0.1F, 2, 2, var12 + 1);
               this.func_189922_a(var1, var3, var2, 0.05F, 0, 2, var12 - 2);
               this.func_189922_a(var1, var3, var2, 0.05F, 2, 2, var12 - 2);
               this.func_189922_a(var1, var3, var2, 0.05F, 0, 2, var12 + 2);
               this.func_189922_a(var1, var3, var2, 0.05F, 2, 2, var12 + 2);
               if (var2.nextInt(100) == 0) {
                  this.func_186167_a(var1, var3, var2, 2, 0, var12 - 1, LootTableList.field_186424_f);
               }

               if (var2.nextInt(100) == 0) {
                  this.func_186167_a(var1, var3, var2, 0, 0, var12 + 1, LootTableList.field_186424_f);
               }

               if (this.field_74956_b && !this.field_74957_c) {
                  int var13 = this.func_74862_a(0);
                  int var14 = var12 - 1 + var2.nextInt(3);
                  int var15 = this.func_74865_a(1, var14);
                  int var16 = this.func_74873_b(1, var14);
                  BlockPos var17 = new BlockPos(var15, var13, var16);
                  if (var3.func_175898_b(var17) && this.func_189916_b(var1, 1, 0, var14, var3)) {
                     this.field_74957_c = true;
                     var1.func_180501_a(var17, Blocks.field_150474_ac.func_176223_P(), 2);
                     TileEntity var18 = var1.func_175625_s(var17);
                     if (var18 instanceof TileEntityMobSpawner) {
                        ((TileEntityMobSpawner)var18).func_145881_a().func_200876_a(EntityType.field_200794_h);
                     }
                  }
               }
            }

            for(var11 = 0; var11 <= 2; ++var11) {
               for(var12 = 0; var12 <= var9; ++var12) {
                  boolean var20 = true;
                  IBlockState var22 = this.func_175807_a(var1, var11, -1, var12, var3);
                  if (var22.func_196958_f() && this.func_189916_b(var1, var11, -1, var12, var3)) {
                     boolean var24 = true;
                     this.func_175811_a(var1, var10, var11, -1, var12, var3);
                  }
               }
            }

            if (this.field_74958_a) {
               IBlockState var19 = (IBlockState)Blocks.field_150448_aq.func_176223_P().func_206870_a(BlockRail.field_176565_b, RailShape.NORTH_SOUTH);

               for(var12 = 0; var12 <= var9; ++var12) {
                  IBlockState var21 = this.func_175807_a(var1, 1, -1, var12, var3);
                  if (!var21.func_196958_f() && var21.func_200015_d(var1, new BlockPos(this.func_74865_a(1, var12), this.func_74862_a(-1), this.func_74873_b(1, var12)))) {
                     float var23 = this.func_189916_b(var1, 1, 0, var12, var3) ? 0.7F : 0.9F;
                     this.func_175809_a(var1, var3, var2, var23, 1, 0, var12, var19);
                  }
               }
            }

            return true;
         }
      }

      private void func_189921_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, Random var8) {
         if (this.func_189918_a(var1, var2, var3, var7, var6, var5)) {
            IBlockState var9 = this.func_189917_F_();
            IBlockState var10 = this.func_189919_b();
            this.func_175804_a(var1, var2, var3, var4, var5, var3, var6 - 1, var5, (IBlockState)var10.func_206870_a(BlockFence.field_196414_y, true), field_202556_l, false);
            this.func_175804_a(var1, var2, var7, var4, var5, var7, var6 - 1, var5, (IBlockState)var10.func_206870_a(BlockFence.field_196411_b, true), field_202556_l, false);
            if (var8.nextInt(4) == 0) {
               this.func_175804_a(var1, var2, var3, var6, var5, var3, var6, var5, var9, field_202556_l, false);
               this.func_175804_a(var1, var2, var7, var6, var5, var7, var6, var5, var9, field_202556_l, false);
            } else {
               this.func_175804_a(var1, var2, var3, var6, var5, var7, var6, var5, var9, field_202556_l, false);
               this.func_175809_a(var1, var2, var8, 0.05F, var3 + 1, var6, var5 - 1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.NORTH));
               this.func_175809_a(var1, var2, var8, 0.05F, var3 + 1, var6, var5 + 1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.SOUTH));
            }

         }
      }

      private void func_189922_a(IWorld var1, MutableBoundingBox var2, Random var3, float var4, int var5, int var6, int var7) {
         if (this.func_189916_b(var1, var5, var6, var7, var2)) {
            this.func_175809_a(var1, var2, var3, var4, var5, var6, var7, Blocks.field_196553_aF.func_176223_P());
         }

      }
   }

   public static class Room extends MineshaftPieces.Piece {
      private final List<MutableBoundingBox> field_74949_a = Lists.newLinkedList();

      public Room() {
         super();
      }

      public Room(int var1, Random var2, int var3, int var4, MineshaftStructure.Type var5) {
         super(var1, var5);
         this.field_189920_a = var5;
         this.field_74887_e = new MutableBoundingBox(var3, 50, var4, var3 + 7 + var2.nextInt(6), 54 + var2.nextInt(6), var4 + 7 + var2.nextInt(6));
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.func_74877_c();
         int var6 = this.field_74887_e.func_78882_c() - 3 - 1;
         if (var6 <= 0) {
            var6 = 1;
         }

         int var5;
         MineshaftPieces.Piece var7;
         MutableBoundingBox var8;
         for(var5 = 0; var5 < this.field_74887_e.func_78883_b(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78883_b());
            if (var5 + 3 > this.field_74887_e.func_78883_b()) {
               break;
            }

            var7 = MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new MutableBoundingBox(var8.field_78897_a, var8.field_78895_b, this.field_74887_e.field_78896_c, var8.field_78893_d, var8.field_78894_e, this.field_74887_e.field_78896_c + 1));
            }
         }

         for(var5 = 0; var5 < this.field_74887_e.func_78883_b(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78883_b());
            if (var5 + 3 > this.field_74887_e.func_78883_b()) {
               break;
            }

            var7 = MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new MutableBoundingBox(var8.field_78897_a, var8.field_78895_b, this.field_74887_e.field_78892_f - 1, var8.field_78893_d, var8.field_78894_e, this.field_74887_e.field_78892_f));
            }
         }

         for(var5 = 0; var5 < this.field_74887_e.func_78880_d(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78880_d());
            if (var5 + 3 > this.field_74887_e.func_78880_d()) {
               break;
            }

            var7 = MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new MutableBoundingBox(this.field_74887_e.field_78897_a, var8.field_78895_b, var8.field_78896_c, this.field_74887_e.field_78897_a + 1, var8.field_78894_e, var8.field_78892_f));
            }
         }

         for(var5 = 0; var5 < this.field_74887_e.func_78880_d(); var5 += 4) {
            var5 += var3.nextInt(this.field_74887_e.func_78880_d());
            if (var5 + 3 > this.field_74887_e.func_78880_d()) {
               break;
            }

            var7 = MineshaftPieces.func_189938_b(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, var4);
            if (var7 != null) {
               var8 = var7.func_74874_b();
               this.field_74949_a.add(new MutableBoundingBox(this.field_74887_e.field_78893_d - 1, var8.field_78895_b, var8.field_78896_c, this.field_74887_e.field_78893_d, var8.field_78894_e, var8.field_78892_f));
            }
         }

      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f, Blocks.field_150346_d.func_176223_P(), field_202556_l, true);
            this.func_175804_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b + 1, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, Math.min(this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78894_e), this.field_74887_e.field_78892_f, field_202556_l, field_202556_l, false);
            Iterator var5 = this.field_74949_a.iterator();

            while(var5.hasNext()) {
               MutableBoundingBox var6 = (MutableBoundingBox)var5.next();
               this.func_175804_a(var1, var3, var6.field_78897_a, var6.field_78894_e - 2, var6.field_78896_c, var6.field_78893_d, var6.field_78894_e, var6.field_78892_f, field_202556_l, field_202556_l, false);
            }

            this.func_180777_a(var1, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b + 4, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, field_202556_l, false);
            return true;
         }
      }

      public void func_181138_a(int var1, int var2, int var3) {
         super.func_181138_a(var1, var2, var3);
         Iterator var4 = this.field_74949_a.iterator();

         while(var4.hasNext()) {
            MutableBoundingBox var5 = (MutableBoundingBox)var4.next();
            var5.func_78886_a(var1, var2, var3);
         }

      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         NBTTagList var2 = new NBTTagList();
         Iterator var3 = this.field_74949_a.iterator();

         while(var3.hasNext()) {
            MutableBoundingBox var4 = (MutableBoundingBox)var3.next();
            var2.add((INBTBase)var4.func_151535_h());
         }

         var1.func_74782_a("Entrances", var2);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         NBTTagList var3 = var1.func_150295_c("Entrances", 11);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            this.field_74949_a.add(new MutableBoundingBox(var3.func_150306_c(var4)));
         }

      }
   }

   abstract static class Piece extends StructurePiece {
      protected MineshaftStructure.Type field_189920_a;

      public Piece() {
         super();
      }

      public Piece(int var1, MineshaftStructure.Type var2) {
         super(var1);
         this.field_189920_a = var2;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74768_a("MST", this.field_189920_a.ordinal());
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         this.field_189920_a = MineshaftStructure.Type.func_189910_a(var1.func_74762_e("MST"));
      }

      protected IBlockState func_189917_F_() {
         switch(this.field_189920_a) {
         case NORMAL:
         default:
            return Blocks.field_196662_n.func_176223_P();
         case MESA:
            return Blocks.field_196672_s.func_176223_P();
         }
      }

      protected IBlockState func_189919_b() {
         switch(this.field_189920_a) {
         case NORMAL:
         default:
            return Blocks.field_180407_aO.func_176223_P();
         case MESA:
            return Blocks.field_180406_aS.func_176223_P();
         }
      }

      protected boolean func_189918_a(IBlockReader var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6) {
         for(int var7 = var3; var7 <= var4; ++var7) {
            if (this.func_175807_a(var1, var7, var5 + 1, var6, var2).func_196958_f()) {
               return false;
            }
         }

         return true;
      }
   }
}
