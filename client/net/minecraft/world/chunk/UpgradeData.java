package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStemGrown;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumDirection8;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger field_209162_b = LogManager.getLogger();
   public static final UpgradeData field_196994_a = new UpgradeData();
   private static final EnumDirection8[] field_208832_b = EnumDirection8.values();
   private final EnumSet<EnumDirection8> field_196995_b;
   private final int[][] field_196996_c;
   private static final Map<Block, UpgradeData.IBlockFixer> field_196997_d = new IdentityHashMap();
   private static final Set<UpgradeData.IBlockFixer> field_208833_f = Sets.newHashSet();

   private UpgradeData() {
      super();
      this.field_196995_b = EnumSet.noneOf(EnumDirection8.class);
      this.field_196996_c = new int[16][];
   }

   public UpgradeData(NBTTagCompound var1) {
      this();
      if (var1.func_150297_b("Indices", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("Indices");

         for(int var3 = 0; var3 < this.field_196996_c.length; ++var3) {
            String var4 = String.valueOf(var3);
            if (var2.func_150297_b(var4, 11)) {
               this.field_196996_c[var3] = var2.func_74759_k(var4);
            }
         }
      }

      int var7 = var1.func_74762_e("Sides");
      EnumDirection8[] var8 = EnumDirection8.values();
      int var9 = var8.length;

      for(int var5 = 0; var5 < var9; ++var5) {
         EnumDirection8 var6 = var8[var5];
         if ((var7 & 1 << var6.ordinal()) != 0) {
            this.field_196995_b.add(var6);
         }
      }

   }

   public void func_196990_a(Chunk var1) {
      this.func_196989_a(var1);
      EnumDirection8[] var2 = field_208832_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumDirection8 var5 = var2[var4];
         func_196991_a(var1, var5);
      }

      World var6 = var1.func_177412_p();
      field_208833_f.forEach((var1x) -> {
         var1x.func_208826_a(var6);
      });
   }

   private static void func_196991_a(Chunk var0, EnumDirection8 var1) {
      World var2 = var0.func_177412_p();
      if (var0.func_196966_y().field_196995_b.remove(var1)) {
         Set var3 = var1.func_197532_a();
         boolean var4 = false;
         boolean var5 = true;
         boolean var6 = var3.contains(EnumFacing.EAST);
         boolean var7 = var3.contains(EnumFacing.WEST);
         boolean var8 = var3.contains(EnumFacing.SOUTH);
         boolean var9 = var3.contains(EnumFacing.NORTH);
         boolean var10 = var3.size() == 1;
         int var11 = (var0.field_76635_g << 4) + (var10 && (var9 || var8) ? 1 : (var7 ? 0 : 15));
         int var12 = (var0.field_76635_g << 4) + (!var10 || !var9 && !var8 ? (var7 ? 0 : 15) : 14);
         int var13 = (var0.field_76647_h << 4) + (!var10 || !var6 && !var7 ? (var9 ? 0 : 15) : 1);
         int var14 = (var0.field_76647_h << 4) + (var10 && (var6 || var7) ? 14 : (var9 ? 0 : 15));
         EnumFacing[] var15 = EnumFacing.values();
         BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();
         Iterator var17 = BlockPos.func_191531_b(var11, 0, var13, var12, var2.func_72800_K() - 1, var14).iterator();

         while(var17.hasNext()) {
            BlockPos.MutableBlockPos var18 = (BlockPos.MutableBlockPos)var17.next();
            IBlockState var19 = var2.func_180495_p(var18);
            IBlockState var20 = var19;
            EnumFacing[] var21 = var15;
            int var22 = var15.length;

            for(int var23 = 0; var23 < var22; ++var23) {
               EnumFacing var24 = var21[var23];
               var16.func_189533_g(var18).func_189536_c(var24);
               var20 = func_196987_a(var20, var24, var2, var18, var16);
            }

            Block.func_196263_a(var19, var20, var2, var18, 18);
         }

      }
   }

   private static IBlockState func_196987_a(IBlockState var0, EnumFacing var1, IWorld var2, BlockPos.MutableBlockPos var3, BlockPos.MutableBlockPos var4) {
      return ((UpgradeData.IBlockFixer)field_196997_d.getOrDefault(var0.func_177230_c(), UpgradeData.BlockFixers.DEFAULT)).func_196982_a(var0, var1, var2.func_180495_p(var4), var2, var3, var4);
   }

   private void func_196989_a(Chunk var1) {
      BlockPos.PooledMutableBlockPos var2 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var3 = null;

      try {
         BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var5 = null;

         try {
            World var6 = var1.func_177412_p();

            int var7;
            for(var7 = 0; var7 < 16; ++var7) {
               ChunkSection var8 = var1.func_76587_i()[var7];
               int[] var9 = this.field_196996_c[var7];
               this.field_196996_c[var7] = null;
               if (var8 != null && var9 != null && var9.length > 0) {
                  EnumFacing[] var10 = EnumFacing.values();
                  BlockStateContainer var11 = var8.func_186049_g();
                  int[] var12 = var9;
                  int var13 = var9.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     int var15 = var12[var14];
                     int var16 = var15 & 15;
                     int var17 = var15 >> 8 & 15;
                     int var18 = var15 >> 4 & 15;
                     var2.func_181079_c(var16 + (var1.field_76635_g << 4), var17 + (var7 << 4), var18 + (var1.field_76647_h << 4));
                     IBlockState var19 = (IBlockState)var11.func_186015_a(var15);
                     IBlockState var20 = var19;
                     EnumFacing[] var21 = var10;
                     int var22 = var10.length;

                     for(int var23 = 0; var23 < var22; ++var23) {
                        EnumFacing var24 = var21[var23];
                        var4.func_189533_g(var2).func_189536_c(var24);
                        if (var2.func_177958_n() >> 4 == var1.field_76635_g && var2.func_177952_p() >> 4 == var1.field_76647_h) {
                           var20 = func_196987_a(var20, var24, var6, var2, var4);
                        }
                     }

                     Block.func_196263_a(var19, var20, var6, var2, 18);
                  }
               }
            }

            for(var7 = 0; var7 < this.field_196996_c.length; ++var7) {
               if (this.field_196996_c[var7] != null) {
                  field_209162_b.warn("Discarding update data for section {} for chunk ({} {})", var7, var1.field_76635_g, var1.field_76647_h);
               }

               this.field_196996_c[var7] = null;
            }

         } catch (Throwable var46) {
            var5 = var46;
            throw var46;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var45) {
                     var5.addSuppressed(var45);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var48) {
         var3 = var48;
         throw var48;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var44) {
                  var3.addSuppressed(var44);
               }
            } else {
               var2.close();
            }
         }

      }
   }

   public boolean func_196988_a() {
      int[][] var1 = this.field_196996_c;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] var4 = var1[var3];
         if (var4 != null) {
            return false;
         }
      }

      return this.field_196995_b.isEmpty();
   }

   public NBTTagCompound func_196992_b() {
      NBTTagCompound var1 = new NBTTagCompound();
      NBTTagCompound var2 = new NBTTagCompound();

      int var3;
      for(var3 = 0; var3 < this.field_196996_c.length; ++var3) {
         String var4 = String.valueOf(var3);
         if (this.field_196996_c[var3] != null && this.field_196996_c[var3].length != 0) {
            var2.func_74783_a(var4, this.field_196996_c[var3]);
         }
      }

      if (!var2.isEmpty()) {
         var1.func_74782_a("Indices", var2);
      }

      var3 = 0;

      EnumDirection8 var5;
      for(Iterator var6 = this.field_196995_b.iterator(); var6.hasNext(); var3 |= 1 << var5.ordinal()) {
         var5 = (EnumDirection8)var6.next();
      }

      var1.func_74774_a("Sides", (byte)var3);
      return var1;
   }

   static enum BlockFixers implements UpgradeData.IBlockFixer {
      BLACKLIST(new Block[]{Blocks.field_190976_dk, Blocks.field_150427_aO, Blocks.field_196860_iS, Blocks.field_196862_iT, Blocks.field_196864_iU, Blocks.field_196866_iV, Blocks.field_196868_iW, Blocks.field_196870_iX, Blocks.field_196872_iY, Blocks.field_196874_iZ, Blocks.field_196877_ja, Blocks.field_196878_jb, Blocks.field_196879_jc, Blocks.field_196880_jd, Blocks.field_196881_je, Blocks.field_196882_jf, Blocks.field_196883_jg, Blocks.field_196884_jh, Blocks.field_150467_bQ, Blocks.field_196717_eY, Blocks.field_196718_eZ, Blocks.field_150380_bt, Blocks.field_150351_n, Blocks.field_150354_m, Blocks.field_196611_F, Blocks.field_196649_cc, Blocks.field_150444_as}) {
         public IBlockState func_196982_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
            return var1;
         }
      },
      DEFAULT(new Block[0]) {
         public IBlockState func_196982_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
            return var1.func_196956_a(var2, var4.func_180495_p(var6), var4, var5, var6);
         }
      },
      CHEST(new Block[]{Blocks.field_150486_ae, Blocks.field_150447_bR}) {
         public IBlockState func_196982_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
            if (var3.func_177230_c() == var1.func_177230_c() && var2.func_176740_k().func_176722_c() && var1.func_177229_b(BlockChest.field_196314_b) == ChestType.SINGLE && var3.func_177229_b(BlockChest.field_196314_b) == ChestType.SINGLE) {
               EnumFacing var7 = (EnumFacing)var1.func_177229_b(BlockChest.field_176459_a);
               if (var2.func_176740_k() != var7.func_176740_k() && var7 == var3.func_177229_b(BlockChest.field_176459_a)) {
                  ChestType var8 = var2 == var7.func_176746_e() ? ChestType.LEFT : ChestType.RIGHT;
                  var4.func_180501_a(var6, (IBlockState)var3.func_206870_a(BlockChest.field_196314_b, var8.func_208081_a()), 18);
                  if (var7 == EnumFacing.NORTH || var7 == EnumFacing.EAST) {
                     TileEntity var9 = var4.func_175625_s(var5);
                     TileEntity var10 = var4.func_175625_s(var6);
                     if (var9 instanceof TileEntityChest && var10 instanceof TileEntityChest) {
                        TileEntityChest.func_199722_a((TileEntityChest)var9, (TileEntityChest)var10);
                     }
                  }

                  return (IBlockState)var1.func_206870_a(BlockChest.field_196314_b, var8);
               }
            }

            return var1;
         }
      },
      LEAVES(true, new Block[]{Blocks.field_196572_aa, Blocks.field_196647_Y, Blocks.field_196574_ab, Blocks.field_196648_Z, Blocks.field_196642_W, Blocks.field_196645_X}) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> field_208828_g = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public IBlockState func_196982_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
            IBlockState var7 = var1.func_196956_a(var2, var4.func_180495_p(var6), var4, var5, var6);
            if (var1 != var7) {
               int var8 = (Integer)var7.func_177229_b(BlockStateProperties.field_208514_aa);
               List var9 = (List)this.field_208828_g.get();
               if (var9.isEmpty()) {
                  for(int var10 = 0; var10 < 7; ++var10) {
                     var9.add(new ObjectOpenHashSet());
                  }
               }

               ((ObjectSet)var9.get(var8)).add(var5.func_185334_h());
            }

            return var1;
         }

         public void func_208826_a(IWorld var1) {
            BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
            List var3 = (List)this.field_208828_g.get();

            label44:
            for(int var4 = 2; var4 < var3.size(); ++var4) {
               int var5 = var4 - 1;
               ObjectSet var6 = (ObjectSet)var3.get(var5);
               ObjectSet var7 = (ObjectSet)var3.get(var4);
               ObjectIterator var8 = var6.iterator();

               while(true) {
                  BlockPos var9;
                  IBlockState var10;
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label44;
                        }

                        var9 = (BlockPos)var8.next();
                        var10 = var1.func_180495_p(var9);
                     } while((Integer)var10.func_177229_b(BlockStateProperties.field_208514_aa) < var5);

                     var1.func_180501_a(var9, (IBlockState)var10.func_206870_a(BlockStateProperties.field_208514_aa, var5), 18);
                  } while(var4 == 7);

                  EnumFacing[] var11 = field_208827_f;
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     EnumFacing var14 = var11[var13];
                     var2.func_189533_g(var9).func_189536_c(var14);
                     IBlockState var15 = var1.func_180495_p(var2);
                     if (var15.func_196959_b(BlockStateProperties.field_208514_aa) && (Integer)var10.func_177229_b(BlockStateProperties.field_208514_aa) > var4) {
                        var7.add(var2.func_185334_h());
                     }
                  }
               }
            }

            var3.clear();
         }
      },
      STEM_BLOCK(new Block[]{Blocks.field_150394_bc, Blocks.field_150393_bb}) {
         public IBlockState func_196982_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
            if ((Integer)var1.func_177229_b(BlockStem.field_176484_a) == 7) {
               BlockStemGrown var7 = ((BlockStem)var1.func_177230_c()).func_208486_d();
               if (var3.func_177230_c() == var7) {
                  return (IBlockState)var7.func_196523_e().func_176223_P().func_206870_a(BlockHorizontal.field_185512_D, var2);
               }
            }

            return var1;
         }
      };

      public static final EnumFacing[] field_208827_f = EnumFacing.values();

      private BlockFixers(Block... var3) {
         this(false, var3);
      }

      private BlockFixers(boolean var3, Block... var4) {
         Block[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Block var8 = var5[var7];
            UpgradeData.field_196997_d.put(var8, this);
         }

         if (var3) {
            UpgradeData.field_208833_f.add(this);
         }

      }

      // $FF: synthetic method
      BlockFixers(Block[] var3, Object var4) {
         this(var3);
      }

      // $FF: synthetic method
      BlockFixers(boolean var3, Block[] var4, Object var5) {
         this(var3, var4);
      }
   }

   public interface IBlockFixer {
      IBlockState func_196982_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6);

      default void func_208826_a(IWorld var1) {
      }
   }
}
