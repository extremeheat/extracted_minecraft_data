package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BannerBlockEntity extends BlockEntity implements Nameable {
   public static final int MAX_PATTERNS = 6;
   private static final String TAG_PATTERNS = "patterns";
   private static final Component DEFAULT_NAME = Component.translatable("block.minecraft.banner");
   @Nullable
   private Component name;
   private final DyeColor baseColor;
   private BannerPatternLayers patterns;

   public BannerBlockEntity(BlockPos var1, BlockState var2) {
      this(var1, var2, ((AbstractBannerBlock)var2.getBlock()).getColor());
   }

   public BannerBlockEntity(BlockPos var1, BlockState var2, DyeColor var3) {
      super(BlockEntityType.BANNER, var1, var2);
      this.patterns = BannerPatternLayers.EMPTY;
      this.baseColor = var3;
   }

   public Component getName() {
      return this.name != null ? this.name : DEFAULT_NAME;
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      if (!this.patterns.equals(BannerPatternLayers.EMPTY)) {
         var1.store("patterns", BannerPatternLayers.CODEC, this.patterns);
      }

      var1.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.name = parseCustomNameSafe(var1, "CustomName");
      this.patterns = (BannerPatternLayers)var1.read("patterns", BannerPatternLayers.CODEC).orElse(BannerPatternLayers.EMPTY);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveWithoutMetadata(var1);
   }

   public BannerPatternLayers getPatterns() {
      return this.patterns;
   }

   public ItemStack getItem() {
      ItemStack var1 = new ItemStack(BannerBlock.byColor(this.baseColor));
      var1.applyComponents(this.collectComponents());
      return var1;
   }

   public DyeColor getBaseColor() {
      return this.baseColor;
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      this.patterns = (BannerPatternLayers)var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      this.name = (Component)var1.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.BANNER_PATTERNS, this.patterns);
      var1.set(DataComponents.CUSTOM_NAME, this.name);
   }

   public void removeComponentsFromTag(ValueOutput var1) {
      var1.discard("patterns");
      var1.discard("CustomName");
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
