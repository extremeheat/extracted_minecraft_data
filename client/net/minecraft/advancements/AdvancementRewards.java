package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
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
      super();
      this.experience = var1;
      this.loot = var2;
      this.recipes = var3;
      this.function = var4;
   }

   public ResourceLocation[] getRecipes() {
      return this.recipes;
   }

   public void grant(ServerPlayer var1) {
      var1.giveExperiencePoints(this.experience);
      LootContext var2 = (new LootContext.Builder(var1.getLevel())).withParameter(LootContextParams.THIS_ENTITY, var1).withParameter(LootContextParams.ORIGIN, var1.position()).withRandom(var1.getRandom()).create(LootContextParamSets.ADVANCEMENT_REWARD);
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
         var1.containerMenu.broadcastChanges();
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
      int var10000 = this.experience;
      return "AdvancementRewards{experience=" + var10000 + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + "}";
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

   public static AdvancementRewards deserialize(JsonObject var0) throws JsonParseException {
      int var1 = GsonHelper.getAsInt(var0, "experience", 0);
      JsonArray var2 = GsonHelper.getAsJsonArray(var0, "loot", new JsonArray());
      ResourceLocation[] var3 = new ResourceLocation[var2.size()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = new ResourceLocation(GsonHelper.convertToString(var2.get(var4), "loot[" + var4 + "]"));
      }

      JsonArray var7 = GsonHelper.getAsJsonArray(var0, "recipes", new JsonArray());
      ResourceLocation[] var5 = new ResourceLocation[var7.size()];

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = new ResourceLocation(GsonHelper.convertToString(var7.get(var6), "recipes[" + var6 + "]"));
      }

      CommandFunction.CacheableFunction var8;
      if (var0.has("function")) {
         var8 = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(var0, "function")));
      } else {
         var8 = CommandFunction.CacheableFunction.NONE;
      }

      return new AdvancementRewards(var1, var3, var5, var8);
   }

   static {
      EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public Builder() {
         super();
      }

      public static AdvancementRewards.Builder experience(int var0) {
         return (new AdvancementRewards.Builder()).addExperience(var0);
      }

      public AdvancementRewards.Builder addExperience(int var1) {
         this.experience += var1;
         return this;
      }

      public static AdvancementRewards.Builder loot(ResourceLocation var0) {
         return (new AdvancementRewards.Builder()).addLootTable(var0);
      }

      public AdvancementRewards.Builder addLootTable(ResourceLocation var1) {
         this.loot.add(var1);
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation var0) {
         return (new AdvancementRewards.Builder()).addRecipe(var0);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation var1) {
         this.recipes.add(var1);
         return this;
      }

      public static AdvancementRewards.Builder function(ResourceLocation var0) {
         return (new AdvancementRewards.Builder()).runs(var0);
      }

      public AdvancementRewards.Builder runs(ResourceLocation var1) {
         this.function = var1;
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray(new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray(new ResourceLocation[0]), this.function == null ? CommandFunction.CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
      }
   }
}
