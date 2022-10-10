package net.minecraft.world.lighting;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

public class BlockLightEngine extends BaseLightEngine {
   public BlockLightEngine() {
      super();
   }

   public EnumLightType func_202657_a() {
      return EnumLightType.BLOCK;
   }

   public void func_202677_a(WorldGenRegion var1, IChunk var2) {
      Iterator var3 = var2.func_201582_h().iterator();

      while(var3.hasNext()) {
         BlockPos var4 = (BlockPos)var3.next();
         this.func_202667_a(var1, var4, this.func_202670_c(var1, var4));
         this.func_202659_a(var2.func_76632_l(), var4, this.func_202666_a(var1, var4));
      }

      this.func_202664_a(var1, var2.func_76632_l());
   }
}
