package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureVillagePieces$House2 extends StructureVillagePieces$Village {
   private static final WeightedRandomChestContent[] field_74918_a = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3),
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10),
      new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5),
      new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151040_l, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151030_Z, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151028_Y, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151165_aa, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151167_ab, 0, 1, 1, 5),
      new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150343_Z), 0, 3, 7, 5),
      new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150345_g), 0, 3, 7, 5),
      new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)
   };
   private boolean field_74917_c;

   public StructureVillagePieces$House2() {
      super();
   }

   public StructureVillagePieces$House2(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
   }

   public static StructureVillagePieces$House2 func_74915_a(
      StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 10, 6, 7, var6);
      return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null
         ? new StructureVillagePieces$House2(var0, var7, var2, var8, var6)
         : null;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Chest", this.field_74917_c);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74917_c = var1.func_74767_n("Chest");
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.field_143015_k < 0) {
         this.field_143015_k = this.func_74889_b(var1, var3);
         if (this.field_143015_k < 0) {
            return true;
         }

         this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
      }

      this.func_151549_a(var1, var3, 0, 1, 0, 9, 4, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 0, 0, 9, 0, 6, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 4, 0, 9, 4, 6, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 5, 0, 9, 5, 6, Blocks.field_150333_U, Blocks.field_150333_U, false);
      this.func_151549_a(var1, var3, 1, 5, 1, 8, 5, 5, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 1, 1, 0, 2, 3, 0, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 0, 1, 0, 0, 4, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 3, 1, 0, 3, 4, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 0, 1, 6, 0, 4, 6, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 3, 3, 1, var3);
      this.func_151549_a(var1, var3, 3, 1, 2, 3, 3, 2, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 4, 1, 3, 5, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 0, 1, 1, 0, 3, 5, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 1, 1, 6, 5, 3, 6, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 5, 1, 0, 5, 3, 0, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
      this.func_151549_a(var1, var3, 9, 1, 0, 9, 3, 0, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
      this.func_151549_a(var1, var3, 6, 1, 4, 9, 4, 6, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151550_a(var1, Blocks.field_150356_k, 0, 7, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150356_k, 0, 8, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150411_aY, 0, 9, 2, 5, var3);
      this.func_151550_a(var1, Blocks.field_150411_aY, 0, 9, 2, 4, var3);
      this.func_151549_a(var1, var3, 7, 2, 4, 8, 2, 5, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 6, 1, 3, var3);
      this.func_151550_a(var1, Blocks.field_150460_al, 0, 6, 2, 3, var3);
      this.func_151550_a(var1, Blocks.field_150460_al, 0, 6, 3, 3, var3);
      this.func_151550_a(var1, Blocks.field_150334_T, 0, 8, 1, 1, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 2, 6, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 2, 6, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 2, 1, 4, var3);
      this.func_151550_a(var1, Blocks.field_150452_aw, 0, 2, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 1, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150476_ad, this.func_151555_a(Blocks.field_150476_ad, 3), 2, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150476_ad, this.func_151555_a(Blocks.field_150476_ad, 1), 1, 1, 4, var3);
      if (!this.field_74917_c) {
         int var4 = this.func_74862_a(1);
         int var5 = this.func_74865_a(5, 5);
         int var6 = this.func_74873_b(5, 5);
         if (var3.func_78890_b(var5, var4, var6)) {
            this.field_74917_c = true;
            this.func_74879_a(var1, var3, var2, 5, 1, 5, field_74918_a, 3 + var2.nextInt(6));
         }
      }

      for(int var7 = 6; var7 <= 8; ++var7) {
         if (this.func_151548_a(var1, var7, 0, -1, var3).func_149688_o() == Material.field_151579_a
            && this.func_151548_a(var1, var7, -1, -1, var3).func_149688_o() != Material.field_151579_a) {
            this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), var7, 0, -1, var3);
         }
      }

      for(int var8 = 0; var8 < 7; ++var8) {
         for(int var9 = 0; var9 < 10; ++var9) {
            this.func_74871_b(var1, var9, 6, var8, var3);
            this.func_151554_b(var1, Blocks.field_150347_e, 0, var9, -1, var8, var3);
         }
      }

      this.func_74893_a(var1, var3, 7, 1, 1, 1);
      return true;
   }

   @Override
   protected int func_74888_b(int var1) {
      return 3;
   }
}
