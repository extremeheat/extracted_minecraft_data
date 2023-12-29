package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket implements Packet<ClientGamePacketListener> {
   public static final int METHOD_ADD = 0;
   public static final int METHOD_REMOVE = 1;
   public static final int METHOD_CHANGE = 2;
   private final String objectiveName;
   private final Component displayName;
   private final ObjectiveCriteria.RenderType renderType;
   @Nullable
   private final NumberFormat numberFormat;
   private final int method;

   public ClientboundSetObjectivePacket(Objective var1, int var2) {
      super();
      this.objectiveName = var1.getName();
      this.displayName = var1.getDisplayName();
      this.renderType = var1.getRenderType();
      this.numberFormat = var1.numberFormat();
      this.method = var2;
   }

   public ClientboundSetObjectivePacket(FriendlyByteBuf var1) {
      super();
      this.objectiveName = var1.readUtf();
      this.method = var1.readByte();
      if (this.method != 0 && this.method != 2) {
         this.displayName = CommonComponents.EMPTY;
         this.renderType = ObjectiveCriteria.RenderType.INTEGER;
         this.numberFormat = null;
      } else {
         this.displayName = var1.readComponentTrusted();
         this.renderType = var1.readEnum(ObjectiveCriteria.RenderType.class);
         this.numberFormat = var1.readNullable(NumberFormatTypes::readFromStream);
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.objectiveName);
      var1.writeByte(this.method);
      if (this.method == 0 || this.method == 2) {
         var1.writeComponent(this.displayName);
         var1.writeEnum(this.renderType);
         var1.writeNullable(this.numberFormat, NumberFormatTypes::writeToStream);
      }
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddObjective(this);
   }

   public String getObjectiveName() {
      return this.objectiveName;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public int getMethod() {
      return this.method;
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }

   @Nullable
   public NumberFormat getNumberFormat() {
      return this.numberFormat;
   }
}
