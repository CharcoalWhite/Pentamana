package org.charcoalwhite.pentamana.api;

import net.minecraft.entity.Entity;

public interface ServerPlayerEntityApi {
    default float getMagicDamageAgainst(Entity entity, float amount) {
        return 0;
    }
}