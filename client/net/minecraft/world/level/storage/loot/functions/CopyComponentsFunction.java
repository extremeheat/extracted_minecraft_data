package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
   public static final Codec<CopyComponentsFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  CopyComponentsFunction.Source.CODEC.fieldOf("source").forGetter(var0x -> var0x.source),
                  DataComponentType.CODEC.listOf().fieldOf("components").forGetter(var0x -> var0x.components)
               )
            )
            .apply(var0, CopyComponentsFunction::new)
   );
   private final CopyComponentsFunction.Source source;
   private final List<DataComponentType<?>> components;

   CopyComponentsFunction(List<LootItemCondition> var1, CopyComponentsFunction.Source var2, List<DataComponentType<?>> var3) {
      super(var1);
      this.source = var2;
      this.components = List.copyOf(var3);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_COMPONENTS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.source.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      DataComponentMap var3 = this.source.get(var2);
      var1.applyComponents(var3.filter(this.components::contains));
      return var1;
   }

   public static CopyComponentsFunction.Builder copyComponents(CopyComponentsFunction.Source var0) {
      return new CopyComponentsFunction.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyComponentsFunction.Builder> {
      private final CopyComponentsFunction.Source source;
      private final com.google.common.collect.ImmutableList.Builder<DataComponentType<?>> components = ImmutableList.builder();

      Builder(CopyComponentsFunction.Source var1) {
         super();
         this.source = var1;
      }

      public CopyComponentsFunction.Builder copy(DataComponentType<?> var1) {
         this.components.add(var1);
         return this;
      }

      protected CopyComponentsFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new CopyComponentsFunction(this.getConditions(), this.source, this.components.build());
      }
   }

   public static enum Source implements StringRepresentable {
      BLOCK_ENTITY("block_entity");

      public static final Codec<CopyComponentsFunction.Source> CODEC = StringRepresentable.fromValues(CopyComponentsFunction.Source::values);
      private final String name;

      private Source(String var3) {
         this.name = var3;
      }

      public DataComponentMap get(LootContext var1) {
         switch(this) {
            case BLOCK_ENTITY:
               BlockEntity var2 = var1.getParamOrNull(LootContextParams.BLOCK_ENTITY);
               return var2 != null ? var2.collectComponents() : DataComponentMap.EMPTY;
            default:
               throw new IncompatibleClassChangeError();
         }
      }

      public Set<LootContextParam<?>> getReferencedContextParams() {
         switch(this) {
            case BLOCK_ENTITY:
               return Set.of(LootContextParams.BLOCK_ENTITY);
            default:
               throw new IncompatibleClassChangeError();
         }
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
