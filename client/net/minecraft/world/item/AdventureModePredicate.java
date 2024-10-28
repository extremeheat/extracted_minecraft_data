package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModePredicate {
   private static final Codec<AdventureModePredicate> SIMPLE_CODEC;
   private static final Codec<AdventureModePredicate> FULL_CODEC;
   public static final Codec<AdventureModePredicate> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, AdventureModePredicate> STREAM_CODEC;
   public static final Component CAN_BREAK_HEADER;
   public static final Component CAN_PLACE_HEADER;
   private static final Component UNKNOWN_USE;
   private final List<BlockPredicate> predicates;
   private final boolean showInTooltip;
   private final List<Component> tooltip;
   @Nullable
   private BlockInWorld lastCheckedBlock;
   private boolean lastResult;
   private boolean checksBlockEntity;

   private AdventureModePredicate(List<BlockPredicate> var1, boolean var2, List<Component> var3) {
      super();
      this.predicates = var1;
      this.showInTooltip = var2;
      this.tooltip = var3;
   }

   public AdventureModePredicate(List<BlockPredicate> var1, boolean var2) {
      super();
      this.predicates = var1;
      this.showInTooltip = var2;
      this.tooltip = computeTooltip(var1);
   }

   private static boolean areSameBlocks(BlockInWorld var0, @Nullable BlockInWorld var1, boolean var2) {
      if (var1 != null && var0.getState() == var1.getState()) {
         if (!var2) {
            return true;
         } else if (var0.getEntity() == null && var1.getEntity() == null) {
            return true;
         } else if (var0.getEntity() != null && var1.getEntity() != null) {
            RegistryAccess var3 = var0.getLevel().registryAccess();
            return Objects.equals(var0.getEntity().saveWithId(var3), var1.getEntity().saveWithId(var3));
         } else {
            return false;
         }
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
         Iterator var2 = this.predicates.iterator();

         BlockPredicate var3;
         do {
            if (!var2.hasNext()) {
               this.lastResult = false;
               return false;
            }

            var3 = (BlockPredicate)var2.next();
         } while(!var3.matches(var1));

         this.checksBlockEntity |= var3.requiresNbt();
         this.lastResult = true;
         return true;
      }
   }

   public void addToTooltip(Consumer<Component> var1) {
      this.tooltip.forEach(var1);
   }

   public AdventureModePredicate withTooltip(boolean var1) {
      return new AdventureModePredicate(this.predicates, var1, this.tooltip);
   }

   private static List<Component> computeTooltip(List<BlockPredicate> var0) {
      Iterator var1 = var0.iterator();

      BlockPredicate var2;
      do {
         if (!var1.hasNext()) {
            return var0.stream().flatMap((var0x) -> {
               return ((HolderSet)var0x.blocks().orElseThrow()).stream();
            }).distinct().map((var0x) -> {
               return ((Block)var0x.value()).getName().withStyle(ChatFormatting.DARK_GRAY);
            }).toList();
         }

         var2 = (BlockPredicate)var1.next();
      } while(!var2.blocks().isEmpty());

      return List.of(UNKNOWN_USE);
   }

   public boolean showInTooltip() {
      return this.showInTooltip;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AdventureModePredicate)) {
         return false;
      } else {
         AdventureModePredicate var2 = (AdventureModePredicate)var1;
         return this.predicates.equals(var2.predicates) && this.showInTooltip == var2.showInTooltip;
      }
   }

   public int hashCode() {
      return this.predicates.hashCode() * 31 + (this.showInTooltip ? 1 : 0);
   }

   public String toString() {
      String var10000 = String.valueOf(this.predicates);
      return "AdventureModePredicate{predicates=" + var10000 + ", showInTooltip=" + this.showInTooltip + "}";
   }

   static {
      SIMPLE_CODEC = BlockPredicate.CODEC.flatComapMap((var0) -> {
         return new AdventureModePredicate(List.of(var0), true);
      }, (var0) -> {
         return DataResult.error(() -> {
            return "Cannot encode";
         });
      });
      FULL_CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.nonEmptyList(BlockPredicate.CODEC.listOf()).fieldOf("predicates").forGetter((var0x) -> {
            return var0x.predicates;
         }), Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(AdventureModePredicate::showInTooltip)).apply(var0, AdventureModePredicate::new);
      });
      CODEC = Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC);
      STREAM_CODEC = StreamCodec.composite(BlockPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()), (var0) -> {
         return var0.predicates;
      }, ByteBufCodecs.BOOL, AdventureModePredicate::showInTooltip, AdventureModePredicate::new);
      CAN_BREAK_HEADER = Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY);
      CAN_PLACE_HEADER = Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY);
      UNKNOWN_USE = Component.translatable("item.canUse.unknown").withStyle(ChatFormatting.GRAY);
   }
}
