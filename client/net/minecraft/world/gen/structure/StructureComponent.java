package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public abstract class StructureComponent {
   protected StructureBoundingBox field_74887_e;
   protected int field_74885_f;
   protected int field_74886_g;

   public StructureComponent() {
      super();
   }

   protected StructureComponent(int var1) {
      super();
      this.field_74886_g = var1;
      this.field_74885_f = -1;
   }

   public NBTTagCompound func_143010_b() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("id", MapGenStructureIO.func_143036_a(this));
      var1.func_74782_a("BB", this.field_74887_e.func_151535_h());
      var1.func_74768_a("O", this.field_74885_f);
      var1.func_74768_a("GD", this.field_74886_g);
      this.func_143012_a(var1);
      return var1;
   }

   protected abstract void func_143012_a(NBTTagCompound var1);

   public void func_143009_a(World var1, NBTTagCompound var2) {
      if (var2.func_74764_b("BB")) {
         this.field_74887_e = new StructureBoundingBox(var2.func_74759_k("BB"));
      }

      this.field_74885_f = var2.func_74762_e("O");
      this.field_74886_g = var2.func_74762_e("GD");
      this.func_143011_b(var2);
   }

   protected abstract void func_143011_b(NBTTagCompound var1);

   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
   }

   public abstract boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3);

   public StructureBoundingBox func_74874_b() {
      return this.field_74887_e;
   }

   public int func_74877_c() {
      return this.field_74886_g;
   }

   public static StructureComponent func_74883_a(List var0, StructureBoundingBox var1) {
      for(StructureComponent var3 : var0) {
         if (var3.func_74874_b() != null && var3.func_74874_b().func_78884_a(var1)) {
            return var3;
         }
      }

      return null;
   }

   public ChunkPosition func_151553_a() {
      return new ChunkPosition(this.field_74887_e.func_78881_e(), this.field_74887_e.func_78879_f(), this.field_74887_e.func_78891_g());
   }

   protected boolean func_74860_a(World var1, StructureBoundingBox var2) {
      int var3 = Math.max(this.field_74887_e.field_78897_a - 1, var2.field_78897_a);
      int var4 = Math.max(this.field_74887_e.field_78895_b - 1, var2.field_78895_b);
      int var5 = Math.max(this.field_74887_e.field_78896_c - 1, var2.field_78896_c);
      int var6 = Math.min(this.field_74887_e.field_78893_d + 1, var2.field_78893_d);
      int var7 = Math.min(this.field_74887_e.field_78894_e + 1, var2.field_78894_e);
      int var8 = Math.min(this.field_74887_e.field_78892_f + 1, var2.field_78892_f);

      for(int var9 = var3; var9 <= var6; ++var9) {
         for(int var10 = var5; var10 <= var8; ++var10) {
            if (var1.func_147439_a(var9, var4, var10).func_149688_o().func_76224_d()) {
               return true;
            }

            if (var1.func_147439_a(var9, var7, var10).func_149688_o().func_76224_d()) {
               return true;
            }
         }
      }

      for(int var11 = var3; var11 <= var6; ++var11) {
         for(int var13 = var4; var13 <= var7; ++var13) {
            if (var1.func_147439_a(var11, var13, var5).func_149688_o().func_76224_d()) {
               return true;
            }

            if (var1.func_147439_a(var11, var13, var8).func_149688_o().func_76224_d()) {
               return true;
            }
         }
      }

      for(int var12 = var5; var12 <= var8; ++var12) {
         for(int var14 = var4; var14 <= var7; ++var14) {
            if (var1.func_147439_a(var3, var14, var12).func_149688_o().func_76224_d()) {
               return true;
            }

            if (var1.func_147439_a(var6, var14, var12).func_149688_o().func_76224_d()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int func_74865_a(int var1, int var2) {
      switch(this.field_74885_f) {
         case 0:
         case 2:
            return this.field_74887_e.field_78897_a + var1;
         case 1:
            return this.field_74887_e.field_78893_d - var2;
         case 3:
            return this.field_74887_e.field_78897_a + var2;
         default:
            return var1;
      }
   }

   protected int func_74862_a(int var1) {
      return this.field_74885_f == -1 ? var1 : var1 + this.field_74887_e.field_78895_b;
   }

   protected int func_74873_b(int var1, int var2) {
      switch(this.field_74885_f) {
         case 0:
            return this.field_74887_e.field_78896_c + var2;
         case 1:
         case 3:
            return this.field_74887_e.field_78896_c + var1;
         case 2:
            return this.field_74887_e.field_78892_f - var2;
         default:
            return var2;
      }
   }

   protected int func_151555_a(Block var1, int var2) {
      if (var1 == Blocks.field_150448_aq) {
         if (this.field_74885_f == 1 || this.field_74885_f == 3) {
            if (var2 == 1) {
               return 0;
            }

            return 1;
         }
      } else if (var1 != Blocks.field_150466_ao && var1 != Blocks.field_150454_av) {
         if (var1 != Blocks.field_150446_ar
            && var1 != Blocks.field_150476_ad
            && var1 != Blocks.field_150387_bl
            && var1 != Blocks.field_150390_bg
            && var1 != Blocks.field_150372_bz) {
            if (var1 == Blocks.field_150468_ap) {
               if (this.field_74885_f == 0) {
                  if (var2 == 2) {
                     return 3;
                  }

                  if (var2 == 3) {
                     return 2;
                  }
               } else if (this.field_74885_f == 1) {
                  if (var2 == 2) {
                     return 4;
                  }

                  if (var2 == 3) {
                     return 5;
                  }

                  if (var2 == 4) {
                     return 2;
                  }

                  if (var2 == 5) {
                     return 3;
                  }
               } else if (this.field_74885_f == 3) {
                  if (var2 == 2) {
                     return 5;
                  }

                  if (var2 == 3) {
                     return 4;
                  }

                  if (var2 == 4) {
                     return 2;
                  }

                  if (var2 == 5) {
                     return 3;
                  }
               }
            } else if (var1 == Blocks.field_150430_aB) {
               if (this.field_74885_f == 0) {
                  if (var2 == 3) {
                     return 4;
                  }

                  if (var2 == 4) {
                     return 3;
                  }
               } else if (this.field_74885_f == 1) {
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
               } else if (this.field_74885_f == 3) {
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
                  if (this.field_74885_f == 0) {
                     if (var2 == 2 || var2 == 3) {
                        return Facing.field_71588_a[var2];
                     }
                  } else if (this.field_74885_f == 1) {
                     if (var2 == 2) {
                        return 4;
                     }

                     if (var2 == 3) {
                        return 5;
                     }

                     if (var2 == 4) {
                        return 2;
                     }

                     if (var2 == 5) {
                        return 3;
                     }
                  } else if (this.field_74885_f == 3) {
                     if (var2 == 2) {
                        return 5;
                     }

                     if (var2 == 3) {
                        return 4;
                     }

                     if (var2 == 4) {
                        return 2;
                     }

                     if (var2 == 5) {
                        return 3;
                     }
                  }
               }
            } else if (this.field_74885_f == 0) {
               if (var2 == 0 || var2 == 2) {
                  return Direction.field_71580_e[var2];
               }
            } else if (this.field_74885_f == 1) {
               if (var2 == 2) {
                  return 1;
               }

               if (var2 == 0) {
                  return 3;
               }

               if (var2 == 1) {
                  return 2;
               }

               if (var2 == 3) {
                  return 0;
               }
            } else if (this.field_74885_f == 3) {
               if (var2 == 2) {
                  return 3;
               }

               if (var2 == 0) {
                  return 1;
               }

               if (var2 == 1) {
                  return 2;
               }

               if (var2 == 3) {
                  return 0;
               }
            }
         } else if (this.field_74885_f == 0) {
            if (var2 == 2) {
               return 3;
            }

            if (var2 == 3) {
               return 2;
            }
         } else if (this.field_74885_f == 1) {
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
         } else if (this.field_74885_f == 3) {
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
      } else if (this.field_74885_f == 0) {
         if (var2 == 0) {
            return 2;
         }

         if (var2 == 2) {
            return 0;
         }
      } else {
         if (this.field_74885_f == 1) {
            return var2 + 1 & 3;
         }

         if (this.field_74885_f == 3) {
            return var2 + 3 & 3;
         }
      }

      return var2;
   }

   protected void func_151550_a(World var1, Block var2, int var3, int var4, int var5, int var6, StructureBoundingBox var7) {
      int var8 = this.func_74865_a(var4, var6);
      int var9 = this.func_74862_a(var5);
      int var10 = this.func_74873_b(var4, var6);
      if (var7.func_78890_b(var8, var9, var10)) {
         var1.func_147465_d(var8, var9, var10, var2, var3, 2);
      }
   }

   protected Block func_151548_a(World var1, int var2, int var3, int var4, StructureBoundingBox var5) {
      int var6 = this.func_74865_a(var2, var4);
      int var7 = this.func_74862_a(var3);
      int var8 = this.func_74873_b(var2, var4);
      return !var5.func_78890_b(var6, var7, var8) ? Blocks.field_150350_a : var1.func_147439_a(var6, var7, var8);
   }

   protected void func_74878_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = var4; var9 <= var7; ++var9) {
         for(int var10 = var3; var10 <= var6; ++var10) {
            for(int var11 = var5; var11 <= var8; ++var11) {
               this.func_151550_a(var1, Blocks.field_150350_a, 0, var10, var9, var11, var2);
            }
         }
      }
   }

   protected void func_151549_a(
      World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, Block var9, Block var10, boolean var11
   ) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var11 || this.func_151548_a(var1, var13, var12, var14, var2).func_149688_o() != Material.field_151579_a) {
                  if (var12 != var4 && var12 != var7 && var13 != var3 && var13 != var6 && var14 != var5 && var14 != var8) {
                     this.func_151550_a(var1, var10, 0, var13, var12, var14, var2);
                  } else {
                     this.func_151550_a(var1, var9, 0, var13, var12, var14, var2);
                  }
               }
            }
         }
      }
   }

   protected void func_151556_a(
      World var1,
      StructureBoundingBox var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      Block var9,
      int var10,
      Block var11,
      int var12,
      boolean var13
   ) {
      for(int var14 = var4; var14 <= var7; ++var14) {
         for(int var15 = var3; var15 <= var6; ++var15) {
            for(int var16 = var5; var16 <= var8; ++var16) {
               if (!var13 || this.func_151548_a(var1, var15, var14, var16, var2).func_149688_o() != Material.field_151579_a) {
                  if (var14 != var4 && var14 != var7 && var15 != var3 && var15 != var6 && var16 != var5 && var16 != var8) {
                     this.func_151550_a(var1, var11, var12, var15, var14, var16, var2);
                  } else {
                     this.func_151550_a(var1, var9, var10, var15, var14, var16, var2);
                  }
               }
            }
         }
      }
   }

   protected void func_74882_a(
      World var1,
      StructureBoundingBox var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      Random var10,
      StructureComponent$BlockSelector var11
   ) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var9 || this.func_151548_a(var1, var13, var12, var14, var2).func_149688_o() != Material.field_151579_a) {
                  var11.func_75062_a(
                     var10, var13, var12, var14, var12 == var4 || var12 == var7 || var13 == var3 || var13 == var6 || var14 == var5 || var14 == var8
                  );
                  this.func_151550_a(var1, var11.func_151561_a(), var11.func_75064_b(), var13, var12, var14, var2);
               }
            }
         }
      }
   }

   protected void func_151551_a(
      World var1,
      StructureBoundingBox var2,
      Random var3,
      float var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      Block var11,
      Block var12,
      boolean var13
   ) {
      for(int var14 = var6; var14 <= var9; ++var14) {
         for(int var15 = var5; var15 <= var8; ++var15) {
            for(int var16 = var7; var16 <= var10; ++var16) {
               if (!(var3.nextFloat() > var4) && (!var13 || this.func_151548_a(var1, var15, var14, var16, var2).func_149688_o() != Material.field_151579_a)) {
                  if (var14 != var6 && var14 != var9 && var15 != var5 && var15 != var8 && var16 != var7 && var16 != var10) {
                     this.func_151550_a(var1, var12, 0, var15, var14, var16, var2);
                  } else {
                     this.func_151550_a(var1, var11, 0, var15, var14, var16, var2);
                  }
               }
            }
         }
      }
   }

   protected void func_151552_a(World var1, StructureBoundingBox var2, Random var3, float var4, int var5, int var6, int var7, Block var8, int var9) {
      if (var3.nextFloat() < var4) {
         this.func_151550_a(var1, var8, var9, var5, var6, var7, var2);
      }
   }

   protected void func_151547_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, Block var9, boolean var10) {
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
               if (!var10 || this.func_151548_a(var1, var18, var16, var20, var2).func_149688_o() != Material.field_151579_a) {
                  float var22 = var19 * var19 + var17 * var17 + var21 * var21;
                  if (var22 <= 1.05F) {
                     this.func_151550_a(var1, var9, 0, var18, var16, var20, var2);
                  }
               }
            }
         }
      }
   }

   protected void func_74871_b(World var1, int var2, int var3, int var4, StructureBoundingBox var5) {
      int var6 = this.func_74865_a(var2, var4);
      int var7 = this.func_74862_a(var3);
      int var8 = this.func_74873_b(var2, var4);
      if (var5.func_78890_b(var6, var7, var8)) {
         while(!var1.func_147437_c(var6, var7, var8) && var7 < 255) {
            var1.func_147465_d(var6, var7, var8, Blocks.field_150350_a, 0, 2);
            ++var7;
         }
      }
   }

   protected void func_151554_b(World var1, Block var2, int var3, int var4, int var5, int var6, StructureBoundingBox var7) {
      int var8 = this.func_74865_a(var4, var6);
      int var9 = this.func_74862_a(var5);
      int var10 = this.func_74873_b(var4, var6);
      if (var7.func_78890_b(var8, var9, var10)) {
         while((var1.func_147437_c(var8, var9, var10) || var1.func_147439_a(var8, var9, var10).func_149688_o().func_76224_d()) && var9 > 1) {
            var1.func_147465_d(var8, var9, var10, var2, var3, 2);
            --var9;
         }
      }
   }

   protected boolean func_74879_a(
      World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, WeightedRandomChestContent[] var7, int var8
   ) {
      int var9 = this.func_74865_a(var4, var6);
      int var10 = this.func_74862_a(var5);
      int var11 = this.func_74873_b(var4, var6);
      if (var2.func_78890_b(var9, var10, var11) && var1.func_147439_a(var9, var10, var11) != Blocks.field_150486_ae) {
         var1.func_147465_d(var9, var10, var11, Blocks.field_150486_ae, 0, 2);
         TileEntityChest var12 = (TileEntityChest)var1.func_147438_o(var9, var10, var11);
         if (var12 != null) {
            WeightedRandomChestContent.func_76293_a(var3, var7, var12, var8);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean func_74869_a(
      World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, int var7, WeightedRandomChestContent[] var8, int var9
   ) {
      int var10 = this.func_74865_a(var4, var6);
      int var11 = this.func_74862_a(var5);
      int var12 = this.func_74873_b(var4, var6);
      if (var2.func_78890_b(var10, var11, var12) && var1.func_147439_a(var10, var11, var12) != Blocks.field_150367_z) {
         var1.func_147465_d(var10, var11, var12, Blocks.field_150367_z, this.func_151555_a(Blocks.field_150367_z, var7), 2);
         TileEntityDispenser var13 = (TileEntityDispenser)var1.func_147438_o(var10, var11, var12);
         if (var13 != null) {
            WeightedRandomChestContent.func_150706_a(var3, var8, var13, var9);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void func_74881_a(World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, int var7) {
      int var8 = this.func_74865_a(var4, var6);
      int var9 = this.func_74862_a(var5);
      int var10 = this.func_74873_b(var4, var6);
      if (var2.func_78890_b(var8, var9, var10)) {
         ItemDoor.func_150924_a(var1, var8, var9, var10, var7, Blocks.field_150466_ao);
      }
   }
}
