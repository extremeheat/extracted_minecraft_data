package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition {
   public final int field_151329_a;
   public final int field_151327_b;
   public final int field_151328_c;

   public ChunkPosition(int var1, int var2, int var3) {
      super();
      this.field_151329_a = var1;
      this.field_151327_b = var2;
      this.field_151328_c = var3;
   }

   public ChunkPosition(Vec3 var1) {
      this(MathHelper.func_76128_c(var1.field_72450_a), MathHelper.func_76128_c(var1.field_72448_b), MathHelper.func_76128_c(var1.field_72449_c));
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ChunkPosition)) {
         return false;
      } else {
         ChunkPosition var2 = (ChunkPosition)var1;
         return var2.field_151329_a == this.field_151329_a && var2.field_151327_b == this.field_151327_b && var2.field_151328_c == this.field_151328_c;
      }
   }

   @Override
   public int hashCode() {
      return this.field_151329_a * 8976890 + this.field_151327_b * 981131 + this.field_151328_c;
   }
}
