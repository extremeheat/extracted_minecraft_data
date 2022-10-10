package net.minecraft.entity.ai;

import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIBreathAir extends EntityAIBase {
   private final EntityCreature field_205142_a;

   public EntityAIBreathAir(EntityCreature var1) {
      super();
      this.field_205142_a = var1;
      this.func_75248_a(3);
   }

   public boolean func_75250_a() {
      return this.field_205142_a.func_70086_ai() < 140;
   }

   public boolean func_75253_b() {
      return this.func_75250_a();
   }

   public boolean func_75252_g() {
      return false;
   }

   public void func_75249_e() {
      this.func_205141_g();
   }

   private void func_205141_g() {
      Iterable var1 = BlockPos.MutableBlockPos.func_191531_b(MathHelper.func_76128_c(this.field_205142_a.field_70165_t - 1.0D), MathHelper.func_76128_c(this.field_205142_a.field_70163_u), MathHelper.func_76128_c(this.field_205142_a.field_70161_v - 1.0D), MathHelper.func_76128_c(this.field_205142_a.field_70165_t + 1.0D), MathHelper.func_76128_c(this.field_205142_a.field_70163_u + 8.0D), MathHelper.func_76128_c(this.field_205142_a.field_70161_v + 1.0D));
      BlockPos var2 = null;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         BlockPos var4 = (BlockPos)var3.next();
         if (this.func_205140_a(this.field_205142_a.field_70170_p, var4)) {
            var2 = var4;
            break;
         }
      }

      if (var2 == null) {
         var2 = new BlockPos(this.field_205142_a.field_70165_t, this.field_205142_a.field_70163_u + 8.0D, this.field_205142_a.field_70161_v);
      }

      this.field_205142_a.func_70661_as().func_75492_a((double)var2.func_177958_n(), (double)(var2.func_177956_o() + 1), (double)var2.func_177952_p(), 1.0D);
   }

   public void func_75246_d() {
      this.func_205141_g();
      this.field_205142_a.func_191958_b(this.field_205142_a.field_70702_br, this.field_205142_a.field_70701_bs, this.field_205142_a.field_191988_bg, 0.02F);
      this.field_205142_a.func_70091_d(MoverType.SELF, this.field_205142_a.field_70159_w, this.field_205142_a.field_70181_x, this.field_205142_a.field_70179_y);
   }

   private boolean func_205140_a(IWorldReaderBase var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      return (var1.func_204610_c(var2).func_206888_e() || var3.func_177230_c() == Blocks.field_203203_C) && var3.func_196957_g(var1, var2, PathType.LAND);
   }
}
