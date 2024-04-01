package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public final class ItemStack implements DataComponentHolder {
   private static final Codec<Holder<Item>> ITEM_NON_AIR_CODEC = ExtraCodecs.validate(
      BuiltInRegistries.ITEM.holderByNameCodec(),
      var0 -> var0.is(Items.AIR.builtInRegistryHolder()) ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(var0)
   );
   public static final Codec<ItemStack> CODEC = ExtraCodecs.lazyInitializedCodec(
      () -> ExtraCodecs.validate(
            RecordCodecBuilder.create(
               var0 -> var0.group(
                        ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                        ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                        ExtraCodecs.strictOptionalField(DataComponentPatch.CODEC, "components", DataComponentPatch.EMPTY)
                           .forGetter(var0x -> var0x.components.asPatch())
                     )
                     .apply(var0, ItemStack::new)
            ),
            ItemStack::validate
         )
   );
   public static final Codec<ItemStack> SINGLE_ITEM_CODEC = ExtraCodecs.lazyInitializedCodec(
      () -> ExtraCodecs.validate(
            RecordCodecBuilder.create(
               var0 -> var0.group(
                        ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                        ExtraCodecs.strictOptionalField(DataComponentPatch.CODEC, "components", DataComponentPatch.EMPTY)
                           .forGetter(var0x -> var0x.components.asPatch())
                     )
                     .apply(var0, (var0x, var1) -> new ItemStack(var0x, 1, var1))
            ),
            ItemStack::validate
         )
   );
   public static final Codec<ItemStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(CODEC)
      .xmap(var0 -> var0.orElse(ItemStack.EMPTY), var0 -> var0.isEmpty() ? Optional.empty() : Optional.of(var0));
   public static final Codec<ItemStack> SIMPLE_ITEM_CODEC = ITEM_NON_AIR_CODEC.xmap(ItemStack::new, ItemStack::getItemHolder);
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> OPTIONAL_STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ItemStack>() {
      private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM);

      public ItemStack decode(RegistryFriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         if (var2 <= 0) {
            return ItemStack.EMPTY;
         } else {
            Holder var3 = ITEM_STREAM_CODEC.decode(var1);
            DataComponentPatch var4 = DataComponentPatch.STREAM_CODEC.decode(var1);
            return new ItemStack(var3, var2, var4);
         }
      }

      public void encode(RegistryFriendlyByteBuf var1, ItemStack var2) {
         if (var2.isEmpty()) {
            var1.writeVarInt(0);
         } else {
            var1.writeVarInt(var2.getCount());
            ITEM_STREAM_CODEC.encode(var1, var2.getItemHolder());
            DataComponentPatch.STREAM_CODEC.encode(var1, var2.components.asPatch());
         }
      }
   };
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ItemStack>() {
      public ItemStack decode(RegistryFriendlyByteBuf var1) {
         ItemStack var2 = ItemStack.OPTIONAL_STREAM_CODEC.decode(var1);
         if (var2.isEmpty()) {
            throw new DecoderException("Empty ItemStack not allowed");
         } else {
            return var2;
         }
      }

      public void encode(RegistryFriendlyByteBuf var1, ItemStack var2) {
         if (var2.isEmpty()) {
            throw new EncoderException("Empty ItemStack not allowed");
         } else {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(var1, var2);
         }
      }
   };
   public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> OPTIONAL_LIST_STREAM_CODEC = OPTIONAL_STREAM_CODEC.apply(
      ByteBufCodecs.collection(NonNullList::createWithCapacity)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
      ByteBufCodecs.collection(NonNullList::createWithCapacity)
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Void)null);
   private static final Component DISABLED_ITEM_TOOLTIP = Component.translatable("item.disabled").withStyle(ChatFormatting.RED);
   private int count;
   private int popTime;
   @Deprecated
   @Nullable
   private final Item item;
   final PatchedDataComponentMap components;
   @Nullable
   private Entity entityRepresentation;

   private static DataResult<ItemStack> validate(ItemStack var0) {
      return var0.getCount() > var0.getMaxStackSize()
         ? DataResult.error(() -> "Item stack with stack size of " + var0.getCount() + " was larger than maximum: " + var0.getMaxStackSize())
            .setPartial(() -> var0.copyWithCount(var0.getMaxStackSize()))
         : DataResult.success(var0);
   }

   public Optional<TooltipComponent> getTooltipImage() {
      return this.getItem().getTooltipImage(this);
   }

   @Override
   public DataComponentMap getComponents() {
      return (DataComponentMap)(!this.isEmpty() ? this.components : DataComponentMap.EMPTY);
   }

   public DataComponentPatch getComponentsPatch() {
      return !this.isEmpty() ? this.components.asPatch() : DataComponentPatch.EMPTY;
   }

   public ItemStack(ItemLike var1) {
      this(var1, 1);
   }

   public ItemStack(Holder<Item> var1) {
      this(var1.value(), 1);
   }

   public ItemStack(Holder<Item> var1, int var2, DataComponentPatch var3) {
      this((ItemLike)var1.value(), var2, PatchedDataComponentMap.fromPatch(((Item)var1.value()).components(), var3));
   }

   public ItemStack(Holder<Item> var1, int var2) {
      this((ItemLike)var1.value(), var2);
   }

   public ItemStack(ItemLike var1, int var2) {
      this(var1, var2, new PatchedDataComponentMap(var1.asItem().components()));
   }

   private ItemStack(ItemLike var1, int var2, PatchedDataComponentMap var3) {
      super();
      this.item = var1.asItem();
      this.count = var2;
      this.components = var3;
      this.getItem().verifyComponentsAfterLoad(this);
   }

   private ItemStack(@Nullable Void var1) {
      super();
      this.item = null;
      this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
   }

   public static Optional<ItemStack> parse(HolderLookup.Provider var0, Tag var1) {
      return CODEC.parse(var0.createSerializationContext(NbtOps.INSTANCE), var1)
         .resultOrPartial(var0x -> LOGGER.error("Tried to load invalid item: '{}'", var0x));
   }

   public static ItemStack parseOptional(HolderLookup.Provider var0, CompoundTag var1) {
      return var1.isEmpty() ? EMPTY : parse(var0, var1).orElse(EMPTY);
   }

   public boolean isEmpty() {
      return this == EMPTY || this.item == Items.AIR || this.count <= 0;
   }

   public boolean isItemEnabled(FeatureFlagSet var1) {
      return this.isEmpty() || this.getItem().isEnabled(var1);
   }

   public ItemStack split(int var1) {
      int var2 = Math.min(var1, this.getCount());
      ItemStack var3 = this.copyWithCount(var2);
      this.shrink(var2);
      return var3;
   }

   public ItemStack copyAndClear() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack var1 = this.copy();
         this.setCount(0);
         return var1;
      }
   }

   public Item getItem() {
      return this.isEmpty() ? Items.AIR : this.item;
   }

   public Holder<Item> getItemHolder() {
      return this.getItem().builtInRegistryHolder();
   }

   public boolean is(TagKey<Item> var1) {
      return this.getItem().builtInRegistryHolder().is(var1);
   }

   public boolean is(Item var1) {
      return this.getItem() == var1;
   }

   public boolean is(Predicate<Holder<Item>> var1) {
      return var1.test(this.getItem().builtInRegistryHolder());
   }

   public boolean is(Holder<Item> var1) {
      return this.getItem().builtInRegistryHolder() == var1;
   }

   public boolean is(HolderSet<Item> var1) {
      return var1.contains(this.getItemHolder());
   }

   public Stream<TagKey<Item>> getTags() {
      return this.getItem().builtInRegistryHolder().tags();
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      BlockPos var3 = var1.getClickedPos();
      if (var2 != null && !var2.getAbilities().mayBuild && !this.canPlaceOnBlockInAdventureMode(new BlockInWorld(var1.getLevel(), var3, false))) {
         return InteractionResult.PASS;
      } else {
         Item var4 = this.getItem();
         InteractionResult var5 = var4.useOn(var1);
         if (var2 != null && var5.shouldAwardStats()) {
            var2.awardStat(Stats.ITEM_USED.get(var4));
         }

         return var5;
      }
   }

   public float getDestroySpeed(BlockState var1) {
      return this.getItem().getDestroySpeed(this, var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return this.getItem().use(var1, var2, var3);
   }

   public ItemStack finishUsingItem(Level var1, LivingEntity var2) {
      return this.getItem().finishUsingItem(this, var1, var2);
   }

   public Tag save(HolderLookup.Provider var1, Tag var2) {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot encode empty ItemStack");
      } else {
         return Util.getOrThrow(CODEC.encode(this, var1.createSerializationContext(NbtOps.INSTANCE), var2), var1x -> new IllegalStateException(var1x));
      }
   }

   public Tag save(HolderLookup.Provider var1) {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot encode empty ItemStack");
      } else {
         return Util.getOrThrow(CODEC.encodeStart(var1.createSerializationContext(NbtOps.INSTANCE), this), IllegalStateException::new);
      }
   }

   public Tag saveOptional(HolderLookup.Provider var1) {
      return (Tag)(this.isEmpty() ? new CompoundTag() : this.save(var1, new CompoundTag()));
   }

   public int getMaxStackSize() {
      return this.getOrDefault(DataComponents.MAX_STACK_SIZE, Integer.valueOf(1));
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
   }

   public boolean isDamageableItem() {
      return this.has(DataComponents.MAX_DAMAGE) && !this.has(DataComponents.UNBREAKABLE) && this.has(DataComponents.DAMAGE);
   }

   public boolean isDamaged() {
      return this.isDamageableItem() && this.getDamageValue() > 0;
   }

   public int getDamageValue() {
      return Mth.clamp(this.getOrDefault(DataComponents.DAMAGE, Integer.valueOf(0)), 0, this.getMaxDamage());
   }

   public void setDamageValue(int var1) {
      this.set(DataComponents.DAMAGE, Mth.clamp(var1, 0, this.getMaxDamage()));
   }

   public int getMaxDamage() {
      return this.getOrDefault(DataComponents.MAX_DAMAGE, Integer.valueOf(0));
   }

   public void hurtAndBreak(int var1, RandomSource var2, @Nullable ServerPlayer var3, Runnable var4) {
      if (this.isDamageableItem()) {
         if (var1 > 0) {
            int var5 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int var6 = 0;

            for(int var7 = 0; var5 > 0 && var7 < var1; ++var7) {
               if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, var5, var2)) {
                  ++var6;
               }
            }

            var1 -= var6;
            if (var1 <= 0) {
               return;
            }
         }

         if (var3 != null && var1 != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(var3, this, this.getDamageValue() + var1);
         }

         int var8 = this.getDamageValue() + var1;
         this.setDamageValue(var8);
         if (var8 >= this.getMaxDamage()) {
            var4.run();
         }
      }
   }

   public void hurtAndBreak(int var1, LivingEntity var2, EquipmentSlot var3) {
      if (!var2.level().isClientSide) {
         if (var2 instanceof Player var4 && var4.hasInfiniteMaterials()) {
            return;
         }

         this.hurtAndBreak(var1, var2.getRandom(), (ServerPlayer)(var2 instanceof ServerPlayer var5 ? var5 : null), () -> {
            var2.broadcastBreakEvent(var3);
            Item var3xx = this.getItem();
            this.shrink(1);
            if (var2 instanceof Player) {
               ((Player)var2).awardStat(Stats.ITEM_BROKEN.get(var3xx));
            }

            this.setDamageValue(0);
         });
      }
   }

   public boolean isBarVisible() {
      return this.getItem().isBarVisible(this);
   }

   public int getBarWidth() {
      return this.getItem().getBarWidth(this);
   }

   public int getBarColor() {
      return this.getItem().getBarColor(this);
   }

   public boolean overrideStackedOnOther(Slot var1, ClickAction var2, Player var3) {
      return this.getItem().overrideStackedOnOther(this, var1, var2, var3);
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, Slot var2, ClickAction var3, Player var4, SlotAccess var5) {
      return this.getItem().overrideOtherStackedOnMe(this, var1, var2, var3, var4, var5);
   }

   public void hurtEnemy(LivingEntity var1, Player var2) {
      Item var3 = this.getItem();
      if (var3.hurtEnemy(this, var1, var2)) {
         var2.awardStat(Stats.ITEM_USED.get(var3));
      }
   }

   public void mineBlock(Level var1, BlockState var2, BlockPos var3, Player var4) {
      Item var5 = this.getItem();
      if (var5.mineBlock(this, var1, var2, var3, var4)) {
         var4.awardStat(Stats.ITEM_USED.get(var5));
      }
   }

   public boolean isCorrectToolForDrops(BlockState var1) {
      return this.getItem().isCorrectToolForDrops(this, var1);
   }

   public InteractionResult interactLivingEntity(Player var1, LivingEntity var2, InteractionHand var3) {
      return this.getItem().interactLivingEntity(this, var1, var2, var3);
   }

   public ItemStack copy() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack var1 = new ItemStack(this.getItem(), this.count, this.components.copy());
         var1.setPopTime(this.getPopTime());
         return var1;
      }
   }

   public ItemStack copyWithCount(int var1) {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack var2 = this.copy();
         var2.setCount(var1);
         return var2;
      }
   }

   public ItemStack transmuteCopy(ItemLike var1, int var2) {
      return this.isEmpty() ? EMPTY : this.transmuteCopyIgnoreEmpty(var1, var2);
   }

   public ItemStack transmuteCopyIgnoreEmpty(ItemLike var1, int var2) {
      return new ItemStack(var1.asItem().builtInRegistryHolder(), var2, this.components.asPatch());
   }

   public static boolean matches(ItemStack var0, ItemStack var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0.getCount() != var1.getCount() ? false : isSameItemSameComponents(var0, var1);
      }
   }

   @Deprecated
   public static boolean listMatches(List<ItemStack> var0, List<ItemStack> var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         for(int var2 = 0; var2 < var0.size(); ++var2) {
            if (!matches((ItemStack)var0.get(var2), (ItemStack)var1.get(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSameItem(ItemStack var0, ItemStack var1) {
      return var0.is(var1.getItem());
   }

   public static boolean isSameItemSameComponents(ItemStack var0, ItemStack var1) {
      if (!var0.is(var1.getItem())) {
         return false;
      } else {
         return var0.isEmpty() && var1.isEmpty() ? true : Objects.equals(var0.components, var1.components);
      }
   }

   public static MapCodec<ItemStack> optionalFieldOf(String var0) {
      return CODEC.optionalFieldOf(var0).xmap(var0x -> var0x.orElse(EMPTY), var0x -> var0x.isEmpty() ? Optional.empty() : Optional.of(var0x));
   }

   public static int hashItemAndComponents(@Nullable ItemStack var0) {
      if (var0 != null) {
         int var1 = 31 + var0.getItem().hashCode();
         return 31 * var1 + var0.getComponents().hashCode();
      } else {
         return 0;
      }
   }

   @Deprecated
   public static int hashStackList(List<ItemStack> var0) {
      int var1 = 0;

      for(ItemStack var3 : var0) {
         var1 = var1 * 31 + hashItemAndComponents(var3);
      }

      return var1;
   }

   public String getDescriptionId() {
      return this.getItem().getDescriptionId(this);
   }

   @Override
   public String toString() {
      return this.getCount() + " " + this.getItem();
   }

   public void inventoryTick(Level var1, Entity var2, int var3, boolean var4) {
      if (this.popTime > 0) {
         --this.popTime;
      }

      if (this.getItem() != null) {
         this.getItem().inventoryTick(this, var1, var2, var3, var4);
      }
   }

   public void onCraftedBy(Level var1, Player var2, int var3) {
      var2.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), var3);
      this.getItem().onCraftedBy(this, var1, var2);
   }

   public void onCraftedBySystem(Level var1) {
      this.getItem().onCraftedPostProcess(this, var1);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public UseAnim getUseAnimation() {
      return this.getItem().getUseAnimation(this);
   }

   public void releaseUsing(Level var1, LivingEntity var2, int var3) {
      this.getItem().releaseUsing(this, var1, var2, var3);
   }

   public boolean useOnRelease() {
      return this.getItem().useOnRelease(this);
   }

   @Nullable
   public <T> T set(DataComponentType<? super T> var1, @Nullable T var2) {
      return this.components.set(var1, (T)var2);
   }

   @Nullable
   public <T, U> T update(DataComponentType<T> var1, T var2, U var3, BiFunction<T, U, T> var4) {
      return this.set(var1, (T)var4.apply(this.getOrDefault(var1, var2), var3));
   }

   @Nullable
   public <T> T update(DataComponentType<T> var1, T var2, UnaryOperator<T> var3) {
      Object var4 = this.getOrDefault(var1, var2);
      return this.set(var1, (T)var3.apply(var4));
   }

   @Nullable
   public <T> T remove(DataComponentType<? extends T> var1) {
      return this.components.remove(var1);
   }

   public void applyComponents(DataComponentPatch var1) {
      this.components.applyPatch(var1);
      this.getItem().verifyComponentsAfterLoad(this);
   }

   public void applyComponents(DataComponentMap var1) {
      this.components.setAll(var1);
      this.getItem().verifyComponentsAfterLoad(this);
   }

   public Component getHoverName() {
      Component var1 = this.get(DataComponents.CUSTOM_NAME);
      return var1 != null ? var1 : this.getItem().getName(this);
   }

   private <T extends TooltipProvider> void addToTooltip(DataComponentType<T> var1, Consumer<Component> var2, TooltipFlag var3) {
      TooltipProvider var4 = this.get(var1);
      if (var4 != null) {
         var4.addToTooltip(var2, var3);
      }
   }

   public List<Component> getTooltipLines(@Nullable Player var1, TooltipFlag var2) {
      if (!var2.isCreative() && this.has(DataComponents.HIDE_TOOLTIP)) {
         return List.of();
      } else {
         ArrayList var3 = Lists.newArrayList();
         MutableComponent var4 = Component.empty().append(this.getHoverName()).withStyle(this.getRarity().color());
         if (this.has(DataComponents.CUSTOM_NAME)) {
            var4.withStyle(ChatFormatting.ITALIC);
         }

         var3.add(var4);
         if (!var2.isAdvanced() && !this.has(DataComponents.CUSTOM_NAME) && this.is(Items.FILLED_MAP)) {
            MapId var5 = this.get(DataComponents.MAP_ID);
            if (var5 != null) {
               var3.add(MapItem.getTooltipForId(var5));
            }
         }

         Consumer var9 = var3::add;
         if (!this.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)) {
            this.getItem().appendHoverText(this, var1 == null ? null : var1.level(), var3, var2);
         }

         this.addToTooltip(DataComponents.POTATO_BANE, var9, var2);
         this.addToTooltip(DataComponents.TRIM, var9, var2);
         this.addToTooltip(DataComponents.RESIN, var9, var2);
         this.addToTooltip(DataComponents.FLETCHING, var9, var2);
         this.addToTooltip(DataComponents.STORED_ENCHANTMENTS, var9, var2);
         this.addToTooltip(DataComponents.ENCHANTMENTS, var9, var2);
         this.addToTooltip(DataComponents.DYED_COLOR, var9, var2);
         this.addToTooltip(DataComponents.LORE, var9, var2);
         this.addToTooltip(DataComponents.XP, var9, var2);
         this.addAttributeTooltips(var9, var1);
         this.addToTooltip(DataComponents.UNBREAKABLE, var9, var2);
         this.addToTooltip(DataComponents.LUBRICATION, var9, var2);
         this.addToTooltip(DataComponents.BASE_COLOR, var9, var2);
         AdventureModePredicate var6 = this.get(DataComponents.CAN_BREAK);
         if (var6 != null && var6.showInTooltip()) {
            var9.accept(CommonComponents.EMPTY);
            var9.accept(AdventureModePredicate.CAN_BREAK_HEADER);
            var6.addToTooltip(var9);
         }

         AdventureModePredicate var7 = this.get(DataComponents.CAN_PLACE_ON);
         if (var7 != null && var7.showInTooltip()) {
            var9.accept(CommonComponents.EMPTY);
            var9.accept(AdventureModePredicate.CAN_PLACE_HEADER);
            var7.addToTooltip(var9);
         }

         if (var2.isAdvanced()) {
            if (this.isDamaged()) {
               var3.add(Component.translatable("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
            }

            var3.add(Component.literal(BuiltInRegistries.ITEM.getKey(this.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
            int var8 = this.components.size();
            if (var8 > 0) {
               var3.add(Component.translatable("item.components", var8).withStyle(ChatFormatting.DARK_GRAY));
            }
         }

         if (var1 != null && !this.getItem().isEnabled(var1.level().enabledFeatures())) {
            var3.add(DISABLED_ITEM_TOOLTIP);
         }

         return var3;
      }
   }

   private void addAttributeTooltips(Consumer<Component> var1, @Nullable Player var2) {
      ItemAttributeModifiers var3 = this.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
      if (var3.showInTooltip()) {
         for(EquipmentSlot var7 : EquipmentSlot.values()) {
            MutableBoolean var8 = new MutableBoolean(true);
            this.forEachModifier(var7, (var5, var6) -> {
               if (var6.amount() != 0.0) {
                  if (var8.isTrue()) {
                     var1.accept(CommonComponents.EMPTY);
                     var1.accept(Component.translatable("item.modifiers." + var7.getName()).withStyle(ChatFormatting.GRAY));
                     var8.setFalse();
                  }

                  this.addModifierTooltip(var1, var2, var5, var6);
               }
            });
         }
      }
   }

   private void addModifierTooltip(Consumer<Component> var1, @Nullable Player var2, Holder<Attribute> var3, AttributeModifier var4) {
      double var5 = var4.amount();
      boolean var7 = false;
      if (var2 != null) {
         if (var4.id() == Item.BASE_ATTACK_DAMAGE_UUID) {
            var5 += var2.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            var5 += (double)EnchantmentHelper.getDamageBonus(this, null);
            var7 = true;
         } else if (var4.id() == Item.BASE_ATTACK_SPEED_UUID) {
            var5 += var2.getAttributeBaseValue(Attributes.ATTACK_SPEED);
            var7 = true;
         }
      }

      double var8;
      if (var4.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || var4.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
         var8 = var5 * 100.0;
      } else if (var3.is(Attributes.KNOCKBACK_RESISTANCE)) {
         var8 = var5 * 10.0;
      } else {
         var8 = var5;
      }

      if (var7) {
         var1.accept(
            CommonComponents.space()
               .append(
                  Component.translatable(
                     "attribute.modifier.equals." + var4.operation().id(),
                     ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var8),
                     Component.translatable(((Attribute)var3.value()).getDescriptionId())
                  )
               )
               .withStyle(ChatFormatting.DARK_GREEN)
         );
      } else if (var5 > 0.0) {
         var1.accept(
            Component.translatable(
                  "attribute.modifier.plus." + var4.operation().id(),
                  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var8),
                  Component.translatable(((Attribute)var3.value()).getDescriptionId())
               )
               .withStyle(ChatFormatting.BLUE)
         );
      } else if (var5 < 0.0) {
         var1.accept(
            Component.translatable(
                  "attribute.modifier.take." + var4.operation().id(),
                  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-var8),
                  Component.translatable(((Attribute)var3.value()).getDescriptionId())
               )
               .withStyle(ChatFormatting.RED)
         );
      }
   }

   public boolean hasFoil() {
      Boolean var1 = this.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
      return var1 != null ? var1 : this.getItem().isFoil(this);
   }

   public Rarity getRarity() {
      Rarity var1 = this.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
      if (!this.isEnchanted()) {
         return var1;
      } else {
         return switch(var1) {
            case COMMON, UNCOMMON -> Rarity.RARE;
            case RARE -> Rarity.EPIC;
            default -> var1;
         };
      }
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         ItemEnchantments var1 = this.get(DataComponents.ENCHANTMENTS);
         return var1 != null && var1.isEmpty();
      }
   }

   public void enchant(Enchantment var1, int var2) {
      EnchantmentHelper.updateEnchantments(this, var2x -> var2x.upgrade(var1, var2));
   }

   public boolean isEnchanted() {
      return !this.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();
   }

   public boolean isFramed() {
      return this.entityRepresentation instanceof ItemFrame;
   }

   public void setEntityRepresentation(@Nullable Entity var1) {
      if (!this.isEmpty()) {
         this.entityRepresentation = var1;
      }
   }

   @Nullable
   public ItemFrame getFrame() {
      return this.entityRepresentation instanceof ItemFrame ? (ItemFrame)this.getEntityRepresentation() : null;
   }

   @Nullable
   public Entity getEntityRepresentation() {
      return !this.isEmpty() ? this.entityRepresentation : null;
   }

   public void forEachModifier(EquipmentSlot var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      ItemAttributeModifiers var3 = this.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
      if (!var3.modifiers().isEmpty()) {
         var3.forEach(var1, var2);
      } else {
         this.getItem().getDefaultAttributeModifiers(var1).forEach(var2);
      }
   }

   public Component getDisplayName() {
      MutableComponent var1 = Component.empty().append(this.getHoverName());
      if (this.has(DataComponents.CUSTOM_NAME)) {
         var1.withStyle(ChatFormatting.ITALIC);
      }

      MutableComponent var2 = ComponentUtils.wrapInSquareBrackets(var1);
      if (!this.isEmpty()) {
         var2.withStyle(this.getRarity().color())
            .withStyle(var1x -> var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(this))));
      }

      return var2;
   }

   public boolean canPlaceOnBlockInAdventureMode(BlockInWorld var1) {
      AdventureModePredicate var2 = this.get(DataComponents.CAN_PLACE_ON);
      return var2 != null && var2.test(var1);
   }

   public boolean canBreakBlockInAdventureMode(BlockInWorld var1) {
      AdventureModePredicate var2 = this.get(DataComponents.CAN_BREAK);
      return var2 != null && var2.test(var1);
   }

   public int getPopTime() {
      return this.popTime;
   }

   public void setPopTime(int var1) {
      this.popTime = var1;
   }

   public int getCount() {
      return this.isEmpty() ? 0 : this.count;
   }

   public void setCount(int var1) {
      this.count = var1;
   }

   public void limitSize(int var1) {
      if (!this.isEmpty() && this.getCount() > var1) {
         this.setCount(var1);
      }
   }

   public void grow(int var1) {
      this.setCount(this.getCount() + var1);
   }

   public void shrink(int var1) {
      this.grow(-var1);
   }

   public void consume(int var1, @Nullable LivingEntity var2) {
      if (var2 == null || !var2.hasInfiniteMaterials()) {
         this.shrink(var1);
      }
   }

   public void onUseTick(Level var1, LivingEntity var2, int var3) {
      this.getItem().onUseTick(var1, var2, this, var3);
   }

   public void onDestroyed(ItemEntity var1) {
      this.getItem().onDestroyed(var1);
   }

   public SoundEvent getDrinkingSound() {
      return this.getItem().getDrinkingSound();
   }

   public SoundEvent getEatingSound() {
      FoodProperties var1 = this.get(DataComponents.FOOD);
      return var1 == null ? SoundEvents.GENERIC_EAT.value() : var1.eatSound().value();
   }

   public SoundEvent getBreakingSound() {
      return this.getItem().getBreakingSound();
   }

   public boolean canBeHurtBy(DamageSource var1) {
      return !this.has(DataComponents.FIRE_RESISTANT) || !var1.is(DamageTypeTags.IS_FIRE);
   }

   public ItemStack makeFoil() {
      this.set(DataComponents.EXPLICIT_FOIL, true);
      return this;
   }
}
