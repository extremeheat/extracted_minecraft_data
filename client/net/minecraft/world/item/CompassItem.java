package net.minecraft.world.item;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public class CompassItem extends Item {
   private static final Component LODESTONE_COMPASS_NAME = Component.translatable("item.minecraft.lodestone_compass");

   public CompassItem(Item.Properties var1) {
      super(var1);
   }

   public boolean isFoil(ItemStack var1) {
      return var1.has(DataComponents.LODESTONE_TRACKER) || super.isFoil(var1);
   }

   public void inventoryTick(ItemStack var1, ServerLevel var2, Entity var3, @Nullable EquipmentSlot var4) {
      LodestoneTracker var5 = (LodestoneTracker)var1.get(DataComponents.LODESTONE_TRACKER);
      if (var5 != null) {
         LodestoneTracker var6 = var5.tick(var2);
         if (var6 != var5) {
            var1.set(DataComponents.LODESTONE_TRACKER, var6);
         }
      }

   }

   public InteractionResult useOn(UseOnContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      if (!var3.getBlockState(var2).is(Blocks.LODESTONE)) {
         return super.useOn(var1);
      } else {
         var3.playSound((Entity)null, (BlockPos)var2, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
         Player var4 = var1.getPlayer();
         ItemStack var5 = var1.getItemInHand();
         boolean var6 = !var4.hasInfiniteMaterials() && var5.getCount() == 1;
         LodestoneTracker var7 = new LodestoneTracker(Optional.of(GlobalPos.of(var3.dimension(), var2)), true);
         if (var6) {
            var5.set(DataComponents.LODESTONE_TRACKER, var7);
         } else {
            ItemStack var8 = var5.transmuteCopy(Items.COMPASS, 1);
            var5.consume(1, var4);
            var8.set(DataComponents.LODESTONE_TRACKER, var7);
            if (!var4.getInventory().add(var8)) {
               var4.drop(var8, false);
            }
         }

         return InteractionResult.SUCCESS;
      }
   }

   public Component getName(ItemStack var1) {
      return var1.has(DataComponents.LODESTONE_TRACKER) ? LODESTONE_COMPASS_NAME : super.getName(var1);
   }
}
