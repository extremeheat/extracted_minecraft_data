package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock extends BaseEntityBlock {
   protected SpawnerBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SpawnerBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.MOB_SPAWNER, var1.isClientSide ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick);
   }

   @Override
   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         int var6 = 15 + var2.random.nextInt(15) + var2.random.nextInt(15);
         this.popExperience(var2, var3, var6);
      }
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable BlockGetter var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      Optional var5 = this.getSpawnEntityDisplayName(var1);
      if (var5.isPresent()) {
         var3.add((Component)var5.get());
      } else {
         var3.add(CommonComponents.EMPTY);
         var3.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
         var3.add(CommonComponents.space().append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
      }
   }

   private Optional<Component> getSpawnEntityDisplayName(ItemStack var1) {
      CompoundTag var2 = BlockItem.getBlockEntityData(var1);
      if (var2 != null && var2.contains("SpawnData", 10)) {
         String var3 = var2.getCompound("SpawnData").getCompound("entity").getString("id");
         ResourceLocation var4 = ResourceLocation.tryParse(var3);
         if (var4 != null) {
            return BuiltInRegistries.ENTITY_TYPE.getOptional(var4).map(var0 -> Component.translatable(var0.getDescriptionId()).withStyle(ChatFormatting.GRAY));
         }
      }

      return Optional.empty();
   }
}
