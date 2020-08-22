package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY;
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final CommandFunction.CacheableFunction function;

   public AdvancementRewards(int var1, ResourceLocation[] var2, ResourceLocation[] var3, CommandFunction.CacheableFunction var4) {
      this.experience = var1;
      this.loot = var2;
      this.recipes = var3;
      this.function = var4;
   }

   public void grant(ServerPlayer var1) {
      var1.giveExperiencePoints(this.experience);
      LootContext var2 = (new LootContext.Builder(var1.getLevel())).withParameter(LootContextParams.THIS_ENTITY, var1).withParameter(LootContextParams.BLOCK_POS, new BlockPos(var1)).withRandom(var1.getRandom()).create(LootContextParamSets.ADVANCEMENT_REWARD);
      boolean var3 = false;
      ResourceLocation[] var4 = this.loot;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ResourceLocation var7 = var4[var6];
         Iterator var8 = var1.server.getLootTables().get(var7).getRandomItems(var2).iterator();

         while(var8.hasNext()) {
            ItemStack var9 = (ItemStack)var8.next();
            if (var1.addItem(var9)) {
               var1.level.playSound((Player)null, var1.getX(), var1.getY(), var1.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((var1.getRandom().nextFloat() - var1.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               var3 = true;
            } else {
               ItemEntity var10 = var1.drop(var9, false);
               if (var10 != null) {
                  var10.setNoPickUpDelay();
                  var10.setOwner(var1.getUUID());
               }
            }
         }
      }

      if (var3) {
         var1.inventoryMenu.broadcastChanges();
      }

      if (this.recipes.length > 0) {
         var1.awardRecipesByKey(this.recipes);
      }

      MinecraftServer var11 = var1.server;
      this.function.get(var11.getFunctions()).ifPresent((var2x) -> {
         var11.getFunctions().execute(var2x, var1.createCommandSourceStack().withSuppressedOutput().withPermission(2));
      });
   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serializeToJson() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.experience != 0) {
            var1.addProperty("experience", this.experience);
         }

         JsonArray var2;
         ResourceLocation[] var3;
         int var4;
         int var5;
         ResourceLocation var6;
         if (this.loot.length > 0) {
            var2 = new JsonArray();
            var3 = this.loot;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               var2.add(var6.toString());
            }

            var1.add("loot", var2);
         }

         if (this.recipes.length > 0) {
            var2 = new JsonArray();
            var3 = this.recipes;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               var2.add(var6.toString());
            }

            var1.add("recipes", var2);
         }

         if (this.function.getId() != null) {
            var1.addProperty("function", this.function.getId().toString());
         }

         return var1;
      }
   }

   static {
      EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
   }

   public static class Builder {
      private int experience;
      private final List loot = Lists.newArrayList();
      private final List recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int var0) {
         return (new AdvancementRewards.Builder()).addExperience(var0);
      }

      public AdvancementRewards.Builder addExperience(int var1) {
         this.experience += var1;
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation var0) {
         return (new AdvancementRewards.Builder()).addRecipe(var0);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation var1) {
         this.recipes.add(var1);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray(new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray(new ResourceLocation[0]), this.function == null ? CommandFunction.CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
      }
   }

   public static class Deserializer implements JsonDeserializer {
      public AdvancementRewards deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "rewards");
         int var5 = GsonHelper.getAsInt(var4, "experience", 0);
         JsonArray var6 = GsonHelper.getAsJsonArray(var4, "loot", new JsonArray());
         ResourceLocation[] var7 = new ResourceLocation[var6.size()];

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var7[var8] = new ResourceLocation(GsonHelper.convertToString(var6.get(var8), "loot[" + var8 + "]"));
         }

         JsonArray var12 = GsonHelper.getAsJsonArray(var4, "recipes", new JsonArray());
         ResourceLocation[] var9 = new ResourceLocation[var12.size()];

         for(int var10 = 0; var10 < var9.length; ++var10) {
            var9[var10] = new ResourceLocation(GsonHelper.convertToString(var12.get(var10), "recipes[" + var10 + "]"));
         }

         CommandFunction.CacheableFunction var11;
         if (var4.has("function")) {
            var11 = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(var4, "function")));
         } else {
            var11 = CommandFunction.CacheableFunction.NONE;
         }

         return new AdvancementRewards(var5, var7, var9, var11);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
