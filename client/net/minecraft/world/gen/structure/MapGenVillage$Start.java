package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MapGenVillage$Start extends StructureStart {
   private boolean field_75076_c;

   public MapGenVillage$Start() {
      super();
   }

   public MapGenVillage$Start(World var1, Random var2, int var3, int var4, int var5) {
      super(var3, var4);
      List var6 = StructureVillagePieces.func_75084_a(var2, var5);
      StructureVillagePieces$Start var7 = new StructureVillagePieces$Start(var1.func_72959_q(), 0, var2, (var3 << 4) + 2, (var4 << 4) + 2, var6, var5);
      this.field_75075_a.add(var7);
      var7.func_74861_a(var7, this.field_75075_a, var2);
      List var8 = var7.field_74930_j;
      List var9 = var7.field_74932_i;

      while(!var8.isEmpty() || !var9.isEmpty()) {
         if (var8.isEmpty()) {
            int var10 = var2.nextInt(var9.size());
            StructureComponent var11 = (StructureComponent)var9.remove(var10);
            var11.func_74861_a(var7, this.field_75075_a, var2);
         } else {
            int var13 = var2.nextInt(var8.size());
            StructureComponent var15 = (StructureComponent)var8.remove(var13);
            var15.func_74861_a(var7, this.field_75075_a, var2);
         }
      }

      this.func_75072_c();
      int var14 = 0;

      for(StructureComponent var12 : this.field_75075_a) {
         if (!(var12 instanceof StructureVillagePieces$Road)) {
            ++var14;
         }
      }

      this.field_75076_c = var14 > 2;
   }

   @Override
   public boolean func_75069_d() {
      return this.field_75076_c;
   }

   @Override
   public void func_143022_a(NBTTagCompound var1) {
      super.func_143022_a(var1);
      var1.func_74757_a("Valid", this.field_75076_c);
   }

   @Override
   public void func_143017_b(NBTTagCompound var1) {
      super.func_143017_b(var1);
      this.field_75076_c = var1.func_74767_n("Valid");
   }
}
