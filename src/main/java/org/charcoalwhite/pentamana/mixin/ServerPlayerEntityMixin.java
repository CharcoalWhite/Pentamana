package org.charcoalwhite.pentamana.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.charcoalwhite.pentamana.ManaCommand;
import org.charcoalwhite.pentamana.Pentamana;
import org.charcoalwhite.pentamana.api.ServerPlayerEntityApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityApi {
	@Inject(method = "tick()V", at = @At("RETURN"))
	private void tickMana(CallbackInfo info) {
		try {
			ManaCommand.executeTick(this.getCommandSource());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public float getMagicDamageAgainst(Entity entity, float amount) {
		int manaCapacity = Pentamana.MANA_SCALE;
		try {
			manaCapacity = ManaCommand.executeGetManaCapacity(this.getCommandSource());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		float potencyDamage = 0;
		int level = ((ServerPlayerEntity)(Object)this).getWeaponStack().getEnchantments().getLevel("pentamana:potency");
		if (level != 0) {
			potencyDamage = (float) (0.5 + level * 0.5);
		}

		return amount * manaCapacity / Pentamana.MANA_SCALE + potencyDamage;
	}

	@Shadow
	public abstract ServerCommandSource getCommandSource();
}