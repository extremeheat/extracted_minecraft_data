package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction extends LootItemConditionalFunction {
   public static final Codec<SetLoreFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  Codec.BOOL.fieldOf("replace").orElse(false).forGetter(var0x -> var0x.replace),
                  ComponentSerialization.CODEC.listOf().fieldOf("lore").forGetter(var0x -> var0x.lore),
                  ExtraCodecs.strictOptionalField(LootContext.EntityTarget.CODEC, "entity").forGetter(var0x -> var0x.resolutionContext)
               )
            )
            .apply(var0, SetLoreFunction::new)
   );
   private final boolean replace;
   private final List<Component> lore;
   private final Optional<LootContext.EntityTarget> resolutionContext;

   public SetLoreFunction(List<LootItemCondition> var1, boolean var2, List<Component> var3, Optional<LootContext.EntityTarget> var4) {
      super(var1);
      this.replace = var2;
      this.lore = List.copyOf(var3);
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
      ListTag var3 = this.getLoreTag(var1, !this.lore.isEmpty());
      if (var3 != null) {
         if (this.replace) {
            var3.clear();
         }

         UnaryOperator var4 = SetNameFunction.createResolver(var2, this.resolutionContext.orElse(null));
         this.lore.stream().map(var4).map(Component.Serializer::toJson).map(StringTag::valueOf).forEach(var3::add);
      }

      return var1;
   }

   @Nullable
   private ListTag getLoreTag(ItemStack var1, boolean var2) {
      CompoundTag var3;
      if (var1.hasTag()) {
         var3 = var1.getTag();
      } else {
         if (!var2) {
            return null;
         }

         var3 = new CompoundTag();
         var1.setTag(var3);
      }

      CompoundTag var4;
      if (var3.contains("display", 10)) {
         var4 = var3.getCompound("display");
      } else {
         if (!var2) {
            return null;
         }

         var4 = new CompoundTag();
         var3.put("display", var4);
      }

      if (var4.contains("Lore", 9)) {
         return var4.getList("Lore", 8);
      } else if (var2) {
         ListTag var5 = new ListTag();
         var4.put("Lore", var5);
         return var5;
      } else {
         return null;
      }
   }

   public static SetLoreFunction.Builder setLore() {
      return new SetLoreFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetLoreFunction.Builder> {
      private boolean replace;
      private Optional<LootContext.EntityTarget> resolutionContext = Optional.empty();
      private final com.google.common.collect.ImmutableList.Builder<Component> lore = ImmutableList.builder();

      public Builder() {
         super();
      }

      public SetLoreFunction.Builder setReplace(boolean var1) {
         this.replace = var1;
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
         return new SetLoreFunction(this.getConditions(), this.replace, this.lore.build(), this.resolutionContext);
      }
   }
}
