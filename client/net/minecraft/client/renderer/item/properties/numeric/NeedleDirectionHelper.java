package net.minecraft.client.renderer.item.properties.numeric;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class NeedleDirectionHelper {
   private final boolean wobble;

   protected NeedleDirectionHelper(boolean var1) {
      super();
      this.wobble = var1;
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable ItemOwner var3, int var4) {
      if (var3 == null) {
         var3 = var1.getEntityRepresentation();
      }

      if (var3 == null) {
         return 0.0F;
      } else {
         if (var2 == null && var3 != null) {
            Level var6 = ((ItemOwner)var3).level();
            if (var6 instanceof ClientLevel) {
               ClientLevel var5 = (ClientLevel)var6;
               var2 = var5;
            }
         }

         return var2 == null ? 0.0F : this.calculate(var1, var2, var4, (ItemOwner)var3);
      }
   }

   protected abstract float calculate(ItemStack var1, ClientLevel var2, int var3, @Nullable ItemOwner var4);

   protected boolean wobble() {
      return this.wobble;
   }

   protected Wobbler newWobbler(float var1) {
      return this.wobble ? standardWobbler(var1) : nonWobbler();
   }

   public static Wobbler standardWobbler(final float var0) {
      return new Wobbler() {
         private float rotation;
         private float deltaRotation;
         private long lastUpdateTick;

         public float rotation() {
            return this.rotation;
         }

         public boolean shouldUpdate(long var1) {
            return this.lastUpdateTick != var1;
         }

         public void update(long var1, float var3) {
            this.lastUpdateTick = var1;
            float var4 = Mth.positiveModulo(var3 - this.rotation + 0.5F, 1.0F) - 0.5F;
            this.deltaRotation += var4 * 0.1F;
            this.deltaRotation *= var0;
            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0F);
         }
      };
   }

   public static Wobbler nonWobbler() {
      return new Wobbler() {
         private float targetValue;

         public float rotation() {
            return this.targetValue;
         }

         public boolean shouldUpdate(long var1) {
            return true;
         }

         public void update(long var1, float var3) {
            this.targetValue = var3;
         }
      };
   }

   public interface Wobbler {
      float rotation();

      boolean shouldUpdate(long var1);

      void update(long var1, float var3);
   }
}
