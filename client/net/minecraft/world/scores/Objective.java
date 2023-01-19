package net.minecraft.world.scores;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Objective {
   private final Scoreboard scoreboard;
   private final String name;
   private final ObjectiveCriteria criteria;
   private Component displayName;
   private Component formattedDisplayName;
   private ObjectiveCriteria.RenderType renderType;

   public Objective(Scoreboard var1, String var2, ObjectiveCriteria var3, Component var4, ObjectiveCriteria.RenderType var5) {
      super();
      this.scoreboard = var1;
      this.name = var2;
      this.criteria = var3;
      this.displayName = var4;
      this.formattedDisplayName = this.createFormattedDisplayName();
      this.renderType = var5;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public String getName() {
      return this.name;
   }

   public ObjectiveCriteria getCriteria() {
      return this.criteria;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   private Component createFormattedDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(
         this.displayName.copy().withStyle(var1 -> var1.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.name))))
      );
   }

   public Component getFormattedDisplayName() {
      return this.formattedDisplayName;
   }

   public void setDisplayName(Component var1) {
      this.displayName = var1;
      this.formattedDisplayName = this.createFormattedDisplayName();
      this.scoreboard.onObjectiveChanged(this);
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(ObjectiveCriteria.RenderType var1) {
      this.renderType = var1;
      this.scoreboard.onObjectiveChanged(this);
   }
}
