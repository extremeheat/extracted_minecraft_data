package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundRecipeBookUpdatePacket implements Packet {
   private ServerboundRecipeBookUpdatePacket.Purpose purpose;
   private ResourceLocation recipe;
   private boolean guiOpen;
   private boolean filteringCraftable;
   private boolean furnaceGuiOpen;
   private boolean furnaceFilteringCraftable;
   private boolean blastFurnaceGuiOpen;
   private boolean blastFurnaceFilteringCraftable;
   private boolean smokerGuiOpen;
   private boolean smokerFilteringCraftable;

   public ServerboundRecipeBookUpdatePacket() {
   }

   public ServerboundRecipeBookUpdatePacket(Recipe var1) {
      this.purpose = ServerboundRecipeBookUpdatePacket.Purpose.SHOWN;
      this.recipe = var1.getId();
   }

   public ServerboundRecipeBookUpdatePacket(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      this.purpose = ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS;
      this.guiOpen = var1;
      this.filteringCraftable = var2;
      this.furnaceGuiOpen = var3;
      this.furnaceFilteringCraftable = var4;
      this.blastFurnaceGuiOpen = var5;
      this.blastFurnaceFilteringCraftable = var6;
      this.smokerGuiOpen = var5;
      this.smokerFilteringCraftable = var6;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.purpose = (ServerboundRecipeBookUpdatePacket.Purpose)var1.readEnum(ServerboundRecipeBookUpdatePacket.Purpose.class);
      if (this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SHOWN) {
         this.recipe = var1.readResourceLocation();
      } else if (this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS) {
         this.guiOpen = var1.readBoolean();
         this.filteringCraftable = var1.readBoolean();
         this.furnaceGuiOpen = var1.readBoolean();
         this.furnaceFilteringCraftable = var1.readBoolean();
         this.blastFurnaceGuiOpen = var1.readBoolean();
         this.blastFurnaceFilteringCraftable = var1.readBoolean();
         this.smokerGuiOpen = var1.readBoolean();
         this.smokerFilteringCraftable = var1.readBoolean();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.purpose);
      if (this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SHOWN) {
         var1.writeResourceLocation(this.recipe);
      } else if (this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS) {
         var1.writeBoolean(this.guiOpen);
         var1.writeBoolean(this.filteringCraftable);
         var1.writeBoolean(this.furnaceGuiOpen);
         var1.writeBoolean(this.furnaceFilteringCraftable);
         var1.writeBoolean(this.blastFurnaceGuiOpen);
         var1.writeBoolean(this.blastFurnaceFilteringCraftable);
         var1.writeBoolean(this.smokerGuiOpen);
         var1.writeBoolean(this.smokerFilteringCraftable);
      }

   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookUpdatePacket(this);
   }

   public ServerboundRecipeBookUpdatePacket.Purpose getPurpose() {
      return this.purpose;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isGuiOpen() {
      return this.guiOpen;
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean isFurnaceGuiOpen() {
      return this.furnaceGuiOpen;
   }

   public boolean isFurnaceFilteringCraftable() {
      return this.furnaceFilteringCraftable;
   }

   public boolean isBlastFurnaceGuiOpen() {
      return this.blastFurnaceGuiOpen;
   }

   public boolean isBlastFurnaceFilteringCraftable() {
      return this.blastFurnaceFilteringCraftable;
   }

   public boolean isSmokerGuiOpen() {
      return this.smokerGuiOpen;
   }

   public boolean isSmokerFilteringCraftable() {
      return this.smokerFilteringCraftable;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;
   }
}
