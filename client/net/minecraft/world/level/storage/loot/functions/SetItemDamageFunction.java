package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.slf4j.Logger;

public class SetItemDamageFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final NumberProvider damage;
   final boolean add;

   SetItemDamageFunction(LootItemCondition[] var1, NumberProvider var2, boolean var3) {
      super(var1);
      this.damage = var2;
      this.add = var3;
   }

   public LootItemFunctionType getType() {
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

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemDamageFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetItemDamageFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("damage", var3.serialize(var2.damage));
         var1.addProperty("add", var2.add);
      }

      public SetItemDamageFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         NumberProvider var4 = (NumberProvider)GsonHelper.getAsObject(var1, "damage", var2, NumberProvider.class);
         boolean var5 = GsonHelper.getAsBoolean(var1, "add", false);
         return new SetItemDamageFunction(var3, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
