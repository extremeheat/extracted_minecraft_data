package net.minecraft.world.level.storage.loot.functions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<SetNameFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(var0x -> var0x.name),
                  LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter(var0x -> var0x.resolutionContext),
                  SetNameFunction.Target.CODEC.optionalFieldOf("target", SetNameFunction.Target.CUSTOM_NAME).forGetter(var0x -> var0x.target)
               )
            )
            .apply(var0, SetNameFunction::new)
   );
   private final Optional<Component> name;
   private final Optional<LootContext.EntityTarget> resolutionContext;
   private final SetNameFunction.Target target;

   private SetNameFunction(List<LootItemCondition> var1, Optional<Component> var2, Optional<LootContext.EntityTarget> var3, SetNameFunction.Target var4) {
      super(var1);
      this.name = var2;
      this.resolutionContext = var3;
      this.target = var4;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_NAME;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.resolutionContext.<Set<LootContextParam<?>>>map(var0 -> Set.of(var0.getParam())).orElse(Set.of());
   }

   public static UnaryOperator<Component> createResolver(LootContext var0, @Nullable LootContext.EntityTarget var1) {
      if (var1 != null) {
         Entity var2 = var0.getParamOrNull(var1.getParam());
         if (var2 != null) {
            CommandSourceStack var3 = var2.createCommandSourceStack().withPermission(2);
            return var2x -> {
               try {
                  return ComponentUtils.updateForEntity(var3, var2x, var2, 0);
               } catch (CommandSyntaxException var4) {
                  LOGGER.warn("Failed to resolve text component", var4);
                  return var2x;
               }
            };
         }
      }

      return var0x -> var0x;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      this.name.ifPresent(var3 -> var1.set(this.target.component(), createResolver(var2, this.resolutionContext.orElse(null)).apply(var3)));
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component var0, SetNameFunction.Target var1) {
      return simpleBuilder(var2 -> new SetNameFunction(var2, Optional.of(var0), Optional.empty(), var1));
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component var0, SetNameFunction.Target var1, LootContext.EntityTarget var2) {
      return simpleBuilder(var3 -> new SetNameFunction(var3, Optional.of(var0), Optional.of(var2), var1));
   }

   public static enum Target implements StringRepresentable {
      CUSTOM_NAME("custom_name"),
      ITEM_NAME("item_name");

      public static final Codec<SetNameFunction.Target> CODEC = StringRepresentable.fromEnum(SetNameFunction.Target::values);
      private final String name;

      private Target(String var3) {
         this.name = var3;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public DataComponentType<Component> component() {
         return switch (this) {
            case CUSTOM_NAME -> DataComponents.CUSTOM_NAME;
            case ITEM_NAME -> DataComponents.ITEM_NAME;
         };
      }
   }
}
