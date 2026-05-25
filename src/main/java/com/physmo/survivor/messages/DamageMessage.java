package com.physmo.survivor.messages;

import com.physmo.survivor.components.weapons.AfflictionPacket;
import java.util.List;

public record DamageMessage(double damage, List<AfflictionPacket> afflictionPackets) {
}
