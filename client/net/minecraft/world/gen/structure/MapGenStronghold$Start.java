package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.world.World;

public class MapGenStronghold$Start extends StructureStart {
   public MapGenStronghold$Start() {
      super();
   }

   public MapGenStronghold$Start(World var1, Random var2, int var3, int var4) {
      super(var3, var4);
      StructureStrongholdPieces.func_75198_a();
      StructureStrongholdPieces$Stairs2 var5 = new StructureStrongholdPieces$Stairs2(0, var2, (var3 << 4) + 2, (var4 << 4) + 2);
      this.field_75075_a.add(var5);
      var5.func_74861_a(var5, this.field_75075_a, var2);
      List var6 = var5.field_75026_c;

      while(!var6.isEmpty()) {
         int var7 = var2.nextInt(var6.size());
         StructureComponent var8 = (StructureComponent)var6.remove(var7);
         var8.func_74861_a(var5, this.field_75075_a, var2);
      }

      this.func_75072_c();
      this.func_75067_a(var1, var2, 10);
   }
}
