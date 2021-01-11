package net.minecraft.block;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public abstract class BlockFlower extends BlockBush {
   protected PropertyEnum<BlockFlower.EnumFlowerType> field_176496_a;

   protected BlockFlower() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(this.func_176494_l(), this.func_176495_j() == BlockFlower.EnumFlowerColor.RED ? BlockFlower.EnumFlowerType.POPPY : BlockFlower.EnumFlowerType.DANDELION));
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockFlower.EnumFlowerType)var1.func_177229_b(this.func_176494_l())).func_176968_b();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockFlower.EnumFlowerType[] var4 = BlockFlower.EnumFlowerType.func_176966_a(this.func_176495_j());
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockFlower.EnumFlowerType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176968_b()));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(this.func_176494_l(), BlockFlower.EnumFlowerType.func_176967_a(this.func_176495_j(), var1));
   }

   public abstract BlockFlower.EnumFlowerColor func_176495_j();

   public IProperty<BlockFlower.EnumFlowerType> func_176494_l() {
      if (this.field_176496_a == null) {
         this.field_176496_a = PropertyEnum.func_177708_a("type", BlockFlower.EnumFlowerType.class, new Predicate<BlockFlower.EnumFlowerType>() {
            public boolean apply(BlockFlower.EnumFlowerType var1) {
               return var1.func_176964_a() == BlockFlower.this.func_176495_j();
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((BlockFlower.EnumFlowerType)var1);
            }
         });
      }

      return this.field_176496_a;
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockFlower.EnumFlowerType)var1.func_177229_b(this.func_176494_l())).func_176968_b();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{this.func_176494_l()});
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.XZ;
   }

   public static enum EnumFlowerType implements IStringSerializable {
      DANDELION(BlockFlower.EnumFlowerColor.YELLOW, 0, "dandelion"),
      POPPY(BlockFlower.EnumFlowerColor.RED, 0, "poppy"),
      BLUE_ORCHID(BlockFlower.EnumFlowerColor.RED, 1, "blue_orchid", "blueOrchid"),
      ALLIUM(BlockFlower.EnumFlowerColor.RED, 2, "allium"),
      HOUSTONIA(BlockFlower.EnumFlowerColor.RED, 3, "houstonia"),
      RED_TULIP(BlockFlower.EnumFlowerColor.RED, 4, "red_tulip", "tulipRed"),
      ORANGE_TULIP(BlockFlower.EnumFlowerColor.RED, 5, "orange_tulip", "tulipOrange"),
      WHITE_TULIP(BlockFlower.EnumFlowerColor.RED, 6, "white_tulip", "tulipWhite"),
      PINK_TULIP(BlockFlower.EnumFlowerColor.RED, 7, "pink_tulip", "tulipPink"),
      OXEYE_DAISY(BlockFlower.EnumFlowerColor.RED, 8, "oxeye_daisy", "oxeyeDaisy");

      private static final BlockFlower.EnumFlowerType[][] field_176981_k = new BlockFlower.EnumFlowerType[BlockFlower.EnumFlowerColor.values().length][];
      private final BlockFlower.EnumFlowerColor field_176978_l;
      private final int field_176979_m;
      private final String field_176976_n;
      private final String field_176977_o;

      private EnumFlowerType(BlockFlower.EnumFlowerColor var3, int var4, String var5) {
         this(var3, var4, var5, var5);
      }

      private EnumFlowerType(BlockFlower.EnumFlowerColor var3, int var4, String var5, String var6) {
         this.field_176978_l = var3;
         this.field_176979_m = var4;
         this.field_176976_n = var5;
         this.field_176977_o = var6;
      }

      public BlockFlower.EnumFlowerColor func_176964_a() {
         return this.field_176978_l;
      }

      public int func_176968_b() {
         return this.field_176979_m;
      }

      public static BlockFlower.EnumFlowerType func_176967_a(BlockFlower.EnumFlowerColor var0, int var1) {
         BlockFlower.EnumFlowerType[] var2 = field_176981_k[var0.ordinal()];
         if (var1 < 0 || var1 >= var2.length) {
            var1 = 0;
         }

         return var2[var1];
      }

      public static BlockFlower.EnumFlowerType[] func_176966_a(BlockFlower.EnumFlowerColor var0) {
         return field_176981_k[var0.ordinal()];
      }

      public String toString() {
         return this.field_176976_n;
      }

      public String func_176610_l() {
         return this.field_176976_n;
      }

      public String func_176963_d() {
         return this.field_176977_o;
      }

      static {
         BlockFlower.EnumFlowerColor[] var0 = BlockFlower.EnumFlowerColor.values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            final BlockFlower.EnumFlowerColor var3 = var0[var2];
            Collection var4 = Collections2.filter(Lists.newArrayList(values()), new Predicate<BlockFlower.EnumFlowerType>() {
               public boolean apply(BlockFlower.EnumFlowerType var1) {
                  return var1.func_176964_a() == var3;
               }

               // $FF: synthetic method
               public boolean apply(Object var1) {
                  return this.apply((BlockFlower.EnumFlowerType)var1);
               }
            });
            field_176981_k[var3.ordinal()] = (BlockFlower.EnumFlowerType[])var4.toArray(new BlockFlower.EnumFlowerType[var4.size()]);
         }

      }
   }

   public static enum EnumFlowerColor {
      YELLOW,
      RED;

      private EnumFlowerColor() {
      }

      public BlockFlower func_180346_a() {
         return this == YELLOW ? Blocks.field_150327_N : Blocks.field_150328_O;
      }
   }
}
