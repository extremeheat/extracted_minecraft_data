package net.minecraft.world.item;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties var1) {
      super(var1);
   }

   public static Tool createToolProperties() {
      HolderGetter var0 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
      return new Tool(
         List.of(
            Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F),
            Tool.Rule.overrideSpeed(var0.getOrThrow(BlockTags.LEAVES), 15.0F),
            Tool.Rule.overrideSpeed(var0.getOrThrow(BlockTags.WOOL), 5.0F),
            Tool.Rule.overrideSpeed(HolderSet.direct(Blocks.VINE.builtInRegistryHolder(), Blocks.GLOW_LICHEN.builtInRegistryHolder()), 2.0F)
         ),
         1.0F,
         1
      );
   }

   @Override
   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (!var2.isClientSide && !var3.is(BlockTags.FIRE)) {
         var1.hurtAndBreak(1, var5, EquipmentSlot.MAINHAND);
      }

      return var3.is(BlockTags.LEAVES)
         || var3.is(Blocks.COBWEB)
         || var3.is(Blocks.SHORT_GRASS)
         || var3.is(Blocks.FERN)
         || var3.is(Blocks.DEAD_BUSH)
         || var3.is(Blocks.HANGING_ROOTS)
         || var3.is(Blocks.VINE)
         || var3.is(Blocks.TRIPWIRE)
         || var3.is(BlockTags.WOOL);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.getBlock() instanceof GrowingPlantHeadBlock var6 && !var6.isMaxAge(var4)) {
         Player var7 = var1.getPlayer();
         ItemStack var8 = var1.getItemInHand();
         if (var7 instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)var7, var3, var8);
         }

         var2.playSound(var7, var3, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
         BlockState var9 = var6.getMaxAgeState(var4);
         var2.setBlockAndUpdate(var3, var9);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1.getPlayer(), var9));
         if (var7 != null) {
            var8.hurtAndBreak(1, var7, LivingEntity.getSlotForHand(var1.getHand()));
         }

         return InteractionResult.SUCCESS;
      }

      return super.useOn(var1);
   }
}
