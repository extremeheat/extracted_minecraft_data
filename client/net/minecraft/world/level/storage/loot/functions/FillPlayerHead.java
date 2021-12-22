package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead extends LootItemConditionalFunction {
   final LootContext.EntityTarget entityTarget;

   public FillPlayerHead(LootItemCondition[] var1, LootContext.EntityTarget var2) {
      super(var1);
      this.entityTarget = var2;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.FILL_PLAYER_HEAD;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.method_87(Items.PLAYER_HEAD)) {
         Entity var3 = (Entity)var2.getParamOrNull(this.entityTarget.getParam());
         if (var3 instanceof Player) {
            GameProfile var4 = ((Player)var3).getGameProfile();
            var1.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var4));
         }
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> fillPlayerHead(LootContext.EntityTarget var0) {
      return simpleBuilder((var1) -> {
         return new FillPlayerHead(var1, var0);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<FillPlayerHead> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, FillPlayerHead var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("entity", var3.serialize(var2.entityTarget));
      }

      public FillPlayerHead deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         LootContext.EntityTarget var4 = (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", var2, LootContext.EntityTarget.class);
         return new FillPlayerHead(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
