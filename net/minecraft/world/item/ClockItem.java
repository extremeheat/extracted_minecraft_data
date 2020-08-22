package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ClockItem extends Item {
   public ClockItem(Item.Properties var1) {
      super(var1);
      this.addProperty(new ResourceLocation("time"), new ItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float call(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3) {
            boolean var4 = var3 != null;
            Object var5 = var4 ? var3 : var1.getFrame();
            if (var2 == null && var5 != null) {
               var2 = ((Entity)var5).level;
            }

            if (var2 == null) {
               return 0.0F;
            } else {
               double var6;
               if (var2.dimension.isNaturalDimension()) {
                  var6 = (double)var2.getTimeOfDay(1.0F);
               } else {
                  var6 = Math.random();
               }

               var6 = this.wobble(var2, var6);
               return (float)var6;
            }
         }

         private double wobble(Level var1, double var2) {
            if (var1.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = var1.getGameTime();
               double var4 = var2 - this.rotation;
               var4 = Mth.positiveModulo(var4 + 0.5D, 1.0D) - 0.5D;
               this.rota += var4 * 0.1D;
               this.rota *= 0.9D;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }
      });
   }
}
