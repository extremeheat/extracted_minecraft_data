package net.minecraft.entity.ai;

import java.util.Iterator;
import net.minecraft.entity.EntityCreature;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIFindWater extends EntityAIBase {
   private final EntityCreature field_205152_a;

   public EntityAIFindWater(EntityCreature var1) {
      super();
      this.field_205152_a = var1;
   }

   public boolean func_75250_a() {
      return this.field_205152_a.field_70122_E && !this.field_205152_a.field_70170_p.func_204610_c(new BlockPos(this.field_205152_a)).func_206884_a(FluidTags.field_206959_a);
   }

   public void func_75249_e() {
      BlockPos var1 = null;
      Iterable var2 = BlockPos.MutableBlockPos.func_191531_b(MathHelper.func_76128_c(this.field_205152_a.field_70165_t - 2.0D), MathHelper.func_76128_c(this.field_205152_a.field_70163_u - 2.0D), MathHelper.func_76128_c(this.field_205152_a.field_70161_v - 2.0D), MathHelper.func_76128_c(this.field_205152_a.field_70165_t + 2.0D), MathHelper.func_76128_c(this.field_205152_a.field_70163_u), MathHelper.func_76128_c(this.field_205152_a.field_70161_v + 2.0D));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         BlockPos var4 = (BlockPos)var3.next();
         if (this.field_205152_a.field_70170_p.func_204610_c(var4).func_206884_a(FluidTags.field_206959_a)) {
            var1 = var4;
            break;
         }
      }

      if (var1 != null) {
         this.field_205152_a.func_70605_aq().func_75642_a((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p(), 1.0D);
      }

   }
}
