package net.minecraft.server.players;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemExchangeValue;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.UnlockCondition;
import net.minecraft.world.level.mines.CustomIcons;

public record PlayerUnlock(String key, Optional<Holder<PlayerUnlock>> parent, List<Holder<PlayerUnlock>> disables, DisplayInfo display, Consumer<ServerPlayer> activation, Consumer<ServerPlayer> onMineEnter, List<UnlockCondition> madeVisibleBy, UnlockVisibility defaultVisibility, Map<TagKey<Item>, Float> experienceFactorForItemTag, Map<Item, Float> experienceFactorForItem, int unlockPrice, String exclusiveKey) {
   public static final Codec<Holder<PlayerUnlock>> CODEC;

   public PlayerUnlock(String var1, Optional<Holder<PlayerUnlock>> var2, List<Holder<PlayerUnlock>> var3, DisplayInfo var4, Consumer<ServerPlayer> var5, Consumer<ServerPlayer> var6, List<UnlockCondition> var7, UnlockVisibility var8, Map<TagKey<Item>, Float> var9, Map<Item, Float> var10, int var11, String var12) {
      super();
      this.key = var1;
      this.parent = var2;
      this.disables = var3;
      this.display = var4;
      this.activation = var5;
      this.onMineEnter = var6;
      this.madeVisibleBy = var7;
      this.defaultVisibility = var8;
      this.experienceFactorForItemTag = var9;
      this.experienceFactorForItem = var10;
      this.unlockPrice = var11;
      this.exclusiveKey = var12;
   }

   public static Builder root(String var0) {
      return new Builder(var0, Optional.empty(), Optional.of(new ClientAsset(ResourceLocation.withDefaultNamespace("unlock_backgrounds/" + var0))));
   }

   public static Builder child(String var0, Holder<PlayerUnlock> var1) {
      return new Builder(var0, Optional.of(var1), Optional.empty());
   }

   public static Holder<PlayerUnlock> getRoot(Holder<PlayerUnlock> var0) {
      Holder var1 = var0;

      while(true) {
         Optional var2 = ((PlayerUnlock)var1.value()).parent();
         if (var2.isEmpty()) {
            return var1;
         }

         var1 = (Holder)var2.get();
      }
   }

   static {
      CODEC = BuiltInRegistries.PLAYER_UNLOCK.holderByNameCodec();
   }

   public static enum UnlockVisibility implements StringRepresentable {
      VISIBLE,
      INVISIBLE,
      MYSTERY;

      public static StreamCodec<ByteBuf, UnlockVisibility> STREAM_CODEC = ByteBufCodecs.idMapper((var0) -> values()[var0], Enum::ordinal);

      private UnlockVisibility() {
      }

      public String getSerializedName() {
         return this.name();
      }

      // $FF: synthetic method
      private static UnlockVisibility[] $values() {
         return new UnlockVisibility[]{VISIBLE, INVISIBLE, MYSTERY};
      }
   }

   public static class Builder {
      private final String key;
      private final Optional<Holder<PlayerUnlock>> parent;
      private final DisplayInfo.Builder display;
      private final List<Consumer<ServerPlayer>> activation = new ArrayList();
      private final List<Consumer<ServerPlayer>> onMineEnter = new ArrayList();
      private final List<UnlockCondition> madeVisibleBy = new ArrayList();
      private final List<Holder<PlayerUnlock>> disables = new ArrayList();
      private final Map<TagKey<Item>, Float> experienceFactorForItemTag = new HashMap();
      private final Map<Item, Float> experienceFactorForItem = new HashMap();
      private UnlockVisibility defaultVisibility;
      int unlockPrice;
      private String exclusiveKey;

      public Builder(String var1, Optional<Holder<PlayerUnlock>> var2, Optional<ClientAsset> var3) {
         super();
         this.defaultVisibility = PlayerUnlock.UnlockVisibility.VISIBLE;
         this.unlockPrice = 1;
         this.exclusiveKey = "";
         this.key = var1;
         this.parent = var2;
         this.display = (new DisplayInfo.Builder()).withTitle(Component.translatable("unlocks.unlock." + var1 + ".name")).withDescription(Component.translatable("unlocks.unlock." + var1 + ".description")).withHint(Component.translatable("unlocks.unlock." + var1 + ".hint")).withType(AdvancementType.TASK).withAnnounceChat(true).withHidden(false).withShowToast(false);
         var3.ifPresent((var1x) -> this.display.withBackground(var1x));
      }

      public Builder withIcon(Supplier<ItemStack> var1) {
         this.display.withIcon(((ItemStack)var1.get()).copy());
         return this;
      }

      public Builder withIcon(Item var1) {
         this.display.withIcon(var1.getDefaultInstance());
         return this;
      }

      public Builder withCustomIcon(String var1) {
         ItemStack var2 = new ItemStack(Items.STONE);
         var2.set(DataComponents.ITEM_MODEL, CustomIcons.register(var1));
         this.display.withIcon(var2);
         return this;
      }

      public Builder withTitle(Component var1) {
         this.display.withTitle(var1);
         return this;
      }

      public Builder becomesVisibleWhen(UnlockCondition... var1) {
         this.madeVisibleBy.addAll(List.of(var1));
         return this;
      }

      public Builder withVisibility(UnlockVisibility var1) {
         this.defaultVisibility = var1;
         return this;
      }

      public Builder withPrice(int var1) {
         this.unlockPrice = var1;
         return this;
      }

      public Builder withExclusiveApplication(String var1) {
         this.exclusiveKey = var1;
         return this;
      }

      public Builder whenActivated(Consumer<ServerPlayer> var1) {
         this.activation.add(var1);
         return this;
      }

      public Builder givesAttributeModifier(Holder<Attribute> var1, double var2, AttributeModifier.Operation var4) {
         this.whenActivated((var5) -> {
            AttributeInstance var6 = var5.getAttribute(var1);
            if (var6 != null) {
               ResourceLocation var7 = ResourceLocation.withDefaultNamespace("unlock_" + this.key);
               var6.removeModifier(var7);
               var6.addPermanentModifier(new AttributeModifier(var7, var2, var4));
            }

         });
         return this;
      }

      public Builder onMineEnter(Consumer<ServerPlayer> var1) {
         this.onMineEnter.add(var1);
         return this;
      }

      public Builder givesItemInMine(Item... var1) {
         List var2 = Arrays.asList(var1);
         this.onMineEnter((var1x) -> var2.forEach((var1) -> {
               ItemStack var2 = var1.getDefaultInstance();
               var2.set(DataComponents.EXCHANGE_VALUE, new ItemExchangeValue(0.0F));
               var1x.addOrDropItem(var2);
            }));
         return this;
      }

      public Builder modifyExperienceGainForItem(TagKey<Item> var1, float var2) {
         this.experienceFactorForItemTag.computeIfPresent(var1, (var1x, var2x) -> var2x * var2);
         this.experienceFactorForItemTag.putIfAbsent(var1, var2);
         return this;
      }

      public Builder modifyExperienceGainForItem(Item var1, float var2) {
         this.experienceFactorForItem.computeIfPresent(var1, (var1x, var2x) -> var2x * var2);
         this.experienceFactorForItem.putIfAbsent(var1, var2);
         return this;
      }

      public Builder givesItemStackInMine(ItemStack... var1) {
         ArrayList var2 = new ArrayList();

         for(ItemStack var6 : var1) {
            var2.add(var6.copy());
         }

         this.onMineEnter((var1x) -> var2.forEach((var1) -> {
               var1.set(DataComponents.EXCHANGE_VALUE, new ItemExchangeValue(0.0F));
               var1x.addOrDropItem(var1.copy());
            }));
         return this;
      }

      public Builder givesItemStackInMine(Function<ServerPlayer, ItemStack> var1) {
         this.onMineEnter((var1x) -> {
            ItemStack var2 = ((ItemStack)var1.apply(var1x)).copy();
            var2.set(DataComponents.EXCHANGE_VALUE, new ItemExchangeValue(0.0F));
            var1x.addOrDropItem(var2);
         });
         return this;
      }

      @SafeVarargs
      public final Builder givesEnchantedItemInMine(Item var1, Pair<ResourceKey<Enchantment>, Integer>... var2) {
         List var3 = Arrays.asList(var2);
         this.onMineEnter((var2x) -> {
            ItemStack var3x = var1.getDefaultInstance();
            var3x.set(DataComponents.EXCHANGE_VALUE, new ItemExchangeValue(0.0F));

            for(Pair var5 : var3) {
               Optional var6 = var2x.serverLevel().registryAccess().get((ResourceKey)var5.first());
               var6.ifPresent((var2) -> var3x.enchant(var2, (Integer)var5.second()));
            }

            var2x.addOrDropItem(var3x);
         });
         return this;
      }

      public Builder givesEffectInMine(Holder<MobEffect> var1, int var2) {
         return this.givesEffectInMine(var1, var2, 0);
      }

      public Builder givesEffectInMine(Holder<MobEffect> var1, int var2, int var3) {
         MobEffectInstance var4 = new MobEffectInstance(var1, var2 == -1 ? -1 : var2 * 20, var3);
         this.onMineEnter((var1x) -> var1x.addEffect(var4, (Entity)null));
         return this;
      }

      public Builder givesPetInWorld(EntityType<?> var1) {
         this.whenActivated((var1x) -> PetUpgrades.addPet(var1));
         this.onMineEnter((var1x) -> PetUpgrades.getPet(var1, var1x));
         return this;
      }

      public Builder upgradePet(EntityType<?> var1, Class<? extends TamableAnimal> var2, Consumer<TamableAnimal> var3) {
         this.whenActivated((var2x) -> PetUpgrades.addUpgrade(var1, var3));
         this.onMineEnter((var2x) -> PetUpgrades.upgradePet(var1, var2, var2x));
         return this;
      }

      @SafeVarargs
      public final Builder disablesOtherUnlock(Holder<PlayerUnlock>... var1) {
         this.disables.addAll(List.of(var1));
         return this;
      }

      private PlayerUnlock build() {
         Consumer var1;
         if (this.activation.isEmpty()) {
            var1 = (var0) -> {
            };
         } else {
            List var2 = List.copyOf(this.activation);
            var1 = (var1x) -> var2.forEach((var1) -> var1.accept(var1x));
         }

         Consumer var4;
         if (this.onMineEnter.isEmpty()) {
            var4 = (var0) -> {
            };
         } else {
            List var3 = List.copyOf(this.onMineEnter);
            var4 = (var1x) -> var3.forEach((var1) -> var1.accept(var1x));
         }

         return new PlayerUnlock(this.key, this.parent, this.disables, this.display.build(), var1, var4, this.madeVisibleBy, this.defaultVisibility, this.experienceFactorForItemTag, this.experienceFactorForItem, this.unlockPrice, this.exclusiveKey);
      }

      public Holder<PlayerUnlock> register() {
         return Registry.registerForHolder(BuiltInRegistries.PLAYER_UNLOCK, ResourceLocation.withDefaultNamespace(this.key), this.build());
      }
   }
}
