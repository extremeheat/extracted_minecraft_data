package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundRecipePacket implements Packet {
   private ClientboundRecipePacket.State state;
   private List recipes;
   private List toHighlight;
   private boolean guiOpen;
   private boolean filteringCraftable;
   private boolean furnaceGuiOpen;
   private boolean furnaceFilteringCraftable;

   public ClientboundRecipePacket() {
   }

   public ClientboundRecipePacket(ClientboundRecipePacket.State var1, Collection var2, Collection var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      this.state = var1;
      this.recipes = ImmutableList.copyOf(var2);
      this.toHighlight = ImmutableList.copyOf(var3);
      this.guiOpen = var4;
      this.filteringCraftable = var5;
      this.furnaceGuiOpen = var6;
      this.furnaceFilteringCraftable = var7;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddOrRemoveRecipes(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.state = (ClientboundRecipePacket.State)var1.readEnum(ClientboundRecipePacket.State.class);
      this.guiOpen = var1.readBoolean();
      this.filteringCraftable = var1.readBoolean();
      this.furnaceGuiOpen = var1.readBoolean();
      this.furnaceFilteringCraftable = var1.readBoolean();
      int var2 = var1.readVarInt();
      this.recipes = Lists.newArrayList();

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.recipes.add(var1.readResourceLocation());
      }

      if (this.state == ClientboundRecipePacket.State.INIT) {
         var2 = var1.readVarInt();
         this.toHighlight = Lists.newArrayList();

         for(var3 = 0; var3 < var2; ++var3) {
            this.toHighlight.add(var1.readResourceLocation());
         }
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.state);
      var1.writeBoolean(this.guiOpen);
      var1.writeBoolean(this.filteringCraftable);
      var1.writeBoolean(this.furnaceGuiOpen);
      var1.writeBoolean(this.furnaceFilteringCraftable);
      var1.writeVarInt(this.recipes.size());
      Iterator var2 = this.recipes.iterator();

      ResourceLocation var3;
      while(var2.hasNext()) {
         var3 = (ResourceLocation)var2.next();
         var1.writeResourceLocation(var3);
      }

      if (this.state == ClientboundRecipePacket.State.INIT) {
         var1.writeVarInt(this.toHighlight.size());
         var2 = this.toHighlight.iterator();

         while(var2.hasNext()) {
            var3 = (ResourceLocation)var2.next();
            var1.writeResourceLocation(var3);
         }
      }

   }

   public List getRecipes() {
      return this.recipes;
   }

   public List getHighlights() {
      return this.toHighlight;
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

   public ClientboundRecipePacket.State getState() {
      return this.state;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;
   }
}
