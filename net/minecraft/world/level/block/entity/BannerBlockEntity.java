package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BannerBlockEntity extends BlockEntity implements Nameable {
   private Component name;
   private DyeColor baseColor;
   private ListTag itemPatterns;
   private boolean receivedData;
   private List patterns;
   private List colors;
   private String textureHashName;
   private boolean onlyRenderPattern;

   public BannerBlockEntity() {
      super(BlockEntityType.BANNER);
      this.baseColor = DyeColor.WHITE;
      this.onlyRenderPattern = false;
   }

   public BannerBlockEntity(DyeColor var1) {
      this();
      this.baseColor = var1;
   }

   public void fromItem(ItemStack var1, DyeColor var2) {
      this.itemPatterns = null;
      CompoundTag var3 = var1.getTagElement("BlockEntityTag");
      if (var3 != null && var3.contains("Patterns", 9)) {
         this.itemPatterns = var3.getList("Patterns", 10).copy();
      }

      this.baseColor = var2;
      this.patterns = null;
      this.colors = null;
      this.textureHashName = "";
      this.receivedData = true;
      this.name = var1.hasCustomHoverName() ? var1.getHoverName() : null;
   }

   public Component getName() {
      return (Component)(this.name != null ? this.name : new TranslatableComponent("block.minecraft.banner", new Object[0]));
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   public void setCustomName(Component var1) {
      this.name = var1;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (this.itemPatterns != null) {
         var1.put("Patterns", this.itemPatterns);
      }

      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"));
      }

      if (this.hasLevel()) {
         this.baseColor = ((AbstractBannerBlock)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.itemPatterns = var1.getList("Patterns", 10);
      this.patterns = null;
      this.colors = null;
      this.textureHashName = null;
      this.receivedData = true;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 6, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public static int getPatternCount(ItemStack var0) {
      CompoundTag var1 = var0.getTagElement("BlockEntityTag");
      return var1 != null && var1.contains("Patterns") ? var1.getList("Patterns", 10).size() : 0;
   }

   public List getPatterns() {
      this.createPatternList();
      return this.patterns;
   }

   public List getColors() {
      this.createPatternList();
      return this.colors;
   }

   private void createPatternList() {
      if (this.patterns == null || this.colors == null || this.textureHashName == null) {
         if (!this.receivedData) {
            this.textureHashName = "";
         } else {
            this.patterns = Lists.newArrayList();
            this.colors = Lists.newArrayList();
            DyeColor var1 = this.getBaseColor(this::getBlockState);
            if (var1 == null) {
               this.textureHashName = "banner_missing";
            } else {
               this.patterns.add(BannerPattern.BASE);
               this.colors.add(var1);
               this.textureHashName = "b" + var1.getId();
               if (this.itemPatterns != null) {
                  for(int var2 = 0; var2 < this.itemPatterns.size(); ++var2) {
                     CompoundTag var3 = this.itemPatterns.getCompound(var2);
                     BannerPattern var4 = BannerPattern.byHash(var3.getString("Pattern"));
                     if (var4 != null) {
                        this.patterns.add(var4);
                        int var5 = var3.getInt("Color");
                        this.colors.add(DyeColor.byId(var5));
                        this.textureHashName = this.textureHashName + var4.getHashname() + var5;
                     }
                  }
               }
            }

         }
      }
   }

   public static void removeLastPattern(ItemStack var0) {
      CompoundTag var1 = var0.getTagElement("BlockEntityTag");
      if (var1 != null && var1.contains("Patterns", 9)) {
         ListTag var2 = var1.getList("Patterns", 10);
         if (!var2.isEmpty()) {
            var2.remove(var2.size() - 1);
            if (var2.isEmpty()) {
               var0.removeTagKey("BlockEntityTag");
            }

         }
      }
   }

   public ItemStack getItem(BlockState var1) {
      ItemStack var2 = new ItemStack(BannerBlock.byColor(this.getBaseColor(() -> {
         return var1;
      })));
      if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
         var2.getOrCreateTagElement("BlockEntityTag").put("Patterns", this.itemPatterns.copy());
      }

      if (this.name != null) {
         var2.setHoverName(this.name);
      }

      return var2;
   }

   public DyeColor getBaseColor(Supplier var1) {
      if (this.baseColor == null) {
         this.baseColor = ((AbstractBannerBlock)((BlockState)var1.get()).getBlock()).getColor();
      }

      return this.baseColor;
   }

   public void setOnlyRenderPattern(boolean var1) {
      this.onlyRenderPattern = var1;
   }

   public boolean onlyRenderPattern() {
      return this.onlyRenderPattern;
   }
}
