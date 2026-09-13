package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class StructureStrongholdPieces$PortalRoom extends StructureStrongholdPieces$Stronghold {
   private boolean field_75005_a;

   public StructureStrongholdPieces$PortalRoom() {
      super();
   }

   public StructureStrongholdPieces$PortalRoom(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Mob", this.field_75005_a);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_75005_a = var1.func_74767_n("Mob");
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      if (var1 != null) {
         ((StructureStrongholdPieces$Stairs2)var1).field_75025_b = this;
      }
   }

   public static StructureStrongholdPieces$PortalRoom func_75004_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -4, -1, 0, 11, 8, 16, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureStrongholdPieces$PortalRoom(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_74882_a(var1, var3, 0, 0, 0, 10, 7, 15, false, var2, StructureStrongholdPieces.access$200());
      this.func_74990_a(var1, var2, var3, StructureStrongholdPieces$Stronghold$Door.GRATES, 4, 1, 0);
      int var4 = 6;
      this.func_74882_a(var1, var3, 1, var4, 1, 1, var4, 14, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 9, var4, 1, 9, var4, 14, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 2, var4, 1, 8, var4, 2, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 2, var4, 14, 8, var4, 14, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 1, 1, 1, 2, 1, 4, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 8, 1, 1, 9, 1, 4, false, var2, StructureStrongholdPieces.access$200());
      this.func_151549_a(var1, var3, 1, 1, 1, 1, 1, 3, Blocks.field_150356_k, Blocks.field_150356_k, false);
      this.func_151549_a(var1, var3, 9, 1, 1, 9, 1, 3, Blocks.field_150356_k, Blocks.field_150356_k, false);
      this.func_74882_a(var1, var3, 3, 1, 8, 7, 1, 12, false, var2, StructureStrongholdPieces.access$200());
      this.func_151549_a(var1, var3, 4, 1, 9, 6, 1, 11, Blocks.field_150356_k, Blocks.field_150356_k, false);

      for(int var5 = 3; var5 < 14; var5 += 2) {
         this.func_151549_a(var1, var3, 0, 3, var5, 0, 4, var5, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
         this.func_151549_a(var1, var3, 10, 3, var5, 10, 4, var5, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
      }

      for(int var14 = 2; var14 < 9; var14 += 2) {
         this.func_151549_a(var1, var3, var14, 3, 15, var14, 4, 15, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
      }

      int var15 = this.func_151555_a(Blocks.field_150390_bg, 3);
      this.func_74882_a(var1, var3, 4, 1, 5, 6, 1, 7, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 4, 2, 6, 6, 2, 7, false, var2, StructureStrongholdPieces.access$200());
      this.func_74882_a(var1, var3, 4, 3, 7, 6, 3, 7, false, var2, StructureStrongholdPieces.access$200());

      for(int var6 = 4; var6 <= 6; ++var6) {
         this.func_151550_a(var1, Blocks.field_150390_bg, var15, var6, 1, 4, var3);
         this.func_151550_a(var1, Blocks.field_150390_bg, var15, var6, 2, 5, var3);
         this.func_151550_a(var1, Blocks.field_150390_bg, var15, var6, 3, 6, var3);
      }

      byte var16 = 2;
      byte var7 = 0;
      byte var8 = 3;
      byte var9 = 1;
      switch(this.field_74885_f) {
         case 0:
            var16 = 0;
            var7 = 2;
            break;
         case 1:
            var16 = 1;
            var7 = 3;
            var8 = 0;
            var9 = 2;
         case 2:
         default:
            break;
         case 3:
            var16 = 3;
            var7 = 1;
            var8 = 0;
            var9 = 2;
      }

      this.func_151550_a(var1, Blocks.field_150378_br, var16 + (var2.nextFloat() > 0.9F ? 4 : 0), 4, 3, 8, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var16 + (var2.nextFloat() > 0.9F ? 4 : 0), 5, 3, 8, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var16 + (var2.nextFloat() > 0.9F ? 4 : 0), 6, 3, 8, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var7 + (var2.nextFloat() > 0.9F ? 4 : 0), 4, 3, 12, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var7 + (var2.nextFloat() > 0.9F ? 4 : 0), 5, 3, 12, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var7 + (var2.nextFloat() > 0.9F ? 4 : 0), 6, 3, 12, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var8 + (var2.nextFloat() > 0.9F ? 4 : 0), 3, 3, 9, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var8 + (var2.nextFloat() > 0.9F ? 4 : 0), 3, 3, 10, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var8 + (var2.nextFloat() > 0.9F ? 4 : 0), 3, 3, 11, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var9 + (var2.nextFloat() > 0.9F ? 4 : 0), 7, 3, 9, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var9 + (var2.nextFloat() > 0.9F ? 4 : 0), 7, 3, 10, var3);
      this.func_151550_a(var1, Blocks.field_150378_br, var9 + (var2.nextFloat() > 0.9F ? 4 : 0), 7, 3, 11, var3);
      if (!this.field_75005_a) {
         var4 = this.func_74862_a(3);
         int var10 = this.func_74865_a(5, 6);
         int var11 = this.func_74873_b(5, 6);
         if (var3.func_78890_b(var10, var4, var11)) {
            this.field_75005_a = true;
            var1.func_147465_d(var10, var4, var11, Blocks.field_150474_ac, 0, 2);
            TileEntityMobSpawner var12 = (TileEntityMobSpawner)var1.func_147438_o(var10, var4, var11);
            if (var12 != null) {
               var12.func_145881_a().func_98272_a("Silverfish");
            }
         }
      }

      return true;
   }
}
