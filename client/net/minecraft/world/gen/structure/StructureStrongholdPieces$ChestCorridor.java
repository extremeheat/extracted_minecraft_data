package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces$ChestCorridor extends StructureStrongholdPieces$Stronghold {
   private static final WeightedRandomChestContent[] field_75003_a = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151079_bi, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3),
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10),
      new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5),
      new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5),
      new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151040_l, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151030_Z, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151028_Y, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151165_aa, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151167_ab, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151153_ao, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)
   };
   private boolean field_75002_c;

   public StructureStrongholdPieces$ChestCorridor() {
      super();
   }

   public StructureStrongholdPieces$ChestCorridor(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Chest", this.field_75002_c);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_75002_c = var1.func_74767_n("Chest");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
   }

   public static StructureStrongholdPieces$ChestCorridor func_75000_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureStrongholdPieces$ChestCorridor(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.func_74990_a(var1, var2, var3, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 6);
         this.func_151549_a(var1, var3, 3, 1, 2, 3, 1, 4, Blocks.field_150417_aV, Blocks.field_150417_aV, false);
         this.func_151550_a(var1, Blocks.field_150333_U, 5, 3, 1, 1, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 5, 3, 1, 5, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 5, 3, 2, 2, var3);
         this.func_151550_a(var1, Blocks.field_150333_U, 5, 3, 2, 4, var3);

         for(int var4 = 2; var4 <= 4; ++var4) {
            this.func_151550_a(var1, Blocks.field_150333_U, 5, 2, 1, var4, var3);
         }

         if (!this.field_75002_c) {
            int var7 = this.func_74862_a(2);
            int var5 = this.func_74865_a(3, 3);
            int var6 = this.func_74873_b(3, 3);
            if (var3.func_78890_b(var5, var7, var6)) {
               this.field_75002_c = true;
               this.func_74879_a(
                  var1,
                  var3,
                  var2,
                  3,
                  2,
                  3,
                  WeightedRandomChestContent.func_92080_a(field_75003_a, Items.field_151134_bR.func_92114_b(var2)),
                  2 + var2.nextInt(2)
               );
            }
         }

         return true;
      }
   }
}
