package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.MineCraftingMenu;
import net.minecraft.world.level.UnlockCondition;

public record SpecialMine(String key, Component name, Component description, List<WorldEffect> requiredEffects, List<List<WorldEffect>> randomEffects, List<UnlockCondition> unlockedBy, List<SpecialMine> unlockedAfter, int extraRandom) {
   final String key;
   final List<UnlockCondition> unlockedBy;
   public static final Codec<SpecialMine> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, SpecialMine> STREAM_CODEC;

   public SpecialMine(String var1, Component var2, Component var3, List<WorldEffect> var4, List<List<WorldEffect>> var5, List<UnlockCondition> var6, List<SpecialMine> var7, int var8) {
      super();
      this.key = var1;
      this.name = var2;
      this.description = var3;
      this.requiredEffects = var4;
      this.randomEffects = var5;
      this.unlockedBy = var6;
      this.unlockedAfter = var7;
      this.extraRandom = var8;
   }

   public static Builder builder(String var0) {
      return new Builder(var0);
   }

   public List<WorldEffect> instantiate(ServerLevel var1) {
      RandomSource var2 = var1.getRandom();
      ArrayList var3 = new ArrayList(this.requiredEffects);

      for(List var5 : this.randomEffects) {
         ArrayList var6 = new ArrayList(var5);
         WorldEffect var7 = null;

         while(var7 == null && !var6.isEmpty()) {
            var7 = (WorldEffect)Util.getRandom(var6, var2);
            if (!var7.isValidWith(var3)) {
               var6.remove(var7);
               var7 = null;
            }
         }

         if (var7 != null) {
            var3.add(var7);
         }
      }

      for(int var8 = 0; var8 < this.extraRandom; ++var8) {
         Optional var10000 = MineCraftingMenu.getRandomEffect(var1, var3, Set.of());
         Objects.requireNonNull(var3);
         var10000.ifPresent(var3::add);
      }

      return var3;
   }

   static {
      CODEC = BuiltInRegistries.SPECIAL_MINE.byNameCodec();
      STREAM_CODEC = ByteBufCodecs.registry(Registries.SPECIAL_MINE);
   }

   public static class Builder {
      private final String key;
      private Style nameStyle;
      private final List<WorldEffect> requiredEffects;
      private final List<List<WorldEffect>> randomEffects;
      private final List<UnlockCondition> unlockedBy;
      private final List<SpecialMine> unlockedAfter;
      private int extraRandom;

      public Builder(String var1) {
         super();
         this.nameStyle = Style.EMPTY.withColor(ChatFormatting.BLUE);
         this.requiredEffects = new ArrayList();
         this.randomEffects = new ArrayList();
         this.unlockedBy = new ArrayList();
         this.unlockedAfter = new ArrayList();
         this.extraRandom = 0;
         this.key = var1;
      }

      public Builder withNameStyle(Style var1) {
         this.nameStyle = var1;
         return this;
      }

      public Builder withRequiredEffects(WorldEffect... var1) {
         this.requiredEffects.addAll(List.of(var1));
         return this;
      }

      public Builder withOneOf(WorldEffect... var1) {
         this.randomEffects.add(List.of(var1));
         return this;
      }

      public Builder withOneOf(WorldEffectSet var1) {
         this.randomEffects.add(var1.effects());
         return this;
      }

      public Builder withRandomEffects(int var1) {
         this.extraRandom = var1;
         return this;
      }

      public Builder addUnlockedBy(UnlockCondition... var1) {
         this.unlockedBy.addAll(List.of(var1));
         return this;
      }

      public Builder unlockedAfter(SpecialMine... var1) {
         this.unlockedAfter.addAll(List.of(var1));
         return this;
      }

      public SpecialMine register() {
         SpecialMine var1 = this.build();
         if (var1.unlockedBy.isEmpty()) {
            if (!this.unlockedAfter.isEmpty()) {
               throw new IllegalStateException("Missing unlock condition for special mine " + this.key);
            }

            SpecialMines.DEFAULT_UNLOCKED.add(var1);
         }

         return (SpecialMine)Registry.register(BuiltInRegistries.SPECIAL_MINE, (String)var1.key, var1);
      }

      private SpecialMine build() {
         MutableComponent var1 = Component.translatable("mine." + this.key + ".name").withStyle(this.nameStyle);
         MutableComponent var2 = Component.translatable("mine." + this.key + ".description");
         return new SpecialMine(this.key, var1, var2, this.requiredEffects, this.randomEffects, this.unlockedBy, this.unlockedAfter, this.extraRandom);
      }
   }
}
