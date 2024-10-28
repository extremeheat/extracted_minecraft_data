package net.minecraft.advancements;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record AdvancementRewards(int experience, List<ResourceKey<LootTable>> loot, List<ResourceLocation> recipes, Optional<CacheableFunction> function) {
   public static final Codec<AdvancementRewards> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.optionalFieldOf("experience", 0).forGetter(AdvancementRewards::experience), ResourceKey.codec(Registries.LOOT_TABLE).listOf().optionalFieldOf("loot", List.of()).forGetter(AdvancementRewards::loot), ResourceLocation.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes), CacheableFunction.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)).apply(var0, AdvancementRewards::new);
   });
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());

   public AdvancementRewards(int experience, List<ResourceKey<LootTable>> loot, List<ResourceLocation> recipes, Optional<CacheableFunction> function) {
      super();
      this.experience = experience;
      this.loot = loot;
      this.recipes = recipes;
      this.function = function;
   }

   public void grant(ServerPlayer var1) {
      var1.giveExperiencePoints(this.experience);
      LootParams var2 = (new LootParams.Builder(var1.serverLevel())).withParameter(LootContextParams.THIS_ENTITY, var1).withParameter(LootContextParams.ORIGIN, var1.position()).create(LootContextParamSets.ADVANCEMENT_REWARD);
      boolean var3 = false;
      Iterator var4 = this.loot.iterator();

      while(var4.hasNext()) {
         ResourceKey var5 = (ResourceKey)var4.next();
         ObjectListIterator var6 = var1.server.reloadableRegistries().getLootTable(var5).getRandomItems(var2).iterator();

         while(var6.hasNext()) {
            ItemStack var7 = (ItemStack)var6.next();
            if (var1.addItem(var7)) {
               var1.level().playSound((Player)null, var1.getX(), var1.getY(), var1.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((var1.getRandom().nextFloat() - var1.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               var3 = true;
            } else {
               ItemEntity var8 = var1.drop(var7, false);
               if (var8 != null) {
                  var8.setNoPickUpDelay();
                  var8.setTarget(var1.getUUID());
               }
            }
         }
      }

      if (var3) {
         var1.containerMenu.broadcastChanges();
      }

      if (!this.recipes.isEmpty()) {
         var1.awardRecipesByKey(this.recipes);
      }

      MinecraftServer var9 = var1.server;
      this.function.flatMap((var1x) -> {
         return var1x.get(var9.getFunctions());
      }).ifPresent((var2x) -> {
         var9.getFunctions().execute(var2x, var1.createCommandSourceStack().withSuppressedOutput().withPermission(2));
      });
   }

   public int experience() {
      return this.experience;
   }

   public List<ResourceKey<LootTable>> loot() {
      return this.loot;
   }

   public List<ResourceLocation> recipes() {
      return this.recipes;
   }

   public Optional<CacheableFunction> function() {
      return this.function;
   }

   public static class Builder {
      private int experience;
      private final ImmutableList.Builder<ResourceKey<LootTable>> loot = ImmutableList.builder();
      private final ImmutableList.Builder<ResourceLocation> recipes = ImmutableList.builder();
      private Optional<ResourceLocation> function = Optional.empty();

      public Builder() {
         super();
      }

      public static Builder experience(int var0) {
         return (new Builder()).addExperience(var0);
      }

      public Builder addExperience(int var1) {
         this.experience += var1;
         return this;
      }

      public static Builder loot(ResourceKey<LootTable> var0) {
         return (new Builder()).addLootTable(var0);
      }

      public Builder addLootTable(ResourceKey<LootTable> var1) {
         this.loot.add(var1);
         return this;
      }

      public static Builder recipe(ResourceLocation var0) {
         return (new Builder()).addRecipe(var0);
      }

      public Builder addRecipe(ResourceLocation var1) {
         this.recipes.add(var1);
         return this;
      }

      public static Builder function(ResourceLocation var0) {
         return (new Builder()).runs(var0);
      }

      public Builder runs(ResourceLocation var1) {
         this.function = Optional.of(var1);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.build(), this.recipes.build(), this.function.map(CacheableFunction::new));
      }
   }
}
