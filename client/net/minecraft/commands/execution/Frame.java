package net.minecraft.commands.execution;

import net.minecraft.commands.CommandResultCallback;

public record Frame(int a, CommandResultCallback b, Frame.FrameControl c) {
   private final int depth;
   private final CommandResultCallback returnValueConsumer;
   private final Frame.FrameControl frameControl;

   public Frame(int var1, CommandResultCallback var2, Frame.FrameControl var3) {
      super();
      this.depth = var1;
      this.returnValueConsumer = var2;
      this.frameControl = var3;
   }

   public void returnSuccess(int var1) {
      this.returnValueConsumer.onSuccess(var1);
   }

   public void returnFailure() {
      this.returnValueConsumer.onFailure();
   }

   public void discard() {
      this.frameControl.discard();
   }

   @FunctionalInterface
   public interface FrameControl {
      void discard();
   }
}
