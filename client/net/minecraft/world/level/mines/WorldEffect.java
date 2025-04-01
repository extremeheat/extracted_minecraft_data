package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.UnlockCondition;
import net.minecraft.world.level.biome.Biome;

public record WorldEffect(String key, Component name, Component description, Component unlockHint, @Nullable ResourceLocation itemModel, List<WorldEffectComponent> components, UnlockMode unlockMode, List<UnlockCondition> unlockedBy, List<WorldEffect> unlockedAfter, int requiredUnlockCount, Set<WorldEffectSet> inSets, Set<WorldEffect> incompatibleWith, float experienceModifier, int randomWeight, RandomizationMode randomizationMode, boolean multiplayerOnly, Consumer<ServerLevel> onMineEnter, Consumer<ServerLevel> onMineLeave, Consumer<ServerLevel> onMineTick) {
   public static final Codec<WorldEffect> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, WorldEffect> STREAM_CODEC;

   public WorldEffect(String var1, Component var2, Component var3, Component var4, @Nullable ResourceLocation var5, List<WorldEffectComponent> var6, UnlockMode var7, List<UnlockCondition> var8, List<WorldEffect> var9, int var10, Set<WorldEffectSet> var11, Set<WorldEffect> var12, float var13, int var14, RandomizationMode var15, boolean var16, Consumer<ServerLevel> var17, Consumer<ServerLevel> var18, Consumer<ServerLevel> var19) {
      super();
      this.key = var1;
      this.name = var2;
      this.description = var3;
      this.unlockHint = var4;
      this.itemModel = var5;
      this.components = var6;
      this.unlockMode = var7;
      this.unlockedBy = var8;
      this.unlockedAfter = var9;
      this.requiredUnlockCount = var10;
      this.inSets = var11;
      this.incompatibleWith = var12;
      this.experienceModifier = var13;
      this.randomWeight = var14;
      this.randomizationMode = var15;
      this.multiplayerOnly = var16;
      this.onMineEnter = var17;
      this.onMineLeave = var18;
      this.onMineTick = var19;
   }

   public boolean isValidWith(Collection<WorldEffect> var1) {
      return var1.stream().noneMatch((var1x) -> areIncompatible(var1x, this));
   }

   public static boolean areIncompatible(WorldEffect var0, WorldEffect var1) {
      return var0.incompatibleWith.contains(var1) || var1.incompatibleWith.contains(var0);
   }

   public boolean canUnlock(ServerLevel var1) {
      boolean var2;
      label23: {
         if (this.canUse(var1)) {
            if (this.requiredUnlockCount == 0) {
               break label23;
            }

            Stream var10000 = BuiltInRegistries.WORLD_EFFECT.stream();
            Objects.requireNonNull(var1);
            if (var10000.filter(var1::isEffectUnlocked).limit((long)this.requiredUnlockCount).count() == (long)this.requiredUnlockCount) {
               break label23;
            }
         }

         var2 = false;
         return var2;
      }

      var2 = true;
      return var2;
   }

   public boolean canUse(ServerLevel var1) {
      if (this.multiplayerOnly() && var1.theGame().server().isSingleplayer()) {
         return false;
      } else {
         return this.unlockMode != UnlockMode.NEVER_UNLOCKED;
      }
   }

   public boolean canRandomize(ServerLevel var1) {
      if (this.multiplayerOnly() && var1.theGame().server().isSingleplayer()) {
         return false;
      } else if (this.randomizationMode == RandomizationMode.NEVER) {
         return false;
      } else if (var1.isEffectUnlocked(this)) {
         return true;
      } else {
         return this.randomizationMode == RandomizationMode.WHEN_UNLOCKABLE ? this.canUnlock(var1) : false;
      }
   }

   public static Builder builder(String var0) {
      return new Builder(var0);
   }

   public <T extends WorldEffectComponent> Stream<T> componentsOfType(Class<T> var1) {
      return this.components.stream().flatMap((var1x) -> var1.isAssignableFrom(var1x.getClass()) ? Stream.of((WorldEffectComponent)var1.cast(var1x)) : Stream.of());
   }

   static {
      CODEC = BuiltInRegistries.WORLD_EFFECT.byNameCodec();
      STREAM_CODEC = ByteBufCodecs.registry(Registries.WORLD_EFFECT);
   }

   public static class Builder {
      private final String key;
      private Style nameStyle;
      @Nullable
      private ResourceLocation itemModel;
      private final List<WorldEffectComponent> components;
      private final List<UnlockCondition> unlockedBy;
      private final List<WorldEffect> unlockedAfter;
      private int requiredUnlockCount;
      private final Set<WorldEffectSet> inSets;
      private float xpModifier;
      private int randomWeight;
      private RandomizationMode randomizationMode;
      private UnlockMode unlockMode;
      private boolean multiplayerOnly;
      private final List<Consumer<ServerLevel>> onMineEnter;
      private final List<Consumer<ServerLevel>> onMineLeave;
      private final List<Consumer<ServerLevel>> onMineTick;
      private final Set<WorldEffect> incompatibleWith;

      public Builder(String var1) {
         super();
         this.nameStyle = Style.EMPTY.withColor(ChatFormatting.BLUE);
         this.components = new ArrayList();
         this.unlockedBy = new ArrayList();
         this.unlockedAfter = new ArrayList();
         this.inSets = new ObjectArraySet();
         this.xpModifier = 1.0F;
         this.randomWeight = 100;
         this.randomizationMode = RandomizationMode.WHEN_UNLOCKED;
         this.unlockMode = UnlockMode.UNLOCKED_ON_WIN;
         this.multiplayerOnly = false;
         this.onMineEnter = new ArrayList();
         this.onMineLeave = new ArrayList();
         this.onMineTick = new ArrayList();
         this.incompatibleWith = new ObjectArraySet();
         this.key = var1;
      }

      public Builder withNameStyle(Style var1) {
         this.nameStyle = var1;
         return this;
      }

      private Builder withItemModel(ResourceLocation var1) {
         this.itemModel = var1;
         return this;
      }

      public Builder withItemModelOf(Item var1) {
         return this.withItemModel((ResourceLocation)var1.components().get(DataComponents.ITEM_MODEL));
      }

      public Builder withCustomIcon(String var1) {
         return this.withItemModel(CustomIcons.register(var1));
      }

      public Builder withRandomizationWeight(int var1) {
         this.randomWeight = var1;
         return this;
      }

      public Builder alwaysRandomizable() {
         this.randomizationMode = RandomizationMode.WHEN_UNLOCKABLE;
         return this;
      }

      public Builder notRandomizable() {
         this.randomWeight = 0;
         this.randomizationMode = RandomizationMode.NEVER;
         return this;
      }

      public Builder neverUnlocked() {
         this.randomWeight = 0;
         this.randomizationMode = RandomizationMode.NEVER;
         this.unlockMode = UnlockMode.NEVER_UNLOCKED;
         return this;
      }

      public Builder multiplayerOnly() {
         this.multiplayerOnly = true;
         return this;
      }

      public Builder addComponent(WorldEffectComponent... var1) {
         this.components.addAll(List.of(var1));
         return this;
      }

      @SafeVarargs
      public final Builder addBiomes(ResourceKey<Biome>... var1) {
         for(ResourceKey var5 : var1) {
            this.components.add(new WorldGenEffect.AddBiome(new ResourceKey[]{var5}));
         }

         return this;
      }

      public final Builder modifyingWorldGen(Consumer<WorldGenBuilder> var1) {
         List var10000 = this.components;
         Objects.requireNonNull(var1);
         var10000.add(var1::accept);
         return this;
      }

      public Builder unlockedBy(UnlockCondition... var1) {
         this.unlockedBy.addAll(List.of(var1));
         this.unlockMode = UnlockMode.UNLOCKED_BY_CONDITION;
         return this;
      }

      public Builder unlockedByWinning() {
         this.unlockMode = UnlockMode.UNLOCKED_ON_WIN;
         return this;
      }

      public Builder unlockedByDefault() {
         this.unlockMode = UnlockMode.ALWAYS_UNLOCKED;
         return this;
      }

      public Builder xpModifier(float var1) {
         this.xpModifier = var1;
         return this;
      }

      public Builder unlockedAfter(WorldEffect... var1) {
         this.unlockedAfter.addAll(List.of(var1));
         return this;
      }

      public Builder requiresUnlockCount(int var1) {
         this.requiredUnlockCount = var1;
         return this;
      }

      public Builder inSet(WorldEffectSet var1) {
         this.inSets.add(var1);
         return this;
      }

      public Builder incompatibleWith(WorldEffect... var1) {
         this.incompatibleWith.addAll(List.of(var1));
         return this;
      }

      public Builder onMineEnter(Consumer<ServerLevel> var1) {
         this.onMineEnter.add(var1);
         return this;
      }

      public Builder onMineLeave(Consumer<ServerLevel> var1) {
         this.onMineLeave.add(var1);
         return this;
      }

      public Builder onPlayerMineEnter(Consumer<ServerPlayer> var1) {
         this.onMineEnter.add((Consumer)(var1x) -> var1x.players().forEach(var1));
         return this;
      }

      public Builder onMineTick(Consumer<ServerLevel> var1) {
         this.onMineTick.add(var1);
         return this;
      }

      public WorldEffect register() {
         return (WorldEffect)Registry.register(BuiltInRegistries.WORLD_EFFECT, (ResourceLocation)ResourceLocation.withDefaultNamespace(this.key), this.build());
      }

      private WorldEffect build() {
         MutableComponent var1 = Component.translatable("world.effect." + this.key + ".name").withStyle(this.nameStyle);
         MutableComponent var2 = Component.translatable("world.effect." + this.key + ".description");
         MutableComponent var3 = Component.translatable("world.effect." + this.key + ".hint");
         Consumer var4;
         if (this.onMineEnter.isEmpty()) {
            var4 = (var0) -> {
            };
         } else {
            List var5 = List.copyOf(this.onMineEnter);
            var4 = (var1x) -> var5.forEach((var1) -> var1.accept(var1x));
         }

         Consumer var8;
         if (this.onMineLeave.isEmpty()) {
            var8 = (var0) -> {
            };
         } else {
            List var6 = List.copyOf(this.onMineLeave);
            var8 = (var1x) -> var6.forEach((var1) -> var1.accept(var1x));
         }

         Consumer var9;
         if (this.onMineEnter.isEmpty()) {
            var9 = (var0) -> {
            };
         } else {
            List var7 = List.copyOf(this.onMineTick);
            var9 = (var1x) -> var7.forEach((var1) -> var1.accept(var1x));
         }

         WorldEffect var10 = new WorldEffect(this.key, var1, var2, var3, this.itemModel, this.components, this.unlockMode, this.unlockedBy, this.unlockedAfter, this.requiredUnlockCount, this.inSets, this.incompatibleWith, this.xpModifier, this.randomWeight, this.randomizationMode, this.multiplayerOnly, var4, var8, var9);
         this.inSets.forEach((var1x) -> var1x.register(var10));
         return var10;
      }
   }
}
