package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Corridor extends StructureStrongholdPieces$Stronghold {
   private int field_74993_a;

   public StructureStrongholdPieces$Corridor() {
      super();
   }

   public StructureStrongholdPieces$Corridor(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
      this.field_74993_a = var4 != 2 && var4 != 0 ? var3.func_78883_b() : var3.func_78880_d();
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74768_a("Steps", this.field_74993_a);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74993_a = var1.func_74762_e("Steps");
   }

   public static StructureBoundingBox func_74992_a(List var0, Random var1, int var2, int var3, int var4, int var5) {
      boolean var6 = true;
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
      StructureComponent var8 = StructureComponent.func_74883_a(var0, var7);
      if (var8 == null) {
         return null;
      } else {
         if (var8.func_74874_b().field_78895_b == var7.field_78895_b) {
            for(int var9 = 3; var9 >= 1; --var9) {
               var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 5, 5, var9 - 1, var5);
               if (!var8.func_74874_b().func_78884_a(var7)) {
                  return StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 5, 5, var9, var5);
               }
            }
         }

         return null;
      }
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         for(int var4 = 0; var4 < this.field_74993_a; ++var4) {
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 0, 0, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 0, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 2, 0, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 0, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 4, 0, var4, var3);

            for(int var5 = 1; var5 <= 3; ++var5) {
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 0, var5, var4, var3);
               this.func_151550_a(var1, Blocks.field_150350_a, 0, 1, var5, var4, var3);
               this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, var5, var4, var3);
               this.func_151550_a(var1, Blocks.field_150350_a, 0, 3, var5, var4, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 4, var5, var4, var3);
            }

            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 0, 4, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 4, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 2, 4, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 4, var4, var3);
            this.func_151550_a(var1, Blocks.field_150417_aV, 0, 4, 4, var4, var3);
         }

         return true;
      }
   }
}
