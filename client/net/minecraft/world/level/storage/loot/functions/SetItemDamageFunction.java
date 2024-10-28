package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.slf4j.Logger;

public class SetItemDamageFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<SetItemDamageFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(NumberProviders.CODEC.fieldOf("damage").forGetter((var0x) -> {
         return var0x.damage;
      }), Codec.BOOL.fieldOf("add").orElse(false).forGetter((var0x) -> {
         return var0x.add;
      }))).apply(var0, SetItemDamageFunction::new);
   });
   private final NumberProvider damage;
   private final boolean add;

   private SetItemDamageFunction(List<LootItemCondition> var1, NumberProvider var2, boolean var3) {
      super(var1);
      this.damage = var2;
      this.add = var3;
   }

   public LootItemFunctionType<SetItemDamageFunction> getType() {
      return LootItemFunctions.SET_DAMAGE;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.damage.getReferencedContextParams();
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isDamageableItem()) {
         int var3 = var1.getMaxDamage();
         float var4 = this.add ? 1.0F - (float)var1.getDamageValue() / (float)var3 : 0.0F;
         float var5 = 1.0F - Mth.clamp(this.damage.getFloat(var2) + var4, 0.0F, 1.0F);
         var1.setDamageValue(Mth.floor(var5 * (float)var3));
      } else {
         LOGGER.warn("Couldn't set damage of loot item {}", var1);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider var0) {
      return simpleBuilder((var1) -> {
         return new SetItemDamageFunction(var1, var0, false);
      });
   }

   public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider var0, boolean var1) {
      return simpleBuilder((var2) -> {
         return new SetItemDamageFunction(var2, var0, var1);
      });
   }
}
