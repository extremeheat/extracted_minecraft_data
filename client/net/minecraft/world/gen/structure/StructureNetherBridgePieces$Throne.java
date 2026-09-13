package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Throne extends StructureNetherBridgePieces$Piece {
   private boolean field_74976_a;

   public StructureNetherBridgePieces$Throne() {
      super();
   }

   public StructureNetherBridgePieces$Throne(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74976_a = var1.func_74767_n("Mob");
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Mob", this.field_74976_a);
   }

   public static StructureNetherBridgePieces$Throne func_74975_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -2, 0, 0, 7, 8, 9, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces$Throne(var6, var1, var7, var5) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 0, 2, 0, 6, 7, 7, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 1, 0, 0, 5, 1, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 2, 1, 5, 2, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 3, 2, 5, 3, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 4, 3, 5, 4, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 2, 0, 1, 4, 2, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 5, 2, 0, 5, 4, 2, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 5, 2, 1, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 5, 5, 2, 5, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 5, 3, 0, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 5, 3, 6, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 5, 8, 5, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151550_a(var1, Blocks.field_150386_bk, 0, 1, 6, 3, var3);
      this.func_151550_a(var1, Blocks.field_150386_bk, 0, 5, 6, 3, var3);
      this.func_151549_a(var1, var3, 0, 6, 3, 0, 6, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 6, 6, 3, 6, 6, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 1, 6, 8, 5, 7, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 2, 8, 8, 4, 8, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      if (!this.field_74976_a) {
         int var4 = this.func_74862_a(5);
         int var5 = this.func_74865_a(3, 5);
         int var6 = this.func_74873_b(3, 5);
         if (var3.func_78890_b(var5, var4, var6)) {
            this.field_74976_a = true;
            var1.func_147465_d(var5, var4, var6, Blocks.field_150474_ac, 0, 2);
            TileEntityMobSpawner var7 = (TileEntityMobSpawner)var1.func_147438_o(var5, var4, var6);
            if (var7 != null) {
               var7.func_145881_a().func_98272_a("Blaze");
            }
         }
      }

      for(int var8 = 0; var8 <= 6; ++var8) {
         for(int var9 = 0; var9 <= 6; ++var9) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var8, -1, var9, var3);
         }
      }

      return true;
   }
}
