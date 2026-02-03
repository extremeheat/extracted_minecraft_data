package net.minecraft.client.renderer.item.properties.numeric;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class NeedleDirectionHelper {
   private final boolean wobble;

   protected NeedleDirectionHelper(final boolean wobble) {
      super();
      this.wobble = wobble;
   }

   public float get(final ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner owner, final int seed) {
      if (owner == null) {
         owner = itemStack.getEntityRepresentation();
      }

      if (owner == null) {
         return 0.0F;
      } else {
         if (clientLevel == null) {
            Level var6 = owner.level();
            if (var6 instanceof ClientLevel) {
               ClientLevel level = (ClientLevel)var6;
               clientLevel = level;
            }
         }

         return clientLevel == null ? 0.0F : this.calculate(itemStack, clientLevel, seed, owner);
      }
   }

   protected abstract float calculate(final ItemStack itemStack, final ClientLevel level, final int seed, final ItemOwner owner);

   protected boolean wobble() {
      return this.wobble;
   }

   protected Wobbler newWobbler(final float factor) {
      return this.wobble ? standardWobbler(factor) : nonWobbler();
   }

   public static Wobbler standardWobbler(final float factor) {
      return new Wobbler() {
         private float rotation;
         private float deltaRotation;
         private long lastUpdateTick;

         public float rotation() {
            return this.rotation;
         }

         public boolean shouldUpdate(final long tick) {
            return this.lastUpdateTick != tick;
         }

         public void update(final long tick, final float targetRotation) {
            this.lastUpdateTick = tick;
            float tempDeltaRotation = Mth.positiveModulo(targetRotation - this.rotation + 0.5F, 1.0F) - 0.5F;
            this.deltaRotation += tempDeltaRotation * 0.1F;
            this.deltaRotation *= factor;
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

         public boolean shouldUpdate(final long tick) {
            return true;
         }

         public void update(final long tick, final float targetRotation) {
            this.targetValue = targetRotation;
         }
      };
   }

   public interface Wobbler {
      float rotation();

      boolean shouldUpdate(long tick);

      void update(long tick, float targetRotation);
   }
}
