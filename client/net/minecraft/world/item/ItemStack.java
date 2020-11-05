package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
   public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Registry.ITEM.fieldOf("id").forGetter((var0x) -> {
         return var0x.item;
      }), Codec.INT.fieldOf("Count").forGetter((var0x) -> {
         return var0x.count;
      }), CompoundTag.CODEC.optionalFieldOf("tag").forGetter((var0x) -> {
         return Optional.ofNullable(var0x.tag);
      })).apply(var0, ItemStack::new);
   });
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Item)null);
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("#.##"), (var0) -> {
      var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private static final Style LORE_STYLE;
   private int count;
   private int popTime;
   @Deprecated
   private final Item item;
   private CompoundTag tag;
   private boolean emptyCacheFlag;
   private Entity entityRepresentation;
   private BlockInWorld cachedBreakBlock;
   private boolean cachedBreakBlockResult;
   private BlockInWorld cachedPlaceBlock;
   private boolean cachedPlaceBlockResult;

   public ItemStack(ItemLike var1) {
      this(var1, 1);
   }

   private ItemStack(ItemLike var1, int var2, Optional<CompoundTag> var3) {
      this(var1, var2);
      var3.ifPresent(this::setTag);
   }

   public ItemStack(ItemLike var1, int var2) {
      super();
      this.item = var1 == null ? null : var1.asItem();
      this.count = var2;
      if (this.item != null && this.item.canBeDepleted()) {
         this.setDamageValue(this.getDamageValue());
      }

      this.updateEmptyCacheFlag();
   }

   private void updateEmptyCacheFlag() {
      this.emptyCacheFlag = false;
      this.emptyCacheFlag = this.isEmpty();
   }

   private ItemStack(CompoundTag var1) {
      super();
      this.item = (Item)Registry.ITEM.get(new ResourceLocation(var1.getString("id")));
      this.count = var1.getByte("Count");
      if (var1.contains("tag", 10)) {
         this.tag = var1.getCompound("tag");
         this.getItem().verifyTagAfterLoad(var1);
      }

      if (this.getItem().canBeDepleted()) {
         this.setDamageValue(this.getDamageValue());
      }

      this.updateEmptyCacheFlag();
   }

   public static ItemStack of(CompoundTag var0) {
      try {
         return new ItemStack(var0);
      } catch (RuntimeException var2) {
         LOGGER.debug("Tried to load invalid item: {}", var0, var2);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      if (this == EMPTY) {
         return true;
      } else if (this.getItem() != null && !this.is(Items.AIR)) {
         return this.count <= 0;
      } else {
         return true;
      }
   }

   public ItemStack split(int var1) {
      int var2 = Math.min(var1, this.count);
      ItemStack var3 = this.copy();
      var3.setCount(var2);
      this.shrink(var2);
      return var3;
   }

   public Item getItem() {
      return this.emptyCacheFlag ? Items.AIR : this.item;
   }

   public boolean is(Tag<Item> var1) {
      return var1.contains(this.getItem());
   }

   public boolean is(Item var1) {
      return this.getItem() == var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      BlockPos var3 = var1.getClickedPos();
      BlockInWorld var4 = new BlockInWorld(var1.getLevel(), var3, false);
      if (var2 != null && !var2.getAbilities().mayBuild && !this.hasAdventureModePlaceTagForBlock(var1.getLevel().getTagManager(), var4)) {
         return InteractionResult.PASS;
      } else {
         Item var5 = this.getItem();
         InteractionResult var6 = var5.useOn(var1);
         if (var2 != null && var6.consumesAction()) {
            var2.awardStat(Stats.ITEM_USED.get(var5));
         }

         return var6;
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

   public CompoundTag save(CompoundTag var1) {
      ResourceLocation var2 = Registry.ITEM.getKey(this.getItem());
      var1.putString("id", var2 == null ? "minecraft:air" : var2.toString());
      var1.putByte("Count", (byte)this.count);
      if (this.tag != null) {
         var1.put("tag", this.tag.copy());
      }

      return var1;
   }

   public int getMaxStackSize() {
      return this.getItem().getMaxStackSize();
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
   }

   public boolean isDamageableItem() {
      if (!this.emptyCacheFlag && this.getItem().getMaxDamage() > 0) {
         CompoundTag var1 = this.getTag();
         return var1 == null || !var1.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageableItem() && this.getDamageValue() > 0;
   }

   public int getDamageValue() {
      return this.tag == null ? 0 : this.tag.getInt("Damage");
   }

   public void setDamageValue(int var1) {
      this.getOrCreateTag().putInt("Damage", Math.max(0, var1));
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage();
   }

   public boolean hurt(int var1, Random var2, @Nullable ServerPlayer var3) {
      if (!this.isDamageableItem()) {
         return false;
      } else {
         int var4;
         if (var1 > 0) {
            var4 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int var5 = 0;

            for(int var6 = 0; var4 > 0 && var6 < var1; ++var6) {
               if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, var4, var2)) {
                  ++var5;
               }
            }

            var1 -= var5;
            if (var1 <= 0) {
               return false;
            }
         }

         if (var3 != null && var1 != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(var3, this, this.getDamageValue() + var1);
         }

         var4 = this.getDamageValue() + var1;
         this.setDamageValue(var4);
         return var4 >= this.getMaxDamage();
      }
   }

   public <T extends LivingEntity> void hurtAndBreak(int var1, T var2, Consumer<T> var3) {
      if (!var2.level.isClientSide && (!(var2 instanceof Player) || !((Player)var2).getAbilities().instabuild)) {
         if (this.isDamageableItem()) {
            if (this.hurt(var1, var2.getRandom(), var2 instanceof ServerPlayer ? (ServerPlayer)var2 : null)) {
               var3.accept(var2);
               Item var4 = this.getItem();
               this.shrink(1);
               if (var2 instanceof Player) {
                  ((Player)var2).awardStat(Stats.ITEM_BROKEN.get(var4));
               }

               this.setDamageValue(0);
            }

         }
      }
   }

   public boolean isBarVisible() {
      return this.item.isBarVisible(this);
   }

   public int getBarWidth() {
      return this.item.getBarWidth(this);
   }

   public int getBarColor() {
      return this.item.getBarColor(this);
   }

   public boolean overrideStackedOnOther(ItemStack var1, ClickAction var2, Inventory var3) {
      return this.getItem().overrideStackedOnOther(this, var1, var2, var3);
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, ClickAction var2, Inventory var3) {
      return this.getItem().overrideOtherStackedOnMe(this, var1, var2, var3);
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
      return this.getItem().isCorrectToolForDrops(var1);
   }

   public InteractionResult interactLivingEntity(Player var1, LivingEntity var2, InteractionHand var3) {
      return this.getItem().interactLivingEntity(this, var1, var2, var3);
   }

   public ItemStack copy() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack var1 = new ItemStack(this.getItem(), this.count);
         var1.setPopTime(this.getPopTime());
         if (this.tag != null) {
            var1.tag = this.tag.copy();
         }

         return var1;
      }
   }

   public static boolean tagMatches(ItemStack var0, ItemStack var1) {
      if (var0.isEmpty() && var1.isEmpty()) {
         return true;
      } else if (!var0.isEmpty() && !var1.isEmpty()) {
         if (var0.tag == null && var1.tag != null) {
            return false;
         } else {
            return var0.tag == null || var0.tag.equals(var1.tag);
         }
      } else {
         return false;
      }
   }

   public static boolean matches(ItemStack var0, ItemStack var1) {
      if (var0.isEmpty() && var1.isEmpty()) {
         return true;
      } else {
         return !var0.isEmpty() && !var1.isEmpty() ? var0.matches(var1) : false;
      }
   }

   private boolean matches(ItemStack var1) {
      if (this.count != var1.count) {
         return false;
      } else if (!this.is(var1.getItem())) {
         return false;
      } else if (this.tag == null && var1.tag != null) {
         return false;
      } else {
         return this.tag == null || this.tag.equals(var1.tag);
      }
   }

   public static boolean isSame(ItemStack var0, ItemStack var1) {
      if (var0 == var1) {
         return true;
      } else {
         return !var0.isEmpty() && !var1.isEmpty() ? var0.sameItem(var1) : false;
      }
   }

   public static boolean isSameIgnoreDurability(ItemStack var0, ItemStack var1) {
      if (var0 == var1) {
         return true;
      } else {
         return !var0.isEmpty() && !var1.isEmpty() ? var0.sameItemStackIgnoreDurability(var1) : false;
      }
   }

   public boolean sameItem(ItemStack var1) {
      return !var1.isEmpty() && this.is(var1.getItem());
   }

   public boolean sameItemStackIgnoreDurability(ItemStack var1) {
      if (!this.isDamageableItem()) {
         return this.sameItem(var1);
      } else {
         return !var1.isEmpty() && this.is(var1.getItem());
      }
   }

   public static boolean isSameItemSameTags(ItemStack var0, ItemStack var1) {
      return var0.is(var1.getItem()) && tagMatches(var0, var1);
   }

   public String getDescriptionId() {
      return this.getItem().getDescriptionId(this);
   }

   public String toString() {
      return this.count + " " + this.getItem();
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

   public boolean hasTag() {
      return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }

   public CompoundTag getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new CompoundTag());
      }

      return this.tag;
   }

   public CompoundTag getOrCreateTagElement(String var1) {
      if (this.tag != null && this.tag.contains(var1, 10)) {
         return this.tag.getCompound(var1);
      } else {
         CompoundTag var2 = new CompoundTag();
         this.addTagElement(var1, var2);
         return var2;
      }
   }

   @Nullable
   public CompoundTag getTagElement(String var1) {
      return this.tag != null && this.tag.contains(var1, 10) ? this.tag.getCompound(var1) : null;
   }

   public void removeTagKey(String var1) {
      if (this.tag != null && this.tag.contains(var1)) {
         this.tag.remove(var1);
         if (this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public ListTag getEnchantmentTags() {
      return this.tag != null ? this.tag.getList("Enchantments", 10) : new ListTag();
   }

   public void setTag(@Nullable CompoundTag var1) {
      this.tag = var1;
      if (this.getItem().canBeDepleted()) {
         this.setDamageValue(this.getDamageValue());
      }

   }

   public Component getHoverName() {
      CompoundTag var1 = this.getTagElement("display");
      if (var1 != null && var1.contains("Name", 8)) {
         try {
            MutableComponent var2 = Component.Serializer.fromJson(var1.getString("Name"));
            if (var2 != null) {
               return var2;
            }

            var1.remove("Name");
         } catch (JsonParseException var3) {
            var1.remove("Name");
         }
      }

      return this.getItem().getName(this);
   }

   public ItemStack setHoverName(@Nullable Component var1) {
      CompoundTag var2 = this.getOrCreateTagElement("display");
      if (var1 != null) {
         var2.putString("Name", Component.Serializer.toJson(var1));
      } else {
         var2.remove("Name");
      }

      return this;
   }

   public void resetHoverName() {
      CompoundTag var1 = this.getTagElement("display");
      if (var1 != null) {
         var1.remove("Name");
         if (var1.isEmpty()) {
            this.removeTagKey("display");
         }
      }

      if (this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasCustomHoverName() {
      CompoundTag var1 = this.getTagElement("display");
      return var1 != null && var1.contains("Name", 8);
   }

   public List<Component> getTooltipLines(@Nullable Player var1, TooltipFlag var2) {
      ArrayList var3 = Lists.newArrayList();
      MutableComponent var4 = (new TextComponent("")).append(this.getHoverName()).withStyle(this.getRarity().color);
      if (this.hasCustomHoverName()) {
         var4.withStyle(ChatFormatting.ITALIC);
      }

      var3.add(var4);
      if (!var2.isAdvanced() && !this.hasCustomHoverName() && this.is(Items.FILLED_MAP)) {
         var3.add((new TextComponent("#" + MapItem.getMapId(this))).withStyle(ChatFormatting.GRAY));
      }

      int var5 = this.getHideFlags();
      if (shouldShowInTooltip(var5, ItemStack.TooltipPart.ADDITIONAL)) {
         this.getItem().appendHoverText(this, var1 == null ? null : var1.level, var3, var2);
      }

      int var8;
      if (this.hasTag()) {
         if (shouldShowInTooltip(var5, ItemStack.TooltipPart.ENCHANTMENTS)) {
            appendEnchantmentNames(var3, this.getEnchantmentTags());
         }

         if (this.tag.contains("display", 10)) {
            CompoundTag var6 = this.tag.getCompound("display");
            if (shouldShowInTooltip(var5, ItemStack.TooltipPart.DYE) && var6.contains("color", 99)) {
               if (var2.isAdvanced()) {
                  var3.add((new TranslatableComponent("item.color", new Object[]{String.format("#%06X", var6.getInt("color"))})).withStyle(ChatFormatting.GRAY));
               } else {
                  var3.add((new TranslatableComponent("item.dyed")).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}));
               }
            }

            if (var6.getTagType("Lore") == 9) {
               ListTag var7 = var6.getList("Lore", 8);

               for(var8 = 0; var8 < var7.size(); ++var8) {
                  String var9 = var7.getString(var8);

                  try {
                     MutableComponent var10 = Component.Serializer.fromJson(var9);
                     if (var10 != null) {
                        var3.add(ComponentUtils.mergeStyles(var10, LORE_STYLE));
                     }
                  } catch (JsonParseException var19) {
                     var6.remove("Lore");
                  }
               }
            }
         }
      }

      int var22;
      if (shouldShowInTooltip(var5, ItemStack.TooltipPart.MODIFIERS)) {
         EquipmentSlot[] var20 = EquipmentSlot.values();
         var22 = var20.length;

         for(var8 = 0; var8 < var22; ++var8) {
            EquipmentSlot var23 = var20[var8];
            Multimap var24 = this.getAttributeModifiers(var23);
            if (!var24.isEmpty()) {
               var3.add(TextComponent.EMPTY);
               var3.add((new TranslatableComponent("item.modifiers." + var23.getName())).withStyle(ChatFormatting.GRAY));
               Iterator var11 = var24.entries().iterator();

               while(var11.hasNext()) {
                  Entry var12 = (Entry)var11.next();
                  AttributeModifier var13 = (AttributeModifier)var12.getValue();
                  double var14 = var13.getAmount();
                  boolean var18 = false;
                  if (var1 != null) {
                     if (var13.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                        var14 += var1.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                        var14 += (double)EnchantmentHelper.getDamageBonus(this, MobType.UNDEFINED);
                        var18 = true;
                     } else if (var13.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                        var14 += var1.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                        var18 = true;
                     }
                  }

                  double var16;
                  if (var13.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && var13.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                     if (((Attribute)var12.getKey()).equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        var16 = var14 * 10.0D;
                     } else {
                        var16 = var14;
                     }
                  } else {
                     var16 = var14 * 100.0D;
                  }

                  if (var18) {
                     var3.add((new TextComponent(" ")).append(new TranslatableComponent("attribute.modifier.equals." + var13.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(var16), new TranslatableComponent(((Attribute)var12.getKey()).getDescriptionId())})).withStyle(ChatFormatting.DARK_GREEN));
                  } else if (var14 > 0.0D) {
                     var3.add((new TranslatableComponent("attribute.modifier.plus." + var13.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(var16), new TranslatableComponent(((Attribute)var12.getKey()).getDescriptionId())})).withStyle(ChatFormatting.BLUE));
                  } else if (var14 < 0.0D) {
                     var16 *= -1.0D;
                     var3.add((new TranslatableComponent("attribute.modifier.take." + var13.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(var16), new TranslatableComponent(((Attribute)var12.getKey()).getDescriptionId())})).withStyle(ChatFormatting.RED));
                  }
               }
            }
         }
      }

      if (this.hasTag()) {
         if (shouldShowInTooltip(var5, ItemStack.TooltipPart.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
            var3.add((new TranslatableComponent("item.unbreakable")).withStyle(ChatFormatting.BLUE));
         }

         ListTag var21;
         if (shouldShowInTooltip(var5, ItemStack.TooltipPart.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
            var21 = this.tag.getList("CanDestroy", 8);
            if (!var21.isEmpty()) {
               var3.add(TextComponent.EMPTY);
               var3.add((new TranslatableComponent("item.canBreak")).withStyle(ChatFormatting.GRAY));

               for(var22 = 0; var22 < var21.size(); ++var22) {
                  var3.addAll(expandBlockState(var21.getString(var22)));
               }
            }
         }

         if (shouldShowInTooltip(var5, ItemStack.TooltipPart.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
            var21 = this.tag.getList("CanPlaceOn", 8);
            if (!var21.isEmpty()) {
               var3.add(TextComponent.EMPTY);
               var3.add((new TranslatableComponent("item.canPlace")).withStyle(ChatFormatting.GRAY));

               for(var22 = 0; var22 < var21.size(); ++var22) {
                  var3.addAll(expandBlockState(var21.getString(var22)));
               }
            }
         }
      }

      if (var2.isAdvanced()) {
         if (this.isDamaged()) {
            var3.add(new TranslatableComponent("item.durability", new Object[]{this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()}));
         }

         var3.add((new TextComponent(Registry.ITEM.getKey(this.getItem()).toString())).withStyle(ChatFormatting.DARK_GRAY));
         if (this.hasTag()) {
            var3.add((new TranslatableComponent("item.nbt_tags", new Object[]{this.tag.getAllKeys().size()})).withStyle(ChatFormatting.DARK_GRAY));
         }
      }

      return var3;
   }

   private static boolean shouldShowInTooltip(int var0, ItemStack.TooltipPart var1) {
      return (var0 & var1.getMask()) == 0;
   }

   private int getHideFlags() {
      return this.hasTag() && this.tag.contains("HideFlags", 99) ? this.tag.getInt("HideFlags") : 0;
   }

   public void hideTooltipPart(ItemStack.TooltipPart var1) {
      CompoundTag var2 = this.getOrCreateTag();
      var2.putInt("HideFlags", var2.getInt("HideFlags") | var1.getMask());
   }

   public static void appendEnchantmentNames(List<Component> var0, ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(var3.getString("id"))).ifPresent((var2x) -> {
            var0.add(var2x.getFullname(var3.getInt("lvl")));
         });
      }

   }

   private static Collection<Component> expandBlockState(String var0) {
      try {
         BlockStateParser var1 = (new BlockStateParser(new StringReader(var0), true)).parse(true);
         BlockState var2 = var1.getState();
         ResourceLocation var3 = var1.getTag();
         boolean var4 = var2 != null;
         boolean var5 = var3 != null;
         if (var4 || var5) {
            if (var4) {
               return Lists.newArrayList(new Component[]{var2.getBlock().getName().withStyle(ChatFormatting.DARK_GRAY)});
            }

            Tag var6 = BlockTags.getAllTags().getTag(var3);
            if (var6 != null) {
               List var7 = var6.getValues();
               if (!var7.isEmpty()) {
                  return (Collection)var7.stream().map(Block::getName).map((var0x) -> {
                     return var0x.withStyle(ChatFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException var8) {
      }

      return Lists.newArrayList(new Component[]{(new TextComponent("missingno")).withStyle(ChatFormatting.DARK_GRAY)});
   }

   public boolean hasFoil() {
      return this.getItem().isFoil(this);
   }

   public Rarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.isEnchanted();
      }
   }

   public void enchant(Enchantment var1, int var2) {
      this.getOrCreateTag();
      if (!this.tag.contains("Enchantments", 9)) {
         this.tag.put("Enchantments", new ListTag());
      }

      ListTag var3 = this.tag.getList("Enchantments", 10);
      CompoundTag var4 = new CompoundTag();
      var4.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(var1)));
      var4.putShort("lvl", (short)((byte)var2));
      var3.add(var4);
   }

   public boolean isEnchanted() {
      if (this.tag != null && this.tag.contains("Enchantments", 9)) {
         return !this.tag.getList("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void addTagElement(String var1, net.minecraft.nbt.Tag var2) {
      this.getOrCreateTag().put(var1, var2);
   }

   public boolean isFramed() {
      return this.entityRepresentation instanceof ItemFrame;
   }

   public void setEntityRepresentation(@Nullable Entity var1) {
      this.entityRepresentation = var1;
   }

   @Nullable
   public ItemFrame getFrame() {
      return this.entityRepresentation instanceof ItemFrame ? (ItemFrame)this.getEntityRepresentation() : null;
   }

   @Nullable
   public Entity getEntityRepresentation() {
      return !this.emptyCacheFlag ? this.entityRepresentation : null;
   }

   public int getBaseRepairCost() {
      return this.hasTag() && this.tag.contains("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
   }

   public void setRepairCost(int var1) {
      this.getOrCreateTag().putInt("RepairCost", var1);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot var1) {
      Object var2;
      if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
         var2 = HashMultimap.create();
         ListTag var3 = this.tag.getList("AttributeModifiers", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            if (!var5.contains("Slot", 8) || var5.getString("Slot").equals(var1.getName())) {
               Optional var6 = Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(var5.getString("AttributeName")));
               if (var6.isPresent()) {
                  AttributeModifier var7 = AttributeModifier.load(var5);
                  if (var7 != null && var7.getId().getLeastSignificantBits() != 0L && var7.getId().getMostSignificantBits() != 0L) {
                     ((Multimap)var2).put(var6.get(), var7);
                  }
               }
            }
         }
      } else {
         var2 = this.getItem().getDefaultAttributeModifiers(var1);
      }

      return (Multimap)var2;
   }

   public void addAttributeModifier(Attribute var1, AttributeModifier var2, @Nullable EquipmentSlot var3) {
      this.getOrCreateTag();
      if (!this.tag.contains("AttributeModifiers", 9)) {
         this.tag.put("AttributeModifiers", new ListTag());
      }

      ListTag var4 = this.tag.getList("AttributeModifiers", 10);
      CompoundTag var5 = var2.save();
      var5.putString("AttributeName", Registry.ATTRIBUTE.getKey(var1).toString());
      if (var3 != null) {
         var5.putString("Slot", var3.getName());
      }

      var4.add(var5);
   }

   public Component getDisplayName() {
      MutableComponent var1 = (new TextComponent("")).append(this.getHoverName());
      if (this.hasCustomHoverName()) {
         var1.withStyle(ChatFormatting.ITALIC);
      }

      MutableComponent var2 = ComponentUtils.wrapInSquareBrackets(var1);
      if (!this.emptyCacheFlag) {
         var2.withStyle(this.getRarity().color).withStyle((var1x) -> {
            return var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(this)));
         });
      }

      return var2;
   }

   private static boolean areSameBlocks(BlockInWorld var0, @Nullable BlockInWorld var1) {
      if (var1 != null && var0.getState() == var1.getState()) {
         if (var0.getEntity() == null && var1.getEntity() == null) {
            return true;
         } else {
            return var0.getEntity() != null && var1.getEntity() != null ? Objects.equals(var0.getEntity().save(new CompoundTag()), var1.getEntity().save(new CompoundTag())) : false;
         }
      } else {
         return false;
      }
   }

   public boolean hasAdventureModeBreakTagForBlock(TagContainer var1, BlockInWorld var2) {
      if (areSameBlocks(var2, this.cachedBreakBlock)) {
         return this.cachedBreakBlockResult;
      } else {
         this.cachedBreakBlock = var2;
         if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListTag var3 = this.tag.getList("CanDestroy", 8);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               String var5 = var3.getString(var4);

               try {
                  Predicate var6 = BlockPredicateArgument.blockPredicate().parse(new StringReader(var5)).create(var1);
                  if (var6.test(var2)) {
                     this.cachedBreakBlockResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.cachedBreakBlockResult = false;
         return false;
      }
   }

   public boolean hasAdventureModePlaceTagForBlock(TagContainer var1, BlockInWorld var2) {
      if (areSameBlocks(var2, this.cachedPlaceBlock)) {
         return this.cachedPlaceBlockResult;
      } else {
         this.cachedPlaceBlock = var2;
         if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListTag var3 = this.tag.getList("CanPlaceOn", 8);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               String var5 = var3.getString(var4);

               try {
                  Predicate var6 = BlockPredicateArgument.blockPredicate().parse(new StringReader(var5)).create(var1);
                  if (var6.test(var2)) {
                     this.cachedPlaceBlockResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.cachedPlaceBlockResult = false;
         return false;
      }
   }

   public int getPopTime() {
      return this.popTime;
   }

   public void setPopTime(int var1) {
      this.popTime = var1;
   }

   public int getCount() {
      return this.emptyCacheFlag ? 0 : this.count;
   }

   public void setCount(int var1) {
      this.count = var1;
      this.updateEmptyCacheFlag();
   }

   public void grow(int var1) {
      this.setCount(this.count + var1);
   }

   public void shrink(int var1) {
      this.grow(-var1);
   }

   public void onUseTick(Level var1, LivingEntity var2, int var3) {
      this.getItem().onUseTick(var1, var2, this, var3);
   }

   public boolean isEdible() {
      return this.getItem().isEdible();
   }

   public SoundEvent getDrinkingSound() {
      return this.getItem().getDrinkingSound();
   }

   public SoundEvent getEatingSound() {
      return this.getItem().getEatingSound();
   }

   @Nullable
   public SoundEvent getEquipSound() {
      return this.getItem().getEquipSound();
   }

   static {
      LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
   }

   public static enum TooltipPart {
      ENCHANTMENTS,
      MODIFIERS,
      UNBREAKABLE,
      CAN_DESTROY,
      CAN_PLACE,
      ADDITIONAL,
      DYE;

      private final int mask = 1 << this.ordinal();

      private TooltipPart() {
      }

      public int getMask() {
         return this.mask;
      }
   }
}
