package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class StructureComponent {
   protected StructureBoundingBox field_74887_e;
   protected EnumFacing field_74885_f;
   protected int field_74886_g;

   public StructureComponent() {
      super();
   }

   protected StructureComponent(int var1) {
      super();
      this.field_74886_g = var1;
   }

   public NBTTagCompound func_143010_b() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("id", MapGenStructureIO.func_143036_a(this));
      var1.func_74782_a("BB", this.field_74887_e.func_151535_h());
      var1.func_74768_a("O", this.field_74885_f == null ? -1 : this.field_74885_f.func_176736_b());
      var1.func_74768_a("GD", this.field_74886_g);
      this.func_143012_a(var1);
      return var1;
   }

   protected abstract void func_143012_a(NBTTagCompound var1);

   public void func_143009_a(World var1, NBTTagCompound var2) {
      if (var2.func_74764_b("BB")) {
         this.field_74887_e = new StructureBoundingBox(var2.func_74759_k("BB"));
      }

      int var3 = var2.func_74762_e("O");
      this.field_74885_f = var3 == -1 ? null : EnumFacing.func_176731_b(var3);
      this.field_74886_g = var2.func_74762_e("GD");
      this.func_143011_b(var2);
   }

   protected abstract void func_143011_b(NBTTagCompound var1);

   public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
   }

   public abstract boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3);

   public StructureBoundingBox func_74874_b() {
      return this.field_74887_e;
   }

   public int func_74877_c() {
      return this.field_74886_g;
   }

   public static StructureComponent func_74883_a(List<StructureComponent> var0, StructureBoundingBox var1) {
      Iterator var2 = var0.iterator();

      StructureComponent var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (StructureComponent)var2.next();
      } while(var3.func_74874_b() == null || !var3.func_74874_b().func_78884_a(var1));

      return var3;
   }

   public BlockPos func_180776_a() {
      return new BlockPos(this.field_74887_e.func_180717_f());
   }

   protected boolean func_74860_a(World var1, StructureBoundingBox var2) {
      int var3 = Math.max(this.field_74887_e.field_78897_a - 1, var2.field_78897_a);
      int var4 = Math.max(this.field_74887_e.field_78895_b - 1, var2.field_78895_b);
      int var5 = Math.max(this.field_74887_e.field_78896_c - 1, var2.field_78896_c);
      int var6 = Math.min(this.field_74887_e.field_78893_d + 1, var2.field_78893_d);
      int var7 = Math.min(this.field_74887_e.field_78894_e + 1, var2.field_78894_e);
      int var8 = Math.min(this.field_74887_e.field_78892_f + 1, var2.field_78892_f);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      int var10;
      int var11;
      for(var10 = var3; var10 <= var6; ++var10) {
         for(var11 = var5; var11 <= var8; ++var11) {
            if (var1.func_180495_p(var9.func_181079_c(var10, var4, var11)).func_177230_c().func_149688_o().func_76224_d()) {
               return true;
            }

            if (var1.func_180495_p(var9.func_181079_c(var10, var7, var11)).func_177230_c().func_149688_o().func_76224_d()) {
               return true;
            }
         }
      }

      for(var10 = var3; var10 <= var6; ++var10) {
         for(var11 = var4; var11 <= var7; ++var11) {
            if (var1.func_180495_p(var9.func_181079_c(var10, var11, var5)).func_177230_c().func_149688_o().func_76224_d()) {
               return true;
            }

            if (var1.func_180495_p(var9.func_181079_c(var10, var11, var8)).func_177230_c().func_149688_o().func_76224_d()) {
               return true;
            }
         }
      }

      for(var10 = var5; var10 <= var8; ++var10) {
         for(var11 = var4; var11 <= var7; ++var11) {
            if (var1.func_180495_p(var9.func_181079_c(var3, var11, var10)).func_177230_c().func_149688_o().func_76224_d()) {
               return true;
            }

            if (var1.func_180495_p(var9.func_181079_c(var6, var11, var10)).func_177230_c().func_149688_o().func_76224_d()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int func_74865_a(int var1, int var2) {
      if (this.field_74885_f == null) {
         return var1;
      } else {
         switch(this.field_74885_f) {
         case NORTH:
         case SOUTH:
            return this.field_74887_e.field_78897_a + var1;
         case WEST:
            return this.field_74887_e.field_78893_d - var2;
         case EAST:
            return this.field_74887_e.field_78897_a + var2;
         default:
            return var1;
         }
      }
   }

   protected int func_74862_a(int var1) {
      return this.field_74885_f == null ? var1 : var1 + this.field_74887_e.field_78895_b;
   }

   protected int func_74873_b(int var1, int var2) {
      if (this.field_74885_f == null) {
         return var2;
      } else {
         switch(this.field_74885_f) {
         case NORTH:
            return this.field_74887_e.field_78892_f - var2;
         case SOUTH:
            return this.field_74887_e.field_78896_c + var2;
         case WEST:
         case EAST:
            return this.field_74887_e.field_78896_c + var1;
         default:
            return var2;
         }
      }
   }

   protected int func_151555_a(Block var1, int var2) {
      if (var1 == Blocks.field_150448_aq) {
         if (this.field_74885_f == EnumFacing.WEST || this.field_74885_f == EnumFacing.EAST) {
            if (var2 == 1) {
               return 0;
            }

            return 1;
         }
      } else if (var1 instanceof BlockDoor) {
         if (this.field_74885_f == EnumFacing.SOUTH) {
            if (var2 == 0) {
               return 2;
            }

            if (var2 == 2) {
               return 0;
            }
         } else {
            if (this.field_74885_f == EnumFacing.WEST) {
               return var2 + 1 & 3;
            }

            if (this.field_74885_f == EnumFacing.EAST) {
               return var2 + 3 & 3;
            }
         }
      } else if (var1 != Blocks.field_150446_ar && var1 != Blocks.field_150476_ad && var1 != Blocks.field_150387_bl && var1 != Blocks.field_150390_bg && var1 != Blocks.field_150372_bz) {
         if (var1 == Blocks.field_150468_ap) {
            if (this.field_74885_f == EnumFacing.SOUTH) {
               if (var2 == EnumFacing.NORTH.func_176745_a()) {
                  return EnumFacing.SOUTH.func_176745_a();
               }

               if (var2 == EnumFacing.SOUTH.func_176745_a()) {
                  return EnumFacing.NORTH.func_176745_a();
               }
            } else if (this.field_74885_f == EnumFacing.WEST) {
               if (var2 == EnumFacing.NORTH.func_176745_a()) {
                  return EnumFacing.WEST.func_176745_a();
               }

               if (var2 == EnumFacing.SOUTH.func_176745_a()) {
                  return EnumFacing.EAST.func_176745_a();
               }

               if (var2 == EnumFacing.WEST.func_176745_a()) {
                  return EnumFacing.NORTH.func_176745_a();
               }

               if (var2 == EnumFacing.EAST.func_176745_a()) {
                  return EnumFacing.SOUTH.func_176745_a();
               }
            } else if (this.field_74885_f == EnumFacing.EAST) {
               if (var2 == EnumFacing.NORTH.func_176745_a()) {
                  return EnumFacing.EAST.func_176745_a();
               }

               if (var2 == EnumFacing.SOUTH.func_176745_a()) {
                  return EnumFacing.WEST.func_176745_a();
               }

               if (var2 == EnumFacing.WEST.func_176745_a()) {
                  return EnumFacing.NORTH.func_176745_a();
               }

               if (var2 == EnumFacing.EAST.func_176745_a()) {
                  return EnumFacing.SOUTH.func_176745_a();
               }
            }
         } else if (var1 == Blocks.field_150430_aB) {
            if (this.field_74885_f == EnumFacing.SOUTH) {
               if (var2 == 3) {
                  return 4;
               }

               if (var2 == 4) {
                  return 3;
               }
            } else if (this.field_74885_f == EnumFacing.WEST) {
               if (var2 == 3) {
                  return 1;
               }

               if (var2 == 4) {
                  return 2;
               }

               if (var2 == 2) {
                  return 3;
               }

               if (var2 == 1) {
                  return 4;
               }
            } else if (this.field_74885_f == EnumFacing.EAST) {
               if (var2 == 3) {
                  return 2;
               }

               if (var2 == 4) {
                  return 1;
               }

               if (var2 == 2) {
                  return 3;
               }

               if (var2 == 1) {
                  return 4;
               }
            }
         } else if (var1 != Blocks.field_150479_bC && !(var1 instanceof BlockDirectional)) {
            if (var1 == Blocks.field_150331_J || var1 == Blocks.field_150320_F || var1 == Blocks.field_150442_at || var1 == Blocks.field_150367_z) {
               if (this.field_74885_f == EnumFacing.SOUTH) {
                  if (var2 == EnumFacing.NORTH.func_176745_a() || var2 == EnumFacing.SOUTH.func_176745_a()) {
                     return EnumFacing.func_82600_a(var2).func_176734_d().func_176745_a();
                  }
               } else if (this.field_74885_f == EnumFacing.WEST) {
                  if (var2 == EnumFacing.NORTH.func_176745_a()) {
                     return EnumFacing.WEST.func_176745_a();
                  }

                  if (var2 == EnumFacing.SOUTH.func_176745_a()) {
                     return EnumFacing.EAST.func_176745_a();
                  }

                  if (var2 == EnumFacing.WEST.func_176745_a()) {
                     return EnumFacing.NORTH.func_176745_a();
                  }

                  if (var2 == EnumFacing.EAST.func_176745_a()) {
                     return EnumFacing.SOUTH.func_176745_a();
                  }
               } else if (this.field_74885_f == EnumFacing.EAST) {
                  if (var2 == EnumFacing.NORTH.func_176745_a()) {
                     return EnumFacing.EAST.func_176745_a();
                  }

                  if (var2 == EnumFacing.SOUTH.func_176745_a()) {
                     return EnumFacing.WEST.func_176745_a();
                  }

                  if (var2 == EnumFacing.WEST.func_176745_a()) {
                     return EnumFacing.NORTH.func_176745_a();
                  }

                  if (var2 == EnumFacing.EAST.func_176745_a()) {
                     return EnumFacing.SOUTH.func_176745_a();
                  }
               }
            }
         } else {
            EnumFacing var3 = EnumFacing.func_176731_b(var2);
            if (this.field_74885_f == EnumFacing.SOUTH) {
               if (var3 == EnumFacing.SOUTH || var3 == EnumFacing.NORTH) {
                  return var3.func_176734_d().func_176736_b();
               }
            } else if (this.field_74885_f == EnumFacing.WEST) {
               if (var3 == EnumFacing.NORTH) {
                  return EnumFacing.WEST.func_176736_b();
               }

               if (var3 == EnumFacing.SOUTH) {
                  return EnumFacing.EAST.func_176736_b();
               }

               if (var3 == EnumFacing.WEST) {
                  return EnumFacing.NORTH.func_176736_b();
               }

               if (var3 == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.func_176736_b();
               }
            } else if (this.field_74885_f == EnumFacing.EAST) {
               if (var3 == EnumFacing.NORTH) {
                  return EnumFacing.EAST.func_176736_b();
               }

               if (var3 == EnumFacing.SOUTH) {
                  return EnumFacing.WEST.func_176736_b();
               }

               if (var3 == EnumFacing.WEST) {
                  return EnumFacing.NORTH.func_176736_b();
               }

               if (var3 == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.func_176736_b();
               }
            }
         }
      } else if (this.field_74885_f == EnumFacing.SOUTH) {
         if (var2 == 2) {
            return 3;
         }

         if (var2 == 3) {
            return 2;
         }
      } else if (this.field_74885_f == EnumFacing.WEST) {
         if (var2 == 0) {
            return 2;
         }

         if (var2 == 1) {
            return 3;
         }

         if (var2 == 2) {
            return 0;
         }

         if (var2 == 3) {
            return 1;
         }
      } else if (this.field_74885_f == EnumFacing.EAST) {
         if (var2 == 0) {
            return 2;
         }

         if (var2 == 1) {
            return 3;
         }

         if (var2 == 2) {
            return 1;
         }

         if (var2 == 3) {
            return 0;
         }
      }

      return var2;
   }

   protected void func_175811_a(World var1, IBlockState var2, int var3, int var4, int var5, StructureBoundingBox var6) {
      BlockPos var7 = new BlockPos(this.func_74865_a(var3, var5), this.func_74862_a(var4), this.func_74873_b(var3, var5));
      if (var6.func_175898_b(var7)) {
         var1.func_180501_a(var7, var2, 2);
      }
   }

   protected IBlockState func_175807_a(World var1, int var2, int var3, int var4, StructureBoundingBox var5) {
      int var6 = this.func_74865_a(var2, var4);
      int var7 = this.func_74862_a(var3);
      int var8 = this.func_74873_b(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      return !var5.func_175898_b(var9) ? Blocks.field_150350_a.func_176223_P() : var1.func_180495_p(var9);
   }

   protected void func_74878_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = var4; var9 <= var7; ++var9) {
         for(int var10 = var3; var10 <= var6; ++var10) {
            for(int var11 = var5; var11 <= var8; ++var11) {
               this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), var10, var9, var11, var2);
            }
         }
      }

   }

   protected void func_175804_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, IBlockState var10, boolean var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var11 || this.func_175807_a(var1, var13, var12, var14, var2).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  if (var12 != var4 && var12 != var7 && var13 != var3 && var13 != var6 && var14 != var5 && var14 != var8) {
                     this.func_175811_a(var1, var10, var13, var12, var14, var2);
                  } else {
                     this.func_175811_a(var1, var9, var13, var12, var14, var2);
                  }
               }
            }
         }
      }

   }

   protected void func_74882_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, Random var10, StructureComponent.BlockSelector var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var9 || this.func_175807_a(var1, var13, var12, var14, var2).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  var11.func_75062_a(var10, var13, var12, var14, var12 == var4 || var12 == var7 || var13 == var3 || var13 == var6 || var14 == var5 || var14 == var8);
                  this.func_175811_a(var1, var11.func_180780_a(), var13, var12, var14, var2);
               }
            }
         }
      }

   }

   protected void func_175805_a(World var1, StructureBoundingBox var2, Random var3, float var4, int var5, int var6, int var7, int var8, int var9, int var10, IBlockState var11, IBlockState var12, boolean var13) {
      for(int var14 = var6; var14 <= var9; ++var14) {
         for(int var15 = var5; var15 <= var8; ++var15) {
            for(int var16 = var7; var16 <= var10; ++var16) {
               if (var3.nextFloat() <= var4 && (!var13 || this.func_175807_a(var1, var15, var14, var16, var2).func_177230_c().func_149688_o() != Material.field_151579_a)) {
                  if (var14 != var6 && var14 != var9 && var15 != var5 && var15 != var8 && var16 != var7 && var16 != var10) {
                     this.func_175811_a(var1, var12, var15, var14, var16, var2);
                  } else {
                     this.func_175811_a(var1, var11, var15, var14, var16, var2);
                  }
               }
            }
         }
      }

   }

   protected void func_175809_a(World var1, StructureBoundingBox var2, Random var3, float var4, int var5, int var6, int var7, IBlockState var8) {
      if (var3.nextFloat() < var4) {
         this.func_175811_a(var1, var8, var5, var6, var7, var2);
      }

   }

   protected void func_180777_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, boolean var10) {
      float var11 = (float)(var6 - var3 + 1);
      float var12 = (float)(var7 - var4 + 1);
      float var13 = (float)(var8 - var5 + 1);
      float var14 = (float)var3 + var11 / 2.0F;
      float var15 = (float)var5 + var13 / 2.0F;

      for(int var16 = var4; var16 <= var7; ++var16) {
         float var17 = (float)(var16 - var4) / var12;

         for(int var18 = var3; var18 <= var6; ++var18) {
            float var19 = ((float)var18 - var14) / (var11 * 0.5F);

            for(int var20 = var5; var20 <= var8; ++var20) {
               float var21 = ((float)var20 - var15) / (var13 * 0.5F);
               if (!var10 || this.func_175807_a(var1, var18, var16, var20, var2).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  float var22 = var19 * var19 + var17 * var17 + var21 * var21;
                  if (var22 <= 1.05F) {
                     this.func_175811_a(var1, var9, var18, var16, var20, var2);
                  }
               }
            }
         }
      }

   }

   protected void func_74871_b(World var1, int var2, int var3, int var4, StructureBoundingBox var5) {
      BlockPos var6 = new BlockPos(this.func_74865_a(var2, var4), this.func_74862_a(var3), this.func_74873_b(var2, var4));
      if (var5.func_175898_b(var6)) {
         while(!var1.func_175623_d(var6) && var6.func_177956_o() < 255) {
            var1.func_180501_a(var6, Blocks.field_150350_a.func_176223_P(), 2);
            var6 = var6.func_177984_a();
         }

      }
   }

   protected void func_175808_b(World var1, IBlockState var2, int var3, int var4, int var5, StructureBoundingBox var6) {
      int var7 = this.func_74865_a(var3, var5);
      int var8 = this.func_74862_a(var4);
      int var9 = this.func_74873_b(var3, var5);
      if (var6.func_175898_b(new BlockPos(var7, var8, var9))) {
         while((var1.func_175623_d(new BlockPos(var7, var8, var9)) || var1.func_180495_p(new BlockPos(var7, var8, var9)).func_177230_c().func_149688_o().func_76224_d()) && var8 > 1) {
            var1.func_180501_a(new BlockPos(var7, var8, var9), var2, 2);
            --var8;
         }

      }
   }

   protected boolean func_180778_a(World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, List<WeightedRandomChestContent> var7, int var8) {
      BlockPos var9 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
      if (var2.func_175898_b(var9) && var1.func_180495_p(var9).func_177230_c() != Blocks.field_150486_ae) {
         IBlockState var10 = Blocks.field_150486_ae.func_176223_P();
         var1.func_180501_a(var9, Blocks.field_150486_ae.func_176458_f(var1, var9, var10), 2);
         TileEntity var11 = var1.func_175625_s(var9);
         if (var11 instanceof TileEntityChest) {
            WeightedRandomChestContent.func_177630_a(var3, var7, (TileEntityChest)var11, var8);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean func_175806_a(World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, int var7, List<WeightedRandomChestContent> var8, int var9) {
      BlockPos var10 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
      if (var2.func_175898_b(var10) && var1.func_180495_p(var10).func_177230_c() != Blocks.field_150367_z) {
         var1.func_180501_a(var10, Blocks.field_150367_z.func_176203_a(this.func_151555_a(Blocks.field_150367_z, var7)), 2);
         TileEntity var11 = var1.func_175625_s(var10);
         if (var11 instanceof TileEntityDispenser) {
            WeightedRandomChestContent.func_177631_a(var3, var8, (TileEntityDispenser)var11, var9);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void func_175810_a(World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, EnumFacing var7) {
      BlockPos var8 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
      if (var2.func_175898_b(var8)) {
         ItemDoor.func_179235_a(var1, var8, var7.func_176735_f(), Blocks.field_180413_ao);
      }

   }

   public void func_181138_a(int var1, int var2, int var3) {
      this.field_74887_e.func_78886_a(var1, var2, var3);
   }

   public abstract static class BlockSelector {
      protected IBlockState field_151562_a;

      protected BlockSelector() {
         super();
         this.field_151562_a = Blocks.field_150350_a.func_176223_P();
      }

      public abstract void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5);

      public IBlockState func_180780_a() {
         return this.field_151562_a;
      }
   }
}
