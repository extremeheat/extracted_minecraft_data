package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class AdventureModePredicate {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<AdventureModePredicate> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, AdventureModePredicate> STREAM_CODEC;
   public static final Component CAN_BREAK_HEADER;
   public static final Component CAN_PLACE_HEADER;
   private static final Component UNKNOWN_USE;
   private final List<BlockPredicate> predicates;
   private @Nullable List<Component> cachedTooltip;
   private @Nullable BlockInWorld lastCheckedBlock;
   private boolean lastResult;
   private boolean checksBlockEntity;

   public AdventureModePredicate(final List<BlockPredicate> predicates) {
      super();
      this.predicates = predicates;
   }

   private static boolean areSameBlocks(final BlockInWorld blockInWorld, final @Nullable BlockInWorld cachedBlock, final boolean checkBlockEntity) {
      if (cachedBlock != null && blockInWorld.getState() == cachedBlock.getState()) {
         if (!checkBlockEntity) {
            return true;
         } else if (blockInWorld.getEntity() == null && cachedBlock.getEntity() == null) {
            return true;
         } else if (blockInWorld.getEntity() != null && cachedBlock.getEntity() != null) {
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
               RegistryAccess registryAccess = blockInWorld.getLevel().registryAccess();
               CompoundTag inWorldTag = saveBlockEntity(blockInWorld.getEntity(), registryAccess, reporter);
               CompoundTag cachedTag = saveBlockEntity(cachedBlock.getEntity(), registryAccess, reporter);
               return Objects.equals(inWorldTag, cachedTag);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static CompoundTag saveBlockEntity(final BlockEntity blockEntity, final RegistryAccess registryAccess, final ProblemReporter reporter) {
      TagValueOutput inWorldOutput = TagValueOutput.createWithContext(reporter.forChild(blockEntity.problemPath()), registryAccess);
      blockEntity.saveWithId(inWorldOutput);
      return inWorldOutput.buildResult();
   }

   public boolean test(final BlockInWorld blockInWorld) {
      if (areSameBlocks(blockInWorld, this.lastCheckedBlock, this.checksBlockEntity)) {
         return this.lastResult;
      } else {
         this.lastCheckedBlock = blockInWorld;
         this.checksBlockEntity = false;

         for(BlockPredicate predicate : this.predicates) {
            if (predicate.matches(blockInWorld)) {
               this.checksBlockEntity |= predicate.requiresNbt();
               this.lastResult = true;
               return true;
            }
         }

         this.lastResult = false;
         return false;
      }
   }

   private List<Component> tooltip() {
      if (this.cachedTooltip == null) {
         this.cachedTooltip = computeTooltip(this.predicates);
      }

      return this.cachedTooltip;
   }

   public void addToTooltip(final Consumer<Component> consumer) {
      this.tooltip().forEach(consumer);
   }

   private static List<Component> computeTooltip(final List<BlockPredicate> predicates) {
      for(BlockPredicate predicate : predicates) {
         if (predicate.blocks().isEmpty()) {
            return List.of(UNKNOWN_USE);
         }
      }

      return predicates.stream().flatMap((predicatex) -> ((HolderSet)predicatex.blocks().orElseThrow()).stream()).distinct().map((block) -> ((Block)block.value()).getName().withStyle(ChatFormatting.DARK_GRAY)).toList();
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof AdventureModePredicate) {
         AdventureModePredicate predicate = (AdventureModePredicate)obj;
         return this.predicates.equals(predicate.predicates);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.predicates.hashCode();
   }

   public String toString() {
      return "AdventureModePredicate{predicates=" + String.valueOf(this.predicates) + "}";
   }

   static {
      CODEC = ExtraCodecs.compactListCodec(BlockPredicate.CODEC, ExtraCodecs.nonEmptyList(BlockPredicate.CODEC.listOf())).xmap(AdventureModePredicate::new, (p) -> p.predicates);
      STREAM_CODEC = StreamCodec.composite(BlockPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()), (predicate) -> predicate.predicates, AdventureModePredicate::new);
      CAN_BREAK_HEADER = Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY);
      CAN_PLACE_HEADER = Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY);
      UNKNOWN_USE = Component.translatable("item.canUse.unknown").withStyle(ChatFormatting.GRAY);
   }
}
