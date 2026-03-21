package com.player_level_skills.network.packet;

import com.player_level_skills.Player_level_skills;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record LevelPacket(int overallLevel, int skillPoints, int totalLevelExperience, float levelProgress) implements CustomPayload {

    public static final CustomPayload.Id<LevelPacket> PACKET_ID = new CustomPayload.Id<>(Player_level_skills.identifierOf("level_packet"));

    public static final PacketCodec<RegistryByteBuf, LevelPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.overallLevel);
        buf.writeInt(value.skillPoints);
        buf.writeInt(value.totalLevelExperience);
        buf.writeFloat(value.levelProgress);
    }, buf -> new LevelPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}


