package net.minecraft.core.dispenser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public interface DispenseBehavior {
   Codec<DispenseBehavior> DIRECT_CODEC = BuiltInRegistries.DISPENSE_BEHAVIOR_TYPE.byNameCodec().dispatch(DispenseBehavior::codec, (c) -> c);
   Codec<List<DispenseBehavior>> LIST_CODEC = DIRECT_CODEC.listOf();
   Codec<Holder<List<DispenseBehavior>>> CODEC = RegistryCodecs.holder(Registries.DISPENSE_BEHAVIOR, LIST_CODEC);

   static ItemStack dispense(final Iterable<DispenseBehavior> behaviors, final BlockSource source, final ItemStack dispensed) {
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);

      for(DispenseBehavior behavior : behaviors) {
         DispenseResult result = behavior.execute(source, dispensed);
         if (result.shouldStop()) {
            behavior.playAnimation(source, direction);
            if (result instanceof DispenseResult.Success) {
               DispenseResult.Success var7 = (DispenseResult.Success)result;
               DispenseResult.Success var10000 = var7;

               try {
                  var11 = var10000.remaining();
               } catch (Throwable var10) {
                  throw new MatchException(var10.toString(), var10);
               }

               ItemStack remaining = var11;
               behavior.playSuccessSound(source);
               return remaining;
            }

            behavior.playFailureSound(source);
            return dispensed;
         }
      }

      playDefaultFailureSound(source);
      playDefaultAnimation(source, direction);
      return dispensed;
   }

   DispenseResult execute(BlockSource source, ItemStack dispensed);

   default void playSuccessSound(final BlockSource source) {
      playDefaultSuccessSound(source);
   }

   default void playFailureSound(final BlockSource source) {
      playDefaultFailureSound(source);
   }

   default void playAnimation(final BlockSource source, final Direction direction) {
      playDefaultAnimation(source, direction);
   }

   static void playDefaultAnimation(final BlockSource source, final Direction direction) {
      source.level().levelEvent(2000, source.pos(), direction.get3DDataValue());
   }

   private static void playDefaultSuccessSound(final BlockSource source) {
      source.level().levelEvent(1000, source.pos(), 0);
   }

   private static void playDefaultFailureSound(final BlockSource source) {
      source.level().levelEvent(1001, source.pos(), 0);
   }

   static DispenseResult consumeWithRemainder(final BlockSource source, final ItemStack dispensed, final ItemStack remainder) {
      dispensed.shrink(1);
      if (dispensed.isEmpty()) {
         return DispenseBehavior.DispenseResult.success(remainder);
      } else {
         addToInventoryOrDispense(source, remainder);
         return DispenseBehavior.DispenseResult.success(dispensed);
      }
   }

   private static void addToInventoryOrDispense(final BlockSource source, final ItemStack itemStack) {
      ItemStack remainder = source.blockEntity().insertItem(itemStack);
      if (!remainder.isEmpty()) {
         Direction direction = DropItemDispenseBehavior.spawnItem(source, remainder);
         playDefaultSuccessSound(source);
         playDefaultAnimation(source, direction);
      }
   }

   MapCodec<? extends DispenseBehavior> codec();

   public sealed interface DispenseResult {
      boolean shouldStop();

      static Success success(final ItemStack remaining) {
         return new Success(remaining);
      }

      static Failure failure() {
         return DispenseBehavior.DispenseResult.Failure.INSTANCE;
      }

      static StoppingFailure stoppingFailure() {
         return DispenseBehavior.DispenseResult.StoppingFailure.INSTANCE;
      }

      public static record Success(ItemStack remaining) implements DispenseResult {
         public Success {
            super();
         }

         public boolean shouldStop() {
            return true;
         }
      }

      public static final class Failure implements DispenseResult {
         public static final Failure INSTANCE = new Failure();

         private Failure() {
            super();
         }

         public boolean shouldStop() {
            return false;
         }
      }

      public static final class StoppingFailure implements DispenseResult {
         public static final StoppingFailure INSTANCE = new StoppingFailure();

         private StoppingFailure() {
            super();
         }

         public boolean shouldStop() {
            return true;
         }
      }
   }
}
