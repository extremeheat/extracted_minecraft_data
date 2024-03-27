package net.minecraft.advancements;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record AdvancementRewards(int c, List<ResourceKey<LootTable>> d, List<ResourceLocation> e, Optional<CacheableFunction> f) {
   private final int experience;
   private final List<ResourceKey<LootTable>> loot;
   private final List<ResourceLocation> recipes;
   private final Optional<CacheableFunction> function;
   public static final Codec<AdvancementRewards> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.INT.optionalFieldOf("experience", 0).forGetter(AdvancementRewards::experience),
               ResourceKey.codec(Registries.LOOT_TABLE).listOf().optionalFieldOf("loot", List.of()).forGetter(AdvancementRewards::loot),
               ResourceLocation.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes),
               CacheableFunction.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)
            )
            .apply(var0, AdvancementRewards::new)
   );
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());

   public AdvancementRewards(int var1, List<ResourceKey<LootTable>> var2, List<ResourceLocation> var3, Optional<CacheableFunction> var4) {
      super();
      this.experience = var1;
      this.loot = var2;
      this.recipes = var3;
      this.function = var4;
   }

   public void grant(ServerPlayer var1) {
      var1.giveExperiencePoints(this.experience);
      LootParams var2 = new LootParams.Builder(var1.serverLevel())
         .withParameter(LootContextParams.THIS_ENTITY, var1)
         .withParameter(LootContextParams.ORIGIN, var1.position())
         .create(LootContextParamSets.ADVANCEMENT_REWARD);
      boolean var3 = false;

      for(ResourceKey var5 : this.loot) {
         ObjectListIterator var6 = var1.server.reloadableRegistries().getLootTable(var5).getRandomItems(var2).iterator();

         while(var6.hasNext()) {
            ItemStack var7 = (ItemStack)var6.next();
            if (var1.addItem(var7)) {
               var1.level()
                  .playSound(
                     null,
                     var1.getX(),
                     var1.getY(),
                     var1.getZ(),
                     SoundEvents.ITEM_PICKUP,
                     SoundSource.PLAYERS,
                     0.2F,
                     ((var1.getRandom().nextFloat() - var1.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                  );
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
      this.function
         .flatMap(var1x -> var1x.get(var9.getFunctions()))
         .ifPresent(var2x -> var9.getFunctions().execute(var2x, var1.createCommandSourceStack().withSuppressedOutput().withPermission(2)));
   }

   public static class Builder {
      private int experience;
      private final com.google.common.collect.ImmutableList.Builder<ResourceKey<LootTable>> loot = ImmutableList.builder();
      private final com.google.common.collect.ImmutableList.Builder<ResourceLocation> recipes = ImmutableList.builder();
      private Optional<ResourceLocation> function = Optional.empty();

      public Builder() {
         super();
      }

      public static AdvancementRewards.Builder experience(int var0) {
         return new AdvancementRewards.Builder().addExperience(var0);
      }

      public AdvancementRewards.Builder addExperience(int var1) {
         this.experience += var1;
         return this;
      }

      public static AdvancementRewards.Builder loot(ResourceKey<LootTable> var0) {
         return new AdvancementRewards.Builder().addLootTable(var0);
      }

      public AdvancementRewards.Builder addLootTable(ResourceKey<LootTable> var1) {
         this.loot.add(var1);
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation var0) {
         return new AdvancementRewards.Builder().addRecipe(var0);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation var1) {
         this.recipes.add(var1);
         return this;
      }

      public static AdvancementRewards.Builder function(ResourceLocation var0) {
         return new AdvancementRewards.Builder().runs(var0);
      }

      public AdvancementRewards.Builder runs(ResourceLocation var1) {
         this.function = Optional.of(var1);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.build(), this.recipes.build(), this.function.map(CacheableFunction::new));
      }
   }
}
