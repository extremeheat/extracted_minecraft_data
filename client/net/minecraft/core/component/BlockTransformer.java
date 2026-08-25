package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

public record BlockTransformer(List<BlockTransformData> transforms) {
   public static final Codec<BlockTransformer> DIRECT_CODEC;
   public static final Codec<Holder<BlockTransformer>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BlockTransformer>> STREAM_CODEC;

   public BlockTransformer {
      super();
   }

   public InteractionResult transformBlock(final UseOnContext context) {
      if (playerHasBlockingItemUseIntent(context)) {
         return InteractionResult.PASS;
      } else {
         BlockPos pos = context.getClickedPos();
         Level level = context.getLevel();

         for(BlockTransformData transformData : this.transforms) {
            Direction clickedFace = context.getClickedFace();
            if (!transformData.disallowedFaces().contains(clickedFace)) {
               BlockState newBlockState = transformData.blockStateProvider.getOptionalState(level, level.getRandom(), pos);
               if (newBlockState != null) {
                  BlockState updatedShape = transformData.updateFromNeighbors ? Block.updateFromNeighbourShapes(newBlockState, level, pos) : newBlockState;
                  Player player = context.getPlayer();
                  ItemStack itemInHand = context.getItemInHand();
                  if (player instanceof ServerPlayer) {
                     ServerPlayer serverPlayer = (ServerPlayer)player;
                     CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemInHand);
                  }

                  BlockState oldBlockState = level.getBlockState(pos);
                  if (level instanceof ServerLevel) {
                     ServerLevel serverLevel = (ServerLevel)level;
                     transformData.loot.ifPresent((lt) -> Block.dropFromBlockInteractLootTable(serverLevel, lt, pos, oldBlockState, level.getBlockEntity(pos), itemInHand, player, (sl, stack) -> transformData.dropStrategy.resourcePopper.pop(sl, pos, clickedFace, stack)));
                  }

                  if (itemInHand.isStackable()) {
                     itemInHand.consume(transformData.consumeOnUse ? 1 : 0, player);
                  } else if (player != null) {
                     itemInHand.hurtAndBreak(transformData.itemDamagePerUse, player, (EquipmentSlot)context.getHand().asEquipmentSlot());
                  }

                  level.setBlock(pos, updatedShape, 11);
                  level.playSound(player, (BlockPos)pos, transformData.sound.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                  transformData.particle.send(level, player, pos);
                  level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, updatedShape));
                  if (transformData.transformType == BlockTransformer.TransformType.COPPER_CHEST && oldBlockState.getBlock() instanceof CopperChestBlock && oldBlockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                     BlockPos neighborPos = ChestBlock.getConnectedBlockPos(pos, oldBlockState);
                     level.gameEvent(GameEvent.BLOCK_CHANGE, neighborPos, GameEvent.Context.of(player, level.getBlockState(neighborPos)));
                     transformData.particle.send(level, player, neighborPos);
                  }

                  return InteractionResult.SUCCESS;
               }
            }
         }

         return InteractionResult.PASS;
      }
   }

   private static boolean playerHasBlockingItemUseIntent(final UseOnContext context) {
      Player player = context.getPlayer();
      return context.getHand().equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && !player.isSecondaryUseActive();
   }

   static {
      DIRECT_CODEC = BlockTransformer.BlockTransformData.CODEC.listOf(1, 200).xmap(BlockTransformer::new, BlockTransformer::transforms);
      CODEC = RegistryCodecs.holder(Registries.BLOCK_TRANSFORMER);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.BLOCK_TRANSFORMER);
   }

   public static record BlockTransformData(BlockStateProvider blockStateProvider, Holder<SoundEvent> sound, TransformParticle particle, List<Direction> disallowedFaces, Optional<ResourceKey<LootTable>> loot, DropStrategy dropStrategy, boolean updateFromNeighbors, TransformType transformType, boolean consumeOnUse, int itemDamagePerUse) {
      public static final Codec<BlockTransformData> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.fieldOf("block_state_provider").forGetter(BlockTransformData::blockStateProvider), SoundEvent.CODEC.optionalFieldOf("sound", BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY)).forGetter(BlockTransformData::sound), BlockTransformer.TransformParticle.CODEC.optionalFieldOf("particle", BlockTransformer.TransformParticle.NONE).forGetter(BlockTransformData::particle), Direction.CODEC.listOf().optionalFieldOf("disallowed_faces", List.of()).forGetter(BlockTransformData::disallowedFaces), LootTable.KEY_CODEC.optionalFieldOf("loot").forGetter(BlockTransformData::loot), BlockTransformer.DropStrategy.CODEC.optionalFieldOf("drop_strategy", BlockTransformer.DropStrategy.FROM_MIDDLE).forGetter(BlockTransformData::dropStrategy), Codec.BOOL.optionalFieldOf("update_from_neighbors", true).forGetter(BlockTransformData::updateFromNeighbors), BlockTransformer.TransformType.CODEC.optionalFieldOf("transform_type", BlockTransformer.TransformType.SINGLE_BLOCK).forGetter(BlockTransformData::transformType), Codec.BOOL.optionalFieldOf("consume_on_use", true).forGetter(BlockTransformData::consumeOnUse), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("item_damage_per_use", 0).forGetter(BlockTransformData::itemDamagePerUse)).apply(i, BlockTransformData::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, BlockTransformData> STREAM_CODEC;

      public BlockTransformData {
         super();
      }

      public static Builder builder(final BlockStateProvider targetStateProvider) {
         return new Builder(targetStateProvider);
      }

      public static Builder builder(final BlockPredicate predicate, final Block block) {
         return builder(RuleBasedStateProvider.builder().ifTrueThenProvide(predicate, block).build());
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.fromCodecWithRegistries(BlockStateProvider.CODEC), BlockTransformData::blockStateProvider, SoundEvent.STREAM_CODEC, BlockTransformData::sound, BlockTransformer.TransformParticle.STREAM_CODEC, BlockTransformData::particle, Direction.STREAM_CODEC.apply(ByteBufCodecs.list()), BlockTransformData::disallowedFaces, ResourceKey.streamCodec(Registries.LOOT_TABLE).apply(ByteBufCodecs::optional), BlockTransformData::loot, BlockTransformer.DropStrategy.STREAM_CODEC, BlockTransformData::dropStrategy, ByteBufCodecs.BOOL, BlockTransformData::updateFromNeighbors, BlockTransformer.TransformType.STREAM_CODEC, BlockTransformData::transformType, ByteBufCodecs.BOOL, BlockTransformData::consumeOnUse, ByteBufCodecs.VAR_INT, BlockTransformData::itemDamagePerUse, BlockTransformData::new);
      }

      public static class Builder {
         private final BlockStateProvider targetStateProvider;
         private Holder<SoundEvent> sound;
         private TransformParticle particle;
         private List<Direction> disallowedFaces;
         private Optional<ResourceKey<LootTable>> loot;
         private DropStrategy dropStrategy;
         private boolean updateFromNeighbors;
         private TransformType transformType;
         private boolean consumeOnUse;
         private int itemDamagePerUse;

         private Builder(final BlockStateProvider targetStateProvider) {
            super();
            this.sound = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY);
            this.particle = BlockTransformer.TransformParticle.NONE;
            this.disallowedFaces = List.of();
            this.loot = Optional.empty();
            this.dropStrategy = BlockTransformer.DropStrategy.FROM_MIDDLE;
            this.updateFromNeighbors = true;
            this.transformType = BlockTransformer.TransformType.SINGLE_BLOCK;
            this.consumeOnUse = true;
            this.itemDamagePerUse = 1;
            this.targetStateProvider = targetStateProvider;
         }

         public Builder sound(final Holder<SoundEvent> sound) {
            this.sound = sound;
            return this;
         }

         public Builder particle(final TransformParticle particle) {
            this.particle = particle;
            return this;
         }

         public Builder disallowedFaces(final List<Direction> disallowedFaces) {
            this.disallowedFaces = disallowedFaces;
            return this;
         }

         public Builder loot(final ResourceKey<LootTable> loot) {
            this.loot = Optional.of(loot);
            return this;
         }

         public Builder dropStrategy(final DropStrategy dropStrategy) {
            this.dropStrategy = dropStrategy;
            return this;
         }

         public Builder updateFromNeighbors(final boolean updateFromNeighbors) {
            this.updateFromNeighbors = updateFromNeighbors;
            return this;
         }

         public Builder transformType(final TransformType transformType) {
            this.transformType = transformType;
            return this;
         }

         public Builder consumeOnUse(final boolean consumeOnUse) {
            this.consumeOnUse = consumeOnUse;
            return this;
         }

         public Builder itemDamagePerUse(final int itemDamagePerUse) {
            this.itemDamagePerUse = itemDamagePerUse;
            return this;
         }

         public BlockTransformData build() {
            return new BlockTransformData(this.targetStateProvider, this.sound, this.particle, this.disallowedFaces, this.loot, this.dropStrategy, this.updateFromNeighbors, this.transformType, this.consumeOnUse, this.itemDamagePerUse);
         }
      }
   }

   public static enum TransformParticle implements StringRepresentable {
      NONE(0, "none", 0),
      SCRAPE(1, "scrape", 3005),
      WAX_ON(2, "wax_on", 3003),
      WAX_OFF(3, "wax_off", 3004);

      private final int id;
      private final String name;
      private final @LevelEvent.Value int levelEvent;
      public static final Codec<TransformParticle> CODEC = StringRepresentable.<TransformParticle>fromValues(TransformParticle::values);
      private static final IntFunction<TransformParticle> BY_ID = ByIdMap.<TransformParticle>continuous(TransformParticle::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, TransformParticle> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, TransformParticle::getId);

      private TransformParticle(final @LevelEvent.Value int id, final String name, final int levelEvent) {
         this.id = id;
         this.name = name;
         this.levelEvent = levelEvent;
      }

      public void send(final Level level, final @Nullable Entity player, final BlockPos pos) {
         if (!this.equals(NONE)) {
            level.levelEvent(player, this.getLevelEvent(), pos, 0);
         }

      }

      public String getSerializedName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      public @LevelEvent.Value int getLevelEvent() {
         return this.levelEvent;
      }

      // $FF: synthetic method
      private static TransformParticle[] $values() {
         return new TransformParticle[]{NONE, SCRAPE, WAX_ON, WAX_OFF};
      }
   }

   public static enum DropStrategy implements StringRepresentable {
      CLICKED_FACE(0, "clicked_face", BlockTransformer.DropStrategy.ResourcePopper.FROM_FACE),
      FROM_MIDDLE(1, "from_middle", BlockTransformer.DropStrategy.ResourcePopper.FROM_MIDDLE);

      private final int id;
      private final String name;
      private final ResourcePopper resourcePopper;
      public static final Codec<DropStrategy> CODEC = StringRepresentable.<DropStrategy>fromValues(DropStrategy::values);
      private static final IntFunction<DropStrategy> BY_ID = ByIdMap.<DropStrategy>continuous(DropStrategy::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, DropStrategy> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DropStrategy::getId);

      private DropStrategy(final int id, final String name, final ResourcePopper resourcePopper) {
         this.id = id;
         this.name = name;
         this.resourcePopper = resourcePopper;
      }

      public String getSerializedName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      public void pop(final Level level, final BlockPos pos, final Direction direction, final ItemStack stack) {
         this.resourcePopper.pop(level, pos, direction, stack);
      }

      // $FF: synthetic method
      private static DropStrategy[] $values() {
         return new DropStrategy[]{CLICKED_FACE, FROM_MIDDLE};
      }

      @FunctionalInterface
      public interface ResourcePopper {
         ResourcePopper FROM_FACE = Block::popResourceFromFace;
         ResourcePopper FROM_MIDDLE = (level, pos, var2, stack) -> Block.popResource(level, pos, stack);

         void pop(final Level level, final BlockPos pos, final Direction direction, final ItemStack stack);
      }
   }

   public static enum TransformType implements StringRepresentable {
      SINGLE_BLOCK(0, "single_block"),
      COPPER_CHEST(1, "copper_chest");

      private final int id;
      private final String name;
      public static final Codec<TransformType> CODEC = StringRepresentable.<TransformType>fromValues(TransformType::values);
      private static final IntFunction<TransformType> BY_ID = ByIdMap.<TransformType>continuous(TransformType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, TransformType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, TransformType::getId);

      private TransformType(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static TransformType[] $values() {
         return new TransformType[]{SINGLE_BLOCK, COPPER_CHEST};
      }
   }
}
