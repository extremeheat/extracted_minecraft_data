package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   public static final IntegerProperty field_176591_a;
   protected static final VoxelShape field_196403_b;
   protected static final VoxelShape field_196404_c;

   public BlockCauldron(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176591_a, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196404_c;
   }

   public boolean func_200124_e(IBlockState var1) {
      return false;
   }

   public VoxelShape func_199600_g(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196403_b;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      int var5 = (Integer)var1.func_177229_b(field_176591_a);
      float var6 = (float)var3.func_177956_o() + (6.0F + (float)(3 * var5)) / 16.0F;
      if (!var2.field_72995_K && var4.func_70027_ad() && var5 > 0 && var4.func_174813_aQ().field_72338_b <= (double)var6) {
         var4.func_70066_B();
         this.func_176590_a(var2, var3, var1, var5 - 1);
      }

   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      ItemStack var10 = var4.func_184586_b(var5);
      if (var10.func_190926_b()) {
         return true;
      } else {
         int var11 = (Integer)var1.func_177229_b(field_176591_a);
         Item var12 = var10.func_77973_b();
         if (var12 == Items.field_151131_as) {
            if (var11 < 3 && !var2.field_72995_K) {
               if (!var4.field_71075_bZ.field_75098_d) {
                  var4.func_184611_a(var5, new ItemStack(Items.field_151133_ar));
               }

               var4.func_195066_a(StatList.field_188077_K);
               this.func_176590_a(var2, var3, var1, 3);
               var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187624_K, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if (var12 == Items.field_151133_ar) {
            if (var11 == 3 && !var2.field_72995_K) {
               if (!var4.field_71075_bZ.field_75098_d) {
                  var10.func_190918_g(1);
                  if (var10.func_190926_b()) {
                     var4.func_184611_a(var5, new ItemStack(Items.field_151131_as));
                  } else if (!var4.field_71071_by.func_70441_a(new ItemStack(Items.field_151131_as))) {
                     var4.func_71019_a(new ItemStack(Items.field_151131_as), false);
                  }
               }

               var4.func_195066_a(StatList.field_188078_L);
               this.func_176590_a(var2, var3, var1, 0);
               var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187630_M, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else {
            ItemStack var16;
            if (var12 == Items.field_151069_bo) {
               if (var11 > 0 && !var2.field_72995_K) {
                  if (!var4.field_71075_bZ.field_75098_d) {
                     var16 = PotionUtils.func_185188_a(new ItemStack(Items.field_151068_bn), PotionTypes.field_185230_b);
                     var4.func_195066_a(StatList.field_188078_L);
                     var10.func_190918_g(1);
                     if (var10.func_190926_b()) {
                        var4.func_184611_a(var5, var16);
                     } else if (!var4.field_71071_by.func_70441_a(var16)) {
                        var4.func_71019_a(var16, false);
                     } else if (var4 instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)var4).func_71120_a(var4.field_71069_bz);
                     }
                  }

                  var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187615_H, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  this.func_176590_a(var2, var3, var1, var11 - 1);
               }

               return true;
            } else if (var12 == Items.field_151068_bn && PotionUtils.func_185191_c(var10) == PotionTypes.field_185230_b) {
               if (var11 < 3 && !var2.field_72995_K) {
                  if (!var4.field_71075_bZ.field_75098_d) {
                     var16 = new ItemStack(Items.field_151069_bo);
                     var4.func_195066_a(StatList.field_188078_L);
                     var4.func_184611_a(var5, var16);
                     if (var4 instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)var4).func_71120_a(var4.field_71069_bz);
                     }
                  }

                  var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_191241_J, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  this.func_176590_a(var2, var3, var1, var11 + 1);
               }

               return true;
            } else {
               if (var11 > 0 && var12 instanceof ItemArmorDyeable) {
                  ItemArmorDyeable var13 = (ItemArmorDyeable)var12;
                  if (var13.func_200883_f_(var10) && !var2.field_72995_K) {
                     var13.func_200884_g(var10);
                     this.func_176590_a(var2, var3, var1, var11 - 1);
                     var4.func_195066_a(StatList.field_188079_M);
                     return true;
                  }
               }

               if (var11 > 0 && var12 instanceof ItemBanner) {
                  if (TileEntityBanner.func_175113_c(var10) > 0 && !var2.field_72995_K) {
                     var16 = var10.func_77946_l();
                     var16.func_190920_e(1);
                     TileEntityBanner.func_175117_e(var16);
                     var4.func_195066_a(StatList.field_188080_N);
                     if (!var4.field_71075_bZ.field_75098_d) {
                        var10.func_190918_g(1);
                        this.func_176590_a(var2, var3, var1, var11 - 1);
                     }

                     if (var10.func_190926_b()) {
                        var4.func_184611_a(var5, var16);
                     } else if (!var4.field_71071_by.func_70441_a(var16)) {
                        var4.func_71019_a(var16, false);
                     } else if (var4 instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)var4).func_71120_a(var4.field_71069_bz);
                     }
                  }

                  return true;
               } else if (var11 > 0 && var12 instanceof ItemBlock) {
                  Block var15 = ((ItemBlock)var12).func_179223_d();
                  if (var15 instanceof BlockShulkerBox && !var2.func_201670_d()) {
                     ItemStack var14 = new ItemStack(Blocks.field_204409_il, 1);
                     if (var10.func_77942_o()) {
                        var14.func_77982_d(var10.func_77978_p().func_74737_b());
                     }

                     var4.func_184611_a(var5, var14);
                     this.func_176590_a(var2, var3, var1, var11 - 1);
                     var4.func_195066_a(StatList.field_212740_X);
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   public void func_176590_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176591_a, MathHelper.func_76125_a(var4, 0, 3)), 2);
      var1.func_175666_e(var2, this);
   }

   public void func_176224_k(World var1, BlockPos var2) {
      if (var1.field_73012_v.nextInt(20) == 1) {
         float var3 = var1.func_180494_b(var2).func_180626_a(var2);
         if (var3 >= 0.15F) {
            IBlockState var4 = var1.func_180495_p(var2);
            if ((Integer)var4.func_177229_b(field_176591_a) < 3) {
               var1.func_180501_a(var2, (IBlockState)var4.func_177231_a(field_176591_a), 2);
            }

         }
      }
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return (Integer)var1.func_177229_b(field_176591_a);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176591_a);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      if (var4 == EnumFacing.UP) {
         return BlockFaceShape.BOWL;
      } else {
         return var4 == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
      }
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176591_a = BlockStateProperties.field_208130_ae;
      field_196403_b = Block.func_208617_a(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
      field_196404_c = VoxelShapes.func_197878_a(VoxelShapes.func_197868_b(), field_196403_b, IBooleanFunction.ONLY_FIRST);
   }
}
