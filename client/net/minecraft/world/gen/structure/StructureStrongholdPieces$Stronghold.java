package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class StructureStrongholdPieces$Stronghold extends StructureComponent {
   protected StructureStrongholdPieces$Stronghold$Door field_143013_d = StructureStrongholdPieces$Stronghold$Door.OPENING;

   public StructureStrongholdPieces$Stronghold() {
      super();
   }

   protected StructureStrongholdPieces$Stronghold(int var1) {
      super(var1);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74778_a("EntryDoor", this.field_143013_d.name());
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      this.field_143013_d = StructureStrongholdPieces$Stronghold$Door.valueOf(var1.func_74779_i("EntryDoor"));
   }

   protected void func_74990_a(
      World var1, Random var2, StructureBoundingBox var3, StructureStrongholdPieces$Stronghold$Door var4, int var5, int var6, int var7
   ) {
      switch(var4) {
         case OPENING:
         default:
            this.func_151549_a(var1, var3, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, Blocks.field_150350_a, Blocks.field_150350_a, false);
            break;
         case WOOD_DOOR:
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 1, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 2, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 2, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 2, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150466_ao, 0, var5 + 1, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150466_ao, 8, var5 + 1, var6 + 1, var7, var3);
            break;
         case GRATES:
            this.func_151550_a(var1, Blocks.field_150350_a, 0, var5 + 1, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150350_a, 0, var5 + 1, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5 + 1, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5 + 2, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5 + 2, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150411_aY, 0, var5 + 2, var6, var7, var3);
            break;
         case IRON_DOOR:
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 1, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 2, var6 + 2, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 2, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, var5 + 2, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150454_av, 0, var5 + 1, var6, var7, var3);
            this.func_151550_a(var1, Blocks.field_150454_av, 8, var5 + 1, var6 + 1, var7, var3);
            this.func_151550_a(var1, Blocks.field_150430_aB, this.func_151555_a(Blocks.field_150430_aB, 4), var5 + 2, var6 + 1, var7 + 1, var3);
            this.func_151550_a(var1, Blocks.field_150430_aB, this.func_151555_a(Blocks.field_150430_aB, 3), var5 + 2, var6 + 1, var7 - 1, var3);
      }
   }

   protected StructureStrongholdPieces$Stronghold$Door func_74988_a(Random var1) {
      int var2 = var1.nextInt(5);
      switch(var2) {
         case 0:
         case 1:
         default:
            return StructureStrongholdPieces$Stronghold$Door.OPENING;
         case 2:
            return StructureStrongholdPieces$Stronghold$Door.WOOD_DOOR;
         case 3:
            return StructureStrongholdPieces$Stronghold$Door.GRATES;
         case 4:
            return StructureStrongholdPieces$Stronghold$Door.IRON_DOOR;
      }
   }

   protected StructureComponent func_74986_a(StructureStrongholdPieces$Stairs2 var1, List var2, Random var3, int var4, int var5) {
      switch(this.field_74885_f) {
         case 0:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var4,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78892_f + 1,
               this.field_74885_f,
               this.func_74877_c()
            );
         case 1:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78896_c + var4,
               this.field_74885_f,
               this.func_74877_c()
            );
         case 2:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var4,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78896_c - 1,
               this.field_74885_f,
               this.func_74877_c()
            );
         case 3:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var5,
               this.field_74887_e.field_78896_c + var4,
               this.field_74885_f,
               this.func_74877_c()
            );
         default:
            return null;
      }
   }

   protected StructureComponent func_74989_b(StructureStrongholdPieces$Stairs2 var1, List var2, Random var3, int var4, int var5) {
      switch(this.field_74885_f) {
         case 0:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               1,
               this.func_74877_c()
            );
         case 1:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c - 1,
               2,
               this.func_74877_c()
            );
         case 2:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               1,
               this.func_74877_c()
            );
         case 3:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c - 1,
               2,
               this.func_74877_c()
            );
         default:
            return null;
      }
   }

   protected StructureComponent func_74987_c(StructureStrongholdPieces$Stairs2 var1, List var2, Random var3, int var4, int var5) {
      switch(this.field_74885_f) {
         case 0:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               3,
               this.func_74877_c()
            );
         case 1:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78892_f + 1,
               0,
               this.func_74877_c()
            );
         case 2:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               3,
               this.func_74877_c()
            );
         case 3:
            return StructureStrongholdPieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78892_f + 1,
               0,
               this.func_74877_c()
            );
         default:
            return null;
      }
   }

   protected static boolean func_74991_a(StructureBoundingBox var0) {
      return var0 != null && var0.field_78895_b > 10;
   }
}
