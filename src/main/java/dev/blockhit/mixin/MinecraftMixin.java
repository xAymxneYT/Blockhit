package dev.blockhit.mixin;

import dev.blockhit.BlockhitClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
  @Inject(method = "tick", at = @At("TAIL"))
  private void blockhit$tick(CallbackInfo ci) {
    BlockhitClient client = BlockhitClient.get();
    if (client != null) client.observeBlocking();
  }
}
