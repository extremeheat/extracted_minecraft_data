package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetLoreFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ComponentSerialization.CODEC.sizeLimitedListOf(256).fieldOf("lore").forGetter(var0x -> var0x.lore),
                  ListOperation.codec(256).forGetter(var0x -> var0x.mode),
                  LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter(var0x -> var0x.resolutionContext)
               )
            )
            .apply(var0, SetLoreFunction::new)
   );
   private final List<Component> lore;
   private final ListOperation mode;
   private final Optional<LootContext.EntityTarget> resolutionContext;

   public SetLoreFunction(List<LootItemCondition> var1, List<Component> var2, ListOperation var3, Optional<LootContext.EntityTarget> var4) {
      super(var1);
      this.lore = List.copyOf(var2);
      this.mode = var3;
      this.resolutionContext = var4;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_LORE;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.resolutionContext.<Set<LootContextParam<?>>>map(var0 -> Set.of(var0.getParam())).orElseGet(Set::of);
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.LORE, ItemLore.EMPTY, var2x -> new ItemLore(this.updateLore(var2x, var2)));
      return var1;
   }

   private List<Component> updateLore(@Nullable ItemLore var1, LootContext var2) {
      if (var1 == null && this.lore.isEmpty()) {
         return List.of();
      } else {
         UnaryOperator var3 = SetNameFunction.createResolver(var2, this.resolutionContext.orElse(null));
         List var4 = this.lore.stream().map(var3).toList();
         return this.mode.apply(var1.lines(), var4, 256);
      }
   }

   public static SetLoreFunction.Builder setLore() {
      return new SetLoreFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetLoreFunction.Builder> {
      private Optional<LootContext.EntityTarget> resolutionContext = Optional.empty();
      private final com.google.common.collect.ImmutableList.Builder<Component> lore = ImmutableList.builder();
      private ListOperation mode = ListOperation.Append.INSTANCE;

      public Builder() {
         super();
      }

      public SetLoreFunction.Builder setMode(ListOperation var1) {
         this.mode = var1;
         return this;
      }

      public SetLoreFunction.Builder setResolutionContext(LootContext.EntityTarget var1) {
         this.resolutionContext = Optional.of(var1);
         return this;
      }

      public SetLoreFunction.Builder addLine(Component var1) {
         this.lore.add(var1);
         return this;
      }

      protected SetLoreFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetLoreFunction(this.getConditions(), this.lore.build(), this.mode, this.resolutionContext);
      }
   }
}
