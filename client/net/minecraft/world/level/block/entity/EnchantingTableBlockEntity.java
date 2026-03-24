package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable {
   private static final Component DEFAULT_NAME = Component.translatable("container.enchant");
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   public float rot;
   public float oRot;
   public float tRot;
   private static final RandomSource RANDOM = RandomSource.create();
   private @Nullable Component name;

   public EnchantingTableBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityType.ENCHANTING_TABLE, worldPosition, blockState);
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.name = parseCustomNameSafe(input, "CustomName");
   }

   public static void bookAnimationTick(final Level level, final BlockPos worldPosition, final BlockState state, final EnchantingTableBlockEntity entity) {
      entity.oOpen = entity.open;
      entity.oRot = entity.rot;
      Player player = level.getNearestPlayer((double)worldPosition.getX() + 0.5, (double)worldPosition.getY() + 0.5, (double)worldPosition.getZ() + 0.5, 3.0, false);
      if (player != null) {
         double xd = player.getX() - ((double)worldPosition.getX() + 0.5);
         double zd = player.getZ() - ((double)worldPosition.getZ() + 0.5);
         entity.tRot = (float)Mth.atan2(zd, xd);
         entity.open += 0.1F;
         if (entity.open < 0.5F || RANDOM.nextInt(40) == 0) {
            float old = entity.flipT;

            do {
               entity.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while(old == entity.flipT);
         }
      } else {
         entity.tRot += 0.02F;
         entity.open -= 0.1F;
      }

      while(entity.rot >= 3.1415927F) {
         entity.rot -= 6.2831855F;
      }

      while(entity.rot < -3.1415927F) {
         entity.rot += 6.2831855F;
      }

      while(entity.tRot >= 3.1415927F) {
         entity.tRot -= 6.2831855F;
      }

      while(entity.tRot < -3.1415927F) {
         entity.tRot += 6.2831855F;
      }

      float rotDir;
      for(rotDir = entity.tRot - entity.rot; rotDir >= 3.1415927F; rotDir -= 6.2831855F) {
      }

      while(rotDir < -3.1415927F) {
         rotDir += 6.2831855F;
      }

      entity.rot += rotDir * 0.4F;
      entity.open = Mth.clamp(entity.open, 0.0F, 1.0F);
      ++entity.time;
      entity.oFlip = entity.flip;
      float diff = (entity.flipT - entity.flip) * 0.4F;
      float max = 0.2F;
      diff = Mth.clamp(diff, -0.2F, 0.2F);
      entity.flipA += (diff - entity.flipA) * 0.9F;
      entity.flip += entity.flipA;
   }

   public Component getName() {
      return this.name != null ? this.name : DEFAULT_NAME;
   }

   public void setCustomName(final @Nullable Component name) {
      this.name = name;
   }

   public @Nullable Component getCustomName() {
      return this.name;
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      super.applyImplicitComponents(components);
      this.name = (Component)components.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(final DataComponentMap.Builder components) {
      super.collectImplicitComponents(components);
      components.set(DataComponents.CUSTOM_NAME, this.name);
   }

   public void removeComponentsFromTag(final ValueOutput output) {
      output.discard("CustomName");
   }
}
