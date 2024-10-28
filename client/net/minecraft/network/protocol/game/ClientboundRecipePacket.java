package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBookSettings;

public class ClientboundRecipePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundRecipePacket> STREAM_CODEC = Packet.codec(ClientboundRecipePacket::write, ClientboundRecipePacket::new);
   private final State state;
   private final List<ResourceLocation> recipes;
   private final List<ResourceLocation> toHighlight;
   private final RecipeBookSettings bookSettings;

   public ClientboundRecipePacket(State var1, Collection<ResourceLocation> var2, Collection<ResourceLocation> var3, RecipeBookSettings var4) {
      super();
      this.state = var1;
      this.recipes = ImmutableList.copyOf(var2);
      this.toHighlight = ImmutableList.copyOf(var3);
      this.bookSettings = var4;
   }

   private ClientboundRecipePacket(FriendlyByteBuf var1) {
      super();
      this.state = (State)var1.readEnum(State.class);
      this.bookSettings = RecipeBookSettings.read(var1);
      this.recipes = var1.readList(FriendlyByteBuf::readResourceLocation);
      if (this.state == ClientboundRecipePacket.State.INIT) {
         this.toHighlight = var1.readList(FriendlyByteBuf::readResourceLocation);
      } else {
         this.toHighlight = ImmutableList.of();
      }

   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.state);
      this.bookSettings.write(var1);
      var1.writeCollection(this.recipes, FriendlyByteBuf::writeResourceLocation);
      if (this.state == ClientboundRecipePacket.State.INIT) {
         var1.writeCollection(this.toHighlight, FriendlyByteBuf::writeResourceLocation);
      }

   }

   public PacketType<ClientboundRecipePacket> type() {
      return GamePacketTypes.CLIENTBOUND_RECIPE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddOrRemoveRecipes(this);
   }

   public List<ResourceLocation> getRecipes() {
      return this.recipes;
   }

   public List<ResourceLocation> getHighlights() {
      return this.toHighlight;
   }

   public RecipeBookSettings getBookSettings() {
      return this.bookSettings;
   }

   public State getState() {
      return this.state;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{INIT, ADD, REMOVE};
      }
   }
}
