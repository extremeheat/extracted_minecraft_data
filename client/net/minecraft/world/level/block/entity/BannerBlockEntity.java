package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class BannerBlockEntity extends BlockEntity implements Nameable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_PATTERNS = 6;
   private static final String TAG_PATTERNS = "patterns";
   @Nullable
   private Component name;
   private DyeColor baseColor;
   private BannerPatternLayers patterns;

   public BannerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BANNER, var1, var2);
      this.patterns = BannerPatternLayers.EMPTY;
      this.baseColor = ((AbstractBannerBlock)var2.getBlock()).getColor();
   }

   public BannerBlockEntity(BlockPos var1, BlockState var2, DyeColor var3) {
      this(var1, var2);
      this.baseColor = var3;
   }

   public void fromItem(ItemStack var1, DyeColor var2) {
      this.baseColor = var2;
      this.applyComponentsFromItemStack(var1);
   }

   public Component getName() {
      return (Component)(this.name != null ? this.name : Component.translatable("block.minecraft.banner"));
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.patterns.equals(BannerPatternLayers.EMPTY)) {
         var1.put("patterns", (Tag)BannerPatternLayers.CODEC.encodeStart(var2.createSerializationContext(NbtOps.INSTANCE), this.patterns).getOrThrow());
      }

      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name, var2));
      }

   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      if (var1.contains("CustomName", 8)) {
         this.name = parseCustomNameSafe(var1.getString("CustomName"), var2);
      }

      if (var1.contains("patterns")) {
         BannerPatternLayers.CODEC.parse(var2.createSerializationContext(NbtOps.INSTANCE), var1.get("patterns")).resultOrPartial((var0) -> {
            LOGGER.error("Failed to parse banner patterns: '{}'", var0);
         }).ifPresent((var1x) -> {
            this.patterns = var1x;
         });
      }

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

   protected void applyImplicitComponents(BlockEntity.DataComponentInput var1) {
      super.applyImplicitComponents(var1);
      this.patterns = (BannerPatternLayers)var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      this.name = (Component)var1.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.BANNER_PATTERNS, this.patterns);
      var1.set(DataComponents.CUSTOM_NAME, this.name);
   }

   public void removeComponentsFromTag(CompoundTag var1) {
      var1.remove("patterns");
      var1.remove("CustomName");
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
