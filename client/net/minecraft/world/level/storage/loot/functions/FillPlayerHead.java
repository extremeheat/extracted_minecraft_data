package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead extends LootItemConditionalFunction {
   public static final MapCodec<FillPlayerHead> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter((var0x) -> {
         return var0x.entityTarget;
      })).apply(var0, FillPlayerHead::new);
   });
   private final LootContext.EntityTarget entityTarget;

   public FillPlayerHead(List<LootItemCondition> var1, LootContext.EntityTarget var2) {
      super(var1);
      this.entityTarget = var2;
   }

   public LootItemFunctionType<FillPlayerHead> getType() {
      return LootItemFunctions.FILL_PLAYER_HEAD;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.is(Items.PLAYER_HEAD)) {
         Object var4 = var2.getParamOrNull(this.entityTarget.getParam());
         if (var4 instanceof Player) {
            Player var3 = (Player)var4;
            var1.set(DataComponents.PROFILE, new ResolvableProfile(var3.getGameProfile()));
         }
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> fillPlayerHead(LootContext.EntityTarget var0) {
      return simpleBuilder((var1) -> {
         return new FillPlayerHead(var1, var0);
      });
   }
}
