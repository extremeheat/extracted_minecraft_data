package net.minecraft.client.stream;

public enum IngestServerTester$IngestTestState {
   Uninitalized,
   Starting,
   ConnectingToServer,
   TestingServer,
   DoneTestingServer,
   Finished,
   Cancelled,
   Failed;

   private IngestServerTester$IngestTestState() {
   }
}
