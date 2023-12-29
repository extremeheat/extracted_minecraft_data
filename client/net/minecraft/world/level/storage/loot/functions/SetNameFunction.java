package net.minecraft.world.level.storage.loot.functions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<SetNameFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ExtraCodecs.strictOptionalField(ComponentSerialization.CODEC, "name").forGetter(var0x -> var0x.name),
                  ExtraCodecs.strictOptionalField(LootContext.EntityTarget.CODEC, "entity").forGetter(var0x -> var0x.resolutionContext)
               )
            )
            .apply(var0, SetNameFunction::new)
   );
   private final Optional<Component> name;
   private final Optional<LootContext.EntityTarget> resolutionContext;

   private SetNameFunction(List<LootItemCondition> var1, Optional<Component> var2, Optional<LootContext.EntityTarget> var3) {
      super(var1);
      this.name = var2;
      this.resolutionContext = var3;
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
      this.name.ifPresent(var3 -> var1.setHoverName(createResolver(var2, this.resolutionContext.orElse(null)).apply(var3)));
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component var0) {
      return simpleBuilder(var1 -> new SetNameFunction(var1, Optional.of(var0), Optional.empty()));
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component var0, LootContext.EntityTarget var1) {
      return simpleBuilder(var2 -> new SetNameFunction(var2, Optional.of(var0), Optional.of(var1)));
   }
}
