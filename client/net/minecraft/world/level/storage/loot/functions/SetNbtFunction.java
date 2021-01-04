package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNbtFunction extends LootItemConditionalFunction {
   private final CompoundTag tag;

   private SetNbtFunction(LootItemCondition[] var1, CompoundTag var2) {
      super(var1);
      this.tag = var2;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.getOrCreateTag().merge(this.tag);
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setTag(CompoundTag var0) {
      return simpleBuilder((var1) -> {
         return new SetNbtFunction(var1, var0);
      });
   }

   // $FF: synthetic method
   SetNbtFunction(LootItemCondition[] var1, CompoundTag var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetNbtFunction> {
      public Serializer() {
         super(new ResourceLocation("set_nbt"), SetNbtFunction.class);
      }

      public void serialize(JsonObject var1, SetNbtFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("tag", var2.tag.toString());
      }

      public SetNbtFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         try {
            CompoundTag var4 = TagParser.parseTag(GsonHelper.getAsString(var1, "tag"));
            return new SetNbtFunction(var3, var4);
         } catch (CommandSyntaxException var5) {
            throw new JsonSyntaxException(var5.getMessage());
         }
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
