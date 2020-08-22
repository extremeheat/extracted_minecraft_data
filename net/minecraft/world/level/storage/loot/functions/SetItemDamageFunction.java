package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetItemDamageFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RandomValueBounds damage;

   private SetItemDamageFunction(LootItemCondition[] var1, RandomValueBounds var2) {
      super(var1);
      this.damage = var2;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isDamageableItem()) {
         float var3 = 1.0F - this.damage.getFloat(var2.getRandom());
         var1.setDamageValue(Mth.floor(var3 * (float)var1.getMaxDamage()));
      } else {
         LOGGER.warn("Couldn't set damage of loot item {}", var1);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder setDamage(RandomValueBounds var0) {
      return simpleBuilder((var1) -> {
         return new SetItemDamageFunction(var1, var0);
      });
   }

   // $FF: synthetic method
   SetItemDamageFunction(LootItemCondition[] var1, RandomValueBounds var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("set_damage"), SetItemDamageFunction.class);
      }

      public void serialize(JsonObject var1, SetItemDamageFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("damage", var3.serialize(var2.damage));
      }

      public SetItemDamageFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return new SetItemDamageFunction(var3, (RandomValueBounds)GsonHelper.getAsObject(var1, "damage", var2, RandomValueBounds.class));
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
