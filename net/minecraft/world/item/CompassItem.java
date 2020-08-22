package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CompassItem extends Item {
   public CompassItem(Item.Properties var1) {
      super(var1);
      this.addProperty(new ResourceLocation("angle"), new ItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float call(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3) {
            if (var3 == null && !var1.isFramed()) {
               return 0.0F;
            } else {
               boolean var4 = var3 != null;
               Object var5 = var4 ? var3 : var1.getFrame();
               if (var2 == null) {
                  var2 = ((Entity)var5).level;
               }

               double var6;
               if (var2.dimension.isNaturalDimension()) {
                  double var8 = var4 ? (double)((Entity)var5).yRot : this.getFrameRotation((ItemFrame)var5);
                  var8 = Mth.positiveModulo(var8 / 360.0D, 1.0D);
                  double var10 = this.getSpawnToAngle(var2, (Entity)var5) / 6.2831854820251465D;
                  var6 = 0.5D - (var8 - 0.25D - var10);
               } else {
                  var6 = Math.random();
               }

               if (var4) {
                  var6 = this.wobble(var2, var6);
               }

               return Mth.positiveModulo((float)var6, 1.0F);
            }
         }

         private double wobble(Level var1, double var2) {
            if (var1.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = var1.getGameTime();
               double var4 = var2 - this.rotation;
               var4 = Mth.positiveModulo(var4 + 0.5D, 1.0D) - 0.5D;
               this.rota += var4 * 0.1D;
               this.rota *= 0.8D;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }

         private double getFrameRotation(ItemFrame var1) {
            return (double)Mth.wrapDegrees(180 + var1.getDirection().get2DDataValue() * 90);
         }

         private double getSpawnToAngle(LevelAccessor var1, Entity var2) {
            BlockPos var3 = var1.getSharedSpawnPos();
            return Math.atan2((double)var3.getZ() - var2.getZ(), (double)var3.getX() - var2.getX());
         }
      });
   }
}
