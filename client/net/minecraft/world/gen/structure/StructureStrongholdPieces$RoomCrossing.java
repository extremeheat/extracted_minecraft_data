package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces$RoomCrossing extends StructureStrongholdPieces$Stronghold {
   private static final WeightedRandomChestContent[] field_75014_c = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10),
      new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5),
      new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5),
      new WeightedRandomChestContent(Items.field_151044_h, 0, 3, 8, 10),
      new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 1)
   };
   protected int field_75013_b;

   public StructureStrongholdPieces$RoomCrossing() {
      super();
   }

   public StructureStrongholdPieces$RoomCrossing(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
      this.field_75013_b = var2.nextInt(5);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74768_a("Type", this.field_75013_b);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_75013_b = var1.func_74762_e("Type");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 4, 1);
      this.func_74989_b((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 4);
      this.func_74987_c((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 4);
   }

   public static StructureStrongholdPieces$RoomCrossing func_75012_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -4, -1, 0, 11, 7, 11, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureStrongholdPieces$RoomCrossing(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 10, 6, 10, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 1, 0);
         this.func_151549_a(var1, var3, 4, 1, 10, 6, 3, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
         this.func_151549_a(var1, var3, 0, 1, 4, 0, 3, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
         this.func_151549_a(var1, var3, 10, 1, 4, 10, 3, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
         switch(this.field_75013_b) {
            case 0:
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 5, 1, 5, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 5, 2, 5, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 5, 3, 5, var3);
               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 4, 3, 5, var3);
               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 6, 3, 5, var3);
               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 5, 3, 4, var3);
               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 5, 3, 6, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 4, 1, 4, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 4, 1, 5, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 4, 1, 6, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 6, 1, 4, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 6, 1, 5, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 6, 1, 6, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 5, 1, 4, var3);
               this.func_151550_a(var1, Blocks.field_150333_U, 0, 5, 1, 6, var3);
               break;
            case 1:
               for(int var8 = 0; var8 < 5; ++var8) {
                  this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 1, 3 + var8, var3);
                  this.func_151550_a(var1, Blocks.field_150417_aV, 0, 7, 1, 3 + var8, var3);
                  this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3 + var8, 1, 3, var3);
                  this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3 + var8, 1, 7, var3);
               }

               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 5, 1, 5, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 5, 2, 5, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 5, 3, 5, var3);
               this.func_151550_a(var1, Blocks.field_150358_i, 0, 5, 4, 5, var3);
               break;
            case 2:
               for(int var4 = 1; var4 <= 9; ++var4) {
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, 1, 3, var4, var3);
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, 9, 3, var4, var3);
               }

               for(int var5 = 1; var5 <= 9; ++var5) {
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, var5, 3, 1, var3);
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, var5, 3, 9, var3);
               }

               this.func_151550_a(var1, Blocks.field_150347_e, 0, 5, 1, 4, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 5, 1, 6, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 5, 3, 4, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 5, 3, 6, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 1, 5, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 6, 1, 5, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 3, 5, var3);
               this.func_151550_a(var1, Blocks.field_150347_e, 0, 6, 3, 5, var3);

               for(int var6 = 1; var6 <= 3; ++var6) {
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, var6, 4, var3);
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, 6, var6, 4, var3);
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, var6, 6, var3);
                  this.func_151550_a(var1, Blocks.field_150347_e, 0, 6, var6, 6, var3);
               }

               this.func_151550_a(var1, Blocks.field_150478_aa, 0, 5, 3, 5, var3);

               for(int var7 = 2; var7 <= 8; ++var7) {
                  this.func_151550_a(var1, Blocks.field_150344_f, 0, 2, 3, var7, var3);
                  this.func_151550_a(var1, Blocks.field_150344_f, 0, 3, 3, var7, var3);
                  if (var7 <= 3 || var7 >= 7) {
                     this.func_151550_a(var1, Blocks.field_150344_f, 0, 4, 3, var7, var3);
                     this.func_151550_a(var1, Blocks.field_150344_f, 0, 5, 3, var7, var3);
                     this.func_151550_a(var1, Blocks.field_150344_f, 0, 6, 3, var7, var3);
                  }

                  this.func_151550_a(var1, Blocks.field_150344_f, 0, 7, 3, var7, var3);
                  this.func_151550_a(var1, Blocks.field_150344_f, 0, 8, 3, var7, var3);
               }

               this.func_151550_a(var1, Blocks.field_150468_ap, this.func_151555_a(Blocks.field_150468_ap, 4), 9, 1, 3, var3);
               this.func_151550_a(var1, Blocks.field_150468_ap, this.func_151555_a(Blocks.field_150468_ap, 4), 9, 2, 3, var3);
               this.func_151550_a(var1, Blocks.field_150468_ap, this.func_151555_a(Blocks.field_150468_ap, 4), 9, 3, 3, var3);
               this.func_74879_a(
                  var1,
                  var3,
                  var2,
                  3,
                  4,
                  8,
                  WeightedRandomChestContent.func_92080_a(field_75014_c, Items.field_151134_bR.func_92114_b(var2)),
                  1 + var2.nextInt(4)
               );
         }

         return true;
      }
   }
}
