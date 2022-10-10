package net.minecraft.entity.ai;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.EntityCreature;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAIWanderAvoidWaterFlying extends EntityAIWanderAvoidWater {
   public EntityAIWanderAvoidWaterFlying(EntityCreature var1, double var2) {
      super(var1, var2);
   }

   @Nullable
   protected Vec3d func_190864_f() {
      Vec3d var1 = null;
      if (this.field_75457_a.func_70090_H()) {
         var1 = RandomPositionGenerator.func_191377_b(this.field_75457_a, 15, 15);
      }

      if (this.field_75457_a.func_70681_au().nextFloat() >= this.field_190865_h) {
         var1 = this.func_192385_j();
      }

      return var1 == null ? super.func_190864_f() : var1;
   }

   @Nullable
   private Vec3d func_192385_j() {
      BlockPos var1 = new BlockPos(this.field_75457_a);
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      Iterable var4 = BlockPos.MutableBlockPos.func_191531_b(MathHelper.func_76128_c(this.field_75457_a.field_70165_t - 3.0D), MathHelper.func_76128_c(this.field_75457_a.field_70163_u - 6.0D), MathHelper.func_76128_c(this.field_75457_a.field_70161_v - 3.0D), MathHelper.func_76128_c(this.field_75457_a.field_70165_t + 3.0D), MathHelper.func_76128_c(this.field_75457_a.field_70163_u + 6.0D), MathHelper.func_76128_c(this.field_75457_a.field_70161_v + 3.0D));
      Iterator var5 = var4.iterator();

      BlockPos var6;
      boolean var8;
      do {
         do {
            if (!var5.hasNext()) {
               return null;
            }

            var6 = (BlockPos)var5.next();
         } while(var1.equals(var6));

         Block var7 = this.field_75457_a.field_70170_p.func_180495_p(var3.func_189533_g(var6).func_189536_c(EnumFacing.DOWN)).func_177230_c();
         var8 = var7 instanceof BlockLeaves || var7.func_203417_a(BlockTags.field_200031_h);
      } while(!var8 || !this.field_75457_a.field_70170_p.func_175623_d(var6) || !this.field_75457_a.field_70170_p.func_175623_d(var2.func_189533_g(var6).func_189536_c(EnumFacing.UP)));

      return new Vec3d((double)var6.func_177958_n(), (double)var6.func_177956_o(), (double)var6.func_177952_p());
   }
}
