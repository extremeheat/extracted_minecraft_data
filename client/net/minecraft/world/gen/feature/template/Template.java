package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapePartBitSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template {
   private final List<List<Template.BlockInfo>> field_204769_a = Lists.newArrayList();
   private final List<Template.EntityInfo> field_186271_b = Lists.newArrayList();
   private BlockPos field_186272_c;
   private String field_186273_d;

   public Template() {
      super();
      this.field_186272_c = BlockPos.field_177992_a;
      this.field_186273_d = "?";
   }

   public BlockPos func_186259_a() {
      return this.field_186272_c;
   }

   public void func_186252_a(String var1) {
      this.field_186273_d = var1;
   }

   public String func_186261_b() {
      return this.field_186273_d;
   }

   public void func_186254_a(World var1, BlockPos var2, BlockPos var3, boolean var4, @Nullable Block var5) {
      if (var3.func_177958_n() >= 1 && var3.func_177956_o() >= 1 && var3.func_177952_p() >= 1) {
         BlockPos var6 = var2.func_177971_a(var3).func_177982_a(-1, -1, -1);
         ArrayList var7 = Lists.newArrayList();
         ArrayList var8 = Lists.newArrayList();
         ArrayList var9 = Lists.newArrayList();
         BlockPos var10 = new BlockPos(Math.min(var2.func_177958_n(), var6.func_177958_n()), Math.min(var2.func_177956_o(), var6.func_177956_o()), Math.min(var2.func_177952_p(), var6.func_177952_p()));
         BlockPos var11 = new BlockPos(Math.max(var2.func_177958_n(), var6.func_177958_n()), Math.max(var2.func_177956_o(), var6.func_177956_o()), Math.max(var2.func_177952_p(), var6.func_177952_p()));
         this.field_186272_c = var3;
         Iterator var12 = BlockPos.func_177975_b(var10, var11).iterator();

         while(true) {
            while(true) {
               BlockPos.MutableBlockPos var13;
               BlockPos var14;
               IBlockState var15;
               do {
                  if (!var12.hasNext()) {
                     ArrayList var18 = Lists.newArrayList();
                     var18.addAll(var7);
                     var18.addAll(var8);
                     var18.addAll(var9);
                     this.field_204769_a.clear();
                     this.field_204769_a.add(var18);
                     if (var4) {
                        this.func_186255_a(var1, var10, var11.func_177982_a(1, 1, 1));
                     } else {
                        this.field_186271_b.clear();
                     }

                     return;
                  }

                  var13 = (BlockPos.MutableBlockPos)var12.next();
                  var14 = var13.func_177973_b(var10);
                  var15 = var1.func_180495_p(var13);
               } while(var5 != null && var5 == var15.func_177230_c());

               TileEntity var16 = var1.func_175625_s(var13);
               if (var16 != null) {
                  NBTTagCompound var17 = var16.func_189515_b(new NBTTagCompound());
                  var17.func_82580_o("x");
                  var17.func_82580_o("y");
                  var17.func_82580_o("z");
                  var8.add(new Template.BlockInfo(var14, var15, var17));
               } else if (!var15.func_200015_d(var1, var13) && !var15.func_185917_h()) {
                  var9.add(new Template.BlockInfo(var14, var15, (NBTTagCompound)null));
               } else {
                  var7.add(new Template.BlockInfo(var14, var15, (NBTTagCompound)null));
               }
            }
         }
      }
   }

   private void func_186255_a(World var1, BlockPos var2, BlockPos var3) {
      List var4 = var1.func_175647_a(Entity.class, new AxisAlignedBB(var2, var3), (var0) -> {
         return !(var0 instanceof EntityPlayer);
      });
      this.field_186271_b.clear();

      Vec3d var7;
      NBTTagCompound var8;
      BlockPos var9;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); this.field_186271_b.add(new Template.EntityInfo(var7, var9, var8))) {
         Entity var6 = (Entity)var5.next();
         var7 = new Vec3d(var6.field_70165_t - (double)var2.func_177958_n(), var6.field_70163_u - (double)var2.func_177956_o(), var6.field_70161_v - (double)var2.func_177952_p());
         var8 = new NBTTagCompound();
         var6.func_70039_c(var8);
         if (var6 instanceof EntityPainting) {
            var9 = ((EntityPainting)var6).func_174857_n().func_177973_b(var2);
         } else {
            var9 = new BlockPos(var7);
         }
      }

   }

   public Map<BlockPos, String> func_186258_a(BlockPos var1, PlacementSettings var2) {
      HashMap var3 = Maps.newHashMap();
      MutableBoundingBox var4 = var2.func_186213_g();
      Iterator var5 = var2.func_204764_a(this.field_204769_a, var1).iterator();

      while(true) {
         Template.BlockInfo var6;
         BlockPos var7;
         do {
            if (!var5.hasNext()) {
               return var3;
            }

            var6 = (Template.BlockInfo)var5.next();
            var7 = func_186266_a(var2, var6.field_186242_a).func_177971_a(var1);
         } while(var4 != null && !var4.func_175898_b(var7));

         IBlockState var8 = var6.field_186243_b;
         if (var8.func_177230_c() == Blocks.field_185779_df && var6.field_186244_c != null) {
            StructureMode var9 = StructureMode.valueOf(var6.field_186244_c.func_74779_i("mode"));
            if (var9 == StructureMode.DATA) {
               var3.put(var7, var6.field_186244_c.func_74779_i("metadata"));
            }
         }
      }
   }

   public BlockPos func_186262_a(PlacementSettings var1, BlockPos var2, PlacementSettings var3, BlockPos var4) {
      BlockPos var5 = func_186266_a(var1, var2);
      BlockPos var6 = func_186266_a(var3, var4);
      return var5.func_177973_b(var6);
   }

   public static BlockPos func_186266_a(PlacementSettings var0, BlockPos var1) {
      return func_207669_a(var1, var0.func_186212_b(), var0.func_186215_c(), var0.func_207664_d());
   }

   public void func_186260_a(IWorld var1, BlockPos var2, PlacementSettings var3) {
      var3.func_186224_i();
      this.func_186253_b(var1, var2, var3);
   }

   public void func_186253_b(IWorld var1, BlockPos var2, PlacementSettings var3) {
      this.func_189960_a(var1, var2, new IntegrityProcessor(var2, var3), var3, 2);
   }

   public boolean func_189962_a(IWorld var1, BlockPos var2, PlacementSettings var3, int var4) {
      return this.func_189960_a(var1, var2, new IntegrityProcessor(var2, var3), var3, var4);
   }

   public boolean func_189960_a(IWorld var1, BlockPos var2, @Nullable ITemplateProcessor var3, PlacementSettings var4, int var5) {
      if (this.field_204769_a.isEmpty()) {
         return false;
      } else {
         List var6 = var4.func_204764_a(this.field_204769_a, var2);
         if ((!var6.isEmpty() || !var4.func_186221_e() && !this.field_186271_b.isEmpty()) && this.field_186272_c.func_177958_n() >= 1 && this.field_186272_c.func_177956_o() >= 1 && this.field_186272_c.func_177952_p() >= 1) {
            Block var7 = var4.func_186219_f();
            MutableBoundingBox var8 = var4.func_186213_g();
            ArrayList var9 = Lists.newArrayListWithCapacity(var4.func_204763_l() ? var6.size() : 0);
            ArrayList var10 = Lists.newArrayListWithCapacity(var6.size());
            int var11 = 2147483647;
            int var12 = 2147483647;
            int var13 = 2147483647;
            int var14 = -2147483648;
            int var15 = -2147483648;
            int var16 = -2147483648;
            Iterator var17 = var6.iterator();

            while(var17.hasNext()) {
               Template.BlockInfo var18 = (Template.BlockInfo)var17.next();
               BlockPos var19 = func_186266_a(var4, var18.field_186242_a).func_177971_a(var2);
               Template.BlockInfo var20 = var3 != null ? var3.func_189943_a(var1, var19, var18) : var18;
               if (var20 != null) {
                  Block var21 = var20.field_186243_b.func_177230_c();
                  if ((var7 == null || var7 != var21) && (!var4.func_186227_h() || var21 != Blocks.field_185779_df) && (var8 == null || var8.func_175898_b(var19))) {
                     IFluidState var22 = var4.func_204763_l() ? var1.func_204610_c(var19) : null;
                     IBlockState var23 = var20.field_186243_b.func_185902_a(var4.func_186212_b());
                     IBlockState var24 = var23.func_185907_a(var4.func_186215_c());
                     TileEntity var25;
                     if (var20.field_186244_c != null) {
                        var25 = var1.func_175625_s(var19);
                        if (var25 instanceof IInventory) {
                           ((IInventory)var25).func_174888_l();
                        }

                        var1.func_180501_a(var19, Blocks.field_180401_cv.func_176223_P(), 4);
                     }

                     if (var1.func_180501_a(var19, var24, var5)) {
                        var11 = Math.min(var11, var19.func_177958_n());
                        var12 = Math.min(var12, var19.func_177956_o());
                        var13 = Math.min(var13, var19.func_177952_p());
                        var14 = Math.max(var14, var19.func_177958_n());
                        var15 = Math.max(var15, var19.func_177956_o());
                        var16 = Math.max(var16, var19.func_177952_p());
                        var10.add(Pair.of(var19, var18.field_186244_c));
                        if (var20.field_186244_c != null) {
                           var25 = var1.func_175625_s(var19);
                           if (var25 != null) {
                              var20.field_186244_c.func_74768_a("x", var19.func_177958_n());
                              var20.field_186244_c.func_74768_a("y", var19.func_177956_o());
                              var20.field_186244_c.func_74768_a("z", var19.func_177952_p());
                              var25.func_145839_a(var20.field_186244_c);
                              var25.func_189668_a(var4.func_186212_b());
                              var25.func_189667_a(var4.func_186215_c());
                           }
                        }

                        if (var22 != null && var24.func_177230_c() instanceof ILiquidContainer) {
                           ((ILiquidContainer)var24.func_177230_c()).func_204509_a(var1, var19, var24, var22);
                           if (!var22.func_206889_d()) {
                              var9.add(var19);
                           }
                        }
                     }
                  }
               }
            }

            boolean var29 = true;
            EnumFacing[] var30 = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

            int var37;
            while(var29 && !var9.isEmpty()) {
               var29 = false;
               Iterator var31 = var9.iterator();

               while(var31.hasNext()) {
                  BlockPos var33 = (BlockPos)var31.next();
                  IFluidState var35 = var1.func_204610_c(var33);

                  for(var37 = 0; var37 < var30.length && !var35.func_206889_d(); ++var37) {
                     IFluidState var39 = var1.func_204610_c(var33.func_177972_a(var30[var37]));
                     if (var39.func_206885_f() > var35.func_206885_f() || var39.func_206889_d() && !var35.func_206889_d()) {
                        var35 = var39;
                     }
                  }

                  if (var35.func_206889_d()) {
                     IBlockState var38 = var1.func_180495_p(var33);
                     if (var38.func_177230_c() instanceof ILiquidContainer) {
                        ((ILiquidContainer)var38.func_177230_c()).func_204509_a(var1, var33, var38, var35);
                        var29 = true;
                        var31.remove();
                     }
                  }
               }
            }

            if (var11 <= var14) {
               VoxelShapePartBitSet var32 = new VoxelShapePartBitSet(var14 - var11 + 1, var15 - var12 + 1, var16 - var13 + 1);
               int var34 = var11;
               int var36 = var12;
               var37 = var13;
               Iterator var40 = var10.iterator();

               Pair var41;
               BlockPos var42;
               while(var40.hasNext()) {
                  var41 = (Pair)var40.next();
                  var42 = (BlockPos)var41.getFirst();
                  var32.func_199625_a(var42.func_177958_n() - var34, var42.func_177956_o() - var36, var42.func_177952_p() - var37, true, true);
               }

               var32.func_211540_a((var5x, var6x, var7x, var8x) -> {
                  BlockPos var9 = new BlockPos(var34 + var6x, var36 + var7x, var37 + var8x);
                  BlockPos var10 = var9.func_177972_a(var5x);
                  IBlockState var11 = var1.func_180495_p(var9);
                  IBlockState var12 = var1.func_180495_p(var10);
                  IBlockState var13 = var11.func_196956_a(var5x, var12, var1, var9, var10);
                  if (var11 != var13) {
                     var1.func_180501_a(var9, var13, var5 & -2 | 16);
                  }

                  IBlockState var14 = var12.func_196956_a(var5x.func_176734_d(), var13, var1, var10, var9);
                  if (var12 != var14) {
                     var1.func_180501_a(var10, var14, var5 & -2 | 16);
                  }

               });
               var40 = var10.iterator();

               while(var40.hasNext()) {
                  var41 = (Pair)var40.next();
                  var42 = (BlockPos)var41.getFirst();
                  IBlockState var26 = var1.func_180495_p(var42);
                  IBlockState var27 = Block.func_199770_b(var26, var1, var42);
                  if (var26 != var27) {
                     var1.func_180501_a(var42, var27, var5 & -2 | 16);
                  }

                  var1.func_195592_c(var42, var27.func_177230_c());
                  if (var41.getSecond() != null) {
                     TileEntity var28 = var1.func_175625_s(var42);
                     if (var28 != null) {
                        var28.func_70296_d();
                     }
                  }
               }
            }

            if (!var4.func_186221_e()) {
               this.func_207668_a(var1, var2, var4.func_186212_b(), var4.func_186215_c(), var4.func_207664_d(), var8);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void func_207668_a(IWorld var1, BlockPos var2, Mirror var3, Rotation var4, BlockPos var5, @Nullable MutableBoundingBox var6) {
      Iterator var7 = this.field_186271_b.iterator();

      while(true) {
         Template.EntityInfo var8;
         BlockPos var9;
         do {
            if (!var7.hasNext()) {
               return;
            }

            var8 = (Template.EntityInfo)var7.next();
            var9 = func_207669_a(var8.field_186248_b, var3, var4, var5).func_177971_a(var2);
         } while(var6 != null && !var6.func_175898_b(var9));

         NBTTagCompound var10 = var8.field_186249_c;
         Vec3d var11 = func_207667_a(var8.field_186247_a, var3, var4, var5);
         Vec3d var12 = var11.func_72441_c((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p());
         NBTTagList var13 = new NBTTagList();
         var13.add((INBTBase)(new NBTTagDouble(var12.field_72450_a)));
         var13.add((INBTBase)(new NBTTagDouble(var12.field_72448_b)));
         var13.add((INBTBase)(new NBTTagDouble(var12.field_72449_c)));
         var10.func_74782_a("Pos", var13);
         var10.func_186854_a("UUID", UUID.randomUUID());

         Entity var14;
         try {
            var14 = EntityType.func_200716_a(var10, var1.func_201672_e());
         } catch (Exception var16) {
            var14 = null;
         }

         if (var14 != null) {
            float var15 = var14.func_184217_a(var3);
            var15 += var14.field_70177_z - var14.func_184229_a(var4);
            var14.func_70012_b(var12.field_72450_a, var12.field_72448_b, var12.field_72449_c, var15, var14.field_70125_A);
            var1.func_72838_d(var14);
         }
      }
   }

   public BlockPos func_186257_a(Rotation var1) {
      switch(var1) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.field_186272_c.func_177952_p(), this.field_186272_c.func_177956_o(), this.field_186272_c.func_177958_n());
      default:
         return this.field_186272_c;
      }
   }

   public static BlockPos func_207669_a(BlockPos var0, Mirror var1, Rotation var2, BlockPos var3) {
      int var4 = var0.func_177958_n();
      int var5 = var0.func_177956_o();
      int var6 = var0.func_177952_p();
      boolean var7 = true;
      switch(var1) {
      case LEFT_RIGHT:
         var6 = -var6;
         break;
      case FRONT_BACK:
         var4 = -var4;
         break;
      default:
         var7 = false;
      }

      int var8 = var3.func_177958_n();
      int var9 = var3.func_177952_p();
      switch(var2) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(var8 - var9 + var6, var5, var8 + var9 - var4);
      case CLOCKWISE_90:
         return new BlockPos(var8 + var9 - var6, var5, var9 - var8 + var4);
      case CLOCKWISE_180:
         return new BlockPos(var8 + var8 - var4, var5, var9 + var9 - var6);
      default:
         return var7 ? new BlockPos(var4, var5, var6) : var0;
      }
   }

   private static Vec3d func_207667_a(Vec3d var0, Mirror var1, Rotation var2, BlockPos var3) {
      double var4 = var0.field_72450_a;
      double var6 = var0.field_72448_b;
      double var8 = var0.field_72449_c;
      boolean var10 = true;
      switch(var1) {
      case LEFT_RIGHT:
         var8 = 1.0D - var8;
         break;
      case FRONT_BACK:
         var4 = 1.0D - var4;
         break;
      default:
         var10 = false;
      }

      int var11 = var3.func_177958_n();
      int var12 = var3.func_177952_p();
      switch(var2) {
      case COUNTERCLOCKWISE_90:
         return new Vec3d((double)(var11 - var12) + var8, var6, (double)(var11 + var12 + 1) - var4);
      case CLOCKWISE_90:
         return new Vec3d((double)(var11 + var12 + 1) - var8, var6, (double)(var12 - var11) + var4);
      case CLOCKWISE_180:
         return new Vec3d((double)(var11 + var11 + 1) - var4, var6, (double)(var12 + var12 + 1) - var8);
      default:
         return var10 ? new Vec3d(var4, var6, var8) : var0;
      }
   }

   public BlockPos func_189961_a(BlockPos var1, Mirror var2, Rotation var3) {
      return func_191157_a(var1, var2, var3, this.func_186259_a().func_177958_n(), this.func_186259_a().func_177952_p());
   }

   public static BlockPos func_191157_a(BlockPos var0, Mirror var1, Rotation var2, int var3, int var4) {
      --var3;
      --var4;
      int var5 = var1 == Mirror.FRONT_BACK ? var3 : 0;
      int var6 = var1 == Mirror.LEFT_RIGHT ? var4 : 0;
      BlockPos var7 = var0;
      switch(var2) {
      case COUNTERCLOCKWISE_90:
         var7 = var0.func_177982_a(var6, 0, var3 - var5);
         break;
      case CLOCKWISE_90:
         var7 = var0.func_177982_a(var4 - var6, 0, var5);
         break;
      case CLOCKWISE_180:
         var7 = var0.func_177982_a(var3 - var5, 0, var4 - var6);
         break;
      case NONE:
         var7 = var0.func_177982_a(var5, 0, var6);
      }

      return var7;
   }

   public NBTTagCompound func_189552_a(NBTTagCompound var1) {
      if (this.field_204769_a.isEmpty()) {
         var1.func_74782_a("blocks", new NBTTagList());
         var1.func_74782_a("palette", new NBTTagList());
      } else {
         ArrayList var2 = Lists.newArrayList();
         Template.BasicPalette var3 = new Template.BasicPalette();
         var2.add(var3);

         for(int var4 = 1; var4 < this.field_204769_a.size(); ++var4) {
            var2.add(new Template.BasicPalette());
         }

         NBTTagList var14 = new NBTTagList();
         List var5 = (List)this.field_204769_a.get(0);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            Template.BlockInfo var7 = (Template.BlockInfo)var5.get(var6);
            NBTTagCompound var8 = new NBTTagCompound();
            var8.func_74782_a("pos", this.func_186267_a(var7.field_186242_a.func_177958_n(), var7.field_186242_a.func_177956_o(), var7.field_186242_a.func_177952_p()));
            int var9 = var3.func_189954_a(var7.field_186243_b);
            var8.func_74768_a("state", var9);
            if (var7.field_186244_c != null) {
               var8.func_74782_a("nbt", var7.field_186244_c);
            }

            var14.add((INBTBase)var8);

            for(int var10 = 1; var10 < this.field_204769_a.size(); ++var10) {
               Template.BasicPalette var11 = (Template.BasicPalette)var2.get(var10);
               var11.func_189956_a(((Template.BlockInfo)((List)this.field_204769_a.get(var6)).get(var6)).field_186243_b, var9);
            }
         }

         var1.func_74782_a("blocks", var14);
         NBTTagList var17;
         Iterator var18;
         if (var2.size() == 1) {
            var17 = new NBTTagList();
            var18 = var3.iterator();

            while(var18.hasNext()) {
               IBlockState var19 = (IBlockState)var18.next();
               var17.add((INBTBase)NBTUtil.func_190009_a(var19));
            }

            var1.func_74782_a("palette", var17);
         } else {
            var17 = new NBTTagList();
            var18 = var2.iterator();

            while(var18.hasNext()) {
               Template.BasicPalette var20 = (Template.BasicPalette)var18.next();
               NBTTagList var21 = new NBTTagList();
               Iterator var22 = var20.iterator();

               while(var22.hasNext()) {
                  IBlockState var23 = (IBlockState)var22.next();
                  var21.add((INBTBase)NBTUtil.func_190009_a(var23));
               }

               var17.add((INBTBase)var21);
            }

            var1.func_74782_a("palettes", var17);
         }
      }

      NBTTagList var12 = new NBTTagList();

      NBTTagCompound var16;
      for(Iterator var13 = this.field_186271_b.iterator(); var13.hasNext(); var12.add((INBTBase)var16)) {
         Template.EntityInfo var15 = (Template.EntityInfo)var13.next();
         var16 = new NBTTagCompound();
         var16.func_74782_a("pos", this.func_186264_a(var15.field_186247_a.field_72450_a, var15.field_186247_a.field_72448_b, var15.field_186247_a.field_72449_c));
         var16.func_74782_a("blockPos", this.func_186267_a(var15.field_186248_b.func_177958_n(), var15.field_186248_b.func_177956_o(), var15.field_186248_b.func_177952_p()));
         if (var15.field_186249_c != null) {
            var16.func_74782_a("nbt", var15.field_186249_c);
         }
      }

      var1.func_74782_a("entities", var12);
      var1.func_74782_a("size", this.func_186267_a(this.field_186272_c.func_177958_n(), this.field_186272_c.func_177956_o(), this.field_186272_c.func_177952_p()));
      var1.func_74768_a("DataVersion", 1631);
      return var1;
   }

   public void func_186256_b(NBTTagCompound var1) {
      this.field_204769_a.clear();
      this.field_186271_b.clear();
      NBTTagList var2 = var1.func_150295_c("size", 3);
      this.field_186272_c = new BlockPos(var2.func_186858_c(0), var2.func_186858_c(1), var2.func_186858_c(2));
      NBTTagList var3 = var1.func_150295_c("blocks", 10);
      NBTTagList var4;
      int var5;
      if (var1.func_150297_b("palettes", 9)) {
         var4 = var1.func_150295_c("palettes", 9);

         for(var5 = 0; var5 < var4.size(); ++var5) {
            this.func_204768_a(var4.func_202169_e(var5), var3);
         }
      } else {
         this.func_204768_a(var1.func_150295_c("palette", 10), var3);
      }

      var4 = var1.func_150295_c("entities", 10);

      for(var5 = 0; var5 < var4.size(); ++var5) {
         NBTTagCompound var6 = var4.func_150305_b(var5);
         NBTTagList var7 = var6.func_150295_c("pos", 6);
         Vec3d var8 = new Vec3d(var7.func_150309_d(0), var7.func_150309_d(1), var7.func_150309_d(2));
         NBTTagList var9 = var6.func_150295_c("blockPos", 3);
         BlockPos var10 = new BlockPos(var9.func_186858_c(0), var9.func_186858_c(1), var9.func_186858_c(2));
         if (var6.func_74764_b("nbt")) {
            NBTTagCompound var11 = var6.func_74775_l("nbt");
            this.field_186271_b.add(new Template.EntityInfo(var8, var10, var11));
         }
      }

   }

   private void func_204768_a(NBTTagList var1, NBTTagList var2) {
      Template.BasicPalette var3 = new Template.BasicPalette();
      ArrayList var4 = Lists.newArrayList();

      int var5;
      for(var5 = 0; var5 < var1.size(); ++var5) {
         var3.func_189956_a(NBTUtil.func_190008_d(var1.func_150305_b(var5)), var5);
      }

      for(var5 = 0; var5 < var2.size(); ++var5) {
         NBTTagCompound var6 = var2.func_150305_b(var5);
         NBTTagList var7 = var6.func_150295_c("pos", 3);
         BlockPos var8 = new BlockPos(var7.func_186858_c(0), var7.func_186858_c(1), var7.func_186858_c(2));
         IBlockState var9 = var3.func_189955_a(var6.func_74762_e("state"));
         NBTTagCompound var10;
         if (var6.func_74764_b("nbt")) {
            var10 = var6.func_74775_l("nbt");
         } else {
            var10 = null;
         }

         var4.add(new Template.BlockInfo(var8, var9, var10));
      }

      this.field_204769_a.add(var4);
   }

   private NBTTagList func_186267_a(int... var1) {
      NBTTagList var2 = new NBTTagList();
      int[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         var2.add((INBTBase)(new NBTTagInt(var6)));
      }

      return var2;
   }

   private NBTTagList func_186264_a(double... var1) {
      NBTTagList var2 = new NBTTagList();
      double[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.add((INBTBase)(new NBTTagDouble(var6)));
      }

      return var2;
   }

   public static class EntityInfo {
      public final Vec3d field_186247_a;
      public final BlockPos field_186248_b;
      public final NBTTagCompound field_186249_c;

      public EntityInfo(Vec3d var1, BlockPos var2, NBTTagCompound var3) {
         super();
         this.field_186247_a = var1;
         this.field_186248_b = var2;
         this.field_186249_c = var3;
      }
   }

   public static class BlockInfo {
      public final BlockPos field_186242_a;
      public final IBlockState field_186243_b;
      public final NBTTagCompound field_186244_c;

      public BlockInfo(BlockPos var1, IBlockState var2, @Nullable NBTTagCompound var3) {
         super();
         this.field_186242_a = var1;
         this.field_186243_b = var2;
         this.field_186244_c = var3;
      }
   }

   static class BasicPalette implements Iterable<IBlockState> {
      public static final IBlockState field_189957_a;
      private final ObjectIntIdentityMap<IBlockState> field_189958_b;
      private int field_189959_c;

      private BasicPalette() {
         super();
         this.field_189958_b = new ObjectIntIdentityMap(16);
      }

      public int func_189954_a(IBlockState var1) {
         int var2 = this.field_189958_b.func_148747_b(var1);
         if (var2 == -1) {
            var2 = this.field_189959_c++;
            this.field_189958_b.func_148746_a(var1, var2);
         }

         return var2;
      }

      @Nullable
      public IBlockState func_189955_a(int var1) {
         IBlockState var2 = (IBlockState)this.field_189958_b.func_148745_a(var1);
         return var2 == null ? field_189957_a : var2;
      }

      public Iterator<IBlockState> iterator() {
         return this.field_189958_b.iterator();
      }

      public void func_189956_a(IBlockState var1, int var2) {
         this.field_189958_b.func_148746_a(var1, var2);
      }

      // $FF: synthetic method
      BasicPalette(Object var1) {
         this();
      }

      static {
         field_189957_a = Blocks.field_150350_a.func_176223_P();
      }
   }
}
