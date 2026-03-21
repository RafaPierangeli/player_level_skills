package com.player_level_skills.mixin.network;

import com.player_level_skills.access.OrbAccess;
import com.player_level_skills.network.packet.OrbPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerMixin extends OrbAccess {

    @Override
    void onLevelExperienceOrbSpawn(OrbPacket packet);
}

