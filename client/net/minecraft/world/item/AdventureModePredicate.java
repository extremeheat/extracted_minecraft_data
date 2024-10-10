package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModePredicate {
   private static final Codec<AdventureModePredicate> SIMPLE_CODEC = BlockPredicate.CODEC
      .flatComapMap(var0 -> new AdventureModePredicate(List.of(var0), true), var0 -> DataResult.error(() -> "Cannot encode"));
   private static final Codec<AdventureModePredicate> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.nonEmptyList(BlockPredicate.CODEC.listOf()).fieldOf("predicates").forGetter(var0x -> var0x.predicates),
               Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(AdventureModePredicate::showInTooltip)
            )
            .apply(var0, AdventureModePredicate::new)
   );
   public static final Codec<AdventureModePredicate> CODEC = Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC);
   public static final StreamCodec<RegistryFriendlyByteBuf, AdventureModePredicate> STREAM_CODEC = StreamCodec.composite(
      BlockPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()),
      var0 -> var0.predicates,
      ByteBufCodecs.BOOL,
      AdventureModePredicate::showInTooltip,
      AdventureModePredicate::new
   );
   public static final Component CAN_BREAK_HEADER = Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY);
   public static final Component CAN_PLACE_HEADER = Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY);
   private static final Component UNKNOWN_USE = Component.translatable("item.canUse.unknown").withStyle(ChatFormatting.GRAY);
   private final List<BlockPredicate> predicates;
   private final boolean showInTooltip;
   @Nullable
   private List<Component> cachedTooltip;
   @Nullable
   private BlockInWorld lastCheckedBlock;
   private boolean lastResult;
   private boolean checksBlockEntity;

   public AdventureModePredicate(List<BlockPredicate> var1, boolean var2) {
      super();
      this.predicates = var1;
      this.showInTooltip = var2;
   }

   private static boolean areSameBlocks(BlockInWorld var0, @Nullable BlockInWorld var1, boolean var2) {
      if (var1 == null || var0.getState() != var1.getState()) {
         return false;
      } else if (!var2) {
         return true;
      } else if (var0.getEntity() == null && var1.getEntity() == null) {
         return true;
      } else if (var0.getEntity() != null && var1.getEntity() != null) {
         RegistryAccess var3 = var0.getLevel().registryAccess();
         return Objects.equals(var0.getEntity().saveWithId(var3), var1.getEntity().saveWithId(var3));
      } else {
         return false;
      }
   }

   public boolean test(BlockInWorld var1) {
      if (areSameBlocks(var1, this.lastCheckedBlock, this.checksBlockEntity)) {
         return this.lastResult;
      } else {
         this.lastCheckedBlock = var1;
         this.checksBlockEntity = false;

         for (BlockPredicate var3 : this.predicates) {
            if (var3.matches(var1)) {
               this.checksBlockEntity = this.checksBlockEntity | var3.requiresNbt();
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

   public void addToTooltip(Consumer<Component> var1) {
      this.tooltip().forEach(var1);
   }

   public AdventureModePredicate withTooltip(boolean var1) {
      return new AdventureModePredicate(this.predicates, var1);
   }

   private static List<Component> computeTooltip(List<BlockPredicate> var0) {
      for (BlockPredicate var2 : var0) {
         if (var2.blocks().isEmpty()) {
            return List.of(UNKNOWN_USE);
         }
      }

      return var0.stream()
         .flatMap(var0x -> var0x.blocks().orElseThrow().stream())
         .distinct()
         .map(var0x -> var0x.value().getName().withStyle(ChatFormatting.DARK_GRAY))
         .toList();
   }

   public boolean showInTooltip() {
      return this.showInTooltip;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof AdventureModePredicate var2) ? false : this.predicates.equals(var2.predicates) && this.showInTooltip == var2.showInTooltip;
      }
   }

   @Override
   public int hashCode() {
      return this.predicates.hashCode() * 31 + (this.showInTooltip ? 1 : 0);
   }

   @Override
   public String toString() {
      return "AdventureModePredicate{predicates=" + this.predicates + ", showInTooltip=" + this.showInTooltip + "}";
   }
}
