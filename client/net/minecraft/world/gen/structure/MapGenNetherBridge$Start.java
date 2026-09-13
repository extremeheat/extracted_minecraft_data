package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.world.World;

public class MapGenNetherBridge$Start extends StructureStart {
   public MapGenNetherBridge$Start() {
      super();
   }

   public MapGenNetherBridge$Start(World var1, Random var2, int var3, int var4) {
      super(var3, var4);
      StructureNetherBridgePieces$Start var5 = new StructureNetherBridgePieces$Start(var2, (var3 << 4) + 2, (var4 << 4) + 2);
      this.field_75075_a.add(var5);
      var5.func_74861_a(var5, this.field_75075_a, var2);
      ArrayList var6 = var5.field_74967_d;

      while(!var6.isEmpty()) {
         int var7 = var2.nextInt(var6.size());
         StructureComponent var8 = (StructureComponent)var6.remove(var7);
         var8.func_74861_a(var5, this.field_75075_a, var2);
      }

      this.func_75072_c();
      this.func_75070_a(var1, var2, 48, 70);
   }
}
